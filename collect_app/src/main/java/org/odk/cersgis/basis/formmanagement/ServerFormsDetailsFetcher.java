/*
 * Copyright 2018 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.cersgis.basis.formmanagement;

import org.odk.cersgis.basis.forms.FormListItem;
import org.odk.cersgis.basis.forms.FormSource;
import org.odk.cersgis.basis.forms.FormSourceException;
import org.odk.cersgis.basis.forms.FormsRepository;
import org.odk.cersgis.basis.forms.ManifestFile;
import org.odk.cersgis.basis.forms.MediaFile;
import org.odk.cersgis.basis.forms.MediaFileRepository;
import org.odk.cersgis.basis.utilities.FileUtils;
import org.odk.cersgis.basis.utilities.WebCredentialsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ServerFormsDetailsFetcher {

    private final FormsRepository formsRepository;
    private final MediaFileRepository mediaFileRepository;
    private final FormSource formSource;
    private final DiskFormsSynchronizer diskFormsSynchronizer;

    public ServerFormsDetailsFetcher(FormsRepository formsRepository,
                                     MediaFileRepository mediaFileRepository,
                                     FormSource formSource,
                                     DiskFormsSynchronizer diskFormsSynchronizer) {
        this.formsRepository = formsRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.formSource = formSource;
        this.diskFormsSynchronizer = diskFormsSynchronizer;
    }

    public void updateUrl(String url) {
        formSource.updateUrl(url);
    }

    public void updateCredentials(WebCredentialsUtils webCredentialsUtils) {
        formSource.updateWebCredentialsUtils(webCredentialsUtils);
    }

    public List<ServerFormDetails> fetchFormDetails() throws FormSourceException {
        diskFormsSynchronizer.synchronize();

        List<FormListItem> formListItems = formSource.fetchFormList();
        List<ServerFormDetails> serverFormDetailsList = new ArrayList<>();

        for (FormListItem listItem : formListItems) {
            ManifestFile manifestFile = null;

            if (listItem.getManifestURL() != null) {
                manifestFile = getManifestFile(formSource, listItem.getManifestURL());
            }

            boolean thisFormAlreadyDownloaded = !formsRepository.getAllNotDeletedByFormId(listItem.getFormID()).isEmpty();

            boolean isNewerFormVersionAvailable = false;
            if (thisFormAlreadyDownloaded) {
                if (isNewerFormVersionAvailable(listItem)) {
                    isNewerFormVersionAvailable = true;
                } else if (manifestFile != null) {
                    List<MediaFile> newMediaFiles = manifestFile.getMediaFiles();

                    if (newMediaFiles != null && !newMediaFiles.isEmpty()) {
                        isNewerFormVersionAvailable = areNewerMediaFilesAvailable(listItem.getFormID(), listItem.getVersion(), newMediaFiles);
                    }
                }
            }

            ServerFormDetails serverFormDetails = new ServerFormDetails(
                    listItem.getName(),
                    listItem.getDownloadURL(),
                    listItem.getManifestURL(),
                    listItem.getFormID(),
                    listItem.getVersion(),
                    listItem.getHashWithPrefix(),
                    !thisFormAlreadyDownloaded,
                    isNewerFormVersionAvailable,
                    manifestFile);

            serverFormDetailsList.add(serverFormDetails);
        }

        return serverFormDetailsList;
    }

    private ManifestFile getManifestFile(FormSource formSource, String manifestUrl) {
        if (manifestUrl == null) {
            return null;
        }

        try {
            return formSource.fetchManifest(manifestUrl);
        } catch (FormSourceException formSourceException) {
            Timber.w(formSourceException);
            return null;
        }
    }

    private boolean isNewerFormVersionAvailable(FormListItem formListItem) {
        if (formListItem.getHashWithPrefix() == null) {
            return false;
        }

        String hash = getMd5HashWithoutPrefix(formListItem.getHashWithPrefix());
        return formsRepository.getOneByMd5Hash(hash) == null;
    }

    private boolean areNewerMediaFilesAvailable(String formId, String formVersion, List<MediaFile> newMediaFiles) {
        List<File> localMediaFiles = mediaFileRepository.getAll(formId, formVersion);

        if (localMediaFiles != null) {
            for (MediaFile newMediaFile : newMediaFiles) {
                if (!isMediaFileAlreadyDownloaded(localMediaFiles, newMediaFile)) {
                    return true;
                }
            }
        } else if (!newMediaFiles.isEmpty()) {
            return true;
        }

        return false;
    }

    private static boolean isMediaFileAlreadyDownloaded(List<File> localMediaFiles, MediaFile newMediaFile) {
        // TODO Zip files are ignored we should find a way to take them into account too
        if (newMediaFile.getFilename().endsWith(".zip")) {
            return true;
        }

        String mediaFileHash = newMediaFile.getHash();
        mediaFileHash = mediaFileHash.substring(4, mediaFileHash.length());
        for (File localMediaFile : localMediaFiles) {
            if (mediaFileHash.equals(FileUtils.getMd5Hash(localMediaFile))) {
                return true;
            }
        }
        return false;
    }

    private String getMd5HashWithoutPrefix(String hash) {
        return hash == null || hash.isEmpty() ? null : hash.substring("md5:".length());
    }
}

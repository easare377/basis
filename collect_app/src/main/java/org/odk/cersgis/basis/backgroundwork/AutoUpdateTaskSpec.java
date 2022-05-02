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

package org.odk.cersgis.basis.backgroundwork;

import android.content.Context;

import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.formmanagement.FormDownloadException;
import org.odk.cersgis.basis.formmanagement.FormDownloader;
import org.odk.cersgis.basis.formmanagement.ServerFormDetails;
import org.odk.cersgis.basis.formmanagement.ServerFormsDetailsFetcher;
import org.odk.cersgis.basis.injection.DaggerUtils;
import org.odk.cersgis.basis.notifications.Notifier;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.storage.migration.StorageMigrationRepository;
import org.odk.cersgis.basis.utilities.TranslationHandler;
import org.odk.cersgis.async.TaskSpec;
import org.odk.cersgis.async.WorkerAdapter;
import org.odk.cersgis.basis.forms.FormSourceException;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import static org.odk.cersgis.basis.preferences.GeneralKeys.KEY_AUTOMATIC_UPDATE;

public class AutoUpdateTaskSpec implements TaskSpec {

    @Inject
    ServerFormsDetailsFetcher serverFormsDetailsFetcher;

    @Inject
    StorageMigrationRepository storageMigrationRepository;

    @Inject
    FormDownloader formDownloader;

    @Inject
    Notifier notifier;

    @Inject
    PreferencesProvider preferencesProvider;

    @Inject
    @Named("FORMS")
    ChangeLock changeLock;

    @NotNull
    @Override
    public Supplier<Boolean> getTask(@NotNull Context context) {
        DaggerUtils.getComponent(context).inject(this);

        return () -> {
            try {
                List<ServerFormDetails> serverForms = serverFormsDetailsFetcher.fetchFormDetails();
                List<ServerFormDetails> updatedForms = serverForms.stream().filter(ServerFormDetails::isUpdated).collect(Collectors.toList());

                if (!updatedForms.isEmpty()) {
                    if (preferencesProvider.getGeneralSharedPreferences().getBoolean(KEY_AUTOMATIC_UPDATE, false)) {
                        changeLock.withLock(acquiredLock -> {
                            if (acquiredLock) {
                                HashMap<ServerFormDetails, String> results = new HashMap<>();
                                for (ServerFormDetails serverFormDetails : updatedForms) {
                                    try {
                                        formDownloader.downloadForm(serverFormDetails, null, null);
                                        results.put(serverFormDetails, TranslationHandler.getString(context, R.string.success));
                                    } catch (FormDownloadException e) {
                                        results.put(serverFormDetails, TranslationHandler.getString(context, R.string.failure));
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                }

                                notifier.onUpdatesDownloaded(results);
                            }

                            return null;
                        });
                    } else {
                        notifier.onUpdatesAvailable(updatedForms);
                    }
                }

                return true;
            } catch (FormSourceException e) {
                return true;
            }
        };
    }

    @NotNull
    @Override
    public Class<? extends WorkerAdapter> getWorkManagerAdapter() {
        return Adapter.class;
    }

    public static class Adapter extends WorkerAdapter {

        public Adapter(@NotNull Context context, @NotNull WorkerParameters workerParams) {
            super(new AutoUpdateTaskSpec(), context, workerParams);
        }
    }

}
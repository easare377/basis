/*
 * Copyright (C) 2018 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.cersgis.basis.gdrive;

import android.database.Cursor;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.application.Collect;
import org.odk.cersgis.basis.dao.FormsDao;
import org.odk.cersgis.basis.forms.Form;
import org.odk.cersgis.basis.instances.Instance;
import org.odk.cersgis.basis.preferences.GeneralKeys;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.tasks.InstanceUploaderTask;
import org.odk.cersgis.basis.upload.UploadException;
import org.odk.cersgis.basis.utilities.InstanceUploaderUtils;
import org.odk.cersgis.basis.utilities.TranslationHandler;

import java.util.List;

import timber.log.Timber;

import static org.odk.cersgis.basis.analytics.AnalyticsEvents.SUBMISSION;
import static org.odk.cersgis.basis.utilities.InstanceUploaderUtils.DEFAULT_SUCCESSFUL_TEXT;
import static org.odk.cersgis.basis.utilities.InstanceUploaderUtils.SPREADSHEET_UPLOADED_TO_GOOGLE_DRIVE;

public class InstanceGoogleSheetsUploaderTask extends InstanceUploaderTask {

    private final GoogleApiProvider googleApiProvider;
    private final Analytics analytics;
    private final PreferencesProvider preferencesProvider;

    public InstanceGoogleSheetsUploaderTask(GoogleApiProvider googleApiProvider, Analytics analytics, PreferencesProvider preferencesProvider) {
        this.googleApiProvider = googleApiProvider;
        this.analytics = analytics;
        this.preferencesProvider = preferencesProvider;
    }

    @Override
    protected Outcome doInBackground(Long... instanceIdsToUpload) {
        String account = preferencesProvider
                .getGeneralSharedPreferences()
                .getString(GeneralKeys.KEY_SELECTED_GOOGLE_ACCOUNT, "");

        InstanceGoogleSheetsUploader uploader = new InstanceGoogleSheetsUploader(googleApiProvider.getDriveApi(account), googleApiProvider.getSheetsApi(account));
        final Outcome outcome = new Outcome();

        List<Instance> instancesToUpload = uploader.getInstancesFromIds(instanceIdsToUpload);

        for (int i = 0; i < instancesToUpload.size(); i++) {
            Instance instance = instancesToUpload.get(i);

            if (isCancelled()) {
                outcome.messagesByInstanceId.put(instance.getId().toString(),
                        TranslationHandler.getString(Collect.getInstance(), R.string.instance_upload_cancelled));
                return outcome;
            }

            publishProgress(i + 1, instancesToUpload.size());

            // Get corresponding blank form and verify there is exactly 1
            FormsDao dao = new FormsDao();
            Cursor formCursor = dao.getFormsCursorSortedByDateDesc(instance.getJrFormId(), instance.getJrVersion());
            List<Form> forms = dao.getFormsFromCursor(formCursor);

            if (forms.size() != 1) {
                outcome.messagesByInstanceId.put(instance.getId().toString(),
                        TranslationHandler.getString(Collect.getInstance(), R.string.not_exactly_one_blank_form_for_this_form_id));
            } else {
                try {
                    String destinationUrl = uploader.getUrlToSubmitTo(instance, null, null);
                    if (InstanceUploaderUtils.doesUrlRefersToGoogleSheetsFile(destinationUrl)) {
                        uploader.uploadOneSubmission(instance, destinationUrl);
                        outcome.messagesByInstanceId.put(instance.getId().toString(), DEFAULT_SUCCESSFUL_TEXT);

                        analytics.logEvent(SUBMISSION, "HTTP-Sheets", Collect.getFormIdentifierHash(instance.getJrFormId(), instance.getJrVersion()));
                    } else {
                        outcome.messagesByInstanceId.put(instance.getId().toString(), SPREADSHEET_UPLOADED_TO_GOOGLE_DRIVE);
                    }
                } catch (UploadException e) {
                    Timber.d(e);
                    outcome.messagesByInstanceId.put(instance.getId().toString(),
                            e.getDisplayMessage());
                }
            }
        }
        return outcome;
    }
}
package org.odk.cersgis.basis.formentry.saving;

import android.net.Uri;

import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.javarosawrapper.FormController;
import org.odk.cersgis.basis.tasks.SaveToDiskResult;
import org.odk.cersgis.basis.utilities.MediaUtils;

import java.util.ArrayList;

public interface FormSaver {
    SaveToDiskResult save(Uri instanceContentURI, FormController formController, MediaUtils mediaUtils, boolean shouldFinalize, boolean exitAfter,
                          String updatedSaveName, ProgressListener progressListener, Analytics analytics, ArrayList<String> tempFiles);

    interface ProgressListener {
        void onProgressUpdate(String message);
    }
}

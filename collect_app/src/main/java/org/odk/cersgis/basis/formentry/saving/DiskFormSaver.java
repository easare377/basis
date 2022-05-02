package org.odk.cersgis.basis.formentry.saving;

import android.net.Uri;

import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.javarosawrapper.FormController;
import org.odk.cersgis.basis.tasks.SaveFormToDisk;
import org.odk.cersgis.basis.tasks.SaveToDiskResult;
import org.odk.cersgis.basis.utilities.MediaUtils;

import java.util.ArrayList;

public class DiskFormSaver implements FormSaver {

    @Override
    public SaveToDiskResult save(Uri instanceContentURI, FormController formController, MediaUtils mediaUtils, boolean shouldFinalize, boolean exitAfter,
                                 String updatedSaveName, ProgressListener progressListener, Analytics analytics, ArrayList<String> tempFiles) {
        SaveFormToDisk saveFormToDisk = new SaveFormToDisk(formController, mediaUtils, exitAfter, shouldFinalize,
                updatedSaveName, instanceContentURI, analytics, tempFiles);
        return saveFormToDisk.saveForm(progressListener);
    }
}

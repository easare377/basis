package org.odk.cersgis.basis.formentry;

import androidx.annotation.NonNull;

import org.odk.cersgis.basis.javarosawrapper.FormController;

public interface RequiresFormController {
    void formLoaded(@NonNull FormController formController);
}

package org.odk.cersgis.basis.formmanagement;

import android.content.Context;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.forms.FormSourceException;
import org.odk.cersgis.basis.utilities.TranslationHandler;

public class FormSourceExceptionMapper {

    private final Context context;

    public FormSourceExceptionMapper(Context context) {
        this.context = context;
    }

    public String getMessage(FormSourceException exception) {
        switch (exception.getType()) {
            case UNREACHABLE:
                return TranslationHandler.getString(context, R.string.unreachable_error, exception.getServerUrl()) + " " + TranslationHandler.getString(context, R.string.report_to_project_lead);
            case SECURITY_ERROR:
                return TranslationHandler.getString(context, R.string.security_error, exception.getServerUrl()) + " " + TranslationHandler.getString(context, R.string.report_to_project_lead);
            default:
                return TranslationHandler.getString(context, R.string.report_to_project_lead);
        }
    }
}

package org.odk.cersgis.basis.configure;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.odk.cersgis.basis.preferences.FormUpdateMode;
import org.odk.cersgis.basis.preferences.GeneralKeys;
import org.odk.cersgis.basis.preferences.Protocol;

public class SettingsUtils {

    private SettingsUtils() {

    }

    @NonNull
    public static FormUpdateMode getFormUpdateMode(Context context, SharedPreferences generalSharedPreferences) {
        String protocol = generalSharedPreferences.getString(GeneralKeys.KEY_PROTOCOL, null);

        if (Protocol.parse(context, protocol) == Protocol.GOOGLE) {
            return FormUpdateMode.MANUAL;
        } else {
            String mode = generalSharedPreferences.getString(GeneralKeys.KEY_FORM_UPDATE_MODE, null);
            return FormUpdateMode.parse(context, mode);
        }
    }
}

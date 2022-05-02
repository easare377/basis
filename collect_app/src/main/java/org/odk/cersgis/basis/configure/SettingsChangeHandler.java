package org.odk.cersgis.basis.configure;

public interface SettingsChangeHandler {
    void onSettingChanged(String changedKey, Object newValue);
}

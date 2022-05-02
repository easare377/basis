package org.odk.cersgis.basis.utilities;

import org.junit.Test;
import org.odk.cersgis.basis.logic.PropertyManager;
import org.odk.cersgis.basis.preferences.GeneralKeys;
import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WebCredentialsUtilsTest {

    @Test
    public void saveCredentialsPreferencesMethod_shouldSaveNewCredentialsAndReloadPropertyManager() {
        WebCredentialsUtils webCredentialsUtils = new WebCredentialsUtils();
        GeneralSharedPreferences generalSharedPreferences = mock(GeneralSharedPreferences.class);
        PropertyManager propertyManager = mock(PropertyManager.class);

        webCredentialsUtils.saveCredentialsPreferences(generalSharedPreferences, "username", "password", propertyManager);

        verify(generalSharedPreferences, times(1)).save(GeneralKeys.KEY_USERNAME, "username");
        verify(generalSharedPreferences, times(1)).save(GeneralKeys.KEY_PASSWORD, "password");
        verify(propertyManager, times(1)).reload();
    }
}

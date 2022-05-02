package org.odk.cersgis.basis.application;

import org.junit.Test;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.backgroundwork.FormUpdateManager;
import org.odk.cersgis.basis.configure.ServerRepository;
import org.odk.cersgis.basis.logic.PropertyManager;
import org.odk.cersgis.basis.preferences.GeneralKeys;
import org.odk.cersgis.basis.preferences.PreferencesProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class CollectSettingsChangeHandlerTest {

    private final PropertyManager propertyManager = mock(PropertyManager.class);
    private final FormUpdateManager formUpdateManager = mock(FormUpdateManager.class);
    private final ServerRepository serverRepository = mock(ServerRepository.class);

    CollectSettingsChangeHandler handler = new CollectSettingsChangeHandler(propertyManager, formUpdateManager, serverRepository, mock(Analytics.class), mock(PreferencesProvider.class));

    @Test
    public void updatesPropertyManager() {
        handler.onSettingChanged("blah", "anything");
        verify(propertyManager).reload();
    }

    @Test
    public void doesNotDoAnythingElse() {
        handler.onSettingChanged("blah", "anything");
        verifyNoInteractions(formUpdateManager);
        verifyNoInteractions(serverRepository);
    }

    @Test
    public void whenChangedKeyIsFormUpdateMode_schedulesUpdates() {
        handler.onSettingChanged(GeneralKeys.KEY_FORM_UPDATE_MODE, "anything");
        verify(formUpdateManager).scheduleUpdates();
    }

    @Test
    public void whenChangedKeyIsPeriodicUpdatesCheck_schedulesUpdates() {
        handler.onSettingChanged(GeneralKeys.KEY_PERIODIC_FORM_UPDATES_CHECK, "anything");
        verify(formUpdateManager).scheduleUpdates();
    }

    @Test
    public void whenChangedKeyIsProtocol_schedulesUpdates() {
        handler.onSettingChanged(GeneralKeys.KEY_PROTOCOL, "anything");
        verify(formUpdateManager).scheduleUpdates();
    }

    @Test
    public void whenChangedKeyIsServerURL_savesURLToServerRepository() {
        handler.onSettingChanged(GeneralKeys.KEY_SERVER_URL, "http://newUrl");
        verify(serverRepository).save("http://newUrl");
    }
}

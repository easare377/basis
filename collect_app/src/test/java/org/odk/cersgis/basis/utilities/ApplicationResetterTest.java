package org.odk.cersgis.basis.utilities;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.configure.ServerRepository;
import org.odk.cersgis.basis.injection.config.AppDependencyModule;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.support.RobolectricHelpers;
import org.robolectric.RobolectricTestRunner;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ApplicationResetterTest {

    private final ServerRepository serverRepository = mock(ServerRepository.class);

    @Before
    public void setup() {
        RobolectricHelpers.overrideAppDependencyModule(new AppDependencyModule() {
            @Override
            public ServerRepository providesServerRepository(Context context, PreferencesProvider preferencesProvider) {
                return serverRepository;
            }
        });
    }

    @Test
    public void resettingPreferences_alsoResetsServers() {
        ApplicationResetter applicationResetter = new ApplicationResetter();
        applicationResetter.reset(asList(ApplicationResetter.ResetAction.RESET_PREFERENCES));
        verify(serverRepository).clear();
    }
}
package org.odk.cersgis.basis.backgroundwork;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.analytics.AnalyticsEvents;
import org.odk.cersgis.basis.formmanagement.FormDownloader;
import org.odk.cersgis.basis.formmanagement.ServerFormsDetailsFetcher;
import org.odk.cersgis.basis.formmanagement.matchexactly.ServerFormsSynchronizer;
import org.odk.cersgis.basis.formmanagement.matchexactly.SyncStatusRepository;
import org.odk.cersgis.basis.forms.FormsRepository;
import org.odk.cersgis.basis.injection.config.AppDependencyModule;
import org.odk.cersgis.basis.instances.InstancesRepository;
import org.odk.cersgis.basis.notifications.Notifier;
import org.odk.cersgis.basis.forms.FormSourceException;
import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.support.BooleanChangeLock;
import org.odk.cersgis.basis.support.RobolectricHelpers;
import org.robolectric.RobolectricTestRunner;

import java.util.function.Supplier;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(RobolectricTestRunner.class)
public class SyncFormsTaskSpecTest {

    private final ServerFormsSynchronizer serverFormsSynchronizer = mock(ServerFormsSynchronizer.class);
    private final SyncStatusRepository syncStatusRepository = mock(SyncStatusRepository.class);
    private final Notifier notifier = mock(Notifier.class);
    private final Analytics analytics = mock(Analytics.class);
    private final BooleanChangeLock changeLock = new BooleanChangeLock();

    @Before
    public void setup() {
        RobolectricHelpers.overrideAppDependencyModule(new AppDependencyModule() {

            @Override
            public ChangeLock providesFormsChangeLock() {
                return changeLock;
            }

            @Override
            public ServerFormsSynchronizer providesServerFormSynchronizer(ServerFormsDetailsFetcher serverFormsDetailsFetcher, FormsRepository formsRepository, FormDownloader formDownloader, InstancesRepository instancesRepository) {
                return serverFormsSynchronizer;
            }

            @Override
            public SyncStatusRepository providesServerFormSyncRepository() {
                return syncStatusRepository;
            }

            @Override
            public Notifier providesNotifier(Application application, PreferencesProvider preferencesProvider) {
                return notifier;
            }

            @Override
            public Analytics providesAnalytics(Application application, GeneralSharedPreferences generalSharedPreferences) {
                return analytics;
            }
        });
    }

    @Test
    public void setsRepositoryToSyncing_runsSync_thenSetsRepositoryToNotSyncingAndNotifies() throws Exception {
        InOrder inOrder = inOrder(syncStatusRepository, serverFormsSynchronizer);

        SyncFormsTaskSpec taskSpec = new SyncFormsTaskSpec();
        Supplier<Boolean> task = taskSpec.getTask(ApplicationProvider.getApplicationContext());
        task.get();

        inOrder.verify(syncStatusRepository).startSync();
        inOrder.verify(serverFormsSynchronizer).synchronize();
        inOrder.verify(syncStatusRepository).finishSync(null);

        verify(notifier).onSync(null);
    }

    @Test
    public void logsAnalytics() {
        SyncFormsTaskSpec taskSpec = new SyncFormsTaskSpec();
        Supplier<Boolean> task = taskSpec.getTask(ApplicationProvider.getApplicationContext());
        task.get();

        verify(analytics).logEvent(AnalyticsEvents.MATCH_EXACTLY_SYNC_COMPLETED, "Success");
    }

    @Test
    public void whenSynchronizingFails_setsRepositoryToNotSyncingAndNotifiesWithError() throws Exception {
        FormSourceException exception = new FormSourceException(FormSourceException.Type.FETCH_ERROR);
        doThrow(exception).when(serverFormsSynchronizer).synchronize();
        InOrder inOrder = inOrder(syncStatusRepository, serverFormsSynchronizer);

        SyncFormsTaskSpec taskSpec = new SyncFormsTaskSpec();
        Supplier<Boolean> task = taskSpec.getTask(ApplicationProvider.getApplicationContext());
        task.get();

        inOrder.verify(syncStatusRepository).startSync();
        inOrder.verify(serverFormsSynchronizer).synchronize();
        inOrder.verify(syncStatusRepository).finishSync(exception);

        verify(notifier).onSync(exception);
    }

    @Test
    public void whenSynchronizingFails_logsAnalytics() throws Exception {
        FormSourceException exception = new FormSourceException(FormSourceException.Type.FETCH_ERROR);
        doThrow(exception).when(serverFormsSynchronizer).synchronize();

        SyncFormsTaskSpec taskSpec = new SyncFormsTaskSpec();
        Supplier<Boolean> task = taskSpec.getTask(ApplicationProvider.getApplicationContext());
        task.get();

        verify(analytics).logEvent(AnalyticsEvents.MATCH_EXACTLY_SYNC_COMPLETED, "FETCH_ERROR");
    }

    @Test
    public void whenChangeLockLocked_doesNothing() {
        changeLock.lock();

        SyncFormsTaskSpec taskSpec = new SyncFormsTaskSpec();
        Supplier<Boolean> task = taskSpec.getTask(ApplicationProvider.getApplicationContext());
        task.get();

        verifyNoInteractions(serverFormsSynchronizer);
        verifyNoInteractions(syncStatusRepository);
        verifyNoInteractions(notifier);
    }
}
package org.odk.cersgis.basis.storage.migration;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.javarosa.core.reference.ReferenceManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.backgroundwork.ChangeLock;
import org.odk.cersgis.basis.backgroundwork.FormSubmitManager;
import org.odk.cersgis.basis.backgroundwork.FormUpdateManager;
import org.odk.cersgis.basis.injection.config.AppDependencyModule;
import org.odk.cersgis.basis.storage.StoragePathProvider;
import org.odk.cersgis.basis.storage.StorageStateProvider;
import org.odk.cersgis.basis.support.BooleanChangeLock;
import org.odk.cersgis.basis.support.RobolectricHelpers;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(AndroidJUnit4.class)
public class StorageMigrationServiceTest {

    private final StorageMigrator storageMigrator = mock(StorageMigrator.class);
    private final StorageMigrationRepository storageMigrationRepository = mock(StorageMigrationRepository.class);
    private final BooleanChangeLock formsChangeLock = new BooleanChangeLock();
    private final BooleanChangeLock instancesChangeLock = new BooleanChangeLock();

    @Before
    public void setup() {
        RobolectricHelpers.overrideAppDependencyModule(new AppDependencyModule() {
            @Override
            public ChangeLock providesFormsChangeLock() {
                return formsChangeLock;
            }

            @Override
            public ChangeLock providesInstancesChangeLock() {
                return instancesChangeLock;
            }

            @Override
            public StorageMigrationRepository providesStorageMigrationRepository() {
                return storageMigrationRepository;
            }

            @Override
            public StorageMigrator providesStorageMigrator(StoragePathProvider storagePathProvider, StorageStateProvider storageStateProvider, StorageMigrationRepository storageMigrationRepository, ReferenceManager referenceManager, FormUpdateManager formUpdateManager, FormSubmitManager formSubmitManager, Analytics analytics, ChangeLock changeLock) {
                return storageMigrator;
            }
        });
    }

    @Test
    public void whenFormsChangeLockLocked_doesNotMigrate_andSetsResultInRepository() {
        formsChangeLock.lock();

        StorageMigrationService service = Robolectric.setupIntentService(StorageMigrationService.class);
        service.onHandleIntent(null);

        verifyNoInteractions(storageMigrator);
        verify(storageMigrationRepository).setResult(StorageMigrationResult.CHANGES_IN_PROGRESS);
        verify(storageMigrationRepository).markMigrationEnd();
    }

    @Test
    public void whenInstancesChangeLockLocked_doesNotMigrate_andSetsResultInRepository() {
        instancesChangeLock.lock();

        StorageMigrationService service = Robolectric.setupIntentService(StorageMigrationService.class);
        service.onHandleIntent(null);

        verifyNoInteractions(storageMigrator);
        verify(storageMigrationRepository).setResult(StorageMigrationResult.CHANGES_IN_PROGRESS);
        verify(storageMigrationRepository).markMigrationEnd();
    }
}
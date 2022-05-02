package org.odk.cersgis.basis.support;

import android.content.Context;
import android.webkit.MimeTypeMap;

import androidx.test.espresso.IdlingResource;
import androidx.work.WorkManager;

import org.odk.cersgis.basis.gdrive.GoogleAccountPicker;
import org.odk.cersgis.basis.gdrive.GoogleApiProvider;
import org.odk.cersgis.basis.gdrive.sheets.DriveApi;
import org.odk.cersgis.basis.gdrive.sheets.SheetsApi;
import org.odk.cersgis.basis.injection.config.AppDependencyModule;
import org.odk.cersgis.basis.openrosa.OpenRosaHttpInterface;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.storage.StoragePathProvider;
import org.odk.cersgis.basis.storage.migration.StorageMigrationService;
import org.odk.cersgis.async.Scheduler;
import org.odk.cersgis.utilities.UserAgentProvider;

import java.util.List;

import static java.util.Arrays.asList;

public class TestDependencies extends AppDependencyModule {

    private final CallbackCountingTaskExecutorRule countingTaskExecutorRule = new CallbackCountingTaskExecutorRule();

    public final StubOpenRosaServer server = new StubOpenRosaServer();
    public final TestScheduler scheduler = new TestScheduler();
    public final FakeGoogleApi googleApi = new FakeGoogleApi();
    public final FakeGoogleAccountPicker googleAccountPicker = new FakeGoogleAccountPicker();
    public final StoragePathProvider storagePathProvider = new StoragePathProvider();

    public final List<IdlingResource> idlingResources = asList(
            new SchedulerIdlingResource(scheduler),
            new CountingTaskExecutorIdlingResource(countingTaskExecutorRule),
            new IntentServiceIdlingResource(StorageMigrationService.SERVICE_NAME)
    );

    @Override
    public OpenRosaHttpInterface provideHttpInterface(MimeTypeMap mimeTypeMap, UserAgentProvider userAgentProvider) {
        return server;
    }

    @Override
    public Scheduler providesScheduler(WorkManager workManager) {
        return scheduler;
    }

    @Override
    public GoogleApiProvider providesGoogleApiProvider(Context context, PreferencesProvider preferencesProvider) {
        return new GoogleApiProvider(context) {

            @Override
            public SheetsApi getSheetsApi(String account) {
                googleApi.setAttemptAccount(account);
                return googleApi;
            }

            @Override
            public DriveApi getDriveApi(String account) {
                googleApi.setAttemptAccount(account);
                return googleApi;
            }
        };
    }

    @Override
    public GoogleAccountPicker providesGoogleAccountPicker(Context context) {
        return googleAccountPicker;
    }
}

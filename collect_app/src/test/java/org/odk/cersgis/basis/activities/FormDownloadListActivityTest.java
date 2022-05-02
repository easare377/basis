package org.odk.cersgis.basis.activities;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.analytics.AnalyticsEvents;
import org.odk.cersgis.basis.dao.FormsDao;
import org.odk.cersgis.basis.listeners.PermissionListener;
import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;
import org.odk.cersgis.basis.storage.StorageInitializer;
import org.odk.cersgis.basis.utilities.PermissionUtils;
import org.robolectric.shadows.ShadowEnvironment;

import static android.os.Environment.MEDIA_MOUNTED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.odk.cersgis.basis.support.RobolectricHelpers.overrideAppDependencyModule;

@RunWith(AndroidJUnit4.class)
public class FormDownloadListActivityTest {

    @Rule public MockitoRule rule = MockitoJUnit.rule();

    @Mock Analytics analytics;

    @Mock FormsDao formsDao;

    @Before public void setup() {
        overrideAppDependencyModule(new AppDependencyModule(analytics, formsDao));
        ShadowEnvironment.setExternalStorageState(MEDIA_MOUNTED); // Required for ODK directories to be created
    }

    @Test
    public void tappingDownloadButton_logsAnalytics() {
        Cursor cursor = mock(Cursor.class);
        when(cursor.getCount()).thenReturn(0);
        when(formsDao.getFormsCursor()).thenReturn(cursor);

        ActivityScenario<FormDownloadListActivity> downloadActivity = ActivityScenario.launch(FormDownloadListActivity.class);

        downloadActivity.onActivity(activity -> {
            activity.findViewById(R.id.add_button).setEnabled(true);
            activity.findViewById(R.id.add_button).performClick();
            verify(analytics).logEvent(eq(AnalyticsEvents.FIRST_FORM_DOWNLOAD), any());
        });
    }

    private static class AppDependencyModule extends org.odk.cersgis.basis.injection.config.AppDependencyModule {

        Analytics analytics;
        FormsDao formsDao;

        AppDependencyModule(Analytics analytics, FormsDao formsDao) {
            this.analytics = analytics;
            this.formsDao = formsDao;
        }

        @Override
        public Analytics providesAnalytics(Application application, GeneralSharedPreferences generalSharedPreferences) {
            return analytics;
        }

        @Override
        public FormsDao provideFormsDao() {
            return formsDao;
        }

        @Override
        public PermissionUtils providesPermissionUtils() {
            return new AlwaysGrantStoragePermissionsPermissionUtils();
        }

        @Override
        public StorageInitializer providesStorageInitializer() {
            return new AlwaysSuccessfullyCreateOdkDirsStorageInitializer();
        }
    }

    private static class AlwaysGrantStoragePermissionsPermissionUtils extends PermissionUtils {
        private AlwaysGrantStoragePermissionsPermissionUtils() {
            super(R.style.Theme_Collect_Dialog_PermissionAlert);
        }

        @Override
        public void requestStoragePermissions(Activity activity, @NonNull PermissionListener action) {
            action.granted();
        }
    }

    private static class AlwaysSuccessfullyCreateOdkDirsStorageInitializer extends StorageInitializer {
        @Override
        public void createOdkDirsOnStorage() {
            // do nothing
        }
    }
}
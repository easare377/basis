package org.odk.cersgis.basis.injection.config;

import android.app.Application;

import org.javarosa.core.reference.ReferenceManager;
import org.odk.cersgis.basis.activities.DeleteSavedFormActivity;
import org.odk.cersgis.basis.activities.FillBlankFormActivity;
import org.odk.cersgis.basis.activities.FormDownloadListActivity;
import org.odk.cersgis.basis.activities.FormEntryActivity;
import org.odk.cersgis.basis.activities.FormHierarchyActivity;
import org.odk.cersgis.basis.activities.FormMapActivity;
import org.odk.cersgis.basis.activities.GeoPointMapActivity;
import org.odk.cersgis.basis.activities.GeoPolyActivity;
import org.odk.cersgis.basis.activities.InstanceUploaderActivity;
import org.odk.cersgis.basis.activities.InstanceUploaderListActivity;
import org.odk.cersgis.basis.activities.MainMenuActivity;
import org.odk.cersgis.basis.activities.SplashScreenActivity;
import org.odk.cersgis.basis.adapters.InstanceUploaderAdapter;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.application.Collect;
import org.odk.cersgis.basis.application.initialization.ApplicationInitializer;
import org.odk.cersgis.basis.audio.AudioRecordingControllerFragment;
import org.odk.cersgis.basis.audio.AudioRecordingErrorDialogFragment;
import org.odk.cersgis.basis.backgroundwork.AutoSendTaskSpec;
import org.odk.cersgis.basis.backgroundwork.AutoUpdateTaskSpec;
import org.odk.cersgis.basis.backgroundwork.SyncFormsTaskSpec;
import org.odk.cersgis.basis.configure.SettingsImporter;
import org.odk.cersgis.basis.configure.qr.QRCodeScannerFragment;
import org.odk.cersgis.basis.configure.qr.QRCodeTabsActivity;
import org.odk.cersgis.basis.configure.qr.ShowQRCodeFragment;
import org.odk.cersgis.basis.formentry.ODKView;
import org.odk.cersgis.basis.formentry.QuitFormDialogFragment;
import org.odk.cersgis.basis.formentry.saving.SaveAnswerFileErrorDialogFragment;
import org.odk.cersgis.basis.formentry.saving.SaveFormProgressDialogFragment;
import org.odk.cersgis.basis.fragments.BarCodeScannerFragment;
import org.odk.cersgis.basis.fragments.BlankFormListFragment;
import org.odk.cersgis.basis.fragments.MapBoxInitializationFragment;
import org.odk.cersgis.basis.fragments.SavedFormListFragment;
import org.odk.cersgis.basis.fragments.dialogs.SelectMinimalDialog;
import org.odk.cersgis.basis.gdrive.GoogleDriveActivity;
import org.odk.cersgis.basis.gdrive.GoogleSheetsUploaderActivity;
import org.odk.cersgis.basis.geo.GoogleMapFragment;
import org.odk.cersgis.basis.geo.MapboxMapFragment;
import org.odk.cersgis.basis.geo.OsmDroidMapFragment;
import org.odk.cersgis.basis.logic.PropertyManager;
import org.odk.cersgis.basis.openrosa.OpenRosaHttpInterface;
import org.odk.cersgis.basis.preferences.AdminPasswordDialogFragment;
import org.odk.cersgis.basis.preferences.AdminPreferencesFragment;
import org.odk.cersgis.basis.preferences.AdminSharedPreferences;
import org.odk.cersgis.basis.preferences.BasePreferenceFragment;
import org.odk.cersgis.basis.preferences.ExperimentalPreferencesFragment;
import org.odk.cersgis.basis.preferences.FormManagementPreferences;
import org.odk.cersgis.basis.preferences.FormMetadataFragment;
import org.odk.cersgis.basis.preferences.GeneralPreferencesFragment;
import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;
import org.odk.cersgis.basis.preferences.IdentityPreferences;
import org.odk.cersgis.basis.preferences.PreferencesActivity;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.preferences.ServerAuthDialogFragment;
import org.odk.cersgis.basis.preferences.ServerPreferencesFragment;
import org.odk.cersgis.basis.preferences.UserInterfacePreferencesFragment;
import org.odk.cersgis.basis.storage.StorageInitializer;
import org.odk.cersgis.basis.storage.migration.StorageMigrationDialog;
import org.odk.cersgis.basis.storage.migration.StorageMigrationService;
import org.odk.cersgis.basis.tasks.InstanceServerUploaderTask;
import org.odk.cersgis.basis.utilities.ApplicationResetter;
import org.odk.cersgis.basis.utilities.AuthDialogUtility;
import org.odk.cersgis.basis.widgets.ExStringWidget;
import org.odk.cersgis.basis.widgets.QuestionWidget;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * Dagger component for the application. Should include
 * application level Dagger Modules and be built with Application
 * object.
 *
 * Add an `inject(MyClass myClass)` method here for objects you want
 * to inject into so Dagger knows to wire it up.
 *
 * Annotated with @Singleton so modules can include @Singletons that will
 * be retained at an application level (as this an instance of this components
 * is owned by the Application object).
 *
 * If you need to call a provider directly from the component (in a test
 * for example) you can add a method with the type you are looking to fetch
 * (`MyType myType()`) to this interface.
 *
 * To read more about Dagger visit: https://google.github.io/dagger/users-guide
 **/

@Singleton
@Component(modules = {
        AppDependencyModule.class
})
public interface AppDependencyComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder appDependencyModule(AppDependencyModule testDependencyModule);

        AppDependencyComponent build();
    }

    void inject(Collect collect);

    void inject(InstanceUploaderAdapter instanceUploaderAdapter);

    void inject(SavedFormListFragment savedFormListFragment);

    void inject(PropertyManager propertyManager);

    void inject(FormEntryActivity formEntryActivity);

    void inject(InstanceServerUploaderTask uploader);

    void inject(ServerPreferencesFragment serverPreferencesFragment);

    void inject(AuthDialogUtility authDialogUtility);

    void inject(FormDownloadListActivity formDownloadListActivity);

    void inject(InstanceUploaderListActivity activity);

    void inject(GoogleDriveActivity googleDriveActivity);

    void inject(GoogleSheetsUploaderActivity googleSheetsUploaderActivity);

    void inject(QuestionWidget questionWidget);

    void inject(ExStringWidget exStringWidget);

    void inject(ODKView odkView);

    void inject(FormMetadataFragment formMetadataFragment);

    void inject(GeoPointMapActivity geoMapActivity);

    void inject(GeoPolyActivity geoPolyActivity);

    void inject(FormMapActivity formMapActivity);

    void inject(OsmDroidMapFragment mapFragment);

    void inject(GoogleMapFragment mapFragment);

    void inject(MapboxMapFragment mapFragment);

    void inject(MainMenuActivity mainMenuActivity);

    void inject(QRCodeTabsActivity qrCodeTabsActivity);

    void inject(ShowQRCodeFragment showQRCodeFragment);

    void inject(StorageInitializer storageInitializer);

    void inject(StorageMigrationService storageMigrationService);

    void inject(AutoSendTaskSpec autoSendTaskSpec);

    void inject(StorageMigrationDialog storageMigrationDialog);

    void inject(AdminPasswordDialogFragment adminPasswordDialogFragment);

    void inject(SplashScreenActivity splashScreenActivity);

    void inject(FormHierarchyActivity formHierarchyActivity);

    void inject(FormManagementPreferences formManagementPreferences);

    void inject(IdentityPreferences identityPreferences);

    void inject(UserInterfacePreferencesFragment userInterfacePreferencesFragment);

    void inject(SaveFormProgressDialogFragment saveFormProgressDialogFragment);

    void inject(QuitFormDialogFragment quitFormDialogFragment);

    void inject(BarCodeScannerFragment barCodeScannerFragment);

    void inject(QRCodeScannerFragment qrCodeScannerFragment);

    void inject(PreferencesActivity preferencesActivity);

    void inject(ApplicationResetter applicationResetter);

    void inject(FillBlankFormActivity fillBlankFormActivity);

    void inject(MapBoxInitializationFragment mapBoxInitializationFragment);

    void inject(SyncFormsTaskSpec syncWork);

    void inject(ExperimentalPreferencesFragment experimentalPreferencesFragment);

    void inject(AutoUpdateTaskSpec autoUpdateTaskSpec);

    void inject(ServerAuthDialogFragment serverAuthDialogFragment);

    void inject(BasePreferenceFragment basePreferenceFragment);

    void inject(BlankFormListFragment blankFormListFragment);

    void inject(InstanceUploaderActivity instanceUploaderActivity);

    void inject(GeneralPreferencesFragment generalPreferencesFragment);

    void inject(DeleteSavedFormActivity deleteSavedFormActivity);

    void inject(AdminPreferencesFragment.MainMenuAccessPreferences mainMenuAccessPreferences);

    void inject(SelectMinimalDialog selectMinimalDialog);

    void inject(AudioRecordingControllerFragment audioRecordingControllerFragment);

    void inject(SaveAnswerFileErrorDialogFragment saveAnswerFileErrorDialogFragment);

    void inject(AudioRecordingErrorDialogFragment audioRecordingErrorDialogFragment);

    OpenRosaHttpInterface openRosaHttpInterface();

    ReferenceManager referenceManager();

    Analytics analytics();

    GeneralSharedPreferences generalSharedPreferences();

    AdminSharedPreferences adminSharedPreferences();

    PreferencesProvider preferencesProvider();

    ApplicationInitializer applicationInitializer();

    SettingsImporter settingsImporter();
}

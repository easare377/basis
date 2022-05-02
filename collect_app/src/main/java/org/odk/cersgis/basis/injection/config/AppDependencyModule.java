package org.odk.cersgis.basis.injection.config;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.TelephonyManager;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkManager;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import org.javarosa.core.reference.ReferenceManager;
import org.odk.cersgis.basis.BuildConfig;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.analytics.FirebaseAnalytics;
import org.odk.cersgis.basis.application.CollectSettingsChangeHandler;
import org.odk.cersgis.basis.application.initialization.ApplicationInitializer;
import org.odk.cersgis.basis.application.initialization.CollectSettingsPreferenceMigrator;
import org.odk.cersgis.basis.application.initialization.SettingsPreferenceMigrator;
import org.odk.cersgis.basis.backgroundwork.ChangeLock;
import org.odk.cersgis.basis.backgroundwork.FormSubmitManager;
import org.odk.cersgis.basis.backgroundwork.FormUpdateManager;
import org.odk.cersgis.basis.backgroundwork.ReentrantLockChangeLock;
import org.odk.cersgis.basis.backgroundwork.SchedulerFormUpdateAndSubmitManager;
import org.odk.cersgis.basis.configure.ServerRepository;
import org.odk.cersgis.basis.configure.SettingsChangeHandler;
import org.odk.cersgis.basis.configure.SettingsImporter;
import org.odk.cersgis.basis.configure.SharedPreferencesServerRepository;
import org.odk.cersgis.basis.configure.StructureAndTypeSettingsValidator;
import org.odk.cersgis.basis.configure.qr.CachingQRCodeGenerator;
import org.odk.cersgis.basis.configure.qr.QRCodeDecoder;
import org.odk.cersgis.basis.configure.qr.QRCodeGenerator;
import org.odk.cersgis.basis.configure.qr.QRCodeUtils;
import org.odk.cersgis.basis.dao.FormsDao;
import org.odk.cersgis.basis.dao.InstancesDao;
import org.odk.cersgis.basis.database.DatabaseFormsRepository;
import org.odk.cersgis.basis.database.DatabaseInstancesRepository;
import org.odk.cersgis.basis.database.DatabaseMediaFileRepository;
import org.odk.cersgis.basis.events.RxEventBus;
import org.odk.cersgis.basis.formentry.media.AudioHelperFactory;
import org.odk.cersgis.basis.formentry.media.ScreenContextAudioHelperFactory;
import org.odk.cersgis.basis.formentry.saving.DiskFormSaver;
import org.odk.cersgis.basis.formentry.saving.FormSaveViewModel;
import org.odk.cersgis.basis.formmanagement.DiskFormsSynchronizer;
import org.odk.cersgis.basis.formmanagement.FormDownloader;
import org.odk.cersgis.basis.formmanagement.FormMetadataParser;
import org.odk.cersgis.basis.formmanagement.ServerFormDownloader;
import org.odk.cersgis.basis.formmanagement.ServerFormsDetailsFetcher;
import org.odk.cersgis.basis.formmanagement.matchexactly.ServerFormsSynchronizer;
import org.odk.cersgis.basis.formmanagement.matchexactly.SyncStatusRepository;
import org.odk.cersgis.basis.forms.FormSource;
import org.odk.cersgis.basis.forms.FormsRepository;
import org.odk.cersgis.basis.forms.MediaFileRepository;
import org.odk.cersgis.basis.gdrive.GoogleAccountCredentialGoogleAccountPicker;
import org.odk.cersgis.basis.gdrive.GoogleAccountPicker;
import org.odk.cersgis.basis.gdrive.GoogleApiProvider;
import org.odk.cersgis.basis.geo.MapProvider;
import org.odk.cersgis.basis.instances.InstancesRepository;
import org.odk.cersgis.basis.logic.PropertyManager;
import org.odk.cersgis.basis.metadata.InstallIDProvider;
import org.odk.cersgis.basis.metadata.SharedPreferencesInstallIDProvider;
import org.odk.cersgis.basis.network.ConnectivityProvider;
import org.odk.cersgis.basis.network.NetworkStateProvider;
import org.odk.cersgis.basis.notifications.NotificationManagerNotifier;
import org.odk.cersgis.basis.notifications.Notifier;
import org.odk.cersgis.basis.openrosa.CollectThenSystemContentTypeMapper;
import org.odk.cersgis.basis.openrosa.OpenRosaFormSource;
import org.odk.cersgis.basis.openrosa.OpenRosaHttpInterface;
import org.odk.cersgis.basis.openrosa.okhttp.OkHttpConnection;
import org.odk.cersgis.basis.openrosa.okhttp.OkHttpOpenRosaServerClientProvider;
import org.odk.cersgis.basis.preferences.AdminKeys;
import org.odk.cersgis.basis.preferences.AdminSharedPreferences;
import org.odk.cersgis.basis.preferences.GeneralKeys;
import org.odk.cersgis.basis.preferences.GeneralSharedPreferences;
import org.odk.cersgis.basis.preferences.JsonPreferencesGenerator;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.storage.StorageInitializer;
import org.odk.cersgis.basis.storage.StoragePathProvider;
import org.odk.cersgis.basis.storage.StorageStateProvider;
import org.odk.cersgis.basis.storage.StorageSubdirectory;
import org.odk.cersgis.basis.storage.migration.StorageEraser;
import org.odk.cersgis.basis.storage.migration.StorageMigrationRepository;
import org.odk.cersgis.basis.storage.migration.StorageMigrator;
import org.odk.cersgis.basis.utilities.ActivityAvailability;
import org.odk.cersgis.basis.utilities.AdminPasswordProvider;
import org.odk.cersgis.basis.utilities.AndroidUserAgent;
import org.odk.cersgis.basis.utilities.DeviceDetailsProvider;
import org.odk.cersgis.basis.utilities.FileProvider;
import org.odk.cersgis.basis.utilities.FileUtil;
import org.odk.cersgis.basis.utilities.FormsDirDiskFormsSynchronizer;
import org.odk.cersgis.basis.utilities.MediaUtils;
import org.odk.cersgis.basis.utilities.PermissionUtils;
import org.odk.cersgis.basis.utilities.ScreenUtils;
import org.odk.cersgis.basis.utilities.SoftKeyboardController;
import org.odk.cersgis.basis.utilities.WebCredentialsUtils;
import org.odk.cersgis.basis.version.VersionInformation;
import org.odk.cersgis.basis.views.BarcodeViewDecoder;
import org.odk.cersgis.async.CoroutineAndWorkManagerScheduler;
import org.odk.cersgis.async.Scheduler;
import org.odk.cersgis.audiorecorder.recording.AudioRecorderViewModelFactory;
import org.odk.cersgis.utilities.Clock;
import org.odk.cersgis.utilities.UserAgentProvider;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

import static androidx.core.content.FileProvider.getUriForFile;
import static org.odk.cersgis.basis.preferences.MetaKeys.KEY_INSTALL_ID;

/**
 * Add dependency providers here (annotated with @Provides)
 * for objects you need to inject
 */
@Module
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class AppDependencyModule {

    @Provides
    Context context(Application application) {
        return application;
    }

    @Provides
    public InstancesDao provideInstancesDao() {
        return new InstancesDao();
    }

    @Provides
    public FormsDao provideFormsDao() {
        return new FormsDao();
    }

    @Provides
    @Singleton
    RxEventBus provideRxEventBus() {
        return new RxEventBus();
    }

    @Provides
    MimeTypeMap provideMimeTypeMap() {
        return MimeTypeMap.getSingleton();
    }

    @Provides
    @Singleton
    UserAgentProvider providesUserAgent() {
        return new AndroidUserAgent();
    }

    @Provides
    @Singleton
    public OpenRosaHttpInterface provideHttpInterface(MimeTypeMap mimeTypeMap, UserAgentProvider userAgentProvider) {
        return new OkHttpConnection(
                new OkHttpOpenRosaServerClientProvider(new OkHttpClient()),
                new CollectThenSystemContentTypeMapper(mimeTypeMap),
                userAgentProvider.getUserAgent()
        );
    }

    @Provides
    WebCredentialsUtils provideWebCredentials() {
        return new WebCredentialsUtils();
    }

    @Provides
    public FormDownloader providesFormDownloader(FormSource formSource, FormsRepository formsRepository, StoragePathProvider storagePathProvider, Analytics analytics) {
        return new ServerFormDownloader(formSource, formsRepository, new File(storagePathProvider.getDirPath(StorageSubdirectory.CACHE)), storagePathProvider.getDirPath(StorageSubdirectory.FORMS), new FormMetadataParser(ReferenceManager.instance()), analytics);
    }

    @Provides
    @Singleton
    public Analytics providesAnalytics(Application application, GeneralSharedPreferences generalSharedPreferences) {
        com.google.firebase.analytics.FirebaseAnalytics firebaseAnalyticsInstance = com.google.firebase.analytics.FirebaseAnalytics.getInstance(application);
        return new FirebaseAnalytics(firebaseAnalyticsInstance, generalSharedPreferences);
    }

    @Provides
    public PermissionUtils providesPermissionUtils() {
        return new PermissionUtils(R.style.Theme_Collect_Dialog_PermissionAlert);
    }

    @Provides
    public ReferenceManager providesReferenceManager() {
        return ReferenceManager.instance();
    }

    @Provides
    public AudioHelperFactory providesAudioHelperFactory(Scheduler scheduler) {
        return new ScreenContextAudioHelperFactory(scheduler, MediaPlayer::new);
    }

    @Provides
    public ActivityAvailability providesActivityAvailability(Context context) {
        return new ActivityAvailability(context);
    }

    @Provides
    @Singleton
    public StorageMigrationRepository providesStorageMigrationRepository() {
        return new StorageMigrationRepository();
    }

    @Provides
    @Singleton
    public StorageInitializer providesStorageInitializer() {
        return new StorageInitializer();
    }

    @Provides
    public StorageMigrator providesStorageMigrator(StoragePathProvider storagePathProvider, StorageStateProvider storageStateProvider, StorageMigrationRepository storageMigrationRepository, ReferenceManager referenceManager, FormUpdateManager formUpdateManager, FormSubmitManager formSubmitManager, Analytics analytics, @Named("FORMS") ChangeLock changeLock) {
        StorageEraser storageEraser = new StorageEraser(storagePathProvider);

        return new StorageMigrator(storagePathProvider, storageStateProvider, storageEraser, storageMigrationRepository, GeneralSharedPreferences.getInstance(), referenceManager, analytics);
    }

    @Provides
    public PreferencesProvider providesPreferencesProvider(Context context) {
        return new PreferencesProvider(context);
    }

    @Provides
    InstallIDProvider providesInstallIDProvider(PreferencesProvider preferencesProvider) {
        return new SharedPreferencesInstallIDProvider(preferencesProvider.getMetaSharedPreferences(), KEY_INSTALL_ID);
    }

    @Provides
    public DeviceDetailsProvider providesDeviceDetailsProvider(Context context, InstallIDProvider installIDProvider) {
        return new DeviceDetailsProvider() {

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getDeviceId() {
                return installIDProvider.getInstallID();
            }

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getLine1Number() {
                TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return telMgr.getLine1Number();
            }
        };
    }

    @Provides
    @Singleton
    GeneralSharedPreferences providesGeneralSharedPreferences(Context context) {
        return new GeneralSharedPreferences(context);
    }

    @Provides
    @Singleton
    AdminSharedPreferences providesAdminSharedPreferences(Context context) {
        return new AdminSharedPreferences(context);
    }

    @Provides
    @Singleton
    public MapProvider providesMapProvider() {
        return new MapProvider();
    }

    @Provides
    @Singleton
    public StorageStateProvider providesStorageStateProvider() {
        return new StorageStateProvider();
    }

    @Provides
    public StoragePathProvider providesStoragePathProvider() {
        return new StoragePathProvider();
    }

    @Provides
    public AdminPasswordProvider providesAdminPasswordProvider() {
        return new AdminPasswordProvider(AdminSharedPreferences.getInstance());
    }

    @Provides
    public FormUpdateManager providesFormUpdateManger(Scheduler scheduler, PreferencesProvider preferencesProvider, Application application, WorkManager workManager) {
        return new SchedulerFormUpdateAndSubmitManager(scheduler, preferencesProvider.getGeneralSharedPreferences(), application);
    }

    @Provides
    public FormSubmitManager providesFormSubmitManager(Scheduler scheduler, PreferencesProvider preferencesProvider, Application application, WorkManager workManager) {
        return new SchedulerFormUpdateAndSubmitManager(scheduler, preferencesProvider.getGeneralSharedPreferences(), application);
    }

    @Provides
    public NetworkStateProvider providesConnectivityProvider() {
        return new ConnectivityProvider();
    }

    @Provides
    public QRCodeGenerator providesQRCodeGenerator(Context context) {
        return new CachingQRCodeGenerator();
    }

    @Provides
    public VersionInformation providesVersionInformation() {
        return new VersionInformation(() -> BuildConfig.VERSION_NAME);
    }

    @Provides
    public FileProvider providesFileProvider(Context context) {
        return filePath -> getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(filePath));
    }

    @Provides
    public WorkManager providesWorkManager(Context context) {
        return WorkManager.getInstance(context);
    }

    @Provides
    public Scheduler providesScheduler(WorkManager workManager) {
        return new CoroutineAndWorkManagerScheduler(workManager);
    }

    @Singleton
    @Provides
    public ApplicationInitializer providesApplicationInitializer(Application application, UserAgentProvider userAgentProvider, SettingsPreferenceMigrator preferenceMigrator, PropertyManager propertyManager, Analytics analytics) {
        return new ApplicationInitializer(application, userAgentProvider, preferenceMigrator, propertyManager, analytics);
    }

    @Provides
    public SettingsPreferenceMigrator providesPreferenceMigrator(PreferencesProvider preferencesProvider) {
        return new CollectSettingsPreferenceMigrator(preferencesProvider.getMetaSharedPreferences());
    }

    @Provides
    @Singleton
    public PropertyManager providesPropertyManager(Application application, RxEventBus eventBus, PermissionUtils permissionUtils, DeviceDetailsProvider deviceDetailsProvider) {
        return new PropertyManager(application, eventBus, permissionUtils, deviceDetailsProvider);
    }

    @Provides
    public ServerRepository providesServerRepository(Context context, PreferencesProvider preferencesProvider) {
        return new SharedPreferencesServerRepository(context.getString(R.string.default_server_url), preferencesProvider.getMetaSharedPreferences());
    }

    @Provides
    public SettingsChangeHandler providesSettingsChangeHandler(PropertyManager propertyManager, FormUpdateManager formUpdateManager, ServerRepository serverRepository, Analytics analytics, PreferencesProvider preferencesProvider) {
        return new CollectSettingsChangeHandler(propertyManager, formUpdateManager, serverRepository, analytics, preferencesProvider);
    }

    @Provides
    public SettingsImporter providesCollectSettingsImporter(PreferencesProvider preferencesProvider, SettingsPreferenceMigrator preferenceMigrator, SettingsChangeHandler settingsChangeHandler) {
        HashMap<String, Object> generalDefaults = GeneralKeys.DEFAULTS;
        Map<String, Object> adminDefaults = AdminKeys.getDefaults();
        return new SettingsImporter(
                preferencesProvider.getGeneralSharedPreferences(),
                preferencesProvider.getAdminSharedPreferences(),
                preferenceMigrator,
                new StructureAndTypeSettingsValidator(generalDefaults, adminDefaults),
                generalDefaults,
                adminDefaults,
                settingsChangeHandler
        );
    }

    @Provides
    public BarcodeViewDecoder providesBarcodeViewDecoder() {
        return new BarcodeViewDecoder();
    }

    @Provides
    public QRCodeDecoder providesQRCodeDecoder() {
        return new QRCodeUtils();
    }

    @Provides
    public FormsRepository providesFormRepository() {
        return new DatabaseFormsRepository();
    }

    @Provides
    public MediaFileRepository providesMediaFileRepository() {
        return new DatabaseMediaFileRepository(new FormsDao(), new FileUtil());
    }

    @Provides
    public FormSource providesFormSource(GeneralSharedPreferences generalSharedPreferences, Context context, OpenRosaHttpInterface openRosaHttpInterface, WebCredentialsUtils webCredentialsUtils, Analytics analytics) {
        SharedPreferences generalPrefs = generalSharedPreferences.getSharedPreferences();
        String serverURL = generalPrefs.getString(GeneralKeys.KEY_SERVER_URL, context.getString(R.string.default_server_url));
        String formListPath = generalPrefs.getString(GeneralKeys.KEY_FORMLIST_URL, context.getString(R.string.default_odk_formlist));

        return new OpenRosaFormSource(serverURL, formListPath, openRosaHttpInterface, webCredentialsUtils, analytics);
    }

    @Provides
    public DiskFormsSynchronizer providesDiskFormSynchronizer() {
        return new FormsDirDiskFormsSynchronizer();
    }

    @Provides
    @Singleton
    public SyncStatusRepository providesServerFormSyncRepository() {
        return new SyncStatusRepository();
    }

    @Provides
    public ServerFormsDetailsFetcher providesServerFormDetailsFetcher(FormsRepository formsRepository, MediaFileRepository mediaFileRepository, FormSource formSource, DiskFormsSynchronizer diskFormsSynchronizer) {
        return new ServerFormsDetailsFetcher(formsRepository, mediaFileRepository, formSource, diskFormsSynchronizer);
    }

    @Provides
    public ServerFormsSynchronizer providesServerFormSynchronizer(ServerFormsDetailsFetcher serverFormsDetailsFetcher, FormsRepository formsRepository, FormDownloader formDownloader, InstancesRepository instancesRepository) {
        return new ServerFormsSynchronizer(serverFormsDetailsFetcher, formsRepository, instancesRepository, formDownloader);
    }

    @Provides
    public Notifier providesNotifier(Application application, PreferencesProvider preferencesProvider) {
        return new NotificationManagerNotifier(application, preferencesProvider);
    }

    @Provides
    @Named("FORMS")
    @Singleton
    public ChangeLock providesFormsChangeLock() {
        return new ReentrantLockChangeLock();
    }

    @Provides
    @Named("INSTANCES")
    @Singleton
    public ChangeLock providesInstancesChangeLock() {
        return new ReentrantLockChangeLock();
    }

    @Provides
    public InstancesRepository providesInstancesRepository() {
        return new DatabaseInstancesRepository();
    }

    @Provides
    public GoogleApiProvider providesGoogleApiProvider(Context context, PreferencesProvider preferencesProvider) {
        return new GoogleApiProvider(context
        );
    }

    @Provides
    public GoogleAccountPicker providesGoogleAccountPicker(Context context) {
        return new GoogleAccountCredentialGoogleAccountPicker(GoogleAccountCredential
                .usingOAuth2(context, Collections.singletonList(DriveScopes.DRIVE))
                .setBackOff(new ExponentialBackOff()));
    }

    @Provides
    ScreenUtils providesScreenUtils(Context context) {
        return new ScreenUtils(context);
    }

    @Provides
    public AudioRecorderViewModelFactory providesAudioRecorderViewModelFactory(Application application) {
        return new AudioRecorderViewModelFactory(application);
    }

    @Provides
    public FormSaveViewModel.FactoryFactory providesFormSaveViewModelFactoryFactory(Analytics analytics, Scheduler scheduler) {
        return (owner, defaultArgs) -> new AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            @NonNull
            @Override
            protected <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle handle) {
                return (T) new FormSaveViewModel(handle, System::currentTimeMillis, new DiskFormSaver(), new MediaUtils(), analytics, scheduler);
            }
        };
    }

    @Provides
    public Clock providesClock() {
        return System::currentTimeMillis;
    }

    @Provides
    public SoftKeyboardController provideSoftKeyboardController() {
        return new SoftKeyboardController();
    }
  
    @Provides
    public JsonPreferencesGenerator providesJsonPreferencesGenerator() {
        return new JsonPreferencesGenerator();
    }
}

package org.odk.cersgis.basis.notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.activities.FillBlankFormActivity;
import org.odk.cersgis.basis.activities.FormDownloadListActivity;
import org.odk.cersgis.basis.activities.NotificationActivity;
import org.odk.cersgis.basis.formmanagement.FormSourceExceptionMapper;
import org.odk.cersgis.basis.formmanagement.ServerFormDetails;
import org.odk.cersgis.basis.forms.FormSourceException;
import org.odk.cersgis.basis.preferences.MetaKeys;
import org.odk.cersgis.basis.preferences.PreferencesProvider;
import org.odk.cersgis.basis.utilities.IconUtils;
import org.odk.cersgis.basis.utilities.TranslationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import static android.content.Context.NOTIFICATION_SERVICE;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;
import static org.odk.cersgis.basis.activities.FormDownloadListActivity.DISPLAY_ONLY_UPDATED_FORMS;
import static org.odk.cersgis.basis.utilities.ApplicationConstants.RequestCodes.FORMS_DOWNLOADED_NOTIFICATION;
import static org.odk.cersgis.basis.utilities.ApplicationConstants.RequestCodes.FORMS_UPLOADED_NOTIFICATION;
import static org.odk.cersgis.basis.utilities.ApplicationConstants.RequestCodes.FORM_UPDATES_AVAILABLE_NOTIFICATION;

public class NotificationManagerNotifier implements Notifier {

    private static final String COLLECT_NOTIFICATION_CHANNEL = "collect_notification_channel";
    private final Application application;
    private final NotificationManager notificationManager;
    private final PreferencesProvider preferencesProvider;

    private static final int FORM_UPDATE_NOTIFICATION_ID = 0;
    private static final int FORM_SYNC_NOTIFICATION_ID = 1;
    private static final int AUTO_SEND_RESULT_NOTIFICATION_ID = 1328974928;

    public NotificationManagerNotifier(Application application, PreferencesProvider preferencesProvider) {
        this.application = application;
        notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);
        this.preferencesProvider = preferencesProvider;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel(
                        COLLECT_NOTIFICATION_CHANNEL,
                        TranslationHandler.getString(application, R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT)
                );
            }
        }
    }

    @Override
    public void onUpdatesAvailable(List<ServerFormDetails> updates) {
        SharedPreferences metaPrefs = preferencesProvider.getMetaSharedPreferences();
        Set<String> updateId = updates.stream().map(f -> f.getFormId() + f.getHash() + (f.getManifest() != null ? f.getManifest().getHash() : null)).collect(toSet());
        if (metaPrefs.getStringSet(MetaKeys.LAST_UPDATED_NOTIFICATION, emptySet()).equals(updateId)) {
            return;
        }

        Intent intent = new Intent(application, FormDownloadListActivity.class);
        intent.putExtra(DISPLAY_ONLY_UPDATED_FORMS, true);
        PendingIntent contentIntent = PendingIntent.getActivity(application, FORM_UPDATES_AVAILABLE_NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, COLLECT_NOTIFICATION_CHANNEL)
                .setContentIntent(contentIntent)
                .setContentTitle(TranslationHandler.getString(application, R.string.form_updates_available))
                .setContentText(null)
                .setSmallIcon(IconUtils.getNotificationAppIcon())
                .setAutoCancel(true);

        notificationManager.notify(FORM_UPDATE_NOTIFICATION_ID, builder.build());

        metaPrefs.edit()
                .putStringSet(MetaKeys.LAST_UPDATED_NOTIFICATION, updateId)
                .apply();
    }

    @Override
    public void onUpdatesDownloaded(HashMap<ServerFormDetails, String> result) {
        Intent intent = new Intent(application, NotificationActivity.class);
        intent.putExtra(NotificationActivity.NOTIFICATION_TITLE, TranslationHandler.getString(application, R.string.download_forms_result));
        intent.putExtra(NotificationActivity.NOTIFICATION_MESSAGE, FormDownloadListActivity.getDownloadResultMessage(result));
        PendingIntent contentIntent = PendingIntent.getActivity(application, FORMS_DOWNLOADED_NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String content = TranslationHandler.getString(application, allFormsDownloadedSuccessfully(result) ?
                R.string.success :
                R.string.failures);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, COLLECT_NOTIFICATION_CHANNEL)
                .setContentIntent(contentIntent)
                .setContentTitle(TranslationHandler.getString(application, R.string.odk_auto_download_notification_title))
                .setContentText(content)
                .setSmallIcon(IconUtils.getNotificationAppIcon())
                .setAutoCancel(true);

        notificationManager.notify(FORM_UPDATE_NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onSync(@Nullable FormSourceException exception) {
        if (exception != null) {
            Intent intent = new Intent(application, FillBlankFormActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(application, FORM_SYNC_NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(application, COLLECT_NOTIFICATION_CHANNEL)
                    .setContentIntent(contentIntent)
                    .setContentTitle(TranslationHandler.getString(application, R.string.form_update_error))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(new FormSourceExceptionMapper(application).getMessage(exception)))
                    .setSmallIcon(IconUtils.getNotificationAppIcon())
                    .setAutoCancel(true);

            notificationManager.notify(FORM_SYNC_NOTIFICATION_ID, builder.build());
        } else {
            notificationManager.cancel(FORM_SYNC_NOTIFICATION_ID);
        }
    }

    @Override
    public void onSubmission(boolean failure, String message) {
        Intent notifyIntent = new Intent(application, NotificationActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notifyIntent.putExtra(NotificationActivity.NOTIFICATION_TITLE, TranslationHandler.getString(application, R.string.upload_results));
        notifyIntent.putExtra(NotificationActivity.NOTIFICATION_MESSAGE, message.trim());

        PendingIntent pendingNotify = PendingIntent.getActivity(application, FORMS_UPLOADED_NOTIFICATION,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String content = failure
                ? TranslationHandler.getString(application, R.string.failures)
                : TranslationHandler.getString(application, R.string.success);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application, COLLECT_NOTIFICATION_CHANNEL)
                .setContentIntent(pendingNotify)
                .setContentTitle(TranslationHandler.getString(application, R.string.odk_auto_note))
                .setContentText(content)
                .setSmallIcon(IconUtils.getNotificationAppIcon())
                .setAutoCancel(true);

        notificationManager.notify(AUTO_SEND_RESULT_NOTIFICATION_ID, builder.build());
    }

    private boolean allFormsDownloadedSuccessfully(HashMap<ServerFormDetails, String> result) {
        for (Map.Entry<ServerFormDetails, String> item : result.entrySet()) {
            if (!item.getValue().equals(TranslationHandler.getString(application, R.string.success))) {
                return false;
            }
        }
        return true;
    }
}

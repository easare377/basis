package org.odk.cersgis.basis.backgroundwork;

import android.content.Context;

import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.odk.cersgis.basis.analytics.Analytics;
import org.odk.cersgis.basis.analytics.AnalyticsEvents;
import org.odk.cersgis.basis.formmanagement.matchexactly.ServerFormsSynchronizer;
import org.odk.cersgis.basis.formmanagement.matchexactly.SyncStatusRepository;
import org.odk.cersgis.basis.injection.DaggerUtils;
import org.odk.cersgis.basis.notifications.Notifier;
import org.odk.cersgis.basis.forms.FormSourceException;
import org.odk.cersgis.async.TaskSpec;
import org.odk.cersgis.async.WorkerAdapter;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

public class SyncFormsTaskSpec implements TaskSpec {

    @Inject
    ServerFormsSynchronizer serverFormsSynchronizer;

    @Inject
    SyncStatusRepository syncStatusRepository;

    @Inject
    Notifier notifier;

    @Inject
    @Named("FORMS")
    ChangeLock changeLock;

    @Inject
    Analytics analytics;

    @NotNull
    @Override
    public Supplier<Boolean> getTask(@NotNull Context context) {
        DaggerUtils.getComponent(context).inject(this);

        return () -> {
            changeLock.withLock((Function<Boolean, Void>) acquiredLock -> {
                if (acquiredLock) {
                    syncStatusRepository.startSync();

                    try {
                        serverFormsSynchronizer.synchronize();
                        syncStatusRepository.finishSync(null);
                        notifier.onSync(null);

                        analytics.logEvent(AnalyticsEvents.MATCH_EXACTLY_SYNC_COMPLETED, "Success");
                    } catch (FormSourceException e) {
                        syncStatusRepository.finishSync(e);
                        notifier.onSync(e);

                        analytics.logEvent(AnalyticsEvents.MATCH_EXACTLY_SYNC_COMPLETED, e.getType().toString());
                    }
                }

                return null;
            });

            return true;
        };
    }

    @NotNull
    @Override
    public Class<? extends WorkerAdapter> getWorkManagerAdapter() {
        return Adapter.class;
    }

    public static class Adapter extends WorkerAdapter {

        public Adapter(@NotNull Context context, @NotNull WorkerParameters workerParams) {
            super(new SyncFormsTaskSpec(), context, workerParams);
        }
    }
}

package org.odk.cersgis.basis.formmanagement.matchexactly;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.odk.cersgis.basis.forms.FormSourceException;

public class SyncStatusRepository {

    private final MutableLiveData<Boolean> syncing = new MutableLiveData<>(false);
    private final MutableLiveData<FormSourceException> lastSyncFailure = new MutableLiveData<>(null);

    public LiveData<Boolean> isSyncing() {
        return syncing;
    }

    public void startSync() {
        syncing.postValue(true);
    }

    public void finishSync(@Nullable FormSourceException exception) {
        lastSyncFailure.postValue(exception);
        syncing.postValue(false);
    }

    public LiveData<FormSourceException> getSyncError() {
        return lastSyncFailure;
    }
}

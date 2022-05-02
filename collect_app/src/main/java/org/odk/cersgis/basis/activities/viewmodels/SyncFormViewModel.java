package org.odk.cersgis.basis.activities.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public abstract class SyncFormViewModel extends BaseObservable {
    public enum State {
        IDLE,
        DOWNLOADING,
        COMPLETE,
        FAILED
    }

    //private final CheckChangedListener checkChangedListener;
    private final String formName;
    private boolean selected;
    private int totalFiles;
    private int filesDownloaded;
    private State currentState;

    public SyncFormViewModel(String formName) {
        //this.checkChangedListener = checkChangedListener;
        this.formName = formName;
    }

    public String getFormName() {
        return formName;
    }

    @Bindable
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (this.selected == selected)
            return;
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
        onCheckChanged(selected);
    }

    @Bindable
    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        if (this.totalFiles == totalFiles)
            return;
        this.totalFiles = totalFiles;
        notifyPropertyChanged(BR.totalFiles);
    }

    @Bindable
    public int getFilesDownloaded() {
        return filesDownloaded;
    }

    public void setFilesDownloaded(int filesDownloaded) {
        this.filesDownloaded = filesDownloaded;
        notifyPropertyChanged(BR.filesDownloaded);
    }

    @Bindable
    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        if(currentState == this.currentState)
            return;
        this.currentState = currentState;
        notifyPropertyChanged(BR.currentState);
        onStateChanged(currentState);
    }

    public abstract void onCheckChanged(boolean checked);

    public abstract void onStateChanged(State currentState);
}

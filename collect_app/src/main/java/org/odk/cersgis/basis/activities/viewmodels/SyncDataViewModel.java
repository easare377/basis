package org.odk.cersgis.basis.activities.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public abstract class SyncDataViewModel extends BaseObservable {
    private boolean selectAll;
    private boolean isBusy;
    private SyncFormViewModel[] forms;
    private int downloadingFormsCount;
    private int formsDownloaded;
    private int formsDownloadFailed;

    @Bindable
    public boolean isSelectAll() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        if(selectAll == this.selectAll)
            return;
        setSelectAll(selectAll, true);
    }

    public void setSelectAll(boolean selectAll, boolean updateForms){
        this.selectAll = selectAll;
        notifyPropertyChanged(BR.selectAll);
        if(updateForms){
            for (SyncFormViewModel form : forms) {
                form.setSelected(selectAll);
            }
        }
    }

    @Bindable
    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
        notifyPropertyChanged(BR.busy);
    }

    @Bindable
    public SyncFormViewModel[] getForms() {
        return forms;
    }

    public void setForms(SyncFormViewModel[] forms) {
        this.forms = forms;
        //notifyPropertyChanged(BR.forms);
        newForms(forms);
    }

    @Bindable
    public int getDownloadingFormsCount() {
        return downloadingFormsCount;
    }

    public void setDownloadingFormsCount(int downloadingFormsCount) {
        this.downloadingFormsCount = downloadingFormsCount;
        notifyPropertyChanged(BR.downloadingFormsCount);
    }

    @Bindable
    public int getFormsDownloaded() {
        return formsDownloaded;
    }

    public void setFormsDownloaded(int formsDownloaded) {
        this.formsDownloaded = formsDownloaded;
        notifyPropertyChanged(BR.formsDownloaded);
    }

    @Bindable
    public int getFormsDownloadFailed() {
        return formsDownloadFailed;
    }

    public void setFormsDownloadFailed(int formsDownloadFailed) {
        this.formsDownloadFailed = formsDownloadFailed;
        notifyPropertyChanged(BR.formsDownloadFailed);
    }

    public abstract void onDownloadSelectedFormsCommand();

    public abstract void onExitCommand();

    public abstract void onRefreshFormsCommand();

    public abstract void onCancelFormsDownloadCommand();

    protected abstract void newForms(SyncFormViewModel[] forms);

}

package org.odk.cersgis.basis.fragments.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class OdfStatusViewModel extends BaseObservable {
   private ArrayList<HashMap<String, String>> communityDataList;
   private int currentIndex;
   private boolean busy;

    @Bindable
    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex){
        if(currentIndex == this.currentIndex)
            return;
        this.currentIndex = currentIndex;
        notifyPropertyChanged(BR.currentIndex);
        onCurrentIndexChanged(currentIndex);
    }

    @Bindable
    public ArrayList<HashMap<String, String>> getCommunityDataList() {
        return communityDataList;
    }

    public void setCommunityDataList(ArrayList<HashMap<String, String>> communityDataList) {
        this.communityDataList = communityDataList;
        notifyPropertyChanged(BR.communityDataList);
        onSetCommunityDataList(communityDataList);
    }

    public abstract void onSetCommunityDataList(ArrayList<HashMap<String, String>> communityDataList);

    public abstract void onCurrentIndexChanged(int currentIndex);

    @Bindable
    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
        notifyPropertyChanged(BR.busy);
    }
}

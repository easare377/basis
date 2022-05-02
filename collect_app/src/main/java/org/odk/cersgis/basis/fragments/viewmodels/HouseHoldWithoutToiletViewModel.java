package org.odk.cersgis.basis.fragments.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class HouseHoldWithoutToiletViewModel extends BaseObservable {
    private int currentIndex;

    private HashMap<String, ArrayList<HouseholdReportViewModel>> communityDataMap;
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
    public HashMap<String, ArrayList<HouseholdReportViewModel>> getCommunityDataMap() {
        return communityDataMap;
    }

    public void setCommunityDataMap(HashMap<String, ArrayList<HouseholdReportViewModel>> communityDataMap) {
        this.communityDataMap = communityDataMap;
        notifyPropertyChanged(BR.communityDataMap);
        onSetHouseHoldWithoutToiletsMap(communityDataMap);
    }

    @Bindable
    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
        notifyPropertyChanged(BR.busy);
    }

    public abstract void onCurrentIndexChanged(int currentIndex);

    public abstract void onSetHouseHoldWithoutToiletsMap(HashMap<String, ArrayList<HouseholdReportViewModel>> communityDataMap);
}

package org.odk.cersgis.basis.activities.viewmodels;

import androidx.databinding.BaseObservable;

import org.odk.cersgis.basis.fragments.viewmodels.HouseHoldWithoutToiletViewModel;
import org.odk.cersgis.basis.fragments.viewmodels.OdfStatusViewModel;

public class HouseholdViewModel extends BaseObservable {
   private OdfStatusViewModel odfStatusViewModel;
   private HouseHoldWithoutToiletViewModel houseHoldWithoutToiletViewModel;

    public HouseholdViewModel(OdfStatusViewModel odfStatusViewModel,
                              HouseHoldWithoutToiletViewModel houseHoldWithoutToiletViewModel) {
        this.odfStatusViewModel = odfStatusViewModel;
        this.houseHoldWithoutToiletViewModel = houseHoldWithoutToiletViewModel;
    }

    public OdfStatusViewModel getOdfStatusViewModel() {
        return odfStatusViewModel;
    }

    public HouseHoldWithoutToiletViewModel getHouseHoldWithoutToiletViewModel() {
        return houseHoldWithoutToiletViewModel;
    }
}

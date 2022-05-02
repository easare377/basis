package org.odk.cersgis.basis.fragments.viewmodels;

public class HouseholdReportViewModel {
    private String status;
    private String householdHeadName;

    public HouseholdReportViewModel(String status, String householdHeadName) {
        this.status = status;
        this.householdHeadName = householdHeadName;
    }

    public String getStatus() {
        return status;
    }

    public String getHouseholdHeadName() {
        return householdHeadName;
    }
}

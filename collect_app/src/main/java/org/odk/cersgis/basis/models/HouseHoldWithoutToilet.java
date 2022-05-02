package org.odk.cersgis.basis.models;

public class HouseHoldWithoutToilet {
    private String status;
    private String commnunity;
    private String household_headname;

    public HouseHoldWithoutToilet(String status, String commnunity, String household_headname) {
        this.status = status;
        this.commnunity = commnunity;
        this.household_headname = household_headname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCommnunity() {
        return commnunity;
    }

    public void setCommnunity(String commnunity) {
        this.commnunity = commnunity;
    }

    public String getHousehold_headname() {
        return household_headname;
    }

    public void setHousehold_headname(String household_headname) {
        this.household_headname = household_headname;
    }
}

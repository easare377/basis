package org.odk.cersgis.basis.models;

public class Uris {
    private static final String baseUrl = "http://basis2.sanitationghana.org";
    private static final String signInUrl = baseUrl + "/backend/odklogin/?contact=";
    private static final String odkFormListUrl = baseUrl + "/backend/odkformlist";
    private static final String odkFormGetCsvUrl = baseUrl + "/backend/automatecsv";
    private static final String odkFormsUrl = baseUrl +  "/backend/odkforms";
    private static final String odkSyncUrl = baseUrl + "/media/odksync";
    private static final String odfStatusUrl = baseUrl + "/report/mobileodfstatus/?contact=";
    private static final String houseHoldReportUrl = baseUrl + "/report/mobilehouseholdreport/?contact=";
    private static final String triggerUrl = baseUrl + "/odksync/syncodk/?contact=";

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getSignInUrl() {
        return signInUrl;
    }

    public static String getOdkFormListUrl() {
        return odkFormListUrl;
    }

    public static String getOdkFormGetCsvUrl() {
        return odkFormGetCsvUrl;
    }

    public static String getOdkFormsUrl(){
        return odkFormsUrl;
    }

    public static String getOdkSyncUrl() {
        return odkSyncUrl;
    }

    public static String getOdfStatusUrl() {
        return odfStatusUrl;
    }

    public static String getHouseHoldReportUrl() {
        return houseHoldReportUrl;
    }

    public static String getTriggerUrl() {
        return triggerUrl;
    }
}

//http://test.cersgis.org/backend/odkforms/communityinfrustructurehealth/?contact=
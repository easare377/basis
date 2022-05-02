package org.odk.cersgis.basis.models;

public class DynamicData {
    private final String triggerUrl;
    private final Generic[] dynamicCsvs;

    public DynamicData(String triggerUrl, Generic[] dynamicCsvs) {
        this.triggerUrl = triggerUrl;
        this.dynamicCsvs = dynamicCsvs;
    }

    public String getTriggerUrl() {
        return triggerUrl;
    }

    public Generic[] getDynamicCsvs() {
        return dynamicCsvs;
    }
}

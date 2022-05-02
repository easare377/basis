package org.odk.cersgis.basis.models;

public class CsvData2 {
    private final Generic[] genericCsvs;
    private final DynamicData dynamicData;

    public CsvData2(Generic[] genericCsvs, DynamicData dynamicData) {
        this.genericCsvs = genericCsvs;
        this.dynamicData = dynamicData;
    }

    public Generic[] getGenericCsvs() {
        return genericCsvs;
    }

    public DynamicData getDynamicData() {
        return dynamicData;
    }
}

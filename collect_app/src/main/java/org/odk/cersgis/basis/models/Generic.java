package org.odk.cersgis.basis.models;

public class Generic {
    private final String url;
    private final String form_name;
    private final String csvName;

    public Generic(String url, String form_name, String csvName) {
        this.url = url;
        this.form_name = form_name;
        this.csvName = csvName;
    }

    public String getUrl() {
        return url;
    }

    public String getForm_name() {
        return form_name;
    }

    public String getCsvName() {
        return csvName;
    }
}

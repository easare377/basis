package org.odk.cersgis.basis.models;

public class CsvData {
    private String url;
    private String form_name;
    private String csvname;

    public CsvData(String url, String form_name, String csvname) {
        this.url = url;
        this.form_name = form_name;
        this.csvname = csvname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getForm_name() {
        return form_name;
    }

    public void setForm_name(String form_name) {
        this.form_name = form_name;
    }

    public String getCsvname() {
        return csvname;
    }

    public void setCsvname(String csvname) {
        this.csvname = csvname;
    }
}

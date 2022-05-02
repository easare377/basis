package org.odk.cersgis.basis.models;

import android.os.Environment;

public class Paths {
    private static final String basisDir = Environment.getExternalStorageDirectory ().getPath () + "/basisapp";
    private static final String basisFormsDir = basisDir + "/Forms";

    public static String getBasisDir() {
        return basisDir;
    }

    public static String getBasisFormsDir() {
        return basisFormsDir;
    }
}

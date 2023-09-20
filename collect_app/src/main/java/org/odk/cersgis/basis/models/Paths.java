package org.odk.cersgis.basis.models;

import android.os.Build;
import android.os.Environment;

public class Paths {
    private static String basisDir = Environment.getExternalStorageDirectory().getPath () + "/basisapp";
    private static final String basisFormsDir = basisDir + "/Forms";

    public static String getBasisDir() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            basisDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/basisapp";
        }
        return basisDir;
    }

    public static String getBasisFormsDir() {
        return basisFormsDir;
    }
}

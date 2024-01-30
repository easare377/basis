package org.odk.cersgis.basis.models;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class Paths {
    private static Context context;
    private static String basisDir; // = Environment.getExternalStorageDirectory().getPath () + "/basisapp";
    private static String basisFormsDir; //= basisDir + "/Forms";

    public static void init(Context context){
        Paths.context = context;
        basisDir = context.getFilesDir() + "/basisapp";
        basisFormsDir = basisDir + "/forms";
    }

    public static String getBasisDir() {
        return basisDir;
    }

    public static String getBasisFormsDir() {
        return basisFormsDir;
    }
}

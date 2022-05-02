package org.odk.cersgis.basis.utilities;

/**
 * Used to allow tests to understand whether fling gestures have been
 * successfully detected or not
 */

public class FlingRegister {

    private static boolean flingDetected;

    private FlingRegister() {
        
    }

    public static void attemptingFling() {
        flingDetected = false;
    }

    public static void flingDetected() {
        flingDetected = true;
    }

    public static boolean isFlingDetected() {
        return flingDetected;
    }
}

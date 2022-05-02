package org.odk.cersgis.basis;

import android.content.Context;

import org.odk.cersgis.basis.utilities.PlayServicesChecker;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(PlayServicesChecker.class)
public abstract class ShadowPlayServicesUtil {

    @Implementation
    public static boolean isGooglePlayServicesAvailable(Context context) {
        return true;
    }
}


package org.odk.cersgis.basis.activities;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {

    private ActivityUtils() {

    }

    public static <A extends Activity> void startActivityAndCloseAllOthers(Activity activity, Class<A> activityClass) {
        activity.startActivity(new Intent(activity, activityClass));
        activity.overridePendingTransition(0, 0);
        activity.finishAffinity();
    }
}

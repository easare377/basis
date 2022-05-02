package org.odk.cersgis.basis.activities.support;

import android.app.Activity;

import androidx.annotation.NonNull;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.listeners.PermissionListener;
import org.odk.cersgis.basis.utilities.PermissionUtils;

public class AlwaysGrantStoragePermissionsPermissionUtils extends PermissionUtils {

    public AlwaysGrantStoragePermissionsPermissionUtils() {
        super(R.style.Theme_Collect_Dialog_PermissionAlert);
    }

    @Override
    public void requestStoragePermissions(Activity activity, @NonNull PermissionListener action) {
        action.granted();
    }
}

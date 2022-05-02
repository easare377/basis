package org.odk.cersgis.basis.preferences;

import android.os.Bundle;

import org.odk.cersgis.basis.R;

public class CustomServerPathsFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.custom_server_paths_preferences, rootKey);
    }
}

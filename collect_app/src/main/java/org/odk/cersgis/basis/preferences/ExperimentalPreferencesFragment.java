package org.odk.cersgis.basis.preferences;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.activities.MainMenuActivity;
import org.odk.cersgis.basis.injection.DaggerUtils;

import static org.odk.cersgis.basis.activities.ActivityUtils.startActivityAndCloseAllOthers;

public class ExperimentalPreferencesFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.experimental_preferences, rootKey);

        findPreference(GeneralKeys.KEY_MAGENTA_THEME).setOnPreferenceChangeListener((preference, newValue) -> {
            startActivityAndCloseAllOthers(requireActivity(), MainMenuActivity.class);
            return true;
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DaggerUtils.getComponent(context).inject(this);
    }
}

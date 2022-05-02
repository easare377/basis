package org.odk.cersgis.basis.formentry.saving;

import android.content.Context;

import androidx.annotation.NonNull;

import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.fragments.dialogs.ProgressDialogFragment;

public class SaveAnswerFileProgressDialogFragment extends ProgressDialogFragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setMessage(getString(R.string.saving_file));
    }
}

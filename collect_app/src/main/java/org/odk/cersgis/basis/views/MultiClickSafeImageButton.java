package org.odk.cersgis.basis.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;

import org.odk.cersgis.basis.utilities.MultiClickGuard;

public class MultiClickSafeImageButton extends AppCompatImageButton {
    public MultiClickSafeImageButton(@NonNull Context context) {
        super(context);
    }

    public MultiClickSafeImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        return MultiClickGuard.allowClick(getClass().getName()) && super.performClick();
    }
}

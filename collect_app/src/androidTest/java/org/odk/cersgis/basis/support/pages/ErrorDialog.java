package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

public class ErrorDialog extends OkDialog {
    ErrorDialog(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public ErrorDialog assertOnPage() {
        super.assertOnPage();
        assertText(R.string.error_occured);
        return this;
    }
}

package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

public class ExitFormDialog extends Page<ExitFormDialog> {

    private final String formName;

    public ExitFormDialog(String formName, ActivityTestRule rule) {
        super(rule);
        this.formName = formName;
    }

    @Override
    public ExitFormDialog assertOnPage() {
        String title = getTranslatedString(R.string.exit) + " " + formName;
        assertText(title);
        return this;
    }
}

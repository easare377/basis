package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

public class ExperimentalPage extends Page<ExperimentalPage> {

    ExperimentalPage(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public ExperimentalPage assertOnPage() {
        assertToolbarTitle(getTranslatedString(R.string.experimental));
        return this;
    }
}

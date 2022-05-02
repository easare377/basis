package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

public class ViewSentFormPage extends Page<ViewSentFormPage> {

    ViewSentFormPage(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public ViewSentFormPage assertOnPage() {
        assertToolbarTitle(R.string.view_sent_forms);
        return this;
    }
}

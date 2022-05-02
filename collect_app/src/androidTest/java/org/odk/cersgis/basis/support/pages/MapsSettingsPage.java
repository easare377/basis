package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

public class MapsSettingsPage extends Page<MapsSettingsPage> {

    MapsSettingsPage(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public MapsSettingsPage assertOnPage() {
        assertText(R.string.maps);
        return this;
    }
}

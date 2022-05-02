package org.odk.cersgis.basis.support;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.activities.MainMenuActivity;
import org.odk.cersgis.basis.support.pages.MainMenuPage;

public class CollectTestRule extends ActivityTestRule<MainMenuActivity> {

    public CollectTestRule() {
        super(MainMenuActivity.class);
    }

    public MainMenuPage mainMenu() {
        return new MainMenuPage(this).assertOnPage();
    }
}

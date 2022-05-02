package org.odk.cersgis.basis.regression;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.support.CollectTestRule;
import org.odk.cersgis.basis.support.ResetStateRule;
import org.odk.cersgis.basis.support.pages.MainMenuPage;

//Issue NODK-241
@RunWith(AndroidJUnit4.class)
public class UserSettingsTest {

    public CollectTestRule rule = new CollectTestRule();

    @Rule
    public RuleChain ruleChain = RuleChain
            .outerRule(new ResetStateRule())
            .around(rule);

    @Test
    public void typeOption_ShouldNotBeVisible() {
        //TestCase1
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickAdminSettings()
                .openUserSettings()
                .assertTextDoesNotExist("Type")
                .assertTextDoesNotExist("Submission transport")
                .assertText(R.string.server_settings_title);
    }
}

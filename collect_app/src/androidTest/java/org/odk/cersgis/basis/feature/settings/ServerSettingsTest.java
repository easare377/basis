package org.odk.cersgis.basis.feature.settings;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.activities.MainMenuActivity;
import org.odk.cersgis.basis.gdrive.sheets.DriveHelper;
import org.odk.cersgis.basis.support.TestDependencies;
import org.odk.cersgis.basis.support.TestRuleChain;
import org.odk.cersgis.basis.support.pages.GeneralSettingsPage;
import org.odk.cersgis.basis.support.pages.MainMenuPage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class ServerSettingsTest {

    private final TestDependencies testDependencies = new TestDependencies();

    public IntentsTestRule<MainMenuActivity> rule = new IntentsTestRule<>(MainMenuActivity.class);

    @Rule
    public RuleChain copyFormChain = TestRuleChain.chain(testDependencies)
            .around(rule);

    @Test
    public void whenUsingODKServer_canAddCredentialsForServer() {
        testDependencies.server.setCredentials("Joe", "netsky");
        testDependencies.server.addForm("One Question", "one-question", "1", "one-question.xml");

        new MainMenuPage(rule).assertOnPage()
                .clickOnMenu()
                .clickGeneralSettings()
                .clickServerSettings()
                .clickOnURL()
                .inputText(testDependencies.server.getURL())
                .clickOKOnDialog()
                .assertText(testDependencies.server.getURL())
                .clickServerUsername()
                .inputText("Joe")
                .clickOKOnDialog()
                .assertText("Joe")
                .clickServerPassword()
                .inputText("netsky")
                .clickOKOnDialog()
                .assertText("********")
                .pressBack(new GeneralSettingsPage(rule))
                .pressBack(new MainMenuPage(rule))

                .clickGetBlankForm()
                .clickGetSelected()
                .assertText("One Question (Version:: 1 ID: one-question) - Success")
                .clickOK(new MainMenuPage(rule));
    }

    /**
     * This test could definitely be extended to cover form download/submit with the creation
     * of a stub
     * {@link DriveHelper} and
     * {@link org.odk.cersgis.basis.gdrive.GoogleAccountsManager}
     */
    @Test
    public void selectingGoogleAccount_showsGoogleAccountSettings() {
        new MainMenuPage(rule).assertOnPage()
                .clickOnMenu()
                .clickGeneralSettings()
                .clickServerSettings()
                .clickOnServerType()
                .clickOnString(R.string.server_platform_google_sheets)
                .assertText(R.string.selected_google_account_text)
                .assertText(R.string.google_sheets_url);
    }

    @Test
    public void selectingGoogleAccount_disablesAutomaticUpdates() {
        MainMenuPage mainMenu = new MainMenuPage(rule).assertOnPage()
                .enablePreviouslyDownloadedOnlyUpdates();
        assertThat(testDependencies.scheduler.getDeferredTasks().size(), is(1));

        testDependencies.googleAccountPicker.setDeviceAccount("steph@curry.basket");
        mainMenu.setGoogleAccount("steph@curry.basket");
        assertThat(testDependencies.scheduler.getDeferredTasks().size(), is(0));
    }
}

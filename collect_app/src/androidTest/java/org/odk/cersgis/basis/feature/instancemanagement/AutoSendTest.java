package org.odk.cersgis.basis.feature.instancemanagement;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.support.CollectTestRule;
import org.odk.cersgis.basis.support.CopyFormRule;
import org.odk.cersgis.basis.support.NotificationDrawerRule;
import org.odk.cersgis.basis.support.TestDependencies;
import org.odk.cersgis.basis.support.TestRuleChain;
import org.odk.cersgis.basis.support.pages.MainMenuPage;

@RunWith(AndroidJUnit4.class)
public class AutoSendTest {

    public CollectTestRule rule = new CollectTestRule();

    final TestDependencies testDependencies = new TestDependencies();
    final NotificationDrawerRule notificationDrawerRule = new NotificationDrawerRule();

    @Rule
    public RuleChain copyFormChain = TestRuleChain.chain(testDependencies)
            .around(notificationDrawerRule)
            .around(new CopyFormRule("one-question.xml"))
            .around(rule);

    @Test
    public void whenAutoSendEnabled_fillingAndFinalizingForm_sendsFormAndNotifiesUser() {
        MainMenuPage mainMenuPage = rule.mainMenu()
                .setServer(testDependencies.server.getURL())
                .enableAutoSend()
                .clickFillBlankForm()
                .clickOnForm("One Question")
                .inputText("The greatest answer")
                .swipeToEndScreen()
                .clickSaveAndExit();

        testDependencies.scheduler.runDeferredTasks();

        mainMenuPage
                .clickViewSentForm(1)
                .assertText("One Question");

        notificationDrawerRule.open()
                .assertAndDismissNotification("ODK Collect", "ODK auto-send results", "Success");
    }
}

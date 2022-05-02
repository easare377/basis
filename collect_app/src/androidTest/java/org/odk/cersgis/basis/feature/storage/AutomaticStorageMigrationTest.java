package org.odk.cersgis.basis.feature.storage;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.support.CollectTestRule;
import org.odk.cersgis.basis.support.TestDependencies;
import org.odk.cersgis.basis.support.TestRuleChain;
import org.odk.cersgis.basis.support.pages.MainMenuPage;

@RunWith(AndroidJUnit4.class)
public class AutomaticStorageMigrationTest {

    public CollectTestRule rule = new CollectTestRule();

    @Rule
    public RuleChain copyFormChain = TestRuleChain.chain(false, new TestDependencies())
            .around(rule);

    @Test
    public void when_storageMigrationNotPerformed_shouldBePerformedAutomatically() {
        new MainMenuPage(rule)
                .assertStorageMigrationCompletedBannerIsDisplayed();
    }
}

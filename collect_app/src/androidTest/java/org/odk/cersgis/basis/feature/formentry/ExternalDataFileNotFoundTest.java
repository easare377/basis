package org.odk.cersgis.basis.feature.formentry;

import android.Manifest;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.activities.FormEntryActivity;
import org.odk.cersgis.basis.storage.StoragePathProvider;
import org.odk.cersgis.basis.storage.StorageSubdirectory;
import org.odk.cersgis.basis.support.pages.FormEntryPage;
import org.odk.cersgis.basis.support.CopyFormRule;
import org.odk.cersgis.basis.support.ResetStateRule;
import org.odk.cersgis.basis.support.FormLoadingUtils;

public class ExternalDataFileNotFoundTest {
    private static final String EXTERNAL_DATA_QUESTIONS = "external_data_questions.xml";

    @Rule
    public IntentsTestRule<FormEntryActivity> activityTestRule = FormLoadingUtils.getFormActivityTestRuleFor(EXTERNAL_DATA_QUESTIONS);

    @Rule
    public RuleChain copyFormChain = RuleChain
            .outerRule(GrantPermissionRule.grant(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
            .around(new ResetStateRule())
            .around(new CopyFormRule(EXTERNAL_DATA_QUESTIONS, true));

    @Test
    public void questionsThatUseExternalFiles_ShouldDisplayFriendlyMessageWhenFilesAreMissing() {
        String formsDirPath = new StoragePathProvider().getDirPath(StorageSubdirectory.FORMS);

        new FormEntryPage("externalDataQuestions", activityTestRule)
                .assertText(activityTestRule.getActivity().getString(R.string.file_missing, formsDirPath + "/external_data_questions-media/fruits.csv"))
                .swipeToNextQuestion()
                .assertText(activityTestRule.getActivity().getString(R.string.file_missing, formsDirPath + "/external_data_questions-media/itemsets.csv"));
    }
}

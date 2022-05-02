package org.odk.cersgis.basis.feature.externalintents;

import androidx.test.filters.Suppress;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.odk.cersgis.basis.activities.FillBlankFormActivity;

import java.io.IOException;

import static org.odk.cersgis.basis.feature.externalintents.ExportedActivitiesUtils.testDirectories;

@Suppress
// Frequent failures: https://github.com/getodk/collect/issues/796
public class FormChooserListTest {

    @Rule
    public ActivityTestRule<FillBlankFormActivity> formChooserListRule =
            new ExportedActivityTestRule<>(FillBlankFormActivity.class);

    @Test
    public void formChooserListMakesDirsTest() throws IOException {
        testDirectories();
    }

}

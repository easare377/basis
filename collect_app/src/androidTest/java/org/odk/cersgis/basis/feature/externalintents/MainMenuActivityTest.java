package org.odk.cersgis.basis.feature.externalintents;

import androidx.test.filters.Suppress;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.odk.cersgis.basis.activities.MainMenuActivity;

import java.io.IOException;

import static org.odk.cersgis.basis.feature.externalintents.ExportedActivitiesUtils.testDirectories;

@Suppress
// Frequent failures: https://github.com/getodk/collect/issues/796
public class MainMenuActivityTest {

    @Rule
    public ActivityTestRule<MainMenuActivity> mainMenuActivityRule =
            new ExportedActivityTestRule<>(MainMenuActivity.class);

    @Test
    public void mainMenuActivityMakesDirsTest() throws IOException {
        testDirectories();
    }

}

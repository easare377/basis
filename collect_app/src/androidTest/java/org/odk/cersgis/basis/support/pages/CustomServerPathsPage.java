package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class CustomServerPathsPage extends Page<CustomServerPathsPage> {

    public CustomServerPathsPage(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public CustomServerPathsPage assertOnPage() {
        assertToolbarTitle(getTranslatedString(R.string.custom_server_paths));
        return this;
    }

    public CustomServerPathsPage clickFormListPath() {
        onView(withText(getTranslatedString(R.string.formlist_url))).perform(click());
        return this;
    }

    public CustomServerPathsPage clickSubmissionPath() {
        onView(withText(getTranslatedString(R.string.submission_url))).perform(click());
        return this;
    }
}

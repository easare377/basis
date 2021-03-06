package org.odk.cersgis.basis.support.pages;

import androidx.test.rule.ActivityTestRule;

import org.odk.cersgis.basis.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class DeleteSavedFormPage extends Page<DeleteSavedFormPage> {

    public DeleteSavedFormPage(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public DeleteSavedFormPage assertOnPage() {
        assertToolbarTitle(getTranslatedString(R.string.manage_files));
        return this;
    }

    public DeleteSavedFormPage clickBlankForms() {
        clickOnString(R.string.forms);
        return this;
    }

    public DeleteSavedFormPage clickForm(String formName) {
        onView(withText(formName)).perform(scrollTo(), click());
        return this;
    }

    public DeleteSelectedDialog clickDeleteSelected(int numberSelected) {
        clickOnString(R.string.delete_file);
        return new DeleteSelectedDialog(numberSelected, this, rule).assertOnPage();
    }
}

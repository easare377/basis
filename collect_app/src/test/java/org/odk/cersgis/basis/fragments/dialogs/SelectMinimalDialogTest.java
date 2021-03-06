package org.odk.cersgis.basis.fragments.dialogs;

import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.test.core.app.ApplicationProvider;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.form.api.FormEntryPrompt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.support.MockFormEntryPromptBuilder;
import org.odk.cersgis.basis.support.RobolectricHelpers;
import org.odk.cersgis.basis.support.TestScreenContextActivity;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class SelectMinimalDialogTest {

    protected FragmentManager fragmentManager;
    protected SelectMinimalDialog dialogFragment;
    protected FormEntryPrompt formEntryPrompt;

    @Before
    public void setup() {
        FragmentActivity activity = RobolectricHelpers.createThemedActivity(TestScreenContextActivity.class);
        fragmentManager = activity.getSupportFragmentManager();
    }

    @Test
    public void whenClickBackButton_shouldDialogBeClosed() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "autocomplete");
        dialogFragment = new SelectOneMinimalDialog(null, false, true, ApplicationProvider.getApplicationContext(), items, formEntryPrompt, null, 0, 1, false);
        SelectMinimalDialog.SelectMinimalDialogListener listener = mock(SelectMinimalDialog.SelectMinimalDialogListener.class);
        dialogFragment.setListener(listener);

        dialogFragment.show(fragmentManager, "TAG");
        assertThat(isDialogVisible(), is(true));
        dialogFragment.onBackPressed();
        assertThat(isDialogVisible(), is(false));
    }

    @Test
    public void whenClickBackArrowButton_shouldDialogBeClosed() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "autocomplete");
        dialogFragment = new SelectOneMinimalDialog(null, false, true, ApplicationProvider.getApplicationContext(), items, formEntryPrompt, null, 0, 1, false);
        SelectMinimalDialog.SelectMinimalDialogListener listener = mock(SelectMinimalDialog.SelectMinimalDialogListener.class);
        dialogFragment.setListener(listener);

        dialogFragment.show(fragmentManager, "TAG");
        assertThat(isDialogVisible(), is(true));
        dialogFragment.getToolbar().getChildAt(0).performClick();
        assertThat(isDialogVisible(), is(false));
    }

    @Test
    public void whenAutoCompleteAppearanceUsed_shouldSearchBarBeVisible() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "autocomplete");
        dialogFragment = new SelectOneMinimalDialog(null, false, true, ApplicationProvider.getApplicationContext(), items, formEntryPrompt, null, 0, 1, false);
        SelectMinimalDialog.SelectMinimalDialogListener listener = mock(SelectMinimalDialog.SelectMinimalDialogListener.class);
        dialogFragment.setListener(listener);

        dialogFragment.show(fragmentManager, "TAG");
        assertThat(dialogFragment.getToolbar().findViewById(R.id.menu_filter).getVisibility(), equalTo(View.VISIBLE));
    }

    protected boolean isDialogVisible() {
        return dialogFragment != null
                && dialogFragment.getDialog() != null
                && dialogFragment.getDialog().isShowing()
                && !dialogFragment.isRemoving();
    }

    protected List<SelectChoice> getTestChoices() {
        return asList(
                new SelectChoice("AAA", "AAA"),
                new SelectChoice("BBB", "BBB")
        );
    }

    protected void setUpFormEntryPrompt(List<SelectChoice> items, String appearance) {
        formEntryPrompt = new MockFormEntryPromptBuilder()
                .withSelectChoices(items)
                .withAppearance(appearance)
                .build();
    }
}

package org.odk.cersgis.basis.widgets.base;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.junit.Test;
import org.mockito.Mock;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.application.Collect;
import org.odk.cersgis.basis.javarosawrapper.FormController;
import org.odk.cersgis.basis.listeners.WidgetValueChangedListener;
import org.odk.cersgis.basis.support.RobolectricHelpers;
import org.odk.cersgis.basis.support.TestScreenContextActivity;
import org.odk.cersgis.basis.widgets.QuestionWidget;
import org.odk.cersgis.basis.widgets.interfaces.Widget;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.mockValueChangedListener;

public abstract class QuestionWidgetTest<W extends Widget, A extends IAnswerData>
        extends WidgetTest {

    protected Random random = new Random();
    protected Activity activity = RobolectricHelpers.buildThemedActivity(TestScreenContextActivity.class).get();

    private W widget;
    private W actualWidget;

    @Mock
    public FormIndex formIndex;

    @Mock
    public FormController formController;

    @NonNull
    public abstract W createWidget();

    @NonNull
    public abstract A getNextAnswer();

    public A getInitialAnswer() {
        return getNextAnswer();
    }

    /**
     * @return Real {@link Widget} object if present otherwise creates one
     * <p>
     * This should be used for mutating the {@link org.odk.cersgis.basis.widgets.QuestionWidget}
     */
    public W getWidget() {
        if (actualWidget == null) {
            actualWidget = createWidget();
        }

        return actualWidget;
    }

    /**
     * @return {@link org.mockito.Spy} of the {@link #actualWidget}
     * <p>
     * This should be unless we want to mutate {@link org.odk.cersgis.basis.widgets.QuestionWidget}
     * This is because a spy is not the real object and changing it won't have any effect on the real object
     */
    public W getSpyWidget() {
        if (widget == null) {
            widget = spy(getWidget());
        }

        return widget;
    }

    public void resetWidget() {
        actualWidget = null;
        widget = null;
    }

    public void setUp() throws Exception {
        super.setUp();

        when(formEntryPrompt.getIndex()).thenReturn(formIndex);

        Collect.getInstance().setFormController(formController);

        widget = null;
    }

    @Test
    public void getAnswerShouldReturnNullIfPromptDoesNotHaveExistingAnswer() {
        W widget = getSpyWidget();
        assertNull(widget.getAnswer());
    }

    @Test
    public void getAnswerShouldReturnExistingAnswerIfPromptHasExistingAnswer() {
        A answer = getInitialAnswer();
        if (answer instanceof StringData) {
            when(formEntryPrompt.getAnswerText()).thenReturn((String) answer.getValue());
        } else {
            when(formEntryPrompt.getAnswerValue()).thenReturn(answer);
        }

        W widget = getSpyWidget();
        IAnswerData newAnswer = widget.getAnswer();

        assertNotNull(newAnswer);
        assertEquals(newAnswer.getDisplayText(), answer.getDisplayText());
    }

    @Test
    public void callingClearShouldRemoveTheExistingAnswer() {
        getAnswerShouldReturnExistingAnswerIfPromptHasExistingAnswer();
        widget.clearAnswer();

        assertNull(widget.getAnswer());
    }

    @Test
    public void callingClearShouldCallValueChangeListeners() {
        QuestionWidget widget = (QuestionWidget) getSpyWidget();
        WidgetValueChangedListener valueChangedListener = mockValueChangedListener(widget);
        widget.clearAnswer();
        verify(valueChangedListener).widgetValueChanged(widget);
    }

    @Test
    public void whenReadOnlyQuestionHasNoAnswer_answerContainerShouldNotBeDisplayed() {
        when(formEntryPrompt.isReadOnly()).thenReturn(true);
        QuestionWidget widget = (QuestionWidget) getWidget();
        assertThat(widget.findViewById(R.id.answer_container).getVisibility(), is(View.GONE));
        assertThat(widget.findViewById(R.id.space_box).getVisibility(), is(View.VISIBLE));
    }

    @Test
    public void whenReadOnlyQuestionHasAnswer_answerContainerShouldBeDisplayed() {
        when(formEntryPrompt.isReadOnly()).thenReturn(true);
        when(formEntryPrompt.getAnswerValue()).thenReturn(getInitialAnswer());
        QuestionWidget widget = (QuestionWidget) getWidget();
        assertThat(widget.findViewById(R.id.answer_container).getVisibility(), is(View.VISIBLE));
        assertThat(widget.findViewById(R.id.space_box).getVisibility(), is(View.GONE));
    }
}
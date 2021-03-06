package org.odk.cersgis.basis.widgets;

import android.view.View;

import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.data.DateData;
import org.javarosa.form.api.FormEntryPrompt;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.formentry.questions.QuestionDetails;
import org.odk.cersgis.basis.listeners.WidgetValueChangedListener;
import org.odk.cersgis.basis.logic.DatePickerDetails;
import org.odk.cersgis.basis.support.TestScreenContextActivity;
import org.odk.cersgis.basis.utilities.DateTimeUtils;
import org.odk.cersgis.basis.widgets.utilities.DateTimeWidgetUtils;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.mockValueChangedListener;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.promptWithQuestionDefAndAnswer;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.promptWithReadOnlyAndQuestionDef;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.widgetTestActivity;

@RunWith(RobolectricTestRunner.class)
public class DateWidgetTest {
    private TestScreenContextActivity widgetActivity;
    private DateTimeWidgetUtils widgetUtils;
    private View.OnLongClickListener onLongClickListener;

    private QuestionDef questionDef;
    private LocalDateTime dateAnswer;

    @Before
    public void setUp() {
        widgetActivity = widgetTestActivity();

        questionDef = mock(QuestionDef.class);
        onLongClickListener = mock(View.OnLongClickListener.class);
        widgetUtils = mock(DateTimeWidgetUtils.class);

        dateAnswer = DateTimeUtils.getSelectedDate(new LocalDateTime().withDate(2010, 5, 12), LocalDateTime.now());
    }

    @Test
    public void usingReadOnlyOption_doesNotShowButton() {
        DateWidget widget = createWidget(promptWithReadOnlyAndQuestionDef(questionDef));
        assertEquals(widget.binding.dateButton.getVisibility(), View.GONE);
    }

    @Test
    public void whenPromptIsNotReadOnly_buttonShowsCorrectText() {
        DateWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, null));
        assertEquals(widget.binding.dateButton.getText(), widget.getContext().getString(R.string.select_date));
    }

    @Test
    public void getAnswer_whenPromptDoesNotHaveAnswer_returnsNull() {
        assertThat(createWidget(promptWithQuestionDefAndAnswer(questionDef, null)).getAnswer(), nullValue());
    }

    @Test
    public void getAnswer_whenPromptHasAnswer_returnsDate() {
        DateWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, new DateData(dateAnswer.toDate())));
        assertEquals(widget.getAnswer().getDisplayText(), new DateData(dateAnswer.toDate()).getDisplayText());
    }

    @Test
    public void whenPromptDoesNotHaveAnswer_answerTextViewShowsNoDateSelected() {
        DateWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, null));
        assertEquals(widget.binding.dateAnswerText.getText(), widget.getContext().getString(R.string.no_date_selected));
    }

    @Test
    public void whenPromptHasAnswer_answerTextViewShowsCorrectDate() {
        FormEntryPrompt prompt = promptWithQuestionDefAndAnswer(questionDef, new DateData(dateAnswer.toDate()));
        DatePickerDetails datePickerDetails = DateTimeWidgetUtils.getDatePickerDetails(prompt.getQuestion().getAppearanceAttr());
        DateWidget widget = createWidget(prompt);

        assertEquals(widget.binding.dateAnswerText.getText(),
                DateTimeWidgetUtils.getDateTimeLabel(dateAnswer.toDate(), datePickerDetails, false, widget.getContext()));
    }

    @Test
    public void clickingButton_callsDisplayDatePickerDialogWithCurrentDate_whenPromptDoesNotHaveAnswer() {
        FormEntryPrompt prompt = promptWithQuestionDefAndAnswer(questionDef, null);
        DateWidget widget = createWidget(prompt);
        widget.binding.dateButton.performClick();

        verify(widgetUtils).showDatePickerDialog(widgetActivity, DateTimeWidgetUtils.getDatePickerDetails(
                prompt.getQuestion().getAppearanceAttr()), DateTimeUtils.getCurrentDateTime());
    }

    @Test
    public void clickingButton_callsDisplayDatePickerDialogWithSelectedDate_whenPromptHasAnswer() {
        FormEntryPrompt prompt = promptWithQuestionDefAndAnswer(questionDef, new DateData(dateAnswer.toDate()));
        DateWidget widget = createWidget(prompt);
        widget.binding.dateButton.performClick();

        verify(widgetUtils).showDatePickerDialog(widgetActivity, DateTimeWidgetUtils.getDatePickerDetails(
                prompt.getQuestion().getAppearanceAttr()), dateAnswer);
    }

    @Test
    public void clearAnswer_clearsWidgetAnswer() {
        DateWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, null));
        widget.clearAnswer();
        assertEquals(widget.binding.dateAnswerText.getText(), widget.getContext().getString(R.string.no_date_selected));
    }

    @Test
    public void clearAnswer_callsValueChangeListener() {
        DateWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, new DateData(dateAnswer.toDate())));
        WidgetValueChangedListener valueChangedListener = mockValueChangedListener(widget);
        widget.clearAnswer();

        verify(valueChangedListener).widgetValueChanged(widget);
    }

    @Test
    public void clickingButtonAndAnswerTextViewForLong_callsLongClickListener() {
        DateWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, new DateData(dateAnswer.toDate())));
        widget.setOnLongClickListener(onLongClickListener);
        widget.binding.dateButton.performLongClick();
        widget.binding.dateAnswerText.performLongClick();

        verify(onLongClickListener).onLongClick(widget.binding.dateButton);
        verify(onLongClickListener).onLongClick(widget.binding.dateAnswerText);
    }

    @Test
    public void setData_updatesWidgetAnswer() {
        DateWidget widget = createWidget(promptWithQuestionDefAndAnswer(questionDef, null));
        widget.setData(dateAnswer);
        assertThat(widget.getAnswer().getDisplayText(), equalTo(new DateData(dateAnswer.toDate()).getDisplayText()));
    }

    @Test
    public void setData_updatesValueDisplayedInAnswerTextView() {
        FormEntryPrompt prompt = promptWithQuestionDefAndAnswer(questionDef, null);
        DatePickerDetails datePickerDetails = DateTimeWidgetUtils.getDatePickerDetails(prompt.getQuestion().getAppearanceAttr());
        DateWidget widget = createWidget(prompt);
        widget.setData(dateAnswer);

        assertEquals(widget.binding.dateAnswerText.getText(),
                DateTimeWidgetUtils.getDateTimeLabel(dateAnswer.toDate(), datePickerDetails, false, widget.getContext()));
    }

    private DateWidget createWidget(FormEntryPrompt prompt) {
        return new DateWidget(widgetActivity, new QuestionDetails(prompt, "formAnalyticsID"), widgetUtils);
    }
}

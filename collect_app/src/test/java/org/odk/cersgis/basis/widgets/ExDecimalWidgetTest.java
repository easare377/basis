package org.odk.cersgis.basis.widgets;

import androidx.annotation.NonNull;

import org.javarosa.core.model.data.DecimalData;
import org.odk.cersgis.basis.formentry.questions.QuestionDetails;
import org.javarosa.core.model.data.IAnswerData;
import org.junit.Test;
import org.mockito.Mock;
import org.odk.cersgis.basis.widgets.base.GeneralExStringWidgetTest;
import org.odk.cersgis.basis.widgets.support.FakeWaitingForDataRegistry;

import java.text.NumberFormat;
import java.util.Locale;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.odk.cersgis.basis.utilities.WidgetAppearanceUtils.THOUSANDS_SEP;

/**
 * @author James Knight
 */

public class ExDecimalWidgetTest extends GeneralExStringWidgetTest<ExDecimalWidget, DecimalData> {

    @Mock
    IAnswerData answerData;

    @NonNull
    @Override
    public ExDecimalWidget createWidget() {
        return new ExDecimalWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), new FakeWaitingForDataRegistry());
    }

    @NonNull
    @Override
    public DecimalData getNextAnswer() {
        // Need to keep under 15 digits:
        double d = random.nextDouble();
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(13); // The Widget internally truncatest this further.
        nf.setMaximumIntegerDigits(13);
        nf.setGroupingUsed(false);

        String formattedValue = nf.format(d);
        return new DecimalData(Double.parseDouble(formattedValue));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(formEntryPrompt.getAppearanceHint()).thenReturn("");
    }

    @Test
    // This should never be possible because the EditText has a limit on it
    public void digitsAboveLimitOfFifteenShouldBeTruncatedFromRight() {
        Double eighteenDigitDouble = 9999999999999994.;
        String fifteenDigitString = "999999999999994";
        assertSame(15, fifteenDigitString.length());

        when(formEntryPrompt.getAnswerValue()).thenReturn(answerData);
        when(answerData.getValue()).thenReturn(eighteenDigitDouble);

        ExDecimalWidget exDecimalWidget = new ExDecimalWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), new FakeWaitingForDataRegistry());

        assertThat(exDecimalWidget.getAnswerText(), is(equalTo(fifteenDigitString)));

        exDecimalWidget = new ExDecimalWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), new FakeWaitingForDataRegistry());

        assertThat(exDecimalWidget.getAnswerText(), is(equalTo(fifteenDigitString)));
    }

    @Test
    public void separatorsShouldBeAddedWhenEnabled() {
        when(formEntryPrompt.getAppearanceHint()).thenReturn(THOUSANDS_SEP);
        getWidget().answerText.setText("123456789.54");
        assertEquals("123,456,789.54", getWidget().answerText.getText().toString());
    }
}
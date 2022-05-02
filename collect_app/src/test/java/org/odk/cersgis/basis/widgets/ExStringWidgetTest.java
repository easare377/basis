package org.odk.cersgis.basis.widgets;

import androidx.annotation.NonNull;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;
import org.odk.cersgis.basis.formentry.questions.QuestionDetails;
import org.odk.cersgis.basis.widgets.base.GeneralExStringWidgetTest;
import org.odk.cersgis.basis.widgets.support.FakeWaitingForDataRegistry;

import static org.mockito.Mockito.when;

/**
 * @author James Knight
 */

public class ExStringWidgetTest extends GeneralExStringWidgetTest<ExStringWidget, StringData> {

    @NonNull
    @Override
    public ExStringWidget createWidget() {
        return new ExStringWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), new FakeWaitingForDataRegistry());
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(RandomString.make());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(formEntryPrompt.getAppearanceHint()).thenReturn("");
    }
}

package org.odk.cersgis.basis.widgets;

import androidx.annotation.NonNull;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;
import org.odk.cersgis.basis.formentry.questions.QuestionDetails;
import org.odk.cersgis.basis.widgets.base.GeneralStringWidgetTest;

/**
 * @author James Knight
 */
public class StringWidgetTest extends GeneralStringWidgetTest<StringWidget, StringData> {

    @NonNull
    @Override
    public StringWidget createWidget() {
        return new StringWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID", readOnlyOverride));
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(RandomString.make());
    }
}

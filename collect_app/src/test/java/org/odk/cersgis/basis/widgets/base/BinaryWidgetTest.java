package org.odk.cersgis.basis.widgets.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.karumi.dexter.DexterActivity;

import org.javarosa.core.model.data.IAnswerData;
import org.junit.Test;
import org.odk.cersgis.basis.fakes.FakePermissionUtils;
import org.odk.cersgis.basis.widgets.QuestionWidget;
import org.odk.cersgis.basis.widgets.interfaces.WidgetDataReceiver;
import org.odk.cersgis.basis.widgets.interfaces.Widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author James Knight
 */
public abstract class BinaryWidgetTest<W extends Widget, A extends IAnswerData>
        extends QuestionWidgetTest<W, A> {

    private final FakePermissionUtils permissionUtils;

    public BinaryWidgetTest() {
        permissionUtils = new FakePermissionUtils();
    }

    protected void stubAllRuntimePermissionsGranted(boolean isGranted) {
        permissionUtils.setPermissionGranted(isGranted);
        ((QuestionWidget) getWidget()).setPermissionUtils(permissionUtils);
    }

    protected Intent getIntentLaunchedByClick(int buttonId) {
        ((QuestionWidget) getSpyWidget()).findViewById(buttonId).performClick();
        return shadowOf(activity).getNextStartedActivity();
    }

    protected void assertComponentEquals(String pkg, String cls, Intent intent) {
        assertEquals(new ComponentName(pkg, cls), intent.getComponent());
    }

    protected void assertComponentEquals(Context context, Class<?> cls, Intent intent) {
        assertEquals(new ComponentName(context, cls), intent.getComponent());
    }

    protected void assertActionEquals(String expectedAction, Intent intent) {
        assertEquals(expectedAction, intent.getAction());
    }

    protected void assertTypeEquals(String type, Intent intent) {
        assertEquals(type, intent.getType());
    }

    protected void assertExtraEquals(String key, Object value, Intent intent) {
        assertEquals(intent.getExtras().get(key), value);
    }

    // After upgrading gradle and some dependencies if an intent can't be started because of not granted
    // permissions the null value or DexterActivity is returned. It works randomly and depends on the
    // order of tests but both results are ok.
    protected void assertIntentNotStarted(Context context, Intent intent) {
        assertTrue(intent == null || new ComponentName(context, DexterActivity.class).equals(intent.getComponent()));
    }

    public abstract Object createBinaryData(A answerData);

    @Test
    public void getAnswerShouldReturnCorrectAnswerAfterBeingSet() {
        W widget = getSpyWidget();
        assertNull(widget.getAnswer());

        A answer = getNextAnswer();
        Object binaryData = createBinaryData(answer);

        ((WidgetDataReceiver) widget).setData(binaryData);

        IAnswerData answerData = widget.getAnswer();

        assertNotNull(answerData);
        assertEquals(answerData.getDisplayText(), answer.getDisplayText());
    }

    @Test
    public void settingANewAnswerShouldRemoveTheOldAnswer() {
        A answer = getInitialAnswer();
        when(formEntryPrompt.getAnswerText()).thenReturn(answer.getDisplayText());

        W widget = getSpyWidget();

        A newAnswer = getNextAnswer();
        Object binaryData = createBinaryData(newAnswer);

        ((WidgetDataReceiver) widget).setData(binaryData);

        IAnswerData answerData = widget.getAnswer();

        assertNotNull(answerData);
        assertEquals(answerData.getDisplayText(), newAnswer.getDisplayText());
    }
}
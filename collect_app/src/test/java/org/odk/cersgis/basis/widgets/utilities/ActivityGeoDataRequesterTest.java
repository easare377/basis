package org.odk.cersgis.basis.widgets.utilities;

import android.content.ComponentName;
import android.content.Intent;

import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.data.GeoPointData;
import org.javarosa.form.api.FormEntryPrompt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.activities.GeoPointActivity;
import org.odk.cersgis.basis.activities.GeoPointMapActivity;
import org.odk.cersgis.basis.activities.GeoPolyActivity;
import org.odk.cersgis.basis.fakes.FakePermissionUtils;
import org.odk.cersgis.basis.support.TestScreenContextActivity;
import org.odk.cersgis.basis.utilities.WidgetAppearanceUtils;
import org.odk.cersgis.basis.widgets.support.FakeWaitingForDataRegistry;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.odk.cersgis.basis.utilities.ApplicationConstants.RequestCodes.GEOSHAPE_CAPTURE;
import static org.odk.cersgis.basis.utilities.ApplicationConstants.RequestCodes.GEOTRACE_CAPTURE;
import static org.odk.cersgis.basis.utilities.ApplicationConstants.RequestCodes.LOCATION_CAPTURE;
import static org.odk.cersgis.basis.widgets.support.GeoWidgetHelpers.assertGeoPointBundleArgumentEquals;
import static org.odk.cersgis.basis.widgets.support.GeoWidgetHelpers.assertGeoPolyBundleArgumentEquals;
import static org.odk.cersgis.basis.widgets.support.GeoWidgetHelpers.getRandomDoubleArray;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.promptWithAnswer;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.widgetTestActivity;
import static org.odk.cersgis.basis.widgets.utilities.ActivityGeoDataRequester.ACCURACY_THRESHOLD;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class ActivityGeoDataRequesterTest {
    private final FakePermissionUtils permissionUtils = new FakePermissionUtils();
    private final ActivityGeoDataRequester activityGeoDataRequester = new ActivityGeoDataRequester(permissionUtils);
    private final FakeWaitingForDataRegistry waitingForDataRegistry = new FakeWaitingForDataRegistry();
    private final GeoPointData answer = new GeoPointData(getRandomDoubleArray());

    private TestScreenContextActivity testActivity;
    private ShadowActivity shadowActivity;
    private FormEntryPrompt prompt;
    private FormIndex formIndex;
    private QuestionDef questionDef;

    @Before
    public void setUp() {
        testActivity = widgetTestActivity();
        shadowActivity = shadowOf(testActivity);

        prompt = promptWithAnswer(null);
        formIndex = mock(FormIndex.class);
        questionDef = mock(QuestionDef.class);

        permissionUtils.setPermissionGranted(true);
        when(prompt.getQuestion()).thenReturn(questionDef);
        when(prompt.getIndex()).thenReturn(formIndex);
    }

    @Test
    public void whenPermissionIsNotGranted_requestGeoPoint_doesNotLaunchAnyIntent() {
        permissionUtils.setPermissionGranted(false);
        activityGeoDataRequester.requestGeoPoint(testActivity, prompt, "", waitingForDataRegistry);

        assertNull(shadowActivity.getNextStartedActivity());
        assertTrue(waitingForDataRegistry.waiting.isEmpty());
    }

    @Test
    public void whenPermissionIsNotGranted_requestGeoShape_doesNotLaunchAnyIntent() {
        permissionUtils.setPermissionGranted(false);
        activityGeoDataRequester.requestGeoShape(testActivity, prompt, "", waitingForDataRegistry);

        assertNull(shadowActivity.getNextStartedActivity());
        assertTrue(waitingForDataRegistry.waiting.isEmpty());
    }

    @Test
    public void whenPermissionIsNotGranted_requestGeoTrace_doesNotLaunchAnyIntent() {
        permissionUtils.setPermissionGranted(false);
        activityGeoDataRequester.requestGeoTrace(testActivity, prompt, "", waitingForDataRegistry);

        assertNull(shadowActivity.getNextStartedActivity());
        assertTrue(waitingForDataRegistry.waiting.isEmpty());
    }

    @Test
    public void whenPermissionIGranted_requestGeoPoint_setsFormIndexWaitingForData() {
        activityGeoDataRequester.requestGeoPoint(testActivity, prompt, "", waitingForDataRegistry);
        assertTrue(waitingForDataRegistry.waiting.contains(formIndex));
    }

    @Test
    public void whenPermissionIGranted_requestGeoShape_setsFormIndexWaitingForData() {
        activityGeoDataRequester.requestGeoShape(testActivity, prompt, "", waitingForDataRegistry);
        assertTrue(waitingForDataRegistry.waiting.contains(formIndex));
    }

    @Test
    public void whenPermissionIGranted_requestGeoTrace_setsFormIndexWaitingForData() {
        activityGeoDataRequester.requestGeoTrace(testActivity, prompt, "", waitingForDataRegistry);
        assertTrue(waitingForDataRegistry.waiting.contains(formIndex));
    }

    @Test
    public void whenWidgetIsReadOnly_requestGeoPoint_launchesCorrectIntent() {
        when(prompt.isReadOnly()).thenReturn(true);
        activityGeoDataRequester.requestGeoPoint(testActivity, prompt, "", waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPointActivity.class));
        assertGeoPointBundleArgumentEquals(startedIntent.getExtras(), null, GeoWidgetUtils.DEFAULT_LOCATION_ACCURACY,
                true, false);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, LOCATION_CAPTURE);
    }

    @Test
    public void whenWidgetHasAnswerAndAccuracyValue_requestGeoPoint_launchesCorrectIntent() {
        when(questionDef.getAdditionalAttribute(null, ACCURACY_THRESHOLD)).thenReturn("10");

        activityGeoDataRequester.requestGeoPoint(testActivity, prompt, answer.getDisplayText(), waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPointActivity.class));
        assertGeoPointBundleArgumentEquals(startedIntent.getExtras(), GeoWidgetUtils.getLocationParamsFromStringAnswer(
                answer.getDisplayText()), 10.0, false, false);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, LOCATION_CAPTURE);
    }

    @Test
    public void whenWidgetHasMapsAppearance_requestGeoPoint_launchesCorrectIntent() {
        when(prompt.getAppearanceHint()).thenReturn(WidgetAppearanceUtils.MAPS);

        activityGeoDataRequester.requestGeoPoint(testActivity, prompt, "", waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPointMapActivity.class));
        assertGeoPointBundleArgumentEquals(startedIntent.getExtras(), null, GeoWidgetUtils.DEFAULT_LOCATION_ACCURACY,
                false, false);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, LOCATION_CAPTURE);
    }

    @Test
    public void whenWidgetHasPlacementMapAppearance_requestGeoPoint_launchesCorrectIntent() {
        when(prompt.getAppearanceHint()).thenReturn(WidgetAppearanceUtils.PLACEMENT_MAP);

        activityGeoDataRequester.requestGeoPoint(testActivity, prompt, "", waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPointMapActivity.class));
        assertGeoPointBundleArgumentEquals(startedIntent.getExtras(), null, GeoWidgetUtils.DEFAULT_LOCATION_ACCURACY,
                false, true);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, LOCATION_CAPTURE);
    }

    @Test
    public void whenWidgetIsReadOnly_requestGeoShape_launchesCorrectIntent() {
        when(prompt.isReadOnly()).thenReturn(true);
        activityGeoDataRequester.requestGeoShape(testActivity, prompt, "", waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPolyActivity.class));
        assertGeoPolyBundleArgumentEquals(startedIntent.getExtras(), "", GeoPolyActivity.OutputMode.GEOSHAPE, true);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, GEOSHAPE_CAPTURE);
    }

    @Test
    public void whenWidgetHasAnswerAndAccuracyValue_requestGeoShape_launchesCorrectIntent() {
        when(questionDef.getAdditionalAttribute(null, ACCURACY_THRESHOLD)).thenReturn("10");

        activityGeoDataRequester.requestGeoShape(testActivity, prompt, "blah", waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPolyActivity.class));
        assertGeoPolyBundleArgumentEquals(startedIntent.getExtras(), "blah", GeoPolyActivity.OutputMode.GEOSHAPE, false);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, GEOSHAPE_CAPTURE);
    }

    @Test
    public void whenWidgetIsReadOnly_requestGeoTrace_launchesCorrectIntent() {
        when(prompt.isReadOnly()).thenReturn(true);
        activityGeoDataRequester.requestGeoTrace(testActivity, prompt, "", waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPolyActivity.class));
        assertGeoPolyBundleArgumentEquals(startedIntent.getExtras(), "", GeoPolyActivity.OutputMode.GEOTRACE, true);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, GEOTRACE_CAPTURE);
    }

    @Test
    public void whenWidgetHasAnswerAndAccuracyValue_requestGeoTrace_launchesCorrectIntent() {
        when(questionDef.getAdditionalAttribute(null, ACCURACY_THRESHOLD)).thenReturn("10");

        activityGeoDataRequester.requestGeoTrace(testActivity, prompt, "blah", waitingForDataRegistry);
        Intent startedIntent = shadowActivity.getNextStartedActivity();

        assertEquals(startedIntent.getComponent(), new ComponentName(testActivity, GeoPolyActivity.class));
        assertGeoPolyBundleArgumentEquals(startedIntent.getExtras(), "blah", GeoPolyActivity.OutputMode.GEOTRACE, false);

        assertEquals(shadowActivity.getNextStartedActivityForResult().requestCode, GEOTRACE_CAPTURE);
    }
}

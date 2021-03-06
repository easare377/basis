package org.odk.cersgis.basis.widgets.utilities;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.javarosa.form.api.FormEntryPrompt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.cersgis.basis.R;
import org.odk.cersgis.basis.fakes.FakePermissionUtils;
import org.odk.cersgis.basis.formentry.FormEntryViewModel;
import org.odk.cersgis.basis.utilities.ActivityAvailability;
import org.odk.cersgis.basis.utilities.ApplicationConstants;
import org.odk.cersgis.basis.widgets.support.FakeWaitingForDataRegistry;
import org.odk.cersgis.audiorecorder.recording.AudioRecorderViewModel;
import org.odk.cersgis.audiorecorder.recording.RecordingSession;
import org.odk.cersgis.testshared.FakeLifecycleOwner;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.odk.cersgis.basis.widgets.support.QuestionWidgetHelpers.promptWithAnswer;
import static org.robolectric.Shadows.shadowOf;

@RunWith(AndroidJUnit4.class)
public class ExternalAppRecordingRequesterTest {

    private final ActivityAvailability activityAvailability = mock(ActivityAvailability.class);
    private final FakePermissionUtils permissionUtils = new FakePermissionUtils();
    private final FakeWaitingForDataRegistry waitingForDataRegistry = new FakeWaitingForDataRegistry();
    private final AudioRecorderViewModel audioRecorderViewModel = mock(AudioRecorderViewModel.class);

    private Activity activity;
    private ExternalAppRecordingRequester requester;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(Activity.class).get();
        requester = new ExternalAppRecordingRequester(activity, activityAvailability, waitingForDataRegistry, permissionUtils, mock(FormEntryViewModel.class), audioRecorderViewModel, new FakeLifecycleOwner());
    }

    @Test
    public void requestRecording_whenIntentIsNotAvailable_doesNotStartAnyIntentAndCancelsWaitingForData() {
        when(activityAvailability.isActivityAvailable(any())).thenReturn(false);
        permissionUtils.setPermissionGranted(true);

        requester.requestRecording(promptWithAnswer(null));

        Intent startedActivity = shadowOf(activity).getNextStartedActivity();
        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertThat(startedActivity, nullValue());
        assertThat(waitingForDataRegistry.waiting.isEmpty(), is(true));
        assertThat(toastMessage, equalTo(activity.getString(R.string.activity_not_found, activity.getString(R.string.capture_audio))));
    }

    @Test
    public void requestRecording_whenPermissionIsNotGranted_doesNotStartAnyIntentAndCancelsWaitingForData() {
        when(activityAvailability.isActivityAvailable(any())).thenReturn(true);
        permissionUtils.setPermissionGranted(false);

        requester.requestRecording(promptWithAnswer(null));

        Intent startedActivity = shadowOf(activity).getNextStartedActivity();
        assertThat(startedActivity, nullValue());
        assertThat(waitingForDataRegistry.waiting.isEmpty(), equalTo(true));
    }

    @Test
    public void requestRecording_whenPermissionIsGranted_startsRecordSoundIntentAndSetsWidgetWaitingForData() {
        when(activityAvailability.isActivityAvailable(any())).thenReturn(true);
        permissionUtils.setPermissionGranted(true);

        FormEntryPrompt prompt = promptWithAnswer(null);
        requester.requestRecording(prompt);

        Intent startedActivity = shadowOf(activity).getNextStartedActivity();
        assertThat(startedActivity.getAction(), equalTo(MediaStore.Audio.Media.RECORD_SOUND_ACTION));
        assertThat(startedActivity.getStringExtra(MediaStore.EXTRA_OUTPUT), equalTo(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                .toString()));

        ShadowActivity.IntentForResult intentForResult = shadowOf(activity).getNextStartedActivityForResult();
        assertThat(intentForResult.requestCode, equalTo(ApplicationConstants.RequestCodes.AUDIO_CAPTURE));

        assertThat(waitingForDataRegistry.waiting.contains(prompt.getIndex()), equalTo(true));
    }

    @Test
    public void onIsRecordingChangedBlocked_listensToCurrentSessionOnAudioRecorderViewModel() {
        MutableLiveData<RecordingSession> liveData = new MutableLiveData<>(null);
        when(audioRecorderViewModel.getCurrentSession()).thenReturn(liveData);

        Consumer<Boolean> listener = mock(Consumer.class);
        requester.onIsRecordingBlocked(listener);
        verify(listener).accept(false);

        liveData.setValue(new RecordingSession("blah", null, 0, 0, false));
        verify(listener).accept(true);
    }
}

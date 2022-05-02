package org.odk.cersgis.basis.widgets.utilities;

import android.app.Activity;
import android.util.Pair;

import androidx.lifecycle.LifecycleOwner;

import org.javarosa.form.api.FormEntryPrompt;
import org.odk.cersgis.basis.analytics.AnalyticsEvents;
import org.odk.cersgis.basis.formentry.FormEntryViewModel;
import org.odk.cersgis.basis.listeners.PermissionListener;
import org.odk.cersgis.basis.utilities.FormEntryPromptUtils;
import org.odk.cersgis.basis.utilities.PermissionUtils;
import org.odk.cersgis.basis.utilities.QuestionMediaManager;
import org.odk.cersgis.audiorecorder.recorder.Output;
import org.odk.cersgis.audiorecorder.recording.AudioRecorderViewModel;

import java.util.function.Consumer;

public class InternalRecordingRequester implements RecordingRequester {

    private final Activity activity;
    private final AudioRecorderViewModel viewModel;
    private final PermissionUtils permissionUtils;
    private final LifecycleOwner lifecycleOwner;
    private final QuestionMediaManager questionMediaManager;
    private final FormEntryViewModel formEntryViewModel;

    public InternalRecordingRequester(Activity activity, AudioRecorderViewModel viewModel, PermissionUtils permissionUtils, LifecycleOwner lifecycleOwner, QuestionMediaManager questionMediaManager, FormEntryViewModel formEntryViewModel) {
        this.activity = activity;
        this.viewModel = viewModel;
        this.permissionUtils = permissionUtils;
        this.lifecycleOwner = lifecycleOwner;
        this.questionMediaManager = questionMediaManager;
        this.formEntryViewModel = formEntryViewModel;
    }

    @Override
    public void onIsRecordingBlocked(Consumer<Boolean> isRecordingBlockedListener) {
        viewModel.getCurrentSession().observe(lifecycleOwner, session -> {
            isRecordingBlockedListener.accept(session != null && session.getFile() == null);
        });
    }

    @Override
    public void requestRecording(FormEntryPrompt prompt) {
        permissionUtils.requestRecordAudioPermission(activity, new PermissionListener() {
            @Override
            public void granted() {
                String quality = FormEntryPromptUtils.getAttributeValue(prompt, "quality");
                if (quality != null && quality.equals("voice-only")) {
                    viewModel.start(prompt.getIndex().toString(), Output.AMR);
                } else if (quality != null && quality.equals("low")) {
                    viewModel.start(prompt.getIndex().toString(), Output.AAC_LOW);
                } else {
                    viewModel.start(prompt.getIndex().toString(), Output.AAC);
                }
            }

            @Override
            public void denied() {

            }
        });

        formEntryViewModel.logFormEvent(AnalyticsEvents.AUDIO_RECORDING_INTERNAL);
    }

    @Override
    public void onRecordingInProgress(FormEntryPrompt prompt, Consumer<Pair<Long, Integer>> durationListener) {
        viewModel.getCurrentSession().observe(lifecycleOwner, session -> {
            if (session != null && session.getId().equals(prompt.getIndex().toString()) && session.getFailedToStart() == null) {
                durationListener.accept(new Pair<>(session.getDuration(), session.getAmplitude()));
            }
        });
    }

    @Override
    public void onRecordingFinished(FormEntryPrompt prompt, Consumer<String> recordingAvailableListener) {
        viewModel.getCurrentSession().observe(lifecycleOwner, session -> {
            if (session != null && session.getId().equals(prompt.getIndex().toString()) && session.getFile() != null) {
                questionMediaManager.createAnswerFile(session.getFile()).observe(lifecycleOwner, result -> {
                    if (result != null) {
                        if (result.isSuccess()) {
                            session.getFile().delete();
                        }

                        viewModel.cleanUp();
                        recordingAvailableListener.accept(result.getOrNull());
                    }
                });
            }
        });
    }
}

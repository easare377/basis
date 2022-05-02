package org.odk.cersgis.audiorecorder.recording.internal

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import org.odk.cersgis.audiorecorder.recorder.Output
import org.odk.cersgis.audiorecorder.recording.AudioRecorderViewModel
import org.odk.cersgis.audiorecorder.recording.RecordingSession
import org.odk.cersgis.audiorecorder.recording.internal.AudioRecorderService.Companion.ACTION_CLEAN_UP
import org.odk.cersgis.audiorecorder.recording.internal.AudioRecorderService.Companion.ACTION_PAUSE
import org.odk.cersgis.audiorecorder.recording.internal.AudioRecorderService.Companion.ACTION_RESUME
import org.odk.cersgis.audiorecorder.recording.internal.AudioRecorderService.Companion.ACTION_START
import org.odk.cersgis.audiorecorder.recording.internal.AudioRecorderService.Companion.ACTION_STOP
import org.odk.cersgis.audiorecorder.recording.internal.AudioRecorderService.Companion.EXTRA_OUTPUT
import org.odk.cersgis.audiorecorder.recording.internal.AudioRecorderService.Companion.EXTRA_SESSION_ID

internal class ForegroundServiceAudioRecorderViewModel internal constructor(private val application: Application, private val recordingRepository: RecordingRepository) : AudioRecorderViewModel() {

    override fun isRecording(): Boolean {
        val currentSession = recordingRepository.currentSession.value
        return currentSession != null && currentSession.file == null
    }

    override fun getCurrentSession(): LiveData<RecordingSession?> {
        return recordingRepository.currentSession
    }

    override fun start(sessionId: String, output: Output) {
        application.startService(
            Intent(application, AudioRecorderService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_OUTPUT, output)
            }
        )
    }

    override fun pause() {
        application.startService(
            Intent(application, AudioRecorderService::class.java).apply { action = ACTION_PAUSE }
        )
    }

    override fun resume() {
        application.startService(
            Intent(application, AudioRecorderService::class.java).apply { action = ACTION_RESUME }
        )
    }

    override fun stop() {
        application.startService(
            Intent(application, AudioRecorderService::class.java).apply { action = ACTION_STOP }
        )
    }

    override fun cleanUp() {
        application.startService(
            Intent(application, AudioRecorderService::class.java).apply { action = ACTION_CLEAN_UP }
        )
    }
}

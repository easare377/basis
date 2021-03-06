package org.odk.cersgis.audiorecorder.recording

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.odk.cersgis.audiorecorder.getComponent
import org.odk.cersgis.audiorecorder.recording.internal.ForegroundServiceAudioRecorderViewModel

open class AudioRecorderViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ForegroundServiceAudioRecorderViewModel(application, application.getComponent().recordingRepository()) as T
    }
}

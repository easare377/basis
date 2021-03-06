package org.odk.cersgis.audiorecorder.recording.internal

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.odk.cersgis.async.Scheduler
import org.odk.cersgis.audiorecorder.AudioRecorderDependencyModule
import org.odk.cersgis.audiorecorder.R
import org.odk.cersgis.audiorecorder.RobolectricApplication
import org.odk.cersgis.audiorecorder.recorder.Output
import org.odk.cersgis.audiorecorder.recorder.Recorder
import org.odk.cersgis.audiorecorder.setupDependencies
import org.odk.cersgis.audiorecorder.support.FakeRecorder
import org.odk.cersgis.testshared.FakeScheduler
import org.robolectric.Robolectric.buildService
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ServiceController
import java.io.File
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
class AudioRecorderServiceTest {

    private val application: RobolectricApplication by lazy { ApplicationProvider.getApplicationContext() }
    private val recorder = FakeRecorder()
    private val scheduler = FakeScheduler()

    private var serviceInstance: ServiceController<AudioRecorderService>? = null

    @Before
    fun setup() {
        application.setupDependencies(
            object : AudioRecorderDependencyModule() {
                override fun providesRecorder(cacheDir: File): Recorder {
                    return recorder
                }

                override fun providesScheduler(application: Application): Scheduler {
                    return scheduler
                }
            }
        )
    }

    @Test
    fun startAction_startsInForegroundWithNotification() {
        val service = buildService(AudioRecorderService::class.java, createStartIntent("123"))
            .create()
            .startCommand(0, 0)

        val notification = shadowOf(service.get()).lastForegroundNotification
        assertThat(notification, not(nullValue()))
        assertThat(shadowOf(notification).contentTitle, equalTo(application.getString(R.string.recording)))
        assertThat(shadowOf(notification.contentIntent).savedIntent.component?.className, equalTo(ReturnToAppActivity::class.qualifiedName))
    }

    @Test
    fun startAction_startsRecorder() {
        startService(createStartIntent("123"))
        assertThat(recorder.isRecording(), equalTo(true))
    }

    @Test
    fun startAction_whenRecordingInProgress_doesNothing() {
        val intent = createStartIntent("456")

        startService(intent)
        assertThat(recorder.isRecording(), equalTo(true))

        startService(intent)
        assertThat(recorder.isRecording(), equalTo(true))
        assertThat(recorder.recordings.size, equalTo(1))
    }

    @Test
    fun startAction_updatesDurationOnNotificationEverySecond() {
        val intent = createStartIntent("456")
        val service = startService(intent)

        val notificationId = shadowOf(service.get()).lastForegroundNotificationId
        val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowNotificationManager = shadowOf(notificationManager)

        scheduler.runForeground(0)
        assertThat(shadowOf(shadowNotificationManager.getNotification(notificationId)).contentText, equalTo("00:00"))

        scheduler.runForeground(500)
        assertThat(shadowOf(shadowNotificationManager.getNotification(notificationId)).contentText, equalTo("00:00"))

        scheduler.runForeground(1000)
        assertThat(shadowOf(shadowNotificationManager.getNotification(notificationId)).contentText, equalTo("00:01"))

        scheduler.runForeground(2000)
        assertThat(shadowOf(shadowNotificationManager.getNotification(notificationId)).contentText, equalTo("00:02"))
    }

    @Test
    fun startAction_whenRecorderStartThrowsException_stopsSelf() {
        recorder.failOnStart(Exception())

        val service = startService(createStartIntent("123"))
        assertThat(shadowOf(service.get()).isStoppedBySelf, equalTo(true))
    }

    @Test
    fun stopAction_stopsSelf() {
        startService(createStartIntent("123"))

        val stopIntent = Intent(application, AudioRecorderService::class.java)
        stopIntent.action = AudioRecorderService.ACTION_STOP
        val service = startService(stopIntent)

        assertThat(shadowOf(service.get()).isStoppedBySelf, equalTo(true))
    }

    @Test
    fun stopAction_stopsUpdates() {
        startService(createStartIntent("123"))

        val stopIntent = Intent(application, AudioRecorderService::class.java)
        stopIntent.action = AudioRecorderService.ACTION_STOP
        startService(stopIntent)

        assertThat(scheduler.isRepeatRunning(), equalTo(false))
    }

    @Test
    fun stopAction_stopsRecorder() {
        startService(createStartIntent("123"))

        val stopIntent = Intent(application, AudioRecorderService::class.java)
        stopIntent.action = AudioRecorderService.ACTION_STOP
        startService(stopIntent)

        assertThat(recorder.isRecording(), equalTo(false))
    }

    @Test
    fun stopAction_beforeStart_doesNothing() {
        val stopIntent = Intent(application, AudioRecorderService::class.java)
        stopIntent.action = AudioRecorderService.ACTION_STOP
        val service = startService(stopIntent)

        val notification = shadowOf(service.get()).lastForegroundNotification
        assertThat(notification, nullValue())
    }

    @Test
    fun cleanUpAction_whileRecording_cancelsRecorder() {
        startService(createStartIntent("123"))

        val cancelIntent = Intent(application, AudioRecorderService::class.java)
        cancelIntent.action = AudioRecorderService.ACTION_CLEAN_UP
        startService(cancelIntent)

        assertThat(recorder.isRecording(), equalTo(false))
        assertThat(recorder.wasCancelled(), equalTo(true))
    }

    @Test
    fun cleanUpAction_whileRecording_stopsUpdates() {
        startService(createStartIntent("123"))

        val cancelIntent = Intent(application, AudioRecorderService::class.java)
        cancelIntent.action = AudioRecorderService.ACTION_CLEAN_UP
        startService(cancelIntent)

        assertThat(scheduler.isRepeatRunning(), equalTo(false))
    }

    @Test
    fun cleanUpAction_whileRecording_stopsSelf() {
        startService(createStartIntent("123"))

        val cancelIntent = Intent(application, AudioRecorderService::class.java)
        cancelIntent.action = AudioRecorderService.ACTION_CLEAN_UP
        val service = startService(cancelIntent)

        assertThat(shadowOf(service.get()).isStoppedBySelf, equalTo(true))
    }

    @Test
    fun cleanUpAction_afterRecording_stopsSelf() {
        startService(createStartIntent("123"))

        val stopIntent = Intent(application, AudioRecorderService::class.java)
        stopIntent.action = AudioRecorderService.ACTION_STOP
        startService(stopIntent)

        val cancelIntent = Intent(application, AudioRecorderService::class.java)
        cancelIntent.action = AudioRecorderService.ACTION_CLEAN_UP
        val service = startService(cancelIntent)

        assertThat(shadowOf(service.get()).isStoppedBySelf, equalTo(true))
    }

    @Test
    fun whenUserKillsAppFromTaskManager_cancelsRecorder_stopsSelf() {
        val service = startService(createStartIntent("123"))

        service.get().onTaskRemoved(Intent())

        assertThat(recorder.isRecording(), equalTo(false))
        assertThat(recorder.wasCancelled(), equalTo(true))
        assertThat(shadowOf(service.get()).isStoppedBySelf, equalTo(true))
    }

    @Test
    fun pauseAction_pausesRecorder() {
        startService(createStartIntent("123"))

        val pauseIntent = Intent(application, AudioRecorderService::class.java)
        pauseIntent.action = AudioRecorderService.ACTION_PAUSE
        startService(pauseIntent)

        assertThat(recorder.paused, equalTo(true))
    }

    @Test
    fun pauseAction_stopsUpdates() {
        startService(createStartIntent("123"))

        val pauseIntent = Intent(application, AudioRecorderService::class.java)
        pauseIntent.action = AudioRecorderService.ACTION_PAUSE
        startService(pauseIntent)

        assertThat(scheduler.isRepeatRunning(), equalTo(false))
    }

    @Test
    fun pauseAction_andThenResumeAction_resumesRecorder() {
        startService(createStartIntent("123"))

        val pauseIntent = Intent(application, AudioRecorderService::class.java)
        pauseIntent.action = AudioRecorderService.ACTION_PAUSE
        startService(pauseIntent)

        val resumeIntent = Intent(application, AudioRecorderService::class.java)
        resumeIntent.action = AudioRecorderService.ACTION_RESUME
        startService(resumeIntent)

        assertThat(recorder.paused, equalTo(false))
    }

    @Test
    fun pauseAction_andThenResumeAction_startsUpdates() {
        startService(createStartIntent("123"))

        val pauseIntent = Intent(application, AudioRecorderService::class.java)
        pauseIntent.action = AudioRecorderService.ACTION_PAUSE
        startService(pauseIntent)

        val resumeIntent = Intent(application, AudioRecorderService::class.java)
        resumeIntent.action = AudioRecorderService.ACTION_RESUME
        startService(resumeIntent)

        assertThat(scheduler.isRepeatRunning(), equalTo(true))
    }

    @Test
    fun pauseAction_afterStop_doesNothing() {
        val pauseIntent = Intent(application, AudioRecorderService::class.java)
        pauseIntent.action = AudioRecorderService.ACTION_PAUSE
        val service = startService(pauseIntent)

        val notification = shadowOf(service.get()).lastForegroundNotification
        assertThat(notification, nullValue())
    }

    private fun createStartIntent(sessionId: String): Intent {
        val intent = Intent(application, AudioRecorderService::class.java)
        intent.action = AudioRecorderService.ACTION_START
        intent.putExtra(AudioRecorderService.EXTRA_SESSION_ID, sessionId)
        intent.putExtra(AudioRecorderService.EXTRA_OUTPUT, Output.AAC)
        return intent
    }

    private fun startService(intent: Intent): ServiceController<AudioRecorderService> {
        return serviceInstance.let {
            if (it == null) {
                val serviceController = buildService(AudioRecorderService::class.java, intent)
                    .create()
                    .startCommand(0, 0)
                serviceInstance = serviceController
                serviceController
            } else {
                it.withIntent(intent)
                    .startCommand(0, 0)
            }
        }
    }
}

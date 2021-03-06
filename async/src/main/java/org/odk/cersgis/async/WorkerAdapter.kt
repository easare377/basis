package org.odk.cersgis.async

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

abstract class WorkerAdapter(private val spec: TaskSpec, context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val completed = spec.getTask(applicationContext).get()

        return if (completed) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}

package com.noto.app

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.noto.domain.interactor.SyncData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SyncWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters), KoinComponent {

    private val syncData by inject<SyncData>()

    override fun doWork(): Result {

        val job = GlobalScope.launch {
            syncData()
        }

        job.invokeOnCompletion {
            if (it == null) {
                job.cancel()
            }
        }

        return Result.success()
    }

}
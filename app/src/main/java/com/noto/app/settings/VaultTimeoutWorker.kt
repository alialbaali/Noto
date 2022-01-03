package com.noto.app.settings

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VaultTimeoutWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {
    private val storage by inject<LocalStorage>()

    override suspend fun doWork(): Result {
        storage.put(Constants.IsVaultOpen, false.toString())
        return Result.success()
    }
}
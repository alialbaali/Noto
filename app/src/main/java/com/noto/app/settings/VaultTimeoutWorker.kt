package com.noto.app.settings

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.cancelVaultNotification
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VaultTimeoutWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val storage by inject<LocalStorage>()

    override suspend fun doWork(): Result {
        storage.put(Constants.IsVaultOpen, false.toString())
        storage.remove(Constants.ScheduledVaultTimeout)
        notificationManager.cancelVaultNotification()
        return Result.success()
    }
}
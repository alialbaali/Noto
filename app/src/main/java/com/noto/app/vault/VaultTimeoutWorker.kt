package com.noto.app.vault

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.cancelVaultNotification
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VaultTimeoutWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val settingsRepository by inject<SettingsRepository>()

    override suspend fun doWork(): Result {
        settingsRepository.updateIsVaultOpen(false)
        settingsRepository.updateScheduledVaultTimeout(null)
        notificationManager.cancelVaultNotification()
        return Result.success()
    }
}
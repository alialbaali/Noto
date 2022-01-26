package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.cancelVaultNotification
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VaultReceiver : BroadcastReceiver(), KoinComponent {
    private val settingsRepository by inject<SettingsRepository>()

    override fun onReceive(context: Context?, intent: Intent?) = runBlocking {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancelVaultNotification()
        settingsRepository.updateIsVaultOpen(false)
    }
}
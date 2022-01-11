package com.noto.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.cancelVaultNotification
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VaultReceiver : BroadcastReceiver(), KoinComponent {
    private val storage by inject<LocalStorage>()

    override fun onReceive(context: Context?, intent: Intent?) = runBlocking {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.cancelVaultNotification()
        storage.put(Constants.IsVaultOpen, false.toString())
    }
}
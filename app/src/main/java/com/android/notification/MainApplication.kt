package com.android.notification

import android.app.Application
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startNotificationChannel()
        startForegroundService(
            Intent(this, NotificationService::class.java)
        )
    }

    private fun startNotificationChannel() {
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        NotificationChannelCompat
            .Builder(CHANNEL.ID, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName("My notification channel")
            .setDescription("Minha descrição")
            .build()
            .apply(notificationManagerCompat::createNotificationChannel)
    }

    object CHANNEL {
        const val ID = "mychannel"
    }

    object UTILS {
        val RANDOM by PseudoRandom(1000, Int::inc)
    }
}
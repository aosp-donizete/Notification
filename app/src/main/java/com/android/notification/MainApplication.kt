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
            .Builder(CHANNEL.ID, NotificationManagerCompat.IMPORTANCE_MAX)
            .setName("My notification channel")
            .setDescription("Minha descrição")
            .setVibrationEnabled(true)
            .setVibrationPattern(longArrayOf(1000, 1000, 1000))
            .build()
            .apply(notificationManagerCompat::createNotificationChannel)
    }

    object CHANNEL {
        const val ID = "mychannel"
    }

    object UTILS {
        val RANDOM by PseudoRandom(1000, Int::inc)
    }

    object ACTION {
        val SIMPLE_RECEIVER_ACTION = "com.android.notification.SIMPLE_RECEIVER_ACTION"
    }
}
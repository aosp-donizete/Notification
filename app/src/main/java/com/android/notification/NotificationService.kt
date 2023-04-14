package com.android.notification

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    private val handler = Handler(Looper.getMainLooper())
    private val notificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }
    private val notification by lazy {
        NotificationCompat.Builder(this, MainApplication.CHANNEL.ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Service title here")
            .setContentText("Service text here")
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(MainApplication.UTILS.RANDOM, notification)
        //startNotificationTimer()
    }

    private fun startNotificationTimer() {
        notificationManagerCompat.notify(MainApplication.UTILS.RANDOM, notification)
        handler.postDelayed(::startNotificationTimer, 2000)
    }
}
package com.android.notification

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationManagerCompat

class NotificationService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    private val handler = Handler(Looper.getMainLooper())

    private val notificationCreator by lazy {
        NotificationCreator(this)
    }
    private val notification by lazy {
        notificationCreator.createReply()
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(MainApplication.UTILS.RANDOM, notification)
        //startNotificationTimer()
    }

    private fun startNotificationTimer() {
        notificationCreator.notificator {
            notify(notification)
        }
        handler.postDelayed(::startNotificationTimer, 10000)
    }
}
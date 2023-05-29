package com.android.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

interface Notificator {
    fun notify(notification: Notification)
}

class NotificationCreator(
    context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val notifier: (Notification) -> Unit = {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(MainApplication.UTILS.RANDOM, it)
        }
    }

    private val notificator = object : Notificator {
        override fun notify(notification: Notification) = notifier(notification)
    }

    context(Context)
    fun notificator(block: Notificator.() -> Unit) = block(notificator)

    context(Context)
    fun createReply() = run {
        val remoteInput = RemoteInput.Builder(MainApplication.ACTION.SIMPLE_RECEIVER_ACTION).run {
            setLabel("Label here")
            build()
        }

        val pending = PendingIntent.getBroadcast(
            this@Context,
            MainApplication.UTILS.RANDOM,
            Intent(MainApplication.ACTION.SIMPLE_RECEIVER_ACTION),
            PendingIntent.FLAG_MUTABLE
        )

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_foreground,
            "Click me",
            pending
        ).addRemoteInput(remoteInput).build()

        NotificationCompat.Builder(this@Context, MainApplication.CHANNEL.ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Title here")
            .setContentText("Text here\nBelow line")
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY).addAction(action)
            .setAutoCancel(true)
            .build()
    }

    context(Context)
    fun createBubble() = run {
        val target = Intent(this@Context, MainActivity::class.java)

        val bubbleIntent = PendingIntent.getActivity(
            this@Context, MainApplication.UTILS.RANDOM, target, PendingIntent.FLAG_MUTABLE
        )

        val chatPartner = Person
            .Builder()
            .setName("Chat partner ${MainApplication.UTILS.RANDOM}")
            .setBot(true)
            .setImportant(true)
            .build()

        val shortcutId = "SHORT_CUT_${MainApplication.UTILS.RANDOM}"

        val shortcut = ShortcutInfoCompat
            .Builder(this@Context, shortcutId)
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel(chatPartner.name ?: "")
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(this@Context, shortcut)

        val icon = IconCompat
            .createWithResource(this@Context, R.drawable.ic_launcher_foreground)

        val bubbleData = NotificationCompat.BubbleMetadata.Builder(
            bubbleIntent, icon
        ).setDesiredHeight(600).build()

        NotificationCompat
            .Builder(this@Context, MainApplication.CHANNEL.ID)
            .setContentIntent(bubbleIntent)
            .setSmallIcon(icon)
            .setStyle(
                NotificationCompat.MessagingStyle(chatPartner)
            ).setContentText("Bubble me")
            .setBubbleMetadata(bubbleData)
            .setShortcutInfo(shortcut)
            .addPerson(chatPartner)
            .build()
    }

    context(Context)
    fun createSimpleNotificationGroupAndBubble() = run {
        val target = Intent(this@Context, MainActivity::class.java)

        val bubbleIntent = PendingIntent.getActivity(
            this@Context, MainApplication.UTILS.RANDOM, target, PendingIntent.FLAG_MUTABLE
        )

        val icon = IconCompat
            .createWithResource(this@Context, R.drawable.ic_launcher_foreground)

        val bubbleData = NotificationCompat.BubbleMetadata
            .Builder(bubbleIntent, icon)
            .setDesiredHeight(600)
            .build()

        val shortcutId = "SHORT_CUT_${MainApplication.UTILS.RANDOM}"

        val chatPartner = Person
            .Builder()
            .setName("Chat partner ${MainApplication.UTILS.RANDOM}")
            .setBot(true)
            .setImportant(true)
            .build()

        val shortcut = ShortcutInfoCompat.Builder(this@Context, shortcutId)
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel(chatPartner.name ?: "")
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(this@Context, shortcut)

        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"

        val newMessageNotification = NotificationCompat
            .Builder(this@Context, MainApplication.CHANNEL.ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(bubbleIntent)
            .setContentTitle("Title here")
            .setContentText("Text here").setStyle(
                NotificationCompat.MessagingStyle(chatPartner)
            ).setGroup(GROUP_KEY_WORK_EMAIL)
            .setBubbleMetadata(bubbleData)
            .setShortcutInfo(shortcut)
            .addPerson(chatPartner)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .build()

        val summaryNotification = NotificationCompat
            .Builder(this@Context, MainApplication.CHANNEL.ID)
            .setContentTitle("emailObject.getSummary()")
            .setContentIntent(bubbleIntent)
            //set content text to support devices running API level < 24
            .setContentText("Two new messages")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            //build summary info into InboxStyle template
            .setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("Alex Faarborg Check this out")
                    .addLine("Jeff Chang Launch Party")
                    .setBigContentTitle("2 new messages")
                    .setSummaryText("janedoe@example.com")
            ).setGroup(GROUP_KEY_WORK_EMAIL)
            .setGroupSummary(true)
            .build()

        summaryNotification to newMessageNotification
    }

    context(Context)
    fun createSimpleAction() = run {
        val intent = Intent(this@Context, MainActivity::class.java)

        val pending = PendingIntent.getActivity(
            this@Context, MainApplication.UTILS.RANDOM, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val action = NotificationCompat.Action(
            R.drawable.ic_launcher_foreground, "Click me", pending
        )

        NotificationCompat.Builder(this@Context, MainApplication.CHANNEL.ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Title here")
            .setContentText("Text here")
            .addAction(action)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
    }
}
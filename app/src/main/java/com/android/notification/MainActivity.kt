package com.android.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

class MainActivity : AppCompatActivity() {
    private val notificationCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    private val channel by lazy {
        getString(R.string.app_name)
    }

    private var random = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerForActivityResult(RequestMultiplePermissions()) {

        }
            .launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

        simpleChannel()
        registerReceiver(simpleReplyReceiver, IntentFilter(SIMPLE_RECEIVER_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simpleReplyReceiver)
    }

    private fun simpleChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(channel, channel, importance).apply {
            description = channel
            setAllowBubbles(true)
        }
        notificationCompat.createNotificationChannel(mChannel)
    }

    fun simpleBubble(v: View) {
        val context = this

        val target = Intent(context, MainActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(context, 0, target, PendingIntent.FLAG_MUTABLE)

        val chatPartner = Person.Builder()
            .setName("Chat partner ${random++}")
            .setBot(true)
            .setImportant(true)
            .build()

        val shortcutId = "SHORT_CUT_$random"

        val shortcut = ShortcutInfoCompat.Builder(context, shortcutId)
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel(chatPartner.name ?: "")
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)

        val icon = IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground)

        val bubbleData = NotificationCompat.BubbleMetadata.Builder(
            bubbleIntent,
            icon
        ).setDesiredHeight(600).build()

        val notification = NotificationCompat.Builder(context, channel)
            .setContentIntent(bubbleIntent)
            .setSmallIcon(icon)
            .setStyle(
                NotificationCompat.MessagingStyle(chatPartner)
            )
            .setContentText("Bubble me")
            .setBubbleMetadata(bubbleData)
            .setShortcutInfo(shortcut)
            .addPerson(chatPartner)
            .build()

        notificationCompat.notify(random * 1000, notification)
    }

    fun simpleNotificationGroupAndBubble(v: View) {
        val target = Intent(this, MainActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(this, 0, target, PendingIntent.FLAG_MUTABLE)

        val icon = IconCompat.createWithResource(this, R.drawable.ic_launcher_foreground)

        val bubbleData = NotificationCompat.BubbleMetadata.Builder(
            bubbleIntent,
            icon
        ).setDesiredHeight(600).build()

        val shortcutId = "SHORT_CUT_$random"

        val chatPartner = Person.Builder()
            .setName("Chat partner ${random++}")
            .setBot(true)
            .setImportant(true)
            .build()

        val shortcut = ShortcutInfoCompat.Builder(this, shortcutId)
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel(chatPartner.name ?: "")
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)

        val SUMMARY_ID = random++ * 1000
        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"

        val newMessageNotification1 = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(bubbleIntent)
            .setContentTitle("emailObject1.getSummary()")
            .setContentText("You will not believe...")
            .setStyle(
                NotificationCompat.MessagingStyle(chatPartner)
            )
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setBubbleMetadata(bubbleData)
            .setShortcutInfo(shortcut)
            .addPerson(chatPartner)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .build()

        val summaryNotification = NotificationCompat.Builder(this, channel)
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
            )
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setGroupSummary(true)
            .build()

        notificationCompat.apply {
            notify(SUMMARY_ID, summaryNotification)
            notify(random++ * 1000, newMessageNotification1)
        }
    }

    private val SIMPLE_RECEIVER_ACTION = "com.android.notification.SIMPLE_RECEIVER_ACTION"
    private val simpleReplyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val reply =
                RemoteInput.getResultsFromIntent(intent!!)?.getCharSequence(SIMPLE_RECEIVER_ACTION)
            Log.d("REPLY", "$reply")
        }
    }

    fun simpleReply(v: View) {
        val remoteInput = RemoteInput.Builder(SIMPLE_RECEIVER_ACTION).run {
            setLabel(channel)
            build()
        }

        val replyPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            random++,
            Intent(SIMPLE_RECEIVER_ACTION),
            PendingIntent.FLAG_MUTABLE
        )

        val action = NotificationCompat.Action.Builder(
                R.drawable.ic_launcher_foreground,
                channel, replyPendingIntent
            )
                .addRemoteInput(remoteInput)
                .build()

        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("emailObject1.getSummary()")
            .setContentText("You will not believe...")
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .addAction(action)
            .build()

        notificationCompat.apply {
            notify(++random * 1000, notification)
        }
    }
}
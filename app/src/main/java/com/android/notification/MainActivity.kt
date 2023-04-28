package com.android.notification

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

class MainActivity : AppCompatActivity() {
    private val notificationManagerCompat by lazy {
        NotificationManagerCompat.from(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerForActivityResult(RequestMultiplePermissions()) {}.launch(
            arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        registerReceiver(simpleReplyReceiver, IntentFilter(SIMPLE_RECEIVER_ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simpleReplyReceiver)
    }

    fun simpleBubble(v: View) {
        val context = this

        val target = Intent(context, MainActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(
            context,
            MainApplication.UTILS.RANDOM,
            target,
            PendingIntent.FLAG_MUTABLE
        )

        val chatPartner = Person.Builder()
            .setName("Chat partner ${MainApplication.UTILS.RANDOM}")
            .setBot(true)
            .setImportant(true)
            .build()

        val shortcutId = "SHORT_CUT_${MainApplication.UTILS.RANDOM}"

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

        val notification = NotificationCompat.Builder(context, MainApplication.CHANNEL.ID)
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

        notificationManagerCompat.notify(MainApplication.UTILS.RANDOM, notification)
    }

    fun simpleNotificationGroupAndBubble(v: View) {
        val target = Intent(this, MainActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(
            this,
            MainApplication.UTILS.RANDOM,
            target,
            PendingIntent.FLAG_MUTABLE
        )

        val icon = IconCompat.createWithResource(this, R.drawable.ic_launcher_foreground)

        val bubbleData = NotificationCompat.BubbleMetadata.Builder(
            bubbleIntent,
            icon
        ).setDesiredHeight(600).build()

        val shortcutId = "SHORT_CUT_${MainApplication.UTILS.RANDOM}"

        val chatPartner = Person.Builder()
            .setName("Chat partner ${MainApplication.UTILS.RANDOM}")
            .setBot(true)
            .setImportant(true)
            .build()

        val shortcut = ShortcutInfoCompat.Builder(this, shortcutId)
            .setIntent(Intent(Intent.ACTION_DEFAULT))
            .setLongLived(true)
            .setShortLabel(chatPartner.name ?: "")
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)

        val SUMMARY_ID = MainApplication.UTILS.RANDOM
        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"

        val newMessageNotification = NotificationCompat.Builder(this, MainApplication.CHANNEL.ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(bubbleIntent)
            .setContentTitle("Title here")
            .setContentText("Text here")
            .setStyle(
                NotificationCompat.MessagingStyle(chatPartner)
            )
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setBubbleMetadata(bubbleData)
            .setShortcutInfo(shortcut)
            .addPerson(chatPartner)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .build()

        val summaryNotification = NotificationCompat.Builder(this, MainApplication.CHANNEL.ID)
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

        notificationManagerCompat.apply {
            notify(SUMMARY_ID, summaryNotification)
            notify(MainApplication.UTILS.RANDOM, newMessageNotification)
        }
    }

    private val SIMPLE_RECEIVER_ACTION = "com.android.notification.SIMPLE_RECEIVER_ACTION"
    private val simpleReplyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val reply = RemoteInput
                .getResultsFromIntent(intent ?: return)
                ?.getCharSequence(SIMPLE_RECEIVER_ACTION)
            Log.d("REPLY", "$reply")
        }
    }

    fun simpleReply(v: View) {
        val remoteInput = RemoteInput.Builder(SIMPLE_RECEIVER_ACTION).run {
            setLabel("Label here")
            build()
        }

        val pending = PendingIntent.getBroadcast(
            this,
            MainApplication.UTILS.RANDOM,
            Intent(SIMPLE_RECEIVER_ACTION),
            PendingIntent.FLAG_MUTABLE
        )

        val action = NotificationCompat.Action.Builder(
            R.drawable.ic_launcher_foreground,
            MainApplication.CHANNEL.ID, pending
        )
            .addRemoteInput(remoteInput)
            .build()

        val notification = NotificationCompat.Builder(this, MainApplication.CHANNEL.ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Title here")
            .setContentText("Text here")
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .addAction(action)
            .build()

        notificationManagerCompat.notify(MainApplication.UTILS.RANDOM, notification)
    }

    fun simpleAction(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(
            this,
            MainApplication.UTILS.RANDOM,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val action = NotificationCompat.Action(
            R.drawable.ic_launcher_foreground,
            "Clique em mim",
            pending
        )

        val notification = NotificationCompat.Builder(this, MainApplication.CHANNEL.ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Title here")
            .setContentText("Text here")
            .addAction(action)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .build()

        notificationManagerCompat.notify(MainApplication.UTILS.RANDOM, notification)
    }

    fun simpleAlert(v: View) {
        val alert = AlertDialog.Builder(this)
        alert.setPositiveButton("Eu aceito") { dialog, which ->

        }
        alert.setNegativeButton("Eu não aceito") { dialog, which ->

        }
        alert.setNeutralButton("MEU NOME É ARI") { dialog, which ->

        }
        alert.setTitle("Algo deu errado!!!")
        alert.setMessage("Você poderia por favor escutar o que eu digo?????")
        alert.show()

        MyDialog().show(supportFragmentManager, null)
    }
}
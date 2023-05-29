package com.android.notification

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.RemoteInput

class MainActivity : AppCompatActivity() {
    private val notificationCreator by lazy {
        NotificationCreator(this)
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
        registerReceiver(
            simpleReplyReceiver, IntentFilter(MainApplication.ACTION.SIMPLE_RECEIVER_ACTION)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(simpleReplyReceiver)
    }

    fun simpleBubble(v: View) {
        notificationCreator.apply {
            createBubble().let {
                notificator {
                    notify(it)
                }
            }
        }
    }

    fun simpleNotificationGroupAndBubble(v: View) {
        notificationCreator.apply {
            createSimpleNotificationGroupAndBubble().apply {
                notificator {
                    notify(first)
                    notify(second)
                }
            }
        }
    }

    private val simpleReplyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val reply = RemoteInput.getResultsFromIntent(intent ?: return)
                ?.getCharSequence(MainApplication.ACTION.SIMPLE_RECEIVER_ACTION) ?: "null"
            Toast.makeText(context, reply, Toast.LENGTH_SHORT).show()
        }
    }

    fun simpleReply(v: View) {
        notificationCreator.apply {
            createReply().let {
                notificator {
                    notify(it)
                }
            }
        }
    }

    fun simpleAction(v: View) {
        notificationCreator.apply {
            createSimpleAction().let {
                notificator {
                    notify(it)
                }
            }
        }
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
package com.jaanonim.you_owe_me_counter

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput


class NotificationListener : NotificationListenerService() {

    private val channelId = "Notification from Service"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel(
                    channelId,
                    "Requests",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }
    }


    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification: Notification? = sbn.notification
        try {
            val packageName = sbn.packageName
            if (packageName != PACKAGE_NAME) {
                return
            }
            val extras = notification?.let { NotificationCompat.getExtras(it) }
            val title = extras!!.getCharSequence(NotificationCompat.EXTRA_TITLE) as String
            val text = extras.getCharSequence(NotificationCompat.EXTRA_TEXT) as String
            val timestamp = sbn.postTime

            if (title == TITLE) {
                Log.d("THING", "onNotificationPosted: $packageName $title $text")
                sendNotification(text, timestamp)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ForegroundServiceType", "MissingPermission", "PrivateResource")
    fun sendNotification(text: String, timestamp: Long) {


        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("New Payment Notification")
            .setContentText(text)
            .setSilent(true)
            .addAction(buildAction("You own me", text, timestamp, 0))
            .addAction(buildAction("I own you", text, timestamp, 1))
            .build()
        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, notification)
        }
    }

    private fun buildAction(
        title: String,
        text: String,
        timestamp: Long,
        tab: Int
    ): NotificationCompat.Action {
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY).run {
            setLabel("Title")
            build()
        }

        val intent =
            Intent(applicationContext, NotificationReceiver::class.java)
                .putExtra("text_yomc", text)
                .putExtra("timestamp_yomc", timestamp)
                .putExtra("tab_yomc", tab)

        val replyPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                tab,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        return NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            title, replyPendingIntent
        ).addRemoteInput(remoteInput).build()
    }
}

val KEY_TEXT_REPLY = "key_text_reply"
val NOTIFICATION_ID = 1
val PACKAGE_NAME = "pl.mbank"
val TITLE = "Nowa operacja kartą"
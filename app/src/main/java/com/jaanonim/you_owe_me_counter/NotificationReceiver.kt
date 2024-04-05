package com.jaanonim.you_owe_me_counter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import kotlinx.coroutines.runBlocking

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            val timestamp = intent.extras?.getLong("timestamp_yomc")
            val text = intent.extras?.getString("text_yomc")
            val tab = intent.extras?.getInt("tab_yomc")
            val split = text?.split(" PLN w ")
            val v = split!![0].replace(',', '.').toDouble()
            val name = split[1]

            if (remoteInput != null) {
                val title = remoteInput.getCharSequence(
                    KEY_TEXT_REPLY
                ).toString()
                Log.d("NotificationReceiver", title)
                with(NotificationManagerCompat.from(context)) {
                    cancel(NOTIFICATION_ID)
                }

                runBlocking {
                    context.notificationRecord.updateData { r ->
                        r.toBuilder().addNotifications(
                            Notification.newBuilder()
                                .setTitle(title)
                                .setText(name)
                                .setTimestamp(timestamp!!)
                                .setValue(v)
                                .setTab(tab!!)
                                .build()
                        ).build()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
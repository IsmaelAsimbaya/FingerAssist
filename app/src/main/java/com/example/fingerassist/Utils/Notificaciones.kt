package com.example.fingerassist.Utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fingerassist.R

class Notificaciones(val context: Context) : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID = "Recordatorio"
        const val CHANNEL_NAME = "Recordatorio"
        const val priority = NotificationManager.IMPORTANCE_DEFAULT
    }


    fun sendNotification(to: Intent, title: String, content: String, bigText: String) {

        val openNotification = to.let {
            it.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, openNotification, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
            )

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}
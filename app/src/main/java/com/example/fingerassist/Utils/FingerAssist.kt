package com.example.fingerassist.Utils

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

import com.example.fingerassist.Utils.Notificaciones.Companion.CHANNEL_ID
import com.example.fingerassist.Utils.Notificaciones.Companion.CHANNEL_NAME
import com.example.fingerassist.Utils.Notificaciones.Companion.priority
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class FingerAssist: Application() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        lateinit var sp: Preferences

        lateinit var db: FirebaseFirestore
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sp = Preferences(applicationContext)
        db = FirebaseFirestore.getInstance()
        addNotificationChannels()
    }

    fun addNotificationChannels() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            priority
        )
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }




}
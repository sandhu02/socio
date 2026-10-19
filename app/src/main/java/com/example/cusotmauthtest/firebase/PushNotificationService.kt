package com.example.cusotmauthtest.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.cusotmauthtest.MainActivity
import com.example.cusotmauthtest.R
import com.example.cusotmauthtest.retrofit.RegisterNotificationRequest
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import com.example.cusotmauthtest.retrofit.getUsername
import com.example.cusotmauthtest.retrofit.saveFcmToken
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New FCM token: $token")
        saveFcmToken(applicationContext, token)

        val username = getUsername(applicationContext)
        if (!username.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    getAuthApiInstance(applicationContext)?.registerNotification(
                        RegisterNotificationRequest(
                            username = username,
                            fcmtoken = token
                        )
                    )
                    Log.d("FCM", "Registered new token for $username with server")
                } catch (e: Exception) {
                    Log.e("FCM", "Failed to register new FCM token: ${e.message}")
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "From: ${message.from}")

        val title = message.notification?.title ?: message.data["title"] ?: "Socio"
        val body = message.notification?.body ?: message.data["body"] ?: "New notification"

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "socio_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Socio Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Chat and Socio app notifications"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
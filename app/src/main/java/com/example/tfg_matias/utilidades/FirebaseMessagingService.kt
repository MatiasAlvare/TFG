package com.example.tfg_matias.utilidades

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.tfg_matias.MainActivity
import com.example.tfg_matias.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("Nuevo token FCM: $token")
        // Aquí puedes guardar el token en Firestore si quieres hacerlo automáticamente
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"] ?: "Nuevo mensaje"
        val body = remoteMessage.data["body"] ?: "Tienes un nuevo mensaje."


        val chatId = remoteMessage.data["chatId"]
        val cocheId = remoteMessage.data["cocheId"]
        val sellerId = remoteMessage.data["sellerId"]

        // Mostrar notificación tú mismo SIEMPRE
        sendNotification(title, body, chatId, cocheId, sellerId)
    }


    private fun sendNotification(
        title: String,
        messageBody: String,
        chatId: String?,
        cocheId: String?,
        sellerId: String?
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            if (!chatId.isNullOrBlank() && !cocheId.isNullOrBlank() && !sellerId.isNullOrBlank()) {
                putExtra("chatId", chatId)
                putExtra("cocheId", cocheId)
                putExtra("sellerId", sellerId)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "default_channel_id"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notificacion) // usa tu icono real
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mensajes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para mensajes nuevos"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}

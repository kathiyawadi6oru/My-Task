package com.example.authentication.Service

import android.R.id.message
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.random.Random


class FirebaseMessageHandler : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        if (p0.data.isEmpty()){
            shownotification(p0.notification!!.title.toString(), p0.notification!!.body.toString())
        }else{
            shownotification(p0.data)
        }

    }
    fun shownotification(data: Map<String, String>){
        val  title:String=data.get("title").toString()
        val body:String=data.get("body").toString()

        val notificationManager:NotificationManager=
            getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager

        val NOTIFICATION_ID = "com.example.authentication.Service.Test"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationchannel:NotificationChannel =
                NotificationChannel(NOTIFICATION_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationchannel.description = "Task taking Application"
            notificationManager.createNotificationChannel(notificationchannel)

            val notificationBuilder:Notification.Builder =
                Notification.Builder(this, NOTIFICATION_ID)

            notificationBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")

            notificationManager.notify(0, notificationBuilder.build())

        }
    }

    fun shownotification(title: String, body: String){

        val notificationManager:NotificationManager=
            getSystemService(Context.NOTIFICATION_SERVICE)as NotificationManager

        val NOTIFICATION_ID = "com.example.authentication.Service.Test"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationchannel:NotificationChannel =
                NotificationChannel(NOTIFICATION_ID,
                    "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationchannel.description = "Task taking Application"
            notificationManager.createNotificationChannel(notificationchannel)

            val notificationBuilder:Notification.Builder =
                Notification.Builder(this, NOTIFICATION_ID)

            notificationBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")

                notificationManager.notify(Random.nextInt(), notificationBuilder.build())

        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

    }
}
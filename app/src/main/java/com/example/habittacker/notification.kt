package com.example.habittacker

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val notificationId = 1
const val notificationChannel = "channel"
const val titlemsg = "title"
const val message = "message"


class notification : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val notification = NotificationCompat.Builder(context!!, notificationChannel)
            .setContentTitle(intent?.getStringExtra(titlemsg))
            .setContentText(intent?.getStringExtra(message))
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}

class scheduleNotification(val context: Context, param: WorkerParameters) : Worker(context, param) {

    override fun doWork(): Result {
        val title = inputData.getString("forwhat") ?: "null"

        val sharedPreferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE)

        if (title == "walk") {
            if (sharedPreferences.getBoolean("isWalk", false)) {
                // Send walk notification
                sendNotification(context, "To Walk", "I hope you have completed 1000 steps")
            }
            sharedPreferences.edit().putBoolean("isWalkScheduled", false).apply()
        }




        if (title == "meditate") {
            if (sharedPreferences.getBoolean("ismeditate", false)) {

                sendNotification(context, "To Meditate", "I hope you have meditated today")
            }
            sharedPreferences.edit().putBoolean("isMeditateScheduled", false).apply()
        }


        if (title == "water") {
            if (sharedPreferences.getBoolean("isdrink", false)) {

                sendNotification(context, "To Drink Water", "It's time to drink water")
            }
            sharedPreferences.edit().putBoolean("isWaterScheduled", false).apply()
        }



        return Result.success()
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(titlemsg, title)
            putExtra(message, message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification = NotificationCompat.Builder(context, notificationChannel)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}

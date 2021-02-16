package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    applicationContext: Context,
    fileName: String,
    status: String,
) {

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra("filename", fileName)
    detailIntent.putExtra("status", status)
    val detailPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    ).setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_action),
            detailPendingIntent
        )
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)


    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
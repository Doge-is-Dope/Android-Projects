package com.udacity.project4.locationreminders.geofence

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import timber.log.Timber
import java.util.concurrent.TimeUnit

object GeofenceUtils {

    private const val GEOFENCE_RADIUS_IN_METERS = 100f
    private val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)

    const val EXTRA_REMINDER = "REMINDER"


    private fun getGeofencePendingIntent(
        context: Context,
        reminderDataItem: ReminderDataItem
    ): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.putExtra(EXTRA_REMINDER, reminderDataItem)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @SuppressLint("MissingPermission")
    fun addGeofence(context: Context, reminderDataItem: ReminderDataItem) {

        val geofence = Geofence.Builder()
            .setRequestId(reminderDataItem.id)
            .setCircularRegion(
                reminderDataItem.latitude!!.toDouble(),
                reminderDataItem.longitude!!.toDouble(),
                GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val pendingIntent = getGeofencePendingIntent(context, reminderDataItem)

        val geofencingClient = LocationServices.getGeofencingClient(context)


        geofencingClient.addGeofences(
            geofencingRequest,
            pendingIntent
        )
            .addOnSuccessListener {
                Timber.d("Success! id: ${geofence.requestId}")
            }
            .addOnFailureListener {
                Timber.e("Failure! id: ${context.getString(R.string.geofences_not_added)}")
            }
    }


}
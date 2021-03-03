package com.udacity.project4.locationreminders.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.R
import com.udacity.project4.locationreminders.geofence.GeofenceUtils.errorMessage
import com.udacity.project4.locationreminders.geofence.GeofencingConstants.ACTION_GEOFENCE_EVENT
import timber.log.Timber

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

//TODO: implement the onReceive method to receive the geofencing events at the background
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                val errorMessage = errorMessage(context, geofencingEvent.errorCode)
                Timber.e(errorMessage)
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Timber.v(context.getString(R.string.geofence_entered))
                val fenceId = when {
                    geofencingEvent.triggeringGeofences.isNotEmpty() ->
                        geofencingEvent.triggeringGeofences[0].requestId
                    else -> {
                        Timber.e("No Geofence Trigger Found! Abort mission!")
                        return
                    }
                }
//                val foundIndex = GeofencingConstants.LANDMARK_DATA.indexOfFirst {
//                    it.id == fenceId
//                }
//                if (-1 == foundIndex) {
//                    Log.e(TAG, "Unknown Geofence: Abort Mission")
//                    return
//                }
//                val notificationManager = ContextCompat.getSystemService(
//                    context,
//                    NotificationManager::class.java
//                ) as NotificationManager
//
//                notificationManager(
//                    context, foundIndex
//                )
            }
        }

    }
}
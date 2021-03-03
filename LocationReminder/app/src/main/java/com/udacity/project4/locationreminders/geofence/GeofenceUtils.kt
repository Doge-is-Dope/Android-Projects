package com.udacity.project4.locationreminders.geofence

import java.util.concurrent.TimeUnit

internal object GeofencingConstants {

    /**
     * Used to set an expiration time for a geofence. After this amount of time, Location services
     * stops tracking the geofence. For this sample, geofences expire after one hour.
     */
    val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.HOURS.toMillis(1)

    const val GEOFENCE_RADIUS_IN_METERS = 100f
    internal const val ACTION_GEOFENCE_EVENT = "com.udacity.project4.locationreminders.geofence.action.ACTION_GEOFENCE_EVENT"
    const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}
package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.geofence.GeofencingConstants
import com.udacity.project4.locationreminders.geofence.GeofencingConstants.ACTION_GEOFENCE_EVENT
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

    // A PendingIntent for the Broadcast Receiver that handles geofence transitions.
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.selectLocation.setOnClickListener {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                navigateToSelectLocationFragment()
            } else {
                requestForegroundPermissionForMyLocation()
            }
        }

        binding.saveReminder.setOnClickListener {
            val backgroundLocationPermission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
            if (checkPermission(backgroundLocationPermission)) {
                // the foreground location permission will be validated
                saveReminderAndStartGeofence()
            } else {
                if (shouldShowRequestPermissionRationale(backgroundLocationPermission)) {
                    showRationaleDialog()
                } else {
                    requestBackgroundPermissionForGeofence()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // Check if location permissions are granted
        when (requestCode) {
            REQUEST_FOREGROUND_PERMISSION_FOR_MY_LOCATION -> {
                if (grantResults.isNotEmpty() && hasAllPermissionGranted(grantResults)) {
                    navigateToSelectLocationFragment()
                } else {
                    _viewModel.showSnackBarInt.value = R.string.permission_denied_explanation
                }
                return
            }
            REQUEST_BACKGROUND_PERMISSION_FOR_GEOFENCE -> {
                if (grantResults.isNotEmpty() && hasAllPermissionGranted(grantResults)) {
                    saveReminderAndStartGeofence()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun hasAllPermissionGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForegroundPermissionForMyLocation() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_FOREGROUND_PERMISSION_FOR_MY_LOCATION
        )
    }

    @TargetApi(29)
    private fun requestBackgroundPermissionForGeofence() {
        if (runningQOrLater) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                REQUEST_BACKGROUND_PERMISSION_FOR_GEOFENCE
            )
        }
    }

    private fun navigateToSelectLocationFragment() {
        _viewModel.navigationCommand.value =
            NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
    }

    private fun saveReminderAndStartGeofence() {
        val title = _viewModel.reminderTitle.value
        val description = _viewModel.reminderDescription.value
        val location = _viewModel.reminderSelectedLocationStr.value
        val latitude = _viewModel.latitude.value
        val longitude = _viewModel.longitude.value


        val reminderDataItem =
            ReminderDataItem(title, description, location, latitude, longitude)
        val isSuccess = _viewModel.validateAndSaveReminder(reminderDataItem)

        // Start the geofence
        if (isSuccess) {
            addGeofence(requireContext(), reminderDataItem)
        }
    }

    /**
     * Shows rationale dialog for displaying why the app needs permission
     */
    private fun showRationaleDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.geofence_permission_rationale_title))
            .setMessage(getString(R.string.geofence_permission_rationale_message))
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                requestBackgroundPermissionForGeofence()
            }
        builder.create().show()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(context: Context, reminderDataItem: ReminderDataItem) {

        val geofence = Geofence.Builder()
            .setRequestId(reminderDataItem.id)
            .setCircularRegion(
                reminderDataItem.latitude!!.toDouble(),
                reminderDataItem.longitude!!.toDouble(),
                GeofencingConstants.GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(GeofencingConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencingClient = LocationServices.getGeofencingClient(context)


        geofencingClient.addGeofences(
            geofencingRequest,
            geofencePendingIntent
        )
            .addOnSuccessListener {
                Timber.d("Success! id: ${geofence.requestId}")
            }
            .addOnFailureListener {
                if (it.message != null) {
                    Timber.e("Failure! ${it.message!!}")
                }
            }
    }

    companion object {
        private const val REQUEST_FOREGROUND_PERMISSION_FOR_MY_LOCATION = 32
        private const val REQUEST_BACKGROUND_PERMISSION_FOR_GEOFENCE = 33

        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
    }
}

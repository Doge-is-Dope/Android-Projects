package com.udacity.project4.locationreminders.savereminder

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceUtils
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

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
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            ) {
                saveReminder()
            } else {
                requestForegroundAndBackgroundPermissions()
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
        if (requestCode == REQUEST_FOREGROUND_PERMISSION_FOR_MY_LOCATION) {
            if (grantResults.isNotEmpty() && hasAllPermissionGranted(grantResults)) {
                navigateToSelectLocationFragment()
            } else {
                _viewModel.showSnackBarInt.value = R.string.permission_denied_explanation
            }
        } else if (requestCode == REQUEST_FOREGROUND_PERMISSION || requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSIONS) {
            if (grantResults.isNotEmpty() && hasAllPermissionGranted(grantResults)) {
                saveReminder()
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
    private fun requestForegroundAndBackgroundPermissions() {
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSIONS
            }
            else -> REQUEST_FOREGROUND_PERMISSION
        }

        requestPermissions(
            permissionsArray,
            resultCode
        )
    }

    private fun navigateToSelectLocationFragment() {
        _viewModel.navigationCommand.value =
            NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
    }

    private fun saveReminder() {
        val title = _viewModel.reminderTitle.value
        val description = _viewModel.reminderDescription.value
        val location = _viewModel.reminderSelectedLocationStr.value
        val latitude = _viewModel.latitude.value
        val longitude = _viewModel.longitude.value


        val reminderDataItem =
            ReminderDataItem(title, description, location, latitude, longitude)
        val isSuccess = _viewModel.validateAndSaveReminder(reminderDataItem)
        if (isSuccess) {
            GeofenceUtils.addGeofence(requireContext(), reminderDataItem)
        }
    }

    companion object {
        private const val REQUEST_FOREGROUND_PERMISSION_FOR_MY_LOCATION = 32
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSIONS = 33
        private const val REQUEST_FOREGROUND_PERMISSION = 34
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29


    }
}

package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var checkedRadioButtonId = View.NO_ID
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
        )
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentMain.customButton.setOnClickListener {
            when (checkedRadioButtonId) {
                R.id.radio_button_loadapp -> download(
                    getString(R.string.radio_btn_loadapp),
                    LOADAPP_URL
                )
                R.id.radio_button_glide -> download(
                    getString(R.string.radio_btn_glide),
                    GLIDE_URL
                )
                R.id.radio_button_retrofit -> download(
                    getString(R.string.radio_btn_retrofit),
                    RETROFIT_URL
                )
                View.NO_ID -> Toast.makeText(
                    this,
                    getString(R.string.toast_select_file),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.contentMain.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            // Returns View.NO_ID if nothing is checked.
            checkedRadioButtonId = checkedId
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = id?.let { DownloadManager.Query().setFilterById(it) }
            val cursor = downloadManager.query(query)


            var fileName = ""
            var status = ""
            if (cursor.moveToFirst()) {
                fileName =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))

                when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        status = "Success"
                    }
                    DownloadManager.STATUS_FAILED -> {
                        status = "Fail"
                    }
                }


            }


            if (context != null) {
                val notificationManager =
                    ContextCompat.getSystemService(
                        context,
                        NotificationManager::class.java
                    ) as NotificationManager
                notificationManager.sendNotification(context, fileName, status)
            }
        }
    }

    private fun download(description: String, url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(description)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

                .apply {
                    enableVibration(true)
                    description = getString(R.string.notification_description)
                }

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"
    }

}

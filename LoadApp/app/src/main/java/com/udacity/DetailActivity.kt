package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)

        val fileName = intent.getStringExtra("filename")
        val status = intent.getStringExtra("status")


        binding.content.apply {
            textFileName.text = fileName
            textStatus.apply {
                if (status == "Success") {
                    setTextColor(
                        ContextCompat.getColor(
                            this@DetailActivity,
                            R.color.colorPrimaryDark
                        )
                    )
                }
                if (status == "Fail") {
                    setTextColor(Color.RED)
                }
                text = status
            }
        }


        val notificationManager =
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelNotifications()

    }

}

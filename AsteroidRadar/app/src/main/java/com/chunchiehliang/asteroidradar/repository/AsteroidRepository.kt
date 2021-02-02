package com.chunchiehliang.asteroidradar.repository

import com.chunchiehliang.asteroidradar.database.AsteroidDatabase
import com.chunchiehliang.asteroidradar.network.Network
import com.chunchiehliang.asteroidradar.utils.getSevenDaysLaterFormattedDate
import com.chunchiehliang.asteroidradar.utils.getTodayFormattedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await
import timber.log.Timber

class AsteroidRepository(private val database: AsteroidDatabase) {
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val startDate = getTodayFormattedDate()
            val endDate = getSevenDaysLaterFormattedDate()
            Timber.d("startDate: $startDate, endDate: $endDate")

            val asteroidList = Network.retrofitService.getAsteroids(startDate, endDate, "API_KEY").await()
            database.asteroidDao.insertAll(*asteroidList)
        }

    }
}
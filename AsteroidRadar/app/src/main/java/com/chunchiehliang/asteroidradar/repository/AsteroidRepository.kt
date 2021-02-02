package com.chunchiehliang.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.chunchiehliang.asteroidradar.BuildConfig
import com.chunchiehliang.asteroidradar.database.AsteroidDatabase
import com.chunchiehliang.asteroidradar.domain.Asteroid
import com.chunchiehliang.asteroidradar.domain.PictureOfDay
import com.chunchiehliang.asteroidradar.network.Network
import com.chunchiehliang.asteroidradar.network.parseAsteroidsJsonResult
import com.chunchiehliang.asteroidradar.utils.getSevenDaysLaterFormattedDate
import com.chunchiehliang.asteroidradar.utils.getTodayFormattedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import timber.log.Timber
import java.lang.Exception


const val API_KEY = BuildConfig.API_KEY

class AsteroidRepository(private val database: AsteroidDatabase) {

    /**
     * Retrieve asteroids from the database.
     */
    val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids()


    /**
     * Refresh the asteroids stored in the offline cache.
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val startDate = getTodayFormattedDate()
            val endDate = getSevenDaysLaterFormattedDate()
            Timber.d("startDate: $startDate, endDate: $endDate")
            try {
                val body = Network.retrofitService.getAsteroids(startDate, endDate, API_KEY).await()
                val asteroidList = parseAsteroidsJsonResult(JSONObject(body))
                database.asteroidDao.insertAll(asteroidList)
            } catch (e: Exception) {
                Timber.e("error: $e")
            }
        }
    }

    suspend fun getPictureOfDay(): PictureOfDay? =
        withContext(Dispatchers.Default) {
            try {
                val pictureOfDay = Network.retrofitService.getPictureOfDay(API_KEY)
                Timber.d("picture of day: $pictureOfDay")
                if (pictureOfDay.mediaType == "image") {
                    pictureOfDay
                } else {
                    null
                }
            } catch (e: Exception) {
                Timber.e("error: $e")
                null
            }
        }
}

package com.chunchiehliang.asteroidradar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chunchiehliang.asteroidradar.domain.Asteroid
import com.chunchiehliang.asteroidradar.domain.PictureOfDay
import com.chunchiehliang.asteroidradar.network.Network
import com.chunchiehliang.asteroidradar.network.parseAsteroidsJsonResult
import com.chunchiehliang.asteroidradar.utils.getSevenDaysLaterFormattedDate
import com.chunchiehliang.asteroidradar.utils.getTodayFormattedDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

const val API_KEY = "DEMO_KEY"

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        val startDate = getTodayFormattedDate()
        val endDate = getSevenDaysLaterFormattedDate()
        Timber.d("startDate: $startDate, endDate: $endDate")
        getAsteroids(startDate, endDate)
        getPictureOfDay()
    }

    private fun getAsteroids(startDate: String, endDate: String) {
        _status.value = AsteroidApiStatus.LOADING
        viewModelScope.launch {

            Network.retrofitService.getAsteroids(startDate, endDate, API_KEY)
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Timber.e("Failure: ${t.message}")
                        _status.value = AsteroidApiStatus.ERROR
                        _asteroidList.value = ArrayList()
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Timber.d("response: $response")
                        val body = response.body()
                        if (body != null) {
                            _asteroidList.value =
                                parseAsteroidsJsonResult(JSONObject(body))
                        }
                        _status.value = AsteroidApiStatus.DONE
                    }
                })
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                val pictureOfDay = Network.retrofitService.getPictureOfDay(API_KEY)
                Timber.d("picture of day: $pictureOfDay")
                if (pictureOfDay.mediaType == "image") {
                    _pictureOfDay.value = pictureOfDay
                } else {
                    _pictureOfDay.value = null
                }
            } catch (e: Exception) {
                Timber.e("error: $e")
                _pictureOfDay.value = null
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}
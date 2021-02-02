package com.chunchiehliang.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.chunchiehliang.asteroidradar.database.getDatabase
import com.chunchiehliang.asteroidradar.domain.Asteroid
import com.chunchiehliang.asteroidradar.domain.PictureOfDay
import com.chunchiehliang.asteroidradar.network.Network
import com.chunchiehliang.asteroidradar.network.parseAsteroidsJsonResult
import com.chunchiehliang.asteroidradar.repository.API_KEY
import com.chunchiehliang.asteroidradar.repository.AsteroidRepository
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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MediatorLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val database = getDatabase(application)

    private val asteroidRepository = AsteroidRepository(database)

    val asteroidList = asteroidRepository.asteroids

    init {
        displayPictureOfDay()

        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }
    }

    private fun displayPictureOfDay() {
        viewModelScope.launch {
            try {
                val pictureOfDay = asteroidRepository.getPictureOfDay()
                Timber.d("picture of day: $pictureOfDay")
                if (pictureOfDay?.mediaType == "image") {
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

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
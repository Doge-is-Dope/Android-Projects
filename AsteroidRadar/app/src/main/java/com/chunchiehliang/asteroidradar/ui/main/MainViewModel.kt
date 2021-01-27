package com.chunchiehliang.asteroidradar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chunchiehliang.asteroidradar.domain.Asteroid
import com.chunchiehliang.asteroidradar.network.Network
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        // todo: this is for testing
//        getAsteroidList()
        getAsteroids()
    }

    private fun getAsteroidList() {
        viewModelScope.launch {
            _status.value = AsteroidApiStatus.LOADING
            try {
//                delay(2000)
//                _asteroidList.value = Network.retrofitService.getAsteroids("2015-09-07", "2015-09-08", "DEMO_KEY")
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                Timber.e("Error: $e")
                _status.value = AsteroidApiStatus.ERROR
                _asteroidList.value = ArrayList()
            }
        }
    }

    private fun getAsteroids() {
        _response.value = "Loading..."
        viewModelScope.launch {

            Network.retrofitService.getAsteroids("2015-09-07", "2015-09-08", "DEMO_KEY")
                .enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        _response.value = "Failure: " + t.message
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        _response.value = response.body()
                    }
                })
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}
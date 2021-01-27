package com.chunchiehliang.asteroidradar.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chunchiehliang.asteroidradar.domain.Asteroid
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getAsteroidList()
    }

    private fun getAsteroidList() {
        viewModelScope.launch {
            _status.value = AsteroidApiStatus.LOADING
            try {
                delay(2000)
                _asteroidList.value =
                    listOf(
                        Asteroid(1, "abc", "2020-01-01", 23.1, 21.1, 1123.2, 50.6, false),
                        Asteroid(2, "cde", "2020-01-02", 1.1, 124.1, 58.5, 44.1, true)
                    )
                _status.value = AsteroidApiStatus.DONE
            } catch (e: Exception) {
                _status.value = AsteroidApiStatus.ERROR
                _asteroidList.value = ArrayList()
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
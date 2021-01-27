package com.chunchiehliang.asteroidradar.network

import com.chunchiehliang.asteroidradar.domain.Asteroid
import com.chunchiehliang.asteroidradar.utils.Constants.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface AsteroidApiService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") key: String,
    ): List<Asteroid>
}


object Network {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}
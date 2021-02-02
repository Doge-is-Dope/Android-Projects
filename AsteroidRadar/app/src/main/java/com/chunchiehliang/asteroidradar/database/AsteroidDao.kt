package com.chunchiehliang.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chunchiehliang.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDao {
    @Query("select * from asteroid")
    fun getAsteroids(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: Asteroid)
}
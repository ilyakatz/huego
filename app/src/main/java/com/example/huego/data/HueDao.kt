package com.example.huego.data

import androidx.room.*

@Dao
interface HueDao {
    @Query("SELECT * FROM credentials WHERE id = 1")
    suspend fun getCredentials(): HueCredentials?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCredentials(credentials: HueCredentials)

    @Query("DELETE FROM credentials")
    suspend fun clearCredentials()
} 
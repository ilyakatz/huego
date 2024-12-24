package com.example.huego.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HueCredentials::class], version = 1)
abstract class HueDatabase : RoomDatabase() {
    abstract fun hueDao(): HueDao

    companion object {
        @Volatile
        private var INSTANCE: HueDatabase? = null

        fun getDatabase(context: Context): HueDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HueDatabase::class.java,
                    "hue_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 
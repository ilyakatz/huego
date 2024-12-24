package com.example.huego.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HueRepository(context: Context) {
    private val hueDao = HueDatabase.getDatabase(context).hueDao()

    suspend fun getCredentials(): HueCredentials? = withContext(Dispatchers.IO) {
        hueDao.getCredentials()
    }

    suspend fun saveCredentials(bridgeIp: String, username: String, discoveryMethod: String) {
        withContext(Dispatchers.IO) {
            hueDao.saveCredentials(
                HueCredentials(
                    bridgeIp = bridgeIp,
                    username = username,
                    discoveryMethod = discoveryMethod
                )
            )
        }
    }

    suspend fun clearCredentials() = withContext(Dispatchers.IO) {
        hueDao.clearCredentials()
    }
} 
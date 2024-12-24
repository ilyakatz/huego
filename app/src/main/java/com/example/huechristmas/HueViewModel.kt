package com.example.huechristmas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class HueViewModel : ViewModel() {
    private val client = OkHttpClient()
    private var colorCycleJob: Job? = null
    private var bridgeIp: String? = null
    private var username: String? = null
    
    var isConnected by mutableStateOf(false)
        private set

    init {
        discoverBridge()
    }

    private fun discoverBridge() {
        val request = Request.Builder()
            .url("https://discovery.meethue.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                isConnected = false
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        val jsonArray = org.json.JSONArray(body)
                        if (jsonArray.length() > 0) {
                            bridgeIp = jsonArray.getJSONObject(0).getString("internalipaddress")
                            createUser()
                        }
                    } catch (e: Exception) {
                        isConnected = false
                    }
                }
            }
        })
    }

    private fun createUser() {
        val json = JSONObject().apply {
            put("devicetype", "christmas_app#android")
        }

        val request = Request.Builder()
            .url("http://$bridgeIp/api")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                isConnected = false
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        val jsonArray = org.json.JSONArray(body)
                        val responseObj = jsonArray.getJSONObject(0)
                        if (responseObj.has("success")) {
                            username = responseObj.getJSONObject("success").getString("username")
                            isConnected = true
                        } else if (responseObj.has("error")) {
                            // Need to press link button - retry after delay
                            viewModelScope.launch {
                                delay(1000)
                                createUser()
                            }
                        }
                    } catch (e: Exception) {
                        isConnected = false
                    }
                }
            }
        })
    }

    fun startColorCycle() {
        colorCycleJob = viewModelScope.launch {
            val christmasColors = listOf(
                mapOf("xy" to listOf(0.675f, 0.322f)), // Red
                mapOf("xy" to listOf(0.409f, 0.518f))  // Green
            )
            
            while (isActive) {
                christmasColors.forEach { color ->
                    setLightState(color)
                    delay(2000) // Wait 2 seconds before next color
                }
            }
        }
    }

    private suspend fun setLightState(state: Map<String, Any>) {
        bridgeIp?.let { ip ->
            username?.let { user ->
                // Get all lights
                val lightsRequest = Request.Builder()
                    .url("http://$ip/api/$user/lights")
                    .build()

                try {
                    client.newCall(lightsRequest).execute().use { response ->
                        val lights = JSONObject(response.body?.string() ?: "{}")
                        
                        // Update each Hue Go light
                        lights.keys().forEach { lightId ->
                            val light = lights.getJSONObject(lightId)
                            if (light.getString("modelid").contains("Go", ignoreCase = true)) {
                                val stateJson = JSONObject(state).apply {
                                    put("on", true)
                                    put("bri", 254)
                                }

                                val request = Request.Builder()
                                    .url("http://$ip/api/$user/lights/$lightId/state")
                                    .put(stateJson.toString().toRequestBody("application/json".toMediaType()))
                                    .build()

                                client.newCall(request).execute()
                            }
                        }
                    }
                } catch (e: Exception) {
                    isConnected = false
                }
            }
        }
    }

    fun stopColorCycle() {
        colorCycleJob?.cancel()
        colorCycleJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopColorCycle()
        client.dispatcher.executorService.shutdown()
    }
} 
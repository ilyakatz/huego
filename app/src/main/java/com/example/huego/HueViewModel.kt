package com.example.huego

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huego.data.HuePreferences
import com.example.huego.discovery.BridgeDiscovery
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class HueViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "HueViewModel"
    }

    private val prefs = HuePreferences(application)
    private val client = OkHttpClient()
    private var colorCycleJob: Job? = null
    private var bridgeIp: String? = null
    private var username: String? = null
    
    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Discovering)
        private set

    sealed class ConnectionState {
        object Discovering : ConnectionState()
        object WaitingForButton : ConnectionState()
        object Connected : ConnectionState()
        object Failed : ConnectionState()
        object RateLimited : ConnectionState()
    }

    private val bridgeDiscovery = BridgeDiscovery(application)

    init {
        // Try to use cached credentials first
        val savedIp = prefs.bridgeIp
        val savedUsername = prefs.username
        
        if (savedIp != null && savedUsername != null) {
            bridgeIp = savedIp
            username = savedUsername
            connectionState = ConnectionState.Connected
            Log.d(TAG, "Using cached bridge credentials")
        } else {
            discoverBridge()
        }
    }

    private fun discoverBridge() {
        connectionState = ConnectionState.Discovering
        viewModelScope.launch {
            try {
                // Try mDNS first with a timeout
                val bridgeIpFromMdns = withTimeoutOrNull(5000) { // 5 second timeout
                    bridgeDiscovery.discoverBridge()
                }
                
                if (bridgeIpFromMdns != null) {
                    Log.d(TAG, "Bridge found via mDNS: $bridgeIpFromMdns")
                    bridgeIp = bridgeIpFromMdns
                    prefs.bridgeIp = bridgeIp
                    createUser()
                } else {
                    // Fall back to meethue.com discovery
                    Log.d(TAG, "mDNS discovery failed, trying meethue.com")
                    discoverBridgeViaCloud()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during bridge discovery", e)
                discoverBridgeViaCloud()
            }
        }
    }

    private fun discoverBridgeViaCloud() {
        val request = Request.Builder()
            .url("https://discovery.meethue.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to discover bridge", e)
                connectionState = ConnectionState.Failed
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 429) {
                    Log.w(TAG, "Rate limited by discovery service")
                    connectionState = ConnectionState.RateLimited
                    return
                }
                
                response.body?.string()?.let { body ->
                    try {
                        Log.d(TAG, "Discovery response: $body")
                        val jsonArray = org.json.JSONArray(body)
                        if (jsonArray.length() > 0) {
                            bridgeIp = jsonArray.getJSONObject(0).getString("internalipaddress")
                            prefs.bridgeIp = bridgeIp  // Cache the IP
                            createUser()
                        } else {
                            connectionState = ConnectionState.Failed
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing discovery response", e)
                        connectionState = ConnectionState.Failed
                    }
                }
            }
        })
    }

    private fun createUser() {
        connectionState = ConnectionState.WaitingForButton
        val json = JSONObject().apply {
            put("devicetype", "christmas_app#android")
        }

        val request = Request.Builder()
            .url("http://$bridgeIp/api")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to connect: ${e.message}", e)
                connectionState = ConnectionState.Failed
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { body ->
                    try {
                        Log.d(TAG, "Bridge response: $body")
                        val jsonArray = org.json.JSONArray(body)
                        val responseObj = jsonArray.getJSONObject(0)
                        if (responseObj.has("success")) {
                            username = responseObj.getJSONObject("success").getString("username")
                            prefs.username = username  // Cache the username
                            Log.i(TAG, "Successfully connected to bridge")
                            connectionState = ConnectionState.Connected
                        } else if (responseObj.has("error")) {
                            val error = responseObj.getJSONObject("error")
                            Log.w(TAG, "Error from bridge: ${error.getString("description")}")
                            connectionState = ConnectionState.Failed
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception parsing response: ${e.message}", e)
                        connectionState = ConnectionState.Failed
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
                Log.d(TAG, "Attempting to set light state. IP: $ip, Username: $user")
                // Get all lights
                val lightsRequest = Request.Builder()
                    .url("http://$ip/api/$user/lights")
                    .build()

                try {
                    client.newCall(lightsRequest).execute().use { response ->
                        val responseBody = response.body?.string() ?: "{}"
                        Log.d(TAG, "Lights response: $responseBody")
                        val lights = JSONObject(responseBody)
                        
                        // Update each Hue Go light
                        var foundGoLight = false
                        lights.keys().forEach { lightId ->
                            val light = lights.getJSONObject(lightId)
                            val modelId = light.getString("modelid")
                            Log.d(TAG, "Found light $lightId with model: $modelId")
                            
                            if (modelId.contains("Go", ignoreCase = true)) {
                                foundGoLight = true
                                val stateJson = JSONObject(state).apply {
                                    put("on", true)
                                    put("bri", 254)
                                }
                                Log.d(TAG, "Setting state for light $lightId: $stateJson")

                                val request = Request.Builder()
                                    .url("http://$ip/api/$user/lights/$lightId/state")
                                    .put(stateJson.toString().toRequestBody("application/json".toMediaType()))
                                    .build()

                                client.newCall(request).execute().use { stateResponse ->
                                    val stateResponseBody = stateResponse.body?.string()
                                    Log.d(TAG, "State update response: $stateResponseBody")
                                }
                            }
                        }
                        
                        if (!foundGoLight) {
                            Log.w(TAG, "No Hue Go lights found!")
                            connectionState = ConnectionState.Failed
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting light state", e)
                    connectionState = ConnectionState.Failed
                }
            } ?: run {
                Log.e(TAG, "Username is null!")
                connectionState = ConnectionState.Failed
            }
        } ?: run {
            Log.e(TAG, "Bridge IP is null!")
            connectionState = ConnectionState.Failed
        }
    }

    fun stopColorCycle() {
        colorCycleJob?.cancel()
        colorCycleJob = null
    }

    fun retryConnection() {
        // Clear cached credentials on manual retry
        prefs.clear()
        discoverBridge()
    }

    override fun onCleared() {
        super.onCleared()
        stopColorCycle()
        client.dispatcher.executorService.shutdown()
    }
} 
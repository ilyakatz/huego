package com.example.huego

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huego.data.HueRepository
import com.example.huego.discovery.BridgeDiscovery
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import com.example.huego.model.ColorScheme
import com.example.huego.model.HueLight

class HueViewModel(
    application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "HueViewModel"
    }

    private val repository = HueRepository(application)
    private val client = OkHttpClient()
    private var colorCycleJob: Job? = null
    private var bridgeIp: String? = null
    private var username: String? = null
    
    var connectionState by mutableStateOf<ConnectionState>(ConnectionState.Discovering)
        private set

    sealed class ConnectionState {
        object Discovering : ConnectionState()
        object WaitingForButton : ConnectionState()
        object ButtonPressed : ConnectionState()
        object Connected : ConnectionState()
        object Failed : ConnectionState()
        object RateLimited : ConnectionState()
    }

    private val bridgeDiscovery = BridgeDiscovery(application)

    var currentScheme by mutableStateOf<ColorScheme?>(null)
        private set
    
    private val christmasColors = listOf(
        mapOf("xy" to listOf(0.675f, 0.322f)), // Red
        mapOf("xy" to listOf(0.409f, 0.518f))  // Green
    )
    
    private val hanukkahColors = listOf(
        mapOf("xy" to listOf(0.167f, 0.040f)), // Blue
        mapOf("xy" to listOf(0.313f, 0.328f))  // White
    )

    private var _availableLights = mutableStateOf<List<HueLight>>(emptyList())
    val availableLights: List<HueLight> get() = _availableLights.value

    init {
        viewModelScope.launch {
            val credentials = repository.getCredentials()
            if (credentials != null) {
                Log.d(TAG, "Found cached credentials - IP: ${credentials.bridgeIp}, Username: ${credentials.username}")
                bridgeIp = credentials.bridgeIp
                username = credentials.username
                // Verify the connection works with cached credentials
                try {
                    withContext(Dispatchers.IO) {
                        val request = Request.Builder()
                            .url("http://${credentials.bridgeIp}/api/${credentials.username}/config")
                            .build()
                        
                        client.newCall(request).execute().use { response ->
                            if (response.isSuccessful) {
                                Log.d(TAG, "Successfully verified cached credentials")
                                onConnectionEstablished()
                            } else {
                                Log.w(TAG, "Cached credentials failed, starting fresh discovery")
                                repository.clearCredentials()
                                discoverBridge()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error verifying cached credentials", e)
                    repository.clearCredentials()
                    discoverBridge()
                }
            } else {
                Log.d(TAG, "No cached credentials found")
                discoverBridge()
            }
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
                            viewModelScope.launch {
                                repository.saveCredentials(
                                    bridgeIp = bridgeIp!!,
                                    username = username!!,
                                    discoveryMethod = "manual"
                                )
                            }
                            onConnectionEstablished()
                        } else if (responseObj.has("error")) {
                            val error = responseObj.getJSONObject("error")
                            Log.w(TAG, "Error from bridge: ${error.getString("description")}")
                            connectionState = ConnectionState.WaitingForButton
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception parsing response: ${e.message}", e)
                        connectionState = ConnectionState.Failed
                    }
                }
            }
        })
    }

    fun startColorCycle(scheme: ColorScheme) {
        currentScheme = scheme
        colorCycleJob = viewModelScope.launch {
            val colors = when (scheme) {
                ColorScheme.CHRISTMAS -> christmasColors
                ColorScheme.HANUKKAH -> hanukkahColors
            }
            
            while (isActive) {
                colors.forEach { color ->
                    setLightState(color)
                    delay(2000)
                }
            }
        }
    }

    private suspend fun setLightState(state: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            bridgeIp?.let { ip ->
                username?.let { user ->
                    availableLights.filter { it.isSelected }.forEach { light ->
                        try {
                            val stateJson = JSONObject(state)
                            // Only add brightness and on=true for color states
                            if (state.containsKey("xy")) {
                                stateJson.put("on", true)
                                stateJson.put("bri", 254)
                            }

                            val request = Request.Builder()
                                .url("http://$ip/api/$user/lights/${light.id}/state")
                                .put(stateJson.toString().toRequestBody("application/json".toMediaType()))
                                .build()

                            client.newCall(request).execute().use { stateResponse ->
                                val stateResponseBody = stateResponse.body?.string()
                                Log.d(TAG, "State update response for light ${light.name}: $stateResponseBody")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error setting state for light ${light.name}", e)
                        }
                    }
                }
            }
        }
    }

    fun stopColorCycle() {
        colorCycleJob?.cancel()
        colorCycleJob = null
        currentScheme = null
    }

    fun retryConnection() {
        viewModelScope.launch {
            repository.clearCredentials()
            discoverBridge()
        }
    }

    fun onButtonPressed() {
        connectionState = ConnectionState.ButtonPressed
        createUser()
    }

    override fun onCleared() {
        super.onCleared()
        stopColorCycle()
        client.dispatcher.executorService.shutdown()
    }

    private suspend fun fetchLights() {
        withContext(Dispatchers.IO) {
            bridgeIp?.let { ip ->
                username?.let { user ->
                    try {
                        val request = Request.Builder()
                            .url("http://$ip/api/$user/lights")
                            .build()

                        client.newCall(request).execute().use { response ->
                            val responseBody = response.body?.string() ?: "{}"
                            Log.d(TAG, "Lights response: $responseBody")
                            val lights = JSONObject(responseBody)
                            
                            val lightsList = mutableListOf<HueLight>()
                            lights.keys().forEach { lightId ->
                                val light = lights.getJSONObject(lightId)
                                lightsList.add(
                                    HueLight(
                                        id = lightId,
                                        name = light.getString("name"),
                                        productName = light.getString("productname")
                                    )
                                )
                            }
                            _availableLights.value = lightsList
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching lights", e)
                    }
                }
            }
        }
    }

    fun toggleLightSelection(lightId: String) {
        _availableLights.value = _availableLights.value.map { light ->
            if (light.id == lightId) light.copy(isSelected = !light.isSelected)
            else light
        }
    }

    private fun onConnectionEstablished() {
        connectionState = ConnectionState.Connected
        viewModelScope.launch {
            fetchLights()
        }
    }

    fun turnOffLights() {
        viewModelScope.launch {
            val offState = mapOf("on" to false)
            setLightState(offState)
        }
    }
} 
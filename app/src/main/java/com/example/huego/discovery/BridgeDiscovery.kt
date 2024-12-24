package com.example.huego.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BridgeDiscovery(private val context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_hue._tcp."

    companion object {
        private const val TAG = "BridgeDiscovery"
    }

    suspend fun discoverBridge(): String? = suspendCancellableCoroutine { continuation ->
        var currentDiscoveryListener: NsdManager.DiscoveryListener? = null
        
        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                Log.d(TAG, "Discovery started")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                Log.d(TAG, "Service found: ${serviceInfo.serviceName}")
                if (serviceInfo.serviceName.contains("Philips hue", ignoreCase = true)) {
                    val resolveListener = object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                            Log.e(TAG, "Resolve failed: $errorCode")
                        }

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            Log.d(TAG, "Service resolved: ${serviceInfo.host.hostAddress}")
                            currentDiscoveryListener?.let { listener ->
                                nsdManager.stopServiceDiscovery(listener)
                            }
                            continuation.resume(serviceInfo.host.hostAddress)
                        }
                    }
                    nsdManager.resolveService(serviceInfo, resolveListener)
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.d(TAG, "Service lost: ${serviceInfo.serviceName}")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.d(TAG, "Discovery stopped")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed to start: $errorCode")
                continuation.resume(null)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed to stop: $errorCode")
            }
        }

        currentDiscoveryListener = discoveryListener

        continuation.invokeOnCancellation {
            try {
                currentDiscoveryListener?.let { listener ->
                    nsdManager.stopServiceDiscovery(listener)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping discovery", e)
            }
        }

        try {
            nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting discovery", e)
            continuation.resume(null)
        }
    }
} 
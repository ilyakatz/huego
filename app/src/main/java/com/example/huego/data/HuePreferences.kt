package com.example.huego.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class HuePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var bridgeIp: String?
        get() = prefs.getString(KEY_BRIDGE_IP, null)
        set(value) = prefs.edit { putString(KEY_BRIDGE_IP, value) }

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) = prefs.edit { putString(KEY_USERNAME, value) }

    fun clear() {
        prefs.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "hue_preferences"
        private const val KEY_BRIDGE_IP = "bridge_ip"
        private const val KEY_USERNAME = "username"
    }
} 
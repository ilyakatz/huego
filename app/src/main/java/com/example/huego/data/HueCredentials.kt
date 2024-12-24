package com.example.huego.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credentials")
data class HueCredentials(
    @PrimaryKey
    val id: Int = 1,  // We'll only ever have one set of credentials
    val bridgeIp: String,
    val username: String,
    val discoveryMethod: String,
    val selectedLightIds: String = ""  // Comma-separated list of selected light IDs
) 
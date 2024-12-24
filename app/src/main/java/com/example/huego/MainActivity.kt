package com.example.huego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.huego.ui.ChristmasLightScreen
import com.example.huego.ui.theme.HueGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HueGoTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ChristmasLightScreen()
                }
            }
        }
    }
} 
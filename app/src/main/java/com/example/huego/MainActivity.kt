package com.example.huego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.huego.ui.ChristmasLightScreen
import com.example.huego.ui.theme.HueGoTheme
import com.example.huego.HueViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HueViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HueGoTheme {
                ChristmasLightScreen()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Turn off lights when app goes to background
        viewModel.turnOffLights()
    }
} 
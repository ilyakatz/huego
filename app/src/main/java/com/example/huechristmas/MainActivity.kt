package com.example.huechristmas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChristmasLightScreen()
                }
            }
        }
    }
}

@Composable
fun ChristmasLightScreen(
    modifier: Modifier = Modifier,
    viewModel: HueViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var isRunning by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                isRunning = !isRunning
                if (isRunning) {
                    viewModel.startColorCycle()
                } else {
                    viewModel.stopColorCycle()
                }
            }
        ) {
            Text(if (isRunning) "Stop" else "Start Christmas Colors")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (viewModel.isConnected) "Connected to Bridge" else "Not Connected",
            style = MaterialTheme.typography.bodyLarge
        )
    }
} 
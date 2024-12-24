package com.example.huechristmas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
        when (viewModel.connectionState) {
            is HueViewModel.ConnectionState.Discovering -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Discovering Hue Bridge...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            is HueViewModel.ConnectionState.WaitingForButton -> {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = "Press button",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Press the link button on your Hue Bridge",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            is HueViewModel.ConnectionState.Connected -> {
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
            }

            is HueViewModel.ConnectionState.Failed -> {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Failed to connect to Hue Bridge",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.retryConnection() }) {
                    Text("Retry")
                }
            }

            is HueViewModel.ConnectionState.RateLimited -> {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Rate Limited",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Too many requests to discovery service.\nPlease wait a few minutes before trying again.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.retryConnection() }) {
                    Text("Retry")
                }
            }
        }
    }
} 
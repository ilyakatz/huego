package com.example.huechristmas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.huechristmas.HueViewModel
import com.example.huechristmas.ui.components.*

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
            is HueViewModel.ConnectionState.Discovering -> DiscoveringState()
            is HueViewModel.ConnectionState.WaitingForButton -> WaitingForButtonState()
            is HueViewModel.ConnectionState.Connected -> ConnectedState(
                isRunning = isRunning,
                onToggle = { running ->
                    isRunning = running
                    if (running) viewModel.startColorCycle() else viewModel.stopColorCycle()
                }
            )
            is HueViewModel.ConnectionState.Failed -> FailedState(
                onRetry = viewModel::retryConnection
            )
            is HueViewModel.ConnectionState.RateLimited -> RateLimitedState(
                onRetry = viewModel::retryConnection
            )
        }
    }
} 
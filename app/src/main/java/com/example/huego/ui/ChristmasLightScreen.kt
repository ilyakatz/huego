package com.example.huego.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.huego.HueViewModel
import com.example.huego.ui.components.*
import androidx.lifecycle.ViewModelProvider
import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChristmasLightScreen(
    modifier: Modifier = Modifier,
    viewModel: HueViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(LocalContext.current.applicationContext as Application)
    )
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
            is HueViewModel.ConnectionState.WaitingForButton -> WaitingForButtonState(
                onButtonPressed = viewModel::onButtonPressed
            )
            is HueViewModel.ConnectionState.ButtonPressed -> ButtonPressedState()
            is HueViewModel.ConnectionState.Connected -> ConnectedState(
                isRunning = isRunning,
                selectedScheme = viewModel.currentScheme,
                availableLights = viewModel.availableLights,
                onLightToggled = viewModel::toggleLightSelection,
                onSchemeSelected = { scheme ->
                    isRunning = true
                    viewModel.startColorCycle(scheme)
                },
                onStop = {
                    isRunning = false
                    viewModel.stopColorCycle()
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
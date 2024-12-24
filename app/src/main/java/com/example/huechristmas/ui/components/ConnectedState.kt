package com.example.huechristmas.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ConnectedState(
    isRunning: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Button(
        onClick = { onToggle(!isRunning) }
    ) {
        Text(if (isRunning) "Stop" else "Start Christmas Colors")
    }
} 
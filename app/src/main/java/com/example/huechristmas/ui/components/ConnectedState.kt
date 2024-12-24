package com.example.huechristmas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

@Preview(showBackground = true)
@Composable
private fun ConnectedStatePreview() {
    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            ConnectedState(
                isRunning = false,
                onToggle = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectedStateRunningPreview() {
    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            ConnectedState(
                isRunning = true,
                onToggle = {}
            )
        }
    }
} 
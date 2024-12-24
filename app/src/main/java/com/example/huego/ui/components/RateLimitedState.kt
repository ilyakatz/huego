package com.example.huego.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun RateLimitedState(
    onRetry: () -> Unit
) {
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
    Button(onClick = onRetry) {
        Text("Retry")
    }
}

@Preview(showBackground = true)
@Composable
private fun RateLimitedStatePreview() {
    MaterialTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            RateLimitedState(
                onRetry = {}
            )
        }
    }
} 
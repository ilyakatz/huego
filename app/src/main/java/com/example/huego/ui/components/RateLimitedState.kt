package com.example.huego.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huego.ui.theme.HueGoTheme

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
        text = "The Hue discovery service can only be queried once every 15 minutes.\n\nPlease wait before trying again.",
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
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RateLimitedStatePreview() {
    HueGoTheme {
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
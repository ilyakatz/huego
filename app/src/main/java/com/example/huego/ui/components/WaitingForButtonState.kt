package com.example.huego.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import com.example.huego.ui.theme.HueGoTheme

@Composable
fun WaitingForButtonState(
    onButtonPressed: () -> Unit
) {
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
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onButtonPressed) {
        Text("I pressed the button")
    }
}

@Preview(showBackground = true)
@Composable
private fun WaitingForButtonStatePreview() {
    HueGoTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            WaitingForButtonState(
                onButtonPressed = {} // Empty lambda for preview
            )
        }
    }
} 
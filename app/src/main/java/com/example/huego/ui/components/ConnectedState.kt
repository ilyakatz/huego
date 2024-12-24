package com.example.huego.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.huego.model.ColorScheme
import android.content.res.Configuration
import com.example.huego.ui.theme.HueGoTheme

@Composable
fun ConnectedState(
    isRunning: Boolean,
    selectedScheme: ColorScheme?,
    onSchemeSelected: (ColorScheme) -> Unit,
    onStop: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isRunning) {
            Text(
                text = "Select a holiday light scheme:",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Button(
                onClick = { onSchemeSelected(ColorScheme.CHRISTMAS) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Christmas Lights")
            }
            
            Button(
                onClick = { onSchemeSelected(ColorScheme.HANUKKAH) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Hanukkah Lights")
            }
        } else {
            Text(
                text = when (selectedScheme) {
                    ColorScheme.CHRISTMAS -> "Running Christmas lights"
                    ColorScheme.HANUKKAH -> "Running Hanukkah lights"
                    null -> "Running lights"
                },
                style = MaterialTheme.typography.bodyLarge
            )
            
            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Stop")
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun ConnectedStatePreview() {
    HueGoTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            ConnectedState(
                isRunning = false,
                selectedScheme = null,
                onSchemeSelected = {},
                onStop = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Light Mode Running")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode Running"
)
@Composable
private fun ConnectedStateRunningPreview() {
    HueGoTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            ConnectedState(
                isRunning = true,
                selectedScheme = ColorScheme.CHRISTMAS,
                onSchemeSelected = {},
                onStop = {}
            )
        }
    }
} 
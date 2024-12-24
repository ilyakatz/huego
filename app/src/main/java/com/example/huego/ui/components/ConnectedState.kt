package com.example.huego.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import com.example.huego.model.HueLight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb  // Regular bulb
import androidx.compose.material.icons.filled.WbIncandescent  // Ambient light
import androidx.compose.material.icons.filled.Light  // Strip light
import androidx.compose.material.icons.filled.LightMode  // Go light
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Star

@Composable
private fun getLightIcon(productName: String): ImageVector {
    return when {
        productName.contains("go", ignoreCase = true) -> Icons.Default.LightMode
        productName.contains("strip", ignoreCase = true) -> Icons.Default.Light
        productName.contains("bloom", ignoreCase = true) || 
        productName.contains("iris", ignoreCase = true) -> Icons.Default.WbIncandescent
        else -> Icons.Default.Lightbulb
    }
}

@Composable
fun ConnectedState(
    isRunning: Boolean,
    selectedScheme: ColorScheme?,
    availableLights: List<HueLight>,
    onLightToggled: (String) -> Unit,
    onSchemeSelected: (ColorScheme) -> Unit,
    onStop: () -> Unit,
    onTurnOff: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Available Lights:",
            style = MaterialTheme.typography.titleMedium
        )

        availableLights.forEach { light ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getLightIcon(light.productName),
                        contentDescription = "Light type",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(light.name)
                        Text(
                            light.productName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Checkbox(
                    checked = light.isSelected,
                    onCheckedChange = { onLightToggled(light.id) }
                )
            }
        }

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
                Icon(
                    imageVector = Icons.Default.Park,
                    contentDescription = "Christmas Tree"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Christmas Lights")
            }
            
            Button(
                onClick = { onSchemeSelected(ColorScheme.HANUKKAH) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star of David"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hanukkah Lights")
            }

            Button(
                onClick = onTurnOff,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Turn Off"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Turn Off Lights")
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
    val sampleLights = listOf(
        HueLight("1", "Living Room Go", "Hue Go", false),
        HueLight("2", "Kitchen Strip", "Hue Strip", false),
        HueLight("3", "Bedroom Bulb", "Hue Bulb", true)
    )

    HueGoTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            ConnectedState(
                isRunning = false,
                selectedScheme = null,
                availableLights = sampleLights,
                onLightToggled = {},
                onSchemeSelected = {},
                onStop = {},
                onTurnOff = {}
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
                availableLights = emptyList(),
                onLightToggled = {},
                onSchemeSelected = {},
                onStop = {},
                onTurnOff = {}
            )
        }
    }
} 
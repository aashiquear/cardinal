package com.cardinal.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cardinal.app.ui.theme.SurfaceDark
import com.cardinal.app.ui.theme.WhitePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    useMetricUnits: Boolean,
    onToggleUnits: (Boolean) -> Unit,
    darkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    drivenRoadMemory: Boolean,
    onToggleDrivenRoadMemory: (Boolean) -> Unit,
    speedLimitAlerts: Boolean,
    onToggleSpeedLimitAlerts: (Boolean) -> Unit,
    voiceGuidance: Boolean,
    onToggleVoiceGuidance: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = WhitePrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WhitePrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Units",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            SettingItem(
                title = if (useMetricUnits) "Metric (km / h)" else "US (mi / h)",
                checked = useMetricUnits,
                onCheckedChange = onToggleUnits
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Navigation",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            SettingItem(
                title = "Voice guidance",
                checked = voiceGuidance,
                onCheckedChange = onToggleVoiceGuidance
            )
            SettingItem(
                title = "Driven-road memory",
                checked = drivenRoadMemory,
                onCheckedChange = onToggleDrivenRoadMemory
            )
            SettingItem(
                title = "Speed limit alerts",
                checked = speedLimitAlerts,
                onCheckedChange = onToggleSpeedLimitAlerts
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Display",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            SettingItem(
                title = "Dark mode",
                checked = darkMode,
                onCheckedChange = onToggleDarkMode
            )
            SettingItem(title = "High contrast")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Data Sources",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            SettingItem(title = "Traffic provider")
            SettingItem(title = "Clear driven-road history")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "Cardinal v1.0.0\nNavigate smarter, drive freely.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = onCheckedChange != null) {
                if (onCheckedChange != null && checked != null) {
                    onCheckedChange(!checked)
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
        if (checked != null && onCheckedChange != null) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

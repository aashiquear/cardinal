package com.cardinal.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardinal.app.prefs.SettingsPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPrefs: SettingsPrefs
) : ViewModel() {

    val useMetricUnits: StateFlow<Boolean> = settingsPrefs.useMetricUnits
    val darkMode: StateFlow<Boolean> = settingsPrefs.darkMode
    val drivenRoadMemoryEnabled: StateFlow<Boolean> = settingsPrefs.drivenRoadMemoryEnabled
    val speedLimitAlertsEnabled: StateFlow<Boolean> = settingsPrefs.speedLimitAlertsEnabled
    val voiceGuidanceEnabled: StateFlow<Boolean> = settingsPrefs.voiceGuidanceEnabled

    fun setMetric(value: Boolean) {
        settingsPrefs.setUseMetric(value)
    }

    fun setDarkMode(value: Boolean) {
        settingsPrefs.setDarkMode(value)
    }

    fun setDrivenRoadMemory(value: Boolean) {
        settingsPrefs.setDrivenRoadMemory(value)
    }

    fun setSpeedLimitAlerts(value: Boolean) {
        settingsPrefs.setSpeedLimitAlerts(value)
    }

    fun setVoiceGuidance(value: Boolean) {
        settingsPrefs.setVoiceGuidance(value)
    }
}

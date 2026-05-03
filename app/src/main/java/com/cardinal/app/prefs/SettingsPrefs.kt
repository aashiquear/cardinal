package com.cardinal.app.prefs

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _useMetricUnits = MutableStateFlow(getBoolean(KEY_USE_METRIC, false))
    val useMetricUnits: StateFlow<Boolean> = _useMetricUnits.asStateFlow()

    private val _drivenRoadMemoryEnabled = MutableStateFlow(getBoolean(KEY_DRIVEN_ROAD_MEMORY, false))
    val drivenRoadMemoryEnabled: StateFlow<Boolean> = _drivenRoadMemoryEnabled.asStateFlow()

    private val _speedLimitAlertsEnabled = MutableStateFlow(getBoolean(KEY_SPEED_LIMIT_ALERTS, false))
    val speedLimitAlertsEnabled: StateFlow<Boolean> = _speedLimitAlertsEnabled.asStateFlow()

    private val _voiceGuidanceEnabled = MutableStateFlow(getBoolean(KEY_VOICE_GUIDANCE, false))
    val voiceGuidanceEnabled: StateFlow<Boolean> = _voiceGuidanceEnabled.asStateFlow()

    private val _darkMode = MutableStateFlow(getBoolean(KEY_DARK_MODE, true))
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    fun setUseMetric(value: Boolean) {
        prefs.edit().putBoolean(KEY_USE_METRIC, value).apply()
        _useMetricUnits.value = value
    }

    fun setDrivenRoadMemory(value: Boolean) {
        prefs.edit().putBoolean(KEY_DRIVEN_ROAD_MEMORY, value).apply()
        _drivenRoadMemoryEnabled.value = value
    }

    fun setSpeedLimitAlerts(value: Boolean) {
        prefs.edit().putBoolean(KEY_SPEED_LIMIT_ALERTS, value).apply()
        _speedLimitAlertsEnabled.value = value
    }

    fun setVoiceGuidance(value: Boolean) {
        prefs.edit().putBoolean(KEY_VOICE_GUIDANCE, value).apply()
        _voiceGuidanceEnabled.value = value
    }

    fun setDarkMode(value: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
        _darkMode.value = value
    }

    private fun getBoolean(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)

    companion object {
        private const val PREFS_NAME = "cardinal_settings"
        private const val KEY_USE_METRIC = "use_metric"
        private const val KEY_DRIVEN_ROAD_MEMORY = "driven_road_memory_enabled"
        private const val KEY_SPEED_LIMIT_ALERTS = "speed_limit_alerts_enabled"
        private const val KEY_VOICE_GUIDANCE = "voice_guidance_enabled"
        private const val KEY_DARK_MODE = "dark_mode"
    }
}

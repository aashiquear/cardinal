package com.cardinal.car

import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.RouteStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CarNavigationState {

    private val _state = MutableStateFlow(CarState())
    val state: StateFlow<CarState> = _state.asStateFlow()

    fun updateLocation(location: GeoPoint?) {
        _state.value = _state.value.copy(currentLocation = location)
    }

    fun updateSpeed(speedMps: Float) {
        _state.value = _state.value.copy(currentSpeedMps = speedMps)
    }

    fun updateRoadName(name: String?) {
        _state.value = _state.value.copy(currentRoadName = name)
    }

    fun updateNextStep(step: RouteStep?, distanceMeters: Int?) {
        _state.value = _state.value.copy(nextStep = step, distanceToNextStepMeters = distanceMeters)
    }

    fun updateIsNavigating(isNavigating: Boolean) {
        _state.value = _state.value.copy(isNavigating = isNavigating)
    }

    data class CarState(
        val currentLocation: GeoPoint? = null,
        val currentSpeedMps: Float = 0f,
        val currentRoadName: String? = null,
        val nextStep: RouteStep? = null,
        val distanceToNextStepMeters: Int? = null,
        val isNavigating: Boolean = false
    )
}

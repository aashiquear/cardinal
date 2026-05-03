package com.cardinal.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardinal.app.navigation.NavigationServiceLauncher
import com.cardinal.app.navigation.VoiceGuidanceManager
import com.cardinal.app.prefs.SettingsPrefs
import com.cardinal.car.CarNavigationState
import com.cardinal.core.data.DrivenRoadRepository
import com.cardinal.core.data.GradeRepository
import com.cardinal.core.data.MapRepository
import com.cardinal.core.data.PoiRepository
import com.cardinal.core.data.RouteRepository
import com.cardinal.core.data.SpeedLimitRepository
import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.Maneuver
import com.cardinal.core.domain.NavigationState
import com.cardinal.core.domain.Place
import com.cardinal.core.domain.PoiType
import com.cardinal.core.domain.RouteStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val routeRepository: RouteRepository,
    private val speedLimitRepository: SpeedLimitRepository,
    private val poiRepository: PoiRepository,
    private val gradeRepository: GradeRepository,
    private val drivenRoadRepository: DrivenRoadRepository,
    private val serviceLauncher: NavigationServiceLauncher,
    private val voiceGuidanceManager: VoiceGuidanceManager,
    private val settingsPrefs: SettingsPrefs
) : ViewModel() {

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    private val _useMetricUnits = MutableStateFlow(settingsPrefs.useMetricUnits.value)
    val useMetricUnits: StateFlow<Boolean> = _useMetricUnits.asStateFlow()

    private val _isLoadingRoute = MutableStateFlow(false)
    val isLoadingRoute: StateFlow<Boolean> = _isLoadingRoute.asStateFlow()

    private val _routeError = MutableStateFlow<String?>(null)
    val routeError: StateFlow<String?> = _routeError.asStateFlow()

    private val _selectedDestination = MutableStateFlow<Place?>(null)
    val selectedDestination: StateFlow<Place?> = _selectedDestination.asStateFlow()

    private var lastSpeedAlertTime = 0L
    private var lastAnnouncedStep: RouteStep? = null

    init {
        viewModelScope.launch {
            mapRepository.lastKnownLocation()?.let { location ->
                _navigationState.update { it.copy(currentLocation = location) }
            }
        }

        mapRepository.currentLocation()
            .onEach { location ->
                _navigationState.update {
                    it.copy(currentLocation = location)
                }
                CarNavigationState.updateLocation(location)
            }
            .launchIn(viewModelScope)

        mapRepository.currentSpeedMps()
            .onEach { speed ->
                _navigationState.update {
                    it.copy(currentSpeedMps = speed)
                }
                CarNavigationState.updateSpeed(speed)
            }
            .launchIn(viewModelScope)

        mapRepository.heading()
            .onEach { heading ->
                _navigationState.update {
                    it.copy(heading = heading)
                }
            }
            .launchIn(viewModelScope)

        _navigationState
            .onEach { state ->
                when (state.phase) {
                    NavigationState.Phase.ACTIVE -> serviceLauncher.start()
                    NavigationState.Phase.IDLE -> serviceLauncher.stop()
                    else -> {}
                }
            }
            .launchIn(viewModelScope)

        combine(_navigationState, _selectedDestination) { state, destination ->
            state to destination
        }.onEach { (state, destination) ->
            val location = state.currentLocation
            if (destination != null && location != null && state.phase == NavigationState.Phase.IDLE) {
                calculateRoute(location, destination)
            }
        }.launchIn(viewModelScope)

        _navigationState
            .map { it.currentLocation }
            .sample(5000)
            .filterNotNull()
            .onEach { location ->
                updateRoadName(location)
            }
            .launchIn(viewModelScope)

        _navigationState
            .map { it.currentLocation }
            .sample(5000)
            .filterNotNull()
            .flatMapLatest { location ->
                speedLimitRepository.speedLimitAt(location)
            }
            .onEach { limit ->
                _navigationState.update { it.copy(currentSpeedLimitMps = limit) }
            }
            .launchIn(viewModelScope)

        _navigationState
            .map { it.currentLocation }
            .sample(10000)
            .filterNotNull()
            .flatMapLatest { location ->
                poiRepository.nearbyPois(
                    location,
                    listOf(PoiType.GAS, PoiType.REST_AREA, PoiType.SHOPPING)
                )
            }
            .onEach { pois ->
                _navigationState.update { it.copy(nearbyPois = pois) }
            }
            .launchIn(viewModelScope)

        _navigationState
            .sample(5000)
            .filter { it.currentLocation != null }
            .flatMapLatest { state ->
                gradeRepository.currentGrade(state.currentLocation!!, state.heading)
            }
            .onEach { grade ->
                _navigationState.update { it.copy(currentGradePercent = grade) }
            }
            .launchIn(viewModelScope)

        /* ── Speed-limit alerts ── */
        _navigationState
            .sample(2000)
            .onEach { state ->
                if (!settingsPrefs.speedLimitAlertsEnabled.value) return@onEach
                val limit = state.currentSpeedLimitMps ?: return@onEach
                val speed = state.currentSpeedMps
                val thresholdMps = if (settingsPrefs.useMetricUnits.value) 2.235f else 2.235f /* 5 mph in mps */
                if (speed > limit + thresholdMps) {
                    val now = System.currentTimeMillis()
                    if (now - lastSpeedAlertTime > 15_000L) {
                        lastSpeedAlertTime = now
                        voiceGuidanceManager.speak("Speed limit exceeded", flush = true)
                    }
                }
            }
            .launchIn(viewModelScope)

        /* ── Voice guidance: next-step changes ── */
        _navigationState
            .map { it.nextStep }
            .distinctUntilChanged()
            .onEach { step ->
                if (!settingsPrefs.voiceGuidanceEnabled.value) return@onEach
                if (step != null && step != lastAnnouncedStep) {
                    lastAnnouncedStep = step
                    val text = buildString {
                        append(maneuverVerb(step.maneuver))
                        if (step.streetName.isNotBlank()) {
                            append(" onto ${step.streetName}")
                        }
                    }
                    voiceGuidanceManager.speak(text, flush = false)
                }
            }
            .launchIn(viewModelScope)

        /* ── Driven road memory ── */
        _navigationState
            .map { it.currentLocation }
            .sample(10_000)
            .filterNotNull()
            .onEach { location ->
                if (!settingsPrefs.drivenRoadMemoryEnabled.value) return@onEach
                try {
                    val wayId = mapRepository.currentWayId() ?: return@onEach
                    drivenRoadRepository.recordWay(wayId)
                } catch (_: Exception) {
                    /* ignore network errors */
                }
            }
            .launchIn(viewModelScope)
    }

    fun setDestination(place: Place) {
        _selectedDestination.value = place
        _navigationState.update { it.copy(phase = NavigationState.Phase.ROUTING) }
    }

    fun clearRoute() {
        _selectedDestination.value = null
        lastAnnouncedStep = null
        _navigationState.update {
            it.copy(
                phase = NavigationState.Phase.IDLE,
                activeRoute = null,
                nextStep = null,
                distanceToNextStepMeters = null,
                etaEpochMs = null
            )
        }
        CarNavigationState.updateIsNavigating(false)
        CarNavigationState.updateNextStep(null, null)
    }

    fun toggleUnits(useMetric: Boolean) {
        _useMetricUnits.value = useMetric
        settingsPrefs.setUseMetric(useMetric)
    }

    fun recenterToCurrentLocation() {
        viewModelScope.launch {
            val location = _navigationState.value.currentLocation
                ?: mapRepository.lastKnownLocation()
                ?: mapRepository.requestSingleLocation()
            location?.let {
                _navigationState.update { state -> state.copy(currentLocation = it) }
            }
        }
    }

    private suspend fun updateRoadName(location: GeoPoint) {
        try {
            val roadName = mapRepository.currentRoadName()
            _navigationState.update { it.copy(currentRoadName = roadName) }
            CarNavigationState.updateRoadName(roadName)
        } catch (_: Exception) {
            // keep existing
        }
    }

    private fun calculateRoute(origin: GeoPoint, destination: Place) {
        viewModelScope.launch {
            _isLoadingRoute.value = true
            _routeError.value = null

            val result = routeRepository.calculateRoute(origin, destination.location)
            result.onSuccess { route ->
                val nextStep = route.steps.firstOrNull()
                _navigationState.update {
                    it.copy(
                        phase = NavigationState.Phase.ACTIVE,
                        activeRoute = route,
                        nextStep = nextStep,
                        distanceToNextStepMeters = nextStep?.distanceMeters,
                        etaEpochMs = System.currentTimeMillis() + (route.durationSeconds * 1000L)
                    )
                }
                CarNavigationState.updateIsNavigating(true)
                CarNavigationState.updateNextStep(nextStep, nextStep?.distanceMeters)
            }.onFailure { error ->
                _routeError.value = error.message ?: "Failed to calculate route"
                _navigationState.update { it.copy(phase = NavigationState.Phase.IDLE) }
                CarNavigationState.updateIsNavigating(false)
            }

            _isLoadingRoute.value = false
        }
    }

    private fun maneuverVerb(m: Maneuver): String = when (m) {
        Maneuver.TURN_LEFT        -> "Turn left"
        Maneuver.TURN_RIGHT       -> "Turn right"
        Maneuver.TURN_SLIGHT_LEFT -> "Bear left"
        Maneuver.TURN_SLIGHT_RIGHT-> "Bear right"
        Maneuver.MERGE            -> "Merge"
        Maneuver.RAMP_LEFT        -> "Ramp left"
        Maneuver.RAMP_RIGHT       -> "Ramp right"
        Maneuver.ROUNDABOUT_ENTER  -> "Enter roundabout"
        Maneuver.ROUNDABOUT_EXIT_1,
        Maneuver.ROUNDABOUT_EXIT_2,
        Maneuver.ROUNDABOUT_EXIT_3,
        Maneuver.ROUNDABOUT_EXIT_4 -> "Exit roundabout"
        Maneuver.UTURN_LEFT,
        Maneuver.UTURN_RIGHT      -> "U-turn"
        Maneuver.ARRIVE            -> "Arrive"
        Maneuver.DEPART            -> "Depart"
        else                       -> "Continue"
    }
}

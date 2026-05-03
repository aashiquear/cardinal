package com.cardinal.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cardinal.app.prefs.OnboardingPrefs
import com.cardinal.app.ui.screens.HomeScreen
import com.cardinal.app.ui.screens.OnboardingScreen
import com.cardinal.app.ui.screens.SearchScreen
import com.cardinal.app.ui.screens.SettingsScreen
import com.cardinal.app.viewmodel.HomeViewModel
import com.cardinal.app.viewmodel.SettingsViewModel

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
}

@Composable
fun CardinalNavHost() {
    val navController = rememberNavController()
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()

    val context = LocalContext.current
    val onboardingPrefs = remember { OnboardingPrefs(context) }
    val startDestination = if (onboardingPrefs.onboardingCompleted) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }

    val useMetricUnits by settingsViewModel.useMetricUnits.collectAsState()
    val darkMode by settingsViewModel.darkMode.collectAsState()
    val drivenRoadMemory by settingsViewModel.drivenRoadMemoryEnabled.collectAsState()
    val speedLimitAlerts by settingsViewModel.speedLimitAlertsEnabled.collectAsState()
    val voiceGuidance by settingsViewModel.voiceGuidanceEnabled.collectAsState()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = {
                onboardingPrefs.onboardingCompleted = true
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                viewModel = homeViewModel
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onPlaceSelected = { place ->
                    homeViewModel.setDestination(place)
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                useMetricUnits = useMetricUnits,
                onToggleUnits = {
                    settingsViewModel.setMetric(it)
                    homeViewModel.toggleUnits(it)
                },
                darkMode = darkMode,
                onToggleDarkMode = { settingsViewModel.setDarkMode(it) },
                drivenRoadMemory = drivenRoadMemory,
                onToggleDrivenRoadMemory = { settingsViewModel.setDrivenRoadMemory(it) },
                speedLimitAlerts = speedLimitAlerts,
                onToggleSpeedLimitAlerts = { settingsViewModel.setSpeedLimitAlerts(it) },
                voiceGuidance = voiceGuidance,
                onToggleVoiceGuidance = { settingsViewModel.setVoiceGuidance(it) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

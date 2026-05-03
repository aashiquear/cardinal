package com.cardinal.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cardinal.app.viewmodel.HomeViewModel
import com.cardinal.core.domain.Maneuver
import com.cardinal.core.domain.NavigationState
import com.cardinal.feature.map.ui.MapLibreController
import com.cardinal.feature.map.ui.MapLibreView
import kotlinx.coroutines.delay

/* ─── Brand colours (spec §2) ─── */
private val ChromeTop    = Color(0xFF1A2942)
private val ChromeBottom = Color(0xFF0A1322)
private val CardinalRed  = Color(0xFFC8102E)
private val CardinalRedBright = Color(0xFFE63946)
private val SurfaceDark  = Color(0xFF0A1322)
private val SurfaceElev  = Color(0xFF1A2942)
private val WhitePrimary = Color(0xFFFFFFFF)
private val WhiteSec     = Color(0xB3FFFFFF)
private val WhiteTer     = Color(0x80FFFFFF)
private val SpeedGreen   = Color(0xFF1F5E3A)
private val SpeedGreenBorder = Color(0xFF34D399)
private val SpeedAmber   = Color(0xFFB45309)
private val SpeedAmberBorder = Color(0xFFF59E0B)
private val SpeedRed     = Color(0xFF991B1B)
private val SpeedRedBorder = Color(0xFFEF4444)
private val CyanDriven   = Color(0xFF22D3EE)

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navState  by viewModel.navigationState.collectAsState()
    val isLoading by viewModel.isLoadingRoute.collectAsState()
    val routeErr  by viewModel.routeError.collectAsState()
    val useMetric by viewModel.useMetricUnits.collectAsState()

    val mapController = remember { MapLibreController() }
    val chromeBrush = Brush.verticalGradient(listOf(ChromeTop, ChromeBottom))
    var followLocation by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        /* ── Map background ── */
        MapLibreView(
            modifier = Modifier.fillMaxSize(),
            controller = mapController,
            cameraPosition = navState.currentLocation,
            routePolyline  = navState.activeRoute?.polyline ?: emptyList(),
            destination    = navState.activeRoute?.destination?.location,
            followLocation = followLocation,
            onUserInteracted = { followLocation = false }
        )

        /* ── Loading ── */
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = WhitePrimary, strokeWidth = 4.dp)
            }
        }

        /* ── Error banner ── */
        routeErr?.let { error ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp, start = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = error,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(onClick = { viewModel.clearRoute() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        /* ── Top bar (64 dp) ── */
        val topBarRoadName = when {
            navState.phase == NavigationState.Phase.ACTIVE && navState.nextStep != null -> navState.nextStep?.streetName ?: ""
            else -> ""
        }
        TopBar(
            roadName = topBarRoadName,
            onSearchClick = onSearchClick,
            onSettingsClick = onSettingsClick,
            chromeBrush = chromeBrush,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        /* ── Right control column (compass + zoom + my location) ── */
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompassHud(heading = navState.heading)
            ZoomControls(
                onZoomIn = { mapController.zoomIn() },
                onZoomOut = { mapController.zoomOut() }
            )
            MyLocationButton(
                onClick = {
                    followLocation = true
                    navState.currentLocation?.let { mapController.zoomToCurrent(it) }
                        ?: viewModel.recenterToCurrentLocation()
                }
            )
        }

        /* ── POI rail ── */
        PoiRail(
            pois = navState.nearbyPois,
            isCollapsed = navState.phase == NavigationState.Phase.ACTIVE,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 84.dp, start = 12.dp)
        )

        /* ── Turn card (active only) ── */
        if (navState.phase == NavigationState.Phase.ACTIVE) {
            TurnCard(
                navState = navState,
                useMetric = useMetric,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 84.dp, start = 12.dp)
                    .offset(x = if (navState.phase == NavigationState.Phase.ACTIVE) 0.dp else (-640).dp)
            )
        }

        /* ── Bottom dock ── */
        BottomDock(
            navState = navState,
            useMetric = useMetric,
            onClearRoute = { viewModel.clearRoute() },
            chromeBrush = chromeBrush,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/* ═══════════════════════════════════════════════
   TOP BAR
   ═══════════════════════════════════════════════ */
@Composable
private fun TopBar(
    roadName: String,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    chromeBrush: Brush,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(chromeBrush)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        /* Logo mark */
        LogoMark(modifier = Modifier.size(40.dp))

        /* Center road name */
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (roadName.isNotBlank()) {
                Text(
                    text = roadName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePrimary,
                    maxLines = 1
                )
            }
        }

        /* Right tools */
        Row(verticalAlignment = Alignment.CenterVertically) {
            ToolCircle(onClick = onSearchClick) {
                Icon(Icons.Default.Search, null, tint = WhitePrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            ToolCircle(onClick = { /* menu */ }) {
                Icon(Icons.Default.Menu, null, tint = WhitePrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            ToolCircle(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, null, tint = WhitePrimary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun LogoMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(SurfaceDark, CircleShape)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        /* Simplified cardinal bird needle */
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(8.dp, 14.dp)
                    .background(CardinalRedBright, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            )
            Box(modifier = Modifier.size(4.dp).background(Color.White, CircleShape))
        }
    }
}

@Composable
private fun PoiChip() {
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color(0xFFF59E0B), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("⛽", fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text("Shell · 0.5 mi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = WhitePrimary)
    }
}

@Composable
private fun ToolCircle(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(SurfaceDark, CircleShape)
    ) {
        content()
    }
}

/* ═══════════════════════════════════════════════
   ZOOM CONTROLS
   ═══════════════════════════════════════════════ */
@Composable
private fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark.copy(alpha = 0.92f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onZoomIn, modifier = Modifier.size(44.dp)) {
            Icon(Icons.Filled.Add, null, tint = WhitePrimary)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(WhiteTer.copy(alpha = 0.3f))
        )
        IconButton(onClick = onZoomOut, modifier = Modifier.size(44.dp)) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(2.dp)
                    .background(WhitePrimary)
            )
        }
    }
}

/* ═══════════════════════════════════════════════
   MY LOCATION BUTTON
   ═══════════════════════════════════════════════ */
@Composable
private fun MyLocationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .background(SurfaceDark.copy(alpha = 0.92f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "My location",
            tint = CardinalRedBright,
            modifier = Modifier.size(20.dp)
        )
    }
}

/* ═══════════════════════════════════════════════
   COMPASS HUD
   ═══════════════════════════════════════════════ */
@Composable
private fun CompassHud(heading: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* Compass disc */
        Box(
            modifier = Modifier.size(84.dp),
            contentAlignment = Alignment.Center
        ) {
            /* Outer ring */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceDark.copy(alpha = 0.92f), CircleShape)
            )
            /* Inner tick ring */
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(Color.Transparent, CircleShape)
            )
            /* Rotating needle container */
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .rotate(-heading),   /* map north-up: needle rotates opposite to heading */
                contentAlignment = Alignment.Center
            ) {
                /* North pointer (red) */
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("N", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WhitePrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(0.dp, 18.dp)
                                .background(CardinalRedBright)
                        )
                    }
                }
                /* South pointer (white) */
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(0.dp, 18.dp)
                                .background(WhitePrimary)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("S", fontSize = 8.sp, fontWeight = FontWeight.SemiBold, color = WhiteTer)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        /* Heading pill */
        val dir = headingToLetter(heading)
        Box(
            modifier = Modifier
                .height(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(SurfaceDark.copy(alpha = 0.92f))
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$dir · ${heading.toInt()}°",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = WhitePrimary
            )
        }
    }
}

private fun headingToLetter(degrees: Float): String {
    val d = ((degrees % 360) + 360) % 360
    return when {
        d < 22.5f  -> "N"
        d < 67.5f  -> "NE"
        d < 112.5f -> "E"
        d < 157.5f -> "SE"
        d < 202.5f -> "S"
        d < 247.5f -> "SW"
        d < 292.5f -> "W"
        d < 337.5f -> "NW"
        else       -> "N"
    }
}

/* ═══════════════════════════════════════════════
   POI RAIL
   ═══════════════════════════════════════════════ */
@Composable
private fun PoiRail(
    pois: List<com.cardinal.core.domain.PoiItem>,
    isCollapsed: Boolean,
    modifier: Modifier = Modifier
) {
    if (pois.isEmpty()) return

    if (isCollapsed) {
        /* Collapsed strip (56 dp wide) */
        Column(
            modifier = modifier
                .width(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark.copy(alpha = 0.85f))
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            pois.take(3).forEach { poi ->
                val (icon, color) = poiTypeVisuals(poi.type)
                PoiIconStrip(color = color, icon = icon, label = formatDistanceShort(poi.distanceMeters))
            }
            WeatherIconStrip(temp = "72°")
        }
    } else {
        /* Expanded panel (220 dp wide) */
        Column(
            modifier = modifier
                .width(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark.copy(alpha = 0.92f))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "NEARBY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = WhiteTer,
                letterSpacing = 1.5.sp
            )
            pois.take(3).forEach { poi ->
                val (icon, color) = poiTypeVisuals(poi.type)
                PoiCard(
                    icon = icon,
                    name = poi.name,
                    detail = formatDistanceShort(poi.distanceMeters),
                    color = color
                )
            }
            WeatherCard()
        }
    }
}

private fun poiTypeVisuals(type: com.cardinal.core.domain.PoiType): Pair<String, Color> {
    return when (type) {
        com.cardinal.core.domain.PoiType.GAS -> "⛽" to Color(0xFFF59E0B)
        com.cardinal.core.domain.PoiType.EV_CHARGING -> "🔌" to Color(0xFF3B82F6)
        com.cardinal.core.domain.PoiType.REST_AREA -> "🌳" to Color(0xFF10B981)
        com.cardinal.core.domain.PoiType.FOOD -> "🍽" to Color(0xFFEF4444)
        com.cardinal.core.domain.PoiType.COFFEE -> "☕" to Color(0xFF8B4513)
        com.cardinal.core.domain.PoiType.SHOPPING -> "🛍" to Color(0xFFA855F7)
        com.cardinal.core.domain.PoiType.PHARMACY -> "💊" to Color(0xFFEC4899)
    }
}

private fun formatDistanceShort(meters: Int): String {
    return if (meters >= 1000) "%.1f mi".format(meters / 1609.344) else "${(meters * 3.28084).toInt()} ft"
}

@Composable
private fun PoiIconStrip(color: Color, icon: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(28.dp).background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 13.sp)
        }
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WhitePrimary)
    }
}

@Composable
private fun PoiCard(icon: String, name: String, detail: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElev)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(28.dp).background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WhitePrimary)
            Text(detail, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = WhiteSec)
        }
    }
}

@Composable
private fun WeatherIconStrip(temp: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(20.dp).background(Color(0xFFF5A623), CircleShape)
        )
        Text(temp, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = WhitePrimary)
    }
}

@Composable
private fun WeatherCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceElev)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(Color(0xFFF5A623).copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(24.dp).background(Color(0xFFF5A623), CircleShape))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text("72°", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = WhitePrimary)
            Text("Mostly Sunny", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = WhiteSec)
            Text("10% rain · 8 mph WSW", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = CyanDriven)
        }
    }
}

/* ═══════════════════════════════════════════════
   TURN CARD (route active)
   ═══════════════════════════════════════════════ */
@Composable
private fun TurnCard(
    navState: NavigationState,
    useMetric: Boolean,
    modifier: Modifier = Modifier
) {
    val step = navState.nextStep ?: return
    val dist = navState.distanceToNextStepMeters ?: step.distanceMeters

    Column(
        modifier = modifier
            .widthIn(max = 620.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* Maneuver icon square */
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(CardinalRedBright, CardinalRed)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = maneuverIcon(step.maneuver),
                    contentDescription = null,
                    tint = WhitePrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        formatDistance(dist, useMetric),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WhitePrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        maneuverVerb(step.maneuver),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WhiteSec
                    )
                }
                Text(
                    "onto ${step.streetName}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePrimary,
                    maxLines = 1
                )
            }
        }

        /* Lane guidance (placeholder) */
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LaneChip("↑", isRecommended = false)
            LaneChip("↱", isRecommended = true)
            LaneChip("→", isRecommended = false)
        }
        Text(
            "LANE GUIDANCE",
            modifier = Modifier.padding(top = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = WhiteTer,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun LaneChip(symbol: String, isRecommended: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp, 56.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isRecommended) CardinalRed else SurfaceElev)
            .padding(if (isRecommended) 2.dp else 0.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceElev),
        contentAlignment = Alignment.Center
    ) {
        Text(
            symbol,
            fontSize = 22.sp,
            color = if (isRecommended) CardinalRedBright else WhitePrimary.copy(alpha = 0.4f)
        )
    }
}

/* ═══════════════════════════════════════════════
   SPEED CLUSTER
   ═══════════════════════════════════════════════ */
@Composable
private fun SpeedCluster(
    speedMps: Float,
    speedLimitMps: Float?,
    useMetric: Boolean,
    modifier: Modifier = Modifier
) {
    val speedValue = if (useMetric) speedMps * 3.6f else speedMps * 2.23694f
    val limitValue = speedLimitMps?.let { if (useMetric) it * 3.6f else it * 2.23694f }

    val (bgColor, borderColor) = speedColors(speedValue, limitValue)

    val animatedBg by animateColorAsState(
        targetValue = bgColor,
        animationSpec = tween(400),
        label = "speedBg"
    )
    val animatedBorder by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(400),
        label = "speedBorder"
    )

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        /* Speed-limit sign (MUTCD style) */
        limitValue?.let {
            SpeedLimitSign(limit = it.toInt())
            Spacer(modifier = Modifier.width(6.dp))
        }

        /* Current speed box */
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(animatedBg)
                .padding(2.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(animatedBg)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (useMetric) "KM/H" else "MPH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePrimary.copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                )
                Text(
                    "${speedValue.toInt()}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WhitePrimary
                )
            }
        }
    }
}

@Composable
private fun SpeedLimitSign(limit: Int) {
    Column(
        modifier = Modifier
            .width(60.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SPEED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SurfaceDark)
        Text("LIMIT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SurfaceDark)
        Spacer(modifier = Modifier.height(1.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SurfaceDark)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "$limit",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SurfaceDark
        )
    }
}

private fun speedColors(speed: Float, limit: Float?): Pair<Color, Color> {
    if (limit == null) return SpeedGreen to SpeedGreenBorder
    return when {
        speed <= limit       -> SpeedGreen to SpeedGreenBorder
        speed <= limit + 5   -> SpeedAmber to SpeedAmberBorder
        else                 -> SpeedRed   to SpeedRedBorder
    }
}

/* ═══════════════════════════════════════════════
   GRADE WIDGET (compact, dock-sized)
   ═══════════════════════════════════════════════ */
@Composable
private fun GradeWidget(gradePercent: Float) {
    if (kotlin.math.abs(gradePercent) < 1f) return

    val arrow = when {
        gradePercent > 0 -> "▲"
        else -> "▼"
    }
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = "$arrow ${kotlin.math.abs(gradePercent).toInt()}%",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = WhitePrimary
        )
        Text(
            text = "GRADE",
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            color = WhiteTer,
            letterSpacing = 1.sp
        )
    }
}

/* ═══════════════════════════════════════════════
   BOTTOM DOCK
   ═══════════════════════════════════════════════ */
@Composable
private fun BottomDock(
    navState: NavigationState,
    useMetric: Boolean,
    onClearRoute: () -> Unit,
    chromeBrush: Brush,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(chromeBrush)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        when (navState.phase) {
            NavigationState.Phase.IDLE,
            NavigationState.Phase.ROUTING -> IdleDockContent(
                speedMps = navState.currentSpeedMps,
                speedLimitMps = navState.currentSpeedLimitMps,
                roadName = navState.currentRoadName ?: "",
                gradePercent = navState.currentGradePercent,
                useMetric = useMetric
            )

            NavigationState.Phase.ACTIVE    -> ActiveDockContent(
                speedMps = navState.currentSpeedMps,
                speedLimitMps = navState.currentSpeedLimitMps,
                roadName = navState.currentRoadName ?: "",
                route = navState.activeRoute,
                eta = navState.etaEpochMs,
                gradePercent = navState.currentGradePercent,
                useMetric = useMetric,
                onClearRoute = onClearRoute
            )

            else -> {}
        }
    }
}

@Composable
private fun IdleDockContent(
    speedMps: Float,
    speedLimitMps: Float?,
    roadName: String,
    gradePercent: Float,
    useMetric: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        /* Left: speed limit + current speed */
        SpeedDisplay(
            speedMps = speedMps,
            speedLimitMps = speedLimitMps,
            useMetric = useMetric
        )

        /* Center: current road name */
        Box(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (roadName.isNotBlank()) {
                Text(
                    text = roadName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePrimary,
                    maxLines = 1
                )
            }
        }

        /* Right: grade */
        GradeWidget(gradePercent = gradePercent)
    }
}

@Composable
private fun ActiveDockContent(
    speedMps: Float,
    speedLimitMps: Float?,
    roadName: String,
    route: com.cardinal.core.domain.Route?,
    eta: Long?,
    gradePercent: Float,
    useMetric: Boolean,
    onClearRoute: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        /* Left: speed limit + current speed */
        SpeedDisplay(
            speedMps = speedMps,
            speedLimitMps = speedLimitMps,
            useMetric = useMetric
        )

        /* Center: current road name + remaining info */
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (roadName.isNotBlank()) {
                Text(
                    text = roadName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePrimary,
                    maxLines = 1
                )
            }
            route?.let {
                Text(
                    "${formatDistance(it.distanceMeters, useMetric)} · ${formatDuration(it.durationSeconds)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = WhiteSec,
                    maxLines = 1
                )
            }
        }

        /* Right: ETA + grade + stop */
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "ARRIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteTer,
                    letterSpacing = 1.5.sp
                )
                eta?.let {
                    val fmt = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                    Text(
                        fmt.format(java.util.Date(it)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SpeedGreenBorder
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            GradeWidget(gradePercent = gradePercent)
            IconButton(onClick = onClearRoute) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Stop",
                    tint = WhitePrimary
                )
            }
        }
    }
}

@Composable
private fun SpeedDisplay(
    speedMps: Float,
    speedLimitMps: Float?,
    useMetric: Boolean
) {
    val speedValue = if (useMetric) speedMps * 3.6f else speedMps * 2.23694f
    val limitValue = speedLimitMps?.let { if (useMetric) it * 3.6f else it * 2.23694f }
    val (bgColor, borderColor) = speedColors(speedValue, limitValue)

    val animatedBg by animateColorAsState(
        targetValue = bgColor,
        animationSpec = tween(400),
        label = "speedBg"
    )
    val animatedBorder by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(400),
        label = "speedBorder"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        /* Speed-limit sign */
        if (limitValue != null) {
            SpeedLimitSign(limit = limitValue.toInt())
            Spacer(modifier = Modifier.width(6.dp))
        }

        /* Current speed box */
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(animatedBg)
                .padding(2.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(animatedBg)
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (useMetric) "KM/H" else "MPH",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhitePrimary.copy(alpha = 0.7f),
                    letterSpacing = 1.2.sp
                )
                Text(
                    "${speedValue.toInt()}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WhitePrimary
                )
            }
        }
    }
}


/* ═══════════════════════════════════════════════
   HELPERS
   ═══════════════════════════════════════════════ */
private fun formatDistance(meters: Int, useMetric: Boolean): String {
    return if (useMetric) {
        if (meters >= 1000) "%.1f km".format(meters / 1000.0) else "$meters m"
    } else {
        val miles = meters / 1609.344
        if (miles >= 1) "%.1f mi".format(miles) else "${(meters * 3.28084).toInt()} ft"
    }
}

private fun formatDuration(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
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

@Composable
private fun maneuverIcon(maneuver: Maneuver): ImageVector = when (maneuver) {
    Maneuver.TURN_LEFT,
    Maneuver.TURN_SLIGHT_LEFT,
    Maneuver.RAMP_LEFT      -> Icons.AutoMirrored.Filled.ArrowForward

    Maneuver.TURN_RIGHT,
    Maneuver.TURN_SLIGHT_RIGHT,
    Maneuver.RAMP_RIGHT     -> Icons.AutoMirrored.Filled.ArrowForward

    Maneuver.UTURN_LEFT,
    Maneuver.UTURN_RIGHT   -> Icons.AutoMirrored.Filled.ArrowForward

    Maneuver.ARRIVE         -> Icons.Default.LocationOn
    else                    -> Icons.AutoMirrored.Filled.ArrowForward
}

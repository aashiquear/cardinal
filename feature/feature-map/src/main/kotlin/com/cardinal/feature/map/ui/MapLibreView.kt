package com.cardinal.feature.map.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.cardinal.core.domain.GeoPoint
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.geojson.Point
import org.maplibre.geojson.LineString

class MapLibreController {
    internal var map: MapLibreMap? = null

    fun zoomIn() {
        map?.animateCamera(CameraUpdateFactory.zoomIn())
    }

    fun zoomOut() {
        map?.animateCamera(CameraUpdateFactory.zoomOut())
    }

    fun centerOn(location: GeoPoint) {
        map?.let { mapLibreMap ->
            mapLibreMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    org.maplibre.android.camera.CameraPosition.Builder()
                        .target(org.maplibre.android.geometry.LatLng(location.lat, location.lng))
                        .zoom(16.0)
                        .tilt(60.0)
                        .build()
                ),
                500
            )
        }
    }

    fun zoomToCurrent(location: GeoPoint) {
        map?.let { mapLibreMap ->
            mapLibreMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    org.maplibre.android.camera.CameraPosition.Builder()
                        .target(org.maplibre.android.geometry.LatLng(location.lat, location.lng))
                        .zoom(18.0)
                        .tilt(60.0)
                        .build()
                ),
                700
            )
        }
    }
}

@Composable
fun MapLibreView(
    modifier: Modifier = Modifier,
    controller: MapLibreController = remember { MapLibreController() },
    cameraPosition: GeoPoint? = null,
    routePolyline: List<GeoPoint> = emptyList(),
    destination: GeoPoint? = null,
    followLocation: Boolean = true,
    onUserInteracted: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }
    var mapReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        mapView.getMapAsync { mapLibreMap ->
            controller.map = mapLibreMap
            mapLibreMap.setStyle("https://tiles.openfreemap.org/styles/liberty") { style ->
                setupMapAnnotations(mapLibreMap, style, routePolyline, destination)
                try {
                    val locationComponent = mapLibreMap.locationComponent
                    locationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(context, style).build()
                    )
                    locationComponent.isLocationComponentEnabled = true
                } catch (_: Exception) {
                    // Location component may fail if permissions aren't granted yet
                }
                cameraPosition?.let { centerCamera(mapLibreMap, it) }
                mapReady = true
            }
            mapLibreMap.addOnCameraMoveStartedListener { reason ->
                if (reason == MapLibreMap.OnCameraMoveStartedListener.REASON_API_GESTURE) {
                    onUserInteracted()
                }
            }
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    LaunchedEffect(cameraPosition, followLocation, mapReady) {
        if (mapReady && followLocation) {
            cameraPosition?.let { controller.centerOn(it) }
        }
    }

    LaunchedEffect(routePolyline, destination, mapReady) {
        if (mapReady) {
            controller.map?.style?.let { style ->
                controller.map?.let { mapLibreMap ->
                    updateAnnotations(mapLibreMap, style, routePolyline, destination)
                }
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { }
    )
}

private fun centerCamera(mapLibreMap: MapLibreMap, position: GeoPoint) {
    mapLibreMap.animateCamera(
        CameraUpdateFactory.newCameraPosition(
            org.maplibre.android.camera.CameraPosition.Builder()
                .target(org.maplibre.android.geometry.LatLng(position.lat, position.lng))
                .zoom(16.0)
                .tilt(60.0)
                .build()
        ),
        500
    )
}

private fun setupMapAnnotations(
    mapLibreMap: MapLibreMap,
    style: org.maplibre.android.maps.Style,
    routePolyline: List<GeoPoint>,
    destination: GeoPoint?
) {
    updateAnnotations(mapLibreMap, style, routePolyline, destination)
}

private fun updateAnnotations(
    mapLibreMap: MapLibreMap,
    style: org.maplibre.android.maps.Style,
    routePolyline: List<GeoPoint>,
    destination: GeoPoint?
) {
    // Remove existing route layer and source
    style.removeLayer("route-layer")
    style.removeSource("route-source")
    style.removeLayer("destination-layer")
    style.removeSource("destination-source")

    if (routePolyline.isNotEmpty()) {
        val lineString = LineString.fromLngLats(
            routePolyline.map { Point.fromLngLat(it.lng, it.lat) }
        )
        val source = org.maplibre.android.style.sources.GeoJsonOptions()
        style.addSource(
            org.maplibre.android.style.sources.GeoJsonSource("route-source", lineString, source)
        )
        val lineLayer = org.maplibre.android.style.layers.LineLayer("route-layer", "route-source")
            .withProperties(
                org.maplibre.android.style.layers.PropertyFactory.lineColor("#C8102E"),
                org.maplibre.android.style.layers.PropertyFactory.lineWidth(5f),
                org.maplibre.android.style.layers.PropertyFactory.lineCap("round"),
                org.maplibre.android.style.layers.PropertyFactory.lineJoin("round")
            )
        style.addLayer(lineLayer)
    }

    destination?.let {
        val point = Point.fromLngLat(it.lng, it.lat)
        val source = org.maplibre.android.style.sources.GeoJsonOptions()
        style.addSource(
            org.maplibre.android.style.sources.GeoJsonSource("destination-source", point, source)
        )
        val circleLayer = org.maplibre.android.style.layers.CircleLayer("destination-layer", "destination-source")
            .withProperties(
                org.maplibre.android.style.layers.PropertyFactory.circleColor("#EF4444"),
                org.maplibre.android.style.layers.PropertyFactory.circleRadius(8f),
                org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor("#FFFFFF"),
                org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth(2f)
            )
        style.addLayer(circleLayer)
    }
}

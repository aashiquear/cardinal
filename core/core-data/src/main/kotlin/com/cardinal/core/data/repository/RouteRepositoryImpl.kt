package com.cardinal.core.data.repository

import com.cardinal.core.data.RouteRepository
import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.Maneuver
import com.cardinal.core.domain.Place
import com.cardinal.core.domain.Route
import com.cardinal.core.domain.RouteStep
import com.cardinal.core.data.util.PolylineDecoder
import com.cardinal.data.remote.api.OsrmApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val osrmApi: OsrmApi
) : RouteRepository {

    override suspend fun calculateRoute(
        origin: GeoPoint,
        destination: GeoPoint
    ): Result<Route> = runCatching {
        val coords = "${origin.lng},${origin.lat};${destination.lng},${destination.lat}"
        val response = osrmApi.getRoute(coords)
        val osrmRoute = response.routes.firstOrNull()
            ?: throw IllegalStateException("No route found")

        val polyline = osrmRoute.geometry?.let { PolylineDecoder.decode(it, precision = 6) }
            ?: emptyList()

        val steps = osrmRoute.legs.flatMap { leg ->
            leg.steps.map { step ->
                RouteStep(
                    maneuver = mapManeuver(step.maneuver.type, step.maneuver.modifier),
                    streetName = step.name,
                    distanceMeters = step.distance.toInt(),
                    laneGuidance = null
                )
            }
        }

        Route(
            id = "${origin.lat},${origin.lng}-${destination.lat},${destination.lng}",
            polyline = polyline,
            steps = steps,
            distanceMeters = osrmRoute.distance.toInt(),
            durationSeconds = osrmRoute.duration.toInt(),
            destination = Place(
                id = "dest_${destination.lat}_${destination.lng}",
                name = "Destination",
                location = destination
            )
        )
    }

    override suspend fun reroute(
        currentLocation: GeoPoint,
        destination: GeoPoint
    ): Result<Route> = calculateRoute(currentLocation, destination)

    private fun mapManeuver(type: String, modifier: String?): Maneuver {
        return when (type) {
            "turn" -> when (modifier) {
                "left" -> Maneuver.TURN_LEFT
                "right" -> Maneuver.TURN_RIGHT
                "slight left" -> Maneuver.TURN_SLIGHT_LEFT
                "slight right" -> Maneuver.TURN_SLIGHT_RIGHT
                "sharp left" -> Maneuver.TURN_LEFT
                "sharp right" -> Maneuver.TURN_RIGHT
                "uturn" -> Maneuver.UTURN_LEFT
                else -> Maneuver.TURN_RIGHT
            }
            "new name", "continue" -> Maneuver.CONTINUE
            "merge" -> Maneuver.MERGE
            "on ramp" -> when (modifier) {
                "left" -> Maneuver.RAMP_LEFT
                "right" -> Maneuver.RAMP_RIGHT
                else -> Maneuver.RAMP_RIGHT
            }
            "off ramp" -> when (modifier) {
                "left" -> Maneuver.RAMP_LEFT
                "right" -> Maneuver.RAMP_RIGHT
                else -> Maneuver.RAMP_RIGHT
            }
            "roundabout", "rotary" -> Maneuver.ROUNDABOUT_ENTER
            "exit roundabout", "exit rotary" -> Maneuver.ROUNDABOUT_EXIT_1
            "depart" -> Maneuver.DEPART
            "arrive" -> Maneuver.ARRIVE
            else -> Maneuver.STRAIGHT
        }
    }
}

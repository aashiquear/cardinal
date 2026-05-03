package com.cardinal.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OverpassResponse(
    val elements: List<OverpassElement> = emptyList()
)

@Serializable
data class OverpassElement(
    val type: String = "",
    val id: Long = 0,
    val lat: Double? = null,
    val lon: Double? = null,
    val tags: OverpassTags? = null
)

@Serializable
data class OverpassTags(
    val name: String? = null,
    @SerialName("maxspeed") val maxSpeed: String? = null,
    val amenity: String? = null,
    val shop: String? = null,
    val highway: String? = null,
    @SerialName("rest_area") val restArea: String? = null,
    val brand: String? = null,
    val operator: String? = null
)

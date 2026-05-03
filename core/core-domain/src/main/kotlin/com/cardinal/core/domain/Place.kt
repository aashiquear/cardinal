package com.cardinal.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String,
    val name: String,
    val address: String? = null,
    val location: GeoPoint
)

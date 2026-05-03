package com.cardinal.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Lane(
    val directions: List<LaneDirection>,
    val recommended: Boolean
)

@Serializable
enum class LaneDirection {
    STRAIGHT,
    LEFT,
    RIGHT,
    SLIGHT_LEFT,
    SLIGHT_RIGHT,
    UTURN
}

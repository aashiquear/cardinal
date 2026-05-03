package com.cardinal.core.domain

data class Weather(
    val temperatureC: Float,
    val condition: String,
    val rainChancePercent: Int,
    val windSpeedKmh: Int,
    val windDirection: String
)

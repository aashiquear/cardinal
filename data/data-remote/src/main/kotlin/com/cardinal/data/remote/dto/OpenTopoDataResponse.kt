package com.cardinal.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenTopoDataResponse(
    val results: List<OpenTopoDataResult> = emptyList()
)

@Serializable
data class OpenTopoDataResult(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val elevation: Double? = null
)

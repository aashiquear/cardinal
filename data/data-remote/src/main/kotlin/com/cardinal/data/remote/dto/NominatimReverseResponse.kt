package com.cardinal.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NominatimReverseResponse(
    @SerialName("display_name") val displayName: String? = null,
    val address: NominatimAddress? = null
)

@Serializable
data class NominatimAddress(
    val road: String? = null,
    val highway: String? = null,
    val street: String? = null,
    val residential: String? = null
)

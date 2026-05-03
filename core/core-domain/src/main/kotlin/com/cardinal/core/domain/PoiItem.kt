package com.cardinal.core.domain

data class PoiItem(
    val id: String,
    val type: PoiType,
    val name: String,
    val location: GeoPoint,
    val distanceMeters: Int,
    val details: String? = null
)

enum class PoiType {
    GAS,
    EV_CHARGING,
    REST_AREA,
    FOOD,
    COFFEE,
    SHOPPING,
    PHARMACY
}

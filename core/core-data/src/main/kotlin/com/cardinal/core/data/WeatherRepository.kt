package com.cardinal.core.data

import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun currentWeather(location: GeoPoint): Flow<Weather>
}

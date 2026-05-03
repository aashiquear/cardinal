package com.cardinal.core.data.repository

import com.cardinal.core.data.SearchRepository
import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.Place
import com.cardinal.data.remote.api.NominatimApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val nominatimApi: NominatimApi
) : SearchRepository {

    override fun search(query: String): Flow<List<Place>> = flow {
        if (query.isBlank()) {
            emit(emptyList())
            return@flow
        }
        val results = nominatimApi.search(query)
        emit(
            results.map {
                Place(
                    id = it.placeId.toString(),
                    name = it.displayName.substringBefore(",").trim(),
                    address = it.displayName,
                    location = GeoPoint(
                        lat = it.lat.toDouble(),
                        lng = it.lon.toDouble()
                    )
                )
            }
        )
    }
}

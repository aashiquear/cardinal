package com.cardinal.data.remote.api

import com.cardinal.data.remote.dto.OsrmRouteResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OsrmApi {

    @GET("route/v1/driving/{coordinates}")
    suspend fun getRoute(
        @Path("coordinates") coordinates: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "polyline6",
        @Query("steps") steps: Boolean = true
    ): OsrmRouteResponse
}

package com.cardinal.data.remote.api

import com.cardinal.data.remote.dto.OpenTopoDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenTopoDataApi {

    @GET("v1/srtm90m")
    suspend fun getElevation(
        @Query("locations") locations: String
    ): OpenTopoDataResponse
}

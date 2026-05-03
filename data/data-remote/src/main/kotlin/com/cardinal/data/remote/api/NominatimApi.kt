package com.cardinal.data.remote.api

import com.cardinal.data.remote.dto.NominatimPlace
import com.cardinal.data.remote.dto.NominatimReverseResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NominatimApi {

    @Headers("User-Agent: Cardinal/1.0")
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 10
    ): List<NominatimPlace>

    @Headers("User-Agent: Cardinal/1.0")
    @GET("reverse")
    suspend fun reverse(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): NominatimReverseResponse
}

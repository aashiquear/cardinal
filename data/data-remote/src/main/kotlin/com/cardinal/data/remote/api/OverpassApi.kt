package com.cardinal.data.remote.api

import com.cardinal.data.remote.dto.OverpassResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OverpassApi {

    @FormUrlEncoded
    @POST("api/interpreter")
    suspend fun query(
        @Field("data") data: String
    ): OverpassResponse
}

package com.strimup.feature.profile.data

import com.strimup.feature.profile.data.response.StreamerResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StreamerApiService {
    @GET("api/streamer/{id}")
    suspend fun getStreamerById(
        @Path("id") id: String
    ): StreamerResponse
}
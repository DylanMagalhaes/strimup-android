package com.strimup.feature.streamerprofile.data

import retrofit2.http.GET
import retrofit2.http.Path

interface StreamerApiService {
    @GET("api/streamer/{id}")
    suspend fun getStreamerById(
        @Path("id") id: String
    ): com.strimup.feature.streamerprofile.data.response.StreamerResponse
}
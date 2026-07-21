package com.strimup.feature.streamerprofile.data

import com.strimup.feature.streamerprofile.data.request.UpdateProfileRequest
import com.strimup.feature.streamerprofile.data.response.StreamerOptionsResponse
import com.strimup.feature.streamerprofile.data.response.StreamerResponse
import com.strimup.feature.streamerprofile.data.response.UpdateProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface StreamerApiService {
    @GET("api/streamer/{id}")
    suspend fun getStreamerById(
        @Path("id") id: String
    ): StreamerResponse

    @PUT("api/streamer/update")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): UpdateProfileResponse

    @GET("/api/streamer/options")
    suspend fun getStreamerOptions(): StreamerOptionsResponse
}
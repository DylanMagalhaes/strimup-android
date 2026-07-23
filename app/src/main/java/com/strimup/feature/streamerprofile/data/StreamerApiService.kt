package com.strimup.feature.streamerprofile.data

import com.strimup.feature.streamerprofile.data.request.UpdateProfileRequest
import com.strimup.feature.streamerprofile.data.response.StreamerOptionsResponse
import com.strimup.feature.streamerprofile.data.response.StreamerResponse
import com.strimup.feature.streamerprofile.data.response.TagResponse
import com.strimup.feature.streamerprofile.data.response.UpdateAvatarResponse
import com.strimup.feature.streamerprofile.data.response.UpdateProfileResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart
    @PUT("api/streamer/avatar")
    suspend fun updateAvatar(
        @Part file: MultipartBody.Part
    ): UpdateAvatarResponse

    @GET("/api/streamer/options")
    suspend fun getStreamerOptions(): StreamerOptionsResponse

    @GET("api/tag")
    suspend fun getTags(): List<TagResponse>
}

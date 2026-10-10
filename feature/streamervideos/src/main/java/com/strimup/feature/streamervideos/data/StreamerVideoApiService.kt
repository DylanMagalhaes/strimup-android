package com.strimup.feature.streamervideos.data

import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.feature.streamervideos.data.response.AddStreamerVideoResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface StreamerVideoApiService {
    @GET("api/streamer/videos/mine")
    suspend fun getMyVideos(): List<StreamerDto.Video>

    @Multipart
    @POST("api/streamer/videos")
    suspend fun addVideo(
        @Part video: MultipartBody.Part,
        @Part title: MultipartBody.Part,
        @Part description: MultipartBody.Part?,
    ): AddStreamerVideoResponse

    @DELETE("api/streamer/videos/{id}")
    suspend fun deleteVideo(@Path("id") id: String)
}

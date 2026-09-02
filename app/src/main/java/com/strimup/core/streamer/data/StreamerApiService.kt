package com.strimup.core.streamer.data

import com.strimup.core.streamer.data.request.StreamerMatchRequest
import com.strimup.core.streamer.data.request.UpdateProfileRequest
import com.strimup.core.streamer.data.response.FavoriteStreamerResponse
import com.strimup.core.streamer.data.response.FilterOptionsResponse
import com.strimup.core.streamer.data.response.InLiveStreamersResponse
import com.strimup.core.streamer.data.response.RandomStreamersResponse
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.data.response.StreamerMatchResponse
import com.strimup.core.streamer.data.response.StreamersResponse
import com.strimup.core.streamer.data.response.UpdateAvatarResponse
import com.strimup.core.streamer.data.response.UpdateProfileResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StreamerApiService {
    @GET("api/streamer/random")
    suspend fun getRandomStreamers(): RandomStreamersResponse

    @GET("api/streamer/live")
    suspend fun getInliveStreamers(): InLiveStreamersResponse

//    @GET("api/favorites")
//    suspend fun getFavoriteStreamers(): List<FavoriteStreamerResponse>

    @GET("api/streamer/search")
    suspend fun searchStreamers(
        @Query("q") query: String
    ): List<StreamersResponse>

    @GET("api/streamer/{id}")
    suspend fun getStreamerById(
        @Path("id") id: String
    ): StreamerDto

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
    suspend fun getStreamerOptions(): FilterOptionsResponse

    @POST("api/streamer/match")
    suspend fun getFilteredStreamers(@Body request: StreamerMatchRequest): StreamerMatchResponse
}

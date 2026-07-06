package com.strimup.feature.home.data

import com.strimup.feature.home.data.response.FavoriteStreamerResponse
import com.strimup.feature.home.data.response.InLiveStreamersResponse
import com.strimup.feature.home.data.response.RandomStreamersResponse
import com.strimup.feature.home.data.response.StreamersFoundResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StreamerApiService {
    @GET("api/streamer/random")
    suspend fun getRandomStreamers(): RandomStreamersResponse

    @GET("api/streamer/live")
    suspend fun getInliveStreamers(): InLiveStreamersResponse

    @GET("api/favorites")
    suspend fun getFavoriteStreamers(): List<FavoriteStreamerResponse>

    @GET("api/streamer/search")
    suspend fun searchStreamers(
        @Query("q") query: String
    ): List<StreamersFoundResponse>

}
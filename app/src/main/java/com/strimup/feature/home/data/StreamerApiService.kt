package com.strimup.feature.home.data

import com.strimup.feature.home.data.response.InLiveStreamersResponse
import com.strimup.feature.home.data.response.RandomStreamersResponse
import retrofit2.http.GET

interface StreamerApiService {
    @GET("api/streamer/random")
    suspend fun getRandomStreamers(): RandomStreamersResponse

    @GET("api/streamer/live")
    suspend fun getInliveStreamers(): InLiveStreamersResponse

}
package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.response.FilterOptionsResponse
import retrofit2.http.GET

interface StreamerApiService {

    @GET("/api/streamer/options")
    suspend fun getStreamerOptions(): FilterOptionsResponse
}
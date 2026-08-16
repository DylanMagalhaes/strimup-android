package com.strimup.feature.filter.data

import com.strimup.feature.filter.data.request.StreamerMatchRequest
import com.strimup.feature.filter.data.response.FilterOptionsResponse
import com.strimup.feature.filter.data.response.StreamerMatchResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StreamerApiService {

    @GET("/api/streamer/options")
    suspend fun getStreamerOptions(): FilterOptionsResponse

    @POST("api/streamer/match")
    suspend fun getFilteredStreamers(@Body request: StreamerMatchRequest): StreamerMatchResponse
}

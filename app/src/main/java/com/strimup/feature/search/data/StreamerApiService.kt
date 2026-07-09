package com.strimup.feature.search.data

import com.strimup.feature.search.data.response.StreamersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StreamerApiService {
    @GET("api/streamer/search")
    suspend fun searchStreamers(
        @Query("q") query: String
    ): List<StreamersResponse>
}
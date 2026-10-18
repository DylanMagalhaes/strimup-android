package com.strimup.feature.report.data

import com.strimup.feature.report.data.request.ReportStreamerRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportApiService {
    @POST("api/streamer/{id}/report")
    suspend fun reportStreamer(
        @Path("id") streamerId: String,
        @Body request: ReportStreamerRequest,
    )
}

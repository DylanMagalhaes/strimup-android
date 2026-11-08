package com.strimup.feature.report.data

import com.strimup.feature.report.data.request.ReportRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportApiService {
    @POST("api/streamer/{id}/report")
    suspend fun reportStreamer(
        @Path("id") streamerId: String,
        @Body request: ReportRequest,
    )

    @POST("api/cuts/{videoId}/report")
    suspend fun reportVideo(
        @Path("videoId") videoId: String,
        @Body request: ReportRequest,
    )
}

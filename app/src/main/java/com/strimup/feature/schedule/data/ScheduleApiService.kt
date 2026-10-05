package com.strimup.feature.schedule.data

import com.strimup.feature.schedule.data.request.CreateScheduleItemRequest
import com.strimup.feature.schedule.data.response.ScheduleResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScheduleApiService {
    @GET("api/streamer/schedule/streamer/{streamerId}")
    suspend fun getSchedule(@Path("streamerId") streamerId: String): List<ScheduleResponse>

    @DELETE("api/streamer/schedule/{id}")
    suspend fun deleteScheduleItem(@Path("id") id: String)

    @POST("api/streamer/schedule")
    suspend fun createScheduleItem(@Body request: CreateScheduleItemRequest): ScheduleResponse
}

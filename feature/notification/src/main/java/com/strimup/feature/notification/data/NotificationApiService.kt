package com.strimup.feature.notification.data

import com.strimup.feature.notification.data.response.NotificationPageResponse
import com.strimup.feature.notification.data.response.UnreadCountResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {
    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): NotificationPageResponse

    @GET("api/notifications/unread-count")
    suspend fun getUnreadCount(): UnreadCountResponse

    @POST("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String)

    @POST("api/notifications/read-all")
    suspend fun markAllAsRead()

    @DELETE("api/notifications/{id}")
    suspend fun delete(@Path("id") id: String)
}

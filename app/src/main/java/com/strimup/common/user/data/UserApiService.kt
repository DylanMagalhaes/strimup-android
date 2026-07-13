package com.strimup.common.user.data

import com.strimup.common.user.data.response.UserMeResponse
import retrofit2.http.GET

interface UserApiService {
    @GET("api/auth/me")
    suspend fun getCurrentUser(): UserMeResponse
}
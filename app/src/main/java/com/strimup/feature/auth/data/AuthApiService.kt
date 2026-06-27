package com.strimup.feature.auth.data

import com.strimup.feature.auth.data.response.UserLoggedResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body email: String, password: String): UserLoggedResponse
}
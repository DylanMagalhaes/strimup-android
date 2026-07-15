package com.strimup.feature.auth.data

import com.strimup.feature.auth.data.request.LoginRequest
import com.strimup.feature.auth.data.response.UserLoggedResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): UserLoggedResponse

    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): UserLoggedResponse
}
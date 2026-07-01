package com.strimup.feature.auth.data.remote

import com.strimup.feature.auth.data.local.AuthPreferencesDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor @Inject constructor(
    private val preferences: AuthPreferencesDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.encodedPath.contains("api/auth/refresh")) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            preferences.getAccessToken()
        }

        val requestBuilder = request.newBuilder()
            .header("X-Requested-With", "com.strimup.app")
            .header("Accept", "application/json")

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val newRequest = requestBuilder.build()

        return chain.proceed(newRequest)
    }
}
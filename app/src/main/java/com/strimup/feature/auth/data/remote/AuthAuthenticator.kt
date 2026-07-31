package com.strimup.feature.auth.data.remote

import com.strimup.feature.auth.data.AuthApiService
import com.strimup.feature.auth.data.local.AuthPreferencesDataSource
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

@Singleton
class AuthAuthenticator @Inject constructor(
    private val apiService: Provider<AuthApiService>,
    private val preferences: AuthPreferencesDataSource
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.contains("refresh")) {
            runBlocking(Dispatchers.IO) { preferences.clear() }
            return null
        }

        if (responseCount(response) >= 3) {
            return null
        }

        return synchronized(this) {
            runBlocking(Dispatchers.IO) {
                val currentAccessToken = preferences.getAccessToken()
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

                if (!currentAccessToken.isNullOrBlank() && currentAccessToken != requestToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                val refreshToken = preferences.getRefreshToken()

                if (refreshToken.isNullOrBlank()) {
                    return@runBlocking null
                }

                try {
                    val refreshResponse = apiService.get().refreshToken("Bearer $refreshToken")
                    val newAccessToken = refreshResponse.token
                    val newRefreshToken = refreshResponse.refreshToken

                    if (!newAccessToken.isNullOrBlank()) {
                        preferences.saveTokens(
                            accessToken = newAccessToken,
                            refreshToken = newRefreshToken ?: refreshToken
                        )

                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    } else {
                        preferences.clear()
                        null
                    }
                } catch (e: Exception) {
                    preferences.clear()
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
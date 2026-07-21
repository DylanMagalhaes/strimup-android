package com.strimup.feature.auth.data.remote

import com.strimup.feature.auth.data.AuthApiService
import com.strimup.feature.auth.data.local.AuthPreferencesDataSource
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AuthAuthenticator @Inject constructor(
    private val apiService: Provider<AuthApiService>,
    private val preferences: AuthPreferencesDataSource
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.contains("api/auth/refresh")) {
            runBlocking(Dispatchers.IO) { preferences.clear() }
            return null
        }

        if (responseCount(response) >= 3) {
            return null
        }

        val refreshToken = runBlocking(Dispatchers.IO) { preferences.getRefreshToken() }

        if (refreshToken.isNullOrBlank()) {
            return null
        }

        return synchronized(this) {
            val currentToken = runBlocking(Dispatchers.IO) { preferences.getAccessToken() }

            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

            if (currentToken != null && currentToken != requestToken) {
                return@synchronized response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            try {
                val refreshResponse = runBlocking(Dispatchers.IO) {
                    apiService.get().refreshToken("Bearer $refreshToken")
                }

                val newToken = refreshResponse.token

                if (newToken.isNotBlank()) {
                    runBlocking(Dispatchers.IO) {
                        preferences.saveAuthToken(newToken)
                    }

                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                } else {
                    runBlocking(Dispatchers.IO) { preferences.clear() }
                    null
                }
            } catch (e: Exception) {
                runBlocking(Dispatchers.IO) { preferences.clear() }
                null
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
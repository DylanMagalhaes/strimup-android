package com.strimup.feature.auth.data.remote

import com.strimup.feature.auth.data.AuthApiService
import com.strimup.feature.auth.data.local.AuthPreferencesDataSource
import javax.inject.Inject
import javax.inject.Provider
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

        val refreshToken = runBlocking { preferences.getRefreshToken() }

        if (refreshToken.isNullOrBlank()) {
            return null
        }

        return synchronized(this) {
            val currentToken = runBlocking { preferences.getAccessToken() }

            if (response.request.header("Authorization") != "Bearer $currentToken") {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            try {
                val refreshResponse = runBlocking {
                    apiService.get().refreshToken("Bearer $refreshToken")
                }

                val newToken = refreshResponse.token

                if (newToken.isNotEmpty()) {
                    runBlocking {
                        preferences.saveAuthToken(newToken)
                    }

                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                } else {
                    null
                }
            } catch (e: Exception) {
                runBlocking { preferences.clear() }
                null
            }
        }
    }
}
package com.strimup.feature.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_AUTH_TOKEN]
    }

    suspend fun getAccessToken(): String? {
        return dataStore.data.first()[KEY_AUTH_TOKEN]
    }

    suspend fun getRefreshToken(): String? {
        return dataStore.data.first()[KEY_REFRESH_TOKEN]
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_AUTH_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
        }
    }

}
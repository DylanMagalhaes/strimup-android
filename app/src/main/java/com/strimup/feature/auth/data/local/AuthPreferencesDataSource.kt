package com.strimup.feature.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.strimup.core.security.SecretCipher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cipher: SecretCipher,
) {
    suspend fun getAccessToken(): String? = readSecret(AccessToken)

    suspend fun getRefreshToken(): String? = readSecret(RefreshToken)

    suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        val encryptedAccessToken = encrypt(accessToken)
        val encryptedRefreshToken = refreshToken?.takeIf { it.isNotBlank() }?.let { encrypt(it) }

        dataStore.edit { preferences ->
            preferences[AccessToken.key] = encryptedAccessToken
            preferences.remove(AccessToken.legacyKey)
            if (encryptedRefreshToken != null) {
                preferences[RefreshToken.key] = encryptedRefreshToken
                preferences.remove(RefreshToken.legacyKey)
            }
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            listOf(AccessToken, RefreshToken).forEach { secret ->
                preferences.remove(secret.key)
                preferences.remove(secret.legacyKey)
            }
        }
    }

    private suspend fun readSecret(secret: StoredSecret): String? {
        val preferences = dataStore.data.first()
        val encryptedValue = preferences[secret.key]
        val legacyValue = preferences[secret.legacyKey]

        return when {
            encryptedValue != null -> decrypt(encryptedValue)
            legacyValue != null -> legacyValue.also { migrateLegacySecret(secret, it) }
            else -> null
        }
    }

    private suspend fun migrateLegacySecret(secret: StoredSecret, plainValue: String) {
        val encryptedValue = encrypt(plainValue)
        dataStore.edit { preferences ->
            preferences[secret.key] = encryptedValue
            preferences.remove(secret.legacyKey)
        }
    }

    private suspend fun encrypt(value: String): String = withContext(Dispatchers.Default) { cipher.encrypt(value) }

    private suspend fun decrypt(value: String): String? = withContext(Dispatchers.Default) { cipher.decrypt(value) }

    private sealed class StoredSecret(keyName: String, legacyKeyName: String) {
        val key = stringPreferencesKey(keyName)
        val legacyKey = stringPreferencesKey(legacyKeyName)
    }

    private data object AccessToken : StoredSecret("auth_token_encrypted", "auth_token")

    private data object RefreshToken : StoredSecret("refresh_token_encrypted", "refresh_token")
}

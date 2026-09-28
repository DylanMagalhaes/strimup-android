package com.strimup.feature.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.strimup.core.security.SecretCipher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class OAuthVerifierDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cipher: SecretCipher,
) {
    companion object {
        private val KEY_PKCE_VERIFIER = stringPreferencesKey("oauth_pkce_verifier")
    }

    suspend fun save(verifier: String) {
        val encrypted = withContext(Dispatchers.Default) { cipher.encrypt(verifier) }
        dataStore.edit { preferences ->
            preferences[KEY_PKCE_VERIFIER] = encrypted
        }
    }

    suspend fun get(): String? {
        val encrypted = dataStore.data.first()[KEY_PKCE_VERIFIER] ?: return null
        return withContext(Dispatchers.Default) { cipher.decrypt(encrypted) }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_PKCE_VERIFIER)
        }
    }
}

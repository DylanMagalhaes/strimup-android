package com.strimup.feature.auth.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.common.truth.Truth.assertThat
import com.strimup.core.security.SecretCipher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AuthPreferencesDataSourceTest {

    private class FakeSecretCipher : SecretCipher {
        var canDecrypt = true

        override fun encrypt(plainText: String): String = "$PREFIX${plainText.reversed()}"

        override fun decrypt(cipherText: String): String? =
            cipherText.removePrefix(PREFIX).reversed().takeIf { canDecrypt && cipherText.startsWith(PREFIX) }

        companion object {
            const val PREFIX = "enc:"
        }
    }

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val cipher = FakeSecretCipher()

    private val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(
            scope = dataStoreScope,
            produceFile = { temporaryFolder.newFile("auth.preferences_pb") },
        )
    }

    private val dataSource by lazy { AuthPreferencesDataSource(dataStore, cipher) }

    private suspend fun rawValues(): Map<String, Any> =
        dataStore.data.first().asMap().mapKeys { (key, _) -> key.name }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `saved tokens should be read back`() = runTest {
        dataSource.saveTokens(accessToken = "access", refreshToken = "refresh")

        assertThat(dataSource.getAccessToken()).isEqualTo("access")
        assertThat(dataSource.getRefreshToken()).isEqualTo("refresh")
    }

    @Test
    fun `tokens should never be stored in clear`() = runTest {
        dataSource.saveTokens(accessToken = "access", refreshToken = "refresh")

        val values = rawValues().values.map { it.toString() }
        assertThat(values).containsNoneOf("access", "refresh")
        assertThat(values.all { it.startsWith(FakeSecretCipher.PREFIX) }).isTrue()
    }

    @Test
    fun `saving without refresh token should keep the previous one`() = runTest {
        dataSource.saveTokens(accessToken = "access", refreshToken = "refresh")

        dataSource.saveTokens(accessToken = "new-access", refreshToken = null)

        assertThat(dataSource.getAccessToken()).isEqualTo("new-access")
        assertThat(dataSource.getRefreshToken()).isEqualTo("refresh")
    }

    @Test
    fun `legacy clear tokens should still be read and then migrated`() = runTest {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("auth_token")] = "legacy-access"
            preferences[stringPreferencesKey("refresh_token")] = "legacy-refresh"
        }

        assertThat(dataSource.getAccessToken()).isEqualTo("legacy-access")
        assertThat(dataSource.getRefreshToken()).isEqualTo("legacy-refresh")

        val values = rawValues()
        assertThat(values.keys).containsExactly("auth_token_encrypted", "refresh_token_encrypted")
        assertThat(values.values.map { it.toString() }).containsNoneOf("legacy-access", "legacy-refresh")
        assertThat(dataSource.getAccessToken()).isEqualTo("legacy-access")
    }

    @Test
    fun `undecryptable tokens should be treated as missing`() = runTest {
        dataSource.saveTokens(accessToken = "access", refreshToken = "refresh")
        cipher.canDecrypt = false

        assertThat(dataSource.getAccessToken()).isNull()
        assertThat(dataSource.getRefreshToken()).isNull()
    }

    @Test
    fun `clear should remove encrypted and legacy tokens but keep other preferences`() = runTest {
        dataSource.saveTokens(accessToken = "access", refreshToken = "refresh")
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey("auth_token")] = "legacy-access"
            preferences[stringPreferencesKey("other")] = "kept"
        }

        dataSource.clear()

        assertThat(dataSource.getAccessToken()).isNull()
        assertThat(dataSource.getRefreshToken()).isNull()
        assertThat(rawValues()).containsExactly("other", "kept")
    }
}

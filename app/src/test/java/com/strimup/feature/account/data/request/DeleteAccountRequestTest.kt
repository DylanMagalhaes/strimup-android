package com.strimup.feature.account.data.request

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Test

class DeleteAccountRequestTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `request without password should not send the password field`() {
        val body = json.encodeToString(DeleteAccountRequest(confirmation = "SUPPRIMER"))

        assertThat(body).isEqualTo("""{"confirmation":"SUPPRIMER"}""")
    }

    @Test
    fun `request with password should send both fields`() {
        val body = json.encodeToString(DeleteAccountRequest(confirmation = "SUPPRIMER", password = "Secret123!"))

        assertThat(body).isEqualTo("""{"confirmation":"SUPPRIMER","password":"Secret123!"}""")
    }
}

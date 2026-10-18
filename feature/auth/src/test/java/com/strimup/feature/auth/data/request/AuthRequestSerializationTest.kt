package com.strimup.feature.auth.data.request

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import org.junit.Test

class AuthRequestSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun JsonObject.acceptedTerms(): JsonPrimitive = getValue("accepted_terms") as JsonPrimitive

    @Test
    fun `register request should send accepted_terms as a JSON boolean`() {
        val request = RegisterRequest(
            role = "viewer",
            userName = "Inox",
            password = "Password123!",
            gender = "male",
            email = "inox@test.com",
            birthDate = "1995-05-05",
            acceptedTerms = true,
        )

        val body = json.parseToJsonElement(json.encodeToString(request)).jsonObject

        assertThat(body.acceptedTerms().isString).isFalse()
        assertThat(body.acceptedTerms().boolean).isTrue()
    }

    @Test
    fun `OAuth complete request should send accepted_terms as a JSON boolean`() {
        val request = OAuthCompleteRequest(
            tmp = "tmp",
            codeVerifier = "verifier",
            role = "viewer",
            birthDate = "1995-05-05",
            gender = "male",
            acceptedTerms = true,
        )

        val body = json.parseToJsonElement(json.encodeToString(request)).jsonObject

        assertThat(body.acceptedTerms().isString).isFalse()
        assertThat(body.acceptedTerms().boolean).isTrue()
    }
}

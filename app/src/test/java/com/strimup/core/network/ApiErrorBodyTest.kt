package com.strimup.core.network

import com.google.common.truth.Truth.assertThat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ApiErrorBodyTest {

    private fun httpException(code: Int, body: String): HttpException {
        val responseBody = body.toResponseBody("application/json".toMediaTypeOrNull())
        return HttpException(Response.error<Any>(code, responseBody))
    }

    @Test
    fun `apiErrorMessage should return the backend message`() {
        val exception = httpException(401, """{ "message": "Mot de passe incorrect" }""")

        assertThat(exception.apiErrorMessage()).isEqualTo("Mot de passe incorrect")
    }

    @Test
    fun `apiErrorMessage should ignore unknown fields`() {
        val exception = httpException(409, """{ "message": "Commande en cours", "code": "UGC" }""")

        assertThat(exception.apiErrorMessage()).isEqualTo("Commande en cours")
    }

    @Test
    fun `apiErrorMessage should return null when the message is blank`() {
        val exception = httpException(403, """{ "message": "  " }""")

        assertThat(exception.apiErrorMessage()).isNull()
    }

    @Test
    fun `apiErrorMessage should return null when the body is not the expected JSON`() {
        val exception = httpException(500, "<html>Internal error</html>")

        assertThat(exception.apiErrorMessage()).isNull()
    }

    @Test
    fun `apiErrorMessage should return null when the body has no message`() {
        val exception = httpException(401, """{ "error": "expired" }""")

        assertThat(exception.apiErrorMessage()).isNull()
    }
}

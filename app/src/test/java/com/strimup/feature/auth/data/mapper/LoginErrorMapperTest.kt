package com.strimup.feature.auth.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class LoginErrorMapperTest {

    private fun httpException(code: Int, body: String = "") =
        HttpException(Response.error<Any>(code, body.toResponseBody("application/json".toMediaTypeOrNull())))

    @Test
    fun `401 should become an invalid credentials error keeping the server message`() {
        val response = httpException(401, """{ "message": "E-mail ou mot de passe incorrect" }""")

        val error = response.asInvalidCredentialsError()

        assertThat((error as DomainException).error)
            .isEqualTo(DomainError.Server(401, "E-mail ou mot de passe incorrect"))
    }

    @Test
    fun `other errors should stay untouched`() {
        val serverError = httpException(500)
        val networkError = IOException()

        assertThat(serverError.asInvalidCredentialsError()).isSameInstanceAs(serverError)
        assertThat(networkError.asInvalidCredentialsError()).isSameInstanceAs(networkError)
    }
}

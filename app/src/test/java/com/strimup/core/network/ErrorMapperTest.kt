package com.strimup.core.network

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorMapperTest {

    private fun httpExceptionWithCode(code: Int): HttpException {
        val body = "".toResponseBody("application/json".toMediaTypeOrNull())
        return HttpException(Response.error<Any>(code, body))
    }

    @Test
    fun `SocketTimeoutException should map to Timeout`() {
        val error = SocketTimeoutException().toDomainError()
        assertThat(error).isEqualTo(DomainError.Timeout)
    }

    @Test
    fun `IOException should map to Network`() {
        val error = IOException().toDomainError()
        assertThat(error).isEqualTo(DomainError.Network)
    }

    @Test
    fun `HttpException with code 401 should map to Unauthorized`() {
        val error = httpExceptionWithCode(401).toDomainError()
        assertThat(error).isEqualTo(DomainError.Unauthorized)
    }

    @Test
    fun `HttpException with a 4xx code other than 401 should map to Server with that code`() {
        val error = httpExceptionWithCode(404).toDomainError()
        assertThat(error).isEqualTo(DomainError.Server(404))
    }

    @Test
    fun `HttpException with a 5xx code should map to Server with that code`() {
        val error = httpExceptionWithCode(500).toDomainError()
        assertThat(error).isEqualTo(DomainError.Server(500))
    }

    @Test
    fun `SerializationException should map to Serialization`() {
        val error = SerializationException().toDomainError()
        assertThat(error).isEqualTo(DomainError.Serialization)
    }

    @Test
    fun `any other throwable should map to Unknown`() {
        val error = IllegalStateException("peu importe").toDomainError()
        assertThat(error).isEqualTo(DomainError.Unknown)
    }
}

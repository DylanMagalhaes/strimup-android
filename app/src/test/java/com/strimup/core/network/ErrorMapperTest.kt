package com.strimup.core.network

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
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

    @Test
    fun `a DomainException should unwrap to its own DomainError instead of remapping to Unknown`() {
        val error = DomainException(DomainError.Network).toDomainError()
        assertThat(error).isEqualTo(DomainError.Network)
    }

    @Test
    fun `toDomainResult on a success should return it unchanged`() {
        val result = Result.success("streamer")

        val mapped = result.toDomainResult()

        assertThat(mapped).isEqualTo(result)
    }

    @Test
    fun `toDomainResult on a failure should wrap the mapped DomainError in a DomainException`() {
        val result = Result.failure<String>(IOException())

        val mapped = result.toDomainResult()

        val exception = mapped.exceptionOrNull() as DomainException
        assertThat(exception.error).isEqualTo(DomainError.Network)
    }

    @Test
    fun `toDomainResult on a failure already wrapped in a DomainException should leave it untouched`() {
        val original = DomainException(DomainError.Unauthorized)
        val result = Result.failure<String>(original)

        val mapped = result.toDomainResult()

        assertThat(mapped.exceptionOrNull()).isSameInstanceAs(original)
    }
}

package com.strimup.feature.account.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class AccountDeletionErrorMapperTest {

    private fun httpException(code: Int, message: String? = null): HttpException {
        val body = message?.let { """{ "message": "$it" }""" } ?: ""
        val responseBody = body.toResponseBody("application/json".toMediaTypeOrNull())
        return HttpException(Response.error<Any>(code, responseBody))
    }

    @Test
    fun `401 should map to InvalidPassword with the backend message`() {
        val error = httpException(401, "Mot de passe incorrect").toAccountDeletionError()

        assertThat(error).isEqualTo(AccountDeletionError.InvalidPassword("Mot de passe incorrect"))
    }

    @Test
    fun `403 should map to NotAllowed with the backend message`() {
        val error = httpException(403, "Compte administrateur").toAccountDeletionError()

        assertThat(error).isEqualTo(AccountDeletionError.NotAllowed("Compte administrateur"))
    }

    @Test
    fun `409 should map to PendingObligations with the backend message`() {
        val error = httpException(409, "Commande UGC en cours").toAccountDeletionError()

        assertThat(error).isEqualTo(AccountDeletionError.PendingObligations("Commande UGC en cours"))
    }

    @Test
    fun `409 without body should map to PendingObligations without message`() {
        val error = httpException(409).toAccountDeletionError()

        assertThat(error).isEqualTo(AccountDeletionError.PendingObligations(null))
    }

    @Test
    fun `429 should map to TooManyAttempts`() {
        val error = httpException(429, "Trop de requêtes").toAccountDeletionError()

        assertThat(error).isEqualTo(AccountDeletionError.TooManyAttempts)
    }

    @Test
    fun `400 should map to Unknown`() {
        val error = httpException(400, "Confirmation invalide").toAccountDeletionError()

        assertThat(error).isEqualTo(AccountDeletionError.Unknown)
    }

    @Test
    fun `500 should map to Unknown`() {
        val error = httpException(500).toAccountDeletionError()

        assertThat(error).isEqualTo(AccountDeletionError.Unknown)
    }

    @Test
    fun `IOException should map to Network`() {
        assertThat(IOException().toAccountDeletionError()).isEqualTo(AccountDeletionError.Network)
    }

    @Test
    fun `SocketTimeoutException should map to Network`() {
        assertThat(SocketTimeoutException().toAccountDeletionError()).isEqualTo(AccountDeletionError.Network)
    }

    @Test
    fun `AccountDeletionException should keep its own error`() {
        val exception = AccountDeletionException(AccountDeletionError.InvalidConfirmation)

        assertThat(exception.toAccountDeletionError()).isEqualTo(AccountDeletionError.InvalidConfirmation)
    }

    @Test
    fun `unexpected exception should map to Unknown`() {
        assertThat(IllegalStateException().toAccountDeletionError()).isEqualTo(AccountDeletionError.Unknown)
    }

    @Test
    fun `isAccountAlreadyDeleted should be true only for a 404`() {
        assertThat(httpException(404).isAccountAlreadyDeleted()).isTrue()
        assertThat(httpException(401).isAccountAlreadyDeleted()).isFalse()
        assertThat(IOException().isAccountAlreadyDeleted()).isFalse()
    }

    @Test
    fun `toAccountDeletionResult should keep a success untouched`() {
        val result = Result.success(Unit).toAccountDeletionResult()

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `toAccountDeletionResult should wrap a failure into an AccountDeletionException`() {
        val result = Result.failure<Unit>(IOException()).toAccountDeletionResult()

        val exception = result.exceptionOrNull() as AccountDeletionException
        assertThat(exception.error).isEqualTo(AccountDeletionError.Network)
    }

    @Test
    fun `toAccountDeletionResult should rethrow a CancellationException`() {
        assertThrows(CancellationException::class.java) {
            Result.failure<Unit>(CancellationException()).toAccountDeletionResult()
        }
    }
}

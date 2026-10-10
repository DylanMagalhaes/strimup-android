package com.strimup.feature.account.data.repository

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.account.data.AccountApiService
import com.strimup.feature.account.data.DefaultAccountRepository
import com.strimup.feature.account.data.request.DeleteAccountRequest
import com.strimup.feature.account.data.response.AccountResponse
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy
import com.strimup.feature.auth.data.local.LocalSessionDataSource
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DefaultAccountRepositoryTest {

    private class FakeAccountApiService(
        private val account: () -> AccountResponse = {
            AccountResponse(user = AccountResponse.AccountData(isTwitchConnected = false))
        },
        private val delete: (DeleteAccountRequest) -> Unit = {},
    ) : AccountApiService {
        val deleteRequests = mutableListOf<DeleteAccountRequest>()

        override suspend fun getAccount(): AccountResponse = account()

        override suspend fun deleteAccount(request: DeleteAccountRequest) {
            deleteRequests += request
            delete(request)
        }
    }

    private class FakeLocalSessionDataSource : LocalSessionDataSource {
        var clearCount = 0

        override suspend fun clear() {
            clearCount++
        }
    }

    private fun httpException(code: Int, message: String? = null): HttpException {
        val body = message?.let { """{ "message": "$it" }""" } ?: ""
        val responseBody = body.toResponseBody("application/json".toMediaTypeOrNull())
        return HttpException(Response.error<Any>(code, responseBody))
    }

    @Test
    fun `getDeletionPolicy should require a password for an e-mail account`() = runTest {
        val repository = DefaultAccountRepository(FakeAccountApiService(), FakeLocalSessionDataSource())

        val policy = repository.getDeletionPolicy().getOrThrow()

        assertThat(policy).isEqualTo(AccountDeletionPolicy(isPasswordRequired = true))
    }

    @Test
    fun `getDeletionPolicy should not require a password for a Twitch account`() = runTest {
        val service = FakeAccountApiService(
            account = { AccountResponse(user = AccountResponse.AccountData(isTwitchConnected = true)) },
        )
        val repository = DefaultAccountRepository(service, FakeLocalSessionDataSource())

        val policy = repository.getDeletionPolicy().getOrThrow()

        assertThat(policy).isEqualTo(AccountDeletionPolicy(isPasswordRequired = false))
    }

    @Test
    fun `getDeletionPolicy when offline should fail with a Network DomainError`() = runTest {
        val service = FakeAccountApiService(account = { throw IOException() })
        val repository = DefaultAccountRepository(service, FakeLocalSessionDataSource())

        val exception = repository.getDeletionPolicy().exceptionOrNull() as DomainException

        assertThat(exception.error).isEqualTo(DomainError.Network)
    }

    @Test
    fun `deleteAccount should send the confirmation word and the password then clear the local session`() =
        runTest {
            val service = FakeAccountApiService()
            val localSession = FakeLocalSessionDataSource()
            val repository = DefaultAccountRepository(service, localSession)

            val result = repository.deleteAccount(password = "Secret123!")

            assertThat(result.isSuccess).isTrue()
            assertThat(service.deleteRequests).containsExactly(
                DeleteAccountRequest(confirmation = "SUPPRIMER", password = "Secret123!")
            )
            assertThat(localSession.clearCount).isEqualTo(1)
        }

    @Test
    fun `deleteAccount without password should not send one`() = runTest {
        val service = FakeAccountApiService()
        val repository = DefaultAccountRepository(service, FakeLocalSessionDataSource())

        repository.deleteAccount(password = null)

        assertThat(service.deleteRequests.single().password).isNull()
    }

    @Test
    fun `deleteAccount when the account is already deleted should succeed and clear the local session`() =
        runTest {
            val service = FakeAccountApiService(delete = { throw httpException(404, "Compte introuvable") })
            val localSession = FakeLocalSessionDataSource()
            val repository = DefaultAccountRepository(service, localSession)

            val result = repository.deleteAccount(password = null)

            assertThat(result.isSuccess).isTrue()
            assertThat(localSession.clearCount).isEqualTo(1)
        }

    @Test
    fun `deleteAccount with a wrong password should fail with InvalidPassword and keep the local session`() =
        runTest {
            val service = FakeAccountApiService(delete = { throw httpException(401, "Mot de passe incorrect") })
            val localSession = FakeLocalSessionDataSource()
            val repository = DefaultAccountRepository(service, localSession)

            val exception = repository.deleteAccount(password = "wrong").exceptionOrNull() as AccountDeletionException

            assertThat(exception.error).isEqualTo(AccountDeletionError.InvalidPassword("Mot de passe incorrect"))
            assertThat(localSession.clearCount).isEqualTo(0)
        }

    @Test
    fun `deleteAccount with pending obligations should fail with PendingObligations and keep the local session`() =
        runTest {
            val service = FakeAccountApiService(delete = { throw httpException(409, "Commande UGC en cours") })
            val localSession = FakeLocalSessionDataSource()
            val repository = DefaultAccountRepository(service, localSession)

            val exception = repository.deleteAccount(password = null).exceptionOrNull() as AccountDeletionException

            assertThat(exception.error).isEqualTo(AccountDeletionError.PendingObligations("Commande UGC en cours"))
            assertThat(localSession.clearCount).isEqualTo(0)
        }

    @Test
    fun `deleteAccount when offline should fail with Network and keep the local session`() = runTest {
        val service = FakeAccountApiService(delete = { throw IOException() })
        val localSession = FakeLocalSessionDataSource()
        val repository = DefaultAccountRepository(service, localSession)

        val exception = repository.deleteAccount(password = null).exceptionOrNull() as AccountDeletionException

        assertThat(exception.error).isEqualTo(AccountDeletionError.Network)
        assertThat(localSession.clearCount).isEqualTo(0)
    }
}

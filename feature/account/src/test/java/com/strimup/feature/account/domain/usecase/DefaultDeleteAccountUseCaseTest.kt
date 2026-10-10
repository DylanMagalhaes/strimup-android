package com.strimup.feature.account.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.account.domain.AccountRepository
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy
import com.strimup.feature.push.domain.usecase.DeletePushTokenUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultDeleteAccountUseCaseTest {

    private var pushTokenDeletions = 0
    private val deletePushToken = DeletePushTokenUseCase {
        pushTokenDeletions++
        Result.success(Unit)
    }

    private class FakeAccountRepository : AccountRepository {
        val deletedWithPasswords = mutableListOf<String?>()

        override suspend fun getDeletionPolicy(): Result<AccountDeletionPolicy> =
            Result.success(AccountDeletionPolicy(isPasswordRequired = true))

        override suspend fun deleteAccount(password: String?): Result<Unit> {
            deletedWithPasswords += password
            return Result.success(Unit)
        }
    }

    @Test
    fun `invoke with the exact confirmation word should delete the account with the password`() = runTest {
        val repository = FakeAccountRepository()
        val useCase = DefaultDeleteAccountUseCase(repository, deletePushToken)

        val result = useCase(confirmation = "SUPPRIMER", password = "Secret123!")

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.deletedWithPasswords).containsExactly("Secret123!")
    }

    @Test
    fun `invoke with a lowercase confirmation should fail without calling the repository`() = runTest {
        val repository = FakeAccountRepository()
        val useCase = DefaultDeleteAccountUseCase(repository, deletePushToken)

        val result = useCase(confirmation = "supprimer", password = null)
        val exception = result.exceptionOrNull() as AccountDeletionException

        assertThat(exception.error).isEqualTo(AccountDeletionError.InvalidConfirmation)
        assertThat(repository.deletedWithPasswords).isEmpty()
    }

    @Test
    fun `invoke with surrounding spaces should fail without calling the repository`() = runTest {
        val repository = FakeAccountRepository()
        val useCase = DefaultDeleteAccountUseCase(repository, deletePushToken)

        val result = useCase(confirmation = " SUPPRIMER ", password = null)

        assertThat(result.isFailure).isTrue()
        assertThat(repository.deletedWithPasswords).isEmpty()
    }

    @Test
    fun `invoke with an empty password should not send it`() = runTest {
        val repository = FakeAccountRepository()
        val useCase = DefaultDeleteAccountUseCase(repository, deletePushToken)

        useCase(confirmation = "SUPPRIMER", password = "")

        assertThat(repository.deletedWithPasswords).containsExactly(null)
    }

    @Test
    fun `successful deletion should delete the local push token`() = runTest {
        val useCase = DefaultDeleteAccountUseCase(FakeAccountRepository(), deletePushToken)

        useCase(confirmation = "SUPPRIMER", password = null)

        assertThat(pushTokenDeletions).isEqualTo(1)
    }

    @Test
    fun `failed deletion should keep the local push token`() = runTest {
        val useCase = DefaultDeleteAccountUseCase(FakeAccountRepository(), deletePushToken)

        useCase(confirmation = "supprimer", password = null)

        assertThat(pushTokenDeletions).isEqualTo(0)
    }

    @Test
    fun `push token deletion failure should not fail the account deletion`() = runTest {
        val useCase = DefaultDeleteAccountUseCase(
            FakeAccountRepository(),
            DeletePushTokenUseCase { Result.failure(IllegalStateException()) },
        )

        val result = useCase(confirmation = "SUPPRIMER", password = null)

        assertThat(result.isSuccess).isTrue()
    }
}

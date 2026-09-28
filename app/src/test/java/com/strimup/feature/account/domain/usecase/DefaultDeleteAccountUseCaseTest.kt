package com.strimup.feature.account.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.account.domain.AccountRepository
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultDeleteAccountUseCaseTest {

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
        val useCase = DefaultDeleteAccountUseCase(repository)

        val result = useCase(confirmation = "SUPPRIMER", password = "Secret123!")

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.deletedWithPasswords).containsExactly("Secret123!")
    }

    @Test
    fun `invoke with a lowercase confirmation should fail without calling the repository`() = runTest {
        val repository = FakeAccountRepository()
        val useCase = DefaultDeleteAccountUseCase(repository)

        val result = useCase(confirmation = "supprimer", password = null)
        val exception = result.exceptionOrNull() as AccountDeletionException

        assertThat(exception.error).isEqualTo(AccountDeletionError.InvalidConfirmation)
        assertThat(repository.deletedWithPasswords).isEmpty()
    }

    @Test
    fun `invoke with surrounding spaces should fail without calling the repository`() = runTest {
        val repository = FakeAccountRepository()
        val useCase = DefaultDeleteAccountUseCase(repository)

        val result = useCase(confirmation = " SUPPRIMER ", password = null)

        assertThat(result.isFailure).isTrue()
        assertThat(repository.deletedWithPasswords).isEmpty()
    }

    @Test
    fun `invoke with an empty password should not send it`() = runTest {
        val repository = FakeAccountRepository()
        val useCase = DefaultDeleteAccountUseCase(repository)

        useCase(confirmation = "SUPPRIMER", password = "")

        assertThat(repository.deletedWithPasswords).containsExactly(null)
    }
}

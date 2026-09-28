package com.strimup.feature.account.domain.usecase

import com.strimup.feature.account.domain.AccountRepository
import com.strimup.feature.account.domain.entity.AccountDeletionConfirmation
import com.strimup.feature.account.domain.entity.AccountDeletionError
import com.strimup.feature.account.domain.entity.AccountDeletionException
import javax.inject.Inject

class DefaultDeleteAccountUseCase @Inject constructor(
    private val repository: AccountRepository,
) : DeleteAccountUseCase {
    override suspend fun invoke(confirmation: String, password: String?): Result<Unit> {
        if (!AccountDeletionConfirmation.isValid(confirmation)) {
            return Result.failure(AccountDeletionException(AccountDeletionError.InvalidConfirmation))
        }

        return repository.deleteAccount(password?.takeIf { it.isNotEmpty() })
    }
}

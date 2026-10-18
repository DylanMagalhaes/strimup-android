package com.strimup.feature.account.domain.usecase

import com.strimup.feature.account.domain.AccountRepository
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy
import javax.inject.Inject

class DefaultGetAccountDeletionPolicyUseCase @Inject constructor(
    private val repository: AccountRepository,
) : GetAccountDeletionPolicyUseCase {
    override suspend fun invoke(): Result<AccountDeletionPolicy> {
        return repository.getDeletionPolicy()
    }
}

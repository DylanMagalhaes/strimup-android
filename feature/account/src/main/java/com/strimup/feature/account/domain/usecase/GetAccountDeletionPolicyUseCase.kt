package com.strimup.feature.account.domain.usecase

import com.strimup.feature.account.domain.entity.AccountDeletionPolicy

fun interface GetAccountDeletionPolicyUseCase {
    suspend operator fun invoke(): Result<AccountDeletionPolicy>
}

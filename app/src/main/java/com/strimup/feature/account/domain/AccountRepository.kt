package com.strimup.feature.account.domain

import com.strimup.feature.account.domain.entity.AccountDeletionPolicy

interface AccountRepository {
    suspend fun getDeletionPolicy(): Result<AccountDeletionPolicy>

    suspend fun deleteAccount(password: String?): Result<Unit>
}

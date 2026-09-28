package com.strimup.feature.account.data

import com.strimup.core.network.toDomainResult
import com.strimup.feature.account.data.mapper.isAccountAlreadyDeleted
import com.strimup.feature.account.data.mapper.toAccountDeletionResult
import com.strimup.feature.account.data.mapper.toDeletionPolicy
import com.strimup.feature.account.data.request.DeleteAccountRequest
import com.strimup.feature.account.domain.AccountRepository
import com.strimup.feature.account.domain.entity.AccountDeletionConfirmation
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy
import com.strimup.feature.auth.data.local.LocalSessionDataSource
import javax.inject.Inject

class DefaultAccountRepository @Inject constructor(
    private val service: AccountApiService,
    private val localSession: LocalSessionDataSource,
) : AccountRepository {

    override suspend fun getDeletionPolicy(): Result<AccountDeletionPolicy> {
        return runCatching {
            service.getAccount().toDeletionPolicy()
        }.toDomainResult()
    }

    override suspend fun deleteAccount(password: String?): Result<Unit> {
        val request = DeleteAccountRequest(
            confirmation = AccountDeletionConfirmation.WORD,
            password = password,
        )

        return runCatching { service.deleteAccount(request) }
            .recoverCatching { throwable -> if (!throwable.isAccountAlreadyDeleted()) throw throwable }
            .mapCatching { localSession.clear() }
            .toAccountDeletionResult()
    }
}

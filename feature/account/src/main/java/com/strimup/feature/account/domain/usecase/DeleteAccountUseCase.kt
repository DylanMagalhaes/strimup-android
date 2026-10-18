package com.strimup.feature.account.domain.usecase

fun interface DeleteAccountUseCase {
    suspend operator fun invoke(confirmation: String, password: String?): Result<Unit>
}

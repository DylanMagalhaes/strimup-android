package com.strimup.feature.account.data.mapper

import com.strimup.feature.account.data.response.AccountResponse
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy

fun AccountResponse.toDeletionPolicy(): AccountDeletionPolicy {
    return AccountDeletionPolicy(isPasswordRequired = !user.isTwitchConnected)
}

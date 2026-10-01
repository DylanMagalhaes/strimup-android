package com.strimup.feature.push.domain.usecase

import kotlinx.coroutines.flow.Flow

fun interface ObserveShouldAskNotificationPermissionUseCase {
    operator fun invoke(): Flow<Boolean>
}

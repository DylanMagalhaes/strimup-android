package com.strimup.feature.push.domain.usecase

import com.strimup.feature.push.domain.entity.PushMessage

fun interface ProcessIncomingPushUseCase {
    suspend operator fun invoke(message: PushMessage): Boolean
}

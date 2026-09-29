package com.strimup.feature.notification.domain.usecase

import kotlinx.coroutines.flow.StateFlow

fun interface ObserveUnreadCountUseCase {
    operator fun invoke(): StateFlow<Int>
}

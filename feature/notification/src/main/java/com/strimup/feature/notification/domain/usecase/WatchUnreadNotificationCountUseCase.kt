package com.strimup.feature.notification.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.seconds

val UNREAD_COUNT_REFRESH_INTERVAL = 60.seconds

fun interface WatchUnreadNotificationCountUseCase {
    operator fun invoke(): Flow<Int>
}

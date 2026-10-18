package com.strimup.feature.notification.domain.usecase

import com.strimup.feature.notification.domain.entity.NotificationPage

const val NOTIFICATIONS_PAGE_SIZE = 20

fun interface GetNotificationsPageUseCase {
    suspend operator fun invoke(offset: Int): Result<NotificationPage>
}

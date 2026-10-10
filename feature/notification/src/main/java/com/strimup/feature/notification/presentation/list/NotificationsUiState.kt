package com.strimup.feature.notification.presentation.list

import androidx.annotation.StringRes
import com.strimup.feature.notification.domain.entity.Notification

data class NotificationsUiState(
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val hasMore: Boolean = false,
    val isInitialLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    @param:StringRes val errorRes: Int? = null,
) {
    val isEmpty: Boolean
        get() = !isInitialLoading && errorRes == null && notifications.isEmpty()

    val canMarkAllAsRead: Boolean
        get() = unreadCount > 0 || notifications.any { !it.isRead }

    val canLoadMore: Boolean
        get() = hasMore && !isInitialLoading && !isRefreshing && !isLoadingMore
}

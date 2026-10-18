package com.strimup.feature.notification.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.notification.R
import com.strimup.feature.notification.domain.entity.Notification
import com.strimup.feature.notification.domain.usecase.DeleteNotificationUseCase
import com.strimup.feature.notification.domain.usecase.GetNotificationsPageUseCase
import com.strimup.feature.notification.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.strimup.feature.notification.domain.usecase.MarkNotificationAsReadUseCase
import com.strimup.feature.notification.domain.usecase.ObserveUnreadCountUseCase
import com.strimup.feature.notification.domain.usecase.RefreshUnreadCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsPage: GetNotificationsPageUseCase,
    private val markNotificationAsRead: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsRead: MarkAllNotificationsAsReadUseCase,
    private val deleteNotification: DeleteNotificationUseCase,
    private val refreshUnreadCount: RefreshUnreadCountUseCase,
    observeUnreadCount: ObserveUnreadCountUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<NotificationsUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            observeUnreadCount().collect { count -> _state.update { it.copy(unreadCount = count) } }
        }
        loadFirstPage()
    }

    fun onRetryClick() {
        _state.update { it.copy(isInitialLoading = true, errorRes = null) }
        loadFirstPage()
    }

    fun onRefresh() {
        if (_state.value.isRefreshing) return
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch { refreshUnreadCount() }
        loadFirstPage()
    }

    fun onLoadMore() {
        val currentState = _state.value
        if (!currentState.canLoadMore) return

        _state.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            getNotificationsPage(offset = currentState.notifications.size)
                .onSuccess { page ->
                    _state.update { state ->
                        val knownIds = state.notifications.map { it.id }.toSet()
                        state.copy(
                            notifications = state.notifications + page.items.filterNot { it.id in knownIds },
                            hasMore = page.hasMore,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoadingMore = false) }
                    showError(exception.toDomainError().toMessageRes())
                }
        }
    }

    fun onNotificationClick(notification: Notification) {
        if (notification.isRead) return

        updateReadState(ids = setOf(notification.id), isRead = true)

        viewModelScope.launch {
            markNotificationAsRead(notification.id).onFailure {
                updateReadState(ids = setOf(notification.id), isRead = false)
                showError(R.string.notifications_error_mark_as_read)
            }
        }
    }

    fun onMarkAllAsReadClick() {
        val unreadIds = _state.value.notifications.filterNot { it.isRead }.map { it.id }.toSet()
        updateReadState(ids = unreadIds, isRead = true)

        viewModelScope.launch {
            markAllNotificationsAsRead().onFailure {
                updateReadState(ids = unreadIds, isRead = false)
                showError(R.string.notifications_error_mark_all_as_read)
            }
        }
    }

    fun onDelete(notification: Notification) {
        if (!notification.isDeletable) return

        val index = _state.value.notifications.indexOfFirst { it.id == notification.id }
        if (index == -1) return

        _state.update { state ->
            state.copy(notifications = state.notifications.filterNot { it.id == notification.id })
        }

        viewModelScope.launch {
            deleteNotification(notification).onFailure {
                _state.update { state ->
                    val restored = state.notifications.toMutableList()
                    restored.add(index.coerceAtMost(restored.size), notification)
                    state.copy(notifications = restored)
                }
                showError(R.string.notifications_error_delete)
            }
        }
    }

    private fun loadFirstPage() {
        viewModelScope.launch {
            getNotificationsPage(offset = 0)
                .onSuccess { page ->
                    _state.update {
                        it.copy(
                            notifications = page.items.distinctBy { notification -> notification.id },
                            hasMore = page.hasMore,
                            isInitialLoading = false,
                            isRefreshing = false,
                            errorRes = null,
                        )
                    }
                }
                .onFailure { exception ->
                    val messageRes = exception.toDomainError().toMessageRes()
                    val hasContent = _state.value.notifications.isNotEmpty()
                    _state.update {
                        it.copy(
                            isInitialLoading = false,
                            isRefreshing = false,
                            errorRes = messageRes.takeUnless { hasContent },
                        )
                    }
                    if (hasContent) showError(messageRes)
                }
        }
    }

    private fun updateReadState(ids: Set<String>, isRead: Boolean) {
        if (ids.isEmpty()) return
        _state.update { state ->
            state.copy(
                notifications = state.notifications.map { notification ->
                    if (notification.id in ids) notification.copy(isRead = isRead) else notification
                }
            )
        }
    }

    private suspend fun showError(messageRes: Int) {
        _events.send(NotificationsUiEvent.ShowSnackBar(messageRes))
    }
}

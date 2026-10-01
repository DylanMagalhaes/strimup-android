package com.strimup.feature.notification.presentation.list

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.notification.domain.entity.Notification
import com.strimup.feature.notification.domain.entity.NotificationPage
import com.strimup.feature.notification.domain.entity.NotificationType
import com.strimup.feature.notification.domain.usecase.DeleteNotificationUseCase
import com.strimup.feature.notification.domain.usecase.GetNotificationsPageUseCase
import com.strimup.feature.notification.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.strimup.feature.notification.domain.usecase.MarkNotificationAsReadUseCase
import com.strimup.feature.notification.domain.usecase.ObserveUnreadCountUseCase
import com.strimup.feature.notification.domain.usecase.RefreshUnreadCountUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val networkFailure = Result.failure<Unit>(DomainException(DomainError.Network))
    private val unreadCount = MutableStateFlow(0)

    private fun notification(
        id: String,
        isRead: Boolean = false,
        type: NotificationType = NotificationType.UgcMessage,
    ) = Notification(id = id, type = type, message = "message $id", createdAt = null, isRead = isRead)

    private fun page(vararg ids: String, hasMore: Boolean = false) =
        NotificationPage(items = ids.map { notification(it) }, total = ids.size, hasMore = hasMore)

    private fun buildViewModel(
        getPage: GetNotificationsPageUseCase = GetNotificationsPageUseCase { Result.success(page("a", "b")) },
        markAsRead: MarkNotificationAsReadUseCase = MarkNotificationAsReadUseCase { Result.success(Unit) },
        markAllAsRead: MarkAllNotificationsAsReadUseCase = MarkAllNotificationsAsReadUseCase { Result.success(Unit) },
        delete: DeleteNotificationUseCase = DeleteNotificationUseCase { Result.success(Unit) },
        refreshUnreadCount: RefreshUnreadCountUseCase = RefreshUnreadCountUseCase { Result.success(0) },
    ) = NotificationsViewModel(
        getNotificationsPage = getPage,
        markNotificationAsRead = markAsRead,
        markAllNotificationsAsRead = markAllAsRead,
        deleteNotification = delete,
        refreshUnreadCount = refreshUnreadCount,
        observeUnreadCount = ObserveUnreadCountUseCase { unreadCount },
    )

    private val NotificationsViewModel.ids get() = state.value.notifications.map { it.id }

    @Test
    fun `init should load the first page`() = runTest {
        val offsets = mutableListOf<Int>()
        val viewModel = buildViewModel(
            getPage = { offset ->
                offsets += offset
                Result.success(page("a", "b", hasMore = true))
            },
        )

        assertThat(viewModel.state.value.isInitialLoading).isTrue()
        advanceUntilIdle()

        assertThat(offsets).containsExactly(0)
        assertThat(viewModel.ids).containsExactly("a", "b").inOrder()
        assertThat(viewModel.state.value.hasMore).isTrue()
        assertThat(viewModel.state.value.isInitialLoading).isFalse()
    }

    @Test
    fun `empty first page should show the empty state`() = runTest {
        val viewModel = buildViewModel(getPage = { Result.success(page()) })
        advanceUntilIdle()

        assertThat(viewModel.state.value.isEmpty).isTrue()
    }

    @Test
    fun `first page failure should show a full screen error that can be retried`() = runTest {
        var calls = 0
        val viewModel = buildViewModel(
            getPage = {
                calls++
                if (calls == 1) Result.failure(DomainException(DomainError.Network)) else Result.success(page("a"))
            },
        )
        advanceUntilIdle()
        assertThat(viewModel.state.value.errorRes).isEqualTo(R.string.error_network)
        assertThat(viewModel.state.value.isEmpty).isFalse()

        viewModel.onRetryClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorRes).isNull()
        assertThat(viewModel.ids).containsExactly("a")
    }

    @Test
    fun `onLoadMore should append the next page while hasMore`() = runTest {
        val offsets = mutableListOf<Int>()
        val viewModel = buildViewModel(
            getPage = { offset ->
                offsets += offset
                if (offset == 0) Result.success(page("a", "b", hasMore = true)) else Result.success(page("c"))
            },
        )
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        assertThat(offsets).containsExactly(0, 2).inOrder()
        assertThat(viewModel.ids).containsExactly("a", "b", "c").inOrder()
        assertThat(viewModel.state.value.hasMore).isFalse()
    }

    @Test
    fun `onLoadMore should do nothing when there is no more page`() = runTest {
        val offsets = mutableListOf<Int>()
        val viewModel = buildViewModel(
            getPage = { offset ->
                offsets += offset
                Result.success(page("a", hasMore = false))
            },
        )
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        assertThat(offsets).containsExactly(0)
    }

    @Test
    fun `onLoadMore should not start a second load while one is running`() = runTest {
        val nextPage = CompletableDeferred<Result<NotificationPage>>()
        val offsets = mutableListOf<Int>()
        val viewModel = buildViewModel(
            getPage = { offset ->
                offsets += offset
                if (offset == 0) Result.success(page("a", hasMore = true)) else nextPage.await()
            },
        )
        advanceUntilIdle()

        viewModel.onLoadMore()
        viewModel.onLoadMore()
        advanceUntilIdle()
        nextPage.complete(Result.success(page("b")))
        advanceUntilIdle()

        assertThat(offsets).containsExactly(0, 1).inOrder()
    }

    @Test
    fun `onLoadMore should skip notifications already displayed`() = runTest {
        val viewModel = buildViewModel(
            getPage = { offset ->
                if (offset == 0) Result.success(page("a", "b", hasMore = true)) else Result.success(page("b", "c"))
            },
        )
        advanceUntilIdle()

        viewModel.onLoadMore()
        advanceUntilIdle()

        assertThat(viewModel.ids).containsExactly("a", "b", "c").inOrder()
    }

    @Test
    fun `onRefresh should reload the first page and refresh the badge`() = runTest {
        var refreshCalls = 0
        var calls = 0
        val viewModel = buildViewModel(
            getPage = {
                calls++
                if (calls == 1) Result.success(page("a")) else Result.success(page("new", "a"))
            },
            refreshUnreadCount = {
                refreshCalls++
                Result.success(1)
            },
        )
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        assertThat(viewModel.ids).containsExactly("new", "a").inOrder()
        assertThat(viewModel.state.value.isRefreshing).isFalse()
        assertThat(refreshCalls).isEqualTo(1)
    }

    @Test
    fun `clicking an unread notification should mark it as read optimistically`() = runTest {
        val readIds = mutableListOf<String>()
        val viewModel = buildViewModel(
            markAsRead = { id ->
                readIds += id
                Result.success(Unit)
            },
        )
        advanceUntilIdle()

        viewModel.onNotificationClick(viewModel.state.value.notifications.first())

        assertThat(viewModel.state.value.notifications.first().isRead).isTrue()
        advanceUntilIdle()
        assertThat(readIds).containsExactly("a")
    }

    @Test
    fun `mark as read failure should revert the notification and show an error`() = runTest {
        val viewModel = buildViewModel(markAsRead = { networkFailure })
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onNotificationClick(viewModel.state.value.notifications.first())
            advanceUntilIdle()

            val event = awaitItem() as NotificationsUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.notifications_error_mark_as_read)
        }
        assertThat(viewModel.state.value.notifications.first().isRead).isFalse()
    }

    @Test
    fun `clicking an already read notification should not call the server`() = runTest {
        var calls = 0
        val viewModel = buildViewModel(
            getPage = { Result.success(NotificationPage(listOf(notification("a", isRead = true)), 1, false)) },
            markAsRead = {
                calls++
                Result.success(Unit)
            },
        )
        advanceUntilIdle()

        viewModel.onNotificationClick(viewModel.state.value.notifications.first())
        advanceUntilIdle()

        assertThat(calls).isEqualTo(0)
    }

    @Test
    fun `mark all as read should mark every notification and revert them on failure`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.onMarkAllAsReadClick()
        assertThat(viewModel.state.value.notifications.all { it.isRead }).isTrue()

        val failingViewModel = buildViewModel(markAllAsRead = { networkFailure })
        advanceUntilIdle()
        failingViewModel.onMarkAllAsReadClick()
        advanceUntilIdle()
        assertThat(failingViewModel.state.value.notifications.none { it.isRead }).isTrue()
    }

    @Test
    fun `mark all as read should be available when the badge shows unread notifications`() = runTest {
        val viewModel = buildViewModel(
            getPage = { Result.success(NotificationPage(listOf(notification("a", isRead = true)), 1, true)) },
        )
        unreadCount.value = 4
        advanceUntilIdle()

        assertThat(viewModel.state.value.unreadCount).isEqualTo(4)
        assertThat(viewModel.state.value.canMarkAllAsRead).isTrue()
    }

    @Test
    fun `delete should remove the notification immediately`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onDelete(viewModel.state.value.notifications.first())

        assertThat(viewModel.ids).containsExactly("b")
    }

    @Test
    fun `delete failure should restore the notification at its position`() = runTest {
        val viewModel = buildViewModel(
            getPage = { Result.success(page("a", "b", "c")) },
            delete = { networkFailure },
        )
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onDelete(viewModel.state.value.notifications[1])
            advanceUntilIdle()

            val event = awaitItem() as NotificationsUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.notifications_error_delete)
        }
        assertThat(viewModel.ids).containsExactly("a", "b", "c").inOrder()
    }

    @Test
    fun `global announcement should never be deleted`() = runTest {
        var deleteCalls = 0
        val announcement = notification("announce", type = NotificationType.GlobalAnnouncement)
        val viewModel = buildViewModel(
            getPage = { Result.success(NotificationPage(listOf(announcement), 1, false)) },
            delete = {
                deleteCalls++
                Result.success(Unit)
            },
        )
        advanceUntilIdle()

        viewModel.onDelete(announcement)
        advanceUntilIdle()

        assertThat(announcement.isDeletable).isFalse()
        assertThat(viewModel.ids).containsExactly("announce")
        assertThat(deleteCalls).isEqualTo(0)
    }
}

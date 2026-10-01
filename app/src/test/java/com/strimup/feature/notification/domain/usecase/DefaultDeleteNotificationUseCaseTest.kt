package com.strimup.feature.notification.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.notification.domain.NotificationRepository
import com.strimup.feature.notification.domain.entity.Notification
import com.strimup.feature.notification.domain.entity.NotificationNotDeletableException
import com.strimup.feature.notification.domain.entity.NotificationPage
import com.strimup.feature.notification.domain.entity.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultDeleteNotificationUseCaseTest {

    private class FakeNotificationRepository : NotificationRepository {
        val deletions = mutableListOf<Pair<String, Boolean>>()
        override val unreadCount: StateFlow<Int> = MutableStateFlow(0)
        override suspend fun refreshUnreadCount(): Result<Int> = Result.success(0)
        override suspend fun getNotifications(offset: Int, limit: Int): Result<NotificationPage> =
            Result.success(NotificationPage(emptyList(), 0, false))
        override suspend fun markAsRead(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun markAllAsRead(): Result<Unit> = Result.success(Unit)
        override suspend fun delete(id: String, wasUnread: Boolean): Result<Unit> {
            deletions += id to wasUnread
            return Result.success(Unit)
        }
        override fun clear() = Unit
    }

    private fun notification(type: NotificationType, isRead: Boolean = false) = Notification(
        id = "n1",
        type = type,
        message = "",
        createdAt = null,
        isRead = isRead,
    )

    @Test
    fun `global announcement should never be deleted`() = runTest {
        val repository = FakeNotificationRepository()
        val useCase = DefaultDeleteNotificationUseCase(repository)

        val result = useCase(notification(NotificationType.GlobalAnnouncement))

        assertThat(result.exceptionOrNull()).isInstanceOf(NotificationNotDeletableException::class.java)
        assertThat(repository.deletions).isEmpty()
    }

    @Test
    fun `other notifications should be deleted with their read state`() = runTest {
        val repository = FakeNotificationRepository()
        val useCase = DefaultDeleteNotificationUseCase(repository)

        useCase(notification(NotificationType.UgcMessage, isRead = false))
        useCase(notification(NotificationType.Unknown("x"), isRead = true))

        assertThat(repository.deletions).containsExactly("n1" to true, "n1" to false).inOrder()
    }
}

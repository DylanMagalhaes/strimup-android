package com.strimup.feature.notification.data.repository

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.notification.data.DefaultNotificationRepository
import com.strimup.feature.notification.data.NotificationApiService
import com.strimup.feature.notification.data.response.NotificationPageResponse
import com.strimup.feature.notification.data.response.NotificationResponse
import com.strimup.feature.notification.data.response.UnreadCountResponse
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class DefaultNotificationRepositoryTest {

    private class FakeNotificationApiService(
        var unreadCount: Int = 0,
        var failWrites: Boolean = false,
        var isOffline: Boolean = false,
    ) : NotificationApiService {
        val requestedPages = mutableListOf<Pair<Int, Int>>()

        override suspend fun getNotifications(limit: Int, offset: Int): NotificationPageResponse {
            requestedPages += limit to offset
            return NotificationPageResponse(
                items = listOf(NotificationResponse(id = "n1", type = "ugc_message")),
                total = 1,
                hasMore = false,
            )
        }

        override suspend fun getUnreadCount(): UnreadCountResponse {
            if (isOffline) throw IOException()
            return UnreadCountResponse(unreadCount)
        }

        override suspend fun markAsRead(id: String) = failIfNeeded()

        override suspend fun markAllAsRead() = failIfNeeded()

        override suspend fun delete(id: String) = failIfNeeded()

        private fun failIfNeeded() {
            if (failWrites) throw IOException()
        }
    }

    private suspend fun repositoryWithUnread(count: Int, failWrites: Boolean = false): DefaultNotificationRepository {
        val repository = DefaultNotificationRepository(FakeNotificationApiService(count, failWrites))
        repository.refreshUnreadCount()
        return repository
    }

    @Test
    fun `refreshUnreadCount should expose the server count`() = runTest {
        val repository = repositoryWithUnread(3)

        assertThat(repository.unreadCount.value).isEqualTo(3)
    }

    @Test
    fun `refreshUnreadCount when offline should keep the previous count and fail with Network`() = runTest {
        val service = FakeNotificationApiService(unreadCount = 3)
        val repository = DefaultNotificationRepository(service)
        repository.refreshUnreadCount()
        service.isOffline = true

        val exception = repository.refreshUnreadCount().exceptionOrNull() as DomainException

        assertThat(exception.error).isEqualTo(DomainError.Network)
        assertThat(repository.unreadCount.value).isEqualTo(3)
    }

    @Test
    fun `getNotifications should forward limit and offset`() = runTest {
        val service = FakeNotificationApiService()
        val repository = DefaultNotificationRepository(service)

        val page = repository.getNotifications(offset = 40, limit = 20).getOrThrow()

        assertThat(service.requestedPages).containsExactly(20 to 40)
        assertThat(page.items.single().id).isEqualTo("n1")
    }

    @Test
    fun `markAsRead should decrement the badge`() = runTest {
        val repository = repositoryWithUnread(3)

        repository.markAsRead("n1")

        assertThat(repository.unreadCount.value).isEqualTo(2)
    }

    @Test
    fun `markAsRead failure should restore the badge`() = runTest {
        val repository = repositoryWithUnread(3, failWrites = true)

        val result = repository.markAsRead("n1")

        assertThat(result.isFailure).isTrue()
        assertThat(repository.unreadCount.value).isEqualTo(3)
    }

    @Test
    fun `markAsRead should never make the badge negative`() = runTest {
        val repository = repositoryWithUnread(0)

        repository.markAsRead("n1")

        assertThat(repository.unreadCount.value).isEqualTo(0)
    }

    @Test
    fun `markAllAsRead should reset the badge and restore it on failure`() = runTest {
        val repository = repositoryWithUnread(5)
        repository.markAllAsRead()
        assertThat(repository.unreadCount.value).isEqualTo(0)

        val failingRepository = repositoryWithUnread(5, failWrites = true)
        failingRepository.markAllAsRead()
        assertThat(failingRepository.unreadCount.value).isEqualTo(5)
    }

    @Test
    fun `delete of an unread notification should decrement the badge`() = runTest {
        val repository = repositoryWithUnread(2)

        repository.delete(id = "n1", wasUnread = true)

        assertThat(repository.unreadCount.value).isEqualTo(1)
    }

    @Test
    fun `delete of a read notification should not touch the badge`() = runTest {
        val repository = repositoryWithUnread(2)

        repository.delete(id = "n1", wasUnread = false)

        assertThat(repository.unreadCount.value).isEqualTo(2)
    }

    @Test
    fun `delete failure should restore the badge`() = runTest {
        val repository = repositoryWithUnread(2, failWrites = true)

        repository.delete(id = "n1", wasUnread = true)

        assertThat(repository.unreadCount.value).isEqualTo(2)
    }

    @Test
    fun `clear should reset the badge`() = runTest {
        val repository = repositoryWithUnread(7)

        repository.clear()

        assertThat(repository.unreadCount.value).isEqualTo(0)
    }
}

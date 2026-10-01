package com.strimup.feature.notification.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.notification.domain.NotificationRepository
import com.strimup.feature.notification.domain.entity.NotificationPage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultWatchUnreadNotificationCountUseCaseTest {

    private class FakeNotificationRepository(var serverCount: Int = 0) : NotificationRepository {
        var refreshCount = 0
        var clearCount = 0
        private val count = MutableStateFlow(0)
        override val unreadCount: StateFlow<Int> = count

        override suspend fun refreshUnreadCount(): Result<Int> {
            refreshCount++
            count.value = serverCount
            return Result.success(serverCount)
        }

        override suspend fun getNotifications(offset: Int, limit: Int): Result<NotificationPage> =
            Result.success(NotificationPage(emptyList(), 0, false))

        override suspend fun markAsRead(id: String): Result<Unit> = Result.success(Unit)
        override suspend fun markAllAsRead(): Result<Unit> = Result.success(Unit)
        override suspend fun delete(id: String, wasUnread: Boolean): Result<Unit> = Result.success(Unit)

        override fun clear() {
            clearCount++
            count.value = 0
        }
    }

    private val user = UserEntity(
        id = "1",
        userName = "Inox",
        email = "inox@test.com",
        role = UserRole.VIEWER,
        avatarUrl = null,
    )

    private fun TestScope.watch(
        userFlow: MutableStateFlow<UserEntity?>,
        repository: FakeNotificationRepository,
    ): List<Int> {
        val emissions = mutableListOf<Int>()
        val useCase = DefaultWatchUnreadNotificationCountUseCase(GetUserFlowUseCase { userFlow }, repository)
        backgroundScope.launch { useCase().collect { emissions += it } }
        runCurrent()
        return emissions
    }

    @Test
    fun `should refresh immediately when a user is logged in`() = runTest {
        val repository = FakeNotificationRepository(serverCount = 3)

        val emissions = watch(MutableStateFlow(user), repository)

        assertThat(repository.refreshCount).isEqualTo(1)
        assertThat(emissions.last()).isEqualTo(3)
    }

    @Test
    fun `should refresh every 60 seconds`() = runTest {
        val repository = FakeNotificationRepository(serverCount = 1)
        val emissions = watch(MutableStateFlow(user), repository)

        repository.serverCount = 4
        advanceTimeBy(UNREAD_COUNT_REFRESH_INTERVAL)
        runCurrent()

        assertThat(repository.refreshCount).isEqualTo(2)
        assertThat(emissions.last()).isEqualTo(4)
    }

    @Test
    fun `should not refresh before the interval is over`() = runTest {
        val repository = FakeNotificationRepository()
        watch(MutableStateFlow(user), repository)

        advanceTimeBy(59.seconds)
        runCurrent()

        assertThat(repository.refreshCount).isEqualTo(1)
    }

    @Test
    fun `logout should clear the count and stop polling`() = runTest {
        val repository = FakeNotificationRepository(serverCount = 5)
        val userFlow = MutableStateFlow<UserEntity?>(user)
        val emissions = watch(userFlow, repository)

        userFlow.value = null
        runCurrent()
        advanceTimeBy(UNREAD_COUNT_REFRESH_INTERVAL * 3)
        runCurrent()

        assertThat(repository.clearCount).isEqualTo(1)
        assertThat(repository.refreshCount).isEqualTo(1)
        assertThat(emissions.last()).isEqualTo(0)
    }

    @Test
    fun `logged out user should never trigger a refresh`() = runTest {
        val repository = FakeNotificationRepository()
        val emissions = watch(MutableStateFlow(null), repository)

        advanceTimeBy(UNREAD_COUNT_REFRESH_INTERVAL * 2)
        runCurrent()

        assertThat(repository.refreshCount).isEqualTo(0)
        assertThat(emissions).containsExactly(0)
    }

    @Test
    fun `login should refresh immediately`() = runTest {
        val repository = FakeNotificationRepository(serverCount = 2)
        val userFlow = MutableStateFlow<UserEntity?>(null)
        val emissions = watch(userFlow, repository)

        userFlow.value = user
        runCurrent()

        assertThat(repository.refreshCount).isEqualTo(1)
        assertThat(emissions.last()).isEqualTo(2)
    }

    @Test
    fun `profile update of the same user should not restart polling`() = runTest {
        val repository = FakeNotificationRepository()
        val userFlow = MutableStateFlow<UserEntity?>(user)
        watch(userFlow, repository)

        userFlow.value = user.copy(userName = "Inox2")
        runCurrent()

        assertThat(repository.refreshCount).isEqualTo(1)
    }

    @Test
    fun `stopping the collection should stop polling`() = runTest {
        val repository = FakeNotificationRepository()
        val useCase = DefaultWatchUnreadNotificationCountUseCase(
            GetUserFlowUseCase { MutableStateFlow(user) },
            repository,
        )
        val job = backgroundScope.launch { useCase().collect {} }
        runCurrent()

        job.cancel()
        advanceTimeBy(UNREAD_COUNT_REFRESH_INTERVAL * 3)
        runCurrent()

        assertThat(repository.refreshCount).isEqualTo(1)
    }
}

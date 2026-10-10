package com.strimup.feature.push.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.notification.domain.entity.NotificationType
import com.strimup.feature.notification.domain.usecase.RefreshUnreadCountUseCase
import com.strimup.feature.push.domain.entity.PushMessage
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultProcessIncomingPushUseCaseTest {

    private val message = PushMessage(
        notificationId = "n1",
        type = NotificationType.UgcMessage,
        title = null,
        body = "Hello",
    )

    private val user = UserEntity(
        id = "1",
        userName = "Inox",
        email = "inox@test.com",
        role = UserRole.VIEWER,
        avatarUrl = null,
    )

    @Test
    fun `push should be displayed and the badge refreshed when logged in`() = runTest {
        var refreshCount = 0
        val useCase = DefaultProcessIncomingPushUseCase(
            getUser = GetUserFlowUseCase { flowOf(user) },
            refreshUnreadCount = RefreshUnreadCountUseCase {
                refreshCount++
                Result.success(1)
            },
        )

        val shouldDisplay = useCase(message)

        assertThat(shouldDisplay).isTrue()
        assertThat(refreshCount).isEqualTo(1)
    }

    @Test
    fun `push should be ignored when nobody is logged in`() = runTest {
        var refreshCount = 0
        val useCase = DefaultProcessIncomingPushUseCase(
            getUser = GetUserFlowUseCase { flowOf(null) },
            refreshUnreadCount = RefreshUnreadCountUseCase {
                refreshCount++
                Result.success(0)
            },
        )

        val shouldDisplay = useCase(message)

        assertThat(shouldDisplay).isFalse()
        assertThat(refreshCount).isEqualTo(0)
    }

    @Test
    fun `badge refresh failure should not prevent the display`() = runTest {
        val useCase = DefaultProcessIncomingPushUseCase(
            getUser = GetUserFlowUseCase { flowOf(user) },
            refreshUnreadCount = RefreshUnreadCountUseCase { Result.failure(IllegalStateException()) },
        )

        assertThat(useCase(message)).isTrue()
    }
}

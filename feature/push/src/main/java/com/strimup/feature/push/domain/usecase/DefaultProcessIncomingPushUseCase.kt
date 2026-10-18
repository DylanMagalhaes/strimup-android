package com.strimup.feature.push.domain.usecase

import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.notification.domain.usecase.RefreshUnreadCountUseCase
import com.strimup.feature.push.domain.entity.PushMessage
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DefaultProcessIncomingPushUseCase @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val refreshUnreadCount: RefreshUnreadCountUseCase,
) : ProcessIncomingPushUseCase {
    override suspend fun invoke(message: PushMessage): Boolean {
        if (getUser().first() == null) return false

        refreshUnreadCount()
        return true
    }
}

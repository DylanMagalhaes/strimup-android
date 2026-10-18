package com.strimup.feature.push.domain.usecase

import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.push.domain.NotificationPermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class DefaultObserveShouldAskNotificationPermissionUseCase @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val repository: NotificationPermissionRepository,
) : ObserveShouldAskNotificationPermissionUseCase {
    override fun invoke(): Flow<Boolean> =
        combine(getUser(), repository.hasAskedPermission) { user, hasAsked -> user != null && !hasAsked }
            .distinctUntilChanged()
}

package com.strimup.feature.push.domain.usecase

import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.push.domain.PushRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSyncPushDeviceRegistrationUseCase @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val repository: PushRepository,
) : SyncPushDeviceRegistrationUseCase {
    override suspend fun invoke() {
        getUser()
            .map { user -> user?.id }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { repository.registerDevice() }
    }
}

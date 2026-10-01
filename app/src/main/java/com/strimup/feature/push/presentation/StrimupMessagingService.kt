package com.strimup.feature.push.presentation

import com.google.firebase.messaging.FirebaseMessagingService
import com.strimup.core.common.ApplicationScope
import com.strimup.feature.push.domain.usecase.RegisterRefreshedPushTokenUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StrimupMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var registerRefreshedPushToken: RegisterRefreshedPushTokenUseCase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onNewToken(token: String) {
        applicationScope.launch { registerRefreshedPushToken(token) }
    }
}

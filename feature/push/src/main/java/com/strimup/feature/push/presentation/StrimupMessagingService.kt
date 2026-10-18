package com.strimup.feature.push.presentation

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.strimup.core.common.ApplicationScope
import com.strimup.feature.push.data.mapper.toPushMessage
import com.strimup.feature.push.domain.usecase.ProcessIncomingPushUseCase
import com.strimup.feature.push.domain.usecase.RegisterRefreshedPushTokenUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class StrimupMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var registerRefreshedPushToken: RegisterRefreshedPushTokenUseCase

    @Inject
    lateinit var processIncomingPush: ProcessIncomingPushUseCase

    @Inject
    lateinit var publisher: PushNotificationPublisher

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onNewToken(token: String) {
        applicationScope.launch { registerRefreshedPushToken(token) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val message = remoteMessage.data.toPushMessage() ?: return

        val shouldDisplay = runBlocking { processIncomingPush(message) }
        if (shouldDisplay) publisher.publish(message)
    }
}

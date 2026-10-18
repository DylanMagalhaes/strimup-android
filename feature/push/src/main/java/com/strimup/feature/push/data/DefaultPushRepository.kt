package com.strimup.feature.push.data

import com.strimup.core.network.toDomainResult
import com.strimup.feature.push.data.client.PushMessagingClient
import com.strimup.feature.push.data.request.DeviceTokenRequest
import com.strimup.feature.push.domain.PushRepository
import javax.inject.Inject

const val ANNOUNCEMENTS_TOPIC = "announcements"

class DefaultPushRepository @Inject constructor(
    private val service: DeviceApiService,
    private val messagingClient: PushMessagingClient,
) : PushRepository {

    override suspend fun registerDevice(token: String?): Result<Unit> {
        return runCatching {
            val deviceToken = token ?: messagingClient.getToken()
            service.register(DeviceTokenRequest.forAndroid(token = deviceToken))
            messagingClient.subscribeToTopic(ANNOUNCEMENTS_TOPIC)
        }.toDomainResult()
    }

    override suspend fun unregisterDevice(): Result<Unit> {
        val unsubscribeResult = runCatching { messagingClient.unsubscribeFromTopic(ANNOUNCEMENTS_TOPIC) }
        val unregisterResult = runCatching {
            service.unregister(DeviceTokenRequest.forAndroid(token = messagingClient.getToken()))
        }

        return unregisterResult
            .mapCatching { unsubscribeResult.getOrThrow() }
            .toDomainResult()
    }

    override suspend fun deleteLocalToken(): Result<Unit> {
        return runCatching {
            messagingClient.unsubscribeFromTopic(ANNOUNCEMENTS_TOPIC)
            messagingClient.deleteToken()
        }.toDomainResult()
    }
}

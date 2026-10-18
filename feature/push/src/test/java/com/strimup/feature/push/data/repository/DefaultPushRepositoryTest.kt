package com.strimup.feature.push.data.repository

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.push.data.ANNOUNCEMENTS_TOPIC
import com.strimup.feature.push.data.DefaultPushRepository
import com.strimup.feature.push.data.DeviceApiService
import com.strimup.feature.push.data.client.PushMessagingClient
import com.strimup.feature.push.data.request.DeviceTokenRequest
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class DefaultPushRepositoryTest {

    private class FakeDeviceApiService(var isOffline: Boolean = false) : DeviceApiService {
        val registered = mutableListOf<DeviceTokenRequest>()
        val unregistered = mutableListOf<DeviceTokenRequest>()

        override suspend fun register(request: DeviceTokenRequest) {
            if (isOffline) throw IOException()
            registered += request
        }

        override suspend fun unregister(request: DeviceTokenRequest) {
            if (isOffline) throw IOException()
            unregistered += request
        }
    }

    private class FakePushMessagingClient(private val token: String = "device-token") : PushMessagingClient {
        val subscribedTopics = mutableSetOf<String>()
        var isTokenDeleted = false

        override suspend fun getToken(): String = token

        override suspend fun deleteToken() {
            isTokenDeleted = true
        }

        override suspend fun subscribeToTopic(topic: String) {
            subscribedTopics += topic
        }

        override suspend fun unsubscribeFromTopic(topic: String) {
            subscribedTopics -= topic
        }
    }

    @Test
    fun `registerDevice should send the current token and subscribe to announcements`() = runTest {
        val service = FakeDeviceApiService()
        val client = FakePushMessagingClient(token = "current")
        val repository = DefaultPushRepository(service, client)

        val result = repository.registerDevice()

        assertThat(result.isSuccess).isTrue()
        assertThat(service.registered).containsExactly(DeviceTokenRequest.forAndroid(token = "current"))
        assertThat(client.subscribedTopics).containsExactly(ANNOUNCEMENTS_TOPIC)
    }

    @Test
    fun `registerDevice with a refreshed token should send that token`() = runTest {
        val service = FakeDeviceApiService()
        val repository = DefaultPushRepository(service, FakePushMessagingClient(token = "old"))

        repository.registerDevice(token = "refreshed")

        assertThat(service.registered.single().token).isEqualTo("refreshed")
    }

    @Test
    fun `registerDevice when offline should fail without subscribing`() = runTest {
        val client = FakePushMessagingClient()
        val repository = DefaultPushRepository(FakeDeviceApiService(isOffline = true), client)

        val exception = repository.registerDevice().exceptionOrNull() as DomainException

        assertThat(exception.error).isEqualTo(DomainError.Network)
        assertThat(client.subscribedTopics).isEmpty()
    }

    @Test
    fun `unregisterDevice should remove the token and unsubscribe from announcements`() = runTest {
        val service = FakeDeviceApiService()
        val client = FakePushMessagingClient(token = "current").apply { subscribedTopics += ANNOUNCEMENTS_TOPIC }
        val repository = DefaultPushRepository(service, client)

        val result = repository.unregisterDevice()

        assertThat(result.isSuccess).isTrue()
        assertThat(service.unregistered).containsExactly(DeviceTokenRequest.forAndroid(token = "current"))
        assertThat(client.subscribedTopics).isEmpty()
    }

    @Test
    fun `unregisterDevice when offline should still unsubscribe and report the failure`() = runTest {
        val client = FakePushMessagingClient().apply { subscribedTopics += ANNOUNCEMENTS_TOPIC }
        val repository = DefaultPushRepository(FakeDeviceApiService(isOffline = true), client)

        val result = repository.unregisterDevice()

        assertThat(result.isFailure).isTrue()
        assertThat(client.subscribedTopics).isEmpty()
    }

    @Test
    fun `deleteLocalToken should unsubscribe and delete the Firebase token`() = runTest {
        val client = FakePushMessagingClient().apply { subscribedTopics += ANNOUNCEMENTS_TOPIC }
        val repository = DefaultPushRepository(FakeDeviceApiService(), client)

        repository.deleteLocalToken()

        assertThat(client.subscribedTopics).isEmpty()
        assertThat(client.isTokenDeleted).isTrue()
    }
}

package com.strimup.feature.push.data.client

interface PushMessagingClient {
    suspend fun getToken(): String

    suspend fun deleteToken()

    suspend fun subscribeToTopic(topic: String)

    suspend fun unsubscribeFromTopic(topic: String)
}

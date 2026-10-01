package com.strimup.feature.push.data.client

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePushMessagingClient @Inject constructor() : PushMessagingClient {

    private val messaging: FirebaseMessaging
        get() = FirebaseMessaging.getInstance()

    override suspend fun getToken(): String = messaging.token.await()

    override suspend fun deleteToken() {
        messaging.deleteToken().await()
    }

    override suspend fun subscribeToTopic(topic: String) {
        messaging.subscribeToTopic(topic).await()
    }

    override suspend fun unsubscribeFromTopic(topic: String) {
        messaging.unsubscribeFromTopic(topic).await()
    }
}

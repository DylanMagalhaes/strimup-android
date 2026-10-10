package com.strimup.feature.push.data.request

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Test

class DeviceTokenRequestTest {

    @Test
    fun `request should always send the android platform`() {
        val body = Json { ignoreUnknownKeys = true }.encodeToString(DeviceTokenRequest.forAndroid(token = "abc"))

        assertThat(body).isEqualTo("""{"token":"abc","platform":"android"}""")
    }
}

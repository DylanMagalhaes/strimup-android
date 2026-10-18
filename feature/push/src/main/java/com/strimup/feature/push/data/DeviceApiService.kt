package com.strimup.feature.push.data

import com.strimup.feature.push.data.request.DeviceTokenRequest
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface DeviceApiService {
    @POST("api/devices")
    suspend fun register(@Body request: DeviceTokenRequest)

    @HTTP(method = "DELETE", path = "api/devices", hasBody = true)
    suspend fun unregister(@Body request: DeviceTokenRequest)
}

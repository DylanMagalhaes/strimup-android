package com.strimup.feature.account.data

import com.strimup.feature.account.data.request.DeleteAccountRequest
import com.strimup.feature.account.data.response.AccountResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP

interface AccountApiService {
    @GET("api/auth/me")
    suspend fun getAccount(): AccountResponse

    @HTTP(method = "DELETE", path = "api/auth/me", hasBody = true)
    suspend fun deleteAccount(@Body request: DeleteAccountRequest)
}

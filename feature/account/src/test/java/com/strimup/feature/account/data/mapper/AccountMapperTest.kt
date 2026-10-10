package com.strimup.feature.account.data.mapper

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.account.data.response.AccountResponse
import org.junit.Test

class AccountMapperTest {

    @Test
    fun `account created with Twitch should not require a password`() {
        val response = AccountResponse(user = AccountResponse.AccountData(isTwitchConnected = true))

        assertThat(response.toDeletionPolicy().isPasswordRequired).isFalse()
    }

    @Test
    fun `account without Twitch should require a password`() {
        val response = AccountResponse(user = AccountResponse.AccountData(isTwitchConnected = false))

        assertThat(response.toDeletionPolicy().isPasswordRequired).isTrue()
    }
}

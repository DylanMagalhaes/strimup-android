package com.strimup.feature.auth.presentation

import com.google.common.truth.Truth.assertThat
import com.strimup.feature.auth.domain.entity.OAuthCallback
import com.strimup.feature.auth.domain.entity.OAuthFailureReason
import org.junit.Test

class OAuthCallbackParserTest {

    private fun parse(
        vararg params: Pair<String, String>,
        scheme: String? = "strimup",
        host: String? = "oauth-callback",
    ): OAuthCallback? {
        val query = params.toMap()
        return parseOAuthCallback(scheme = scheme, host = host, queryParameter = query::get)
    }

    @Test
    fun `mode login with a code should return LoggedIn`() {
        assertThat(parse("mode" to "login", "code" to "abc"))
            .isEqualTo(OAuthCallback.LoggedIn(code = "abc"))
    }

    @Test
    fun `mode login without code should return null`() {
        assertThat(parse("mode" to "login")).isNull()
    }

    @Test
    fun `mode login with a blank code should return null`() {
        assertThat(parse("mode" to "login", "code" to "")).isNull()
    }

    @Test
    fun `mode login with a legacy token and no code should return null`() {
        assertThat(parse("mode" to "login", "token" to "t", "refresh_token" to "r")).isNull()
    }

    @Test
    fun `mode link with a code should return Linked`() {
        assertThat(parse("mode" to "link", "code" to "abc"))
            .isEqualTo(OAuthCallback.Linked(code = "abc"))
    }

    @Test
    fun `mode link without code should return null`() {
        assertThat(parse("mode" to "link")).isNull()
    }

    @Test
    fun `mode onboarding with a tmp should return Onboarding`() {
        assertThat(parse("mode" to "onboarding", "tmp" to "xyz"))
            .isEqualTo(OAuthCallback.Onboarding(tmp = "xyz"))
    }

    @Test
    fun `mode onboarding without tmp should return null`() {
        assertThat(parse("mode" to "onboarding")).isNull()
    }

    @Test
    fun `mode error should map every known reason`() {
        OAuthFailureReason.entries
            .filter { it != OAuthFailureReason.UNKNOWN }
            .forEach { reason ->
                assertThat(parse("mode" to "error", "reason" to reason.apiValue))
                    .isEqualTo(OAuthCallback.Failed(reason))
            }
    }

    @Test
    fun `mode error with an unknown or missing reason should return Failed UNKNOWN`() {
        assertThat(parse("mode" to "error", "reason" to "whatever"))
            .isEqualTo(OAuthCallback.Failed(OAuthFailureReason.UNKNOWN))
        assertThat(parse("mode" to "error"))
            .isEqualTo(OAuthCallback.Failed(OAuthFailureReason.UNKNOWN))
    }

    @Test
    fun `unknown or missing mode should return null`() {
        assertThat(parse("mode" to "other", "code" to "abc")).isNull()
        assertThat(parse("code" to "abc")).isNull()
    }

    @Test
    fun `wrong scheme or host should return null`() {
        assertThat(parse("mode" to "login", "code" to "abc", scheme = "https")).isNull()
        assertThat(parse("mode" to "login", "code" to "abc", host = "other")).isNull()
    }
}

package com.strimup.feature.auth.data.pkce

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PkceTest {

    @Test
    fun `generateVerifier should produce 43 base64url characters without padding`() {
        // WHEN
        val verifier = Pkce.generateVerifier()

        // THEN
        assertThat(verifier).hasLength(43)
        assertThat(verifier).matches("[A-Za-z0-9_-]+")
    }

    @Test
    fun `generateVerifier should produce a new value on each call`() {
        // WHEN
        val first = Pkce.generateVerifier()
        val second = Pkce.generateVerifier()

        // THEN
        assertThat(first).isNotEqualTo(second)
    }

    @Test
    fun `challengeOf should match the RFC 7636 S256 test vector`() {
        // GIVEN (RFC 7636, Appendix B)
        val verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"

        // WHEN
        val challenge = Pkce.challengeOf(verifier)

        // THEN
        assertThat(challenge).isEqualTo("E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM")
    }

    @Test
    fun `challengeOf should produce 43 base64url characters without padding`() {
        // WHEN
        val challenge = Pkce.challengeOf(Pkce.generateVerifier())

        // THEN
        assertThat(challenge).hasLength(43)
        assertThat(challenge).matches("[A-Za-z0-9_-]+")
    }
}

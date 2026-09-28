package com.strimup.feature.auth.data.pkce

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object Pkce {
    private const val VERIFIER_BYTES = 32

    fun generateVerifier(random: SecureRandom = SecureRandom()): String {
        val bytes = ByteArray(VERIFIER_BYTES).also(random::nextBytes)
        return bytes.toBase64Url()
    }

    fun challengeOf(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(verifier.toByteArray(Charsets.US_ASCII))
        return digest.toBase64Url()
    }

    private fun ByteArray.toBase64Url(): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(this)
}

package com.strimup.core.security

interface SecretCipher {
    fun encrypt(plainText: String): String

    fun decrypt(cipherText: String): String?
}

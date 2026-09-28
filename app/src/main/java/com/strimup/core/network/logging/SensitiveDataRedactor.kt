package com.strimup.core.network.logging

private const val REDACTED = "██"
private val SENSITIVE_JSON_FIELDS = listOf("password", "token", "refreshToken", "code", "code_verifier", "tmp")
private val SENSITIVE_JSON_FIELD_REGEX = Regex(
    "(\"(?:${SENSITIVE_JSON_FIELDS.joinToString("|")})\"\\s*:\\s*)\"(?:[^\"\\\\]|\\\\.)*\""
)

fun String.redactSensitiveData(): String =
    replace(SENSITIVE_JSON_FIELD_REGEX) { match -> "${match.groupValues[1]}\"$REDACTED\"" }

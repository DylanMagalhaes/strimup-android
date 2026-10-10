package com.strimup.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException

@Serializable
private data class ApiErrorBody(
    @SerialName("message")
    val message: String? = null,
)

private val apiErrorJson = Json { ignoreUnknownKeys = true }

fun HttpException.apiErrorMessage(): String? {
    val rawBody = response()?.errorBody()?.string() ?: return null
    return runCatching { apiErrorJson.decodeFromString<ApiErrorBody>(rawBody).message }
        .getOrNull()
        ?.takeIf { it.isNotBlank() }
}

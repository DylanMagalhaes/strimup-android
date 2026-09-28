package com.strimup.feature.auth.domain.entity

/**
 * Outcome of the Twitch OAuth deep link (`strimup://oauth-callback?mode=...`),
 * relayed by the backend once it has exchanged the Twitch authorization code.
 */
sealed interface OAuthCallback {
    data class LoggedIn(val code: String) : OAuthCallback
    data class Linked(val code: String) : OAuthCallback
    data class Onboarding(val tmp: String) : OAuthCallback
    data class Failed(val reason: OAuthFailureReason) : OAuthCallback
}

enum class OAuthFailureReason(val apiValue: String) {
    ACCESS_DENIED("access_denied"),
    INVALID_REQUEST("invalid_request"),
    INVALID_STATE("invalid_state"),
    SERVER_ERROR("server_error"),
    UNKNOWN("unknown");

    companion object {
        fun fromApi(value: String?): OAuthFailureReason =
            entries.firstOrNull { it.apiValue == value } ?: UNKNOWN
    }
}

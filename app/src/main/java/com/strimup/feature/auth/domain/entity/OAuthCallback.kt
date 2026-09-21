package com.strimup.feature.auth.domain.entity

/**
 * Outcome of the Twitch OAuth deep link (`strimup://oauth-callback?mode=...`),
 * relayed by the backend once it has exchanged the Twitch authorization code.
 */
sealed interface OAuthCallback {
    data class LoggedIn(val token: String, val refreshToken: String) : OAuthCallback
    data class Onboarding(val tmp: String) : OAuthCallback
    data class Linked(val token: String) : OAuthCallback
}

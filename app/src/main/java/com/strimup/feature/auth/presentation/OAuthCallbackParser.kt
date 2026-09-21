package com.strimup.feature.auth.presentation

import android.net.Uri
import com.strimup.feature.auth.domain.entity.OAuthCallback

private const val OAUTH_SCHEME = "strimup"
private const val OAUTH_HOST = "oauth-callback"
private const val MODE_LOGIN = "login"
private const val MODE_ONBOARDING = "onboarding"
private const val MODE_LINK = "link"

fun Uri.toOAuthCallback(): OAuthCallback? {
    if (scheme != OAUTH_SCHEME || host != OAUTH_HOST) return null

    return when (getQueryParameter("mode")) {
        MODE_LOGIN -> parseLoggedIn()
        MODE_ONBOARDING -> parseOnboarding()
        MODE_LINK -> parseLinked()
        else -> null
    }
}

private fun Uri.parseLoggedIn(): OAuthCallback.LoggedIn? {
    val token = getQueryParameter("token")
    val refreshToken = getQueryParameter("refresh_token")

    return if (token != null && refreshToken != null) {
        OAuthCallback.LoggedIn(token = token, refreshToken = refreshToken)
    } else {
        null
    }
}

private fun Uri.parseOnboarding(): OAuthCallback.Onboarding? {
    val tmp = getQueryParameter("tmp") ?: return null
    return OAuthCallback.Onboarding(tmp = tmp)
}

private fun Uri.parseLinked(): OAuthCallback.Linked? {
    val token = getQueryParameter("token") ?: return null
    return OAuthCallback.Linked(token = token)
}

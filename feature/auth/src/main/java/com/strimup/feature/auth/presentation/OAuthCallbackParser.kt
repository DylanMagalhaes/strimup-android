package com.strimup.feature.auth.presentation

import android.net.Uri
import com.strimup.feature.auth.domain.entity.OAuthCallback
import com.strimup.feature.auth.domain.entity.OAuthFailureReason

private const val OAUTH_SCHEME = "strimup"
private const val OAUTH_HOST = "oauth-callback"
private const val MODE_LOGIN = "login"
private const val MODE_ONBOARDING = "onboarding"
private const val MODE_LINK = "link"
private const val MODE_ERROR = "error"

fun Uri.toOAuthCallback(): OAuthCallback? =
    parseOAuthCallback(scheme = scheme, host = host, queryParameter = ::getQueryParameter)

internal fun parseOAuthCallback(
    scheme: String?,
    host: String?,
    queryParameter: (String) -> String?,
): OAuthCallback? {
    if (scheme != OAUTH_SCHEME || host != OAUTH_HOST) return null

    return when (queryParameter("mode")) {
        MODE_LOGIN -> queryParameter("code")?.takeIf { it.isNotBlank() }?.let(OAuthCallback::LoggedIn)
        MODE_LINK -> queryParameter("code")?.takeIf { it.isNotBlank() }?.let(OAuthCallback::Linked)
        MODE_ONBOARDING -> queryParameter("tmp")?.takeIf { it.isNotBlank() }?.let(OAuthCallback::Onboarding)
        MODE_ERROR -> OAuthCallback.Failed(OAuthFailureReason.fromApi(queryParameter("reason")))
        else -> null
    }
}

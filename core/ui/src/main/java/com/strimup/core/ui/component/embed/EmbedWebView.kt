package com.strimup.core.ui.component.embed

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.strimup.core.streamer.domain.entity.EMBED_PARENT_DOMAIN

internal const val EMBED_BASE_URL = "https://$EMBED_PARENT_DOMAIN/"

@SuppressLint("SetJavaScriptEnabled")
internal fun WebView.configureForEmbed() {
    setBackgroundColor(Color.Black.toArgb())
    isVerticalScrollBarEnabled = false
    isHorizontalScrollBarEnabled = false
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        mediaPlaybackRequiresUserGesture = false
        useWideViewPort = true
        loadWithOverviewMode = true
        setSupportZoom(false)
    }
    webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
            request.isForMainFrame
    }
}

internal fun WebView.loadEmbedHtml(html: String) {
    loadDataWithBaseURL(EMBED_BASE_URL, html, "text/html", "utf-8", null)
}

internal fun String.escapeHtmlAttribute(): String =
    replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;")

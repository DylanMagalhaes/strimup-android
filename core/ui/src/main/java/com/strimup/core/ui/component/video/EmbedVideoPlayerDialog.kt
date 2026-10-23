package com.strimup.core.ui.component.video

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.VideoOrientation
import com.strimup.core.streamer.domain.entity.playerUrl
import com.strimup.core.ui.R
import com.strimup.core.ui.component.embed.configureForEmbed
import com.strimup.core.ui.component.embed.escapeHtmlAttribute
import com.strimup.core.ui.component.embed.loadEmbedHtml
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable
fun EmbedVideoPlayerDialog(
    embed: VideoEmbed,
    title: String,
    onWatchOnPlatformClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .safeDrawingPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.video_close),
                        tint = Color.White,
                    )
                }
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                EmbedVideoPlayer(
                    url = embed.playerUrl(autoplay = true),
                    modifier = Modifier.playerSize(embed.orientation),
                )
            }

            WatchOnPlatformButton(
                provider = embed.provider,
                onClick = onWatchOnPlatformClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

private fun Modifier.playerSize(orientation: VideoOrientation): Modifier = when (orientation) {
    VideoOrientation.VERTICAL ->
        fillMaxHeight().aspectRatio(orientation.aspectRatio, matchHeightConstraintsFirst = true)
    VideoOrientation.HORIZONTAL ->
        fillMaxWidth().aspectRatio(orientation.aspectRatio)
}

@Composable
private fun EmbedVideoPlayer(url: String, modifier: Modifier = Modifier) {
    var webView by remember { mutableStateOf<WebView?>(null) }

    LifecycleResumeEffect(webView) {
        webView?.onResume()
        onPauseOrDispose { webView?.onPause() }
    }

    AndroidView(
        modifier = modifier.background(Color.Black),
        factory = { context ->
            WebView(context).apply {
                configureForEmbed()
                webView = this
            }
        },
        update = { view ->
            if (view.tag != url) {
                view.tag = url
                view.loadEmbedHtml(embedPlayerHtml(url))
            }
        },
        onRelease = { view ->
            webView = null
            view.destroy()
        },
    )
}

@Composable
private fun WatchOnPlatformButton(
    provider: VideoEmbed.Provider,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = provider.brandColor,
            contentColor = Color.White,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(provider.iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(provider.watchOnRes),
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun embedPlayerHtml(url: String): String = """
    <!DOCTYPE html>
    <html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
    <style>
    html, body { margin: 0; padding: 0; width: 100%; height: 100%; background: #000; overflow: hidden; }
    iframe { display: block; border: 0; width: 100%; height: 100%; }
    </style>
    </head>
    <body>
    <iframe
        src="${url.escapeHtmlAttribute()}"
        allow="autoplay; encrypted-media; fullscreen; picture-in-picture"
        referrerpolicy="strict-origin-when-cross-origin"
        allowfullscreen>
    </iframe>
    </body>
    </html>
""".trimIndent()

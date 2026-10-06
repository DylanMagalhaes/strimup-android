package com.strimup.core.ui.component.player

internal const val YOUTUBE_PLAYER_ORIGIN = "https://strimup.com"

private const val PROGRESS_INTERVAL_MS = 250

internal fun buildYouTubePlayerHtml(videoId: String, isVertical: Boolean): String {
    val safeId = videoId.filter { it.isLetterOrDigit() || it == '_' || it == '-' }

    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
        <style>
            ${buildPageCss(isVertical)}
        </style>
    </head>
    <body>
        <div id="stage"><div id="player"></div></div>
        <script src="https://www.youtube.com/iframe_api"></script>
        <script>
            ${buildPlayerScript(safeId)}
        </script>
    </body>
    </html>
    """.trimIndent()
}

private fun buildPageCss(isVertical: Boolean): String {
    val playerCss = if (isVertical) {
        """
        #player {
            position: absolute;
            top: 0;
            left: 50%;
            height: 100vh;
            width: calc(100vh * 16 / 9);
            transform: translateX(-50%);
        }
        """
    } else {
        """
        #player {
            position: absolute;
            top: 50%;
            left: 0;
            width: 100vw;
            height: calc(100vw * 9 / 16);
            transform: translateY(-50%);
        }
        """
    }

    return """
    html, body {
        margin: 0;
        padding: 0;
        width: 100%;
        height: 100%;
        background: #000;
        overflow: hidden;
    }
    #stage {
        position: fixed;
        inset: 0;
        background: #000;
        overflow: hidden;
        pointer-events: none;
    }
    $playerCss
    """.trimIndent()
}

private fun buildPlayerScript(videoId: String): String {
    val bridge = YouTubePlayerBridge.NAME

    return """
    var player;
    function isReady() { return player && player.getPlayerState; }
    function playVideo() { if (isReady()) player.playVideo(); }
    function pauseVideo() { if (isReady()) player.pauseVideo(); }
    function seekVideo(seconds) { if (isReady()) player.seekTo(seconds, true); }
    function reportProgress() {
        if (!isReady()) return;
        $bridge.onProgress(player.getCurrentTime() || 0, player.getDuration() || 0);
    }
    function onYouTubeIframeAPIReady() {
        player = new YT.Player('player', {
            videoId: '$videoId',
            playerVars: {
                autoplay: 1,
                controls: 0,
                disablekb: 1,
                playsinline: 1,
                rel: 0,
                modestbranding: 1,
                fs: 0,
                iv_load_policy: 3,
                origin: '$YOUTUBE_PLAYER_ORIGIN'
            },
            events: {
                onReady: function (e) {
                    e.target.playVideo();
                    setInterval(reportProgress, $PROGRESS_INTERVAL_MS);
                },
                onStateChange: function (e) { $bridge.onStateChange(e.data); }
            }
        });
    }
    """.trimIndent()
}

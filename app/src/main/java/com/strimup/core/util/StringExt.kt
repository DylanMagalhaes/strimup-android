package com.strimup.core.util

private val YOUTUBE_REGEX =
    "(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/|youtube\\.com/shorts/)([a-zA-Z0-9_-]{11})".toRegex()

fun String.extractYouTubeVideoId(): String? {
    return YOUTUBE_REGEX.find(this)?.groupValues?.get(1)
}
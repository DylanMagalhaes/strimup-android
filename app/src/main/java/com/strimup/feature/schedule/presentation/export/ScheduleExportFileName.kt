package com.strimup.feature.schedule.presentation.export

private val unsafeFileNameCharacters = Regex("[^A-Za-z0-9_-]+")
private const val FALLBACK_FILE_NAME = "strimup"

fun scheduleExportFileName(username: String): String {
    val safeUsername = username.trim()
        .replace(unsafeFileNameCharacters, "-")
        .trim('-')
        .ifEmpty { FALLBACK_FILE_NAME }
    return "planning-$safeUsername.png"
}

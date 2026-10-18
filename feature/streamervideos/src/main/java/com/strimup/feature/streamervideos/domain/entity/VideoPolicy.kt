package com.strimup.feature.streamervideos.domain.entity

private const val BYTES_PER_MEGABYTE = 1024L * 1024L

object VideoPolicy {
    const val MAX_VIDEOS = 3
    const val MAX_FILE_SIZE_MEGABYTES = 80L
    const val MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MEGABYTES * BYTES_PER_MEGABYTE
    const val MIN_TITLE_LENGTH = 2
    const val MAX_TITLE_LENGTH = 120
    const val MAX_DESCRIPTION_LENGTH = 1000

    fun formatOf(file: LocalVideoFile): VideoFormat? {
        val mimeType = file.mimeType?.lowercase()
        val extension = file.fileName?.substringAfterLast('.', missingDelimiterValue = "")?.lowercase()
        return VideoFormat.entries.firstOrNull { it.mimeType == mimeType }
            ?: VideoFormat.entries.firstOrNull { it.extension == extension }
    }

    fun isTooLarge(file: LocalVideoFile): Boolean = (file.sizeBytes ?: 0L) > MAX_FILE_SIZE_BYTES

    fun isValidTitle(title: String): Boolean = title.trim().length in MIN_TITLE_LENGTH..MAX_TITLE_LENGTH

    fun isValidDescription(description: String?): Boolean =
        description == null || description.trim().length <= MAX_DESCRIPTION_LENGTH
}

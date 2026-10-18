package com.strimup.feature.streamervideos.data

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.InputStream

private const val SEGMENT_SIZE = 64L * 1024
private const val PERCENT = 100

class StreamingVideoRequestBody(
    private val mediaType: MediaType,
    private val sizeBytes: Long?,
    private val openStream: () -> InputStream,
    private val onProgress: (percent: Int) -> Unit,
) : RequestBody() {

    override fun contentType(): MediaType = mediaType

    override fun contentLength(): Long = sizeBytes ?: -1L

    override fun writeTo(sink: BufferedSink) {
        openStream().source().use { source ->
            var written = 0L
            var lastPercent = -1
            while (true) {
                val read = source.read(sink.buffer, SEGMENT_SIZE)
                if (read == -1L) break
                sink.emit()
                written += read
                val percent = progressPercent(written)
                if (percent != null && percent != lastPercent) {
                    lastPercent = percent
                    onProgress(percent)
                }
            }
        }
    }

    private fun progressPercent(written: Long): Int? {
        val total = sizeBytes?.takeIf { it > 0 } ?: return null
        return (written * PERCENT / total).toInt().coerceAtMost(PERCENT)
    }
}

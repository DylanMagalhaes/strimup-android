package com.strimup.core.ui.component.video

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val CACHE_SIZE = 12
private const val FRAME_WIDTH = 480
private const val FRAME_HEIGHT = 270

internal object VideoFrameLoader {
    private val frames = LruCache<String, Bitmap>(CACHE_SIZE)
    private val failedUrls = mutableSetOf<String>()
    private val mutex = Mutex()

    suspend fun firstFrame(url: String): Bitmap? = mutex.withLock {
        frames.get(url)?.let { return@withLock it }
        if (url in failedUrls) return@withLock null

        val frame = withContext(Dispatchers.IO) { readFirstFrame(url) }
        if (frame != null) frames.put(url, frame) else failedUrls += url
        frame
    }

    private fun readFirstFrame(url: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(url, emptyMap())
            retriever.getScaledFrameAtTime(
                0L,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                FRAME_WIDTH,
                FRAME_HEIGHT,
            )
        } catch (_: RuntimeException) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }
}

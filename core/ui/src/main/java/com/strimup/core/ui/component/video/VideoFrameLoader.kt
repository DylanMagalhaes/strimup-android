package com.strimup.core.ui.component.video

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.LruCache
import com.strimup.core.streamer.domain.entity.VideoOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val CACHE_SIZE = 12
private const val FRAME_MAX_SIDE = 480

internal data class HostedVideoFrame(
    val bitmap: Bitmap?,
    val orientation: VideoOrientation?,
)

internal object VideoFrameLoader {
    private val frames = LruCache<String, HostedVideoFrame>(CACHE_SIZE)
    private val failedUrls = mutableSetOf<String>()
    private val mutex = Mutex()

    suspend fun load(url: String): HostedVideoFrame? = mutex.withLock {
        frames.get(url)?.let { return@withLock it }
        if (url in failedUrls) return@withLock null

        val frame = withContext(Dispatchers.IO) { readFrame(url) }
        if (frame != null) frames.put(url, frame) else failedUrls += url
        frame
    }

    private fun readFrame(url: String): HostedVideoFrame? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(url, emptyMap())
            val orientation = VideoOrientation.fromDimensions(
                width = retriever.intMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),
                height = retriever.intMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT),
                rotationDegrees = retriever.intMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION),
            )
            val bitmap = retriever.getScaledFrameAtTime(
                0L,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                FRAME_MAX_SIDE,
                FRAME_MAX_SIDE,
            )
            HostedVideoFrame(bitmap = bitmap, orientation = orientation)
                .takeIf { bitmap != null || orientation != null }
        } catch (_: RuntimeException) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }

    private fun MediaMetadataRetriever.intMetadata(key: Int): Int =
        extractMetadata(key)?.toIntOrNull() ?: 0
}

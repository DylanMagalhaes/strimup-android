package com.strimup.feature.streamervideos.data

import com.google.common.truth.Truth.assertThat
import okhttp3.MediaType.Companion.toMediaType
import okio.Buffer
import org.junit.Test

class StreamingVideoRequestBodyTest {

    private val bytes = ByteArray(200_000) { (it % 251).toByte() }

    @Test
    fun `writeTo should stream every byte of the file`() {
        val body = StreamingVideoRequestBody(
            mediaType = "video/mp4".toMediaType(),
            sizeBytes = bytes.size.toLong(),
            openStream = { bytes.inputStream() },
            onProgress = {},
        )
        val sink = Buffer()

        body.writeTo(sink)

        assertThat(sink.readByteArray()).isEqualTo(bytes)
    }

    @Test
    fun `writeTo should report an increasing progress ending at 100`() {
        val progress = mutableListOf<Int>()
        val body = StreamingVideoRequestBody(
            mediaType = "video/mp4".toMediaType(),
            sizeBytes = bytes.size.toLong(),
            openStream = { bytes.inputStream() },
            onProgress = { progress += it },
        )

        body.writeTo(Buffer())

        assertThat(progress).isInStrictOrder()
        assertThat(progress.last()).isEqualTo(100)
    }

    @Test
    fun `unknown size should stream without reporting progress`() {
        val progress = mutableListOf<Int>()
        val body = StreamingVideoRequestBody(
            mediaType = "video/webm".toMediaType(),
            sizeBytes = null,
            openStream = { bytes.inputStream() },
            onProgress = { progress += it },
        )

        body.writeTo(Buffer())

        assertThat(body.contentLength()).isEqualTo(-1L)
        assertThat(progress).isEmpty()
    }

    @Test
    fun `contentType should be the declared video mime type`() {
        val body = StreamingVideoRequestBody(
            mediaType = "video/quicktime".toMediaType(),
            sizeBytes = 10L,
            openStream = { ByteArray(10).inputStream() },
            onProgress = {},
        )

        assertThat(body.contentType().toString()).isEqualTo("video/quicktime")
    }
}

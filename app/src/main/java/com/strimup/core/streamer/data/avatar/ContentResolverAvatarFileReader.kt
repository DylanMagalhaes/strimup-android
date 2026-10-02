package com.strimup.core.streamer.data.avatar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

private const val JPEG_MIME_TYPE = "image/jpeg"
private const val JPEG_QUALITY = 85
private const val ROTATION_90 = 90f
private const val ROTATION_180 = 180f
private const val ROTATION_270 = 270f

class ContentResolverAvatarFileReader @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AvatarFileReader {

    override suspend fun read(uri: String): AvatarFile = withContext(Dispatchers.IO) {
        val imageUri = uri.toUri()
        val decoded = decodeSampled(imageUri)
        val resized = decoded.resizeToAvatar().rotate(readRotation(imageUri))

        val output = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
        resized.recycle()

        AvatarFile(bytes = output.toByteArray(), mimeType = JPEG_MIME_TYPE)
    }

    private fun decodeSampled(uri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

        require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Image illisible" }

        val options = BitmapFactory.Options().apply {
            inSampleSize = avatarSampleSize(bounds.outWidth, bounds.outHeight)
        }
        return requireNotNull(
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        ) { "Image illisible" }
    }

    private fun readRotation(uri: Uri): Float {
        val orientation = context.contentResolver.openInputStream(uri)?.use { input ->
            ExifInterface(input).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> ROTATION_90
            ExifInterface.ORIENTATION_ROTATE_180 -> ROTATION_180
            ExifInterface.ORIENTATION_ROTATE_270 -> ROTATION_270
            else -> 0f
        }
    }

    private fun Bitmap.resizeToAvatar(): Bitmap {
        val (targetWidth, targetHeight) = avatarTargetSize(width, height)
        if (targetWidth == width && targetHeight == height) return this

        return Bitmap.createScaledBitmap(this, targetWidth, targetHeight, true).also { if (it !== this) recycle() }
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        if (degrees == 0f) return this

        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true).also { if (it !== this) recycle() }
    }
}

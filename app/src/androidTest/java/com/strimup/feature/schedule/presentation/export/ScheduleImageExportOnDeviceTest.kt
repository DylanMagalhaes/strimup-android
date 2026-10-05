package com.strimup.feature.schedule.presentation.export

import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ScheduleImageExportOnDeviceTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun exportedFile(name: String): File {
        val directory = File(context.cacheDir, "schedule_export/black").apply { mkdirs() }
        return File(directory, name).apply {
            val bitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
            outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Test
    fun exportedFileShouldBeSharedThroughTheFileProvider() {
        val file = exportedFile("planning-share-test.png")

        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)

        assertThat(uri.authority).isEqualTo("com.strimup.fileprovider")
        assertThat(context.contentResolver.getType(uri)).isEqualTo("image/png")
    }

    @Test
    fun savingToTheGalleryShouldCreateTheImageInPicturesStrimUp() = runTest {
        val file = exportedFile("planning-gallery-test-${System.currentTimeMillis()}.png")
        val saver = DefaultScheduleImageGallerySaver(context)

        val result = saver.save(file)

        assertThat(result.isSuccess).isTrue()
        val resolver = context.contentResolver
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.RELATIVE_PATH)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, arrayOf(file.name), null)
            .use { cursor ->
                assertThat(cursor!!.moveToFirst()).isTrue()
                assertThat(cursor.getString(1)).isEqualTo("Pictures/StrimUp/")
                val id = cursor.getLong(0)
                resolver.delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "${MediaStore.Images.Media._ID} = ?",
                    arrayOf(id.toString()),
                )
            }
    }
}

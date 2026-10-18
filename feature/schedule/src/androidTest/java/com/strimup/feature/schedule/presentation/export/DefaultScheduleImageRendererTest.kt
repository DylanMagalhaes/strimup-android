package com.strimup.feature.schedule.presentation.export

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.strimup.feature.schedule.domain.entity.ScheduleItemEntity
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class DefaultScheduleImageRendererTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val renderer = DefaultScheduleImageRenderer(context)

    private val busySchedule = listOf(
        ScheduleItemEntity(id = "1", title = "Just Chatting & Q/R du soir", dayOfWeek = 0, startTime = "14:00"),
        ScheduleItemEntity(id = "2", title = "Soirée Valorant ranked 🎮", dayOfWeek = 0, startTime = "18:30"),
        ScheduleItemEntity(id = "3", title = "a".repeat(60), dayOfWeek = 0, startTime = "22:00"),
        ScheduleItemEntity(
            id = "4",
            title = "GTA RP avec la commu, ça va être génial à fond",
            dayOfWeek = 2,
            startTime = "20:00",
        ),
        ScheduleItemEntity(id = "5", title = "Tournoi", dayOfWeek = 5, startTime = "09:00"),
    )

    @Test
    fun renderedImageShouldAlwaysBe1080x1920WithOpaqueCorners() = runTest {
        ScheduleExportTemplate.entries.forEach { template ->
            val bitmap = renderer.render(
                username = "UnPseudoVraimentBeaucoupTropLongPourTenirSurUneLigne",
                days = busySchedule.toExportDays(),
                template = template,
            )

            assertThat(bitmap.width).isEqualTo(1080)
            assertThat(bitmap.height).isEqualTo(1920)
            listOf(0 to 0, 1079 to 0, 0 to 1919, 1079 to 1919).forEach { (x, y) ->
                assertThat(Color.alpha(bitmap.getPixel(x, y))).isEqualTo(255)
            }
            bitmap.saveToCache("schedule-export-${template.name.lowercase()}.png")
        }
    }

    @Test
    fun renderingAnEmptyScheduleShouldNotFail() = runTest {
        val bitmap = renderer.render(
            username = "raziuko",
            days = emptyList<ScheduleItemEntity>().toExportDays(),
            template = ScheduleExportTemplate.Black,
        )

        assertThat(bitmap.width).isEqualTo(1080)
        bitmap.saveToCache("schedule-export-empty.png")
    }

    private fun Bitmap.saveToCache(fileName: String) {
        File(context.cacheDir, fileName).outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}

package com.strimup.feature.schedule.presentation.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import androidx.annotation.DrawableRes
import com.strimup.R
import com.strimup.feature.schedule.presentation.exportLabelRes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class DefaultScheduleImageRenderer @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ScheduleImageRenderer {

    override suspend fun render(
        username: String,
        days: List<ScheduleExportDay>,
        template: ScheduleExportTemplate,
    ): Bitmap = withContext(Dispatchers.Default) {
        val paints = ScheduleExportPaints(ScheduleExportFonts.load(context))
        val bitmap = Bitmap.createBitmap(
            ScheduleExportLayout.IMAGE_WIDTH,
            ScheduleExportLayout.IMAGE_HEIGHT,
            Bitmap.Config.ARGB_8888,
        )
        val canvas = Canvas(bitmap)

        drawBackground(canvas, template.backgroundRes)
        val labelTop = drawUsername(canvas, paints, username, top = ScheduleExportLayout.CONTENT_TOP)
        val gridTop = drawWeekLabel(canvas, paints, top = labelTop)

        val cellDrawer = ScheduleCellDrawer(
            paints = paints,
            dayLabel = { day -> context.getString(day.exportLabelRes()) },
            offLabel = context.getString(R.string.schedule_export_off),
        )
        ScheduleExportLayout.gridCells(gridTop).zip(days).forEach { (bounds, day) ->
            cellDrawer.draw(canvas, bounds, day)
        }

        bitmap
    }

    private fun drawBackground(canvas: Canvas, @DrawableRes backgroundRes: Int) {
        canvas.drawColor(Color.BLACK)
        val options = BitmapFactory.Options().apply { inScaled = false }
        val background = BitmapFactory.decodeResource(context.resources, backgroundRes, options) ?: return
        val destination = Rect(0, 0, ScheduleExportLayout.IMAGE_WIDTH, ScheduleExportLayout.IMAGE_HEIGHT)
        canvas.drawBitmap(background, null, destination, Paint(Paint.FILTER_BITMAP_FLAG))
        background.recycle()
    }

    private fun drawUsername(canvas: Canvas, paints: ScheduleExportPaints, username: String, top: Float): Float {
        val paint = paints.username()
        val availableWidth = ScheduleExportLayout.CONTENT_WIDTH - 2 * USERNAME_HORIZONTAL_PADDING
        val text = TextUtils.ellipsize(
            username.uppercase(Locale.FRENCH),
            paint,
            availableWidth,
            TextUtils.TruncateAt.END,
        ).toString()

        val metrics = paint.fontMetrics
        canvas.drawText(text, ScheduleExportLayout.IMAGE_WIDTH / 2f, top - metrics.ascent, paint)
        return top + (metrics.descent - metrics.ascent) + USERNAME_BOTTOM_SPACING
    }

    private fun drawWeekLabel(canvas: Canvas, paints: ScheduleExportPaints, top: Float): Float {
        val paint = paints.weekLabel()
        val text = context.getString(R.string.schedule_export_week_label).uppercase(Locale.FRENCH)

        val metrics = paint.fontMetrics
        canvas.drawText(text, ScheduleExportLayout.CONTENT_LEFT, top - metrics.ascent, paint)
        return top + (metrics.descent - metrics.ascent) + WEEK_LABEL_BOTTOM_SPACING
    }

    private companion object {
        const val USERNAME_HORIZONTAL_PADDING = 24f
        const val USERNAME_BOTTOM_SPACING = 96f
        const val WEEK_LABEL_BOTTOM_SPACING = 24f
    }
}

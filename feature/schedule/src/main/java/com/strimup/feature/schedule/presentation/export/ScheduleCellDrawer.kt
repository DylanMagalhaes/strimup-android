package com.strimup.feature.schedule.presentation.export

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import java.time.DayOfWeek
import java.util.Locale

class ScheduleCellDrawer(
    private val paints: ScheduleExportPaints,
    private val dayLabel: (DayOfWeek) -> String,
    private val offLabel: String,
) {
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = BORDER_WIDTH
    }

    fun draw(canvas: Canvas, bounds: CellBounds, day: ScheduleExportDay) {
        if (day.slots.isEmpty()) {
            drawBackground(canvas, bounds, fillColor = OFF_FILL_COLOR, strokeColor = OFF_STROKE_COLOR)
            drawBlocks(canvas, bounds, offBlocks(day.dayOfWeek, innerWidth(bounds)))
        } else {
            drawBackground(canvas, bounds, fillColor = SLOTS_FILL_COLOR, strokeColor = BRAND_PINK)
            drawBlocks(canvas, bounds, fittedSlotBlocks(day, innerWidth(bounds), innerHeight(bounds)))
        }
    }

    private fun drawBackground(canvas: Canvas, bounds: CellBounds, fillColor: Int, strokeColor: Int) {
        val inset = BORDER_WIDTH / 2
        val rect = RectF(bounds.left + inset, bounds.top + inset, bounds.right - inset, bounds.bottom - inset)
        fillPaint.color = fillColor
        strokePaint.color = strokeColor
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, fillPaint)
        canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, strokePaint)
    }

    private fun drawBlocks(canvas: Canvas, bounds: CellBounds, blocks: List<TextBlock>) {
        var y = bounds.top + CELL_PADDING + (innerHeight(bounds) - blocks.totalHeight()) / 2
        blocks.forEach { block ->
            y += block.gapBefore
            canvas.save()
            canvas.translate(bounds.left + CELL_PADDING, y)
            block.layout.draw(canvas)
            canvas.restore()
            y += block.layout.height
        }
    }

    private fun offBlocks(dayOfWeek: DayOfWeek, width: Int): List<TextBlock> = listOf(
        TextBlock(layout = layout(dayText(dayOfWeek), paints.day(scale = 1f, isOff = true), width)),
        TextBlock(layout = layout(offLabel.uppercase(Locale.FRENCH), paints.off(), width), gapBefore = SLOT_GAP),
    )

    private fun fittedSlotBlocks(day: ScheduleExportDay, width: Int, maxHeight: Float): List<TextBlock> {
        val fittingBlocks = (0..SCALE_STEPS)
            .asSequence()
            .map { step -> slotBlocks(day, width, 1f - step * SCALE_STEP, titleMaxLines = Int.MAX_VALUE) }
            .firstOrNull { it.totalHeight() <= maxHeight }

        return fittingBlocks ?: slotBlocks(day, width, MIN_SCALE, titleMaxLines = 1)
    }

    private fun slotBlocks(day: ScheduleExportDay, width: Int, scale: Float, titleMaxLines: Int): List<TextBlock> {
        val dayBlock = TextBlock(layout = layout(dayText(day.dayOfWeek), paints.day(scale, isOff = false), width))
        val slotBlocks = day.slots.flatMap { slot ->
            listOf(
                TextBlock(layout = layout(slot.startTime, paints.time(scale), width), gapBefore = SLOT_GAP * scale),
                TextBlock(
                    layout = layout(slot.title, paints.title(scale), width, titleMaxLines),
                    gapBefore = TITLE_GAP * scale,
                ),
            )
        }
        return listOf(dayBlock) + slotBlocks
    }

    private fun dayText(dayOfWeek: DayOfWeek): String = dayLabel(dayOfWeek).uppercase(Locale.FRENCH)

    private fun innerWidth(bounds: CellBounds): Int = (bounds.width - 2 * CELL_PADDING).toInt()

    private fun innerHeight(bounds: CellBounds): Float = bounds.height - 2 * CELL_PADDING

    private data class TextBlock(
        val layout: StaticLayout,
        val gapBefore: Float = 0f,
    )

    private companion object {
        const val BRAND_PINK = 0xFFE91E63.toInt()
        const val SLOTS_FILL_COLOR = 0x73000000
        const val OFF_FILL_COLOR = 0x40000000
        const val OFF_STROKE_COLOR = 0x1AFFFFFF
        const val BORDER_WIDTH = 3f
        const val CORNER_RADIUS = 36f
        const val CELL_PADDING = 24f
        const val SLOT_GAP = 18f
        const val TITLE_GAP = 12f
        const val SCALE_STEP = 0.05f
        const val SCALE_STEPS = 12
        const val MIN_SCALE = 1f - SCALE_STEPS * SCALE_STEP

        fun List<TextBlock>.totalHeight(): Float = sumOf { (it.gapBefore + it.layout.height).toDouble() }.toFloat()

        fun layout(text: String, paint: TextPaint, width: Int, maxLines: Int = 1): StaticLayout =
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setIncludePad(false)
                .setMaxLines(maxLines)
                .setEllipsize(TextUtils.TruncateAt.END)
                .build()
    }
}

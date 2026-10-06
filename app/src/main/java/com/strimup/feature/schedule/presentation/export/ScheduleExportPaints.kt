package com.strimup.feature.schedule.presentation.export

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint

class ScheduleExportPaints(private val fonts: ScheduleExportFonts) {

    fun username(): TextPaint = textPaint(
        typeface = fonts.zalandoBlackItalic,
        size = USERNAME_TEXT_SIZE,
        letterSpacing = USERNAME_LETTER_SPACING,
    ).apply {
        textAlign = Paint.Align.CENTER
        setShadowLayer(USERNAME_SHADOW_RADIUS, 0f, USERNAME_SHADOW_OFFSET_Y, USERNAME_SHADOW_COLOR)
    }

    fun weekLabel(): TextPaint = textPaint(
        typeface = fonts.zalandoBlack,
        size = WEEK_LABEL_TEXT_SIZE,
        letterSpacing = WEEK_LABEL_LETTER_SPACING,
        alpha = WEEK_LABEL_ALPHA,
    )

    fun day(scale: Float, isOff: Boolean): TextPaint = textPaint(
        typeface = fonts.zalandoBlack,
        size = DAY_TEXT_SIZE * scale,
        letterSpacing = DAY_LETTER_SPACING,
        alpha = if (isOff) OFF_DAY_ALPHA else OPAQUE,
    )

    fun time(scale: Float): TextPaint = textPaint(
        typeface = fonts.montserratBlack,
        size = TIME_TEXT_SIZE * scale,
    ).apply { fontFeatureSettings = TABULAR_NUMBERS }

    fun title(scale: Float): TextPaint = textPaint(
        typeface = fonts.montserratSemiBold,
        size = TITLE_TEXT_SIZE * scale,
        alpha = TITLE_ALPHA,
    )

    fun off(): TextPaint = textPaint(
        typeface = fonts.montserratBold,
        size = OFF_TEXT_SIZE,
        letterSpacing = DAY_LETTER_SPACING,
        alpha = OFF_TEXT_ALPHA,
    )

    private fun textPaint(
        typeface: Typeface,
        size: Float,
        letterSpacing: Float = 0f,
        alpha: Int = OPAQUE,
    ): TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.typeface = typeface
        textSize = size
        this.letterSpacing = letterSpacing
        color = Color.WHITE
        this.alpha = alpha
    }

    private companion object {
        const val OPAQUE = 255
        const val TABULAR_NUMBERS = "tnum"
        const val USERNAME_TEXT_SIZE = 90f
        const val USERNAME_LETTER_SPACING = -0.025f
        const val USERNAME_SHADOW_RADIUS = 12f
        const val USERNAME_SHADOW_OFFSET_Y = 4f
        const val USERNAME_SHADOW_COLOR = 0x66000000
        const val WEEK_LABEL_TEXT_SIZE = 27f
        const val WEEK_LABEL_LETTER_SPACING = 0.2f
        const val WEEK_LABEL_ALPHA = 204
        const val DAY_TEXT_SIZE = 27f
        const val DAY_LETTER_SPACING = 0.1f
        const val OFF_DAY_ALPHA = 128
        const val TIME_TEXT_SIZE = 36f
        const val TITLE_TEXT_SIZE = 24f
        const val TITLE_ALPHA = 204
        const val OFF_TEXT_SIZE = 21f
        const val OFF_TEXT_ALPHA = 102
    }
}

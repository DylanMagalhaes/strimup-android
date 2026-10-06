package com.strimup.feature.schedule.presentation.export

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.strimup.R

data class ScheduleExportFonts(
    val zalandoBlack: Typeface,
    val zalandoBlackItalic: Typeface,
    val montserratBlack: Typeface,
    val montserratBold: Typeface,
    val montserratSemiBold: Typeface,
) {
    companion object {
        fun load(context: Context): ScheduleExportFonts = ScheduleExportFonts(
            zalandoBlack = context.font(R.font.zalando_sans_black),
            zalandoBlackItalic = context.font(R.font.zalando_sans_black_italic),
            montserratBlack = context.font(R.font.montserrat_black),
            montserratBold = context.font(R.font.montserrat_bold),
            montserratSemiBold = context.font(R.font.montserrat_semi_bold),
        )

        private fun Context.font(@FontRes fontRes: Int): Typeface =
            ResourcesCompat.getFont(this, fontRes) ?: Typeface.DEFAULT_BOLD
    }
}

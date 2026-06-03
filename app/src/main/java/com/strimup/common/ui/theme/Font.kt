package com.strimup.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.strimup.R

@Immutable class StrimUpFontFamily(
    private val useSparkTokensHighlighter: Boolean,
    private val fontFamily: FontFamily = montserratFontFamily
) {
    val default: FontFamily
        @Composable @ReadOnlyComposable
        get() = when {
            LocalInspectionMode.current -> fontFamily
            useSparkTokensHighlighter -> FontFamily.Cursive
            else -> fontFamily
        }
}

internal val montserratFontFamily = FontFamily(
    fonts = listOf(
        Font(resId = R.font.montserrat_regular, weight = FontWeight.Normal),
        Font(
            resId = R.font.montserrat_italic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic
        ),
        Font(resId = R.font.montserrat_semi_bold, weight = FontWeight.SemiBold),
        Font(
            resId = R.font.montserrat_semi_bold_italic,
            weight = FontWeight.SemiBold,
            style = FontStyle.Italic
        ),
        Font(resId = R.font.montserrat_bold, weight = FontWeight.Bold),
        Font(
            resId = R.font.montserrat_bold_italic,
            weight = FontWeight.Bold,
            style = FontStyle.Italic
        ),
    ),
)

internal val zalandoFontFamily = FontFamily(
    fonts = listOf(
        Font(resId = R.font.zalando_sans_regular, weight = FontWeight.Normal),
        Font(
            resId = R.font.zalando_sans_italic,
            weight = FontWeight.Normal,
            style = FontStyle.Italic
        ),
        Font(resId = R.font.zalando_sans_semi_bold, weight = FontWeight.SemiBold),
        Font(
            resId = R.font.zalando_sans_semi_bold_italic,
            weight = FontWeight.SemiBold,
            style = FontStyle.Italic
        ),
        Font(resId = R.font.zalando_sans_bold, weight = FontWeight.Bold),
        Font(
            resId = R.font.zalando_sans_bold_italic,
            weight = FontWeight.Bold,
            style = FontStyle.Italic
        ),
    ),
)
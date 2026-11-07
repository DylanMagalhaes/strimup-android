package com.strimup.feature.cuts.presentation.component

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private const val THOUSAND = 1_000
private const val MILLION = 1_000_000

private val shortFormat = DecimalFormat("0.#", DecimalFormatSymbols(Locale.FRANCE)).apply {
    roundingMode = RoundingMode.DOWN
}

fun formatLikesCount(count: Int): String = when {
    count < THOUSAND -> count.coerceAtLeast(0).toString()
    count < MILLION -> "${shortFormat.format(count / THOUSAND.toDouble())}k"
    else -> "${shortFormat.format(count / MILLION.toDouble())}M"
}

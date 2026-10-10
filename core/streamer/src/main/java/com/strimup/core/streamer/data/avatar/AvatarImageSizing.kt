package com.strimup.core.streamer.data.avatar

import kotlin.math.max
import kotlin.math.roundToInt

const val AVATAR_MAX_DIMENSION = 1024

fun avatarSampleSize(width: Int, height: Int, maxDimension: Int = AVATAR_MAX_DIMENSION): Int {
    var sampleSize = 1
    while (max(width, height) / (sampleSize * 2) >= maxDimension) {
        sampleSize *= 2
    }
    return sampleSize
}

fun avatarTargetSize(width: Int, height: Int, maxDimension: Int = AVATAR_MAX_DIMENSION): Pair<Int, Int> {
    val longestSide = max(width, height)
    if (longestSide <= maxDimension) return width to height

    val ratio = maxDimension.toFloat() / longestSide
    return (width * ratio).roundToInt().coerceAtLeast(1) to (height * ratio).roundToInt().coerceAtLeast(1)
}

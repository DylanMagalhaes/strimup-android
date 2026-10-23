package com.strimup.core.streamer.domain.entity

private const val QUARTER_TURN_DEGREES = 90
private const val HALF_TURN_DEGREES = 180

enum class VideoOrientation {
    VERTICAL,
    HORIZONTAL;

    companion object {
        fun fromDimensions(width: Int, height: Int, rotationDegrees: Int = 0): VideoOrientation? {
            if (width <= 0 || height <= 0) return null
            val isQuarterTurned = Math.floorMod(rotationDegrees, HALF_TURN_DEGREES) == QUARTER_TURN_DEGREES
            val displayedWidth = if (isQuarterTurned) height else width
            val displayedHeight = if (isQuarterTurned) width else height
            return if (displayedHeight > displayedWidth) VERTICAL else HORIZONTAL
        }
    }
}

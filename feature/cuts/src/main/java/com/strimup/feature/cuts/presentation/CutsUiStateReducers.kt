package com.strimup.feature.cuts.presentation

import com.strimup.feature.cuts.domain.entity.Cut

internal fun CutsUiState.findCut(videoId: String): Cut? =
    slides.firstOrNull { it.cut.id == videoId }?.cut

internal fun CutsUiState.withLike(videoId: String, isLiked: Boolean, likesCount: Int): CutsUiState = copy(
    slides = slides.map { slide ->
        if (slide.cut.id == videoId) {
            slide.copy(cut = slide.cut.copy(isLikedByMe = isLiked, likesCount = likesCount.coerceAtLeast(0)))
        } else {
            slide
        }
    },
)

internal fun CutsUiState.withoutVideo(videoId: String): CutsUiState {
    val removedBeforeActive = slides.take(activeIndex).count { it.cut.id == videoId }
    val remainingSlides = slides.filterNot { it.cut.id == videoId }
    return copy(
        slides = remainingSlides,
        activeIndex = (activeIndex - removedBeforeActive).coerceIn(0, (remainingSlides.size - 1).coerceAtLeast(0)),
        reportedVideoId = reportedVideoId.takeIf { it != videoId },
    )
}

internal val CutsUiState.remainingSlides: Int
    get() = slides.size - activeIndex - 1

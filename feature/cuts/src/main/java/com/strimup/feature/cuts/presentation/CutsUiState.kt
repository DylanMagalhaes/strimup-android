package com.strimup.feature.cuts.presentation

import com.strimup.core.ui.text.UiText
import com.strimup.feature.cuts.domain.entity.Cut

data class CutSlide(
    val key: Long,
    val cut: Cut,
)

data class CutsUiState(
    val slides: List<CutSlide> = emptyList(),
    val activeIndex: Int = 0,
    val isLoading: Boolean = false,
    val canLoadMore: Boolean = true,
    val errorMessage: UiText? = null,
    val isMuted: Boolean = true,
    val isLikeDisabled: Boolean = false,
    val reportedVideoId: String? = null,
) {
    val isEmpty: Boolean
        get() = slides.isEmpty() && !isLoading && errorMessage == null && !canLoadMore
}

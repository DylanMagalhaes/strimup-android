package com.strimup.feature.home.presentation.hero

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.cuts.domain.entity.Cut

data class HeroLive(
    val streamer: Streamer,
    val channel: String,
)

data class HomeHeroUiState(
    val lives: List<HeroLive> = emptyList(),
    val cuts: List<Cut> = emptyList(),
    val isLoading: Boolean = true,
    val isMuted: Boolean = true,
) {
    val fallbackCut: Cut?
        get() = cuts.firstOrNull().takeIf { lives.isEmpty() }
}

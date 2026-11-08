package com.strimup.feature.home.presentation.hero

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.streamer.domain.entity.twitchChannel
import com.strimup.feature.cuts.domain.usecase.GetCutsUseCase
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.GetStreamersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeHeroViewModel @Inject constructor(
    private val getStreamers: GetStreamersUseCase,
    private val getCuts: GetCutsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeHeroUiState())
    val state = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val lives = async { getStreamers(FilterEntity.Live) }
            val cuts = async { getCuts(cursor = null) }

            val liveStreamers = lives.await().getOrNull()
            val cutsPage = cuts.await().getOrNull()
            _state.update { current ->
                current.copy(
                    lives = liveStreamers
                        ?.mapNotNull { streamer -> streamer.twitchChannel()?.let { HeroLive(streamer, it) } }
                        ?.shuffled()
                        ?: current.lives,
                    cuts = cutsPage?.cuts ?: current.cuts,
                    isLoading = false,
                )
            }
        }
    }

    fun onMuteClick() {
        _state.update { it.copy(isMuted = !it.isMuted) }
    }
}

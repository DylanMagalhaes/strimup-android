package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.domain.entity.StreamerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val streamerRepository: StreamerRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val streamers = streamerRepository.getRandomStreamers()

            _state.update {
                it.copy(
                    streamers = streamers,
                    loading = false,
                )
            }
        }
    }

    fun onSegmentedControlClick(tab: HomeTab) {
        if (tab == HomeTab.IN_LIVE && state.value.isInLiveSelected || tab == HomeTab.DISCOVERY && _state.value.isDiscoverySelected) return
        _state.update {
            it.copy(
                loading = true,
                isInLiveSelected = tab == HomeTab.IN_LIVE,
                isDiscoverySelected = tab == HomeTab.DISCOVERY,
            )
        }
        viewModelScope.launch {
           val streamers = when (tab) {
                HomeTab.IN_LIVE -> streamerRepository.getInLiveStreamers()
                HomeTab.DISCOVERY -> streamerRepository.getRandomStreamers()
            }

            _state.update {
                it.copy(
                    streamers = streamers,
                    loading = false,
                )
            }
        }
    }

}
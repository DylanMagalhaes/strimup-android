package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.home.domain.StreamerRepository
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

    fun onSegmentedControlClick(buttonName: HomeTab) {
        when(buttonName) {
            HomeTab.DISCOVERY -> {
                if (_state.value.isDiscoverySelected) return
                _state.update {
                    it.copy(
                        loading = true,
                        isInLiveSelected = false,
                        isDiscoverySelected = true,
                    )
                }
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
            HomeTab.IN_LIVE -> {
                if (_state.value.isInLiveSelected) return
                _state.update {
                    it.copy(
                        loading = true,
                        isInLiveSelected = true,
                        isDiscoverySelected = false,
                    )
                }
                viewModelScope.launch {
                    val streamers = streamerRepository.getInLiveStreamers()

                    _state.update {
                        it.copy(
                            streamers = streamers,
                            loading = false,
                        )
                    }
                }
            }
        }
    }
}
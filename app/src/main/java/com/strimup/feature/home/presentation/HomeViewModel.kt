package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.home.domain.StreamerRepository
import com.strimup.feature.home.presentation.model.HomeTab
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var fetchStreamersJob: Job? = null

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

    fun onTabClick(tab: HomeTab) {
        fetchStreamersJob?.cancel()

        _state.update {
            it.copy(
                loading = true,
                currentTab = tab,
            )
        }

        fetchStreamersJob =
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
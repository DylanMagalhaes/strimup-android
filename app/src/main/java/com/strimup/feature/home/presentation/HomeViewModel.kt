package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.GetStreamersUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStreamers: GetStreamersUsecase,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private var fetchStreamersJob: Job? = null

    init {
        viewModelScope.launch {
            val streamers = getStreamers(FilterEntity.Discovery)

            _state.update {
                it.copy(
                    streamers = streamers,
                    loading = false,
                )
            }
        }
    }

    fun onTabClick(filter: FilterEntity) {
        fetchStreamersJob?.cancel()

        _state.update {
            it.copy(
                loading = true,
                currentTab = filter,
            )
        }

        fetchStreamersJob =
            viewModelScope.launch {
                val streamers = getStreamers(filter)

                _state.update {
                    it.copy(
                        streamers = streamers,
                        loading = false,
                    )
                }
            }
    }

}
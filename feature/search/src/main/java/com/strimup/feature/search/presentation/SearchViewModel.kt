package com.strimup.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.search.domain.usecase.GetLiveStreamersUseCase
import com.strimup.feature.search.domain.usecase.GetStreamersUseCase
import com.strimup.feature.search.domain.usecase.ObserveRecentStreamersUseCase
import com.strimup.feature.search.domain.usecase.RemoveRecentStreamerUseCase
import com.strimup.feature.search.domain.usecase.SaveRecentStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getStreamers: GetStreamersUseCase,
    private val saveRecentStreamer: SaveRecentStreamerUseCase,
    private val removeRecentStreamer: RemoveRecentStreamerUseCase,
    observeRecentStreamers: ObserveRecentStreamersUseCase,
    getLiveStreamers: GetLiveStreamersUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Content())
    val state = _state.asStateFlow()

    val recentStreamers: StateFlow<List<Streamer>> = observeRecentStreamers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    val liveStreamers: StateFlow<List<Streamer>> = flow { emit(getLiveStreamers().getOrDefault(emptyList())) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), emptyList())

    private var searchJob: Job? = null

    fun onSearchInputChange(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _state.value = SearchUiState.Content(searchQuery = "")
            return
        }

        _state.value = SearchUiState.Loading(searchQuery = query)

        searchJob = viewModelScope.launch {
            delay(500L)

            getStreamers(query)
                .onSuccess { response ->
                    _state.value = if (response.isEmpty()) {
                        SearchUiState.Empty(searchQuery = query)
                    } else {
                        SearchUiState.Content(
                            searchQuery = query,
                            streamers = response,
                        )
                    }
                }
                .onFailure { exception ->
                    _state.value = SearchUiState.Error(
                        searchQuery = query,
                        messageRes = exception.toDomainError().toMessageRes(),
                    )
                }
        }
    }

    fun onStreamerClick(streamer: Streamer) {
        viewModelScope.launch { saveRecentStreamer(streamer) }
    }

    fun onRemoveRecentStreamerClick(id: String) {
        viewModelScope.launch { removeRecentStreamer(id) }
    }
}

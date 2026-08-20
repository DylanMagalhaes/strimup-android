package com.strimup.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.search.domain.usecase.DefaultGetStreamerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getStreamers: DefaultGetStreamerUsecase,
) : ViewModel() {

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Content())
    val state = _state.asStateFlow()

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
                        message = exception.localizedMessage ?: "Une erreur est survenue",
                    )
                }
        }
    }
}
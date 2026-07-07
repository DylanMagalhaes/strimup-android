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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getStreamers: DefaultGetStreamerUsecase
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()


    private var searchJob: Job? = null
    fun onSearchInputChange(query: String) {

        _state.update {
            it.copy(
                loading = query.isNotBlank(),
                searchQuery = query
            )
        }

        searchJob?.cancel()

        if (query.isBlank()) {
            _state.update {
                it.copy(
                    loading = false,
                    streamers = emptyList()
                )
            }
        }

        searchJob = viewModelScope.launch {

            delay(500L)
            getStreamers(query)
                .onSuccess { response ->
                    _state.update {
                        it.copy(
                            loading = false,
                            streamers = response,
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(loading = false) }
                }
        }
    }
}
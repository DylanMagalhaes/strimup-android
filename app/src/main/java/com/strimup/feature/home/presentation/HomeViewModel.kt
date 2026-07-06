package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.DefaultSearchStreamersUsecase
import com.strimup.feature.home.domain.usecase.GetStreamersUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel class HomeViewModel @Inject constructor(
    private val getStreamers: GetStreamersUsecase,
    private val searchStreamers: DefaultSearchStreamersUsecase,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    private var fetchStreamersJob: Job? = null

    init {
        viewModelScope.launch {
            fetchStreamers(_state.value.currentTab)
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

        fetchStreamersJob = fetchStreamers(filter)
    }

    private var searchJob: Job? = null
    fun onSearchQueryChange(query: String) {
        _state.update {
            it.copy(
                loadingSearch = query.isNotBlank(), searchQuery = query
            )
        }

        searchJob?.cancel()

        if (query.isBlank()) {
            _state.update { it.copy(streamersResultSearch = emptyList(), loadingSearch = false) }
            return
        }

        searchJob = viewModelScope.launch {

            delay(500L)
            searchStreamers(query).onSuccess { response ->
                    _state.update {
                        it.copy(
                            loadingSearch = false, streamersResultSearch = response
                        )
                    }
                }.onFailure { exception ->
                    _state.update { it.copy(loadingSearch = false) }
                _events.send(UiEvent.ShowSnackBar(exception.message ?: ""))
                }
        }
    }

    fun onFocusChanges() {
        _state.update {
            it.copy(
                isSearchMode = true
            )
        }
    }

    fun onNavBackClick() {
        _state.update {
            it.copy(
                isSearchMode = false,
                searchQuery = "",
                streamersResultSearch = emptyList()
            )
        }
    }

    private fun fetchStreamers(filter: FilterEntity): Job {
        return viewModelScope.launch {
            getStreamers(filter).onSuccess { streamers ->
                    _state.update {
                        it.copy(
                            streamers = streamers,
                            loading = false,
                        )
                    }
                }.onFailure { exception ->
                    _events.send(UiEvent.ShowSnackBar(exception.message ?: ""))

                }
        }
    }
}
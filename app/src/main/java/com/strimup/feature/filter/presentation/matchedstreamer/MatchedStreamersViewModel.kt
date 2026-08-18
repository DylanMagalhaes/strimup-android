package com.strimup.feature.filter.presentation.matchedstreamer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.StreamerMatchResult
import com.strimup.feature.filter.domain.usecase.GetFilterByIdUsecase
import com.strimup.feature.filter.domain.usecase.GetStreamersByFilterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MatchedStreamerListViewModel @Inject constructor(
    private val getMatchedStreamers: GetStreamersByFilterUseCase,
    private val getFilterByIdUsecase: GetFilterByIdUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private var filterId: String = ""
    private var cachedCriteria: FilterCriteria? = null
    private var currentPage = 1
    private var isEndReached = false

    fun initData(id: String) {
        if (filterId.isEmpty()) {
            filterId = id
            loadInitialData()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            getFilterByIdUsecase(filterId)
                .onSuccess { filter ->
                    cachedCriteria = filter.criteria
                    _state.update { it.copy(filterName = filter.name) }

                    fetchStreamersPage(pageNumber = 1)
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
        }
    }

    fun loadNextPage() {
        if (_state.value.isLoading || isEndReached || cachedCriteria == null) {
            return
        }
        currentPage++
        viewModelScope.launch {
            fetchStreamersPage(pageNumber = currentPage)
        }
    }

    fun onLiveSwitch() {
        _state.update { currentState ->
            val originalResult = currentState.originalMatchedResult ?: return@update currentState
            val newIsLiveOnly = !currentState.isLiveOnly

            val filteredStreamers = if (newIsLiveOnly) {
                originalResult.streamers?.filter { it.isLive }
            } else {
                originalResult.streamers
            }

            currentState.copy(
                isLiveOnly = newIsLiveOnly,
                matchedResult = originalResult.copy(
                    streamers = filteredStreamers ?: emptyList(),
                    total = originalResult.total
                )
            )
        }
    }

    private suspend fun fetchStreamersPage(pageNumber: Int) {
        val criteria = cachedCriteria ?: return

        _state.update { it.copy(isLoading = true) }

        getMatchedStreamers(page = pageNumber, filter = criteria)
            .onSuccess { response ->
                val newStreamers = response.streamers.orEmpty()

                if (newStreamers.isEmpty()) {
                    isEndReached = true
                }

                _state.update { currentState ->
                    val currentOriginalStreamers = currentState.originalMatchedResult?.streamers.orEmpty()
                    val updatedOriginalStreamers = if (pageNumber == 1) newStreamers else currentOriginalStreamers + newStreamers

                    val newOriginalResult = StreamerMatchResult(
                        streamers = updatedOriginalStreamers,
                        total = response.total
                    )

                    val displayedStreamers = if (currentState.isLiveOnly) {
                        updatedOriginalStreamers.filter { it.isLive }
                    } else {
                        updatedOriginalStreamers
                    }

                    currentState.copy(
                        isLoading = false,
                        originalMatchedResult = newOriginalResult,
                        matchedResult = StreamerMatchResult(
                            streamers = displayedStreamers,
                            total = response.total
                        )
                    )
                }
            }
            .onFailure { throwable ->
                _state.update {
                    it.copy(isLoading = false, errorMessage = throwable.message)
                }
            }
    }
}
package com.strimup.feature.filter.presentation.matchedstreamer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.feature.filter.domain.entity.FilterCriteria
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

    private val _state = MutableStateFlow<MatchedStreamersUiState>(MatchedStreamersUiState.Loading)
    val state = _state.asStateFlow()

    private var filterId: String = ""
    private var filterName: String? = null
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
            _state.value = MatchedStreamersUiState.Loading

            getFilterByIdUsecase(filterId)
                .onSuccess { filter ->
                    cachedCriteria = filter.criteria
                    filterName = filter.name
                    fetchStreamersPage(pageNumber = 1)
                }
                .onFailure { throwable ->
                    _state.value = MatchedStreamersUiState.Error(
                        errorMessage = throwable.message ?: "Erreur de chargement du filtre"
                    )
                }
        }
    }

    fun loadNextPage() {
        val currentState = _state.value as? MatchedStreamersUiState.Success ?: return

        if (currentState.isLoadingNextPage || isEndReached || cachedCriteria == null) {
            return
        }

        currentPage++
        viewModelScope.launch {
            fetchStreamersPage(pageNumber = currentPage)
        }
    }

    fun onLiveSwitch() {
        _state.update { currentState ->
            if (currentState !is MatchedStreamersUiState.Success) return@update currentState

            val newIsLiveOnly = !currentState.isLiveOnly
            val originalStreamers = currentState.originalMatchedResult.streamers.orEmpty()

            val filteredStreamers = if (newIsLiveOnly) {
                originalStreamers.filter { it.isLive }
            } else {
                originalStreamers
            }

            currentState.copy(
                isLiveOnly = newIsLiveOnly,
                matchedResult = currentState.matchedResult.copy(
                    streamers = filteredStreamers
                )
            )
        }
    }

    private suspend fun fetchStreamersPage(pageNumber: Int) {
        val criteria = cachedCriteria ?: return

        if (pageNumber > 1) {
            _state.update { currentState ->
                if (currentState is MatchedStreamersUiState.Success) {
                    currentState.copy(isLoadingNextPage = true)
                } else currentState
            }
        }

        getMatchedStreamers(page = pageNumber, filter = criteria)
            .onSuccess { response ->
                val newStreamers = response.streamers.orEmpty()

                if (newStreamers.isEmpty()) {
                    isEndReached = true
                }

                _state.update { currentState ->
                    val previousSuccessState = currentState as? MatchedStreamersUiState.Success

                    val currentOriginalStreamers = previousSuccessState?.originalMatchedResult?.streamers.orEmpty()
                    val updatedOriginalStreamers = if (pageNumber == 1) newStreamers else currentOriginalStreamers + newStreamers

                    val isLiveOnly = previousSuccessState?.isLiveOnly ?: false

                    val displayedStreamers = if (isLiveOnly) {
                        updatedOriginalStreamers.filter { it.isLive }
                    } else {
                        updatedOriginalStreamers
                    }

                    val newOriginalResult = StreamerMatchResult(
                        streamers = updatedOriginalStreamers,
                        total = response.total
                    )

                    MatchedStreamersUiState.Success(
                        filterName = filterName,
                        matchedResult = StreamerMatchResult(
                            streamers = displayedStreamers,
                            total = response.total
                        ),
                        originalMatchedResult = newOriginalResult,
                        isLiveOnly = isLiveOnly,
                        isLoadingNextPage = false
                    )
                }
            }
            .onFailure { throwable ->
                _state.update { currentState ->
                    if (pageNumber == 1) {
                        MatchedStreamersUiState.Error(
                            errorMessage = throwable.message ?: "Une erreur est survenue"
                        )
                    } else if (currentState is MatchedStreamersUiState.Success) {
                        currentState.copy(isLoadingNextPage = false)
                    } else {
                        currentState
                    }
                }
            }
    }
}
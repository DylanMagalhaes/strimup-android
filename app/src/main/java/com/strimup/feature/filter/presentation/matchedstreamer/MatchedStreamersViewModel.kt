package com.strimup.feature.filter.presentation.matchedstreamer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.filter.domain.usecase.GetFilterByIdUsecase
import com.strimup.feature.filter.domain.usecase.GetStreamersByFilterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "MatchedStreamerVM"

@HiltViewModel
class MatchedStreamerListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMatchedStreamers: GetStreamersByFilterUseCase,
    private val getFilterByIdUsecase: GetFilterByIdUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private var filterId: String = ""
    private var currentPage = 1
    private var isEndReached = false

    fun initData(id: String) {
        Log.d(TAG, "initData() appelé avec id = '$id' (filterId actuel = '$filterId')")
        if (filterId.isEmpty()) {
            filterId = id
            loadData(pageNumber = 1)
        }
    }

    fun loadNextPage() {
        Log.d(TAG, "loadNextPage() appelé : currentPage = $currentPage, loading = ${_state.value.loading}, isEndReached = $isEndReached")
        if (_state.value.loading || isEndReached) {
            Log.w(TAG, "loadNextPage() ignoré (loading = ${_state.value.loading}, isEndReached = $isEndReached)")
            return
        }
        currentPage++
        loadData(pageNumber = currentPage)
    }

    private fun loadData(pageNumber: Int) {
        Log.d(TAG, "loadData() lancé pour filterId = '$filterId', page = $pageNumber")
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }

            getFilterByIdUsecase(filterId)
                .onSuccess { filter ->
                    Log.d(TAG, "getFilterByIdUsecase Succès : critères = ${filter.criteria}")
                    getMatchedStreamers(
                        page = pageNumber,
                        filter = filter.criteria
                    ).onSuccess { response ->
                        val newStreamers = response.streamers
                        Log.d(TAG, "getMatchedStreamers Succès : ${newStreamers.size} streamers reçus")

                        if (newStreamers.isEmpty()) {
                            Log.d(TAG, "Aucun streamer supplémentaire, isEndReached passe à true")
                            isEndReached = true
                        }

                        _state.update { currentState ->
                            val updatedList = if (pageNumber == 1) newStreamers else currentState.streamers + newStreamers
                            Log.d(TAG, "Mise à jour UiState : total streamers = ${updatedList.size}")
                            currentState.copy(
                                loading = false,
                                streamers = updatedList
                            )
                        }
                    }.onFailure { throwable ->
                        Log.e(TAG, "getMatchedStreamers Échec : ${throwable.message}", throwable)
                        _state.update {
                            it.copy(loading = false, errorMessage = throwable.message)
                        }
                    }
                }
                .onFailure { throwable ->
                    Log.e(TAG, "getFilterByIdUsecase Échec : ${throwable.message}", throwable)
                    _state.update {
                        it.copy(loading = false, errorMessage = throwable.message)
                    }
                }
        }
    }
}
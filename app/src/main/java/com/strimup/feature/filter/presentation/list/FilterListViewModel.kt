package com.strimup.feature.filter.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.filter.domain.usecase.DeleteFilterUsecase
import com.strimup.feature.filter.domain.usecase.GetFiltersUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FilterListViewModel @Inject constructor(
    private val getFilters: GetFiltersUsecase, private val deleteFilter: DeleteFilterUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadFilters()
    }

    fun loadFilters(): Job {
        return viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getFilters().onSuccess { filters ->
                _state.update {
                    it.copy(
                        isLoading = false, filters = filters
                    )
                }
            }.onFailure {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    fun onDeleteButtonClick(id: String): Job {
        return viewModelScope.launch {
            val previousFilters = _state.value.filters

            _state.update { currentState ->
                currentState.copy(
                    filters = currentState.filters.filterNot { it.id == id })
            }

            deleteFilter(id).onFailure { error ->
                _state.update { currentState ->
                    currentState.copy(
                        filters = previousFilters,
                        errorMessage = error.localizedMessage ?: "Impossible de supprimer le filtre"
                    )
                }
            }
        }
    }

}
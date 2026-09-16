package com.strimup.feature.filter.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.R
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.filter.domain.usecase.DeleteFilterUseCase
import com.strimup.feature.filter.domain.usecase.ObserveFiltersUseCase
import com.strimup.feature.filter.domain.usecase.RefreshFiltersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterListViewModel @Inject constructor(
    private val observeFilters: ObserveFiltersUseCase,
    private val refreshFilters: RefreshFiltersUseCase,
    private val deleteFilter: DeleteFilterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FilterListUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<FilterListUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        observeFiltersState()
        refresh()
    }

    private fun observeFiltersState() {
        viewModelScope.launch {
            observeFilters().collect { filters ->
                _state.update { it.copy(filters = filters, isLoading = false) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            refreshFilters()
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    val messageRes = exception.toDomainError().toMessageRes()
                    _events.send(FilterListUiEvent.ShowSnackBar(messageRes))
                }
        }
    }

    fun onDeleteButtonClick(id: String) {
        viewModelScope.launch {
            val previousFilters = _state.value.filters

            _state.update { currentState ->
                currentState.copy(
                    filters = currentState.filters.filterNot { it.id == id }
                )
            }

            deleteFilter(id)
                .onSuccess {
                    _events.send(FilterListUiEvent.ShowSnackBar(R.string.filter_deleted_success))

                }
                .onFailure { error ->
                _state.update { currentState ->
                    currentState.copy(filters = previousFilters)
                }
                val messageRes = error.toDomainError().toMessageRes()
                _events.send(FilterListUiEvent.ShowSnackBar(messageRes))
            }
        }
    }
}
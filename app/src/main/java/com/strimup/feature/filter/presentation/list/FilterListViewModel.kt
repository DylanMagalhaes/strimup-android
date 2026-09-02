package com.strimup.feature.filter.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.filter.domain.usecase.DeleteFilterUseCase
import com.strimup.feature.filter.domain.usecase.GetFiltersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FilterListViewModel @Inject constructor(
    private val getFilters: GetFiltersUseCase,
    private val deleteFilter: DeleteFilterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FilterListUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<FilterListUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadFilters()
    }

    fun loadFilters() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getFilters()
                .onSuccess { filters ->
                    _state.update {
                        it.copy(
                            filters = filters,
                            isLoading = false,
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                    val message = exception.localizedMessage ?: "Erreur lors de la récupération des filtres"
                    _events.send(FilterListUiEvent.ShowSnackBar(message))
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
                    _events.send(FilterListUiEvent.ShowSnackBar("votre filtre a bien été supprimé"))

                }
                .onFailure { error ->
                _state.update { currentState ->
                    currentState.copy(filters = previousFilters)
                }
                val message = error.localizedMessage ?: "Impossible de supprimer le filtre"
                _events.send(FilterListUiEvent.ShowSnackBar(message))
            }
        }
    }
}
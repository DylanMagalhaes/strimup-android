package com.strimup.feature.filter.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.filter.domain.usecase.GetFiltersUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getFilters: GetFiltersUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadFilters()
    }

    private fun loadFilters(): Job {
        return viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getFilters()
                .onSuccess { filters ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            filters = filters
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }
}
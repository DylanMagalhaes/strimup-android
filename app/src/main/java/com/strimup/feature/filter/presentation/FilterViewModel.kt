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

private const val TAG = "FilterViewModel"

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getFilters: GetFiltersUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        Log.d(TAG, "init: Initialisation du FilterViewModel")
        loadFilters()
    }

    private fun loadFilters(): Job {
        return viewModelScope.launch {
            Log.d(TAG, "loadFilters: Début de la récupération des filtres")

            _state.update { it.copy(isLoading = true) }

            getFilters()
                .onSuccess { filters ->
                    Log.d(TAG, "loadFilters SUCCESS: ${filters.size} filtre(s) trouvé(s)")
                    filters.forEachIndexed { index, filter ->
                        Log.d(TAG, " -> [$index] id=${filter.id} | name=${filter.name}")
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            filters = filters
                        )
                    }
                }
                .onFailure { throwable ->
                    Log.e(TAG, "loadFilters ERROR: Échec lors de la récupération", throwable)

                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
        }
    }
}
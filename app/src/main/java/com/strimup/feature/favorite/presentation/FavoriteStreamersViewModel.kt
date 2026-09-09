package com.strimup.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.favorite.domain.usecase.DefaultGetFavoriteStreamersUseCase
import com.strimup.core.favorite.domain.usecase.GetFavoriteStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoriteStreamersViewModel @Inject constructor(
    private val getFavoriteStreamers: GetFavoriteStreamerUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteStreamersUiState())
    val state = _state.asStateFlow()

    init {
        fetchFavoriteStreamers()
    }

    fun fetchFavoriteStreamers() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getFavoriteStreamers()
                .onSuccess { streamers ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            favoriteStreamers = streamers
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    fun onSearchQueryChange(query: String){
        _state.update {
            it.copy(
            searchQuery = query,
        ) }
    }

}
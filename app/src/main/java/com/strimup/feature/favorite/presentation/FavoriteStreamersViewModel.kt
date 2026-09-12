package com.strimup.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.favorite.domain.usecase.ObserveFavoritesStreamersUseCase
import com.strimup.core.favorite.domain.usecase.RefreshFavoriteStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoriteStreamersViewModel @Inject constructor(
    private val refreshFavoriteStreamers: RefreshFavoriteStreamerUseCase,
    private val observeFavoritesStreamers: ObserveFavoritesStreamersUseCase,

    ) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteStreamersUiState())
    val state = _state.asStateFlow()

    init {
        observeFavorites()
        refreshFavorite()
    }

    private fun observeFavorites() {
        _state.update {
            it.copy(
                isLoading = true,
            )
        }
        viewModelScope.launch {
            observeFavoritesStreamers()
                .collect { streamers ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        favoriteStreamers = streamers
                    )
                }
            }
        }
    }

    private fun refreshFavorite() {
        viewModelScope.launch {
            _state.update {
                it.copy(isRefreshing = true)
            }

            refreshFavoriteStreamers()
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                        )
                    }
                }

            _state.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update {
            it.copy(
                searchQuery = query,
            )
        }
    }

}
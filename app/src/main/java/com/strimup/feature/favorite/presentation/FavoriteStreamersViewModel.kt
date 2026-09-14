package com.strimup.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.favorite.domain.usecase.ObserveFavoritesStreamersUseCase
import com.strimup.core.favorite.domain.usecase.RefreshFavoriteStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteStreamersViewModel @Inject constructor(
    private val refreshFavoriteStreamers: RefreshFavoriteStreamerUseCase,
    private val observeFavoritesStreamers: ObserveFavoritesStreamersUseCase,

    ) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteStreamersUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<FavoriteStreamersUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

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
                    val message = exception.localizedMessage ?: "Impossible d'actualiser vos favoris"
                    _events.send(FavoriteStreamersUiEvent.ShowSnackBar(message))
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
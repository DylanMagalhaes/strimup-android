package com.strimup.feature.streamerdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.favorite.domain.usecase.DeleteStreamerFromFavoritesUseCase
import com.strimup.core.favorite.domain.usecase.AddStreamerToFavoritesUseCase
import com.strimup.feature.streamerdetail.domain.usecase.GetStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StreamerDetailViewModel @Inject constructor(
    private val getStreamer: GetStreamerUseCase,
    private val addStreamerToFavorites: AddStreamerToFavoritesUseCase,
    private val deleteStreamerFromFavorites: DeleteStreamerFromFavoritesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<StreamerDetailUiState>(StreamerDetailUiState.Loading)
    val state: StateFlow<StreamerDetailUiState> = _state.asStateFlow()

    fun loadStreamer(id: String) {
        viewModelScope.launch {
            _state.value = StreamerDetailUiState.Loading

            getStreamer(id)
                .onSuccess { streamerDetails ->
                    _state.value = StreamerDetailUiState.Success(
                        streamer = streamerDetails.streamer,
                        isFavorite = streamerDetails.isFavorite
                    )
                }
                .onFailure { exception ->
                    _state.value = StreamerDetailUiState.Error(
                        message = exception.localizedMessage ?: "Impossible de charger le streamer"
                    )
                }
        }
    }

    fun onFavoriteClick() {
        val currentState = _state.value
        if (currentState is StreamerDetailUiState.Success) {
            val currentStreamer = currentState.streamer
            val previousFavoriteState = currentState.isFavorite

            val currentFollowers = currentStreamer.followersCount ?: 0
            val targetFollowers = if (previousFavoriteState) {
                (currentFollowers - 1).coerceAtLeast(0)
            } else {
                currentFollowers + 1
            }

            _state.value = currentState.copy(
                streamer = currentStreamer.copy(followersCount = targetFollowers),
                isFavorite = !previousFavoriteState
            )

            viewModelScope.launch {
                val result = if (previousFavoriteState) {
                    deleteStreamerFromFavorites(currentStreamer.id)
                } else {
                    addStreamerToFavorites(currentStreamer.id)
                }

                result.onFailure {
                    _state.value = currentState.copy(
                        streamer = currentStreamer,
                        isFavorite = previousFavoriteState
                    )
                }
            }
        }
    }
}
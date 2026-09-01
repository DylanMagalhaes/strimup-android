package com.strimup.feature.favorite.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.favorite.domain.usecase.AddFavoriteStreamerUsecase
import com.strimup.feature.favorite.domain.usecase.DeleteFavoriteStreamerUsecase
import com.strimup.feature.favorite.domain.usecase.GetFavoriteStreamersUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavoriteStreamersViewModel @Inject constructor(
    private val getFavoriteStreamersUsecase: GetFavoriteStreamersUsecase,
    private val addFavoriteStreamerUsecase: AddFavoriteStreamerUsecase,
    private val deleteFavoriteStreamerUsecase: DeleteFavoriteStreamerUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoriteStreamersUiState())
    val state = _state.asStateFlow()

    init {
        fetchFavoriteStreamers()
    }

    fun fetchFavoriteStreamers() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getFavoriteStreamersUsecase()
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
package com.strimup.feature.streamerprofile.presentation.streamerprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StreamerProfileViewModel @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val getStreamer: GetStreamerUsecase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state = _state.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            getUser().collect { user ->
                val id = user?.id
                if (!id.isNullOrBlank()) {
                    loadStreamer(id)
                } else {
                    _state.value = ProfileUiState.Error(
                        errorMessage = "Utilisateur non connecté"
                    )
                }
            }
        }
    }

    private fun loadStreamer(id: String) {
        viewModelScope.launch {
            currentUserId = id
            getStreamer(id)
                .onSuccess { streamer ->
                    _state.value = ProfileUiState.Success(streamer = streamer)
                }
                .onFailure { throwable ->
                    _state.value = ProfileUiState.Error(
                        errorMessage = throwable.message ?: "Erreur de chargement du profil"
                    )
                }
        }
    }

    fun refresh() {
        currentUserId?.let { id ->
            _state.value = ProfileUiState.Loading
            loadStreamer(id)
        }
    }
}
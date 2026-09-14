package com.strimup.feature.streamerprofile.presentation.streamerprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamerProfileViewModel @Inject constructor(
    private val getUser: GetUserFlowUseCase,
    private val getStreamer: GetStreamerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val state = _state.asStateFlow()

    private val _events = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

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

    private fun loadStreamer(id: String, previousState: ProfileUiState? = null) {
        viewModelScope.launch {
            currentUserId = id
            getStreamer(id)
                .onSuccess { streamer ->
                    _state.value = ProfileUiState.Success(streamer = streamer)
                }
                .onFailure { throwable ->
                    val message = throwable.message ?: "Erreur de chargement du profil"
                    if (previousState is ProfileUiState.Success) {
                        _state.value = previousState
                        _events.send(ProfileUiEvent.ShowSnackBar(message))
                    } else {
                        _state.value = ProfileUiState.Error(errorMessage = message)
                    }
                }
        }
    }

    fun refresh() {
        currentUserId?.let { id ->
            val previousState = _state.value
            _state.value = ProfileUiState.Loading
            loadStreamer(id, previousState = previousState)
        }
    }
}

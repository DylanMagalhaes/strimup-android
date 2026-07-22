package com.strimup.feature.streamerprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.common.user.domain.usecase.GetUserFlowUseCase
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

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            getUser().collect { user ->
                val id = user?.id
                if (!id.isNullOrBlank()) {
                    loadStreamer(id)
                } else {
                    _state.update { it.copy(loading = false) }
                }
            }
        }
    }

    private fun loadStreamer(id: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loading = true
                )
            }
            currentUserId = id

            getStreamer(id)
                .onSuccess { streamer ->
                    _state.update {
                        it.copy(
                            streamer = streamer,
                            loading = false
                        )
                    }
                }
        }
    }

    fun refresh() {
        currentUserId?.let { id ->
            loadStreamer(id)
        }
    }

}
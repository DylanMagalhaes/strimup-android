package com.strimup.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.profile.domain.usecase.GetStreamerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class StreamerProfileViewModel @Inject constructor(
    private val getStreamer: GetStreamerUsecase,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val streamer = getStreamer("39f1159e-9259-4e38-b72c-57d6792a6995")

            _state.update {
                it.copy(
                    loading = false,
                    streamer = streamer,
                )
            }

        }

    }

}
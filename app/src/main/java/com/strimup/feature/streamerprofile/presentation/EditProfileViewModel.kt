package com.strimup.feature.streamerprofile.presentation

import androidx.lifecycle.ViewModel
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUsecase
import com.strimup.feature.streamerprofile.domain.usecase.UpdateProfileUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getStreamer: GetStreamerUsecase,
    private val updateProfile: UpdateProfileUsecase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.loading)
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

}
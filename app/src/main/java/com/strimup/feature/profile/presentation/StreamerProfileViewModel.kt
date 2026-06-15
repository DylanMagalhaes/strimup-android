package com.strimup.feature.profile.presentation

import androidx.lifecycle.ViewModel
import com.strimup.feature.profile.domain.StreamerRepository
import javax.inject.Inject

class StreamerProfileViewModel @Inject constructor(
    private val repository: StreamerRepository,
) : ViewModel() {
    init {
        repository.getStreamerById()
    }
}
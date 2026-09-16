package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.GetBannerUseCase
import com.strimup.feature.home.domain.usecase.GetStreamersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStreamers: GetStreamersUseCase,
    private val getBannerItems: GetBannerUseCase
) : ViewModel() {

    val state: StateFlow<HomeUiState>
        field = MutableStateFlow(HomeUiState())

    private val _events = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var fetchStreamersJob: Job? = null
    private var fetchBannerJob: Job? = null

    init {
        fetchBannerJob = loadBanner()
        fetchStreamersJob = fetchStreamers(state.value.currentTab)
    }

    private fun loadBanner(): Job {
        state.update {
            it.copy(
                isBannerLoading = true,
                errorMessageRes = null
            )
        }

        return viewModelScope.launch {
            getBannerItems()
                .onSuccess { bannerItems ->
                    state.update {
                        it.copy(
                            isBannerLoading = false,
                            bannerItems = bannerItems
                        )
                    }
                }
                .onFailure { exception ->
                    val messageRes = exception.toDomainError().toMessageRes()
                    state.update {
                        it.copy(
                            isBannerLoading = false,
                            errorMessageRes = messageRes,
                        )
                    }
                }
        }
    }

    fun onTabClick(filter: FilterEntity) {
        if (state.value.currentTab == filter && !state.value.isLoading) return

        fetchStreamersJob?.cancel()

        state.update {
            it.copy(
                isLoading = true,
                currentTab = filter,
                errorMessageRes = null,
            )
        }

        fetchStreamersJob = fetchStreamers(filter)
    }

    private fun fetchStreamers(filter: FilterEntity): Job {
        return viewModelScope.launch {
            getStreamers(filter)
                .onSuccess { streamers ->
                    state.update {
                        it.copy(
                            streamers = streamers,
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
                .onFailure { exception ->
                    val messageRes = exception.toDomainError().toMessageRes()
                    state.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = messageRes,
                        )
                    }
                    _events.send(HomeUiEvent.ShowSnackBar(messageRes))
                }
        }
    }
}
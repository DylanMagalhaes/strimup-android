package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.GetBannerItemsUseCase
import com.strimup.feature.home.domain.usecase.GetStreamersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStreamers: GetStreamersUseCase,
    private val getBannerItems: GetBannerItemsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var fetchStreamersJob: Job? = null
    private var fetchBannerJob: Job? = null

    init {
        fetchBannerJob = loadBanner()
        fetchStreamersJob = fetchStreamers(_state.value.currentTab)
    }

    fun retryBanner() {
        fetchBannerJob?.cancel()
        fetchBannerJob = loadBanner()
    }

    private fun loadBanner(): Job {
        _state.update {
            it.copy(
                isBannerLoading = true,
                errorMessage = null
            )
        }

        return viewModelScope.launch {
            getBannerItems()
                .onSuccess { bannerItems ->
                    _state.update {
                        it.copy(
                            isBannerLoading = false,
                            bannerItems = bannerItems
                        )
                    }
                }
                .onFailure { exception ->
                    val message = exception.localizedMessage ?: "Une erreur est survenue"
                    _state.update {
                        it.copy(
                            isBannerLoading = false,
                            errorMessage = message,
                        )
                    }
                }
        }
    }

    fun onTabClick(filter: FilterEntity) {
        if (_state.value.currentTab == filter && !_state.value.isLoading) return

        fetchStreamersJob?.cancel()

        _state.update {
            it.copy(
                isLoading = true,
                currentTab = filter,
                errorMessage = null,
            )
        }

        fetchStreamersJob = fetchStreamers(filter)
    }

    private fun fetchStreamers(filter: FilterEntity): Job {
        return viewModelScope.launch {
            getStreamers(filter)
                .onSuccess { streamers ->
                    _state.update {
                        it.copy(
                            streamers = streamers,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { exception ->
                    val message = exception.localizedMessage ?: "Une erreur est survenue"
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = message,
                        )
                    }
                    _events.send(HomeUiEvent.ShowSnackBar(message))
                }
        }
    }
}
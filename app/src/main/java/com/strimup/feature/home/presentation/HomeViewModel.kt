package com.strimup.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.favorite.domain.usecase.ObserveFavoritesStreamersUseCase
import com.strimup.core.favorite.domain.usecase.ToggleFavoriteStreamerUseCase
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.GetCachedDiscoveryStreamersUseCase
import com.strimup.feature.home.domain.usecase.GetStreamersUseCase
import com.strimup.feature.home.domain.usecase.ObserveBannerUseCase
import com.strimup.feature.home.domain.usecase.RefreshBannerUseCase
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
    private val getCachedDiscoveryStreamers: GetCachedDiscoveryStreamersUseCase,
    private val observeBanner: ObserveBannerUseCase,
    private val refreshBanner: RefreshBannerUseCase,
    private val observeFavorites: ObserveFavoritesStreamersUseCase,
    private val toggleFavoriteStreamer: ToggleFavoriteStreamerUseCase,
) : ViewModel() {

    val state: StateFlow<HomeUiState>
        field = MutableStateFlow(HomeUiState())

    private val _events = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var fetchStreamersJob: Job? = null

    init {
        observeBannerItems()
        observeFavoriteStreamers()
        refreshBannerItems()
        fetchStreamersJob = fetchStreamers(state.value.currentTab)
    }

    fun onTabClick(filter: FilterEntity) {
        if (state.value.currentTab == filter && !state.value.isLoading) return
        reloadStreamers(filter)
    }

    fun onRefresh() {
        if (state.value.isRefreshing || state.value.isLoading) return

        fetchStreamersJob?.cancel()
        state.update { it.copy(isRefreshing = true) }
        refreshBannerItems()

        val filter = state.value.currentTab
        fetchStreamersJob = viewModelScope.launch {
            getStreamers(filter)
                .onSuccess { streamers ->
                    state.update {
                        it.copy(
                            streamers = streamers,
                            isRefreshing = false,
                            errorMessageRes = null,
                            isShowingSavedContent = false,
                        )
                    }
                }
                .onFailure { exception ->
                    val messageRes = exception.toDomainError().toMessageRes()
                    state.update { current ->
                        current.copy(
                            isRefreshing = false,
                            errorMessageRes = messageRes.takeIf { current.streamers.isEmpty() },
                        )
                    }
                    _events.send(HomeUiEvent.ShowSnackBar(messageRes))
                }
        }
    }

    fun onFavoriteClick(streamerId: String) {
        val isFavorite = streamerId in state.value.favoriteStreamerIds
        state.update { it.copy(favoriteStreamerIds = it.favoriteStreamerIds.toggle(streamerId)) }

        viewModelScope.launch {
            toggleFavoriteStreamer(streamerId = streamerId, isFavorite = isFavorite)
                .onFailure { exception ->
                    state.update { it.copy(favoriteStreamerIds = it.favoriteStreamerIds.toggle(streamerId)) }
                    _events.send(HomeUiEvent.ShowSnackBar(exception.toDomainError().toMessageRes()))
                }
        }
    }

    fun onRetryClick() {
        refreshBannerItems()
        reloadStreamers(state.value.currentTab)
    }

    private fun reloadStreamers(filter: FilterEntity) {
        fetchStreamersJob?.cancel()

        state.update {
            it.copy(
                isLoading = true,
                isRefreshing = false,
                currentTab = filter,
                errorMessageRes = null,
                isShowingSavedContent = false,
            )
        }

        fetchStreamersJob = fetchStreamers(filter)
    }

    private fun observeBannerItems() {
        viewModelScope.launch {
            observeBanner().collect { bannerItems ->
                state.update {
                    it.copy(
                        bannerItems = bannerItems,
                        isBannerLoading = it.isBannerLoading && bannerItems.isEmpty(),
                    )
                }
            }
        }
    }

    private fun observeFavoriteStreamers() {
        viewModelScope.launch {
            observeFavorites().collect { favorites ->
                state.update { it.copy(favoriteStreamerIds = favorites.map { favorite -> favorite.id }.toSet()) }
            }
        }
    }

    private fun refreshBannerItems() {
        viewModelScope.launch {
            refreshBanner()
            state.update { it.copy(isBannerLoading = false) }
        }
    }

    private fun fetchStreamers(filter: FilterEntity): Job {
        return viewModelScope.launch {
            val savedStreamers = when (filter) {
                FilterEntity.Discovery -> getCachedDiscoveryStreamers()
                FilterEntity.Live -> emptyList()
            }

            if (savedStreamers.isNotEmpty()) {
                state.update { it.copy(streamers = savedStreamers, isLoading = false) }
            }

            getStreamers(filter)
                .onSuccess { streamers ->
                    state.update {
                        it.copy(
                            streamers = streamers,
                            isLoading = false,
                            errorMessageRes = null,
                            isShowingSavedContent = false,
                        )
                    }
                }
                .onFailure { exception ->
                    if (savedStreamers.isNotEmpty()) {
                        state.update { it.copy(isShowingSavedContent = true) }
                        return@onFailure
                    }

                    val messageRes = exception.toDomainError().toMessageRes()
                    state.update {
                        it.copy(
                            streamers = emptyList(),
                            isLoading = false,
                            errorMessageRes = messageRes,
                            isShowingSavedContent = false,
                        )
                    }
                    _events.send(HomeUiEvent.ShowSnackBar(messageRes))
                }
        }
    }
}

private fun Set<String>.toggle(id: String): Set<String> = if (id in this) this - id else this + id

package com.strimup.feature.cuts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.common.DomainError
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toUiText
import com.strimup.core.ui.text.UiText
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.cuts.R
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.domain.entity.CutUnavailableException
import com.strimup.feature.cuts.domain.entity.LikeRateLimitedException
import com.strimup.feature.cuts.domain.usecase.GetCachedCutUseCase
import com.strimup.feature.cuts.domain.usecase.LikeCutUseCase
import com.strimup.feature.cuts.domain.usecase.MarkCutsOpenedUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

const val CUTS_PREFETCH_THRESHOLD = 3
val LIKE_COOLDOWN = 5.minutes

@HiltViewModel(assistedFactory = CutsViewModel.Factory::class)
class CutsViewModel @AssistedInject constructor(
    @Assisted private val startVideoId: String?,
    getCachedCut: GetCachedCutUseCase,
    private val pager: CutsPager,
    private val likeCut: LikeCutUseCase,
    getUser: GetUserFlowUseCase,
    markCutsOpened: MarkCutsOpenedUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CutsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<CutsUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val pendingLikes = mutableSetOf<String>()
    private var loadJob: Job? = null
    private var nextSlideKey = 0L
    private var isLoggedIn = false
    private var pinnedVideoId: String? = null

    init {
        startVideoId?.let(getCachedCut::invoke)?.let { cut ->
            pinnedVideoId = cut.id
            _state.update { it.copy(slides = listOf(CutSlide(key = nextSlideKey++, cut = cut))) }
        }
        viewModelScope.launch { getUser().collect { user -> isLoggedIn = user != null } }
        viewModelScope.launch { markCutsOpened() }
        loadMore()
    }

    fun onActiveSlideChanged(index: Int) {
        _state.update { it.copy(activeIndex = index) }
        if (_state.value.remainingSlides <= CUTS_PREFETCH_THRESHOLD) loadMore()
    }

    fun onRetryClick() {
        _state.update { it.copy(canLoadMore = true, errorMessage = null) }
        loadMore()
    }

    fun onLikeClick(videoId: String, isDoubleTap: Boolean = false) {
        if (!isLoggedIn) {
            _events.trySend(CutsUiEvent.LoginRequired(R.string.cuts_login_to_like))
            return
        }
        val cut = _state.value.findCut(videoId)?.takeUnless { isDoubleTap && it.isLikedByMe } ?: return
        if (!_state.value.isLikeDisabled && pendingLikes.add(videoId)) {
            sendLike(cut, isLiked = !cut.isLikedByMe)
        }
    }

    fun onMuteClick() {
        _state.update { it.copy(isMuted = !it.isMuted) }
    }

    fun onReportClick(videoId: String) {
        if (!isLoggedIn) {
            _events.trySend(CutsUiEvent.LoginRequired(R.string.cuts_login_to_report))
            return
        }
        _state.update { it.copy(reportedVideoId = videoId) }
    }

    fun onReportDismiss() {
        _state.update { it.copy(reportedVideoId = null) }
    }

    fun onVideoUnavailable(videoId: String) {
        _state.update { it.withoutVideo(videoId) }
        if (_state.value.remainingSlides <= CUTS_PREFETCH_THRESHOLD) loadMore()
    }

    private fun loadMore() {
        val currentState = _state.value
        if (loadJob?.isActive == true || !currentState.canLoadMore) return

        _state.update { it.copy(isLoading = true) }
        loadJob = viewModelScope.launch {
            pager.loadNextPage()
                .onSuccess { cuts ->
                    val newSlides = cuts
                        .filterNot { cut -> cut.id == pinnedVideoId }
                        .map { cut -> CutSlide(key = nextSlideKey++, cut = cut) }
                    pinnedVideoId = null
                    _state.update {
                        it.copy(slides = it.slides + newSlides, isLoading = false, canLoadMore = cuts.isNotEmpty())
                    }
                }
                .onFailure { exception ->
                    val message = exception.toDomainError().toUiText()
                    val hasSlides = _state.value.slides.isNotEmpty()
                    _state.update { it.copy(isLoading = false, errorMessage = message.takeUnless { hasSlides }) }
                    if (hasSlides) _events.send(CutsUiEvent.ShowMessage(message))
                }
        }
    }

    private fun sendLike(cut: Cut, isLiked: Boolean) {
        val optimisticCount = cut.likesCount + if (isLiked) 1 else -1
        _state.update { it.withLike(cut.id, isLiked, optimisticCount) }

        viewModelScope.launch {
            likeCut(cut.id, isLiked)
                .onSuccess { like -> _state.update { it.withLike(cut.id, like.isLiked, like.likesCount) } }
                .onFailure { exception -> onLikeFailure(cut, exception) }
            pendingLikes.remove(cut.id)
        }
    }

    private suspend fun onLikeFailure(cut: Cut, exception: Throwable) {
        if (exception is CutUnavailableException) {
            onVideoUnavailable(cut.id)
            return
        }

        _state.update { it.withLike(cut.id, cut.isLikedByMe, cut.likesCount) }
        when {
            exception is LikeRateLimitedException -> {
                _state.update { it.copy(isLikeDisabled = true) }
                exception.serverMessage?.let { _events.send(CutsUiEvent.ShowMessage(UiText.Dynamic(it))) }
                viewModelScope.launch {
                    delay(LIKE_COOLDOWN)
                    _state.update { it.copy(isLikeDisabled = false) }
                }
            }

            exception.toDomainError() == DomainError.Unauthorized ->
                _events.send(CutsUiEvent.LoginRequired(R.string.cuts_login_to_like))

            else -> _events.send(CutsUiEvent.ShowMessage(exception.toDomainError().toUiText()))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(startVideoId: String?): CutsViewModel
    }
}

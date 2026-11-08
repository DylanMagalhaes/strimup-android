package com.strimup.feature.cuts.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.text.UiText
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.cuts.R
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.domain.entity.CutLike
import com.strimup.feature.cuts.domain.entity.CutUnavailableException
import com.strimup.feature.cuts.domain.entity.CutsPage
import com.strimup.feature.cuts.domain.entity.LikeRateLimitedException
import com.strimup.feature.cuts.domain.usecase.GetCachedCutUseCase
import com.strimup.feature.cuts.domain.usecase.GetCutsUseCase
import com.strimup.feature.cuts.domain.usecase.LikeCutUseCase
import com.strimup.feature.cuts.domain.usecase.MarkCutsOpenedUseCase
import com.strimup.feature.cuts.fake.cut
import com.strimup.feature.cuts.fake.cuts
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CutsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserEntity("me", "Moi", "moi@strimup.com", UserRole.VIEWER, null)
    private val userFlow = MutableStateFlow<UserEntity?>(user)
    private val pages = ArrayDeque<Result<CutsPage>>()
    private val requestedCursors = mutableListOf<String?>()
    private val likeCalls = mutableListOf<Pair<String, Boolean>>()
    private var likeResult: suspend (String, Boolean) -> Result<CutLike> = { _, isLiked ->
        Result.success(CutLike(isLiked = isLiked, likesCount = if (isLiked) 11 else 9))
    }
    private var openedCount = 0

    private val cachedCuts = mutableMapOf<String, Cut>()

    private fun buildViewModel(startVideoId: String? = null) = CutsViewModel(
        startVideoId = startVideoId,
        getCachedCut = GetCachedCutUseCase { videoId -> cachedCuts[videoId] },
        pager = CutsPager(
            getCuts = GetCutsUseCase { cursor ->
                requestedCursors += cursor
                pages.removeFirstOrNull() ?: Result.success(CutsPage(emptyList(), nextCursor = null))
            },
        ),
        likeCut = LikeCutUseCase { videoId, isLiked ->
            likeCalls += videoId to isLiked
            likeResult(videoId, isLiked)
        },
        getUser = GetUserFlowUseCase { userFlow },
        markCutsOpened = MarkCutsOpenedUseCase { openedCount++ },
    )

    private fun page(vararg cuts: Cut, nextCursor: String? = "next") =
        Result.success(CutsPage(cuts.toList(), nextCursor))

    private val CutsViewModel.videoIds get() = state.value.slides.map { it.cut.id }

    private fun CutsViewModel.cutOf(videoId: String) = state.value.slides.first { it.cut.id == videoId }.cut

    @Test
    fun `opening the feed should load the first page and mark Cuts as opened`() = runTest {
        pages += page(*cuts("a", "b").toTypedArray())

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertThat(viewModel.videoIds).containsExactly("a", "b").inOrder()
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(openedCount).isEqualTo(1)
    }

    @Test
    fun `the next page should be loaded when 3 videos or less remain`() = runTest {
        pages += page(*cuts("a", "b", "c", "d", "e", "f").toTypedArray(), nextCursor = "c1")
        pages += page(*cuts("g").toTypedArray())
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onActiveSlideChanged(1)
        advanceUntilIdle()
        assertThat(requestedCursors).containsExactly(null)

        viewModel.onActiveSlideChanged(2)
        advanceUntilIdle()
        assertThat(requestedCursors).containsExactly(null, "c1").inOrder()
        assertThat(viewModel.videoIds.last()).isEqualTo("g")
    }

    @Test
    fun `the end of the feed should start a new shuffle with unique slide keys`() = runTest {
        pages += page(*cuts("a", "b").toTypedArray(), nextCursor = null)
        pages += page(*cuts("b", "a").toTypedArray(), nextCursor = null)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onActiveSlideChanged(1)
        advanceUntilIdle()

        assertThat(requestedCursors).containsExactly(null, null)
        assertThat(viewModel.videoIds).containsExactly("a", "b", "b", "a").inOrder()
        assertThat(viewModel.state.value.slides.map { it.key }.toSet()).hasSize(4)
    }

    @Test
    fun `an empty pool should show the empty state and stop loading`() = runTest {
        pages += page(nextCursor = null)
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onActiveSlideChanged(0)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isEmpty).isTrue()
        assertThat(requestedCursors).hasSize(1)
    }

    @Test
    fun `a first page failure should show a retryable error`() = runTest {
        pages += Result.failure(DomainException(DomainError.Server(403, "Interdit")))
        pages += page(cut("a"))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isEqualTo(UiText.Dynamic("Interdit"))

        viewModel.onRetryClick()
        advanceUntilIdle()

        assertThat(viewModel.state.value.errorMessage).isNull()
        assertThat(viewModel.videoIds).containsExactly("a")
    }

    @Test
    fun `a like should be shown at once then confirmed by the server`() = runTest {
        pages += page(cut("a", likesCount = 10))
        val pending = CompletableDeferred<Result<CutLike>>()
        likeResult = { _, _ -> pending.await() }
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLikeClick("a")
        advanceUntilIdle()
        assertThat(viewModel.cutOf("a").isLikedByMe).isTrue()
        assertThat(viewModel.cutOf("a").likesCount).isEqualTo(11)

        pending.complete(Result.success(CutLike(isLiked = true, likesCount = 43)))
        advanceUntilIdle()
        assertThat(viewModel.cutOf("a").likesCount).isEqualTo(43)
    }

    @Test
    fun `a second like while one is in flight should be ignored`() = runTest {
        pages += page(cut("a"))
        val pending = CompletableDeferred<Result<CutLike>>()
        likeResult = { _, _ -> pending.await() }
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLikeClick("a")
        viewModel.onLikeClick("a")
        advanceUntilIdle()

        assertThat(likeCalls).containsExactly("a" to true)
        pending.complete(Result.success(CutLike(isLiked = true, likesCount = 1)))
    }

    @Test
    fun `tapping like on a liked video should unlike it`() = runTest {
        pages += page(cut("a", likesCount = 10, isLikedByMe = true))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLikeClick("a")
        advanceUntilIdle()

        assertThat(likeCalls).containsExactly("a" to false)
        assertThat(viewModel.cutOf("a").isLikedByMe).isFalse()
    }

    @Test
    fun `a double tap should only like, never unlike`() = runTest {
        pages += page(cut("a", isLikedByMe = true), cut("b"))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLikeClick("a", isDoubleTap = true)
        viewModel.onLikeClick("b", isDoubleTap = true)
        advanceUntilIdle()

        assertThat(likeCalls).containsExactly("b" to true)
    }

    @Test
    fun `a logged out user should be asked to log in without any call`() = runTest {
        userFlow.value = null
        pages += page(cut("a"))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onLikeClick("a")
            assertThat(awaitItem()).isEqualTo(CutsUiEvent.LoginRequired(R.string.cuts_login_to_like))

            viewModel.onReportClick("a")
            assertThat(awaitItem()).isEqualTo(CutsUiEvent.LoginRequired(R.string.cuts_login_to_report))
        }
        assertThat(likeCalls).isEmpty()
        assertThat(viewModel.state.value.reportedVideoId).isNull()
    }

    @Test
    fun `a failed like should be rolled back with a message`() = runTest {
        pages += page(cut("a", likesCount = 10))
        likeResult = { _, _ -> Result.failure(DomainException(DomainError.Server(409, "Action impossible"))) }
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onLikeClick("a")
            assertThat(awaitItem()).isEqualTo(CutsUiEvent.ShowMessage(UiText.Dynamic("Action impossible")))
        }
        assertThat(viewModel.cutOf("a").isLikedByMe).isFalse()
        assertThat(viewModel.cutOf("a").likesCount).isEqualTo(10)
    }

    @Test
    fun `an unavailable video should be removed from the feed`() = runTest {
        pages += page(cut("a"), cut("b"), cut("c"), cut("d"), cut("e"))
        likeResult = { _, _ -> Result.failure(CutUnavailableException()) }
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLikeClick("b")
        advanceUntilIdle()

        assertThat(viewModel.videoIds).containsExactly("a", "c", "d", "e").inOrder()
    }

    @Test
    fun `too many likes should disable the button for a few minutes`() = runTest {
        pages += page(cut("a"), cut("b"))
        likeResult = { _, _ -> Result.failure(LikeRateLimitedException("Trop de likes, réessaie plus tard.")) }
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onLikeClick("a")
        advanceTimeBy(1_000)
        assertThat(viewModel.state.value.isLikeDisabled).isTrue()

        viewModel.onLikeClick("b")
        advanceTimeBy(1_000)
        assertThat(likeCalls).containsExactly("a" to true)

        advanceTimeBy(LIKE_COOLDOWN.inWholeMilliseconds)
        assertThat(viewModel.state.value.isLikeDisabled).isFalse()
    }

    @Test
    fun `an expired session on like should ask to log in`() = runTest {
        pages += page(cut("a", likesCount = 3))
        likeResult = { _, _ -> Result.failure(DomainException(DomainError.Unauthorized)) }
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onLikeClick("a")
            assertThat(awaitItem()).isEqualTo(CutsUiEvent.LoginRequired(R.string.cuts_login_to_like))
        }
        assertThat(viewModel.cutOf("a").likesCount).isEqualTo(3)
    }

    @Test
    fun `reporting should open the form and an unavailable video should be removed`() = runTest {
        pages += page(cut("a"), cut("b"))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onReportClick("b")
        assertThat(viewModel.state.value.reportedVideoId).isEqualTo("b")

        viewModel.onVideoUnavailable("b")
        assertThat(viewModel.state.value.reportedVideoId).isNull()
        assertThat(viewModel.videoIds).containsExactly("a")
    }

    @Test
    fun `the sound should be off by default and toggle`() = runTest {
        val viewModel = buildViewModel()

        assertThat(viewModel.state.value.isMuted).isTrue()
        viewModel.onMuteClick()
        assertThat(viewModel.state.value.isMuted).isFalse()
    }

    @Test
    fun `opening on a known video should show it first without repeating it`() = runTest {
        cachedCuts["b"] = cut("b", likesCount = 7)
        pages += page(cut("a"), cut("b"), cut("c"))
        val viewModel = buildViewModel(startVideoId = "b")

        assertThat(viewModel.videoIds).containsExactly("b")

        advanceUntilIdle()

        assertThat(viewModel.videoIds).containsExactly("b", "a", "c").inOrder()
        assertThat(viewModel.cutOf("b").likesCount).isEqualTo(7)
    }

    @Test
    fun `an unknown start video should open the regular feed`() = runTest {
        pages += page(cut("a"))
        val viewModel = buildViewModel(startVideoId = "missing")
        advanceUntilIdle()

        assertThat(viewModel.videoIds).containsExactly("a")
    }
}

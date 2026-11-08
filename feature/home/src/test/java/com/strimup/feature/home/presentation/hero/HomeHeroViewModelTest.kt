package com.strimup.feature.home.presentation.hero

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.domain.entity.CutsPage
import com.strimup.feature.cuts.domain.usecase.GetCutsUseCase
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.GetStreamersUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeHeroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val requestedFilters = mutableListOf<FilterEntity>()
    private var streamersResult: Result<List<Streamer>> = Result.success(emptyList())
    private var cutsResult: Result<CutsPage> = Result.success(CutsPage(emptyList(), nextCursor = null))

    private fun buildViewModel() = HomeHeroViewModel(
        getStreamers = GetStreamersUseCase { filter ->
            requestedFilters += filter
            streamersResult
        },
        getCuts = GetCutsUseCase { cutsResult },
    )

    private fun liveStreamer(id: String, twitchUrl: String? = "https://www.twitch.tv/$id") = Streamer(
        id = id,
        userName = id,
        imageUrl = null,
        socials = listOfNotNull(twitchUrl?.let { Social(url = it, type = Social.Type.Twitch) }),
        isLive = true,
    )

    private fun cut(id: String) = Cut(
        video = Streamer.Video(id = id, title = id, description = "", url = "https://cdn/$id.mp4", order = 1),
        author = Cut.Author(id = "a", username = "a", avatarUrl = "", isVerified = false, isLive = false),
        likesCount = 0,
        isLikedByMe = false,
    )

    @Test
    fun `live streamers with a Twitch channel should be shown`() = runTest {
        streamersResult = Result.success(
            listOf(liveStreamer("inox"), liveStreamer("noTwitch", twitchUrl = null), liveStreamer("zerator")),
        )

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertThat(requestedFilters).containsExactly(FilterEntity.Live)
        assertThat(viewModel.state.value.lives.map { it.channel }).containsExactly("inox", "zerator")
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `the first cut should be the fallback only when nobody is live`() = runTest {
        cutsResult = Result.success(CutsPage(listOf(cut("a"), cut("b")), nextCursor = "next"))

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.cuts.map { it.id }).containsExactly("a", "b").inOrder()
        assertThat(viewModel.state.value.fallbackCut?.id).isEqualTo("a")

        streamersResult = Result.success(listOf(liveStreamer("inox")))
        viewModel.refresh()
        advanceUntilIdle()

        assertThat(viewModel.state.value.fallbackCut).isNull()
    }

    @Test
    fun `a failed refresh should keep the previous content`() = runTest {
        streamersResult = Result.success(listOf(liveStreamer("inox")))
        cutsResult = Result.success(CutsPage(listOf(cut("a")), nextCursor = null))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        streamersResult = Result.failure(DomainException(DomainError.Network))
        cutsResult = Result.failure(DomainException(DomainError.Network))
        viewModel.refresh()
        advanceUntilIdle()

        assertThat(viewModel.state.value.lives.map { it.channel }).containsExactly("inox")
        assertThat(viewModel.state.value.cuts.map { it.id }).containsExactly("a")
    }

    @Test
    fun `the sound should be off by default and toggle`() = runTest {
        val viewModel = buildViewModel()

        assertThat(viewModel.state.value.isMuted).isTrue()
        viewModel.onMuteClick()
        assertThat(viewModel.state.value.isMuted).isFalse()
    }
}

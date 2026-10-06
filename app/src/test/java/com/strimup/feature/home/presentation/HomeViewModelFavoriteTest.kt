package com.strimup.feature.home.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.favorite.domain.usecase.ToggleFavoriteStreamerUseCase
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelFavoriteTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val favorites = MutableStateFlow<List<Streamer>>(emptyList())

    private fun homeViewModel(
        toggleFavoriteStreamer: ToggleFavoriteStreamerUseCase = ToggleFavoriteStreamerUseCase { _, _ ->
            Result.success(Unit)
        },
    ) = HomeViewModel(
        getStreamers = { Result.success(emptyList()) },
        getCachedDiscoveryStreamers = { emptyList() },
        observeBanner = { flowOf(emptyList()) },
        refreshBanner = { Result.success(Unit) },
        observeFavorites = { favorites },
        toggleFavoriteStreamer = toggleFavoriteStreamer,
    )

    @Test
    fun `favorite ids should follow the saved favorites`() = runTest {
        val viewModel = homeViewModel()
        advanceUntilIdle()

        favorites.value = listOf(Streamer(id = "1", userName = "Inox", imageUrl = null))
        advanceUntilIdle()

        assertThat(viewModel.state.value.favoriteStreamerIds).containsExactly("1")
    }

    @Test
    fun `favorite click should mark the streamer as favorite before the network answers`() = runTest {
        val network = CompletableDeferred<Result<Unit>>()
        var receivedIsFavorite: Boolean? = null
        val viewModel = homeViewModel(
            toggleFavoriteStreamer = { _, isFavorite ->
                receivedIsFavorite = isFavorite
                network.await()
            },
        )
        advanceUntilIdle()

        viewModel.onFavoriteClick("1")
        runCurrent()

        assertThat(viewModel.state.value.favoriteStreamerIds).containsExactly("1")
        assertThat(receivedIsFavorite).isFalse()
        network.complete(Result.success(Unit))
    }

    @Test
    fun `favorite click on a favorite should remove it`() = runTest {
        var receivedIsFavorite: Boolean? = null
        favorites.value = listOf(Streamer(id = "1", userName = "Inox", imageUrl = null))
        val viewModel = homeViewModel(
            toggleFavoriteStreamer = { _, isFavorite ->
                receivedIsFavorite = isFavorite
                Result.success(Unit)
            },
        )
        advanceUntilIdle()

        viewModel.onFavoriteClick("1")
        advanceUntilIdle()

        assertThat(viewModel.state.value.favoriteStreamerIds).isEmpty()
        assertThat(receivedIsFavorite).isTrue()
    }

    @Test
    fun `favorite click failure should restore the previous state and show a snackbar`() = runTest {
        val viewModel = homeViewModel(
            toggleFavoriteStreamer = { _, _ -> Result.failure(DomainException(DomainError.Network)) },
        )
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onFavoriteClick("1")
            advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(HomeUiEvent.ShowSnackBar::class.java)
            assertThat(viewModel.state.value.favoriteStreamerIds).isEmpty()
        }
    }
}

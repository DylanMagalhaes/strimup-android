package com.strimup.feature.favorite.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.favorite.domain.usecase.ObserveFavoritesStreamersUseCase
import com.strimup.core.favorite.domain.usecase.RefreshFavoriteStreamerUseCase
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteStreamersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeStreamers = listOf(
        Streamer(id = "1", userName = "inox", imageUrl = ""),
        Streamer(id = "2", userName = "gotaga", imageUrl = "")
    )

    private fun observeUseCase(flow: Flow<List<Streamer>>) =
        object : ObserveFavoritesStreamersUseCase {
            override fun invoke(): Flow<List<Streamer>> = flow
        }

    private fun buildViewModel(
        refreshFavoriteStreamers: RefreshFavoriteStreamerUseCase =
            RefreshFavoriteStreamerUseCase { Result.success(Unit) },
        observeFavoritesStreamers: ObserveFavoritesStreamersUseCase =
            observeUseCase(flowOf(emptyList())),
    ) = FavoriteStreamersViewModel(
        refreshFavoriteStreamers = refreshFavoriteStreamers,
        observeFavoritesStreamers = observeFavoritesStreamers,
    )

    @Test
    fun `init should observe favorites and update state with emitted streamers`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            observeFavoritesStreamers = observeUseCase(flowOf(fakeStreamers)),
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.favoriteStreamers).isEqualTo(fakeStreamers)
    }

    @Test
    fun `init should refresh favorites then set isRefreshing to false on success`() = runTest {
        // GIVEN
        var refreshCalled = false
        val viewModel = buildViewModel(
            refreshFavoriteStreamers = RefreshFavoriteStreamerUseCase {
                refreshCalled = true
                Result.success(Unit)
            },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        assertThat(refreshCalled).isTrue()
        assertThat(viewModel.state.value.isRefreshing).isFalse()
    }

    @Test
    fun `init when refresh fails should set isRefreshing to false without clearing favoriteStreamers`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            refreshFavoriteStreamers = RefreshFavoriteStreamerUseCase {
                Result.failure(Exception("peu importe"))
            },
            observeFavoritesStreamers = observeUseCase(flowOf(fakeStreamers)),
        )

        // WHEN & THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem() as FavoriteStreamersUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_unknown)
        }
        val state = viewModel.state.value
        assertThat(state.isRefreshing).isFalse()
        assertThat(state.favoriteStreamers).isEqualTo(fakeStreamers)
    }

    @Test
    fun `init when refresh fails with a DomainException should emit its own category`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            refreshFavoriteStreamers = RefreshFavoriteStreamerUseCase {
                Result.failure(DomainException(DomainError.Network))
            },
        )

        // WHEN & THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem() as FavoriteStreamersUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_network)
        }
    }

    @Test
    fun `favoriteStreamers should update reactively when the observed flow emits again`() = runTest {
        // GIVEN
        val favoritesFlow = MutableStateFlow(listOf(fakeStreamers[0]))
        val viewModel = buildViewModel(
            observeFavoritesStreamers = observeUseCase(favoritesFlow),
        )
        advanceUntilIdle()
        assertThat(viewModel.state.value.favoriteStreamers).containsExactly(fakeStreamers[0])

        // WHEN
        favoritesFlow.value = fakeStreamers
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.favoriteStreamers).isEqualTo(fakeStreamers)
    }

    @Test
    fun `onSearchQueryChange should update state searchQuery`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN
        viewModel.onSearchQueryChange("inox")

        // THEN
        assertThat(viewModel.state.value.searchQuery).isEqualTo("inox")
    }
}

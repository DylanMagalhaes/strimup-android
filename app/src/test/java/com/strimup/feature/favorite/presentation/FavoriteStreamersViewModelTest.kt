package com.strimup.feature.favorite.presentation

import com.google.common.truth.Truth.assertThat
import com.strimup.core.favorite.domain.usecase.GetFavoriteStreamerUseCase
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoriteStreamersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `init should fetch favorite streamers and update state on success`() = runTest {
        // GIVEN
        val fakeStreamers = listOf(
            Streamer(id = "1", userName = "inox", imageUrl = ""),
            Streamer(id = "2", userName = "gotaga", imageUrl = "")
        )

        // WHEN
        val viewModel = FavoriteStreamersViewModel(
            getFavoriteStreamers = GetFavoriteStreamerUseCase { Result.success(fakeStreamers) }

        )

        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.favoriteStreamers).isEqualTo(fakeStreamers)
    }

    @Test
    fun `fetchFavoriteStreamers when use case fails should set isLoading to false`() = runTest {
        // GIVEN
        val viewModel = FavoriteStreamersViewModel(
            getFavoriteStreamers = GetFavoriteStreamerUseCase { Result.failure(Exception("Erreur réseau")) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.favoriteStreamers).isEmpty()
    }

    @Test
    fun `onSearchQueryChange should update state searchQuery`() = runTest {
        // GIVEN
        val viewModel = FavoriteStreamersViewModel(
            getFavoriteStreamers = GetFavoriteStreamerUseCase { Result.success(emptyList()) },
        )
        advanceUntilIdle()

        // WHEN
        viewModel.onSearchQueryChange("inox")

        // THEN
        assertThat(viewModel.state.value.searchQuery).isEqualTo("inox")
    }
}
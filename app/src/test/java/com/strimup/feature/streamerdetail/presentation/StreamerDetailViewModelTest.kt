package com.strimup.feature.streamerdetail.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.favorite.domain.usecase.AddStreamerToFavoritesUseCase
import com.strimup.core.favorite.domain.usecase.DeleteStreamerFromFavoritesUseCase
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamerdetail.domain.usecase.GetStreamerUseCase
import com.strimup.feature.streamerdetail.domain.usecase.StreamerDetailResult
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StreamerDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeStreamer = Streamer(
        id = "12",
        userName = "inox",
        imageUrl = "",
        followersCount = 10
    )

    @Test
    fun `loadStreamer when use case succeeds should emit Success state with streamer details`() = runTest {
        // GIVEN
        val streamerResult = StreamerDetailResult(
            streamer = fakeStreamer,
            isFavorite = false
        )

        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.success(streamerResult) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase { Result.success(Unit) },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase { Result.success(Unit) }
        )

        // WHEN
        viewModel.loadStreamer("12")
        advanceUntilIdle()

        // THEN
        val expectedState = StreamerDetailUiState.Success(
            streamer = fakeStreamer,
            isFavorite = false
        )
        assertThat(viewModel.state.value).isEqualTo(expectedState)
    }

    @Test
    fun `loadStreamer when use case fails should emit Error state with the mapped DomainError message`() = runTest {
        // GIVEN
        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.failure(Exception("peu importe")) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase { Result.success(Unit) },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase { Result.success(Unit) }
        )

        // WHEN
        viewModel.loadStreamer("12")
        advanceUntilIdle()

        // THEN
        val expectedState = StreamerDetailUiState.Error(messageRes = R.string.error_unknown)
        assertThat(viewModel.state.value).isEqualTo(expectedState)
    }

    @Test
    fun `loadStreamer when use case fails with a DomainException should keep its own category`() = runTest {
        // GIVEN
        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.failure(DomainException(DomainError.Network)) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase { Result.success(Unit) },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase { Result.success(Unit) }
        )

        // WHEN
        viewModel.loadStreamer("12")
        advanceUntilIdle()

        // THEN
        val expectedState = StreamerDetailUiState.Error(messageRes = R.string.error_network)
        assertThat(viewModel.state.value).isEqualTo(expectedState)
    }

    @Test
    fun `onFavoriteClick when currently not favorite should update state optimistically and call addStreamerToFavorites`() = runTest {
        // GIVEN
        var addCalled = false
        val streamerResult = StreamerDetailResult(streamer = fakeStreamer, isFavorite = false)

        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.success(streamerResult) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase { id ->
                addCalled = true
                assertThat(id).isEqualTo("12")
                Result.success(Unit)
            },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase { Result.success(Unit) }
        )

        viewModel.loadStreamer("12")
        advanceUntilIdle()

        // WHEN
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as StreamerDetailUiState.Success
        assertThat(state.isFavorite).isTrue()
        assertThat(state.streamer.followersCount).isEqualTo(11) // 10 + 1
        assertThat(addCalled).isTrue()
    }

    @Test
    fun `onFavoriteClick when currently favorite should update state optimistically and call deleteStreamerFromFavorites`() = runTest {
        // GIVEN
        var deleteCalled = false
        val streamerResult = StreamerDetailResult(streamer = fakeStreamer, isFavorite = true)

        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.success(streamerResult) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase { Result.success(Unit) },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase { id ->
                deleteCalled = true
                assertThat(id).isEqualTo("12")
                Result.success(Unit)
            }
        )

        viewModel.loadStreamer("12")
        advanceUntilIdle()

        // WHEN
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as StreamerDetailUiState.Success
        assertThat(state.isFavorite).isFalse()
        assertThat(state.streamer.followersCount).isEqualTo(9) // 10 - 1
        assertThat(deleteCalled).isTrue()
    }

    @Test
    fun `onFavoriteClick when addition fails should rollback state and emit ShowSnackBar`() = runTest {
        // GIVEN
        val streamerResult = StreamerDetailResult(streamer = fakeStreamer, isFavorite = false)

        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.success(streamerResult) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase {
                Result.failure(DomainException(DomainError.Network))
            },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase { Result.success(Unit) }
        )

        viewModel.loadStreamer("12")
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.event.test {
            viewModel.onFavoriteClick()
            advanceUntilIdle()

            val event = awaitItem() as StreamerDetailUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_network)
        }
        val state = viewModel.state.value as StreamerDetailUiState.Success
        assertThat(state.isFavorite).isFalse()
        assertThat(state.streamer.followersCount).isEqualTo(10)
    }

    @Test
    fun `onFavoriteClick when deletion fails should rollback state to previous values`() = runTest {
        // GIVEN
        val streamerResult = StreamerDetailResult(streamer = fakeStreamer, isFavorite = true)

        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.success(streamerResult) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase { Result.success(Unit) },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase {
                Result.failure(
                    Exception("Erreur réseau")
                )
            }
        )

        viewModel.loadStreamer("12")
        advanceUntilIdle()

        // WHEN
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as StreamerDetailUiState.Success
        assertThat(state.isFavorite).isTrue()
        assertThat(state.streamer.followersCount).isEqualTo(10)
    }

    @Test
    fun `onFavoriteClick when state is not Success should do nothing`() = runTest {
        // GIVEN
        var addCalled = false
        var deleteCalled = false

        val viewModel = StreamerDetailViewModel(
            getStreamer = GetStreamerUseCase { Result.failure(Exception("Error")) },
            addStreamerToFavorites = AddStreamerToFavoritesUseCase {
                addCalled = true; Result.success(
                Unit
            )
            },
            deleteStreamerFromFavorites = DeleteStreamerFromFavoritesUseCase {
                deleteCalled = true; Result.success(
                Unit
            )
            }
        )

        // WHEN
        viewModel.onFavoriteClick()
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value).isInstanceOf(StreamerDetailUiState.Loading::class.java)
        assertThat(addCalled).isFalse()
        assertThat(deleteCalled).isFalse()
    }
}
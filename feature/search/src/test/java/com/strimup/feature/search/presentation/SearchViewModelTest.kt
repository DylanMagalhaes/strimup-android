package com.strimup.feature.search.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.core.ui.R
import com.strimup.feature.search.domain.usecase.GetLiveStreamersUseCase
import com.strimup.feature.search.domain.usecase.GetStreamersUseCase
import com.strimup.feature.search.domain.usecase.ObserveRecentStreamersUseCase
import com.strimup.feature.search.domain.usecase.RemoveRecentStreamerUseCase
import com.strimup.feature.search.domain.usecase.SaveRecentStreamerUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun searchViewModel(
        getStreamers: GetStreamersUseCase = GetStreamersUseCase { Result.success(emptyList()) },
        saveRecentStreamer: SaveRecentStreamerUseCase = SaveRecentStreamerUseCase {},
        removeRecentStreamer: RemoveRecentStreamerUseCase = RemoveRecentStreamerUseCase {},
        observeRecentStreamers: ObserveRecentStreamersUseCase = ObserveRecentStreamersUseCase { flowOf(emptyList()) },
        getLiveStreamers: GetLiveStreamersUseCase = GetLiveStreamersUseCase { Result.success(emptyList()) },
    ) = SearchViewModel(
        getStreamers = getStreamers,
        saveRecentStreamer = saveRecentStreamer,
        removeRecentStreamer = removeRecentStreamer,
        observeRecentStreamers = observeRecentStreamers,
        getLiveStreamers = getLiveStreamers,
    )

    @Test
    fun `on search input change, should emit Loading then Content with streamers when search succeeds`() = runTest {
        // GIVEN
        val query = "inox"
        val streamers = listOf(
            Streamer(id = "1", userName = "inox", imageUrl = ""),
            Streamer(id = "2", userName = "gotaga", imageUrl = "")
        )

        val viewModel = searchViewModel(
            getStreamers = GetStreamersUseCase { Result.success(streamers) }
        )

        // WHEN
        viewModel.onSearchInputChange(query)

        // THEN
        val loadingState = viewModel.state.value
        assertThat(loadingState).isInstanceOf(SearchUiState.Loading::class.java)
        assertThat(loadingState.searchQuery).isEqualTo(query)

        // WHEN
        advanceUntilIdle()

        // THEN
        val stateValue = viewModel.state.value
        assertThat(stateValue).isInstanceOf(SearchUiState.Content::class.java)

        val contentState = stateValue as SearchUiState.Content
        assertThat(contentState.searchQuery).isEqualTo(query)
        assertThat(contentState.streamers).isEqualTo(streamers)
    }

    @Test
    fun `on search input change, should emit Empty when search returns empty list`() = runTest {
        // GIVEN
        val query = "inconnu"
        val viewModel = searchViewModel(
            getStreamers = GetStreamersUseCase { Result.success(emptyList()) }
        )

        // WHEN
        viewModel.onSearchInputChange(query)
        advanceUntilIdle()

        // THEN
        val stateValue = viewModel.state.value
        assertThat(stateValue).isInstanceOf(SearchUiState.Empty::class.java)

        val emptyState = stateValue as SearchUiState.Empty
        assertThat(emptyState.searchQuery).isEqualTo(query)
    }

    @Test
    fun `on search input change, should emit Error with the mapped DomainError message when search fails`() = runTest {
        // GIVEN
        val query = "erreur"
        val viewModel = searchViewModel(
            getStreamers = GetStreamersUseCase { Result.failure(Exception("peu importe")) }
        )

        // WHEN
        viewModel.onSearchInputChange(query)
        advanceUntilIdle()

        // THEN
        val stateValue = viewModel.state.value
        assertThat(stateValue).isInstanceOf(SearchUiState.Error::class.java)

        val errorState = stateValue as SearchUiState.Error
        assertThat(errorState.searchQuery).isEqualTo(query)
        assertThat(errorState.messageRes).isEqualTo(R.string.error_unknown)
    }

    @Test
    fun `on search input change, should emit Error keeping the DomainException own category`() = runTest {
        // GIVEN
        val query = "erreur"
        val viewModel = searchViewModel(
            getStreamers = GetStreamersUseCase { Result.failure(DomainException(DomainError.Network)) }
        )

        // WHEN
        viewModel.onSearchInputChange(query)
        advanceUntilIdle()

        // THEN
        val errorState = viewModel.state.value as SearchUiState.Error
        assertThat(errorState.messageRes).isEqualTo(R.string.error_network)
    }

    @Test
    fun `on search input change with blank query, should emit Content empty without invoking use case`() = runTest {
        // GIVEN
        var useCaseCalled = false
        val viewModel = searchViewModel(
            getStreamers = GetStreamersUseCase {
                useCaseCalled = true
                Result.success(emptyList())
            }
        )

        // WHEN
        viewModel.onSearchInputChange("   ")

        // THEN
        val stateValue = viewModel.state.value
        assertThat(stateValue).isInstanceOf(SearchUiState.Content::class.java)

        val contentState = stateValue as SearchUiState.Content
        assertThat(contentState.searchQuery).isEmpty()
        assertThat(contentState.streamers).isEmpty()
        assertThat(useCaseCalled).isFalse()
    }

    @Test
    fun `on rapid search input changes, should debounce and process only the last query`() = runTest {
        // GIVEN
        var callCount = 0
        var lastQueryProcessed = ""

        val viewModel = searchViewModel(
            getStreamers = GetStreamersUseCase { query ->
                callCount++
                lastQueryProcessed = query
                Result.success(emptyList())
            }
        )

        // WHEN
        viewModel.onSearchInputChange("i")
        advanceTimeBy(200L)

        viewModel.onSearchInputChange("ino")
        advanceTimeBy(200L)

        viewModel.onSearchInputChange("inox")
        advanceUntilIdle()

        // THEN
        assertThat(callCount).isEqualTo(1)
        assertThat(lastQueryProcessed).isEqualTo("inox")
        assertThat(viewModel.state.value.searchQuery).isEqualTo("inox")
    }

    @Test
    fun `on streamer click, should save the streamer as recently opened`() = runTest {
        // GIVEN
        val streamer = Streamer(id = "1", userName = "inox", imageUrl = "")
        val savedStreamers = mutableListOf<Streamer>()
        val viewModel = searchViewModel(saveRecentStreamer = { savedStreamers += it })

        // WHEN
        viewModel.onStreamerClick(streamer)
        advanceUntilIdle()

        // THEN
        assertThat(savedStreamers).containsExactly(streamer)
    }

    @Test
    fun `on remove recent streamer click, should remove that streamer`() = runTest {
        // GIVEN
        val removedIds = mutableListOf<String>()
        val viewModel = searchViewModel(removeRecentStreamer = { removedIds += it })

        // WHEN
        viewModel.onRemoveRecentStreamerClick("1")
        advanceUntilIdle()

        // THEN
        assertThat(removedIds).containsExactly("1")
    }

    @Test
    fun `recent streamers should expose the observed recent streamers`() = runTest {
        // GIVEN
        val recentStreamers = listOf(Streamer(id = "1", userName = "inox", imageUrl = ""))
        val viewModel = searchViewModel(observeRecentStreamers = { flowOf(recentStreamers) })

        // WHEN
        viewModel.recentStreamers.test {
            // THEN
            assertThat(awaitItem()).isEmpty()
            assertThat(awaitItem()).isEqualTo(recentStreamers)
        }
    }

    @Test
    fun `live streamers should expose the streamers currently live`() = runTest {
        // GIVEN
        val liveStreamers = listOf(Streamer(id = "1", userName = "inox", imageUrl = "", isLive = true))
        val viewModel = searchViewModel(getLiveStreamers = { Result.success(liveStreamers) })

        // WHEN
        viewModel.liveStreamers.test {
            // THEN
            assertThat(awaitItem()).isEmpty()
            assertThat(awaitItem()).isEqualTo(liveStreamers)
        }
    }

    @Test
    fun `live streamers should stay empty when loading them fails`() = runTest {
        // GIVEN
        val viewModel = searchViewModel(
            getLiveStreamers = { Result.failure(DomainException(DomainError.Network)) }
        )

        // WHEN
        viewModel.liveStreamers.test {
            // THEN
            assertThat(awaitItem()).isEmpty()
            advanceUntilIdle()
            expectNoEvents()
        }
    }
}

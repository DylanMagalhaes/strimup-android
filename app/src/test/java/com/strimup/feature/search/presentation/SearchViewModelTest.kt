package com.strimup.feature.search.presentation

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.search.domain.usecase.GetStreamersUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `on search input change, should emit Loading then Content with streamers when search succeeds`() = runTest {
        // GIVEN
        val query = "inox"
        val streamers = listOf(
            Streamer(id = "1", userName = "inox", imageUrl = ""),
            Streamer(id = "2", userName = "gotaga", imageUrl = "")
        )

        val viewModel = SearchViewModel(
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
        val viewModel = SearchViewModel(
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
    fun `on search input change, should emit Error when search fails`() = runTest {
        // GIVEN
        val query = "erreur"
        val errorMessage = "Erreur réseau"
        val viewModel = SearchViewModel(
            getStreamers = GetStreamersUseCase { Result.failure(Exception(errorMessage)) }
        )

        // WHEN
        viewModel.onSearchInputChange(query)
        advanceUntilIdle()

        // THEN
        val stateValue = viewModel.state.value
        assertThat(stateValue).isInstanceOf(SearchUiState.Error::class.java)

        val errorState = stateValue as SearchUiState.Error
        assertThat(errorState.searchQuery).isEqualTo(query)
        assertThat(errorState.message).isEqualTo(errorMessage)
    }

    @Test
    fun `on search input change with blank query, should emit Content empty without invoking use case`() = runTest {
        // GIVEN
        var useCaseCalled = false
        val viewModel = SearchViewModel(
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

        val viewModel = SearchViewModel(
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
}
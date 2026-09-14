package com.strimup.feature.filter.presentation.matchedstreamer

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerMatchResult
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import com.strimup.feature.filter.domain.usecase.GetFilterByIdUseCase
import com.strimup.feature.filter.domain.usecase.GetStreamersByFilterUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MatchedStreamersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeFilter = FilterEntity(
        id = "f1",
        name = "Chill",
        criteria = FilterCriteria(),
        userId = "u1",
    )

    private val page1Streamers = listOf(
        Streamer(id = "1", userName = "inox", imageUrl = "", isLive = true),
        Streamer(id = "2", userName = "gotaga", imageUrl = "", isLive = false),
    )

    private val page2Streamers = listOf(
        Streamer(id = "3", userName = "squeezie", imageUrl = "", isLive = true),
    )

    private fun buildViewModel(
        getFilterById: GetFilterByIdUseCase = GetFilterByIdUseCase { Result.success(fakeFilter) },
        getMatchedStreamers: GetStreamersByFilterUseCase = GetStreamersByFilterUseCase { page, _ ->
            if (page == 1) {
                Result.success(StreamerMatchResult(streamers = page1Streamers, total = 2))
            } else {
                Result.success(StreamerMatchResult(streamers = page2Streamers, total = 3))
            }
        },
    ) = MatchedStreamerListViewModel(
        getMatchedStreamers = getMatchedStreamers,
        getFilterById = getFilterById,
    )

    // region initData

    @Test
    fun `state should be Loading before initData is called`() = runTest {
        // GIVEN / WHEN
        val viewModel = buildViewModel()

        // THEN
        assertThat(viewModel.state.value).isEqualTo(MatchedStreamersUiState.Loading)
    }

    @Test
    fun `initData should load the filter then the first page and expose Success state`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        viewModel.initData("f1")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.filterName).isEqualTo("Chill")
        assertThat(state.matchedResult.streamers).isEqualTo(page1Streamers)
        assertThat(state.matchedResult.total).isEqualTo(2)
        assertThat(state.isLiveOnly).isFalse()
        assertThat(state.isLoadingNextPage).isFalse()
    }

    @Test
    fun `initData called a second time should be ignored since a filter is already loaded`() = runTest {
        // GIVEN
        var getFilterByIdCallCount = 0
        val viewModel = buildViewModel(
            getFilterById = GetFilterByIdUseCase { getFilterByIdCallCount++; Result.success(fakeFilter) },
        )
        viewModel.initData("f1")
        advanceUntilIdle()

        // WHEN
        viewModel.initData("f2")
        advanceUntilIdle()

        // THEN
        assertThat(getFilterByIdCallCount).isEqualTo(1)
    }

    @Test
    fun `initData when getFilterById fails should expose Error state with exception message`() = runTest {
        // GIVEN
        val errorMessage = "Filtre introuvable"
        val viewModel = buildViewModel(
            getFilterById = GetFilterByIdUseCase { Result.failure(Exception(errorMessage)) },
        )

        // WHEN
        viewModel.initData("f1")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Error
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `initData when getFilterById fails without a message should expose the default error message`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getFilterById = GetFilterByIdUseCase { Result.failure(Exception()) },
        )

        // WHEN
        viewModel.initData("f1")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Error
        assertThat(state.errorMessage).isEqualTo("Erreur de chargement du filtre")
    }

    @Test
    fun `initData when the first page fetch fails should expose Error state with exception message`() = runTest {
        // GIVEN
        val errorMessage = "Erreur serveur"
        val viewModel = buildViewModel(
            getMatchedStreamers = GetStreamersByFilterUseCase { _, _ -> Result.failure(Exception(errorMessage)) },
        )

        // WHEN
        viewModel.initData("f1")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Error
        assertThat(state.errorMessage).isEqualTo(errorMessage)
    }

    @Test
    fun `initData when the first page fetch fails without a message should expose the default error message`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getMatchedStreamers = GetStreamersByFilterUseCase { _, _ -> Result.failure(Exception()) },
        )

        // WHEN
        viewModel.initData("f1")
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Error
        assertThat(state.errorMessage).isEqualTo("Une erreur est survenue")
    }

    // endregion

    // region loadNextPage

    @Test
    fun `loadNextPage when state is not Success should do nothing`() = runTest {
        // GIVEN
        var callCount = 0
        val viewModel = buildViewModel(
            getFilterById = GetFilterByIdUseCase { Result.failure(Exception("Erreur")) },
            getMatchedStreamers = GetStreamersByFilterUseCase { _, _ -> callCount++; Result.success(StreamerMatchResult(emptyList(), 0)) },
        )
        viewModel.initData("f1")
        advanceUntilIdle()
        assertThat(viewModel.state.value).isInstanceOf(MatchedStreamersUiState.Error::class.java)

        // WHEN
        viewModel.loadNextPage()
        advanceUntilIdle()

        // THEN
        assertThat(callCount).isEqualTo(0)
    }

    @Test
    fun `loadNextPage should fetch the next page and append its streamers to the original result`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.initData("f1")
        advanceUntilIdle()

        // WHEN
        viewModel.loadNextPage()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.matchedResult.streamers).isEqualTo(page1Streamers + page2Streamers)
        assertThat(state.matchedResult.total).isEqualTo(3)
        assertThat(state.isLoadingNextPage).isFalse()
    }

    @Test
    fun `loadNextPage when the next page returns no streamers should mark the end reached and ignore further calls`() = runTest {
        // GIVEN
        var callCount = 0
        val viewModel = buildViewModel(
            getMatchedStreamers = GetStreamersByFilterUseCase { page, _ ->
                callCount++
                if (page == 1) {
                    Result.success(StreamerMatchResult(page1Streamers, 2))
                } else {
                    Result.success(StreamerMatchResult(emptyList(), 2))
                }
            },
        )
        viewModel.initData("f1")
        advanceUntilIdle()

        // WHEN
        viewModel.loadNextPage()
        advanceUntilIdle()
        viewModel.loadNextPage() // should be ignored: isEndReached is now true
        advanceUntilIdle()

        // THEN
        assertThat(callCount).isEqualTo(2) // page 1 + the empty page 2, never a 3rd call
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.matchedResult.streamers).isEqualTo(page1Streamers)
    }

    @Test
    fun `loadNextPage called while a page is already loading should be ignored`() = runTest {
        // GIVEN
        val pageTwoResult = CompletableDeferred<Result<StreamerMatchResult>>()
        var callCount = 0
        val viewModel = buildViewModel(
            getMatchedStreamers = GetStreamersByFilterUseCase { page, _ ->
                callCount++
                if (page == 1) Result.success(StreamerMatchResult(page1Streamers, 2)) else pageTwoResult.await()
            },
        )
        viewModel.initData("f1")
        advanceUntilIdle()

        // WHEN
        viewModel.loadNextPage()
        runCurrent() // let the coroutine run up to pageTwoResult.await()
        assertThat((viewModel.state.value as MatchedStreamersUiState.Success).isLoadingNextPage).isTrue()

        viewModel.loadNextPage() // should be ignored: isLoadingNextPage is already true
        runCurrent()

        // THEN
        assertThat(callCount).isEqualTo(2) // page 1 + the single in-flight page 2 call

        pageTwoResult.complete(Result.success(StreamerMatchResult(page2Streamers, 3)))
        advanceUntilIdle()
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.matchedResult.streamers).isEqualTo(page1Streamers + page2Streamers)
    }

    @Test
    fun `loadNextPage when a later page fails should reset isLoadingNextPage and keep the existing streamers`() = runTest {
        // GIVEN
        // TODO: cette branche ne remonte aucun message d'erreur à l'utilisateur (voir le TODO
        //  dans MatchedStreamerListViewModel.fetchStreamersPage) — ce test documente le
        //  comportement actuel (silencieux), à mettre à jour si un canal d'événements est ajouté.
        val viewModel = buildViewModel(
            getMatchedStreamers = GetStreamersByFilterUseCase { page, _ ->
                if (page == 1) {
                    Result.success(StreamerMatchResult(page1Streamers, 2))
                } else {
                    Result.failure(Exception("Erreur réseau"))
                }
            },
        )
        viewModel.initData("f1")
        advanceUntilIdle()

        // WHEN
        viewModel.loadNextPage()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.isLoadingNextPage).isFalse()
        assertThat(state.matchedResult.streamers).isEqualTo(page1Streamers)
    }

    // endregion

    // region onLiveSwitch

    @Test
    fun `onLiveSwitch should filter down to live streamers only when toggled on`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.initData("f1")
        advanceUntilIdle()

        // WHEN
        viewModel.onLiveSwitch()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.isLiveOnly).isTrue()
        assertThat(state.matchedResult.streamers).containsExactly(page1Streamers[0]) // only the live one
    }

    @Test
    fun `onLiveSwitch called again should revert to the full streamers list`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.initData("f1")
        advanceUntilIdle()
        viewModel.onLiveSwitch()

        // WHEN
        viewModel.onLiveSwitch()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.isLiveOnly).isFalse()
        assertThat(state.matchedResult.streamers).isEqualTo(page1Streamers)
    }

    @Test
    fun `onLiveSwitch when state is not Success should do nothing`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getFilterById = GetFilterByIdUseCase { Result.failure(Exception("Erreur")) },
        )
        viewModel.initData("f1")
        advanceUntilIdle()
        val errorState = viewModel.state.value

        // WHEN
        viewModel.onLiveSwitch()

        // THEN
        assertThat(viewModel.state.value).isEqualTo(errorState)
    }

    @Test
    fun `loadNextPage after onLiveSwitch is enabled should keep filtering newly appended streamers to live only`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        viewModel.initData("f1")
        advanceUntilIdle()
        viewModel.onLiveSwitch()

        // WHEN
        viewModel.loadNextPage()
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as MatchedStreamersUiState.Success
        assertThat(state.isLiveOnly).isTrue()
        // page1Streamers[0] (live) + page2Streamers[0] (live) are kept, page1Streamers[1] (not live) is filtered out
        assertThat(state.matchedResult.streamers).containsExactly(page1Streamers[0], page2Streamers[0])
        assertThat(state.originalMatchedResult.streamers).isEqualTo(page1Streamers + page2Streamers)
    }

    // endregion
}

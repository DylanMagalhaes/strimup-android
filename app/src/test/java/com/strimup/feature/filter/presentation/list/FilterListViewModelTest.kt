package com.strimup.feature.filter.presentation.list

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import com.strimup.feature.filter.domain.usecase.DeleteFilterUseCase
import com.strimup.feature.filter.domain.usecase.ObserveFiltersUseCase
import com.strimup.feature.filter.domain.usecase.RefreshFiltersUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FilterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeFilters = listOf(
        FilterEntity(id = "1", name = "Chill", criteria = FilterCriteria(), userId = "u1"),
        FilterEntity(id = "2", name = "Tryhard", criteria = FilterCriteria(), userId = "u1"),
    )

    private fun observeUseCase(flow: Flow<List<FilterEntity>>) =
        object : ObserveFiltersUseCase {
            override fun invoke(): Flow<List<FilterEntity>> = flow
        }

    private fun buildViewModel(
        observeFilters: ObserveFiltersUseCase = observeUseCase(MutableStateFlow(fakeFilters)),
        refreshFilters: RefreshFiltersUseCase = RefreshFiltersUseCase { Result.success(Unit) },
        deleteFilter: DeleteFilterUseCase = DeleteFilterUseCase { Result.success(Unit) },
    ) = FilterListViewModel(
        observeFilters = observeFilters,
        refreshFilters = refreshFilters,
        deleteFilter = deleteFilter,
    )

    @Test
    fun `state should default to isLoading true before the observed filters are collected`() = runTest {
        // GIVEN
        val observedFilters = MutableStateFlow(fakeFilters)

        // WHEN
        val viewModel = buildViewModel(
            observeFilters = observeUseCase(observedFilters),
        )

        // THEN
        assertThat(viewModel.state.value.isLoading).isTrue()
    }

    @Test
    fun `init should collect the observed filters and update state with the list`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            observeFilters = observeUseCase(MutableStateFlow(fakeFilters)),
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.filters).isEqualTo(fakeFilters)
        assertThat(state.isEmpty).isFalse()
    }

    @Test
    fun `init when the observed filters are empty should set isEmpty to true`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            observeFilters = observeUseCase(MutableStateFlow(emptyList())),
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.isEmpty).isTrue()
    }

    @Test
    fun `init should collect every update emitted by the observed filters flow`() = runTest {
        // GIVEN
        val observedFilters = MutableStateFlow(fakeFilters)
        val viewModel = buildViewModel(
            observeFilters = observeUseCase(observedFilters),
        )
        advanceUntilIdle()
        assertThat(viewModel.state.value.filters).isEqualTo(fakeFilters)

        // WHEN
        observedFilters.value = listOf(fakeFilters[0])
        advanceUntilIdle()

        // THEN
        assertThat(viewModel.state.value.filters).isEqualTo(listOf(fakeFilters[0]))
    }

    @Test
    fun `init when refresh fails should set isLoading false and emit ShowSnackBar with the mapped DomainError message`() = runTest {
        // GIVEN & WHEN
        val viewModel = buildViewModel(
            refreshFilters = RefreshFiltersUseCase { Result.failure(Exception("peu importe")) },
        )

        // THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(FilterListUiEvent.ShowSnackBar::class.java)
            assertThat((event as FilterListUiEvent.ShowSnackBar).textRes).isEqualTo(R.string.error_unknown)
        }
        assertThat(viewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `init when refresh fails with a DomainException should keep its own category`() = runTest {
        // GIVEN & WHEN
        val viewModel = buildViewModel(
            refreshFilters = RefreshFiltersUseCase { Result.failure(DomainException(DomainError.Network)) },
        )

        // THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem() as FilterListUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_network)
        }
    }

    @Test
    fun `init should call refreshFilters exactly once`() = runTest {
        // GIVEN
        var callCount = 0

        // WHEN
        buildViewModel(
            refreshFilters = RefreshFiltersUseCase {
                callCount++
                Result.success(Unit)
            },
        )
        advanceUntilIdle()

        // THEN
        assertThat(callCount).isEqualTo(1)
    }

    @Test
    fun `onDeleteButtonClick should call deleteFilter with the given id`() = runTest {
        // GIVEN
        var capturedId: String? = null
        val viewModel = buildViewModel(
            deleteFilter = DeleteFilterUseCase { id -> capturedId = id; Result.success(Unit) },
        )
        advanceUntilIdle()

        // WHEN
        viewModel.onDeleteButtonClick("1")
        advanceUntilIdle()

        // THEN
        assertThat(capturedId).isEqualTo("1")
    }

    @Test
    fun `onDeleteButtonClick when delete succeeds should keep the filter removed and emit a success snackbar`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onDeleteButtonClick("1")
            advanceUntilIdle()

            val event = awaitItem() as FilterListUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.filter_deleted_success)
        }
        assertThat(viewModel.state.value.filters).containsExactly(fakeFilters[1])
    }

    @Test
    fun `onDeleteButtonClick when delete fails should rollback the filters and emit ShowSnackBar with the mapped DomainError message`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            deleteFilter = DeleteFilterUseCase { Result.failure(Exception("peu importe")) },
        )
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onDeleteButtonClick("1")
            advanceUntilIdle()

            val event = awaitItem() as FilterListUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_unknown)
        }
        assertThat(viewModel.state.value.filters).isEqualTo(fakeFilters)
    }

    @Test
    fun `onDeleteButtonClick when delete fails with a DomainException should keep its own category`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            deleteFilter = DeleteFilterUseCase { Result.failure(DomainException(DomainError.Server(500))) },
        )
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onDeleteButtonClick("1")
            advanceUntilIdle()

            val event = awaitItem() as FilterListUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_server)
        }
    }
}

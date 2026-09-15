package com.strimup.feature.filter.presentation.list

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterEntity
import com.strimup.feature.filter.domain.usecase.DeleteFilterUseCase
import com.strimup.feature.filter.domain.usecase.GetFiltersUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private fun buildViewModel(
        getFilters: GetFiltersUseCase = GetFiltersUseCase { Result.success(fakeFilters) },
        deleteFilter: DeleteFilterUseCase = DeleteFilterUseCase { Result.success(Unit) },
    ) = FilterListViewModel(
        getFilters = getFilters,
        deleteFilter = deleteFilter,
    )


    @Test
    fun `state should default to isLoading true before any filters are loaded`() = runTest {
        // GIVEN / WHEN
        val viewModel = buildViewModel()

        // THEN
        assertThat(viewModel.state.value.isLoading).isTrue()
        assertThat(viewModel.state.value.filters).isEmpty()
    }

    @Test
    fun `init should load filters and update state with the list on success`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getFilters = GetFiltersUseCase { Result.success(fakeFilters) },
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
    fun `init when getFilters returns an empty list should set isEmpty to true`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getFilters = GetFiltersUseCase { Result.success(emptyList()) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.isEmpty).isTrue()
    }

    @Test
    fun `init when getFilters fails should set isLoading false and emit ShowSnackBar with the mapped DomainError message`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getFilters = GetFiltersUseCase { Result.failure(Exception("peu importe")) },
        )

        // WHEN & THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isInstanceOf(FilterListUiEvent.ShowSnackBar::class.java)
            assertThat((event as FilterListUiEvent.ShowSnackBar).textRes).isEqualTo(R.string.error_unknown)
        }
        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.filters).isEmpty()
    }

    @Test
    fun `init when getFilters fails with a DomainException should keep its own category`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getFilters = GetFiltersUseCase { Result.failure(DomainException(DomainError.Network)) },
        )

        // WHEN & THEN
        viewModel.events.test {
            advanceUntilIdle()

            val event = awaitItem() as FilterListUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_network)
        }
    }

    @Test
    fun `loadFilters called again should reload and replace the current filters`() = runTest {
        // GIVEN
        var callCount = 0
        val viewModel = buildViewModel(
            getFilters = GetFiltersUseCase {
                callCount++
                if (callCount == 1) Result.success(fakeFilters) else Result.success(listOf(fakeFilters[0]))
            },
        )
        advanceUntilIdle()
        assertThat(viewModel.state.value.filters).isEqualTo(fakeFilters)

        // WHEN
        viewModel.loadFilters()
        advanceUntilIdle()

        // THEN
        assertThat(callCount).isEqualTo(2)
        assertThat(viewModel.state.value.filters).isEqualTo(listOf(fakeFilters[0]))
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

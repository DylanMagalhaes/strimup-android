package com.strimup.feature.cuts.presentation

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.cuts.domain.entity.CutsPage
import com.strimup.feature.cuts.domain.entity.ExpiredCutsCursorException
import com.strimup.feature.cuts.domain.usecase.GetCutsUseCase
import com.strimup.feature.cuts.fake.cuts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CutsPagerTest {

    private val requestedCursors = mutableListOf<String?>()

    private fun pager(results: List<Result<CutsPage>>): CutsPager {
        val queue = ArrayDeque(results)
        return CutsPager(
            getCuts = GetCutsUseCase { cursor ->
                requestedCursors += cursor
                queue.removeFirst()
            },
        )
    }

    private fun serverError(code: Int) = Result.failure<CutsPage>(DomainException(DomainError.Server(code)))

    @Test
    fun `pages should follow the cursor given by the server`() = runTest {
        val pager = pager(
            listOf(
                Result.success(CutsPage(cuts("a", "b"), nextCursor = "c1")),
                Result.success(CutsPage(cuts("c"), nextCursor = null)),
                Result.success(CutsPage(cuts("b", "a"), nextCursor = "c2")),
            ),
        )

        pager.loadNextPage()
        pager.loadNextPage()
        val reshuffled = pager.loadNextPage().getOrThrow()

        assertThat(requestedCursors).containsExactly(null, "c1", null).inOrder()
        assertThat(reshuffled.map { it.id }).containsExactly("b", "a").inOrder()
    }

    @Test
    fun `an expired cursor should restart the feed without cursor`() = runTest {
        val pager = pager(
            listOf(
                Result.success(CutsPage(cuts("a"), nextCursor = "altered")),
                Result.failure(ExpiredCutsCursorException()),
                Result.success(CutsPage(cuts("b"), nextCursor = "c1")),
            ),
        )

        pager.loadNextPage()
        val restarted = pager.loadNextPage().getOrThrow()

        assertThat(requestedCursors).containsExactly(null, "altered", null).inOrder()
        assertThat(restarted.map { it.id }).containsExactly("b")
    }

    @Test
    fun `a server error should be retried after 1, 2 then 4 seconds`() = runTest {
        val pager = pager(
            listOf(
                serverError(500),
                serverError(500),
                serverError(503),
                Result.success(CutsPage(cuts("a"), nextCursor = null)),
            ),
        )

        val result = pager.loadNextPage()

        assertThat(result.getOrThrow().map { it.id }).containsExactly("a")
        assertThat(currentTime).isEqualTo(7_000)
    }

    @Test
    fun `a server error should fail after the last retry`() = runTest {
        val pager = pager(List(4) { serverError(500) })

        val error = pager.loadNextPage().exceptionOrNull() as DomainException

        assertThat(error.error).isEqualTo(DomainError.Server(500))
        assertThat(requestedCursors).hasSize(4)
    }

    @Test
    fun `a client error should not be retried`() = runTest {
        val pager = pager(listOf(serverError(403)))

        pager.loadNextPage()

        assertThat(requestedCursors).hasSize(1)
        assertThat(currentTime).isEqualTo(0)
    }
}

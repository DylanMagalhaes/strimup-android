package com.strimup.feature.streamerprofile.presentation.streamerprofile

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.auth.domain.usecase.LogoutUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StreamerProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeUser = UserEntity(
        id = "1",
        userName = "inox",
        email = "inox@mail.com",
        role = UserRole.STREAMER,
        avatarUrl = "",
    )

    private val fakeStreamer = Streamer(
        id = "1",
        userName = "inox",
        imageUrl = "",
    )

    private fun getStreamerUseCase(fn: suspend (String) -> Result<Streamer>) =
        object : GetStreamerUseCase {
            override suspend fun invoke(id: String): Result<Streamer> = fn(id)
        }

    private fun buildViewModel(
        getUser: GetUserFlowUseCase = GetUserFlowUseCase { flowOf(fakeUser) },
        getStreamer: GetStreamerUseCase = getStreamerUseCase { Result.success(fakeStreamer) },
        logout: LogoutUseCase = LogoutUseCase { Result.success(Unit) },
    ) = StreamerProfileViewModel(
        getUser = getUser,
        getStreamer = getStreamer,
        logout = logout,
    )

    // region init

    @Test
    fun `state should default to Loading before the user flow emits`() = runTest {
        // GIVEN / WHEN
        val viewModel = buildViewModel(getUser = GetUserFlowUseCase { MutableSharedFlow() })

        // THEN
        assertThat(viewModel.state.value).isEqualTo(ProfileUiState.Loading)
    }

    @Test
    fun `init when user is logged in should load and expose the streamer profile`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ProfileUiState.Success
        assertThat(state.streamer).isEqualTo(fakeStreamer)
    }

    @Test
    fun `init when user flow emits null should expose Error state`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(getUser = GetUserFlowUseCase { flowOf(null) })

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ProfileUiState.Error
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_not_logged_in)
    }

    @Test
    fun `init when user id is blank should expose Error state`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getUser = GetUserFlowUseCase { flowOf(fakeUser.copy(id = "  ")) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ProfileUiState.Error
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_not_logged_in)
    }

    @Test
    fun `init when getStreamer fails should expose Error state with the mapped DomainError message`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getStreamer = getStreamerUseCase { Result.failure(Exception("peu importe")) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ProfileUiState.Error
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_unknown)
    }

    @Test
    fun `init when getStreamer fails with a DomainException should keep its own category`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            getStreamer = getStreamerUseCase { Result.failure(DomainException(DomainError.Network)) },
        )

        // WHEN
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value as ProfileUiState.Error
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_network)
    }

    @Test
    fun `when the user flow emits a different user the profile should reload for the new id`() = runTest {
        // GIVEN
        val userFlow = MutableSharedFlow<UserEntity?>()
        val requestedIds = mutableListOf<String>()
        val otherStreamer = Streamer(id = "2", userName = "gotaga", imageUrl = "")
        val viewModel = buildViewModel(
            getUser = GetUserFlowUseCase { userFlow },
            getStreamer = getStreamerUseCase { id ->
                requestedIds += id
                if (id == "1") Result.success(fakeStreamer) else Result.success(otherStreamer)
            },
        )
        runCurrent()

        // WHEN
        userFlow.emit(fakeUser)
        advanceUntilIdle()
        userFlow.emit(fakeUser.copy(id = "2"))
        advanceUntilIdle()

        // THEN
        assertThat(requestedIds).containsExactly("1", "2").inOrder()
        val state = viewModel.state.value as ProfileUiState.Success
        assertThat(state.streamer).isEqualTo(otherStreamer)
    }

    // endregion

    // region refresh

    @Test
    fun `refresh should immediately reset state to Loading before the new profile arrives`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()
        assertThat(viewModel.state.value).isInstanceOf(ProfileUiState.Success::class.java)

        // WHEN
        viewModel.refresh()

        // THEN
        assertThat(viewModel.state.value).isEqualTo(ProfileUiState.Loading)
    }

    @Test
    fun `refresh should reload the streamer for the current user and expose the latest data`() = runTest {
        // GIVEN
        var callCount = 0
        val updatedStreamer = fakeStreamer.copy(bio = "Nouvelle bio")
        val viewModel = buildViewModel(
            getStreamer = getStreamerUseCase {
                callCount++
                Result.success(if (callCount == 1) fakeStreamer else updatedStreamer)
            },
        )
        advanceUntilIdle()

        // WHEN
        viewModel.refresh()
        advanceUntilIdle()

        // THEN
        assertThat(callCount).isEqualTo(2)
        val state = viewModel.state.value as ProfileUiState.Success
        assertThat(state.streamer).isEqualTo(updatedStreamer)
    }

    @Test
    fun `refresh when it fails should keep the previously displayed profile and emit ShowSnackBar`() = runTest {
        // GIVEN
        var callCount = 0
        val viewModel = buildViewModel(
            getStreamer = getStreamerUseCase {
                callCount++
                if (callCount == 1) Result.success(fakeStreamer) else Result.failure(Exception("peu importe"))
            },
        )
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.refresh()
            advanceUntilIdle()

            val event = awaitItem() as ProfileUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_unknown)
        }
        val state = viewModel.state.value as ProfileUiState.Success
        assertThat(state.streamer).isEqualTo(fakeStreamer)
    }

    @Test
    fun `refresh when it fails with a DomainException should emit its own category`() = runTest {
        // GIVEN
        var callCount = 0
        val viewModel = buildViewModel(
            getStreamer = getStreamerUseCase {
                callCount++
                if (callCount == 1) {
                    Result.success(fakeStreamer)
                } else {
                    Result.failure(DomainException(DomainError.Network))
                }
            },
        )
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.refresh()
            advanceUntilIdle()

            val event = awaitItem() as ProfileUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_network)
        }
    }

    @Test
    fun `refresh when no user has ever been loaded should do nothing`() = runTest {
        // GIVEN
        var callCount = 0
        val viewModel = buildViewModel(
            getUser = GetUserFlowUseCase { flowOf(null) },
            getStreamer = getStreamerUseCase { callCount++; Result.success(fakeStreamer) },
        )
        advanceUntilIdle()
        val errorState = viewModel.state.value

        // WHEN
        viewModel.refresh()
        advanceUntilIdle()

        // THEN
        assertThat(callCount).isEqualTo(0)
        assertThat(viewModel.state.value).isEqualTo(errorState)
    }

    // endregion

    // region logout

    @Test
    fun `onLogoutClick when logout succeeds should emit LoggedOut`() = runTest {
        // GIVEN
        val viewModel = buildViewModel()
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()

            val event = awaitItem()
            assertThat(event).isEqualTo(ProfileUiEvent.LoggedOut)
        }
    }

    @Test
    fun `onLogoutClick when logout fails should emit ShowSnackBar with the mapped DomainError message`() = runTest {
        // GIVEN
        val viewModel = buildViewModel(
            logout = LogoutUseCase { Result.failure(Exception("peu importe")) },
        )
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.events.test {
            viewModel.onLogoutClick()
            advanceUntilIdle()

            val event = awaitItem() as ProfileUiEvent.ShowSnackBar
            assertThat(event.textRes).isEqualTo(R.string.error_unknown)
        }
    }

    // endregion
}

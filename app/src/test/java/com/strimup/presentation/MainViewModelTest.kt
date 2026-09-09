package com.strimup.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.strimup.core.user.domain.entity.UserEntity
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.core.user.domain.usecase.GetUserFlowUseCase
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
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeUser = UserEntity(
        id = "1",
        userName ="inox",
        email = "inox@mail.com",
        role = UserRole.STREAMER,
        avatarUrl = "",
    )

    @Test
    fun `init should collect user flow and update state from loading to loaded user`() = runTest {
        // GIVEN
        val getUserUseCase = GetUserFlowUseCase { flowOf(fakeUser) }

        // WHEN
        val viewModel = MainViewModel(getUser = getUserUseCase)
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.loading).isFalse()
        assertThat(state.user).isEqualTo(fakeUser)
    }

    @Test
    fun `init when user flow emits null should update state with loading false and null user`() = runTest {
        // GIVEN
        val getUserUseCase = GetUserFlowUseCase { flowOf(null) }

        // WHEN
        val viewModel = MainViewModel(getUser = getUserUseCase)
        advanceUntilIdle()

        // THEN
        val state = viewModel.state.value
        assertThat(state.loading).isFalse()
        assertThat(state.user).isNull()
    }

    @Test
    fun `init when user flow emits updated user should reflect latest user in state`() = runTest {
        // GIVEN
        val userFlow = MutableSharedFlow<UserEntity?>()
        val getUserUseCase = GetUserFlowUseCase { userFlow }

        val viewModel = MainViewModel(getUser = getUserUseCase)
        // Laisse le `collect { }` du init s'abonner à userFlow avant toute émission :
        // sinon un MutableSharedFlow sans replay perd la valeur émise.
        runCurrent()

        viewModel.state.test {

            val initialState = awaitItem()
            assertThat(initialState.loading).isTrue()
            assertThat(initialState.user).isNull()

            userFlow.emit(fakeUser)
            val updatedState = awaitItem()
            assertThat(updatedState.loading).isFalse()
            assertThat(updatedState.user).isEqualTo(fakeUser)

            val updatedUser = fakeUser.copy(userName = "inox_updated")
            userFlow.emit(updatedUser)
            val finalState = awaitItem()
            assertThat(finalState.loading).isFalse()
            assertThat(finalState.user).isEqualTo(updatedUser)
        }
    }
}
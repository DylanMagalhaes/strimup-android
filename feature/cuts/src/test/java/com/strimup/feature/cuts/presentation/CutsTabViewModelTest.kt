package com.strimup.feature.cuts.presentation

import com.google.common.truth.Truth.assertThat
import com.strimup.core.testing.MainDispatcherRule
import com.strimup.feature.cuts.domain.usecase.ObserveHasOpenedCutsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CutsTabViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `the new badge should show until Cuts is opened`() = runTest {
        val hasOpenedCuts = MutableStateFlow(false)
        val viewModel = CutsTabViewModel(ObserveHasOpenedCutsUseCase { hasOpenedCuts })
        runCurrent()

        assertThat(viewModel.showNewBadge.value).isTrue()

        hasOpenedCuts.value = true
        runCurrent()

        assertThat(viewModel.showNewBadge.value).isFalse()
    }
}

package com.strimup.feature.cuts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.cuts.domain.usecase.ObserveHasOpenedCutsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CutsTabViewModel @Inject constructor(
    observeHasOpenedCuts: ObserveHasOpenedCutsUseCase,
) : ViewModel() {

    val showNewBadge: StateFlow<Boolean> = observeHasOpenedCuts()
        .map { hasOpenedCuts -> !hasOpenedCuts }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = false)
}

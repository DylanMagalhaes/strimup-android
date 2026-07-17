package com.strimup.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.common.user.domain.usecase.GetUserFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUser: GetUserFlowUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(Uistate())
    val state: StateFlow<Uistate> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getUser().collect { user ->
                _state.update {
                    it.copy(
                        loading = false,
                        user = user
                    )
                }
            }

        }
    }

}
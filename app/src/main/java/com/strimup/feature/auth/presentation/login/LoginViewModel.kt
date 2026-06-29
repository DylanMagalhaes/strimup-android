package com.strimup.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import com.strimup.feature.auth.domain.AuthRepository
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
}
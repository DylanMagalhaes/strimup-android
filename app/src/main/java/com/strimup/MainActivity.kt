package com.strimup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.feature.auth.presentation.login.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StrimupTheme {
                LoginScreen(
                    modifier = Modifier.fillMaxSize(),
                    emailValue = "",
                    onEmailChange = {},
                    passwordValue = "",
                    onPasswordChange = {},
                    onForgetPasswordClick = {},
                    onLoginClick = {},
                    onRegisterClick = {},
                )
            }
        }
    }
}

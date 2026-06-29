package com.strimup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.feature.auth.presentation.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StrimupTheme {
                // StrimupNavDisplay(modifier = Modifier.fillMaxSize())
                LoginScreen()
            }
        }
    }
}

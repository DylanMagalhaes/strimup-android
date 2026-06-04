package com.strimup.common.ui.theme

import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

@Composable
fun StrimupTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 32.dp) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            content = content
        )
    }
}
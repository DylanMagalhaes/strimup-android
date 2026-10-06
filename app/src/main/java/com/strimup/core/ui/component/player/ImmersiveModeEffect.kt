package com.strimup.core.ui.component.player

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun ImmersiveModeEffect() {
    val view = LocalView.current
    val window = remember(view) { (view.context as? ComponentActivity)?.window }

    DisposableEffect(window, view) {
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }

        window?.let { WindowCompat.setDecorFitsSystemWindows(it, false) }
        controller?.apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            window?.let { WindowCompat.setDecorFitsSystemWindows(it, true) }
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

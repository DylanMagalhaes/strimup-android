package com.strimup.common.ui.component.helper

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.animateDismiss(
    scope: CoroutineScope,
    onDismiss: () -> Unit
) {
    scope.launch {
        hide()
    }.invokeOnCompletion {
        if (!isVisible) {
            onDismiss()
        }
    }
}
package com.strimup.core.ui.browser

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import com.strimup.R
import kotlinx.coroutines.launch

@Composable
fun rememberExternalLinkOpener(snackBarHostState: SnackbarHostState): ExternalLinkOpener {
    val uriHandler = LocalUriHandler.current
    val resources = LocalResources.current
    val coroutineScope = rememberCoroutineScope()

    return remember(uriHandler, resources, snackBarHostState, coroutineScope) {
        ExternalLinkOpener { url ->
            if (url.isNullOrBlank()) return@ExternalLinkOpener
            runCatching { uriHandler.openUri(url) }.onFailure {
                coroutineScope.launch { snackBarHostState.showSnackbar(resources.getString(R.string.error_open_link)) }
            }
        }
    }
}

fun interface ExternalLinkOpener {
    operator fun invoke(url: String?)
}

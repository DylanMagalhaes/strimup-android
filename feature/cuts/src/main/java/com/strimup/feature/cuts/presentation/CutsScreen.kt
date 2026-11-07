package com.strimup.feature.cuts.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.text.asString
import com.strimup.feature.cuts.R
import com.strimup.feature.cuts.presentation.component.CutSlideContent
import com.strimup.feature.report.presentation.ReportVideoSheet
import com.strimup.core.ui.R as CoreUiR

private const val BEYOND_VIEWPORT_PAGES = 2

@Composable
fun CutsScreen(
    onNavUp: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CutsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val currentOnLoginRequired by rememberUpdatedState(onLoginRequired)

    KeepScreenOn()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CutsUiEvent.LoginRequired -> {
                    val result = snackBarHostState.showSnackbar(
                        message = resources.getString(event.messageRes),
                        actionLabel = resources.getString(R.string.cuts_login_action),
                    )
                    if (result == SnackbarResult.ActionPerformed) currentOnLoginRequired()
                }

                is CutsUiEvent.ShowMessage -> snackBarHostState.showSnackbar(event.message.asString(resources))
            }
        }
    }

    CutsScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onNavUp = onNavUp,
        onActiveSlideChanged = viewModel::onActiveSlideChanged,
        onRetryClick = viewModel::onRetryClick,
        onMuteClick = viewModel::onMuteClick,
        onLikeClick = viewModel::onLikeClick,
        onReportClick = viewModel::onReportClick,
        onAuthorClick = onAuthorClick,
        modifier = modifier,
    )

    state.reportedVideoId?.let { videoId ->
        ReportVideoSheet(
            videoId = videoId,
            onDismiss = viewModel::onReportDismiss,
            onVideoUnavailable = { viewModel.onVideoUnavailable(videoId) },
            onLoginRequired = {
                viewModel.onReportDismiss()
                onLoginRequired()
            },
        )
    }
}

@Composable
private fun CutsScreen(
    state: CutsUiState,
    snackBarHostState: SnackbarHostState,
    onNavUp: () -> Unit,
    onActiveSlideChanged: (Int) -> Unit,
    onRetryClick: () -> Unit,
    onMuteClick: () -> Unit,
    onLikeClick: (String, Boolean) -> Unit,
    onReportClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when {
            state.slides.isNotEmpty() -> CutsPager(
                state = state,
                onActiveSlideChanged = onActiveSlideChanged,
                onMuteClick = onMuteClick,
                onLikeClick = onLikeClick,
                onReportClick = onReportClick,
                onAuthorClick = onAuthorClick,
            )

            state.errorMessage != null -> CutsMessage(
                text = state.errorMessage.asString(),
                onRetryClick = onRetryClick,
            )

            state.isEmpty -> CutsMessage(text = stringResource(R.string.cuts_empty), onRetryClick = onRetryClick)

            else -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
        }

        CutsTopBar(onNavUp = onNavUp, modifier = Modifier.align(Alignment.TopStart))

        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp),
        )
    }
}

@Composable
private fun CutsPager(
    state: CutsUiState,
    onActiveSlideChanged: (Int) -> Unit,
    onMuteClick: () -> Unit,
    onLikeClick: (String, Boolean) -> Unit,
    onReportClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = state.activeIndex) { state.slides.size }
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    val isResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    val currentOnActiveSlideChanged by rememberUpdatedState(onActiveSlideChanged)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page -> currentOnActiveSlideChanged(page) }
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = BEYOND_VIEWPORT_PAGES,
        key = { page -> state.slides[page].key },
    ) { page ->
        val cut = state.slides[page].cut
        CutSlideContent(
            cut = cut,
            isActive = isResumed && page == pagerState.settledPage,
            isMuted = state.isMuted,
            isLikeEnabled = !state.isLikeDisabled,
            onMuteClick = onMuteClick,
            onLikeClick = { isDoubleTap -> onLikeClick(cut.id, isDoubleTap) },
            onReportClick = { onReportClick(cut.id) },
            onAuthorClick = { onAuthorClick(cut.author.id) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun CutsTopBar(onNavUp: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavUp) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(CoreUiR.string.action_back),
                tint = Color.White,
            )
        }
        Image(
            painter = painterResource(R.drawable.cuts_logo),
            contentDescription = stringResource(R.string.cuts_title),
            modifier = Modifier.height(24.dp),
        )
    }
}

@Composable
private fun CutsMessage(text: String, onRetryClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        PrimaryButton(
            label = stringResource(CoreUiR.string.action_retry),
            modifier = Modifier.fillMaxWidth(),
            onClick = onRetryClick,
        )
    }
}

@Composable
private fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}

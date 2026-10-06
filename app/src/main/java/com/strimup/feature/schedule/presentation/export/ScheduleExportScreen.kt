package com.strimup.feature.schedule.presentation.export

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.strimup.R
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.text.UiText
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

private const val STORY_ASPECT_RATIO = 9f / 16f
private const val PREVIEW_WIDTH_FRACTION = 0.72f
private const val OVERLAY_ALPHA = 0.6f

@Composable
fun ScheduleExportScreen(
    username: String,
    onNavUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleExportViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val shareImage = rememberScheduleImageSharer(snackBarHostState)

    LaunchedEffect(username) {
        viewModel.load(username)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ScheduleExportUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }
            }
        }
    }

    ScheduleExportScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onNavUp = onNavUp,
        onTemplateSelected = viewModel::onTemplateSelected,
        onRetryClick = viewModel::onRetryClick,
        onShareClick = { state.imageFile?.let(shareImage::invoke) },
        onSaveToGalleryClick = viewModel::onSaveToGalleryClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleExportScreen(
    state: ScheduleExportUiState,
    snackBarHostState: SnackbarHostState,
    onNavUp: () -> Unit,
    onTemplateSelected: (ScheduleExportTemplate) -> Unit,
    onRetryClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveToGalleryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.schedule_export_title),
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ExportPreview(
                state = state,
                onRetryClick = onRetryClick,
                modifier = Modifier.fillMaxWidth(PREVIEW_WIDTH_FRACTION),
            )

            TemplateSelector(
                selectedTemplate = state.selectedTemplate,
                onTemplateSelected = onTemplateSelected,
            )

            ExportActions(
                state = state,
                onShareClick = onShareClick,
                onSaveToGalleryClick = onSaveToGalleryClick,
            )
        }
    }
}

@Composable
private fun ExportPreview(
    state: ScheduleExportUiState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(STORY_ASPECT_RATIO)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        state.imageFile?.let { file ->
            AsyncImage(
                model = file,
                contentDescription = stringResource(R.string.schedule_export_preview_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }

        when {
            state.errorMessage != null -> PreviewOverlay {
                Text(
                    text = state.errorMessage.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                TextButton(onClick = onRetryClick) {
                    Text(text = stringResource(R.string.action_retry))
                }
            }

            state.isGenerating -> PreviewOverlay {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.schedule_export_preparing),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun PreviewOverlay(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = OVERLAY_ALPHA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        content()
    }
}

@Composable
private fun TemplateSelector(
    selectedTemplate: ScheduleExportTemplate,
    onTemplateSelected: (ScheduleExportTemplate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.schedule_export_template_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScheduleExportTemplate.entries.forEach { template ->
                TemplateOption(
                    template = template,
                    isSelected = template == selectedTemplate,
                    onClick = { onTemplateSelected(template) },
                )
            }
        }
    }
}

@Composable
private fun TemplateOption(
    template: ScheduleExportTemplate,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    val border = if (isSelected) {
        BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }

    Column(
        modifier = modifier
            .clip(shape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = template.backgroundRes,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(72.dp)
                .aspectRatio(STORY_ASPECT_RATIO)
                .clip(shape)
                .border(border, shape),
        )
        Text(
            text = stringResource(template.labelRes),
            style = MaterialTheme.typography.labelLarge,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ExportActions(
    state: ScheduleExportUiState,
    onShareClick: () -> Unit,
    onSaveToGalleryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PrimaryButton(
            label = stringResource(R.string.schedule_export_share),
            onClick = onShareClick,
            enabled = state.isImageReady,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isGallerySaveAvailable) {
            OutlinedButton(
                onClick = onSaveToGalleryClick,
                enabled = state.isImageReady && !state.isSavingToGallery,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSavingToGallery) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = stringResource(R.string.schedule_export_save_to_gallery),
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun ScheduleExportScreenGeneratingPreview() {
    StrimupTheme {
        ScheduleExportScreen(
            state = ScheduleExportUiState(isGenerating = true, isGallerySaveAvailable = true),
            snackBarHostState = remember { SnackbarHostState() },
            onNavUp = {},
            onTemplateSelected = {},
            onRetryClick = {},
            onShareClick = {},
            onSaveToGalleryClick = {},
        )
    }
}

@Preview
@Composable
internal fun ScheduleExportScreenErrorPreview() {
    StrimupTheme {
        ScheduleExportScreen(
            state = ScheduleExportUiState(
                selectedTemplate = ScheduleExportTemplate.Pink,
                isGenerating = false,
                errorMessage = UiText.Resource(R.string.schedule_export_error),
            ),
            snackBarHostState = remember { SnackbarHostState() },
            onNavUp = {},
            onTemplateSelected = {},
            onRetryClick = {},
            onShareClick = {},
            onSaveToGalleryClick = {},
        )
    }
}

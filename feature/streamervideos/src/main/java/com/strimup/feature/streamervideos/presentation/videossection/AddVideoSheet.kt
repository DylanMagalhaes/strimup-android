package com.strimup.feature.streamervideos.presentation.videossection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.streamervideos.R
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy

private const val PERCENT = 100f
private const val DESCRIPTION_MIN_LINES = 3

internal data class AddVideoActions(
    val onModeChange: (AddVideoMode) -> Unit,
    val onPickFileClick: () -> Unit,
    val onUrlChange: (String) -> Unit,
    val onTitleChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onSubmitClick: () -> Unit,
    val onCancelUploadClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddVideoSheet(
    state: AddVideoUiState,
    actions: AddVideoActions,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUploading by rememberUpdatedState(state.isUploading)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { value -> value != SheetValue.Hidden || !isUploading },
        ),
        modifier = modifier,
    ) {
        AddVideoContent(state = state, actions = actions)
    }
}

@Composable
private fun AddVideoContent(
    state: AddVideoUiState,
    actions: AddVideoActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.videos_add_title),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
        )

        ModeTabs(
            mode = state.mode,
            enabled = !state.isUploading,
            onModeChange = actions.onModeChange,
        )

        when (state.mode) {
            AddVideoMode.FILE -> FileSource(
                file = state.file,
                enabled = !state.isUploading,
                onPickFileClick = actions.onPickFileClick,
            )

            AddVideoMode.LINK -> LinkSource(
                url = state.url,
                isInvalid = state.isLinkInvalid,
                enabled = !state.isUploading,
                onUrlChange = actions.onUrlChange,
            )
        }

        DetailsFields(state = state, actions = actions)

        state.errorMessage?.let { message ->
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        SubmitArea(state = state, actions = actions)
    }
}

@Composable
private fun DetailsFields(state: AddVideoUiState, actions: AddVideoActions) {
    OutlinedTextField(
        value = state.title,
        onValueChange = actions.onTitleChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isUploading,
        singleLine = true,
        label = { Text(text = stringResource(R.string.videos_add_title_label)) },
        supportingText = {
            Text(text = stringResource(R.string.videos_add_counter, state.title.length, VideoPolicy.MAX_TITLE_LENGTH))
        },
    )

    OutlinedTextField(
        value = state.description,
        onValueChange = actions.onDescriptionChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = !state.isUploading,
        minLines = DESCRIPTION_MIN_LINES,
        label = { Text(text = stringResource(R.string.videos_add_description_label)) },
        supportingText = {
            Text(
                text = stringResource(
                    R.string.videos_add_counter,
                    state.description.length,
                    VideoPolicy.MAX_DESCRIPTION_LENGTH,
                ),
            )
        },
    )
}

@Composable
private fun SubmitArea(state: AddVideoUiState, actions: AddVideoActions) {
    when {
        state.isUploading && state.mode == AddVideoMode.FILE -> UploadProgress(
            progressPercent = state.progressPercent,
            onCancelClick = actions.onCancelUploadClick,
        )

        else -> PrimaryButton(
            label = stringResource(
                if (state.isUploading) R.string.videos_add_link_in_progress else R.string.videos_add_submit,
            ),
            onClick = actions.onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isSubmitEnabled,
        )
    }
}

@Composable
private fun UploadProgress(
    progressPercent: Int?,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (progressPercent != null) {
            LinearProgressIndicator(
                progress = { progressPercent / PERCENT },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = stringResource(R.string.videos_upload_progress, progressPercent),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        } else {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Text(
            text = stringResource(R.string.videos_upload_keep_open),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.videos_upload_cancel),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private val previewActions = AddVideoActions(
    onModeChange = {},
    onPickFileClick = {},
    onUrlChange = {},
    onTitleChange = {},
    onDescriptionChange = {},
    onSubmitClick = {},
    onCancelUploadClick = {},
)

private val previewFile = LocalVideoFile(
    uri = "content://video/1",
    fileName = "best-of-gta-rp.mp4",
    mimeType = "video/mp4",
    sizeBytes = 42L * 1024 * 1024,
)

@Preview
@Composable
internal fun AddVideoContentPreview() {
    StrimupTheme {
        Surface {
            AddVideoContent(
                state = AddVideoUiState(file = previewFile, title = "Best of GTA RP"),
                actions = previewActions,
            )
        }
    }
}

@Preview
@Composable
internal fun AddVideoContentUploadingPreview() {
    StrimupTheme {
        Surface {
            AddVideoContent(
                state = AddVideoUiState(
                    file = previewFile,
                    title = "Best of GTA RP",
                    isUploading = true,
                    progressPercent = 42,
                ),
                actions = previewActions,
            )
        }
    }
}

@Preview
@Composable
internal fun AddVideoContentLinkPreview() {
    StrimupTheme {
        Surface {
            AddVideoContent(
                state = AddVideoUiState(
                    isOpen = true,
                    mode = AddVideoMode.LINK,
                    url = "https://vimeo.com/123",
                    title = "Mon meilleur clip",
                ),
                actions = previewActions,
            )
        }
    }
}

package com.strimup.feature.streamervideos.presentation.videossection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.streamervideos.R
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy
import java.util.Locale

private const val BYTES_PER_MEGABYTE = 1024.0 * 1024.0
private const val PERCENT = 100f
private const val DESCRIPTION_MIN_LINES = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddVideoSheet(
    state: AddVideoUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onUploadClick: () -> Unit,
    onCancelUploadClick: () -> Unit,
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
        AddVideoContent(
            state = state,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange,
            onUploadClick = onUploadClick,
            onCancelUploadClick = onCancelUploadClick,
        )
    }
}

@Composable
private fun AddVideoContent(
    state: AddVideoUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onUploadClick: () -> Unit,
    onCancelUploadClick: () -> Unit,
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
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.videos_add_title),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.videos_add_subtitle, VideoPolicy.MAX_FILE_SIZE_MEGABYTES),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        state.file?.let { file -> SelectedFileRow(file = file) }

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isUploading,
            singleLine = true,
            label = { Text(text = stringResource(R.string.videos_add_title_label)) },
            supportingText = {
                Text(
                    text = stringResource(
                        R.string.videos_add_counter,
                        state.title.length,
                        VideoPolicy.MAX_TITLE_LENGTH,
                    ),
                )
            },
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
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

        state.errorMessage?.let { message ->
            Text(
                text = message.asString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        if (state.isUploading) {
            UploadProgress(
                progressPercent = state.progressPercent,
                onCancelClick = onCancelUploadClick,
            )
        } else {
            PrimaryButton(
                label = stringResource(R.string.videos_add_submit),
                onClick = onUploadClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isSubmitEnabled,
            )
        }
    }
}

@Composable
private fun SelectedFileRow(
    file: LocalVideoFile,
    modifier: Modifier = Modifier,
) {
    val fileName = file.fileName ?: stringResource(R.string.videos_add_unnamed_file)
    val label = file.sizeBytes?.let { size ->
        val sizeMegabytes = String.format(Locale.FRANCE, "%.1f", size / BYTES_PER_MEGABYTE)
        stringResource(R.string.videos_add_file, fileName, sizeMegabytes)
    } ?: fileName

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Movie,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
                onTitleChange = {},
                onDescriptionChange = {},
                onUploadClick = {},
                onCancelUploadClick = {},
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
                onTitleChange = {},
                onDescriptionChange = {},
                onUploadClick = {},
                onCancelUploadClick = {},
            )
        }
    }
}

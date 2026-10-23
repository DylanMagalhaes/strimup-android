package com.strimup.feature.streamervideos.presentation.videossection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.strimup.feature.streamervideos.R
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy
import java.util.Locale

private const val BYTES_PER_MEGABYTE = 1024.0 * 1024.0

@Composable
internal fun ModeTabs(
    mode: AddVideoMode,
    enabled: Boolean,
    onModeChange: (AddVideoMode) -> Unit,
) {
    PrimaryTabRow(selectedTabIndex = mode.ordinal) {
        AddVideoMode.entries.forEach { tabMode ->
            Tab(
                selected = tabMode == mode,
                onClick = { onModeChange(tabMode) },
                enabled = enabled,
                text = {
                    Text(
                        text = stringResource(
                            when (tabMode) {
                                AddVideoMode.FILE -> R.string.videos_add_tab_file
                                AddVideoMode.LINK -> R.string.videos_add_tab_link
                            },
                        ),
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        }
    }
}

@Composable
internal fun FileSource(
    file: LocalVideoFile?,
    enabled: Boolean,
    onPickFileClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HelperText(text = stringResource(R.string.videos_add_subtitle, VideoPolicy.MAX_FILE_SIZE_MEGABYTES))
        if (file == null) {
            OutlinedButton(
                onClick = onPickFileClick,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.videos_add_pick_file), fontWeight = FontWeight.Bold)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SelectedFileRow(file = file, modifier = Modifier.weight(1f))
                TextButton(onClick = onPickFileClick, enabled = enabled) {
                    Text(text = stringResource(R.string.videos_add_change_file))
                }
            }
        }
    }
}

@Composable
internal fun LinkSource(
    url: String,
    isInvalid: Boolean,
    enabled: Boolean,
    onUrlChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HelperText(text = stringResource(R.string.videos_add_link_subtitle))
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            isError = isInvalid,
            label = { Text(text = stringResource(R.string.videos_add_link_label)) },
            placeholder = { Text(text = stringResource(R.string.videos_add_link_placeholder)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
            supportingText = if (isInvalid) {
                { Text(text = stringResource(R.string.videos_error_link)) }
            } else {
                null
            },
        )
    }
}

@Composable
private fun HelperText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
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
        modifier = modifier,
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

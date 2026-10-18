package com.strimup.feature.schedule.presentation.export

import android.content.ClipData
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.core.content.FileProvider
import com.strimup.feature.schedule.R
import kotlinx.coroutines.launch
import java.io.File

private const val PNG_MIME_TYPE = "image/png"
private const val FILE_PROVIDER_SUFFIX = ".fileprovider"

fun interface ScheduleImageSharer {
    operator fun invoke(file: File)
}

@Composable
fun rememberScheduleImageSharer(snackBarHostState: SnackbarHostState): ScheduleImageSharer {
    val context = LocalContext.current
    val resources = LocalResources.current
    val coroutineScope = rememberCoroutineScope()

    return remember(context, resources, snackBarHostState, coroutineScope) {
        ScheduleImageSharer { file ->
            runCatching {
                val uri = FileProvider.getUriForFile(context, context.packageName + FILE_PROVIDER_SUFFIX, file)
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = PNG_MIME_TYPE
                    putExtra(Intent.EXTRA_STREAM, uri)
                    clipData = ClipData.newRawUri(file.name, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(
                    Intent.createChooser(sendIntent, resources.getString(R.string.schedule_export_share_title)),
                )
            }.onFailure {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(resources.getString(R.string.schedule_export_share_error))
                }
            }
        }
    }
}

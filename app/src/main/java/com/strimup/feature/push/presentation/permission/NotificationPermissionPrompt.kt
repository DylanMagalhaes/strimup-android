package com.strimup.feature.push.presentation.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.strimup.R
import com.strimup.core.ui.theme.StrimupTheme

@Composable
fun NotificationPermissionPrompt(
    shouldAsk: Boolean,
    onHandled: () -> Unit,
) {
    if (!shouldAsk) return

    val context = LocalContext.current
    val isPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED

    if (!isPermissionRequired) {
        LaunchedEffect(Unit) { onHandled() }
        return
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { onHandled() },
    )

    NotificationPermissionDialog(
        onEnableClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
        onLaterClick = onHandled,
    )
}

@Composable
private fun NotificationPermissionDialog(
    onEnableClick: () -> Unit,
    onLaterClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onLaterClick,
        icon = { Icon(imageVector = Icons.Outlined.NotificationsActive, contentDescription = null) },
        title = { Text(text = stringResource(R.string.push_permission_title)) },
        text = { Text(text = stringResource(R.string.push_permission_message)) },
        confirmButton = {
            TextButton(onClick = onEnableClick) {
                Text(text = stringResource(R.string.push_permission_enable))
            }
        },
        dismissButton = {
            TextButton(onClick = onLaterClick) {
                Text(text = stringResource(R.string.push_permission_later))
            }
        },
    )
}

@Preview
@Composable
internal fun NotificationPermissionDialogPreview() {
    StrimupTheme {
        NotificationPermissionDialog(onEnableClick = {}, onLaterClick = {})
    }
}

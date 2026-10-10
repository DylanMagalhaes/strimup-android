package com.strimup.feature.notification.presentation.bell

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.feature.notification.R

@Composable
fun NotificationBell(
    unreadCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val badgeLabel = unreadBadgeLabel(unreadCount)
    val description = if (unreadCount > 0) {
        pluralStringResource(R.plurals.notifications_bell_unread, unreadCount, unreadCount)
    } else {
        stringResource(R.string.notifications_title)
    }

    IconButton(
        modifier = modifier.semantics { contentDescription = description },
        onClick = onClick,
    ) {
        BadgedBox(
            badge = {
                badgeLabel?.let { label ->
                    Badge { Text(text = label) }
                }
            },
        ) {
            Icon(
                imageVector = if (badgeLabel != null) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
internal fun NotificationBellPreview() {
    StrimupTheme {
        NotificationBell(unreadCount = 10, onClick = {})
    }
}

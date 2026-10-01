package com.strimup.feature.notification.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.R
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.feature.notification.domain.entity.Notification
import com.strimup.feature.notification.domain.entity.NotificationType
import com.strimup.feature.notification.presentation.time.toRelativeTimeUiText
import java.time.Instant

private const val UNREAD_BACKGROUND_ALPHA = 0.08f
private val PreviewNow: Instant = Instant.parse("2026-09-29T12:00:00Z")

@Composable
fun SwipeableNotificationItem(
    notification: Notification,
    now: Instant,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!notification.isDeletable) {
        NotificationItem(notification = notification, now = now, onClick = onClick, modifier = modifier)
        return
    }

    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        enableDismissFromStartToEnd = false,
        onDismiss = { value -> if (value == SwipeToDismissBoxValue.EndToStart) onDelete() },
        backgroundContent = { DeleteBackground() },
    ) {
        NotificationItem(notification = notification, now = now, onClick = onClick)
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    now: Instant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (notification.isRead) {
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.primary
            .copy(alpha = UNREAD_BACKGROUND_ALPHA)
            .compositeOver(MaterialTheme.colorScheme.background)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .background(
                    color = if (notification.isRead) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape,
                ),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            notification.createdAt?.let { createdAt ->
                Text(
                    text = createdAt.toRelativeTimeUiText(now = now).asString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DeleteBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.error)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = stringResource(R.string.notifications_delete),
            tint = MaterialTheme.colorScheme.onError,
        )
    }
}

@Preview
@Composable
internal fun NotificationItemPreview() {
    StrimupTheme {
        Column {
            NotificationItem(
                notification = Notification(
                    id = "1",
                    type = NotificationType.NewFavorite(fanId = "42", fanPseudo = "Inox"),
                    message = "Inox t'a ajouté à ses streamers favoris",
                    createdAt = Instant.parse("2026-09-29T11:55:00Z"),
                    isRead = false,
                ),
                now = PreviewNow,
                onClick = {},
            )
            NotificationItem(
                notification = Notification(
                    id = "2",
                    type = NotificationType.GlobalAnnouncement,
                    message = "Strimup fait peau neuve : découvre les nouveautés !",
                    createdAt = Instant.parse("2026-09-26T12:00:00Z"),
                    isRead = true,
                ),
                now = PreviewNow,
                onClick = {},
            )
        }
    }
}

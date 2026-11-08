package com.strimup.feature.cuts.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.core.ui.theme.RedLive
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.cuts.R
import com.strimup.feature.cuts.domain.entity.Cut

private const val SECONDARY_TEXT_ALPHA = 0.85f

@Composable
internal fun CutInfo(
    cut: Cut,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        CutAuthorRow(author = cut.author, onClick = onAuthorClick)

        if (cut.video.title.isNotBlank()) {
            Text(
                text = cut.video.title,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (cut.video.description.isNotBlank()) {
            Text(
                text = cut.video.description,
                color = Color.White.copy(alpha = SECONDARY_TEXT_ALPHA),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
internal fun CutAuthorRow(
    author: Cut.Author,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val openProfileLabel = stringResource(R.string.cuts_open_profile, author.username)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClickLabel = openProfileLabel, role = Role.Button, onClick = onClick)
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = author.avatarUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
        )
        Text(
            text = author.username,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (author.isVerified) {
            Icon(
                imageVector = Icons.Filled.Verified,
                contentDescription = stringResource(R.string.cuts_verified),
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
        if (author.isLive) {
            Text(
                text = stringResource(R.string.cuts_live),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(RedLive, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}

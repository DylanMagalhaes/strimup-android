package com.strimup.feature.home.presentation.hero

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.strimup.core.ui.component.video.StreamerVideoPreview
import com.strimup.core.ui.component.video.rememberStreamerVideoPreview
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.cuts.domain.entity.Cut
import com.strimup.feature.cuts.presentation.component.formatLikesCount
import com.strimup.feature.home.R
import com.strimup.feature.home.presentation.component.CUT_CARD_ASPECT_RATIO
import com.strimup.feature.home.presentation.component.CutCardWidth

private const val CARD_SCRIM_ALPHA = 0.7f
private val PillShape = RoundedCornerShape(percent = 50)
private val cardScrim = Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = CARD_SCRIM_ALPHA)))

@Composable
fun HomeCutsRow(
    cuts: List<Cut>,
    onCutClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_cuts_title),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.home_cuts_see_all),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(PillShape)
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items = cuts, key = { cut -> cut.id }) { cut ->
                HomeCutCard(cut = cut, onClick = { onCutClick(cut.id) })
            }
        }
    }
}

@Composable
private fun HomeCutCard(cut: Cut, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val preview = rememberStreamerVideoPreview(cut.video)

    Box(
        modifier = modifier
            .width(CutCardWidth)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        StreamerVideoPreview(
            preview = preview,
            isCompact = true,
            aspectRatio = CUT_CARD_ASPECT_RATIO,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(cardScrim)
                .padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AsyncImage(
                    model = cut.author.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray),
                )
                Text(
                    text = cut.author.username,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = formatLikesCount(cut.likesCount),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

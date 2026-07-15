package com.strimup.feature.streamerdetail.presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strimup.common.ui.component.button.SocialIconButton
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity
import com.strimup.feature.streamerdetail.presentation.mapper.getIconRes

@Composable
fun StreamerContent(
    description: String,
    socials: List<StreamerDetailEntity.Social>,
    onSocialClick: (StreamerDetailEntity.Social) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            if (socials.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    socials.forEach { social ->
                        SocialIconButton(
                            iconRes = social.getIconRes(),
                            onClick = { onSocialClick(social) }
                        )
                    }
                }
                VerticalSpacer(24.dp)
            }

            Text(
                text = "À propos",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )

            VerticalSpacer(8.dp)

            Text(
                text = description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .animateContentSize()
                    .clickable { isExpanded = !isExpanded }
            )

            if (description.length > 120) {
                Text(
                    text = if (isExpanded) "Voir moins" else "Lire la suite",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { isExpanded = !isExpanded }
                )
            }

            VerticalSpacer(24.dp)

        }
    }
}

@Preview
@Composable
private fun StreamerContentPreview() {
    StrimupTheme {
        StreamerContent(
            socials = listOf(
                StreamerDetailEntity.Social(
                    url = "https://twitch.tv",
                    type = StreamerDetailEntity.Social.Type.Twitch
                ),
                StreamerDetailEntity.Social(
                    url = "https://youtube.com",
                    type = StreamerDetailEntity.Social.Type.Youtube
                ),
                StreamerDetailEntity.Social(
                    url = "https://instagram.com",
                    type = StreamerDetailEntity.Social.Type.Instagram
                ),
                StreamerDetailEntity.Social(
                    url = "https://kick.com",
                    type = StreamerDetailEntity.Social.Type.Kick
                ),
            ),
            description = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting (Petit bonus si t'aimes t'enjailler en musique). Je partage également toutes mes activités (Création graphique, montage vidéo), session cinéma sur Discord, ainsi que montage Lego ou activités communautaires. Contact: moontsuki.pro@gmail.com",
            onSocialClick = {},
        )
    }
}
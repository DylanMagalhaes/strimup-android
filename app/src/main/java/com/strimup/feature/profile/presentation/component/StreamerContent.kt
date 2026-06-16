package com.strimup.feature.profile.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strimup.common.ui.component.button.SocialIconButton
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity
import com.strimup.feature.profile.presentation.mapper.getIconRes

@Composable
fun StreamerContent(
    description: String,
    socials: List<ProfileStreamerEntity.Social>,
    onSocialClick: (ProfileStreamerEntity.Social) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                socials.forEach { social ->
                    SocialIconButton(
                        iconRes = social.getIconRes(), onClick = { onSocialClick(social) })
                }
            }

            VerticalSpacer(16.dp)

            Text(
                text = "Description",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
            )

            VerticalSpacer(12.dp)

            Text(
                text = description,
                fontSize = 12.sp,
            )

            VerticalSpacer(16.dp)

            Text(
                text = "Video",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
            )

            VerticalSpacer(12.dp)

        }
    }
}

@Preview
@Composable
private fun StreamerContentPreview() {
    StrimupTheme {
        StreamerContent(
            socials = listOf(
                ProfileStreamerEntity.Social(
                    url = "",
                    type = ProfileStreamerEntity.Social.Type.Twitch
                ),
                ProfileStreamerEntity.Social(
                    url = "",
                    type = ProfileStreamerEntity.Social.Type.Youtube
                ),
                ProfileStreamerEntity.Social(
                    url = "",
                    type = ProfileStreamerEntity.Social.Type.Instagram
                ),
                ProfileStreamerEntity.Social(
                    url = "",
                    type = ProfileStreamerEntity.Social.Type.Kick
                ),
            ),
            description = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting (Petit bonus si t'aimes t'enjailler en musique). Je partage également toutes mes activités (Création graphique, montage vidéo), session cinéma sur Discord, ainsi que montage Lego ou activités communautaires. Contact: moontsuki.pro@gmail.com",
            onSocialClick = {},
        )
    }
}
package com.strimup.feature.profile.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily

@Composable
fun StreamerContent(
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Description",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
            )

            VerticalSpacer(12.dp)

            Text(text = description)

            VerticalSpacer(16.dp)

            Text(
                text = "Video",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
            )

            VerticalSpacer(12.dp)

            Text("TODO")
            // TODO: implementer video
        }
    }
}

@Preview
@Composable
private fun StreamerContentPreview() {
    StrimupTheme {
        StreamerContent(
            description = "Joueuse roleplay (Gtarp), multigaming et pas mal de sessions Just Chatting (Petit bonus si t'aimes t'enjailler en musique). Je partage également toutes mes activités (Création graphique, montage vidéo), session cinéma sur Discord, ainsi que montage Lego ou activités communautaires. Contact: moontsuki.pro@gmail.com"
        )
    }
}
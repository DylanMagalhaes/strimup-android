package com.strimup.core.ui.component.streamer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.theme.zalandoFontFamily

private const val PERSONALITY_DIVIDER_ALPHA = 0.12f

@Composable
internal fun PersonalityLine(
    personality: String?,
    secondaryPersonality: String?,
) {
    val first = personality?.takeIf { it.isNotBlank() }
    val second = secondaryPersonality?.takeIf { it.isNotBlank() }

    when {
        first != null && second != null -> PersonalityPair(first = first, second = second)
        first != null -> PersonalityLabel(text = first)
        second != null -> PersonalityLabel(text = second)
    }
}

@Composable
private fun PersonalityPair(
    first: String,
    second: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PersonalityLabel(text = first)

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = PERSONALITY_DIVIDER_ALPHA)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        PersonalityLabel(text = second)
    }
}

@Composable
private fun PersonalityLabel(
    text: String,
) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontFamily = zalandoFontFamily,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

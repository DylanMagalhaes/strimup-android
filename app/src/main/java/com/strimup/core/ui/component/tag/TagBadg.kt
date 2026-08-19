package com.strimup.core.ui.component.tag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.component.spacer.VerticalSpacer
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable
fun TagBadge(
    tag: String,
    isSelected: Boolean = false,
    onTagClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val hashtagColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = onTagClick,
        modifier = modifier
    ) {
        Badge(
            containerColor = containerColor
        ) {
            Row(modifier = Modifier.padding(2.dp)) {
                Text(
                    text = "#",
                    color = hashtagColor,
                    fontFamily = zalandoFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tag,
                    color = textColor,
                    fontFamily = zalandoFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun TagBadgePreview() {
    StrimupTheme {
        Surface {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    TagBadge(
                        isSelected = true,
                        tag = "Gaming"
                    )

                    VerticalSpacer(8.dp)

                    TagBadge(
                        isSelected = false,
                        tag = "Gaming"
                    )
                }
            }
        }
    }
}
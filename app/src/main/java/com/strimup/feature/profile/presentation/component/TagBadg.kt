package com.strimup.feature.profile.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import org.w3c.dom.Text

@Composable
fun TagBadge(
    tag: String,
    modifier: Modifier = Modifier
) {
    Badge(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(modifier = Modifier.padding(2.dp)) {
            Text(
                text = "#",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,

            )
            Text(
                text = "$tag",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                )
        }
    }
}

@Preview
@Composable
private fun TagBadgPreview() {
    StrimupTheme {

        TagBadge(
            tag = "Gamming"
        )


    }
}
package com.strimup.feature.filter.presentation.list.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.theme.zalandoFontFamily

@Composable
fun FilterBadge(
    label: String,
    modifier: Modifier = Modifier
) {
    Badge(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Row(modifier = Modifier.padding(2.dp)) {
            Text(
                text = label,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        }
    }

}
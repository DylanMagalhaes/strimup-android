package com.strimup.feature.streamerprofile.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleSelectBottomSheet(
    title: String,
    options: List<String>,
    selectedOptions: List<String>?,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss
    ) {
        MultipleSelectContent(
            title = title,
            options = options,
            selectedOptions = selectedOptions,
            onOptionSelected = onOptionSelected,
            onDone = onDismiss
        )
    }
}

@Composable
fun MultipleSelectContent(
    title: String,
    options: List<String>,
    selectedOptions: List<String>?,
    onOptionSelected: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold
        )

        VerticalSpacer(8.dp)
        Text(
            text = "Plusieurs choix possibles",
            style = MaterialTheme.typography.titleSmall,
            fontFamily = zalandoFontFamily,
            fontStyle = FontStyle.Italic,
            color = Color.Gray
        )

        VerticalSpacer(16.dp)

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(options) { option ->
                val isSelected = selectedOptions?.contains(option) == true

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(option) }
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )

                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onOptionSelected(option) }
                    )
                }
            }
        }

        VerticalSpacer(16.dp)

        OutlinedButton(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary // 👈 Adapté pour contraster avec le fond 'primary'
            )
        ) {
            Text(
                text = "Terminer",
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleSelectBottomSheetPreview() {
    StrimupTheme {
        Surface {
            MultipleSelectContent(
                title = "Langues parlées",
                options = listOf(
                    "Français",
                    "Anglais",
                    "Portugais",
                ),
                selectedOptions = listOf("Français", "Portugais"),
                onOptionSelected = {},
                onDone = {}
            )
        }
    }
}
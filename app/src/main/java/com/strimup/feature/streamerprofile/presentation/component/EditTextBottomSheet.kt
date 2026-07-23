package com.strimup.feature.streamerprofile.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun EditTextBottomSheet(
    title: String,
    description: String?,
    currentText: String,
    onDone: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
    ) {
        EditBioContent(
            currentBio = currentText,
            onSave = onDone,
            title = title,
            description = description
        )
    }
}

@Composable
fun EditBioContent(
    title: String,
    description: String?,
    currentBio: String,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(currentBio) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .imePadding()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold
        )

        if (!description.isNullOrBlank()) {
            VerticalSpacer(8.dp)
            Text(
                text = description,
                style = MaterialTheme.typography.titleSmall,
                fontFamily = zalandoFontFamily,
                fontStyle = FontStyle.Italic,
                color = Color.Gray
            )
        }

        VerticalSpacer(16.dp)

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6
        )

        VerticalSpacer(16.dp)

        OutlinedButton(
            onClick = { onSave(text) },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
fun EditBioBottomSheetPreview() {
    StrimupTheme {
        Surface {
            EditBioContent(
                currentBio = "Joueuse roleplay (Gtarp), multigaming et Just Chatting...",
                onSave = {},
                title = "Modifier la bio",
                description = "lorem ipsum dolores"
            )
        }
    }
}
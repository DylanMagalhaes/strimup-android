package com.strimup.feature.streamerprofile.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBioBottomSheet(
    currentBio: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
    ) {
        EditBioContent(
            currentBio = currentBio,
            onSave = onSave
        )
    }
}

@Composable
fun EditBioContent(
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
            text = "Modifier la bio",
            style = MaterialTheme.typography.titleMedium,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold
        )

        VerticalSpacer(16.dp)

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 6
        )

        VerticalSpacer(16.dp)

        Button(
            onClick = { onSave(text) },
            modifier = Modifier.fillMaxWidth()
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
                onSave = {}
            )
        }
    }
}
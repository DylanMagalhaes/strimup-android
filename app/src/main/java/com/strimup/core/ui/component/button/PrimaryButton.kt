package com.strimup.core.ui.component.button

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable
fun PrimaryButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            text = label,
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    StrimupTheme {
        PrimaryButton(
            onClick = {},
            label = "Entrer",
        )
    }
}

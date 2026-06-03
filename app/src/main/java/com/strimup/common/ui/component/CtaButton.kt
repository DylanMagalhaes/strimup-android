package com.strimup.common.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.strimup.common.ui.theme.zalandoFontFamily
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import com.strimup.common.ui.theme.StrimupTheme

@Composable
fun CtaButton(
    label : String,
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,

    ) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onButtonClick,
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
private fun CtaButtonPreview() {
    StrimupTheme {
        CtaButton(
            onButtonClick = {},
            label = "Entrer",
        )
    }
}

package com.strimup.common.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.theme.StrimupTheme

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean,
) {
    val customTextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,

        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        errorIndicatorColor = MaterialTheme.colorScheme.error,

        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        errorLabelColor = MaterialTheme.colorScheme.error,

        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = customTextFieldColors,
        singleLine = true,
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else {
            KeyboardOptions.Default
        },
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }
    )
}

@Preview
@Composable
private fun CustomTextFieldPreview() {
    StrimupTheme {
        CustomTextField(
            value = "",
            onValueChange = {},
            label = "Mot de passe",
            isPassword = true,

        )
    }
}
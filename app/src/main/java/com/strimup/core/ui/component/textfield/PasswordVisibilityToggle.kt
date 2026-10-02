package com.strimup.core.ui.component.textfield

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.strimup.R

@Composable
fun PasswordVisibilityToggle(
    isVisible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(modifier = modifier, onClick = { onVisibleChange(!isVisible) }) {
        Icon(
            imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = stringResource(if (isVisible) R.string.password_hide else R.string.password_show),
        )
    }
}

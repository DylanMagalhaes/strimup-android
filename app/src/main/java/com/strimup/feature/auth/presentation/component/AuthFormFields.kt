package com.strimup.feature.auth.presentation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.strimup.core.ui.component.textfield.StrimupTextField
import java.time.Instant
import java.time.ZoneOffset

/**
 * Formats a [DatePicker]-selected UTC epoch millis timestamp into an ISO-8601
 * date string ("yyyy-MM-dd"), matching the API's expected birth_date format.
 */
fun formatBirthDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).toLocalDate().toString()

/**
 * Read-only text field paired with a [DatePickerDialog], shared by every auth
 * screen collecting a birth date (register, OAuth onboarding...).
 */
@Composable
fun AuthDateField(
    dateTextValue: String,
    datePickerState: DatePickerState,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDateSelected: (String) -> Unit,
    errorText: String? = null,
) {
    Box {
        StrimupTextField(
            value = dateTextValue,
            onValueChange = {},
            label = "Date de naissance",
            errorText = errorText,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            },
        )

        ClickableFieldOverlay(
            onClickLabel = "Sélectionner la date",
            role = Role.Button,
            onClick = { onExpandedChange(!isExpanded) },
        )
    }

    if (!isExpanded) return

    DatePickerDialog(
        onDismissRequest = { onExpandedChange(false) },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(formatBirthDate(millis))
                    }
                    onExpandedChange(false)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onExpandedChange(false) }) {
                Text("Annuler")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
    }
}

/**
 * Read-only text field paired with a [DropdownMenu], shared by every auth
 * screen collecting a value from a closed set of options (gender, role...).
 */
@Composable
fun <T> AuthDropdownField(
    label: String,
    @StringRes selectedLabelRes: Int?,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<T>,
    optionLabelRes: (T) -> Int,
    onOptionSelected: (T) -> Unit,
    contentDescription: String,
    errorText: String? = null,
) {
    Box {
        StrimupTextField(
            value = selectedLabelRes?.let { stringResource(it) } ?: "",
            onValueChange = {},
            label = label,
            errorText = errorText,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
        )

        ClickableFieldOverlay(
            onClickLabel = contentDescription,
            role = Role.DropdownList,
            onClick = { onExpandedChange(!isExpanded) },
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(optionLabelRes(option))) },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

/**
 * Transparent layer covering a read-only field: a tap anywhere on it triggers [onClick]
 * instead of focusing the underlying text field (no cursor, no keyboard).
 */
@Composable
private fun BoxScope.ClickableFieldOverlay(
    onClickLabel: String,
    role: Role,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = onClickLabel,
                role = role,
                onClick = onClick,
            )
    )
}

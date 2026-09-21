package com.strimup.feature.auth.presentation.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
) {
    StrimupTextField(
        value = dateTextValue,
        onValueChange = {},
        label = "Date de naissance",
        trailingIcon = {
            IconButton(onClick = { onExpandedChange(!isExpanded) }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Sélectionner la date"
                )
            }
        },
    )

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
) {
    Box {
        StrimupTextField(
            value = selectedLabelRes?.let { stringResource(it) } ?: "",
            onValueChange = {},
            label = label,
            trailingIcon = {
                IconButton(onClick = { onExpandedChange(!isExpanded) }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = contentDescription
                    )
                }
            },
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

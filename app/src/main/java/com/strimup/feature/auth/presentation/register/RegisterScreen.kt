package com.strimup.feature.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.component.button.PrimaryButton
import com.strimup.common.ui.component.spacer.VerticalSpacer
import com.strimup.common.ui.component.textfield.StrimupTextField
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    pseudoValue: String,
    onPseudoChange: (String) -> Unit,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    dateTextValue: String,
    onDateTextValueChange: (String) -> Unit,
    sexValue: String,
    onSexValueChange: (String) -> Unit,
    datePickerState: DatePickerState,
    isDateDropDownExpended: Boolean,
    onDateDropDownExpendedChange: (Boolean) -> Unit,
    isSexDropDownExpended: Boolean,
    onSexDropDownExpendedChange: (Boolean) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    confirmPasswordValue: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    isConfirmPasswordVisible: Boolean,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = zalandoFontFamily,
                    style = MaterialTheme.typography.headlineLarge,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    text = buildAnnotatedString {
                        append("Creer un compte")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(".")
                        }
                    },
                )

                StrimupTextField(
                    value = pseudoValue,
                    onValueChange = onPseudoChange,
                    label = "Pseudo",
                )

                StrimupTextField(
                    value = emailValue,
                    onValueChange = onEmailChange,
                    label = "email",
                )

                Box {
                    StrimupTextField(
                        value = dateTextValue,
                        onValueChange = onDateTextValueChange,
                        label = "Date de naissance",
                        trailingIcon = {
                            IconButton(onClick = { onDateDropDownExpendedChange(!isDateDropDownExpended) }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Sélectionner la date"
                                )
                            }
                        },

                        )
                    DropdownMenu(
                        expanded = isDateDropDownExpended,
                        onDismissRequest = { onDateDropDownExpendedChange(false) }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false
                        )
                    }
                }

                Box {
                    StrimupTextField(
                        value = sexValue,
                        onValueChange = onSexValueChange,
                        label = "Sexe",
                        trailingIcon = {
                            IconButton(onClick = { onSexDropDownExpendedChange(!isSexDropDownExpended) }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Sélectionner votre sexe"
                                )
                            }
                        },
                    )

                    DropdownMenu(
                        expanded = isSexDropDownExpended,
                        onDismissRequest = { onSexDropDownExpendedChange(false) }
                    ) {
                        TODO()
                    }
                }

                StrimupTextField(
                    value = passwordValue,
                    onValueChange = onPasswordChange,
                    label = "Mot de passe",
                    isPassword = isPasswordVisible,
                    trailingIcon = {
                        IconButton(onClick = { onPasswordVisibleChange(!isPasswordVisible) }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Cacher le mot de passe" else "Montrer le mot de passe"
                            )
                        }
                    }
                )

                StrimupTextField(
                    value = confirmPasswordValue,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirmation",
                    isPassword = isConfirmPasswordVisible,
                    trailingIcon = {
                        IconButton(onClick = { onConfirmPasswordVisibleChange(!isConfirmPasswordVisible) }) {
                            Icon(
                                imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isConfirmPasswordVisible) "Cacher le mot de passe" else "Montrer le mot de passe"
                            )
                        }
                    }
                )

                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = "S'inscrire",
                    onClick = {},
                )

            }

            VerticalSpacer(8.dp)

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun RegisterScreenPreview() {
    StrimupTheme {
        RegisterScreen(
            modifier = Modifier.fillMaxSize(),
            pseudoValue = "Dylan",
            onPseudoChange = {},
            emailValue = "dylan@strimup.com",
            onEmailChange = {},
            dateTextValue = "05/05/1995",
            datePickerState = rememberDatePickerState(),
            isDateDropDownExpended = false,
            onDateDropDownExpendedChange = {},
            isSexDropDownExpended = false,
            onSexDropDownExpendedChange = {},
            sexValue = "Homme",
            onSexValueChange = {},
            onDateTextValueChange = {},
            passwordValue = "",
            onPasswordChange = {},
            confirmPasswordValue = "",
            onConfirmPasswordChange = {},
            isPasswordVisible = true,
            isConfirmPasswordVisible = true,
            onPasswordVisibleChange = {},
            onConfirmPasswordVisibleChange = {},
        )
    }
}
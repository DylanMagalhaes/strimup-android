package com.strimup.feature.auth.presentation.register

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.component.textfield.StrimupTextField
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.core.ui.user.toLabelRes
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.PasswordCheck
import com.strimup.feature.auth.presentation.toChecklistLabelRes
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavToHome: () -> Unit,
    onNavToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is RegisterUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }

                RegisterUiEvent.ShowHomeUi -> {
                    onNavToHome()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        RegisterContent(
            modifier = Modifier.padding(padding),
            state = state,
            pseudoValue = state.pseudoInput,
            onPseudoChange = viewModel::onPseudoChange,
            emailValue = state.emailInput,
            onEmailChange = viewModel::onEmailChange,
            dateTextValue = state.birthDateInput,
            onDateTextValueChange = viewModel::onBirthDateChange,
            sexValue = state.genderInput,
            onSexValueChange = viewModel::onGenderChange,
            roleValue = state.roleInput,
            onRoleValueChange = viewModel::onRoleChange,
            datePickerState = datePickerState,
            isDateDropDownExpended = state.isDateDropDownExpended,
            onDateDropDownExpendedChange = {
                viewModel.onDropdownExpendedChange(RegisterDropdown.DATE, it)
            },
            isSexDropDownExpended = state.isSexDropDownExpended,
            onSexDropDownExpendedChange = {
                viewModel.onDropdownExpendedChange(RegisterDropdown.SEX, it)
            },
            isRoleDropDownExpended = state.isRoleDropDownExpended,
            onRoleDropDownExpendedChange = {
                viewModel.onDropdownExpendedChange(RegisterDropdown.ROLE, it)
            },
            passwordValue = state.passwordInput,
            onPasswordChange = viewModel::onPasswordChange,
            confirmPasswordValue = state.confirmPasswordInput,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            isPasswordVisible = state.isPasswordVisible,
            onPasswordVisibleChange = {
                viewModel.onPasswordVisibleChange(RegisterPasswordField.PASSWORD, it)
            },
            isConfirmPasswordVisible = state.isConfirmPasswordVisible,
            onConfirmPasswordVisibleChange = {
                viewModel.onPasswordVisibleChange(RegisterPasswordField.CONFIRM_PASSWORD, it)
            },
            onRegisterClick = viewModel::onRegisterButtonClick,
            onLoginClick = onNavToLogin,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    state: RegisterUiState,
    pseudoValue: String,
    onPseudoChange: (String) -> Unit,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    dateTextValue: String,
    onDateTextValueChange: (String) -> Unit,
    sexValue: Gender?,
    onSexValueChange: (Gender) -> Unit,
    roleValue: UserRole?,
    onRoleValueChange: (UserRole) -> Unit,
    datePickerState: DatePickerState,
    isDateDropDownExpended: Boolean,
    onDateDropDownExpendedChange: (Boolean) -> Unit,
    isSexDropDownExpended: Boolean,
    onSexDropDownExpendedChange: (Boolean) -> Unit,
    isRoleDropDownExpended: Boolean,
    onRoleDropDownExpendedChange: (Boolean) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    confirmPasswordValue: String,
    onConfirmPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    isConfirmPasswordVisible: Boolean,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    modifier = Modifier
                        .padding(top = 24.dp, bottom = 16.dp)
                        .size(96.dp),
                    painter = painterResource(R.drawable.ic_strimup),
                    contentDescription = "Strimup icon",
                )

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

                RegisterDateField(
                    dateTextValue = dateTextValue,
                    datePickerState = datePickerState,
                    isExpanded = isDateDropDownExpended,
                    onExpandedChange = onDateDropDownExpendedChange,
                    onDateSelected = onDateTextValueChange,
                )

                RegisterDropdownField(
                    label = "Sexe",
                    selectedLabelRes = sexValue?.toLabelRes(),
                    isExpanded = isSexDropDownExpended,
                    onExpandedChange = onSexDropDownExpendedChange,
                    options = Gender.entries,
                    optionLabelRes = { it.toLabelRes() },
                    onOptionSelected = onSexValueChange,
                    contentDescription = "Sélectionner votre sexe",
                )

                RegisterDropdownField(
                    label = "Je suis un(e)",
                    selectedLabelRes = roleValue?.toLabelRes(),
                    isExpanded = isRoleDropDownExpended,
                    onExpandedChange = onRoleDropDownExpendedChange,
                    options = listOf(UserRole.VIEWER, UserRole.STREAMER),
                    optionLabelRes = { it.toLabelRes() },
                    onOptionSelected = onRoleValueChange,
                    contentDescription = "Sélectionner votre profil",
                )

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

                PasswordChecklist(
                    checks = state.passwordChecklist,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                state.passwordErrorRes?.let { errorRes ->
                    Text(
                        text = stringResource(errorRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

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
                    label = if (state.isLoading) "Inscription en cours..." else "S'inscrire",
                    onClick = onRegisterClick,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Déjà un compte ? ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    )
                    Text(
                        modifier = Modifier.clickable(onClick = onLoginClick),
                        text = "Se connecter",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/**
 * Formats a [DatePicker]-selected UTC epoch millis timestamp into an ISO-8601
 * date string ("yyyy-MM-dd"), matching the API's expected birth_date format.
 */
private fun formatBirthDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC).toLocalDate().toString()

@Composable
private fun RegisterDateField(
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

@Composable
private fun <T> RegisterDropdownField(
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

@Composable
private fun PasswordChecklist(
    checks: List<PasswordCheck>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        checks.forEach { check ->
            val color = if (check.isSatisfied) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (check.isSatisfied) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(14.dp)
                )
                Text(
                    text = stringResource(check.requirement.toChecklistLabelRes()),
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun RegisterContentPreview() {
    StrimupTheme {
        RegisterContent(
            modifier = Modifier.fillMaxSize(),
            state = RegisterUiState(),
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
            sexValue = Gender.MALE,
            onSexValueChange = {},
            isRoleDropDownExpended = false,
            onRoleDropDownExpendedChange = {},
            roleValue = UserRole.VIEWER,
            onRoleValueChange = {},
            onDateTextValueChange = {},
            passwordValue = "",
            onPasswordChange = {},
            confirmPasswordValue = "",
            onConfirmPasswordChange = {},
            isPasswordVisible = true,
            isConfirmPasswordVisible = true,
            onPasswordVisibleChange = {},
            onConfirmPasswordVisibleChange = {},
            onRegisterClick = {},
            onLoginClick = {},
        )
    }
}

package com.strimup.feature.auth.presentation.register

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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import com.strimup.core.ui.browser.openInCustomTab
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.component.textfield.PasswordVisibilityToggle
import com.strimup.core.ui.component.textfield.StrimupTextField
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.core.ui.user.toLabelRes
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.domain.PasswordCheck
import com.strimup.feature.auth.presentation.component.AuthDateField
import com.strimup.feature.auth.presentation.component.AuthDropdownField
import com.strimup.feature.auth.presentation.component.AuthLegalText
import com.strimup.feature.auth.presentation.component.AuthOAuthSection
import com.strimup.feature.auth.presentation.toChecklistLabelRes

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
    val context = LocalContext.current
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

                is RegisterUiEvent.OpenCustomTab -> {
                    context.openInCustomTab(event.url)
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
            isTermsAccepted = state.isTermsAccepted,
            onTermsAcceptedChange = viewModel::onTermsAcceptedChange,
            onRegisterClick = viewModel::onRegisterButtonClick,
            onLoginClick = onNavToLogin,
            onTwitchLoginClick = viewModel::onTwitchLoginClick,
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
    isTermsAccepted: Boolean,
    onTermsAcceptedChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onTwitchLoginClick: () -> Unit,
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
                        .padding(top = 8.dp, bottom = 4.dp)
                        .size(56.dp),
                    painter = painterResource(R.drawable.ic_strimup),
                    contentDescription = stringResource(R.string.auth_logo_description),
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = zalandoFontFamily,
                    style = MaterialTheme.typography.headlineMedium,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    text = buildAnnotatedString {
                        append(stringResource(R.string.register_title))
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(".")
                        }
                    },
                )

                StrimupTextField(
                    value = pseudoValue,
                    onValueChange = onPseudoChange,
                    label = stringResource(R.string.register_pseudo_label),
                    errorText = state.pseudoError?.asString(),
                )

                StrimupTextField(
                    value = emailValue,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.auth_email_label),
                    errorText = state.emailError?.asString(),
                )

                AuthDateField(
                    dateTextValue = dateTextValue,
                    datePickerState = datePickerState,
                    isExpanded = isDateDropDownExpended,
                    onExpandedChange = onDateDropDownExpendedChange,
                    onDateSelected = onDateTextValueChange,
                    errorText = state.birthDateError?.asString(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        AuthDropdownField(
                            label = stringResource(R.string.auth_gender_label),
                            selectedLabelRes = sexValue?.toLabelRes(),
                            isExpanded = isSexDropDownExpended,
                            onExpandedChange = onSexDropDownExpendedChange,
                            options = Gender.entries,
                            optionLabelRes = { it.toLabelRes() },
                            onOptionSelected = onSexValueChange,
                            contentDescription = stringResource(R.string.auth_gender_pick),
                            errorText = state.genderError?.asString(),
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        AuthDropdownField(
                            label = stringResource(R.string.auth_role_label),
                            selectedLabelRes = roleValue?.toLabelRes(),
                            isExpanded = isRoleDropDownExpended,
                            onExpandedChange = onRoleDropDownExpendedChange,
                            options = listOf(UserRole.VIEWER, UserRole.STREAMER),
                            optionLabelRes = { it.toLabelRes() },
                            onOptionSelected = onRoleValueChange,
                            contentDescription = stringResource(R.string.auth_role_pick),
                            errorText = state.roleError?.asString(),
                        )
                    }
                }

                StrimupTextField(
                    value = passwordValue,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.auth_password_label),
                    isPassword = !isPasswordVisible,
                    errorText = state.passwordError?.asString(),
                    trailingIcon = {
                        PasswordVisibilityToggle(
                            isVisible = isPasswordVisible,
                            onVisibleChange = onPasswordVisibleChange,
                        )
                    }
                )

                PasswordChecklist(
                    checks = state.passwordChecklist,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                StrimupTextField(
                    value = confirmPasswordValue,
                    onValueChange = onConfirmPasswordChange,
                    label = stringResource(R.string.register_password_confirmation_label),
                    isPassword = !isConfirmPasswordVisible,
                    errorText = state.confirmPasswordError?.asString(),
                    trailingIcon = {
                        PasswordVisibilityToggle(
                            isVisible = isConfirmPasswordVisible,
                            onVisibleChange = onConfirmPasswordVisibleChange,
                        )
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .toggleable(
                            value = isTermsAccepted,
                            role = Role.Checkbox,
                            onValueChange = onTermsAcceptedChange,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = isTermsAccepted,
                        onCheckedChange = null,
                    )
                    AuthLegalText(
                        modifier = Modifier.padding(start = 12.dp),
                        prefixRes = R.string.register_legal_prefix,
                    )
                }

                state.termsError?.let { error ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = error.asString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                state.errorMessage?.let { message ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        text = message.asString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(
                        if (state.isLoading) R.string.register_submit_loading else R.string.register_submit
                    ),
                    enabled = !state.isLoading,
                    onClick = onRegisterClick,
                )

                AuthOAuthSection(onTwitchClick = onTwitchLoginClick)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.register_has_account) + " ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    )
                    Text(
                        modifier = Modifier.clickable(onClick = onLoginClick),
                        text = stringResource(R.string.register_login_link),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
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
            isTermsAccepted = false,
            onTermsAcceptedChange = {},
            onRegisterClick = {},
            onLoginClick = {},
            onTwitchLoginClick = {},
        )
    }
}

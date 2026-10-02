package com.strimup.feature.auth.presentation.oauthonboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePickerState
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.core.ui.user.toLabelRes
import com.strimup.core.user.domain.entity.Gender
import com.strimup.core.user.domain.entity.UserRole
import com.strimup.feature.auth.presentation.component.AuthDateField
import com.strimup.feature.auth.presentation.component.AuthDropdownField
import com.strimup.feature.auth.presentation.component.AuthLegalText

@Composable
fun OAuthOnboardingScreen(
    tmp: String,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OAuthOnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is OAuthOnboardingUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }

                OAuthOnboardingUiEvent.Completed -> {
                    onCompleted()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        OAuthOnboardingContent(
            modifier = Modifier.padding(padding),
            state = state,
            datePickerState = datePickerState,
            onDateChange = viewModel::onBirthDateChange,
            onDateDropDownExpendedChange = {
                viewModel.onDropdownExpendedChange(OAuthOnboardingDropdown.DATE, it)
            },
            onGenderChange = viewModel::onGenderChange,
            onSexDropDownExpendedChange = {
                viewModel.onDropdownExpendedChange(OAuthOnboardingDropdown.SEX, it)
            },
            onRoleChange = viewModel::onRoleChange,
            onRoleDropDownExpendedChange = {
                viewModel.onDropdownExpendedChange(OAuthOnboardingDropdown.ROLE, it)
            },
            onSubmitClick = { viewModel.onSubmitClick(tmp) },
        )
    }
}

@Composable
fun OAuthOnboardingContent(
    state: OAuthOnboardingUiState,
    datePickerState: DatePickerState,
    onDateChange: (String) -> Unit,
    onDateDropDownExpendedChange: (Boolean) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onSexDropDownExpendedChange: (Boolean) -> Unit,
    onRoleChange: (UserRole) -> Unit,
    onRoleDropDownExpendedChange: (Boolean) -> Unit,
    onSubmitClick: () -> Unit,
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
                    contentDescription = stringResource(R.string.auth_logo_description),
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
                    text = stringResource(R.string.oauth_onboarding_title),
                )

                AuthDateField(
                    dateTextValue = state.birthDateInput,
                    datePickerState = datePickerState,
                    isExpanded = state.isDateDropDownExpended,
                    onExpandedChange = onDateDropDownExpendedChange,
                    onDateSelected = onDateChange,
                )

                AuthDropdownField(
                    label = stringResource(R.string.auth_gender_label),
                    selectedLabelRes = state.genderInput?.toLabelRes(),
                    isExpanded = state.isSexDropDownExpended,
                    onExpandedChange = onSexDropDownExpendedChange,
                    options = Gender.entries,
                    optionLabelRes = { it.toLabelRes() },
                    onOptionSelected = onGenderChange,
                    contentDescription = stringResource(R.string.auth_gender_pick),
                )

                AuthDropdownField(
                    label = stringResource(R.string.auth_role_label),
                    selectedLabelRes = state.roleInput?.toLabelRes(),
                    isExpanded = state.isRoleDropDownExpended,
                    onExpandedChange = onRoleDropDownExpendedChange,
                    options = listOf(UserRole.VIEWER, UserRole.STREAMER),
                    optionLabelRes = { it.toLabelRes() },
                    onOptionSelected = onRoleChange,
                    contentDescription = stringResource(R.string.auth_role_pick),
                )

                AuthLegalText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    prefixRes = R.string.oauth_onboarding_legal_prefix,
                    textAlign = TextAlign.Center,
                )

                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = if (state.isLoading) {
                        stringResource(R.string.oauth_onboarding_submit_loading)
                    } else {
                        stringResource(R.string.oauth_onboarding_submit)
                    },
                    onClick = onSubmitClick,
                )
            }
        }
    }
}

@Preview
@Composable
private fun OAuthOnboardingContentPreview() {
    StrimupTheme {
        OAuthOnboardingContent(
            modifier = Modifier.fillMaxSize(),
            state = OAuthOnboardingUiState(),
            datePickerState = rememberDatePickerState(),
            onDateChange = {},
            onDateDropDownExpendedChange = {},
            onGenderChange = {},
            onSexDropDownExpendedChange = {},
            onRoleChange = {},
            onRoleDropDownExpendedChange = {},
            onSubmitClick = {},
        )
    }
}

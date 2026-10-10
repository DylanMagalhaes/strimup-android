package com.strimup.feature.account.presentation.deletion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.core.ui.component.button.DangerButton
import com.strimup.core.ui.component.error.ErrorState
import com.strimup.core.ui.component.textfield.PasswordVisibilityToggle
import com.strimup.core.ui.component.textfield.StrimupTextField
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.text.asString
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.account.R
import com.strimup.feature.account.domain.entity.AccountDeletionConfirmation
import com.strimup.feature.account.domain.entity.AccountDeletionPolicy

@Composable
fun DeleteAccountScreen(
    onNavUp: () -> Unit,
    onAccountDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DeleteAccountViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DeleteAccountUiEvent.AccountDeleted -> onAccountDeleted()
            }
        }
    }

    BackHandler(enabled = state.isDeleting) {}

    DeleteAccountContent(
        modifier = modifier,
        state = state,
        onNavUp = onNavUp,
        onRetryClick = viewModel::onRetryClick,
        onConfirmationChange = viewModel::onConfirmationChange,
        onPasswordChange = viewModel::onPasswordChange,
        onPasswordVisibleChange = viewModel::onPasswordVisibleChange,
        onDeleteClick = viewModel::onDeleteClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountContent(
    state: DeleteAccountUiState,
    onNavUp: () -> Unit,
    onRetryClick: () -> Unit,
    onConfirmationChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.account_deletion_title),
                        fontFamily = zalandoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavUp, enabled = !state.isDeleting) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.account_nav_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()

        when {
            state.isPolicyLoading -> {
                Box(modifier = contentModifier, contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.policyErrorRes != null -> {
                ErrorState(
                    modifier = contentModifier,
                    messageRes = state.policyErrorRes,
                    onRetryClick = onRetryClick,
                )
            }

            else -> {
                DeleteAccountForm(
                    modifier = contentModifier,
                    state = state,
                    onConfirmationChange = onConfirmationChange,
                    onPasswordChange = onPasswordChange,
                    onPasswordVisibleChange = onPasswordVisibleChange,
                    onDeleteClick = onDeleteClick,
                )
            }
        }
    }
}

@Composable
private fun DeleteAccountForm(
    state: DeleteAccountUiState,
    onConfirmationChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DeletionWarningCard()

        Column {
            ConfirmationInstruction()

            StrimupTextField(
                modifier = Modifier.padding(top = 12.dp),
                value = state.confirmationInput,
                onValueChange = onConfirmationChange,
                label = stringResource(R.string.account_deletion_confirmation_label),
                enabled = !state.isDeleting,
                capitalization = KeyboardCapitalization.Characters,
            )

            if (state.isPasswordRequired) {
                Text(
                    text = stringResource(R.string.account_deletion_password_instruction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                StrimupTextField(
                    modifier = Modifier.padding(top = 12.dp),
                    value = state.passwordInput,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.account_deletion_password_label),
                    isPassword = !state.isPasswordVisible,
                    enabled = !state.isDeleting,
                    trailingIcon = {
                        PasswordVisibilityToggle(
                            isVisible = state.isPasswordVisible,
                            onVisibleChange = onPasswordVisibleChange,
                        )
                    },
                )
            }

            state.errorMessage?.let { message ->
                Text(
                    text = message.asString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        DangerButton(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.account_deletion_submit),
            enabled = state.isSubmitEnabled,
            isLoading = state.isDeleting,
            onClick = onDeleteClick,
        )
    }
}

@Composable
private fun DeletionWarningCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = null,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.account_deletion_warning_title),
                    fontFamily = zalandoFontFamily,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = stringResource(R.string.account_deletion_warning_body),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ConfirmationInstruction(modifier: Modifier = Modifier) {
    val word = AccountDeletionConfirmation.WORD
    val instruction = stringResource(R.string.account_deletion_confirmation_instruction, word)
    val wordStart = instruction.indexOf(word)

    Text(
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        text = buildAnnotatedString {
            append(instruction)
            if (wordStart >= 0) {
                addStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    start = wordStart,
                    end = wordStart + word.length,
                )
            }
        },
    )
}

@Preview
@Composable
internal fun DeleteAccountContentPreview() {
    StrimupTheme {
        DeleteAccountContent(
            state = DeleteAccountUiState(
                policy = AccountDeletionPolicy(isPasswordRequired = true),
                isPolicyLoading = false,
                confirmationInput = AccountDeletionConfirmation.WORD,
            ),
            onNavUp = {},
            onRetryClick = {},
            onConfirmationChange = {},
            onPasswordChange = {},
            onPasswordVisibleChange = {},
            onDeleteClick = {},
        )
    }
}

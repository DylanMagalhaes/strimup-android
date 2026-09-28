package com.strimup.feature.auth.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
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
import com.strimup.core.ui.component.textfield.StrimupTextField
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import com.strimup.feature.auth.presentation.component.AuthOAuthSection

@Composable fun LoginScreen(
    onNavToHome: () -> Unit,
    onNavToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is LoginUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.text)
                }

                is LoginUiEvent.ShowSnackBarRes -> {
                    snackBarHostState.showSnackbar(resources.getString(event.textRes))
                }

                is LoginUiEvent.OpenCustomTab -> {
                    context.openInCustomTab(event.url)
                }

                LoginUiEvent.ShowHomeUi -> {
                    onNavToHome()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = screenTopWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) { padding ->
        LoginContent(
            modifier = Modifier.padding(padding),
            state = state,
            emailValue = state.emailInput,
            onEmailChange = { viewModel.onEmailChange(it) },
            passwordValue = state.passwordInput,
            onPasswordChange = { viewModel.onPasswordChange(it) },
            onForgetPasswordClick = { /* TODO */ },
            onLoginClick = { viewModel.onLoginButtonClick() },
            onNavToRegister = onNavToRegister,
            onTwitchLoginClick = viewModel::onTwitchLoginClick,
        )
    }
}

@Composable fun LoginContent(
    state: LoginUiState,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    onForgetPasswordClick: () -> Unit,
    onLoginClick: () -> Unit,
    onNavToRegister: () -> Unit,
    onTwitchLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier, color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(72.dp),
                painter = painterResource(R.drawable.ic_strimup),
                contentDescription = "Strimup icon",
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
                    append("Connexion")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(".")
                    }
                },
            )

            StrimupTextField(
                value = emailValue,
                onValueChange = onEmailChange,
                label = "Email",
            )

            StrimupTextField(
                value = passwordValue,
                onValueChange = onPasswordChange,
                label = "Mot de passe",
                isPassword = true,
            )

            TextButton(
                modifier = Modifier.fillMaxWidth(), onClick = onForgetPasswordClick
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    text = "Mot de passe oublié ?"
                )
            }

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                label = if (state.isLoading) "Connexion en cours..." else "Se connecter",
                onClick = onLoginClick,
            )

            AuthOAuthSection(onTwitchClick = onTwitchLoginClick)

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Pas encore de compte ?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )

                TextButton(
                    onClick = onNavToRegister,
                ) {
                    Text(text = "S'inscrire")
                }
            }
        }
    }
}

@Preview @Composable private fun LoginScreenPreview() {
    StrimupTheme {
        LoginContent(
            emailValue = "",
            onEmailChange = {},
            passwordValue = "",
            onPasswordChange = {},
            onForgetPasswordClick = {},
            onLoginClick = {},
            onNavToRegister = {},
            onTwitchLoginClick = {},
            state = LoginUiState()
        )
    }
}
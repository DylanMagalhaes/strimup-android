package com.strimup.feature.auth.presentation.login

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.strimup.R
import com.strimup.core.ui.component.button.PrimaryButton
import com.strimup.core.ui.component.button.SocialIconButton
import com.strimup.core.ui.component.textfield.StrimupTextField
import com.strimup.core.ui.inset.screenTopWindowInsets
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily

@Composable fun LoginScreen(
    onNavToHome: () -> Unit,
    onNavToRegister: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is LoginUiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(event.text)
                }

                is LoginUiEvent.ShowSnackBarRes -> {
                    snackBarHostState.showSnackbar(context.getString(event.textRes))
                }

                is LoginUiEvent.OpenCustomTab -> {
                    CustomTabsIntent.Builder().build().launchUrl(context, event.url.toUri())
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 40.dp, bottom = 16.dp)
                    .size(112.dp),
                painter = painterResource(R.drawable.ic_strimup),
                contentDescription = "Strimup icon",
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textAlign = TextAlign.Center,
                fontFamily = zalandoFontFamily,
                style = MaterialTheme.typography.headlineLarge,
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom= 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Text(
                    text = "Ou continuer avec",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }


            IconButton(
                modifier = modifier
                    .size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFF9146FF),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                shape = RoundedCornerShape(12.dp),
                onClick = onTwitchLoginClick,
            ) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    painter = painterResource(R.drawable.ic_twitch),
                    contentDescription = null,
                )
            }

//            OutlinedButton(
//                modifier = Modifier.fillMaxWidth(),
//                onClick = onTwitchLoginClick,
//            ) {
//                Text(text = stringResource(R.string.oauth_twitch_continue))
//            }

            Text(
                text = "Pas encore de compte ?",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )

            TextButton(
                onClick = onNavToRegister,
            ) {
                Text(text = "S'inscrire maintenant")
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
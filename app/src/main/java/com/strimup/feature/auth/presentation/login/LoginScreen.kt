package com.strimup.feature.auth.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.R
import com.strimup.common.ui.component.CtaButton
import com.strimup.common.ui.component.CustomTextField
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.home.presentation.component.Social
import com.strimup.feature.home.presentation.component.SocialIconButton

@Composable
fun LoginScreen(
    emailValue: String,
    onEmailChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    onForgetPasswordClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val customTextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,

        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        errorIndicatorColor = MaterialTheme.colorScheme.error,

        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        errorLabelColor = MaterialTheme.colorScheme.error,

        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
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

            CustomTextField(
                value = emailValue,
                onValueChange = onEmailChange,
                label = "Email",
                isPassword = false,
            )

            CustomTextField(
                value = passwordValue,
                onValueChange = onPasswordChange,
                label = "Mot de passe",
                isPassword = true,
            )

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onForgetPasswordClick
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    text = "Mot de passe oublié ?"
                )
            }

            CtaButton(
                modifier = Modifier,
                label = "Entrer",
                onButtonClick = onLoginClick,

            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
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

            SocialIconButton(
                modifier = Modifier.size(56.dp),
                social = Social.Twitch,
            ) { }

            Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Pas encore de compte ?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    )

                    TextButton(
                        onClick = onRegisterClick,
                    ) {
                        Text(text = "S'inscrire maintenant")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    StrimupTheme {
        LoginScreen(
            emailValue = "",
            onEmailChange = {},
            passwordValue = "",
            onPasswordChange = {},
            onForgetPasswordClick = {},
            onLoginClick = {},
            onRegisterClick = {},
        )
    }
}
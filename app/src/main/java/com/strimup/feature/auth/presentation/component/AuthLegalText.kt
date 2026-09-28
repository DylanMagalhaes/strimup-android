package com.strimup.feature.auth.presentation.component

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.core.net.toUri

private const val CGU_URL = "https://www.strimup.com/cgu"
private const val PRIVACY_POLICY_URL = "https://www.strimup.com/politique-de-confidentialite"

/**
 * "[prefix] les CGU et la politique de confidentialité." with both documents
 * opening in a Custom Tab, shared by every auth entry point creating an account.
 */
@Composable
fun AuthLegalText(
    prefix: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    val context = LocalContext.current
    val linkStyles = TextLinkStyles(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
    )
    val openInCustomTab = LinkInteractionListener { link ->
        val url = (link as? LinkAnnotation.Url)?.url ?: return@LinkInteractionListener
        CustomTabsIntent.Builder().build().launchUrl(context, url.toUri())
    }

    Text(
        modifier = modifier,
        textAlign = textAlign,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        text = buildAnnotatedString {
            append("$prefix les ")
            withLink(LinkAnnotation.Url(CGU_URL, linkStyles, openInCustomTab)) {
                append("CGU")
            }
            append(" et la ")
            withLink(LinkAnnotation.Url(PRIVACY_POLICY_URL, linkStyles, openInCustomTab)) {
                append("politique de confidentialité")
            }
            append(".")
        },
    )
}

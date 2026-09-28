package com.strimup.feature.auth.presentation.component

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
import com.strimup.core.legal.LegalUrls
import com.strimup.core.ui.browser.openInCustomTab

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
    val openLink = LinkInteractionListener { link ->
        val url = (link as? LinkAnnotation.Url)?.url ?: return@LinkInteractionListener
        context.openInCustomTab(url)
    }

    Text(
        modifier = modifier,
        textAlign = textAlign,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        text = buildAnnotatedString {
            append("$prefix les ")
            withLink(LinkAnnotation.Url(LegalUrls.TERMS_OF_SERVICE, linkStyles, openLink)) {
                append("CGU")
            }
            append(" et la ")
            withLink(LinkAnnotation.Url(LegalUrls.PRIVACY_POLICY, linkStyles, openLink)) {
                append("politique de confidentialité")
            }
            append(".")
        },
    )
}

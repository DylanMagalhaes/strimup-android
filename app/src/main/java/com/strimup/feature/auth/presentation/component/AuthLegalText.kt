package com.strimup.feature.auth.presentation.component

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import com.strimup.R
import com.strimup.core.legal.LegalUrls
import com.strimup.core.ui.browser.openInCustomTab

@Composable
fun AuthLegalText(
    @StringRes prefixRes: Int,
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
            legalSentenceParts(stringResource(R.string.auth_legal_sentence)).forEach { part ->
                when (part) {
                    LegalSentencePart.Prefix -> append(stringResource(prefixRes))
                    LegalSentencePart.Terms -> withLink(
                        LinkAnnotation.Url(LegalUrls.TERMS_OF_SERVICE, linkStyles, openLink)
                    ) {
                        append(stringResource(R.string.auth_legal_terms))
                    }
                    LegalSentencePart.PrivacyPolicy -> withLink(
                        LinkAnnotation.Url(LegalUrls.PRIVACY_POLICY, linkStyles, openLink)
                    ) {
                        append(stringResource(R.string.auth_legal_privacy_policy))
                    }
                    is LegalSentencePart.Text -> append(part.value)
                }
            }
        },
    )
}

sealed interface LegalSentencePart {
    data object Prefix : LegalSentencePart
    data object Terms : LegalSentencePart
    data object PrivacyPolicy : LegalSentencePart
    data class Text(val value: String) : LegalSentencePart
}

private val PLACEHOLDER_REGEX = Regex("%([1-3])\\\$s")

fun legalSentenceParts(template: String): List<LegalSentencePart> {
    val parts = mutableListOf<LegalSentencePart>()
    var cursor = 0
    PLACEHOLDER_REGEX.findAll(template).forEach { match ->
        if (match.range.first > cursor) {
            parts += LegalSentencePart.Text(template.substring(cursor, match.range.first))
        }
        parts += when (match.groupValues[1]) {
            "1" -> LegalSentencePart.Prefix
            "2" -> LegalSentencePart.Terms
            else -> LegalSentencePart.PrivacyPolicy
        }
        cursor = match.range.last + 1
    }
    if (cursor < template.length) parts += LegalSentencePart.Text(template.substring(cursor))
    return parts
}

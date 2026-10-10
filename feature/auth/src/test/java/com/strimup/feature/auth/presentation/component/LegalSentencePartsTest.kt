package com.strimup.feature.auth.presentation.component

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LegalSentencePartsTest {

    @Test
    fun `french sentence should be split around the links`() {
        val parts = legalSentenceParts("%1\$s les %2\$s et la %3\$s.")

        assertThat(parts).containsExactly(
            LegalSentencePart.Prefix,
            LegalSentencePart.Text(" les "),
            LegalSentencePart.Terms,
            LegalSentencePart.Text(" et la "),
            LegalSentencePart.PrivacyPolicy,
            LegalSentencePart.Text("."),
        ).inOrder()
    }

    @Test
    fun `a translation may reorder the parts`() {
        val parts = legalSentenceParts("%3\$s and %2\$s: %1\$s")

        assertThat(parts.filterNot { it is LegalSentencePart.Text }).containsExactly(
            LegalSentencePart.PrivacyPolicy,
            LegalSentencePart.Terms,
            LegalSentencePart.Prefix,
        ).inOrder()
    }
}

package com.strimup.core.ui.error

import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.ui.text.UiText
import org.junit.Test

class DomainErrorMessagesTest {

    @Test
    fun `Network should map to error_network`() {
        assertThat(DomainError.Network.toMessageRes()).isEqualTo(R.string.error_network)
    }

    @Test
    fun `Timeout should map to error_timeout`() {
        assertThat(DomainError.Timeout.toMessageRes()).isEqualTo(R.string.error_timeout)
    }

    @Test
    fun `Unauthorized should map to error_unauthorized`() {
        assertThat(DomainError.Unauthorized.toMessageRes()).isEqualTo(R.string.error_unauthorized)
    }

    @Test
    fun `Server should map to error_server regardless of the code`() {
        assertThat(DomainError.Server(404).toMessageRes()).isEqualTo(R.string.error_server)
        assertThat(DomainError.Server(500).toMessageRes()).isEqualTo(R.string.error_server)
    }

    @Test
    fun `Serialization should map to error_serialization`() {
        assertThat(DomainError.Serialization.toMessageRes()).isEqualTo(R.string.error_serialization)
    }

    @Test
    fun `Unknown should map to error_unknown`() {
        assertThat(DomainError.Unknown.toMessageRes()).isEqualTo(R.string.error_unknown)
    }

    @Test
    fun `toUiText should show the backend message of a client error`() {
        val uiText = DomainError.Server(400, "Email déjà utilisé").toUiText()

        assertThat(uiText).isEqualTo(UiText.Dynamic("Email déjà utilisé"))
    }

    @Test
    fun `toUiText should hide the backend message of a server error`() {
        val uiText = DomainError.Server(500, "Cannot read properties of undefined").toUiText()

        assertThat(uiText).isEqualTo(UiText.Resource(R.string.error_server))
    }

    @Test
    fun `toUiText should fall back to the generic message when the backend sent none`() {
        assertThat(DomainError.Server(400).toUiText()).isEqualTo(UiText.Resource(R.string.error_server))
        assertThat(DomainError.Network.toUiText()).isEqualTo(UiText.Resource(R.string.error_network))
    }
}

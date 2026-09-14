package com.strimup.core.ui.error

import com.google.common.truth.Truth.assertThat
import com.strimup.R
import com.strimup.core.common.DomainError
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
}

package com.strimup.update

import com.google.android.play.core.install.model.UpdateAvailability
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class InAppUpdatePolicyTest {

    @Test
    fun `starts the update when an update is available and immediate updates are allowed`() {
        assertThat(InAppUpdatePolicy.shouldStartUpdate(UpdateAvailability.UPDATE_AVAILABLE, true)).isTrue()
    }

    @Test
    fun `does not start the update when immediate updates are not allowed`() {
        assertThat(InAppUpdatePolicy.shouldStartUpdate(UpdateAvailability.UPDATE_AVAILABLE, false)).isFalse()
    }

    @Test
    fun `does not start the update when no update is available`() {
        assertThat(InAppUpdatePolicy.shouldStartUpdate(UpdateAvailability.UPDATE_NOT_AVAILABLE, true)).isFalse()
    }

    @Test
    fun `resumes the update when an update started by the app is in progress`() {
        val updateAvailability = UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

        assertThat(InAppUpdatePolicy.shouldResumeUpdate(updateAvailability)).isTrue()
    }

    @Test
    fun `does not resume when no update is in progress`() {
        assertThat(InAppUpdatePolicy.shouldResumeUpdate(UpdateAvailability.UPDATE_AVAILABLE)).isFalse()
    }
}

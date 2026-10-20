package com.strimup.update

import com.google.android.play.core.install.model.UpdateAvailability

object InAppUpdatePolicy {

    fun shouldStartUpdate(updateAvailability: Int, isImmediateUpdateAllowed: Boolean): Boolean =
        updateAvailability == UpdateAvailability.UPDATE_AVAILABLE && isImmediateUpdateAllowed

    fun shouldResumeUpdate(updateAvailability: Int): Boolean =
        updateAvailability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
}

package com.strimup.update

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType

class InAppUpdater(
    private val appUpdateManager: AppUpdateManager,
    private val launcher: ActivityResultLauncher<IntentSenderRequest>,
) {

    fun checkForUpdate() = startUpdateIf { info ->
        InAppUpdatePolicy.shouldStartUpdate(
            updateAvailability = info.updateAvailability(),
            isImmediateUpdateAllowed = info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
        )
    }

    fun resumeUpdateInProgress() = startUpdateIf { info ->
        InAppUpdatePolicy.shouldResumeUpdate(info.updateAvailability())
    }

    private fun startUpdateIf(condition: (AppUpdateInfo) -> Boolean) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (condition(info)) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    launcher,
                    AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                )
            }
        }
    }
}

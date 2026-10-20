package com.strimup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.android.play.core.appupdate.AppUpdateManager
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.feature.auth.presentation.toOAuthCallback
import com.strimup.feature.push.presentation.PushIntents
import com.strimup.presentation.MainViewModel
import com.strimup.update.InAppUpdater
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    private val appUpdateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {}

    private val inAppUpdater by lazy { InAppUpdater(appUpdateManager, appUpdateLauncher) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        if (savedInstanceState == null) {
            handleDeepLink(intent)
            inAppUpdater.checkForUpdate()
        }

        setContent {
            StrimupTheme {
                StrimupNavDisplay(modifier = Modifier.fillMaxSize())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdater.resumeUpdateInProgress()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        if (PushIntents.isOpenNotifications(intent)) {
            mainViewModel.onOpenNotificationsRequested()
            return
        }
        mainViewModel.onOAuthCallback(intent?.data?.toOAuthCallback())
    }
}

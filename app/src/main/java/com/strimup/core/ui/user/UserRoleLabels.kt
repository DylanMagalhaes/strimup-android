package com.strimup.core.ui.user

import androidx.annotation.StringRes
import com.strimup.R
import com.strimup.core.user.domain.entity.UserRole

@StringRes
fun UserRole.toLabelRes(): Int = when (this) {
    UserRole.VIEWER -> R.string.role_viewer
    UserRole.STREAMER -> R.string.role_streamer
    UserRole.ADMIN -> R.string.role_admin
}

package com.strimup.core.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

fun <T : NavKey> SnapshotStateList<T>.navigateAsTab(destination: T) {
    if (lastOrNull() == destination) return
    remove(destination)
    add(destination)
}

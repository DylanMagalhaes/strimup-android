package com.strimup.core.navigation

import androidx.navigation3.runtime.NavKey

fun <T : NavKey> MutableList<T>.navigateAsTab(destination: T) {
    if (lastOrNull() == destination) return
    remove(destination)
    add(destination)
}

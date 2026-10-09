package com.strimup.core.navigation

import androidx.navigation3.runtime.NavKey

fun <T : NavKey> MutableList<T>.navigateAsTab(destination: T) {
    if (lastOrNull() == destination) return
    remove(destination)
    add(destination)
}

fun <T : NavKey> MutableList<T>.popOrReplaceWith(fallback: T) {
    if (size > 1) {
        removeAt(lastIndex)
    } else {
        clear()
        add(fallback)
    }
}

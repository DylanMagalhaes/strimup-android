package com.strimup.core.ui.text

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class Dynamic(val value: String) : UiText
    data class Resource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList(),
    ) : UiText
}

@Suppress("SpreadOperator")
fun UiText.asString(resources: Resources): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> if (args.isEmpty()) {
        resources.getString(resId)
    } else {
        resources.getString(resId, *args.toTypedArray())
    }
}

@Suppress("SpreadOperator")
@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> if (args.isEmpty()) {
        stringResource(resId)
    } else {
        stringResource(resId, *args.toTypedArray())
    }
}

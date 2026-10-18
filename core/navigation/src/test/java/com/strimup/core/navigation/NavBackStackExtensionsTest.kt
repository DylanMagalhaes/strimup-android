package com.strimup.core.navigation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NavBackStackExtensionsTest {

    @Test
    fun `popOrReplaceWith should pop the top screen when there is one below`() {
        val backStack = mutableListOf<Destination>(Destination.Home.StreamerList, Destination.Notifications)

        backStack.popOrReplaceWith(Destination.Home.StreamerList)

        assertThat(backStack).containsExactly(Destination.Home.StreamerList)
    }

    @Test
    fun `popOrReplaceWith should never leave the back stack empty`() {
        val backStack = mutableListOf<Destination>(Destination.Register)

        backStack.popOrReplaceWith(Destination.Login)

        assertThat(backStack).containsExactly(Destination.Login)
    }

    @Test
    fun `popOrReplaceWith should recover an already empty back stack`() {
        val backStack = mutableListOf<Destination>()

        backStack.popOrReplaceWith(Destination.Home.StreamerList)

        assertThat(backStack).containsExactly(Destination.Home.StreamerList)
    }
}

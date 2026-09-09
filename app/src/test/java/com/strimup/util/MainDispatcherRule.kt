package com.strimup.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Remplace [Dispatchers.Main] par un [TestDispatcher] le temps d'un test.
 *
 * Nécessaire pour tout test qui touche un ViewModel : `viewModelScope` tourne
 * sur `Dispatchers.Main`, qui n'existe pas dans un test JVM (→ crash
 * "Module with the Main dispatcher had failed to initialize").
 *
 * Usage :
 * ```
 * @get:Rule
 * val mainDispatcherRule = MainDispatcherRule()
 * ```
 *
 * Le [StandardTestDispatcher] par défaut ne lance rien tant qu'on n'appelle pas
 * `advanceUntilIdle()` / `runCurrent()` dans `runTest { }` — on contrôle donc
 * précisément quand les coroutines s'exécutent.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

package com.strimup.feature.push.data.local

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DataStoreNotificationPermissionRepositoryTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val repository by lazy {
        DataStoreNotificationPermissionRepository(
            PreferenceDataStoreFactory.create(
                scope = dataStoreScope,
                produceFile = { temporaryFolder.newFile("test.preferences_pb") },
            )
        )
    }

    @After
    fun tearDown() {
        dataStoreScope.cancel()
    }

    @Test
    fun `permission should not be asked by default`() = runTest {
        assertThat(repository.hasAskedPermission.first()).isFalse()
    }

    @Test
    fun `markPermissionAsked should be remembered`() = runTest {
        repository.markPermissionAsked()

        assertThat(repository.hasAskedPermission.first()).isTrue()
    }
}

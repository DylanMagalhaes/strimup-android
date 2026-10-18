package com.strimup.feature.push.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.strimup.feature.push.domain.NotificationPermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreNotificationPermissionRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : NotificationPermissionRepository {

    override val hasAskedPermission: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_PERMISSION_ASKED] == true }

    override suspend fun markPermissionAsked() {
        dataStore.edit { preferences -> preferences[KEY_PERMISSION_ASKED] = true }
    }

    private companion object {
        val KEY_PERMISSION_ASKED = booleanPreferencesKey("notification_permission_asked")
    }
}

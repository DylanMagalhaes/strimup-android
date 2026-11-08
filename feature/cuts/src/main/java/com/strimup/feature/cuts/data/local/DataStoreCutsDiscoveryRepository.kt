package com.strimup.feature.cuts.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.strimup.feature.cuts.domain.CutsDiscoveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreCutsDiscoveryRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : CutsDiscoveryRepository {

    override val hasOpenedCuts: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[KEY_CUTS_OPENED] == true }

    override suspend fun markCutsOpened() {
        dataStore.edit { preferences -> preferences[KEY_CUTS_OPENED] = true }
    }

    private companion object {
        val KEY_CUTS_OPENED = booleanPreferencesKey("cuts_opened")
    }
}

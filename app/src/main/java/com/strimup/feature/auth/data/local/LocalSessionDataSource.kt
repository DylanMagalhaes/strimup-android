package com.strimup.feature.auth.data.local

import com.strimup.core.database.dao.FavoriteDao
import com.strimup.core.database.dao.UserDao
import com.strimup.core.database.dao.FilterDao
import javax.inject.Inject

interface LocalSessionDataSource {
    suspend fun clear()
}

class DefaultLocalSessionDataSource @Inject constructor(
    private val preferences: AuthPreferencesDataSource,
    private val userDao: UserDao,
    private val filterDao: FilterDao,
    private val favoriteDao: FavoriteDao,
) : LocalSessionDataSource {
    override suspend fun clear() {
        preferences.clear()
        userDao.deleteAllUsers()
        filterDao.deleteAllFilters()
        favoriteDao.deleteAllFavorites()
    }
}

package com.strimup.feature.auth.data.local

import com.strimup.core.favorite.data.local.dao.FavoriteDao
import com.strimup.core.user.data.local.dao.UserDao
import com.strimup.feature.filter.data.local.dao.FilterDao
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

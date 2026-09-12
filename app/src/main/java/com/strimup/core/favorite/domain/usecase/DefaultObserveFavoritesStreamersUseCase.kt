package com.strimup.core.favorite.domain.usecase

import com.strimup.core.favorite.data.DefaultFavoriteStreamerRepository
import com.strimup.core.streamer.domain.entity.Streamer
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DefaultObserveFavoritesStreamersUseCase @Inject constructor(
    private val repository: DefaultFavoriteStreamerRepository
): ObserveFavoritesStreamersUseCase {
    override fun invoke(): Flow<List<Streamer>> {
        return repository.observeFavorites()
    }
}
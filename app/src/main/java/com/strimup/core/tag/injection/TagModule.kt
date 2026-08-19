package com.strimup.core.tag.injection

import com.strimup.core.tag.data.remote.TagApiService
import com.strimup.core.tag.data.repository.DefaultTagRepository
import com.strimup.core.tag.domain.repository.TagRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface TagModule {

    @Binds
    @Singleton
    abstract fun bindTagRepository(
        tagRepositoryImpl: DefaultTagRepository
    ): TagRepository

    companion object {
        @Provides
        @Singleton
        fun provideTagApiService(retrofit: Retrofit): TagApiService {
            return retrofit.create(TagApiService::class.java)
        }
    }
}
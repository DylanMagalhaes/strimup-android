package com.strimup.feature.streamervideos.injection

import com.strimup.feature.streamervideos.data.DefaultStreamerVideoRepository
import com.strimup.feature.streamervideos.data.StreamerVideoApiService
import com.strimup.feature.streamervideos.data.file.ContentResolverVideoFileReader
import com.strimup.feature.streamervideos.data.file.VideoFileReader
import com.strimup.feature.streamervideos.domain.StreamerVideoRepository
import com.strimup.feature.streamervideos.domain.usecase.DefaultDeleteVideoUseCase
import com.strimup.feature.streamervideos.domain.usecase.DefaultGetMyVideosUseCase
import com.strimup.feature.streamervideos.domain.usecase.DefaultSelectVideoFileUseCase
import com.strimup.feature.streamervideos.domain.usecase.DefaultUploadVideoUseCase
import com.strimup.feature.streamervideos.domain.usecase.DeleteVideoUseCase
import com.strimup.feature.streamervideos.domain.usecase.GetMyVideosUseCase
import com.strimup.feature.streamervideos.domain.usecase.SelectVideoFileUseCase
import com.strimup.feature.streamervideos.domain.usecase.UploadVideoUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val VIDEO_WRITE_TIMEOUT_MINUTES = 10L
private const val VIDEO_READ_TIMEOUT_MINUTES = 2L

@Module
@InstallIn(SingletonComponent::class)
interface StreamerVideoModule {

    @Binds
    @Singleton
    fun bindStreamerVideoRepository(impl: DefaultStreamerVideoRepository): StreamerVideoRepository

    @Binds
    @Singleton
    fun bindVideoFileReader(impl: ContentResolverVideoFileReader): VideoFileReader

    @Binds
    fun bindGetMyVideosUseCase(impl: DefaultGetMyVideosUseCase): GetMyVideosUseCase

    @Binds
    fun bindSelectVideoFileUseCase(impl: DefaultSelectVideoFileUseCase): SelectVideoFileUseCase

    @Binds
    fun bindUploadVideoUseCase(impl: DefaultUploadVideoUseCase): UploadVideoUseCase

    @Binds
    fun bindDeleteVideoUseCase(impl: DefaultDeleteVideoUseCase): DeleteVideoUseCase

    companion object {
        @Provides
        @Singleton
        fun providesStreamerVideoApiService(retrofit: Retrofit, okHttpClient: OkHttpClient): StreamerVideoApiService {
            val videoClient = okHttpClient.newBuilder()
                .apply { interceptors().removeAll { it is HttpLoggingInterceptor } }
                .writeTimeout(VIDEO_WRITE_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .readTimeout(VIDEO_READ_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .build()

            return retrofit.newBuilder()
                .client(videoClient)
                .build()
                .create(StreamerVideoApiService::class.java)
        }
    }
}

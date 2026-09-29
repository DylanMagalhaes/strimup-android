package com.strimup.feature.notification.injection

import com.strimup.feature.notification.data.DefaultNotificationRepository
import com.strimup.feature.notification.data.NotificationApiService
import com.strimup.feature.notification.domain.NotificationRepository
import com.strimup.feature.notification.domain.usecase.ClearNotificationsUseCase
import com.strimup.feature.notification.domain.usecase.DefaultClearNotificationsUseCase
import com.strimup.feature.notification.domain.usecase.DefaultDeleteNotificationUseCase
import com.strimup.feature.notification.domain.usecase.DefaultGetNotificationsPageUseCase
import com.strimup.feature.notification.domain.usecase.DefaultMarkAllNotificationsAsReadUseCase
import com.strimup.feature.notification.domain.usecase.DefaultMarkNotificationAsReadUseCase
import com.strimup.feature.notification.domain.usecase.DefaultObserveUnreadCountUseCase
import com.strimup.feature.notification.domain.usecase.DefaultRefreshUnreadCountUseCase
import com.strimup.feature.notification.domain.usecase.DeleteNotificationUseCase
import com.strimup.feature.notification.domain.usecase.GetNotificationsPageUseCase
import com.strimup.feature.notification.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.strimup.feature.notification.domain.usecase.MarkNotificationAsReadUseCase
import com.strimup.feature.notification.domain.usecase.ObserveUnreadCountUseCase
import com.strimup.feature.notification.domain.usecase.RefreshUnreadCountUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationNetworkModule {

    @Provides
    @Singleton
    fun providesNotificationApiService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
interface NotificationDomainModule {

    @Binds
    @Singleton
    fun bindsNotificationRepository(impl: DefaultNotificationRepository): NotificationRepository

    @Binds
    fun bindsObserveUnreadCountUseCase(impl: DefaultObserveUnreadCountUseCase): ObserveUnreadCountUseCase

    @Binds
    fun bindsRefreshUnreadCountUseCase(impl: DefaultRefreshUnreadCountUseCase): RefreshUnreadCountUseCase

    @Binds
    fun bindsGetNotificationsPageUseCase(impl: DefaultGetNotificationsPageUseCase): GetNotificationsPageUseCase

    @Binds
    fun bindsMarkNotificationAsReadUseCase(impl: DefaultMarkNotificationAsReadUseCase): MarkNotificationAsReadUseCase

    @Binds
    fun bindsMarkAllNotificationsAsReadUseCase(
        impl: DefaultMarkAllNotificationsAsReadUseCase,
    ): MarkAllNotificationsAsReadUseCase

    @Binds
    fun bindsDeleteNotificationUseCase(impl: DefaultDeleteNotificationUseCase): DeleteNotificationUseCase

    @Binds
    fun bindsClearNotificationsUseCase(impl: DefaultClearNotificationsUseCase): ClearNotificationsUseCase
}

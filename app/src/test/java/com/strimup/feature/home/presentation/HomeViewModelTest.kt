package com.strimup.feature.home.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.strimup.feature.home.domain.entity.BannerItemEntity
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule val mainDispatcherRule = InstantTaskExecutorRule()

    @Test
    fun `on init, should show banner items when succeed`() = runTest {
        //GIVEN
        val bannerItems = listOf(
            Random.nextBannerItemEntity(),
            Random.nextBannerItemEntity(),
            Random.nextBannerItemEntity(),
            Random.nextBannerItemEntity(),
        )

        //WHEN
        val viewModel = HomeViewModel(
            getStreamers = { Result.success(emptyList()) },
            getBannerItems = { Result.success(bannerItems) },
        )

        //THEN
        val actual = viewModel.state.value.bannerItems

        Assert.assertEquals(bannerItems, actual)
    }

}

private fun Random.nextBannerItemEntity(): BannerItemEntity {
    return BannerItemEntity(
        title = "${Random.nextInt()}",
        description = "${Random.nextInt()}",
        imageUrl = "${Random.nextInt()}",
        position = Random.nextUInt().toInt(),
        linkUrl = "${Random.nextInt()}",
        type = "${Random.nextInt()}",
        avatarUrl = "${Random.nextInt()}",
        streamerId = "${Random.nextInt()}",
    )
}

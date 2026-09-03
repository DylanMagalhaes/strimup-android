package com.strimup.feature.home.presentation

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.entity.BannerItemEntity
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.util.MainDispatcherRule
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

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
        advanceUntilIdle()

        //THEN
        val actual = viewModel.state.value.bannerItems

        Assert.assertEquals(bannerItems, actual)
    }

    @Test
    fun `on init, should not show banner if banner fetching failed`() = runTest {
        //GIVEN
        val bannerItems = emptyList<BannerItemEntity>()

        //WHEN
        val viewModel = HomeViewModel(
            getStreamers = { Result.success(emptyList()) },
            getBannerItems = { Result.failure(Exception()) },
        )
        advanceUntilIdle()

        //THEN
        val actual = viewModel.state.value.bannerItems

        Assert.assertEquals(bannerItems, actual)
    }

    @Test
    fun `on init, should show streamers when succeed`() = runTest {
        //GIVEN
        val streamers = listOf<Streamer>(
            Streamer(
                id = "1",
                userName = "Tom",
                imageUrl = ""
            ),
            Streamer(
                id = "2",
                userName = "Jerry",
                imageUrl = ""
            )
        )


        //WHEN
        val viewModel = HomeViewModel(
            getStreamers = { Result.success(streamers) },
            getBannerItems = { Result.success(emptyList()) },
        )
        advanceUntilIdle()

        //THEN
        val actual = viewModel.state.value.streamers

        Assert.assertEquals(streamers, actual)
    }

    @Test
    fun `on init, should show SnackBar if failure`() = runTest {
        //WHEN
        val viewModel = HomeViewModel(
            getStreamers = { Result.failure(Exception()) },
            getBannerItems = { Result.success(emptyList()) },
        )
        advanceUntilIdle()

        //THEN
        val actual = viewModel.events.first()

        assert(actual is HomeUiEvent.ShowSnackBar)
    }

    @Test
    fun `on tab click, should change filter tab`() = runTest {
        //GIVEN
        val discoveryStreamers = listOf(
            Streamer(
                id = "1",
                userName = "Tom",
                imageUrl = ""
            ),
            Streamer(
                id = "2",
                userName = "Jerry",
                imageUrl = ""
            )
        )

        val liveStreamers = listOf(
            Streamer(
                id = "4",
                userName = "Marcus",
                imageUrl = ""
            ),
            Streamer(
                id = "5",
                userName = "Paul",
                imageUrl = ""
            )
        )

        val viewModel = HomeViewModel(
            getStreamers = { filter ->
                val list = when (filter) {
                    FilterEntity.Discovery -> discoveryStreamers
                    FilterEntity.Live -> liveStreamers
                }
                Result.success(list)
            },
            getBannerItems = { Result.success(emptyList()) },
        )

        //WHEN
        viewModel.onTabClick(FilterEntity.Live)
        advanceUntilIdle()

        //THEN
        val actual = viewModel.state.value.streamers

        Assert.assertEquals(liveStreamers, actual)
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

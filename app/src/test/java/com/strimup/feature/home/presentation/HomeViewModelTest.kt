package com.strimup.feature.home.presentation

import app.cash.turbine.test
import com.strimup.R
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.home.domain.entity.BannerItemEntity
import com.strimup.feature.home.domain.entity.BannerType
import com.strimup.feature.home.domain.entity.FilterEntity
import com.strimup.feature.home.domain.usecase.GetStreamersUseCase
import com.strimup.util.MainDispatcherRule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextUInt

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
            getCachedDiscoveryStreamers = { emptyList() },
            observeFavorites = { flowOf(emptyList()) },
            toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
            observeBanner = { flowOf(bannerItems) },
            refreshBanner = { Result.success(Unit) },
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
            getCachedDiscoveryStreamers = { emptyList() },
            observeFavorites = { flowOf(emptyList()) },
            toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
            observeBanner = { flowOf(emptyList()) },
            refreshBanner = { Result.failure(Exception()) },
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
            getCachedDiscoveryStreamers = { emptyList() },
            observeFavorites = { flowOf(emptyList()) },
            toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
            observeBanner = { flowOf(emptyList()) },
            refreshBanner = { Result.success(Unit) },
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
            getCachedDiscoveryStreamers = { emptyList() },
            observeFavorites = { flowOf(emptyList()) },
            toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
            observeBanner = { flowOf(emptyList()) },
            refreshBanner = { Result.success(Unit) },
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
            getCachedDiscoveryStreamers = { emptyList() },
            observeFavorites = { flowOf(emptyList()) },
            toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
            observeBanner = { flowOf(emptyList()) },
            refreshBanner = { Result.success(Unit) },
        )

        //WHEN
        viewModel.onTabClick(FilterEntity.Live)
        advanceUntilIdle()

        //THEN
        val actual = viewModel.state.value.streamers

        Assert.assertEquals(liveStreamers, actual)
    }

    @Test
    fun `on init, should keep showing cached banner items when the refresh fails`() = runTest {
        val cachedItems = listOf(Random.nextBannerItemEntity(), Random.nextBannerItemEntity())

        val viewModel = HomeViewModel(
            getStreamers = { Result.success(emptyList()) },
            getCachedDiscoveryStreamers = { emptyList() },
            observeFavorites = { flowOf(emptyList()) },
            toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
            observeBanner = { flowOf(cachedItems) },
            refreshBanner = { Result.failure(Exception()) },
        )
        advanceUntilIdle()

        Assert.assertEquals(cachedItems, viewModel.state.value.bannerItems)
        Assert.assertFalse(viewModel.state.value.isBannerLoading)
    }

    private val cachedStreamers = listOf(
        Streamer(id = "cached-1", userName = "Inox", imageUrl = ""),
        Streamer(id = "cached-2", userName = "Gotaga", imageUrl = ""),
    )

    private val freshStreamers = listOf(
        Streamer(id = "fresh-1", userName = "Squeezie", imageUrl = "", isLive = true),
    )

    private fun homeViewModel(
        getStreamers: GetStreamersUseCase,
        cached: List<Streamer> = cachedStreamers,
    ) = HomeViewModel(
        getStreamers = getStreamers,
        getCachedDiscoveryStreamers = { cached },
        observeFavorites = { flowOf(emptyList()) },
        toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
        observeBanner = { flowOf(emptyList()) },
        refreshBanner = { Result.success(Unit) },
    )

    @Test
    fun `discovery should show saved streamers immediately while the network is loading`() = runTest {
        val network = CompletableDeferred<Result<List<Streamer>>>()
        val viewModel = homeViewModel(getStreamers = { network.await() })

        advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertEquals(cachedStreamers, state.streamers)
        Assert.assertFalse(state.isLoading)
        Assert.assertFalse(state.isShowingSavedContent)

        network.complete(Result.success(freshStreamers))
        advanceUntilIdle()
    }

    @Test
    fun `fresh streamers should replace the saved ones`() = runTest {
        val viewModel = homeViewModel(getStreamers = { Result.success(freshStreamers) })

        advanceUntilIdle()

        Assert.assertEquals(freshStreamers, viewModel.state.value.streamers)
        Assert.assertFalse(viewModel.state.value.isShowingSavedContent)
    }

    @Test
    fun `offline discovery should keep saved streamers and flag them without error`() = runTest {
        val viewModel = homeViewModel(getStreamers = { Result.failure(DomainException(DomainError.Network)) })

        viewModel.events.test {
            advanceUntilIdle()
            expectNoEvents()
        }

        val state = viewModel.state.value
        Assert.assertEquals(cachedStreamers, state.streamers)
        Assert.assertTrue(state.isShowingSavedContent)
        Assert.assertFalse(state.shouldShowStreamersError)
    }

    @Test
    fun `offline discovery without saved streamers should show a retryable error`() = runTest {
        val viewModel = homeViewModel(
            getStreamers = { Result.failure(DomainException(DomainError.Network)) },
            cached = emptyList(),
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertTrue(state.shouldShowStreamersError)
        Assert.assertEquals(R.string.error_network, state.errorMessageRes)
    }

    @Test
    fun `live tab should never show saved streamers when offline`() = runTest {
        val viewModel = homeViewModel(
            getStreamers = { filter ->
                if (filter == FilterEntity.Live) {
                    Result.failure(DomainException(DomainError.Network))
                } else {
                    Result.success(freshStreamers)
                }
            },
        )
        advanceUntilIdle()

        viewModel.onTabClick(FilterEntity.Live)
        advanceUntilIdle()

        val state = viewModel.state.value
        Assert.assertTrue(state.streamers.isEmpty())
        Assert.assertTrue(state.shouldShowStreamersError)
        Assert.assertFalse(state.isShowingSavedContent)
    }

    @Test
    fun `onRetryClick should load the current tab again`() = runTest {
        var calls = 0
        val viewModel = homeViewModel(
            getStreamers = {
                calls++
                if (calls == 1) Result.failure(DomainException(DomainError.Network)) else Result.success(freshStreamers)
            },
            cached = emptyList(),
        )
        advanceUntilIdle()

        viewModel.onRetryClick()
        advanceUntilIdle()

        Assert.assertEquals(freshStreamers, viewModel.state.value.streamers)
        Assert.assertFalse(viewModel.state.value.shouldShowStreamersError)
    }

    @Test
    fun `onRefresh should reload the current tab without the full screen loader`() = runTest {
        val network = CompletableDeferred<Result<List<Streamer>>>()
        var calls = 0
        val viewModel = homeViewModel(
            getStreamers = {
                calls++
                if (calls == 1) Result.success(freshStreamers) else network.await()
            },
        )
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        val refreshing = viewModel.state.value
        Assert.assertTrue(refreshing.isRefreshing)
        Assert.assertFalse(refreshing.isLoading)
        Assert.assertEquals(freshStreamers, refreshing.streamers)

        val newStreamers = listOf(Streamer(id = "new-1", userName = "Locklear", imageUrl = ""))
        network.complete(Result.success(newStreamers))
        advanceUntilIdle()

        Assert.assertFalse(viewModel.state.value.isRefreshing)
        Assert.assertEquals(newStreamers, viewModel.state.value.streamers)
    }

    @Test
    fun `onRefresh should refresh the banner too`() = runTest {
        var bannerRefreshes = 0
        val viewModel = HomeViewModel(
            getStreamers = { Result.success(freshStreamers) },
            getCachedDiscoveryStreamers = { emptyList() },
            observeFavorites = { flowOf(emptyList()) },
            toggleFavoriteStreamer = { _, _ -> Result.success(Unit) },
            observeBanner = { flowOf(emptyList()) },
            refreshBanner = {
                bannerRefreshes++
                Result.success(Unit)
            },
        )
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()

        Assert.assertEquals(2, bannerRefreshes)
    }

    @Test
    fun `failed refresh should keep the current list and show a snackbar`() = runTest {
        var calls = 0
        val viewModel = homeViewModel(
            getStreamers = {
                calls++
                if (calls == 1) Result.success(freshStreamers) else Result.failure(DomainException(DomainError.Network))
            },
        )
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onRefresh()
            advanceUntilIdle()

            val event = awaitItem() as HomeUiEvent.ShowSnackBar
            Assert.assertEquals(R.string.error_network, event.textRes)
        }

        val state = viewModel.state.value
        Assert.assertEquals(freshStreamers, state.streamers)
        Assert.assertFalse(state.isRefreshing)
        Assert.assertFalse(state.shouldShowStreamersError)
    }

    @Test
    fun `onRefresh should be ignored while a refresh is already running`() = runTest {
        val network = CompletableDeferred<Result<List<Streamer>>>()
        var calls = 0
        val viewModel = homeViewModel(
            getStreamers = {
                calls++
                if (calls == 1) Result.success(freshStreamers) else network.await()
            },
        )
        advanceUntilIdle()

        viewModel.onRefresh()
        viewModel.onRefresh()
        advanceUntilIdle()

        Assert.assertEquals(2, calls)
        network.complete(Result.success(freshStreamers))
        advanceUntilIdle()
    }

    @Test
    fun `switching tab during a refresh should stop the refresh indicator`() = runTest {
        val network = CompletableDeferred<Result<List<Streamer>>>()
        var calls = 0
        val viewModel = homeViewModel(
            getStreamers = {
                calls++
                if (calls == 2) network.await() else Result.success(freshStreamers)
            },
        )
        advanceUntilIdle()

        viewModel.onRefresh()
        advanceUntilIdle()
        viewModel.onTabClick(FilterEntity.Live)
        advanceUntilIdle()

        Assert.assertFalse(viewModel.state.value.isRefreshing)
        Assert.assertEquals(FilterEntity.Live, viewModel.state.value.currentTab)
    }
}


private fun Random.nextBannerItemEntity(): BannerItemEntity {
    return BannerItemEntity(
        title = "${Random.nextInt()}",
        description = "${Random.nextInt()}",
        imageUrl = "${Random.nextInt()}",
        position = Random.nextUInt().toInt(),
        linkUrl = "${Random.nextInt()}",
        type = BannerType.Other,
        avatarUrl = "${Random.nextInt()}",
        streamerId = "${Random.nextInt()}",
    )
}

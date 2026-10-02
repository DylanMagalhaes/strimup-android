package com.strimup.feature.home.data.repository

import com.google.common.truth.Truth.assertThat
import com.strimup.core.common.DomainError
import com.strimup.core.common.DomainException
import com.strimup.feature.home.data.BannerApiService
import com.strimup.feature.home.data.DefaultBannerRepository
import com.strimup.feature.home.data.local.FakeHomeDao
import com.strimup.feature.home.data.response.BannerItemsResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

class DefaultBannerRepositoryTest {

    private class FakeBannerApiService(var response: () -> List<BannerItemsResponse>) : BannerApiService {
        override suspend fun getBannerItems(): List<BannerItemsResponse> = response()
    }

    private fun banner(title: String, position: Int) = BannerItemsResponse(
        type = "PROMO",
        title = title,
        description = "description",
        imageUrl = null,
        position = position,
        linkUrl = "https://strimup.com",
    )

    @Test
    fun `refresh should store the server banner in its display order`() = runTest {
        val dao = FakeHomeDao()
        val repository = DefaultBannerRepository(
            FakeBannerApiService { listOf(banner("first", position = 5), banner("second", position = 1)) },
            dao,
        )

        val result = repository.refreshBannerItems()

        assertThat(result.isSuccess).isTrue()
        assertThat(repository.observeBannerItems().first().map { it.title })
            .containsExactly("first", "second")
            .inOrder()
    }

    @Test
    fun `refresh should replace the previous banner`() = runTest {
        val dao = FakeHomeDao()
        val service = FakeBannerApiService { listOf(banner("old", position = 0)) }
        val repository = DefaultBannerRepository(service, dao)
        repository.refreshBannerItems()

        service.response = { listOf(banner("new", position = 0)) }
        repository.refreshBannerItems()

        assertThat(repository.observeBannerItems().first().map { it.title }).containsExactly("new")
    }

    @Test
    fun `refresh failure should keep the cached banner`() = runTest {
        val dao = FakeHomeDao()
        val service = FakeBannerApiService { listOf(banner("cached", position = 0)) }
        val repository = DefaultBannerRepository(service, dao)
        repository.refreshBannerItems()

        service.response = { throw IOException() }
        val exception = repository.refreshBannerItems().exceptionOrNull() as DomainException

        assertThat(exception.error).isEqualTo(DomainError.Network)
        assertThat(repository.observeBannerItems().first().map { it.title }).containsExactly("cached")
    }
}

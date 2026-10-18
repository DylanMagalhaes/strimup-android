package com.strimup.feature.home.data.cache

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.feature.home.data.RoomDiscoveryStreamerCache
import com.strimup.feature.home.data.local.FakeHomeDao
import com.strimup.feature.home.domain.DISCOVERY_CACHE_SIZE
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RoomDiscoveryStreamerCacheTest {

    private fun streamer(id: String, isLive: Boolean = false) = Streamer(
        id = id,
        userName = "streamer-$id",
        imageUrl = "https://cdn/$id.png",
        socials = listOf(Social(url = "https://twitch.tv/$id", type = Social.Type.Twitch)),
        isLive = isLive,
        liveTitle = if (isLive) "En live !" else null,
        tags = listOf(TagEntity(id = 1, name = "FPS", category = "Genre")),
        personality = "Chill",
        personalitySecondary = "Fun",
    )

    @Test
    fun `cached streamers should come back in the same order with their profile`() = runTest {
        val cache = RoomDiscoveryStreamerCache(FakeHomeDao())

        cache.saveStreamers(listOf(streamer("b"), streamer("a")))

        val cached = cache.getStreamers()
        assertThat(cached.map { it.id }).containsExactly("b", "a").inOrder()
        assertThat(cached.first()).isEqualTo(streamer("b"))
    }

    @Test
    fun `live information should never be cached`() = runTest {
        val cache = RoomDiscoveryStreamerCache(FakeHomeDao())

        cache.saveStreamers(listOf(streamer("a", isLive = true)))

        val cached = cache.getStreamers().single()
        assertThat(cached.isLive).isFalse()
        assertThat(cached.liveTitle).isNull()
    }

    @Test
    fun `only the first streamers should be cached`() = runTest {
        val cache = RoomDiscoveryStreamerCache(FakeHomeDao())

        cache.saveStreamers((1..30).map { streamer("$it") })

        assertThat(cache.getStreamers()).hasSize(DISCOVERY_CACHE_SIZE)
        assertThat(cache.getStreamers().first().id).isEqualTo("1")
    }

    @Test
    fun `saving should replace the previous cache and ignore duplicates`() = runTest {
        val cache = RoomDiscoveryStreamerCache(FakeHomeDao())
        cache.saveStreamers(listOf(streamer("old")))

        cache.saveStreamers(listOf(streamer("new"), streamer("new")))

        assertThat(cache.getStreamers().map { it.id }).containsExactly("new")
    }
}

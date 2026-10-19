package com.strimup.feature.search.data

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.search.data.local.FakeRecentStreamerDao
import com.strimup.feature.search.domain.RECENT_STREAMERS_LIMIT
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultRecentStreamerRepositoryTest {

    private fun streamer(id: String) = Streamer(
        id = id,
        userName = "streamer-$id",
        imageUrl = "https://cdn/$id.png",
        isLive = true,
        liveTitle = "En live !",
    )

    @Test
    fun `saved streamer should come back with its profile but without live information`() = runTest {
        val repository = DefaultRecentStreamerRepository(FakeRecentStreamerDao())

        repository.saveRecentStreamer(streamer("a"))

        assertThat(repository.observeRecentStreamers().first()).containsExactly(
            Streamer(id = "a", userName = "streamer-a", imageUrl = "https://cdn/a.png")
        )
    }

    @Test
    fun `saving the same streamer twice should keep a single entry`() = runTest {
        val repository = DefaultRecentStreamerRepository(FakeRecentStreamerDao())

        repository.saveRecentStreamer(streamer("a"))
        repository.saveRecentStreamer(streamer("a"))

        assertThat(repository.observeRecentStreamers().first().map { it.id }).containsExactly("a")
    }

    @Test
    fun `saving should keep only the most recent streamers`() = runTest {
        val dao = FakeRecentStreamerDao()
        val repository = DefaultRecentStreamerRepository(dao)

        (1..RECENT_STREAMERS_LIMIT + 2).forEach { repository.saveRecentStreamer(streamer("$it")) }

        assertThat(dao.streamers.value).hasSize(RECENT_STREAMERS_LIMIT)
    }

    @Test
    fun `removing a streamer should delete only that streamer`() = runTest {
        val repository = DefaultRecentStreamerRepository(FakeRecentStreamerDao())
        repository.saveRecentStreamer(streamer("a"))
        repository.saveRecentStreamer(streamer("b"))

        repository.removeRecentStreamer("a")

        assertThat(repository.observeRecentStreamers().first().map { it.id }).containsExactly("b")
    }
}

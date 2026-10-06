package com.strimup.core.ui.component.streamer

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime

class StreamerNextLiveTest {

    private val thursdayAt20h = LocalDateTime.of(2026, 10, 8, 20, 0)

    @Test
    fun `no schedule should give no next live`() {
        assertThat(emptyList<Streamer.ScheduleSlot>().nextLive(thursdayAt20h)).isNull()
    }

    @Test
    fun `a slot later today should be today`() {
        val nextLive = listOf(slot(dayOfWeek = 3, startTime = "21:00:00", title = "Ranked"))
            .nextLive(thursdayAt20h)

        assertThat(nextLive).isEqualTo(
            StreamerNextLive(day = NextLiveDay.Today, startTime = "21:00", title = "Ranked"),
        )
    }

    @Test
    fun `a slot starting right now should still be today`() {
        val nextLive = listOf(slot(dayOfWeek = 3, startTime = "20:00:00")).nextLive(thursdayAt20h)

        assertThat(nextLive?.day).isEqualTo(NextLiveDay.Today)
    }

    @Test
    fun `a slot tomorrow should be tomorrow`() {
        val nextLive = listOf(slot(dayOfWeek = 4)).nextLive(thursdayAt20h)

        assertThat(nextLive?.day).isEqualTo(NextLiveDay.Tomorrow)
    }

    @Test
    fun `a slot later this week should give its day`() {
        val nextLive = listOf(slot(dayOfWeek = 0)).nextLive(thursdayAt20h)

        assertThat(nextLive?.day).isEqualTo(NextLiveDay.ThisWeek(DayOfWeek.MONDAY))
    }

    @Test
    fun `a slot already passed today should move to next week`() {
        val nextLive = listOf(slot(dayOfWeek = 3, startTime = "18:00:00")).nextLive(thursdayAt20h)

        assertThat(nextLive?.day).isEqualTo(NextLiveDay.NextWeek(DayOfWeek.THURSDAY))
    }

    @Test
    fun `the closest slot should win`() {
        val nextLive = listOf(
            slot(dayOfWeek = 1, title = "Mardi"),
            slot(dayOfWeek = 5, startTime = "15:00:00", title = "Samedi aprem"),
            slot(dayOfWeek = 5, startTime = "10:00:00", title = "Samedi matin"),
        ).nextLive(thursdayAt20h)

        assertThat(nextLive?.title).isEqualTo("Samedi matin")
        assertThat(nextLive?.startTime).isEqualTo("10:00")
    }

    @Test
    fun `invalid slots should be ignored`() {
        val nextLive = listOf(
            slot(dayOfWeek = 9),
            slot(dayOfWeek = 4, startTime = "pas une heure"),
        ).nextLive(thursdayAt20h)

        assertThat(nextLive).isNull()
    }

    private fun slot(
        dayOfWeek: Int,
        startTime: String = "21:00:00",
        title: String = "Live",
    ) = Streamer.ScheduleSlot(dayOfWeek = dayOfWeek, startTime = startTime, title = title)
}

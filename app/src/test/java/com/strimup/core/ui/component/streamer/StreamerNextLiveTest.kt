package com.strimup.core.ui.component.streamer

import com.google.common.truth.Truth.assertThat
import com.strimup.core.streamer.domain.entity.Streamer
import org.junit.Test
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class StreamerNextLiveTest {

    private val paris = ZoneId.of("Europe/Paris")
    private val thursday = LocalDate.of(2026, 10, 8)

    @Test
    fun `a live later today should be today with the local time`() {
        val nextLive = nextLive(startsAt = "2026-10-08T19:30:00Z", title = "Ranked")

        assertThat(nextLive.toDisplay(today = thursday, zone = paris)).isEqualTo(
            StreamerNextLive(day = NextLiveDay.Today, startTime = "21:30", title = "Ranked"),
        )
    }

    @Test
    fun `a live tomorrow should be tomorrow`() {
        val display = nextLive(startsAt = "2026-10-09T18:00:00Z").toDisplay(today = thursday, zone = paris)

        assertThat(display?.day).isEqualTo(NextLiveDay.Tomorrow)
    }

    @Test
    fun `a live later in the week should give its day`() {
        val display = nextLive(startsAt = "2026-10-10T18:00:00Z").toDisplay(today = thursday, zone = paris)

        assertThat(display?.day).isEqualTo(NextLiveDay.Later(DayOfWeek.SATURDAY))
    }

    @Test
    fun `the day and time should follow the phone time zone`() {
        val montreal = ZoneId.of("America/Montreal")

        val display = nextLive(startsAt = "2026-10-09T02:00:00Z").toDisplay(today = thursday, zone = montreal)

        assertThat(display?.day).isEqualTo(NextLiveDay.Today)
        assertThat(display?.startTime).isEqualTo("22:00")
    }

    @Test
    fun `a live on a past day should not be shown`() {
        val display = nextLive(startsAt = "2026-10-07T18:00:00Z").toDisplay(today = thursday, zone = paris)

        assertThat(display).isNull()
    }

    private fun nextLive(
        startsAt: String,
        title: String = "Live",
    ) = Streamer.NextLive(title = title, startsAt = Instant.parse(startsAt))
}

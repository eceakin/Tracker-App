package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DaylightSavingTest {
    private val berlin = ZoneId.of("Europe/Berlin")

    @Test
    fun NFR_CMP_02_ilkbaharGecisi_yatsiPenceresi8Saat() {
        val yatsi = PrayerDayWindows(day("2026-03-28"), day("2026-03-29"))
            .windowOf(Prayer.YATSI)

        assertEquals(Instant.parse("2026-03-28T19:00:00Z"), yatsi.start)
        assertEquals(Instant.parse("2026-03-29T03:00:00Z"), yatsi.end)
        assertEquals(Duration.ofHours(8), yatsi.duration)
    }

    @Test
    fun NFR_CMP_02_sonbaharGecisi_yatsiPenceresi10Saat() {
        val yatsi = PrayerDayWindows(day("2026-10-24"), day("2026-10-25"))
            .windowOf(Prayer.YATSI)

        assertEquals(Instant.parse("2026-10-24T18:00:00Z"), yatsi.start)
        assertEquals(Instant.parse("2026-10-25T04:00:00Z"), yatsi.end)
        assertEquals(Duration.ofHours(10), yatsi.duration)
    }

    @Test
    fun NFR_CMP_02_ilkbaharGecisi_namazGunu() {
        val localDayTimes = day("2026-03-29")

        assertEquals(
            LocalDate.parse("2026-03-28"),
            prayerDateOf(clockAt("2026-03-29T00:59:00Z"), berlin, localDayTimes)
        )
        assertEquals(
            LocalDate.parse("2026-03-28"),
            prayerDateOf(clockAt("2026-03-29T01:30:00Z"), berlin, localDayTimes)
        )
        assertEquals(
            LocalDate.parse("2026-03-29"),
            prayerDateOf(clockAt("2026-03-29T03:00:00Z"), berlin, localDayTimes)
        )
    }

    @Test
    fun NFR_CMP_02_sonbaharGecisi_ikiKezYasanan0230() {
        val localDayTimes = day("2026-10-25")

        assertEquals(
            LocalDate.parse("2026-10-24"),
            prayerDateOf(clockAt("2026-10-25T00:30:00Z"), berlin, localDayTimes)
        )
        assertEquals(
            LocalDate.parse("2026-10-24"),
            prayerDateOf(clockAt("2026-10-25T01:30:00Z"), berlin, localDayTimes)
        )
        assertEquals(
            LocalDate.parse("2026-10-25"),
            prayerDateOf(clockAt("2026-10-25T04:00:00Z"), berlin, localDayTimes)
        )
    }

    @Test
    fun NFR_CMP_02_gecisGunundeVakitlerUtcDogru() {
        val spring = PrayerDayWindows(day("2026-03-29"), day("2026-03-30"))
        assertEquals(
            TimeWindow(
                Instant.parse("2026-03-29T10:30:00Z"),
                Instant.parse("2026-03-29T13:30:00Z")
            ),
            spring.windowOf(Prayer.OGLE)
        )

        val fall = PrayerDayWindows(day("2026-10-25"), day("2026-10-26"))
        assertEquals(
            TimeWindow(
                Instant.parse("2026-10-25T11:30:00Z"),
                Instant.parse("2026-10-25T14:30:00Z")
            ),
            fall.windowOf(Prayer.OGLE)
        )
    }

    private fun day(date: String): PrayerTimesDay = PrayerTimesDay(
        district = DistrictId(1),
        date = LocalDate.parse(date),
        imsak = at(date, "05:00"),
        gunes = at(date, "06:30"),
        ogle = at(date, "12:30"),
        ikindi = at(date, "15:30"),
        aksam = at(date, "18:30"),
        yatsi = at(date, "20:00"),
        origin = TimesOrigin.DIYANET
    )

    private fun at(date: String, time: String): Instant = LocalDate.parse(date)
        .atTime(LocalTime.parse(time))
        .atZone(berlin)
        .toInstant()

    private fun clockAt(instant: String): Clock = Clock.fixed(Instant.parse(instant), berlin)
}

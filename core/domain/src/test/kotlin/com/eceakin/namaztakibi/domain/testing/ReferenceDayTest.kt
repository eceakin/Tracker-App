package com.eceakin.namaztakibi.domain.testing

import com.eceakin.namaztakibi.domain.times.DistrictId
import com.eceakin.namaztakibi.domain.times.TimesOrigin
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ReferenceDayTest {
    @Test
    fun K17_G1Pazartesi_G5Cuma() {
        assertEquals(DayOfWeek.MONDAY, ReferenceDay.G1.dayOfWeek)
        assertEquals(DayOfWeek.MONDAY, ReferenceDay.date(1).dayOfWeek)
        assertEquals(DayOfWeek.FRIDAY, ReferenceDay.date(5).dayOfWeek)
    }

    @Test
    fun K17_vakitlerKararTablosuFiksturuyleAyni() {
        val day = ReferenceDay.times(1)

        assertEquals(DistrictId(1), day.district)
        assertEquals(LocalDate.of(2026, 3, 2), day.date)
        assertEquals(TimesOrigin.DIYANET, day.origin)
        assertEquals(LocalTime.of(5, 30), localTime(day.imsak))
        assertEquals(LocalTime.of(7, 0), localTime(day.gunes))
        assertEquals(LocalTime.of(12, 30), localTime(day.ogle))
        assertEquals(LocalTime.of(15, 30), localTime(day.ikindi))
        assertEquals(LocalTime.of(18, 0), localTime(day.aksam))
        assertEquals(LocalTime.of(19, 30), localTime(day.yatsi))
    }

    @Test
    fun K17_G6_1700_UtcOlarak1400() {
        assertEquals(
            Instant.parse("2026-03-07T14:00:00Z"),
            ReferenceDay.at(6, "17:00")
        )
    }

    @Test
    fun K17_ertesiGunImsaki_G2_0530() {
        assertEquals(ReferenceDay.at(2, "05:30"), ReferenceDay.times(2).imsak)
    }

    @Test
    fun K17_G41_tarihi() {
        assertEquals(LocalDate.of(2026, 4, 11), ReferenceDay.date(41))
    }

    @Test
    fun K17_clockAt_sabitAnVeIstanbul() {
        val clock = ReferenceDay.clockAt(6, "17:00")

        assertEquals(ReferenceDay.at(6, "17:00"), clock.instant())
        assertEquals(ReferenceDay.ZONE, clock.zone)
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("gecersizGirdiler")
    fun K17_gecersizGirdilerReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    private fun localTime(instant: Instant): LocalTime =
        instant.atZone(ReferenceDay.ZONE).toLocalTime()

    private companion object {
        @JvmStatic
        fun gecersizGirdiler(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "K-17 day 0 reddedilir",
                Executable { ReferenceDay.date(0) }
            ),
            Arguments.argumentSet(
                "K-17 day -1 reddedilir",
                Executable { ReferenceDay.date(-1) }
            ),
            Arguments.argumentSet(
                "K-17 7:00 bicimi reddedilir",
                Executable { ReferenceDay.at(1, "7:00") }
            ),
            Arguments.argumentSet(
                "K-17 17:00:30 bicimi reddedilir",
                Executable { ReferenceDay.at(1, "17:00:30") }
            ),
            Arguments.argumentSet(
                "K-17 24:00 bicimi reddedilir",
                Executable { ReferenceDay.at(1, "24:00") }
            ),
            Arguments.argumentSet(
                "K-17 17.00 bicimi reddedilir",
                Executable { ReferenceDay.at(1, "17.00") }
            )
        )
    }
}

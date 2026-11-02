package com.eceakin.namaztakibi.domain.times

import java.time.Instant
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class PrayerTimesDayTest {
    @Test
    fun M24_gecerliGunKurulurVeAlanlarOkunur() {
        val day = baseDay()

        assertEquals(DistrictId(1), day.district)
        assertEquals(LocalDate.of(2026, 3, 2), day.date)
        assertEquals(Instant.parse("2026-03-02T02:30:00Z"), day.imsak)
        assertEquals(Instant.parse("2026-03-02T04:00:00Z"), day.gunes)
        assertEquals(Instant.parse("2026-03-02T09:30:00Z"), day.ogle)
        assertEquals(Instant.parse("2026-03-02T12:30:00Z"), day.ikindi)
        assertEquals(Instant.parse("2026-03-02T15:00:00Z"), day.aksam)
        assertEquals(Instant.parse("2026-03-02T16:30:00Z"), day.yatsi)
        assertEquals(TimesOrigin.DIYANET, day.origin)
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("siraliOlmayanVakitler")
    fun M24_siraliOlmayanVakitlerReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    @Test
    fun M24_saniyeliVakitReddedilir() {
        assertThrows<IllegalArgumentException> {
            baseDay().copy(imsak = baseDay().imsak.plusSeconds(1))
        }
    }

    @Test
    fun M24_nanosaniyeliVakitReddedilir() {
        assertThrows<IllegalArgumentException> {
            baseDay().copy(imsak = baseDay().imsak.plusNanos(1))
        }
    }

    @ParameterizedTest(name = "M-24 DistrictId {0} reddedilir")
    @ValueSource(ints = [0, -1])
    fun M24_gecersizDistrictReddedilir(value: Int) {
        assertThrows<IllegalArgumentException> { DistrictId(value) }
    }

    private companion object {
        @JvmStatic
        fun siraliOlmayanVakitler(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "M-24 imsak ile gunes ayni",
                Executable { baseDay().copy(imsak = baseDay().gunes) }
            ),
            Arguments.argumentSet(
                "M-24 gunes ile ogle ayni",
                Executable { baseDay().copy(gunes = baseDay().ogle) }
            ),
            Arguments.argumentSet(
                "M-24 ogle ile ikindi ayni",
                Executable { baseDay().copy(ogle = baseDay().ikindi) }
            ),
            Arguments.argumentSet(
                "M-24 ikindi ile aksam ayni",
                Executable { baseDay().copy(ikindi = baseDay().aksam) }
            ),
            Arguments.argumentSet(
                "M-24 aksam ile yatsi ayni",
                Executable { baseDay().copy(aksam = baseDay().yatsi) }
            )
        )

        private fun baseDay(): PrayerTimesDay = PrayerTimesDay(
            district = DistrictId(1),
            date = LocalDate.of(2026, 3, 2),
            imsak = Instant.parse("2026-03-02T02:30:00Z"),
            gunes = Instant.parse("2026-03-02T04:00:00Z"),
            ogle = Instant.parse("2026-03-02T09:30:00Z"),
            ikindi = Instant.parse("2026-03-02T12:30:00Z"),
            aksam = Instant.parse("2026-03-02T15:00:00Z"),
            yatsi = Instant.parse("2026-03-02T16:30:00Z"),
            origin = TimesOrigin.DIYANET
        )
    }
}

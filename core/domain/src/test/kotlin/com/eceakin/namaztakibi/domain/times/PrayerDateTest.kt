package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class PrayerDateTest {
    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("namazGunleri")
    fun KS_TRK_11_geceYarisindanImsakaOncekiNamazGunu(
        clock: Clock,
        localDayTimes: PrayerTimesDay,
        expected: LocalDate
    ) {
        assertEquals(
            expected,
            prayerDateOf(clock, ReferenceDay.ZONE, localDayTimes)
        )
    }

    @Test
    fun M23_yanlisGununVakitleriReddedilir() {
        assertThrows<IllegalArgumentException> {
            prayerDateOf(
                ReferenceDay.clockAt(2, "00:30"),
                ReferenceDay.ZONE,
                ReferenceDay.times(1)
            )
        }
    }

    @Test
    fun M23_saatinBolgesiKullanilmaz() {
        val clock = Clock.fixed(ReferenceDay.at(2, "01:00"), ZoneOffset.UTC)

        assertEquals(
            ReferenceDay.date(1),
            prayerDateOf(clock, ReferenceDay.ZONE, ReferenceDay.times(2))
        )
    }

    private companion object {
        @JvmStatic
        fun namazGunleri(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "KS-TRK-11 G1 23:59 times(1) -> G1",
                ReferenceDay.clockAt(1, "23:59"),
                ReferenceDay.times(1),
                ReferenceDay.date(1)
            ),
            Arguments.argumentSet(
                "KS-TRK-11 G2 00:00 times(2) -> G1",
                ReferenceDay.clockAt(2, "00:00"),
                ReferenceDay.times(2),
                ReferenceDay.date(1)
            ),
            Arguments.argumentSet(
                "KS-TRK-11 G2 00:30 times(2) -> G1",
                ReferenceDay.clockAt(2, "00:30"),
                ReferenceDay.times(2),
                ReferenceDay.date(1)
            ),
            Arguments.argumentSet(
                "KS-TRK-11 G2 05:29 times(2) -> G1",
                ReferenceDay.clockAt(2, "05:29"),
                ReferenceDay.times(2),
                ReferenceDay.date(1)
            ),
            Arguments.argumentSet(
                "KS-TRK-11 G2 05:29:59 times(2) -> G1",
                Clock.fixed(ReferenceDay.at(2, "05:29").plusSeconds(59), ReferenceDay.ZONE),
                ReferenceDay.times(2),
                ReferenceDay.date(1)
            ),
            Arguments.argumentSet(
                "KS-TRK-11 G2 05:30 times(2) -> G2",
                ReferenceDay.clockAt(2, "05:30"),
                ReferenceDay.times(2),
                ReferenceDay.date(2)
            )
        )
    }
}

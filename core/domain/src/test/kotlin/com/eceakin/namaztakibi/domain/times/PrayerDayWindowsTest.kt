package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class PrayerDayWindowsTest {
    private val windows = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("vakitAraliklari")
    fun BR02_BR04_vakitAraliklari(prayer: Prayer, expected: TimeWindow) {
        assertEquals(expected, windows.windowOf(prayer))
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("kacirildiAnlari")
    fun BR04_kacirildiAnlari(prayer: Prayer, expected: Instant) {
        assertEquals(expected, windows.missedAt(prayer))
    }

    @Test
    fun BR02_gunesIleOgleArasiHicbirKaleminPenceresiDegil() {
        val instances = listOf(
            ReferenceDay.at(1, "07:00"),
            ReferenceDay.at(1, "09:00"),
            ReferenceDay.at(1, "12:29")
        )

        Prayer.entries.forEach { prayer ->
            instances.forEach { instant ->
                assertFalse(windows.windowOf(prayer).contains(instant))
            }
        }
        assertTrue(windows.windowOf(Prayer.OGLE).contains(ReferenceDay.at(1, "12:30")))
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("gecersizGunCiftleri")
    fun M24_gecersizGunCiftiReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    private companion object {
        @JvmStatic
        fun vakitAraliklari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "BR-02 SABAH 05:30-07:00",
                Prayer.SABAH,
                TimeWindow(ReferenceDay.at(1, "05:30"), ReferenceDay.at(1, "07:00"))
            ),
            Arguments.argumentSet(
                "BR-02 OGLE 12:30-15:30",
                Prayer.OGLE,
                TimeWindow(ReferenceDay.at(1, "12:30"), ReferenceDay.at(1, "15:30"))
            ),
            Arguments.argumentSet(
                "BR-02 IKINDI 15:30-18:00",
                Prayer.IKINDI,
                TimeWindow(ReferenceDay.at(1, "15:30"), ReferenceDay.at(1, "18:00"))
            ),
            Arguments.argumentSet(
                "BR-02 AKSAM 18:00-19:30",
                Prayer.AKSAM,
                TimeWindow(ReferenceDay.at(1, "18:00"), ReferenceDay.at(1, "19:30"))
            ),
            Arguments.argumentSet(
                "BR-02 YATSI 19:30-G2 05:30",
                Prayer.YATSI,
                TimeWindow(ReferenceDay.at(1, "19:30"), ReferenceDay.at(2, "05:30"))
            ),
            Arguments.argumentSet(
                "BR-03 VITIR 19:30-G2 05:30",
                Prayer.VITIR,
                TimeWindow(ReferenceDay.at(1, "19:30"), ReferenceDay.at(2, "05:30"))
            )
        )

        @JvmStatic
        fun kacirildiAnlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "BR-04 SABAH 07:00",
                Prayer.SABAH,
                ReferenceDay.at(1, "07:00")
            ),
            Arguments.argumentSet(
                "BR-04 OGLE 15:30",
                Prayer.OGLE,
                ReferenceDay.at(1, "15:30")
            ),
            Arguments.argumentSet(
                "BR-04 IKINDI 18:00",
                Prayer.IKINDI,
                ReferenceDay.at(1, "18:00")
            ),
            Arguments.argumentSet(
                "BR-04 AKSAM 19:30",
                Prayer.AKSAM,
                ReferenceDay.at(1, "19:30")
            ),
            Arguments.argumentSet(
                "BR-04 YATSI G2 05:30",
                Prayer.YATSI,
                ReferenceDay.at(2, "05:30")
            ),
            Arguments.argumentSet(
                "BR-04 VITIR G2 05:30",
                Prayer.VITIR,
                ReferenceDay.at(2, "05:30")
            )
        )

        @JvmStatic
        fun gecersizGunCiftleri(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "M-24 farkli ilce",
                Executable {
                    PrayerDayWindows(
                        ReferenceDay.times(1),
                        ReferenceDay.times(2).copy(district = DistrictId(2))
                    )
                }
            ),
            Arguments.argumentSet(
                "M-24 ardisik olmayan tarih",
                Executable {
                    PrayerDayWindows(
                        ReferenceDay.times(1),
                        ReferenceDay.times(3)
                    )
                }
            ),
            Arguments.argumentSet(
                "M-24 yatsi ertesi imsaktan sonra",
                Executable {
                    PrayerDayWindows(
                        ReferenceDay.times(1),
                        ReferenceDay.times(2).copy(imsak = ReferenceDay.at(1, "19:30"))
                    )
                }
            )
        )
    }
}

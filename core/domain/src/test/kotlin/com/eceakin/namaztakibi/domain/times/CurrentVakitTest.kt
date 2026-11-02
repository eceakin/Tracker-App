package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.Clock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CurrentVakitTest {
    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("vakitler")
    fun BR02_BR04_suAnkiVakit(day: Int, time: String, expected: ExpectedVakit) {
        val actual = windows.currentVakit(ReferenceDay.clockAt(day, time))

        when (expected) {
            is ExpectedVakit.Within -> {
                assertTrue(actual is CurrentVakit.Within)
                val within = actual as CurrentVakit.Within
                assertEquals(expected.prayer, within.prayer)
                assertEquals(windows.windowOf(expected.prayer), within.window)
            }

            is ExpectedVakit.Outside -> {
                assertTrue(actual is CurrentVakit.Outside)
                val outside = actual as CurrentVakit.Outside
                assertEquals(expected.nextPrayer, outside.nextPrayer)
                assertEquals(ReferenceDay.at(day, "12:30"), outside.nextStart)
            }
        }
    }

    @Test
    fun BR04_saniyeliAnSinirinDogruTarafinda() {
        val justBeforeBoundary = windows.currentVakit(
            Clock.fixed(ReferenceDay.at(1, "15:29").plusSeconds(59), ReferenceDay.ZONE)
        )
        assertTrue(justBeforeBoundary is CurrentVakit.Within)
        assertEquals(Prayer.OGLE, (justBeforeBoundary as CurrentVakit.Within).prayer)

        val boundary = windows.currentVakit(ReferenceDay.clockAt(1, "15:30"))
        assertTrue(boundary is CurrentVakit.Within)
        assertEquals(Prayer.IKINDI, (boundary as CurrentVakit.Within).prayer)
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("namazGunuDisindakiAnlar")
    fun M23_namazGunuDisindakiAnReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    sealed interface ExpectedVakit {
        data class Within(val prayer: Prayer) : ExpectedVakit

        data class Outside(val nextPrayer: Prayer) : ExpectedVakit
    }

    private companion object {
        private val windows = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))

        @JvmStatic
        fun vakitler(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "BR-02 G1 05:30 SABAH",
                1,
                "05:30",
                ExpectedVakit.Within(Prayer.SABAH)
            ),
            Arguments.argumentSet(
                "BR-02 G1 06:59 SABAH",
                1,
                "06:59",
                ExpectedVakit.Within(Prayer.SABAH)
            ),
            Arguments.argumentSet(
                "BR-02 G1 07:00 OGLE disinda",
                1,
                "07:00",
                ExpectedVakit.Outside(Prayer.OGLE)
            ),
            Arguments.argumentSet(
                "BR-02 G1 12:29 OGLE disinda",
                1,
                "12:29",
                ExpectedVakit.Outside(Prayer.OGLE)
            ),
            Arguments.argumentSet(
                "BR-02 G1 12:30 OGLE",
                1,
                "12:30",
                ExpectedVakit.Within(Prayer.OGLE)
            ),
            Arguments.argumentSet(
                "BR-02 G1 15:29 OGLE",
                1,
                "15:29",
                ExpectedVakit.Within(Prayer.OGLE)
            ),
            Arguments.argumentSet(
                "BR-02 G1 15:30 IKINDI",
                1,
                "15:30",
                ExpectedVakit.Within(Prayer.IKINDI)
            ),
            Arguments.argumentSet(
                "BR-02 G1 17:59 IKINDI",
                1,
                "17:59",
                ExpectedVakit.Within(Prayer.IKINDI)
            ),
            Arguments.argumentSet(
                "BR-02 G1 18:00 AKSAM",
                1,
                "18:00",
                ExpectedVakit.Within(Prayer.AKSAM)
            ),
            Arguments.argumentSet(
                "BR-02 G1 19:29 AKSAM",
                1,
                "19:29",
                ExpectedVakit.Within(Prayer.AKSAM)
            ),
            Arguments.argumentSet(
                "BR-02 G1 19:30 YATSI",
                1,
                "19:30",
                ExpectedVakit.Within(Prayer.YATSI)
            ),
            Arguments.argumentSet(
                "BR-02 G2 00:30 YATSI",
                2,
                "00:30",
                ExpectedVakit.Within(Prayer.YATSI)
            ),
            Arguments.argumentSet(
                "BR-02 G2 05:29 YATSI",
                2,
                "05:29",
                ExpectedVakit.Within(Prayer.YATSI)
            )
        )

        @JvmStatic
        fun namazGunuDisindakiAnlar(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "M-23 G1 05:29",
                Executable { windows.currentVakit(ReferenceDay.clockAt(1, "05:29")) }
            ),
            Arguments.argumentSet(
                "M-23 G2 05:30",
                Executable { windows.currentVakit(ReferenceDay.clockAt(2, "05:30")) }
            )
        )
    }
}

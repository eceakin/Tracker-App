package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.rules.RuleSet
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class KerahatTest {
    @Test
    fun BR07_kerahatPencereleri() {
        assertEquals(
            listOf(
                KerahatWindow(
                    KerahatKind.GUNES,
                    TimeWindow(ReferenceDay.at(1, "07:00"), ReferenceDay.at(1, "07:45"))
                ),
                KerahatWindow(
                    KerahatKind.ISTIVA,
                    TimeWindow(ReferenceDay.at(1, "11:45"), ReferenceDay.at(1, "12:30"))
                ),
                KerahatWindow(
                    KerahatKind.AKSAM,
                    TimeWindow(ReferenceDay.at(1, "17:15"), ReferenceDay.at(1, "18:00"))
                )
            ),
            windows.kerahatWindows(RuleSet.HANEFI)
        )
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("kerahatSinirlari")
    fun BR07_kerahatSinirlari(time: String, expected: ExpectedKerahat) {
        val actual = windows.kerahatStatus(ReferenceDay.clockAt(1, time), RuleSet.HANEFI)

        when (expected) {
            is ExpectedKerahat.None -> assertEquals(KerahatStatus.None, actual)

            is ExpectedKerahat.Inside -> {
                assertTrue(actual is KerahatStatus.Inside)
                val inside = actual as KerahatStatus.Inside
                assertEquals(expected.kind, inside.kerahat.kind)
            }
        }
    }

    @Test
    fun BR07_pencereSuresiRuleSettenGelir() {
        val ruleSet = RuleSet.HANEFI.copy(kerahatAfterGunes = Duration.ofMinutes(40))
        val gunes = windows.kerahatWindows(ruleSet).first { it.kind == KerahatKind.GUNES }

        assertEquals(
            TimeWindow(ReferenceDay.at(1, "07:00"), ReferenceDay.at(1, "07:40")),
            gunes.window
        )
        assertEquals(
            KerahatStatus.None,
            windows.kerahatStatus(ReferenceDay.clockAt(1, "07:40"), ruleSet)
        )
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("namazGunuDisindakiAnlar")
    fun M23_kerahatDurumuNamazGunuDisindaReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    sealed interface ExpectedKerahat {
        data object None : ExpectedKerahat

        data class Inside(val kind: KerahatKind) : ExpectedKerahat
    }

    private companion object {
        private val windows = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))

        @JvmStatic
        fun kerahatSinirlari(): List<Arguments> = listOf(
            Arguments.argumentSet("BR-07 G1 06:59 None", "06:59", ExpectedKerahat.None),
            Arguments.argumentSet(
                "BR-07 G1 07:00 GUNES",
                "07:00",
                ExpectedKerahat.Inside(KerahatKind.GUNES)
            ),
            Arguments.argumentSet(
                "BR-07 G1 07:44 GUNES",
                "07:44",
                ExpectedKerahat.Inside(KerahatKind.GUNES)
            ),
            Arguments.argumentSet("BR-07 G1 07:45 None", "07:45", ExpectedKerahat.None),
            Arguments.argumentSet("BR-07 G1 11:44 None", "11:44", ExpectedKerahat.None),
            Arguments.argumentSet(
                "BR-07 G1 11:45 ISTIVA",
                "11:45",
                ExpectedKerahat.Inside(KerahatKind.ISTIVA)
            ),
            Arguments.argumentSet(
                "BR-07 G1 12:29 ISTIVA",
                "12:29",
                ExpectedKerahat.Inside(KerahatKind.ISTIVA)
            ),
            Arguments.argumentSet("BR-07 G1 12:30 None", "12:30", ExpectedKerahat.None),
            Arguments.argumentSet("BR-07 G1 17:14 None", "17:14", ExpectedKerahat.None),
            Arguments.argumentSet(
                "BR-07 G1 17:15 AKSAM",
                "17:15",
                ExpectedKerahat.Inside(KerahatKind.AKSAM)
            ),
            Arguments.argumentSet(
                "BR-07 G1 17:59 AKSAM",
                "17:59",
                ExpectedKerahat.Inside(KerahatKind.AKSAM)
            ),
            Arguments.argumentSet("BR-07 G1 18:00 None", "18:00", ExpectedKerahat.None)
        )

        @JvmStatic
        fun namazGunuDisindakiAnlar(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "M-23 G1 05:29",
                Executable {
                    windows.kerahatStatus(ReferenceDay.clockAt(1, "05:29"), RuleSet.HANEFI)
                }
            ),
            Arguments.argumentSet(
                "M-23 G2 05:30",
                Executable {
                    windows.kerahatStatus(ReferenceDay.clockAt(2, "05:30"), RuleSet.HANEFI)
                }
            )
        )
    }
}

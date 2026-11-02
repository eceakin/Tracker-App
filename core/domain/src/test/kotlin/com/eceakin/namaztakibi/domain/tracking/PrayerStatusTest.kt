package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import java.time.Clock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class PrayerStatusTest {
    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("durumlar")
    fun DATA05_durumTuretilir(
        record: DayRecord,
        prayer: Prayer,
        clock: Clock,
        expected: PrayerStatus
    ) {
        assertEquals(expected, record.statusOf(prayer, windows, clock))
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("yanlisGunler")
    fun M24_yanlisGununVakitleriReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    private companion object {
        private val windows = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))

        @JvmStatic
        fun durumlar(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "DATA-05 ogle bos 15:29 belirsiz",
                DayRecord.empty(ReferenceDay.date(1)),
                Prayer.OGLE,
                ReferenceDay.clockAt(1, "15:29"),
                PrayerStatus.BELIRSIZ
            ),
            Arguments.argumentSet(
                "DATA-05 ogle bos 15:30 kilinmadi",
                DayRecord.empty(ReferenceDay.date(1)),
                Prayer.OGLE,
                ReferenceDay.clockAt(1, "15:30"),
                PrayerStatus.KILINMADI
            ),
            Arguments.argumentSet(
                "DATA-05 ogle 12:40 isaretli 16:00 kilindi",
                markedRecord(Prayer.OGLE, asKaza = false),
                Prayer.OGLE,
                ReferenceDay.clockAt(1, "16:00"),
                PrayerStatus.KILINDI
            ),
            Arguments.argumentSet(
                "DATA-05 ogle kaza isaretli 16:00 kilindi",
                markedRecord(Prayer.OGLE, asKaza = true),
                Prayer.OGLE,
                ReferenceDay.clockAt(1, "16:00"),
                PrayerStatus.KILINDI
            ),
            Arguments.argumentSet(
                "DATA-05 vitir bos G2 05:29 belirsiz",
                DayRecord.empty(ReferenceDay.date(1)),
                Prayer.VITIR,
                ReferenceDay.clockAt(2, "05:29"),
                PrayerStatus.BELIRSIZ
            ),
            Arguments.argumentSet(
                "DATA-05 vitir bos G2 05:30 kilinmadi",
                DayRecord.empty(ReferenceDay.date(1)),
                Prayer.VITIR,
                ReferenceDay.clockAt(2, "05:30"),
                PrayerStatus.KILINMADI
            )
        )

        @JvmStatic
        fun yanlisGunler(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "M-24 statusOf yanlis gun",
                Executable {
                    DayRecord.empty(ReferenceDay.date(1)).statusOf(
                        Prayer.OGLE,
                        PrayerDayWindows(ReferenceDay.times(2), ReferenceDay.times(3)),
                        ReferenceDay.clockAt(2, "13:00")
                    )
                }
            )
        )

        private fun markedRecord(prayer: Prayer, asKaza: Boolean): DayRecord = DayRecord(
            date = ReferenceDay.date(1),
            marks = listOf(
                PrayerMark(
                    prayer = prayer,
                    markedAt = ReferenceDay.at(1, "12:40"),
                    asCuma = false,
                    asKaza = asKaza
                )
            )
        )
    }
}

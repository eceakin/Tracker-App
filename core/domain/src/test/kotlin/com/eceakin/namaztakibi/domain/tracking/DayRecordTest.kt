package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.OperationId
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.LocalDate
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class DayRecordTest {
    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("gunlukKayitDurumlari")
    fun DATA05_gunlukKayitDegismezleri(action: Executable) {
        action.execute()
    }

    private companion object {
        @JvmStatic
        fun gunlukKayitDurumlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "DATA-05 ayni kaleme iki isaret",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        DayRecord(
                            date = ReferenceDay.date(1),
                            marks = listOf(mark(Prayer.OGLE), mark(Prayer.OGLE))
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-05 yatsisiz vitir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        DayRecord(
                            date = ReferenceDay.date(1),
                            marks = listOf(mark(Prayer.VITIR))
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-05 cuma olmayan gunde cuma",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        DayRecord(
                            date = ReferenceDay.date(1),
                            marks = listOf(mark(Prayer.OGLE, asCuma = true))
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-05 ogle disinda cuma",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        DayRecord(
                            date = ReferenceDay.date(5),
                            marks = listOf(mark(Prayer.IKINDI, asCuma = true))
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-05 cuma ve kaza birlikte",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        DayRecord(
                            date = ReferenceDay.date(5),
                            marks = listOf(mark(Prayer.OGLE, asCuma = true, asKaza = true))
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-05 G5 ogle cuma kabul",
                Executable {
                    DayRecord(
                        date = ReferenceDay.date(5),
                        marks = listOf(mark(Prayer.OGLE, asCuma = true))
                    )
                }
            ),
            Arguments.argumentSet(
                "DATA-05 yatsi ve vitir kabul",
                Executable {
                    DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(mark(Prayer.YATSI), mark(Prayer.VITIR))
                    )
                }
            )
        )

        private fun mark(
            prayer: Prayer,
            asCuma: Boolean = false,
            asKaza: Boolean = false
        ): PrayerMark = PrayerMark(
            prayer = prayer,
            markedAt = ReferenceDay.at(1, "13:00"),
            asCuma = asCuma,
            asKaza = asKaza,
            operationId = if (asKaza) OperationId("op1") else null
        )
    }
}

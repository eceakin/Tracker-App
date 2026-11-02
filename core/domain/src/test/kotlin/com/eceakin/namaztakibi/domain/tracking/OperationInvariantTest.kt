package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.KazaEntry
import com.eceakin.namaztakibi.domain.kaza.KazaEntryKind
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.KazaPerformed
import com.eceakin.namaztakibi.domain.kaza.KazaRecordId
import com.eceakin.namaztakibi.domain.kaza.OperationId
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class OperationInvariantTest {
    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("islemDurumlari")
    fun DATA04_islemDegismezleri(action: Executable) {
        action.execute()
    }

    private companion object {
        @JvmStatic
        fun islemDurumlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "DATA-04 duzeltme isleme ait olamaz",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(
                            kind = KazaEntryKind.DUZELTME,
                            count = -1,
                            corrects = KazaRecordId("e0"),
                            operationId = OperationId("op1")
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 islem kayitlari ayni kalemde",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        KazaLedger(
                            entries = listOf(
                                entry(
                                    prayer = Prayer.OGLE,
                                    kind = KazaEntryKind.ONERI,
                                    operationId = OperationId("op1")
                                )
                            ),
                            performed = listOf(
                                performed(
                                    prayer = Prayer.IKINDI,
                                    operationId = OperationId("op1")
                                )
                            )
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-05 kaza isareti islemsiz olamaz",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        DayRecord(
                            date = ReferenceDay.date(1),
                            marks = listOf(
                                mark(
                                    asKaza = true,
                                    operationId = null
                                )
                            )
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-05 islemli isaret kaza olmali",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        DayRecord(
                            date = ReferenceDay.date(1),
                            marks = listOf(
                                mark(
                                    asKaza = false,
                                    operationId = OperationId("op1")
                                )
                            )
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 islemli kayitlar kabul",
                Executable {
                    KazaLedger(
                        entries = listOf(
                            entry(
                                prayer = Prayer.OGLE,
                                kind = KazaEntryKind.ONERI,
                                operationId = OperationId("op1")
                            )
                        ),
                        performed = listOf(
                            performed(
                                prayer = Prayer.OGLE,
                                operationId = OperationId("op1")
                            )
                        )
                    )
                    DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(
                            mark(
                                asKaza = true,
                                operationId = OperationId("op1")
                            )
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "DATA-04 bos islem kimligi",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        OperationId(" ")
                    }
                }
            )
        )

        private fun entry(
            prayer: Prayer = Prayer.OGLE,
            kind: KazaEntryKind = KazaEntryKind.ONERI,
            count: Int = 1,
            corrects: KazaRecordId? = null,
            operationId: OperationId? = null
        ): KazaEntry = KazaEntry(
            id = KazaRecordId("e1"),
            prayer = prayer,
            sourceDate = null,
            kind = kind,
            count = count,
            corrects = corrects,
            missedInSefer = false,
            createdAt = ReferenceDay.at(1, "16:00"),
            operationId = operationId
        )

        private fun performed(
            prayer: Prayer = Prayer.OGLE,
            operationId: OperationId? = null
        ): KazaPerformed = KazaPerformed(
            id = KazaRecordId("p1"),
            prayer = prayer,
            performedAt = ReferenceDay.at(1, "16:00"),
            operationId = operationId
        )

        private fun mark(asKaza: Boolean, operationId: OperationId?): PrayerMark = PrayerMark(
            prayer = Prayer.OGLE,
            markedAt = ReferenceDay.at(1, "16:00"),
            asCuma = false,
            asKaza = asKaza,
            operationId = operationId
        )
    }
}

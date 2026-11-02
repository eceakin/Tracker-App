package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.KazaEntry
import com.eceakin.namaztakibi.domain.kaza.KazaEntryKind
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.KazaPerformed
import com.eceakin.namaztakibi.domain.kaza.KazaRecordId
import com.eceakin.namaztakibi.domain.kaza.KazaWrite
import com.eceakin.namaztakibi.domain.kaza.OperationId
import com.eceakin.namaztakibi.domain.kaza.UndoBlock
import com.eceakin.namaztakibi.domain.kaza.UndoDecision
import com.eceakin.namaztakibi.domain.settings.DaySettings
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import com.eceakin.namaztakibi.domain.tracking.testing.LateMarkFixture
import java.time.Clock
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

private fun List<MissedOutcome>.hasOutcomeFor(prayer: Prayer): Boolean = any { outcome ->
    when (outcome) {
        is MissedOutcome.Suggest -> outcome.prayer == prayer
        is MissedOutcome.AutoAdd -> outcome.write.prayer == prayer
    }
}

class OperationUndoTest {
    private val windows1 = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))
    private val g1 = ReferenceDay.date(1)
    private val op1 = OperationId("op1")

    @Test
    fun KS_KAZ_10_oneriBekleyenVakitKazaOlarak_tekIslem_birlikteGeriAlinir() {
        val (record, ledger) = applyLateMark(
            record = DayRecord.empty(g1),
            ledger = KazaLedger.EMPTY,
            choice = LateChoice.AS_KAZA,
            clock = ReferenceDay.clockAt(2, "10:00")
        )
        val entryId = ledger.entries.single().id
        val performedId = ledger.performed.single().id

        val decision = decideUndoOperation(
            op1,
            record,
            ledger,
            ReferenceDay.clockAt(2, "10:05")
        )
        assertEquals(
            OperationUndoDecision.Allowed(
                clear = RecordWrite.Clear(g1, Prayer.OGLE),
                kazaWrites = listOf(
                    KazaWrite.AddEntry(
                        prayer = Prayer.OGLE,
                        sourceDate = g1,
                        kind = KazaEntryKind.DUZELTME,
                        count = -1,
                        corrects = entryId,
                        missedInSefer = false,
                        createdAt = ReferenceDay.at(2, "10:05")
                    ),
                    KazaWrite.AddEntry(
                        prayer = Prayer.OGLE,
                        sourceDate = null,
                        kind = KazaEntryKind.DUZELTME,
                        count = 1,
                        corrects = performedId,
                        missedInSefer = false,
                        createdAt = ReferenceDay.at(2, "10:05")
                    )
                )
            ),
            decision
        )

        val allowed = decision as OperationUndoDecision.Allowed
        val (newRecord, newLedger) = LateMarkFixture.applyUndo(record, ledger, allowed)
        assertEquals(0, newLedger.balance(Prayer.OGLE))
        assertTrue(newRecord.markOf(Prayer.OGLE) == null)
        assertTrue(
            newRecord.evaluateMissed(
                windows1,
                newLedger,
                DaySettings(autoAddDebt = false),
                ReferenceDay.at(1, "00:00"),
                ReferenceDay.clockAt(2, "10:05")
            ).hasOutcomeFor(Prayer.OGLE)
        )
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("islemGeriAlmaDurumlari")
    fun FR_KAZ_05_islemBirlikteGeriAlinir(action: Executable) {
        action.execute()
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("tekKayitDurumlari")
    fun FR_KAZ_05_islemKaydiTekBasinaGeriAlinmaz(action: Executable) {
        action.execute()
    }

    private fun applyLateMark(
        record: DayRecord,
        ledger: KazaLedger,
        choice: LateChoice,
        clock: Clock
    ): Pair<DayRecord, KazaLedger> {
        val decision = record.decideLateMark(
            Prayer.OGLE,
            choice,
            windows1,
            ledger,
            clock
        )
        assertTrue(decision is LateMarkDecision.Allowed)
        return LateMarkFixture.apply(
            record,
            ledger,
            (decision as LateMarkDecision.Allowed).writes,
            op1
        )
    }

    private companion object {
        @JvmStatic
        fun islemGeriAlmaDurumlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-KAZ-05 yalniz kilinan kaza islemi",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(entry("e1", KazaEntryKind.ONERI, ReferenceDay.date(1))),
                        performed = emptyList()
                    )
                    val record = DayRecord.empty(ReferenceDay.date(1))
                    val decision = record.decideLateMark(
                        Prayer.OGLE,
                        LateChoice.AS_KAZA,
                        w1(),
                        ledger,
                        ReferenceDay.clockAt(1, "16:00")
                    )
                    val allowedLate = decision as LateMarkDecision.Allowed
                    val (markedRecord, operationLedger) = LateMarkFixture.apply(
                        record,
                        ledger,
                        allowedLate.writes,
                        OperationId("op1")
                    )
                    val performedId = operationLedger.performed.single().id

                    val undo = decideUndoOperation(
                        OperationId("op1"),
                        markedRecord,
                        operationLedger,
                        ReferenceDay.clockAt(1, "16:05")
                    )
                    assertEquals(
                        OperationUndoDecision.Allowed(
                            clear = RecordWrite.Clear(ReferenceDay.date(1), Prayer.OGLE),
                            kazaWrites = listOf(
                                KazaWrite.AddEntry(
                                    prayer = Prayer.OGLE,
                                    sourceDate = null,
                                    kind = KazaEntryKind.DUZELTME,
                                    count = 1,
                                    corrects = performedId,
                                    missedInSefer = false,
                                    createdAt = ReferenceDay.at(1, "16:05")
                                )
                            )
                        ),
                        undo
                    )

                    val (undoneRecord, undoneLedger) = LateMarkFixture.applyUndo(
                        markedRecord,
                        operationLedger,
                        undo as OperationUndoDecision.Allowed
                    )
                    assertEquals(1, undoneLedger.balance(Prayer.OGLE))
                    assertEquals(
                        PrayerStatus.KILINMADI,
                        undoneRecord.statusOf(
                            Prayer.OGLE,
                            w1(),
                            ReferenceDay.clockAt(1, "16:05")
                        )
                    )
                    assertFalse(
                        undoneRecord.evaluateMissed(
                            w1(),
                            undoneLedger,
                            DaySettings(autoAddDebt = false),
                            ReferenceDay.at(1, "00:00"),
                            ReferenceDay.clockAt(1, "16:05")
                        ).hasOutcomeFor(Prayer.OGLE)
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 islem bulunamadi",
                Executable {
                    assertEquals(
                        OperationUndoDecision.Blocked(OperationUndoBlock.NOT_FOUND),
                        decideUndoOperation(
                            OperationId("op9"),
                            DayRecord.empty(ReferenceDay.date(1)),
                            KazaLedger.EMPTY,
                            ReferenceDay.clockAt(1, "16:05")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 islem zaten geri alindi",
                Executable {
                    val (record, ledger) = appliedKsKaz10()
                    val undo = decideUndoOperation(
                        OperationId("op1"),
                        record,
                        ledger,
                        ReferenceDay.clockAt(2, "10:05")
                    )
                    val (_, undoneLedger) = LateMarkFixture.applyUndo(
                        record,
                        ledger,
                        undo as OperationUndoDecision.Allowed
                    )
                    val recreatedRecord = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(
                            PrayerMark(
                                prayer = Prayer.OGLE,
                                markedAt = ReferenceDay.at(1, "16:00"),
                                asCuma = false,
                                asKaza = true,
                                operationId = OperationId("op1")
                            )
                        )
                    )
                    assertEquals(
                        OperationUndoDecision.Blocked(OperationUndoBlock.ALREADY_UNDONE),
                        decideUndoOperation(
                            OperationId("op1"),
                            recreatedRecord,
                            undoneLedger,
                            ReferenceDay.clockAt(2, "10:10")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 gun kaydinda isaret yok",
                Executable {
                    val (_, ledger) = appliedKsKaz10()
                    assertEquals(
                        OperationUndoDecision.Blocked(OperationUndoBlock.NOT_FOUND),
                        decideUndoOperation(
                            OperationId("op1"),
                            DayRecord.empty(ReferenceDay.date(1)),
                            ledger,
                            ReferenceDay.clockAt(2, "10:10")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 borc eksiye duserse",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(
                            entry(
                                id = "e1",
                                kind = KazaEntryKind.ONERI,
                                sourceDate = ReferenceDay.date(1),
                                operationId = OperationId("op1")
                            )
                        ),
                        performed = listOf(
                            KazaPerformed(
                                id = KazaRecordId("p1"),
                                prayer = Prayer.OGLE,
                                performedAt = ReferenceDay.at(1, "15:45")
                            )
                        )
                    )
                    val record = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(
                            PrayerMark(
                                prayer = Prayer.OGLE,
                                markedAt = ReferenceDay.at(1, "16:00"),
                                asCuma = false,
                                asKaza = true,
                                operationId = OperationId("op1")
                            )
                        )
                    )
                    assertEquals(
                        OperationUndoDecision.Blocked(OperationUndoBlock.WOULD_GO_NEGATIVE),
                        decideUndoOperation(
                            OperationId("op1"),
                            record,
                            ledger,
                            ReferenceDay.clockAt(1, "16:05")
                        )
                    )
                }
            )
        )

        @JvmStatic
        fun tekKayitDurumlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-KAZ-05 islemdeki oneri kaydi",
                Executable {
                    val (_, ledger) = appliedKsKaz10()
                    val entryId = ledger.entries.first().id
                    assertEquals(
                        UndoDecision.Blocked(UndoBlock.PART_OF_OPERATION),
                        ledger.decideUndo(entryId, ReferenceDay.clockAt(2, "10:05"))
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 islemdeki kilinan kaza",
                Executable {
                    val (_, ledger) = appliedKsKaz10()
                    val performedId = ledger.performed.single().id
                    assertEquals(
                        UndoDecision.Blocked(UndoBlock.PART_OF_OPERATION),
                        ledger.decideUndo(performedId, ReferenceDay.clockAt(2, "10:05"))
                    )
                }
            )
        )

        private fun appliedKsKaz10(): Pair<DayRecord, KazaLedger> {
            val record = DayRecord.empty(ReferenceDay.date(1))
            val decision = record.decideLateMark(
                Prayer.OGLE,
                LateChoice.AS_KAZA,
                w1(),
                KazaLedger.EMPTY,
                ReferenceDay.clockAt(2, "10:00")
            )
            val allowed = decision as LateMarkDecision.Allowed
            return LateMarkFixture.apply(
                record,
                KazaLedger.EMPTY,
                allowed.writes,
                OperationId("op1")
            )
        }

        private fun entry(
            id: String,
            kind: KazaEntryKind,
            sourceDate: LocalDate?,
            operationId: OperationId? = null
        ): KazaEntry = KazaEntry(
            id = KazaRecordId(id),
            prayer = Prayer.OGLE,
            sourceDate = sourceDate,
            kind = kind,
            count = 1,
            corrects = null,
            missedInSefer = false,
            createdAt = ReferenceDay.at(1, "15:40"),
            operationId = operationId
        )

        private fun w1(): PrayerDayWindows =
            PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))
    }
}

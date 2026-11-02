package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.testing.LedgerFixture
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UndoAndSetDebtTest {
    private val clock = ReferenceDay.clockAt(2, "10:00")
    private val now: Instant = ReferenceDay.at(2, "10:00")

    @Test
    fun KT81_FR_KAZ_05_onaylananOneriGeriAlinir_duzeltmeKaydiEklenir() {
        val ledger = KazaLedger(
            entries = listOf(
                entry(
                    id = "e1",
                    kind = KazaEntryKind.ONERI,
                    count = 1,
                    sourceDate = ReferenceDay.date(1)
                )
            ),
            performed = emptyList()
        )

        val decision = ledger.decideUndo(KazaRecordId("e1"), clock)
        assertTrue(decision is UndoDecision.Allowed)
        val allowed = decision as UndoDecision.Allowed
        assertEquals(
            KazaWrite.AddEntry(
                prayer = Prayer.OGLE,
                sourceDate = ReferenceDay.date(1),
                kind = KazaEntryKind.DUZELTME,
                count = -1,
                corrects = KazaRecordId("e1"),
                missedInSefer = false,
                createdAt = now
            ),
            allowed.write
        )

        val after = LedgerFixture.apply(ledger, listOf(allowed.write))
        assertEquals(2, after.entries.size)
        assertEquals(0, after.balance(Prayer.OGLE))
        assertTrue(after.isCorrected(KazaRecordId("e1")))
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("geriAlmaKurallari")
    fun FR_KAZ_05_geriAlmaKurallari(action: Executable) {
        action.execute()
    }

    @Test
    fun KS_KAZ_01_borcElleDuzenlenir_duzeltmeKaydiSaklanir() {
        val ledger = ledgerWithDebt(8)

        val decision = ledger.decideSetDebt(Prayer.OGLE, 10, clock)
        assertTrue(decision is SetDebtDecision.Allowed)
        val allowed = decision as SetDebtDecision.Allowed
        assertEquals(
            KazaWrite.AddEntry(
                prayer = Prayer.OGLE,
                sourceDate = null,
                kind = KazaEntryKind.DUZELTME,
                count = 2,
                corrects = null,
                missedInSefer = false,
                createdAt = now
            ),
            allowed.write
        )

        val after = LedgerFixture.apply(ledger, listOf(allowed.write))
        assertEquals(10, after.balance(Prayer.OGLE))
    }

    @Test
    fun KS_ONB_04_baslangicBorcuKalemBasinaTekElleKaydi() {
        val targets = linkedMapOf(
            Prayer.SABAH to 10,
            Prayer.OGLE to 8,
            Prayer.IKINDI to 8,
            Prayer.AKSAM to 5,
            Prayer.YATSI to 9,
            Prayer.VITIR to 9
        )
        var ledger = KazaLedger.EMPTY

        targets.forEach { (prayer, target) ->
            val decision = ledger.decideSetDebt(prayer, target, clock)
            assertTrue(decision is SetDebtDecision.Allowed)
            val allowed = decision as SetDebtDecision.Allowed
            assertEquals(KazaEntryKind.ELLE, allowed.write.kind)
            assertEquals(target, allowed.write.count)
            ledger = LedgerFixture.apply(ledger, listOf(allowed.write))
        }

        targets.forEach { (prayer, target) ->
            assertEquals(target, ledger.balance(prayer))
            assertEquals(1, ledger.entries.count { it.prayer == prayer })
        }
        assertEquals(49, targets.values.sum())
        assertEquals(49, Prayer.entries.sumOf { ledger.balance(it) })
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("elleDuzenlemeSinirlari")
    fun FR_KAZ_01_elleDuzenlemeSinirlari(action: Executable) {
        action.execute()
    }

    private companion object {
        private val now: Instant = ReferenceDay.at(2, "10:00")

        @JvmStatic
        fun geriAlmaKurallari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-KAZ-05 olmayan kayit",
                Executable {
                    assertEquals(
                        UndoDecision.Blocked(UndoBlock.NOT_FOUND),
                        KazaLedger.EMPTY.decideUndo(KazaRecordId("missing"), clock())
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 duzeltme geri alinmaz",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(
                            entry("e1", kind = KazaEntryKind.ONERI, count = 1),
                            entry(
                                "d1",
                                kind = KazaEntryKind.DUZELTME,
                                count = -1,
                                corrects = KazaRecordId("e1")
                            )
                        ),
                        performed = emptyList()
                    )
                    assertEquals(
                        UndoDecision.Blocked(UndoBlock.CORRECTION_NOT_UNDOABLE),
                        ledger.decideUndo(KazaRecordId("d1"), clock())
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 ikinci geri alma",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(
                            entry("e1", kind = KazaEntryKind.ONERI, count = 1),
                            entry(
                                "d1",
                                kind = KazaEntryKind.DUZELTME,
                                count = -1,
                                corrects = KazaRecordId("e1")
                            )
                        ),
                        performed = emptyList()
                    )
                    assertEquals(
                        UndoDecision.Blocked(UndoBlock.ALREADY_CORRECTED),
                        ledger.decideUndo(KazaRecordId("e1"), clock())
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 negatife dusuren geri alma",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(entry("e1", kind = KazaEntryKind.ONERI, count = 1)),
                        performed = listOf(performed("p1"))
                    )
                    assertEquals(
                        UndoDecision.Blocked(UndoBlock.WOULD_GO_NEGATIVE),
                        ledger.decideUndo(KazaRecordId("e1"), clock())
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-05 kilinan kaza geri alinir",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(entry("e1", kind = KazaEntryKind.ELLE, count = 2)),
                        performed = listOf(performed("p1"))
                    )
                    val decision = ledger.decideUndo(KazaRecordId("p1"), clock())
                    assertTrue(decision is UndoDecision.Allowed)
                    val allowed = decision as UndoDecision.Allowed
                    assertEquals(
                        KazaWrite.AddEntry(
                            prayer = Prayer.OGLE,
                            sourceDate = null,
                            kind = KazaEntryKind.DUZELTME,
                            count = 1,
                            corrects = KazaRecordId("p1"),
                            missedInSefer = false,
                            createdAt = now
                        ),
                        allowed.write
                    )
                    val after = LedgerFixture.apply(ledger, listOf(allowed.write))
                    assertEquals(2, after.balance(Prayer.OGLE))
                }
            )
        )

        @JvmStatic
        fun elleDuzenlemeSinirlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-KAZ-01 negatif hedef",
                Executable {
                    assertEquals(
                        SetDebtDecision.Blocked(SetDebtBlock.NEGATIVE_TARGET),
                        ledgerWithDebt(8).decideSetDebt(Prayer.OGLE, -1, clock())
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-01 ayni sayi",
                Executable {
                    assertEquals(
                        SetDebtDecision.NoChange,
                        ledgerWithDebt(8).decideSetDebt(Prayer.OGLE, 8, clock())
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-01 sifira indirme",
                Executable {
                    val decision = ledgerWithDebt(8).decideSetDebt(Prayer.OGLE, 0, clock())
                    assertTrue(decision is SetDebtDecision.Allowed)
                    val allowed = decision as SetDebtDecision.Allowed
                    assertEquals(KazaEntryKind.DUZELTME, allowed.write.kind)
                    assertEquals(-8, allowed.write.count)
                }
            ),
            Arguments.argumentSet(
                "FR-KAZ-01 bos kalemde sifir",
                Executable {
                    assertEquals(
                        SetDebtDecision.NoChange,
                        KazaLedger.EMPTY.decideSetDebt(Prayer.OGLE, 0, clock())
                    )
                }
            )
        )

        private fun clock(): java.time.Clock = ReferenceDay.clockAt(2, "10:00")

        private fun ledgerWithDebt(count: Int): KazaLedger = KazaLedger(
            entries = listOf(entry("e1", kind = KazaEntryKind.ELLE, count = count)),
            performed = emptyList()
        )

        private fun entry(
            id: String,
            kind: KazaEntryKind,
            count: Int,
            corrects: KazaRecordId? = null,
            sourceDate: java.time.LocalDate? = null
        ): KazaEntry = KazaEntry(
            id = KazaRecordId(id),
            prayer = Prayer.OGLE,
            sourceDate = sourceDate,
            kind = kind,
            count = count,
            corrects = corrects,
            missedInSefer = false,
            createdAt = now
        )

        private fun performed(id: String): KazaPerformed = KazaPerformed(
            id = KazaRecordId(id),
            prayer = Prayer.OGLE,
            performedAt = now
        )
    }
}

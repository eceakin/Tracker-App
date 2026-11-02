package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.testing.LedgerFixture
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PerformKazaTest {
    private val clock = ReferenceDay.clockAt(1, "14:00")
    private val now: Instant = ReferenceDay.at(1, "14:00")

    @Test
    fun KT35_FR_KAZ_04_borcSifirkenKazaKildim_onceEkleSonraDusSorulur() {
        val ledger = KazaLedger.EMPTY
        val decision = ledger.decidePerform(Prayer.OGLE, clock)

        assertTrue(decision is PerformDecision.NeedsConfirmation)
        val needsConfirmation = decision as PerformDecision.NeedsConfirmation
        assertEquals(PerformQuestion.NO_DEBT_ADD_FIRST, needsConfirmation.question)
        assertEquals(
            listOf(
                KazaWrite.AddEntry(
                    prayer = Prayer.OGLE,
                    sourceDate = null,
                    kind = KazaEntryKind.ELLE,
                    count = 1,
                    corrects = null,
                    missedInSefer = false,
                    createdAt = now
                ),
                KazaWrite.AddPerformed(prayer = Prayer.OGLE, performedAt = now)
            ),
            needsConfirmation.writesIfConfirmed
        )

        assertEquals(KazaLedger.EMPTY, ledger)
        assertEquals(0, ledger.balance(Prayer.OGLE))

        val afterEntry = LedgerFixture.apply(
            ledger,
            listOf(needsConfirmation.writesIfConfirmed.first())
        )
        assertEquals(1, afterEntry.balance(Prayer.OGLE))

        val afterPerform = LedgerFixture.apply(
            afterEntry,
            needsConfirmation.writesIfConfirmed.drop(1)
        )
        assertEquals(0, afterPerform.balance(Prayer.OGLE))
        assertEquals(1, afterPerform.entries.count { it.kind == KazaEntryKind.ELLE })
        assertEquals(1, afterPerform.performed.size)
    }

    @Test
    fun KT36_FR_KAZ_02_vakitSecilmedenKazaKaydedilemez() {
        val ledger = ledgerWithDebt(Prayer.IKINDI, 2)
        val entriesBefore = ledger.entries.toList()
        val performedBefore = ledger.performed.toList()

        assertEquals(
            PerformDecision.Blocked(PerformBlock.NO_PRAYER_SELECTED),
            ledger.decidePerform(null, clock)
        )
        assertEquals(entriesBefore, ledger.entries)
        assertEquals(performedBefore, ledger.performed)
    }

    @Test
    fun KS_KAZ_02_kilinanKazaSecilenVakittenDuser() {
        val ledger = KazaLedger(
            entries = listOf(
                entry("o1", Prayer.OGLE, 3),
                entry("i1", Prayer.IKINDI, 2)
            ),
            performed = emptyList()
        )

        val decision = ledger.decidePerform(Prayer.IKINDI, clock)
        assertTrue(decision is PerformDecision.Allowed)
        val allowed = decision as PerformDecision.Allowed
        assertEquals(
            listOf(KazaWrite.AddPerformed(prayer = Prayer.IKINDI, performedAt = now)),
            allowed.writes
        )

        val after = LedgerFixture.apply(ledger, allowed.writes)
        assertEquals(1, after.balance(Prayer.IKINDI))
        assertEquals(3, after.balance(Prayer.OGLE))
    }

    private fun ledgerWithDebt(prayer: Prayer, count: Int): KazaLedger = KazaLedger(
        entries = listOf(entry("e1", prayer, count)),
        performed = emptyList()
    )

    private fun entry(id: String, prayer: Prayer, count: Int): KazaEntry = KazaEntry(
        id = KazaRecordId(id),
        prayer = prayer,
        sourceDate = null,
        kind = KazaEntryKind.ELLE,
        count = count,
        corrects = null,
        missedInSefer = false,
        createdAt = now
    )
}

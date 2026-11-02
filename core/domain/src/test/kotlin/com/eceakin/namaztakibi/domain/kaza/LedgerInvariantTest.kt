package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.testing.LedgerFixture
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LedgerInvariantTest {
    private val clock = ReferenceDay.clockAt(2, "10:00")

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("senaryolar")
    fun FR_KAZ_04_borcAslaNegatifOlmaz(commands: List<Command>, expectedBalances: List<Int>) {
        var ledger = KazaLedger.EMPTY
        val balances = mutableListOf<Int>()

        commands.forEach { command ->
            ledger = execute(ledger, command)
            val balance = ledger.balance(Prayer.OGLE)
            assertTrue(balance >= 0)
            balances += balance
        }

        assertEquals(expectedBalances, balances)
    }

    private fun execute(ledger: KazaLedger, command: Command): KazaLedger = when (command) {
        is Command.Perform -> when (val decision = ledger.decidePerform(Prayer.OGLE, clock)) {
            is PerformDecision.Allowed -> LedgerFixture.apply(ledger, decision.writes)

            is PerformDecision.NeedsConfirmation ->
                if (command.confirm) {
                    LedgerFixture.apply(ledger, decision.writesIfConfirmed)
                } else {
                    ledger
                }

            is PerformDecision.Blocked -> ledger
        }

        is Command.SetDebt -> when (
            val decision = ledger.decideSetDebt(Prayer.OGLE, command.target, clock)
        ) {
            is SetDebtDecision.Allowed -> LedgerFixture.apply(ledger, listOf(decision.write))
            SetDebtDecision.NoChange -> ledger
            is SetDebtDecision.Blocked -> ledger
        }

        is Command.UndoEntry -> when (
            val decision = ledger.decideUndo(ledger.entries[command.index].id, clock)
        ) {
            is UndoDecision.Allowed -> LedgerFixture.apply(ledger, listOf(decision.write))
            is UndoDecision.Blocked -> ledger
        }

        is Command.UndoPerformed -> when (
            val decision = ledger.decideUndo(ledger.performed[command.index].id, clock)
        ) {
            is UndoDecision.Allowed -> LedgerFixture.apply(ledger, listOf(decision.write))
            is UndoDecision.Blocked -> ledger
        }
    }

    sealed interface Command {
        data class Perform(val confirm: Boolean) : Command

        data class SetDebt(val target: Int) : Command

        data class UndoEntry(val index: Int) : Command

        data class UndoPerformed(val index: Int) : Command
    }

    private companion object {
        @JvmStatic
        fun senaryolar(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-KAZ-04 sifirdan onayli kaza ve geri alma",
                listOf(
                    Command.Perform(confirm = true),
                    Command.UndoPerformed(index = 0)
                ),
                listOf(0, 1)
            ),
            Arguments.argumentSet(
                "FR-KAZ-04 vazgecilen soru",
                listOf(
                    Command.Perform(confirm = false),
                    Command.Perform(confirm = false)
                ),
                listOf(0, 0)
            ),
            Arguments.argumentSet(
                "FR-KAZ-04 borc biterken",
                listOf(
                    Command.SetDebt(target = 2),
                    Command.Perform(confirm = true),
                    Command.Perform(confirm = true),
                    Command.Perform(confirm = false)
                ),
                listOf(2, 1, 0, 0)
            ),
            Arguments.argumentSet(
                "FR-KAZ-04 negatife dusuren geri alma engellenir",
                listOf(
                    Command.SetDebt(target = 1),
                    Command.Perform(confirm = true),
                    Command.UndoEntry(index = 0)
                ),
                listOf(1, 0, 0)
            ),
            Arguments.argumentSet(
                "FR-KAZ-04 sifira indirme ve negatif hedef",
                listOf(
                    Command.SetDebt(target = 5),
                    Command.SetDebt(target = 0),
                    Command.SetDebt(target = -1)
                ),
                listOf(5, 0, 0)
            )
        )
    }
}

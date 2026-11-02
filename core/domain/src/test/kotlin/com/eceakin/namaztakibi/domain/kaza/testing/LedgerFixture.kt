package com.eceakin.namaztakibi.domain.kaza.testing

import com.eceakin.namaztakibi.domain.kaza.KazaEntry
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.KazaPerformed
import com.eceakin.namaztakibi.domain.kaza.KazaRecordId
import com.eceakin.namaztakibi.domain.kaza.KazaWrite
import com.eceakin.namaztakibi.domain.kaza.OperationId

/**
 * Kaza defterinin bellek içi test karşılığı (K-14).
 *
 * Yazmaları sırayla uygular ve her adımda yeni bir [KazaLedger] kurar; böylece defterin
 * değişmezleri her adımda denetlenir. Kimlikleri `w1`, `w2`, … diye üretir.
 */
object LedgerFixture {
    fun apply(
        ledger: KazaLedger,
        writes: List<KazaWrite>,
        operationId: OperationId? = null
    ): KazaLedger {
        var current = ledger
        writes.forEach { write ->
            val id = nextId(current)
            current = when (write) {
                is KazaWrite.AddEntry -> current.copy(
                    entries = current.entries + KazaEntry(
                        id = id,
                        prayer = write.prayer,
                        sourceDate = write.sourceDate,
                        kind = write.kind,
                        count = write.count,
                        corrects = write.corrects,
                        missedInSefer = write.missedInSefer,
                        createdAt = write.createdAt,
                        operationId = operationId
                    )
                )

                is KazaWrite.AddPerformed -> current.copy(
                    performed = current.performed + KazaPerformed(
                        id = id,
                        prayer = write.prayer,
                        performedAt = write.performedAt,
                        operationId = operationId
                    )
                )
            }
        }
        return current
    }

    private fun nextId(ledger: KazaLedger): KazaRecordId {
        val usedIds = (ledger.entries.map { it.id } + ledger.performed.map { it.id }).toSet()
        var index = ledger.entries.size + ledger.performed.size + 1
        while (KazaRecordId("w$index") in usedIds) {
            index++
        }
        return KazaRecordId("w$index")
    }
}

package com.eceakin.namaztakibi.domain.tracking.testing

import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.OperationId
import com.eceakin.namaztakibi.domain.kaza.testing.LedgerFixture
import com.eceakin.namaztakibi.domain.tracking.DayRecord
import com.eceakin.namaztakibi.domain.tracking.LateMarkWrites
import com.eceakin.namaztakibi.domain.tracking.OperationUndoDecision

/**
 * Geç işaretleme ve işlem geri alma yazmalarını uygulayan test yardımcısı (K-14).
 */
object LateMarkFixture {
    fun apply(
        record: DayRecord,
        ledger: KazaLedger,
        writes: LateMarkWrites,
        operationId: OperationId?
    ): Pair<DayRecord, KazaLedger> {
        require((operationId != null) == writes.grouped) {
            "grouped yazmalar işlem kimliği ister; gruplanmamış yazmalar istemez"
        }
        val newRecord = DayRecordFixture.apply(record, listOf(writes.mark), operationId)
        val newLedger = LedgerFixture.apply(ledger, writes.kazaWrites, operationId)
        return newRecord to newLedger
    }

    fun applyUndo(
        record: DayRecord,
        ledger: KazaLedger,
        decision: OperationUndoDecision.Allowed
    ): Pair<DayRecord, KazaLedger> {
        val newRecord = DayRecordFixture.apply(record, listOf(decision.clear), null)
        val newLedger = LedgerFixture.apply(ledger, decision.kazaWrites, null)
        return newRecord to newLedger
    }
}

package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.kaza.KazaEntryKind
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.KazaWrite
import com.eceakin.namaztakibi.domain.kaza.OperationId
import java.time.Clock

/**
 * Bir işlemin bütün olarak geri alınmasının sonucu (FR-KAZ-05, DATA-04).
 *
 * Motor veri yazmaz; [Allowed] yalnızca uygulanırsa yazılacak işareti ve düzeltmeleri taşır.
 */
sealed interface OperationUndoDecision {
    /** İşlem geri alınabilir; [clear] ve [kazaWrites] yazılır. */
    data class Allowed(val clear: RecordWrite.Clear, val kazaWrites: List<KazaWrite.AddEntry>) :
        OperationUndoDecision

    /** İşlem geri alınamaz; hiçbir şey yazılmaz. */
    data class Blocked(val reason: OperationUndoBlock) : OperationUndoDecision
}

/** İşlem geri alma engelinin nedeni. */
enum class OperationUndoBlock {
    NOT_FOUND,
    ALREADY_UNDONE,
    WOULD_GO_NEGATIVE
}

/**
 * Bir işlemi bütün olarak geri alma kararı (FR-KAZ-05, DATA-04).
 *
 * Sıra: işlem yok; işlem zaten geri alınmış; gün kaydında işaret yok; düzeltmeler borcu
 * negatife düşürür; aksi hâlde işaret silinir ve bütün kayıtlar için düzeltme önerilir.
 * Düzeltmeler işlem kimliği taşımaz; motor kimlik üretmez.
 */
fun decideUndoOperation(
    operationId: OperationId,
    record: DayRecord,
    ledger: KazaLedger,
    clock: Clock
): OperationUndoDecision {
    val entries = ledger.entries.filter { it.operationId == operationId }
    val performed = ledger.performed.filter { it.operationId == operationId }
    if (entries.isEmpty() && performed.isEmpty()) {
        return OperationUndoDecision.Blocked(OperationUndoBlock.NOT_FOUND)
    }
    if (entries.any { ledger.isCorrected(it.id) } ||
        performed.any { ledger.isCorrected(it.id) }
    ) {
        return OperationUndoDecision.Blocked(OperationUndoBlock.ALREADY_UNDONE)
    }

    val mark = record.marks.firstOrNull { it.operationId == operationId }
        ?: return OperationUndoDecision.Blocked(OperationUndoBlock.NOT_FOUND)
    val now = clock.instant()
    val corrections = entries.map { entry ->
        KazaWrite.AddEntry(
            prayer = entry.prayer,
            sourceDate = entry.sourceDate,
            kind = KazaEntryKind.DUZELTME,
            count = -entry.count,
            corrects = entry.id,
            missedInSefer = entry.missedInSefer,
            createdAt = now
        )
    } + performed.map { performedRecord ->
        KazaWrite.AddEntry(
            prayer = performedRecord.prayer,
            sourceDate = null,
            kind = KazaEntryKind.DUZELTME,
            count = 1,
            corrects = performedRecord.id,
            missedInSefer = false,
            createdAt = now
        )
    }

    if (ledger.balance(mark.prayer) + corrections.sumOf { it.count } < 0) {
        return OperationUndoDecision.Blocked(OperationUndoBlock.WOULD_GO_NEGATIVE)
    }
    return OperationUndoDecision.Allowed(
        clear = RecordWrite.Clear(date = record.date, prayer = mark.prayer),
        kazaWrites = corrections
    )
}

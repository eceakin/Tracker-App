package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.kaza.KazaWrite

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

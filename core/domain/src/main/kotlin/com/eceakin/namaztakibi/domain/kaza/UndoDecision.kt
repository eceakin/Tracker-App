package com.eceakin.namaztakibi.domain.kaza

/**
 * Bir kaydı geri alma kararı (FR-KAZ-05, DATA-04).
 *
 * Kayıt silinmez; uygulanırsa [Allowed.write] ile karşı etkili bir düzeltme kaydı eklenir.
 */
sealed interface UndoDecision {
    /** Geri alma uygulanabilir; bu düzeltme kaydı yazılır. */
    data class Allowed(val write: KazaWrite.AddEntry) : UndoDecision

    /** Geri alma engellenir; hiçbir şey yazılmaz. */
    data class Blocked(val reason: UndoBlock) : UndoDecision
}

/** Geri alma engelinin nedeni. */
enum class UndoBlock {
    NOT_FOUND,
    ALREADY_CORRECTED,
    CORRECTION_NOT_UNDOABLE,
    WOULD_GO_NEGATIVE
}

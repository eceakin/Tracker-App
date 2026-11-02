package com.eceakin.namaztakibi.domain.kaza

/**
 * Bir kalemin borcunu elle düzenleme kararı (FR-KAZ-01, DATA-04).
 *
 * Motor veri yazmaz; yalnızca uygulanırsa yazılacak kaydı önerir.
 */
sealed interface SetDebtDecision {
    /** Borç zaten hedef sayıda; değişiklik yok. */
    data object NoChange : SetDebtDecision

    /** Hedefe ulaşmak için bu kayıt yazılır. */
    data class Allowed(val write: KazaWrite.AddEntry) : SetDebtDecision

    /** Hedef geçersiz; hiçbir şey yazılmaz. */
    data class Blocked(val reason: SetDebtBlock) : SetDebtDecision
}

/** Elle düzenleme engelinin nedeni. */
enum class SetDebtBlock {
    NEGATIVE_TARGET
}

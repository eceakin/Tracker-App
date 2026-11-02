package com.eceakin.namaztakibi.domain.kaza

/**
 * "Kaza kıldım" eyleminin sonucu (FR-KAZ-02, FR-KAZ-04, KT-35, KT-36).
 *
 * Motor veri yazmaz; yalnızca uygulanırsa yazılacak kayıtları önerir (BR-00, M-25).
 */
sealed interface PerformDecision {
    /** Eylem doğrudan uygulanabilir; [writes] yazılır. */
    data class Allowed(val writes: List<KazaWrite>) : PerformDecision

    /** Kullanıcıya soru sorulur; yalnızca onaylanırsa [writesIfConfirmed] yazılır. */
    data class NeedsConfirmation(
        val question: PerformQuestion,
        val writesIfConfirmed: List<KazaWrite>
    ) : PerformDecision

    /** Eylem engellenir; hiçbir şey yazılmaz. */
    data class Blocked(val reason: PerformBlock) : PerformDecision
}

/** "Kaza kıldım" sırasında sorulabilecek sorular. */
enum class PerformQuestion {
    NO_DEBT_ADD_FIRST
}

/** "Kaza kıldım" eyleminin engellenme nedenleri. */
enum class PerformBlock {
    NO_PRAYER_SELECTED
}

package com.eceakin.namaztakibi.domain.tracking

/**
 * İşaretleme kararı (FR-TRK-01 – 03, 06, 07; KT-01 – 06, 15, 17, 18).
 *
 * Motor veri yazmaz; yalnızca uygulanırsa yazılacak işareti önerir.
 */
sealed interface MarkDecision {
    /** İşaret konabilir; [write] yazılır. */
    data class Allowed(val write: RecordWrite.Mark) : MarkDecision

    /** İşaret engellenir; hiçbir şey yazılmaz. */
    data class Blocked(val reason: MarkBlock) : MarkDecision

    /** Vakti çıkmış kalem: "vaktinde / sonradan kaza olarak" seçimi gerekir (F2-04b). */
    data object NeedsLateChoice : MarkDecision
}

/** İşaretleme engelinin nedeni. */
enum class MarkBlock {
    ALREADY_MARKED,
    NOT_YET_IN_TIME,
    YATSI_FIRST,
    CUMA_NOT_ALLOWED
}

/** İşaret kaldırma kararı (FR-TRK-03, DATA-05). */
sealed interface UnmarkDecision {
    /** İşaret kaldırılabilir; [write] yazılır. */
    data class Allowed(val write: RecordWrite.Clear) : UnmarkDecision

    /** Geri alma engellenir; hiçbir şey yazılmaz. */
    data class Blocked(val reason: UnmarkBlock) : UnmarkDecision
}

/** Geri alma engelinin nedeni. */
enum class UnmarkBlock {
    NOT_MARKED,
    UNMARK_VITIR_FIRST,
    KAZA_MARK_NEEDS_GROUP_UNDO
}

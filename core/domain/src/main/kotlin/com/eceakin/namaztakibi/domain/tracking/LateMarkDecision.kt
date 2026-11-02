package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.kaza.KazaWrite

/** Geç işaretlemede kullanıcının seçimi (FR-TRK-04, DATA-05). */
enum class LateChoice {
    ON_TIME,
    AS_KAZA
}

/**
 * Bir geç işaretlemenin yazacakları.
 *
 * [grouped] doğruysa [mark] ve [kazaWrites]'ın hepsi yazan katmanın üreteceği tek bir
 * yeni [com.eceakin.namaztakibi.domain.kaza.OperationId] taşır (DATA-04).
 */
data class LateMarkWrites(
    val mark: RecordWrite.Mark,
    val kazaWrites: List<KazaWrite>,
    val grouped: Boolean
)

/** Geç işaretleme kararı (FR-TRK-04, KT-12, KT-13). */
sealed interface LateMarkDecision {
    /** Doğrudan uygulanabilir. */
    data class Allowed(val writes: LateMarkWrites) : LateMarkDecision

    /** Soru sorulur; cevaba göre [ifConfirmed] ya da [ifDeclined] yazılır. */
    data class NeedsConfirmation(
        val question: LateMarkQuestion,
        val ifConfirmed: LateMarkWrites,
        val ifDeclined: LateMarkWrites
    ) : LateMarkDecision

    /** Geç işaretleme engellenir. */
    data class Blocked(val reason: LateMarkBlock) : LateMarkDecision
}

/** Geç işaretlemede sorulabilecek sorular. */
enum class LateMarkQuestion {
    UNDO_ADDED_DEBT
}

/** Geç işaretleme engelinin nedeni. */
enum class LateMarkBlock {
    ALREADY_MARKED,
    NOT_LATE,
    YATSI_FIRST
}

package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Instant
import java.time.LocalDate

/**
 * Günlük kayda uygulanırsa yazılacak değişiklik önerisi (BR-00, M-25).
 *
 * Motor bu tipleri döndürür ama hiçbir şey yazmaz.
 */
sealed interface RecordWrite {
    /** Kalemi işaretleme önerisi. */
    data class Mark(
        val date: LocalDate,
        val prayer: Prayer,
        val markedAt: Instant,
        val asCuma: Boolean,
        val asKaza: Boolean
    ) : RecordWrite

    /** Kalemin işaretini kaldırma önerisi. */
    data class Clear(val date: LocalDate, val prayer: Prayer) : RecordWrite
}

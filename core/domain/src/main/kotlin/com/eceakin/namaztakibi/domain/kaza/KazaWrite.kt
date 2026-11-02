package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Instant
import java.time.LocalDate

/**
 * Kural motorunun, uygulanırsa yazılacak kaza kaydı önerisi (BR-00, M-25).
 *
 * Motor bu tipleri döndürür ama hiçbir şey yazmaz; yazma Faz 4'ün repository işidir.
 */
sealed interface KazaWrite {
    /** Yeni bir [KazaEntry] yazılması önerisi. */
    data class AddEntry(
        val prayer: Prayer,
        val sourceDate: LocalDate?,
        val kind: KazaEntryKind,
        val count: Int,
        val corrects: KazaRecordId?,
        val missedInSefer: Boolean,
        val createdAt: Instant
    ) : KazaWrite

    /** Yeni bir [KazaPerformed] yazılması önerisi. */
    data class AddPerformed(val prayer: Prayer, val performedAt: Instant) : KazaWrite
}

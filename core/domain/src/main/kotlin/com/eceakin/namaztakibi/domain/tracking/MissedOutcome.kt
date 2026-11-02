package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.KazaEntryKind
import com.eceakin.namaztakibi.domain.kaza.KazaWrite
import java.time.Clock
import java.time.LocalDate

/**
 * Vakti çıkmış, işaretlenmemiş bir kalem için sonuç (FR-KAZ-03, BR-04).
 *
 * Motor veri yazmaz; [AutoAdd] yalnızca uygulanırsa yazılacak kaydı, [Suggest] ise
 * kullanıcıya sorulacak öneriyi taşır.
 */
sealed interface MissedOutcome {
    /** Kullanıcıya "borca ekle" önerisi. */
    data class Suggest(val date: LocalDate, val prayer: Prayer) : MissedOutcome

    /** Otomatik ekleme açık; bu kayıt yazılır. */
    data class AutoAdd(val write: KazaWrite.AddEntry) : MissedOutcome
}

/**
 * "Borca ekle" onayının yazacağı kayıt (FR-KAZ-03, DATA-05).
 *
 * Kayıt türü *öneri*dir; onaylanmış kaçırıldı önerisini temsil eder.
 */
fun confirmSuggestion(suggestion: MissedOutcome.Suggest, clock: Clock): KazaWrite.AddEntry =
    KazaWrite.AddEntry(
        prayer = suggestion.prayer,
        sourceDate = suggestion.date,
        kind = KazaEntryKind.ONERI,
        count = 1,
        corrects = null,
        missedInSefer = false,
        createdAt = clock.instant()
    )

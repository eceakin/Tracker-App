package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Clock

/**
 * Kaza borcunun append-only defteri (M-31, SRS DATA-04).
 *
 * Kalan borç kayıtlardan hesaplanır; hiçbir kayıt silinmez. Bu sınıf tutarlı veriyi temsil
 * eder: Faz 4'te veritabanından okunan defter bu değişmezleri sağlamalıdır.
 *
 * @property entries Borç ekleyen kayıtlar.
 * @property performed Kılınan kaza kayıtları.
 */
data class KazaLedger(val entries: List<KazaEntry>, val performed: List<KazaPerformed>) {
    init {
        val allIds = entries.map { it.id } + performed.map { it.id }
        require(allIds.size == allIds.toSet().size) {
            "Kaza kayıt kimlikleri benzersiz olmalı"
        }

        val entriesById = entries.associateBy { it.id }
        val performedById = performed.associateBy { it.id }
        val correctedTargets = mutableSetOf<KazaRecordId>()
        entries.forEach { entry ->
            val targetId = entry.corrects ?: return@forEach
            val targetEntry = entriesById[targetId]
            val targetPerformed = performedById[targetId]
            require(targetEntry != null || targetPerformed != null) {
                "Düzeltme defterde var olan bir kayda işaret etmeli"
            }
            require(targetEntry?.kind != KazaEntryKind.DUZELTME) {
                "Düzeltme kaydı geri alınamaz"
            }
            require(correctedTargets.add(targetId)) {
                "Bir kayıt yalnızca bir kez geri alınır"
            }
            val targetPrayer = targetEntry?.prayer ?: targetPerformed?.prayer
            require(targetPrayer == entry.prayer) {
                "Düzeltme, işaret ettiği kayıtla aynı kalemde olmalı"
            }
        }

        // FR-KAZ-04 — Borç hiçbir zaman negatif olamaz.
        Prayer.entries.forEach { prayer ->
            require(balance(prayer) >= 0) {
                "Kalan borç negatif olamaz: $prayer"
            }
        }
    }

    /** O kalemin kalan borcu: kayıt adetlerinin toplamı eksi kılınan kaza sayısı (DATA-04). */
    fun balance(prayer: Prayer): Int {
        val added = entries.filter { it.prayer == prayer }.sumOf { it.count }
        val performedCount = performed.count { it.prayer == prayer }
        return added - performedCount
    }

    /** Bu kaydı geri alan bir düzeltme var mı (FR-KAZ-05). */
    fun isCorrected(id: KazaRecordId): Boolean =
        entries.any { it.kind == KazaEntryKind.DUZELTME && it.corrects == id }

    /**
     * "Kaza kıldım" kararı (FR-KAZ-02, FR-KAZ-04, KT-35, KT-36, DATA-04).
     *
     * Kalem seçilmemişse engellenir. Borç varsa yalnızca kılınan kayıt önerilir; borç
     * sıfırsa önce bir `ELLE` kaydı ekleyip sonra düşmek için onay istenir. Hiçbir dalda
     * defter değişmez; borç hiçbir zaman negatife düşmez.
     */
    fun decidePerform(prayer: Prayer?, clock: Clock): PerformDecision {
        if (prayer == null) {
            return PerformDecision.Blocked(PerformBlock.NO_PRAYER_SELECTED)
        }

        val now = clock.instant()
        return if (balance(prayer) >= 1) {
            PerformDecision.Allowed(
                listOf(KazaWrite.AddPerformed(prayer = prayer, performedAt = now))
            )
        } else {
            PerformDecision.NeedsConfirmation(
                question = PerformQuestion.NO_DEBT_ADD_FIRST,
                writesIfConfirmed = listOf(
                    KazaWrite.AddEntry(
                        prayer = prayer,
                        sourceDate = null,
                        kind = KazaEntryKind.ELLE,
                        count = 1,
                        corrects = null,
                        missedInSefer = false,
                        createdAt = now
                    ),
                    KazaWrite.AddPerformed(prayer = prayer, performedAt = now)
                )
            )
        }
    }

    /**
     * Bir kaydı geri alma kararı (FR-KAZ-05, DATA-04).
     *
     * Kayıt silinmez: `KazaEntry` için adedi ters işaretli, `KazaPerformed` için `+1`
     * adetli bir `DUZELTME` kaydı önerilir. Düzeltmeler geri alınamaz; bir kayıt yalnızca
     * bir kez geri alınır; borcu negatife düşürecek geri alma engellenir.
     */
    fun decideUndo(id: KazaRecordId, clock: Clock): UndoDecision {
        val entry = entries.firstOrNull { it.id == id }
        if (entry == null) {
            val performedRecord = performed.firstOrNull { it.id == id }
                ?: return UndoDecision.Blocked(UndoBlock.NOT_FOUND)
            if (isCorrected(id)) {
                return UndoDecision.Blocked(UndoBlock.ALREADY_CORRECTED)
            }
            return UndoDecision.Allowed(
                KazaWrite.AddEntry(
                    prayer = performedRecord.prayer,
                    sourceDate = null,
                    kind = KazaEntryKind.DUZELTME,
                    count = 1,
                    corrects = id,
                    missedInSefer = false,
                    createdAt = clock.instant()
                )
            )
        }

        if (entry.kind == KazaEntryKind.DUZELTME) {
            return UndoDecision.Blocked(UndoBlock.CORRECTION_NOT_UNDOABLE)
        }
        if (isCorrected(id)) {
            return UndoDecision.Blocked(UndoBlock.ALREADY_CORRECTED)
        }
        if (balance(entry.prayer) - entry.count < 0) {
            return UndoDecision.Blocked(UndoBlock.WOULD_GO_NEGATIVE)
        }
        return UndoDecision.Allowed(
            KazaWrite.AddEntry(
                prayer = entry.prayer,
                sourceDate = entry.sourceDate,
                kind = KazaEntryKind.DUZELTME,
                count = -entry.count,
                corrects = id,
                missedInSefer = entry.missedInSefer,
                createdAt = clock.instant()
            )
        )
    }

    /**
     * Bir kalemin borcunu yeni bir sayıya ayarlama kararı (FR-KAZ-01, DATA-04).
     *
     * Kalemde hiç kayıt yoksa `ELLE` kaydı, varsa aradaki fark kadar `DUZELTME` kaydı
     * önerilir. Negatif hedef kabul edilmez; hedef bakiyeye eşitse değişiklik yoktur.
     */
    fun decideSetDebt(prayer: Prayer, target: Int, clock: Clock): SetDebtDecision {
        if (target < 0) {
            return SetDebtDecision.Blocked(SetDebtBlock.NEGATIVE_TARGET)
        }

        val current = balance(prayer)
        if (target == current) {
            return SetDebtDecision.NoChange
        }

        val hasRecords = entries.any { it.prayer == prayer } ||
            performed.any { it.prayer == prayer }
        return SetDebtDecision.Allowed(
            KazaWrite.AddEntry(
                prayer = prayer,
                sourceDate = null,
                kind = if (hasRecords) KazaEntryKind.DUZELTME else KazaEntryKind.ELLE,
                count = if (hasRecords) target - current else target,
                corrects = null,
                missedInSefer = false,
                createdAt = clock.instant()
            )
        )
    }

    companion object {
        /** Karar tablosu: başlangıç kaza borcu tüm kalemlerde 0. */
        val EMPTY: KazaLedger = KazaLedger(entries = emptyList(), performed = emptyList())
    }
}

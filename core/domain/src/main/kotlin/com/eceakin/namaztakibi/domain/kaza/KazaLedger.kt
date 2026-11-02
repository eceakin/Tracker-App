package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer

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

    companion object {
        /** Karar tablosu: başlangıç kaza borcu tüm kalemlerde 0. */
        val EMPTY: KazaLedger = KazaLedger(entries = emptyList(), performed = emptyList())
    }
}

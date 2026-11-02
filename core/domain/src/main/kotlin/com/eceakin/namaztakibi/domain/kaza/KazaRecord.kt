package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Instant
import java.time.LocalDate

/** Kaza defterindeki bir kaydın kimliği (SRS 6.1, DATA-04). */
@JvmInline
value class KazaRecordId(val value: String) {
    init {
        require(value.isNotBlank()) { "Kaza kayıt kimliği boş olamaz" }
    }
}

/**
 * Kaza kaydının türü (SRS 6.1, DATA-04).
 *
 * *elle* kullanıcının doğrudan girdiği borç, *öneri* onaylanan kaçırıldı önerisi,
 * *otomatik* otomatik ekleme ayarıyla yazılan borç, *hesaplayıcı* borç hesaplayıcının
 * sonucu, *düzeltme* elle düzenleme ya da geri almadır.
 */
enum class KazaEntryKind {
    ELLE,
    ONERI,
    OTOMATIK,
    HESAPLAYICI,
    DUZELTME
}

/**
 * Kaza defterine borç ekleyen kayıt (SRS 6.1, DATA-04).
 *
 * @property id Kaydın benzersiz kimliği.
 * @property prayer Kaydın namaz kalemi.
 * @property sourceDate Kaynak tarih; bilinmiyorsa `null`.
 * @property kind Kaydın türü.
 * @property count Borca etkisi; düzeltmede işaretli ve sıfırdan farklı.
 * @property corrects Düzeltilen kaydın kimliği; yalnızca geri alma düzeltmesinde dolu.
 * @property missedInSefer Namaz seferde mi kaçırıldı.
 * @property createdAt Kaydın oluşturulma anı.
 */
data class KazaEntry(
    val id: KazaRecordId,
    val prayer: Prayer,
    val sourceDate: LocalDate?,
    val kind: KazaEntryKind,
    val count: Int,
    val corrects: KazaRecordId?,
    val missedInSefer: Boolean,
    val createdAt: Instant
) {
    init {
        if (kind == KazaEntryKind.DUZELTME) {
            require(count != 0) { "Düzeltme adedi sıfır olamaz" }
        } else {
            require(count > 0) { "Düzeltme dışındaki türlerde adet pozitif olmalı" }
            require(corrects == null) { "Düzeltme dışındaki türler bir kaydı düzeltemez" }
        }
    }
}

/**
 * Kılınan bir kaza kaydı (SRS 6.1, DATA-04).
 *
 * @property id Kaydın benzersiz kimliği.
 * @property prayer Kılınan kazanın kalemi.
 * @property performedAt Kılınma anı.
 */
data class KazaPerformed(val id: KazaRecordId, val prayer: Prayer, val performedAt: Instant)

package com.eceakin.namaztakibi.domain.rules

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Duration

/**
 * Hanefî fıkhi sayılarının tek kaynağı (M-21). Ürünün tek kural seti [HANEFI]'dir.
 *
 * Fıkhi bir sayı ürün kodunda başka yerde tekrarlanmaz; yeni bir mezhep, kod yapısı
 * değişmeden yeni bir [RuleSet] olarak eklenir (NFR-MNT-02).
 *
 * @property version Kural setinin sürüm kimliği; gün anlık kopyası bu kimliği kaydeder
 * (M-21, BR-21).
 * @property rekatCounts Kalem başına rekât sayıları (BR-17; KT-31, KT-84; BR-18).
 * @property kerahatAfterGunes Güneş vaktinden sonraki kerahat penceresi (BR-07).
 * @property kerahatBeforeOgle Öğle vaktinden önceki kerahat penceresi (BR-07).
 * @property kerahatBeforeAksam Akşam vaktinden önceki kerahat penceresi (BR-07).
 * @property hayizMin Hayızın alt sınırı (BR-13).
 * @property hayizMax Hayızın üst sınırı (BR-14, BR-16).
 * @property nifasMax Nifasın üst sınırı (BR-12, BR-15).
 * @property minTuhr İki hayız arasındaki en az temizlik süresi (BR-16).
 * @property defaultAdetHabitDays Âdet alışkanlığı girilmemişse kullanılan gün sayısı
 * (BR-14, FR-MAZ-06).
 * @property tertipFallsAtFarzDebt Tertibin düştüğü vitir hariç farz borcu (BR-17).
 * @property rekatDuration Bir rekât için ayrılan süre (BR-17).
 * @property gusulMin Kabul edilen en kısa gusül süresi (BR-12, FR-AYR-01).
 * @property gusulMax Kabul edilen en uzun gusül süresi (BR-12, FR-AYR-01).
 * @property gusulDefault Gusül süresinin varsayılanı (BR-12).
 * @property tekbirDuration İftitah tekbiri süresi (BR-12).
 * @property seferRekatForFourRekatFarz Seferde kılınan dört rekâtlı farzın rekât sayısı
 * (BR-18).
 * @property bulughMinAgeMale Bulûğun erkekte alt sınırı, kamerî yıl (BR-19).
 * @property bulughMinAgeFemale Bulûğun kızda alt sınırı, kamerî yıl (BR-19).
 * @property bulughSuggestedAge Bulûğ yaşı bilinmiyorsa iki cinsiyette önerilen kamerî yaş
 * (BR-19).
 * @property bayginlikFallsAtMissedFarz Baygınlıkta namazların düştüğü vakti çıkmış farz
 * sayısı (BR-22).
 */
data class RuleSet(
    val version: String,
    val rekatCounts: Map<Prayer, Int>,
    val kerahatAfterGunes: Duration,
    val kerahatBeforeOgle: Duration,
    val kerahatBeforeAksam: Duration,
    val hayizMin: Duration,
    val hayizMax: Duration,
    val nifasMax: Duration,
    val minTuhr: Duration,
    val defaultAdetHabitDays: Int,
    val tertipFallsAtFarzDebt: Int,
    val rekatDuration: Duration,
    val gusulMin: Duration,
    val gusulMax: Duration,
    val gusulDefault: Duration,
    val tekbirDuration: Duration,
    val seferRekatForFourRekatFarz: Int,
    val bulughMinAgeMale: Int,
    val bulughMinAgeFemale: Int,
    val bulughSuggestedAge: Int,
    val bayginlikFallsAtMissedFarz: Int
) {
    init {
        require(version.isNotBlank()) { "Kural seti sürüm kimliği boş olamaz" }
        require(rekatCounts.keys == Prayer.entries.toSet()) {
            "Rekât sayıları tam olarak Prayer.entries anahtarlarını içermeli"
        }
        require(rekatCounts.values.all { it > 0 }) { "Rekât sayıları sıfırdan büyük olmalı" }
        require(
            listOf(
                kerahatAfterGunes,
                kerahatBeforeOgle,
                kerahatBeforeAksam,
                hayizMin,
                hayizMax,
                nifasMax,
                minTuhr,
                rekatDuration,
                gusulMin,
                gusulMax,
                gusulDefault,
                tekbirDuration
            ).all { it > Duration.ZERO }
        ) { "Süreler sıfırdan büyük olmalı" }
        require(
            listOf(
                defaultAdetHabitDays,
                tertipFallsAtFarzDebt,
                seferRekatForFourRekatFarz,
                bulughMinAgeMale,
                bulughMinAgeFemale,
                bulughSuggestedAge,
                bayginlikFallsAtMissedFarz
            ).all { it > 0 }
        ) { "Sayılar sıfırdan büyük olmalı" }
        require(hayizMin < hayizMax) { "Hayızın alt sınırı üst sınırından küçük olmalı" }
        require(gusulMin <= gusulDefault) { "Gusül varsayılanı alt sınırdan küçük olamaz" }
        require(gusulDefault <= gusulMax) { "Gusül varsayılanı üst sınırdan büyük olamaz" }
    }

    companion object {
        /** Hanefî mezhebinin ürün kural seti (M-21). */
        val HANEFI: RuleSet =
            RuleSet(
                version = "hanefi-1",
                rekatCounts =
                    mapOf(
                        Prayer.SABAH to 2,
                        Prayer.OGLE to 4,
                        Prayer.IKINDI to 4,
                        Prayer.AKSAM to 3,
                        Prayer.YATSI to 4,
                        Prayer.VITIR to 3
                    ),
                kerahatAfterGunes = Duration.ofMinutes(45),
                kerahatBeforeOgle = Duration.ofMinutes(45),
                kerahatBeforeAksam = Duration.ofMinutes(45),
                hayizMin = Duration.ofHours(72),
                hayizMax = Duration.ofHours(240),
                nifasMax = Duration.ofHours(960),
                minTuhr = Duration.ofDays(15),
                defaultAdetHabitDays = 10,
                tertipFallsAtFarzDebt = 6,
                rekatDuration = Duration.ofMinutes(2),
                gusulMin = Duration.ofMinutes(5),
                gusulMax = Duration.ofMinutes(30),
                gusulDefault = Duration.ofMinutes(15),
                tekbirDuration = Duration.ofMinutes(1),
                seferRekatForFourRekatFarz = 2,
                bulughMinAgeMale = 12,
                bulughMinAgeFemale = 9,
                bulughSuggestedAge = 14,
                bayginlikFallsAtMissedFarz = 6
            )
    }
}

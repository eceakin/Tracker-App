package com.eceakin.namaztakibi.domain.times

import java.time.Instant
import java.time.LocalDate

/**
 * İlçe kimliği (SRS 6.1).
 *
 * Konum izni istenmediği için ilçe, gömülü listeden seçilen kimlikle temsil edilir
 * (NFR-SEC-01).
 */
@JvmInline
value class DistrictId(val value: Int) {
    init {
        require(value > 0) { "İlçe kimliği sıfırdan büyük olmalı" }
    }
}

/** Vaktin kaynağı (SRS 6.1): Diyanet verisi ya da yerel tahmin. */
enum class TimesOrigin {
    DIYANET,
    ESTIMATED
}

/**
 * Bir ilçenin bir günlük altı vakti (SRS 6.1). Kural motorunun zaman girdisidir.
 *
 * Bu sınıf hesap yapmaz: "şu anki vakit", "vaktin bitişi", "ertesi imsak" ve "namaz günü"
 * F2-02'nin işidir. [date] ile vakitlerin aynı yerel güne düştüğü burada denetlenmez;
 * bu denetim saat dilimi gerektirdiği için F2-02'de ele alınır.
 *
 * @property district Vakitlerin ait olduğu ilçe (SRS 6.1).
 * @property date Vakitlerin ait olduğu yerel gün (SRS 6.1).
 * @property imsak İmsak vakti (SRS 6.1).
 * @property gunes Güneş vakti (SRS 6.1).
 * @property ogle Öğle vakti (SRS 6.1).
 * @property ikindi İkindi vakti (SRS 6.1).
 * @property aksam Akşam vakti (SRS 6.1).
 * @property yatsi Yatsı vakti (SRS 6.1).
 * @property origin Vakitlerin kaynağı (SRS 6.1).
 */
data class PrayerTimesDay(
    val district: DistrictId,
    val date: LocalDate,
    val imsak: Instant,
    val gunes: Instant,
    val ogle: Instant,
    val ikindi: Instant,
    val aksam: Instant,
    val yatsi: Instant,
    val origin: TimesOrigin
) {
    init {
        require(imsak < gunes) { "Vakitler artan sırada olmalı: imsak < güneş" }
        require(gunes < ogle) { "Vakitler artan sırada olmalı: güneş < öğle" }
        require(ogle < ikindi) { "Vakitler artan sırada olmalı: öğle < ikindi" }
        require(ikindi < aksam) { "Vakitler artan sırada olmalı: ikindi < akşam" }
        require(aksam < yatsi) { "Vakitler artan sırada olmalı: akşam < yatsı" }

        val times = listOf(imsak, gunes, ogle, ikindi, aksam, yatsi)
        require(times.all { it.nano == 0 && it.epochSecond % 60L == 0L }) {
            "Vakitler tam dakikada olmalı; saniye ve nanosaniye sıfır olmalı"
        }
    }
}

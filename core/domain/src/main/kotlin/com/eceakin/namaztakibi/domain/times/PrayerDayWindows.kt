package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Instant

/**
 * Bir namaz gününün vakit aralıkları (BR-02, BR-03, BR-04).
 *
 * [day] namaz gününün kendi vakitleridir; [next] ertesi günün vakitleridir ve yalnızca
 * `imsak` alanı kullanılır. Namaz günü [day].imsak ile [next].imsak arasındadır.
 *
 * @property day Namaz gününün kendi vakitleri.
 * @property next Ertesi günün vakitleri; yalnızca `imsak` kullanılır.
 */
data class PrayerDayWindows(val day: PrayerTimesDay, val next: PrayerTimesDay) {
    init {
        require(next.district == day.district) {
            "Ertesi günün vakitleri aynı ilçeye ait olmalı"
        }
        require(next.date == day.date.plusDays(1)) {
            "Ertesi günün tarihi, günün tarihinden bir gün sonra olmalı"
        }
        require(day.yatsi < next.imsak) {
            "Yatsı vakti ertesi günün imsakından önce olmalı"
        }
    }

    /** Kalemin `[başlangıç, bitiş)` penceresi (BR-02, BR-03, BR-04). */
    fun windowOf(prayer: Prayer): TimeWindow = when (prayer) {
        Prayer.SABAH -> TimeWindow(day.imsak, day.gunes)
        Prayer.OGLE -> TimeWindow(day.ogle, day.ikindi)
        Prayer.IKINDI -> TimeWindow(day.ikindi, day.aksam)
        Prayer.AKSAM -> TimeWindow(day.aksam, day.yatsi)
        Prayer.YATSI, Prayer.VITIR -> TimeWindow(day.yatsi, next.imsak)
    }

    /** Vaktin "kaçırıldı" anı; pencerenin bitişidir (BR-04). */
    fun missedAt(prayer: Prayer): Instant = windowOf(prayer).end
}

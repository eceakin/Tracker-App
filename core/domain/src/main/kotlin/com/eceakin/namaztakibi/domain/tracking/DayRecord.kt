package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import java.time.Clock
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Bir namaz gününün kullanıcı işaretleri (SRS 6.1, DATA-05).
 *
 * Durum ve kaçırıldı önerisi bu kayıtta saklanmaz; işaretten ve saatten türetilir.
 *
 * @property date Namaz gününün tarihi.
 * @property marks Günün işaretleri.
 */
data class DayRecord(val date: LocalDate, val marks: List<PrayerMark>) {
    init {
        val prayers = marks.map { it.prayer }
        require(prayers.size == prayers.toSet().size) {
            "Her kalem için en fazla bir işaret olmalı"
        }
        if (Prayer.VITIR in prayers) {
            require(Prayer.YATSI in prayers) {
                "Vitir işaretliyse yatsı da işaretli olmalı (BR-03)"
            }
        }
        marks.forEach { mark ->
            if (mark.asCuma) {
                require(mark.prayer == Prayer.OGLE) {
                    "Cuma işareti yalnızca öğle kalemine konur (BR-08)"
                }
                require(date.dayOfWeek == DayOfWeek.FRIDAY) {
                    "Cuma işareti yalnızca cuma gününe konur (BR-08)"
                }
            }
            require(!(mark.asCuma && mark.asKaza)) {
                "Cuma ve kaza işareti birlikte olamaz (BR-08)"
            }
        }
    }

    /** Kalemin işareti; yoksa `null`. */
    fun markOf(prayer: Prayer): PrayerMark? = marks.firstOrNull { it.prayer == prayer }

    /**
     * Kalemin türetilmiş durumu (DATA-05, BR-04).
     *
     * İşaretli kalem her durumda kılındı sayılır. İşaretsiz kalem, "kaçırıldı" anı
     * geçtiyse kılınmadı, aksi hâlde belirsizdir. Mazeretli durumunu mazeret kartları
     * (F2-07) üretecek.
     */
    fun statusOf(prayer: Prayer, windows: PrayerDayWindows, clock: Clock): PrayerStatus {
        require(windows.day.date == date) {
            "Vakitler bu gün kaydına ait olmalı"
        }
        if (markOf(prayer) != null) {
            return PrayerStatus.KILINDI
        }
        return if (clock.instant() >= windows.missedAt(prayer)) {
            PrayerStatus.KILINMADI
        } else {
            PrayerStatus.BELIRSIZ
        }
    }

    companion object {
        /** İşaretsiz gün kaydı. */
        fun empty(date: LocalDate): DayRecord = DayRecord(date = date, marks = emptyList())
    }
}

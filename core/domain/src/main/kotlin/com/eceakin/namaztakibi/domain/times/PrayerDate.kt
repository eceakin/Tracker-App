package com.eceakin.namaztakibi.domain.times

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

/**
 * Verilen anın ait olduğu namaz gününün tarihi. Namaz günü imsakta başlar; gece
 * yarısından imsaka kadar önceki namaz günü geçerlidir (SRS 1.3, FR-TRK-05).
 *
 * [clock] yalnızca "şu an" için kullanılır; saat nesnesinin bölgesi kullanılmaz. Takvim
 * günü ilçenin saat dilimi olan [zone] ile bulunur (M-23, DATA-03).
 *
 * @param clock Şu anı veren saat.
 * @param zone İlçenin saat dilimi.
 * @param localDayTimes Anın [zone]'daki takvim gününe ait vakitler.
 */
fun prayerDateOf(clock: Clock, zone: ZoneId, localDayTimes: PrayerTimesDay): LocalDate {
    val instant = clock.instant()
    val localDate = instant.atZone(zone).toLocalDate()
    require(localDayTimes.date == localDate) {
        "Vakitler anın yerel gününe ait olmalı"
    }
    return if (instant < localDayTimes.imsak) localDate.minusDays(1) else localDate
}

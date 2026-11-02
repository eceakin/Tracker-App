package com.eceakin.namaztakibi.domain.testing

import com.eceakin.namaztakibi.domain.times.DistrictId
import com.eceakin.namaztakibi.domain.times.PrayerTimesDay
import com.eceakin.namaztakibi.domain.times.TimesOrigin
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Karar tablosundaki sabit referans gün fikstürü (K-17).
 *
 * G1 = 2 Mart 2026 Pazartesi; G5 cuma. Her gün aynı altı vakit kullanılır: imsak 05:30,
 * güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30. Saat dilimi her zaman
 * [ZONE]'dur; sistemin saat dilimi kullanılmaz. Bu nesne fıkhi hesap içermez; yalnızca
 * tablodaki sabitleri [Instant]'a çevirir.
 */
object ReferenceDay {
    val ZONE: ZoneId = ZoneId.of("Europe/Istanbul")
    val G1: LocalDate = LocalDate.of(2026, 3, 2)
    val DISTRICT: DistrictId = DistrictId(1)

    private val timePattern = Regex("""([01]\d|2[0-3]):([0-5]\d)""")

    /** G1'den itibaren [day]. günün tarihi; [date] (1) == [G1]. */
    fun date(day: Int): LocalDate {
        require(day >= 1) { "Gün numarası 1 veya daha büyük olmalı" }
        return G1.plusDays((day - 1).toLong())
    }

    /** [day]. günün [time] anı; ör. `at(6, "17:00")` = G6 17:00, [ZONE]'da. */
    fun at(day: Int, time: String): Instant =
        date(day).atTime(parseTime(time)).atZone(ZONE).toInstant()

    /** [at] anında sabitlenmiş, bölgesi [ZONE] olan saat. */
    fun clockAt(day: Int, time: String): Clock = Clock.fixed(at(day, time), ZONE)

    /** [day]. günün karar tablosundaki altı vakti; kaynak [TimesOrigin.DIYANET]. */
    fun times(day: Int): PrayerTimesDay = PrayerTimesDay(
        district = DISTRICT,
        date = date(day),
        imsak = at(day, "05:30"),
        gunes = at(day, "07:00"),
        ogle = at(day, "12:30"),
        ikindi = at(day, "15:30"),
        aksam = at(day, "18:00"),
        yatsi = at(day, "19:30"),
        origin = TimesOrigin.DIYANET
    )

    private fun parseTime(time: String): LocalTime {
        val match = timePattern.matchEntire(time)
            ?: throw IllegalArgumentException("Saat HH:mm biçiminde olmalı: $time")
        return LocalTime.of(match.groupValues[1].toInt(), match.groupValues[2].toInt())
    }
}

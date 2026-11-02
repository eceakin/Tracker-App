package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.rules.RuleSet
import java.time.Clock
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

    /**
     * Verilen anın farz vakit durumu (BR-02, BR-04).
     *
     * An `[day.imsak, next.imsak)` içinde değilse başka bir namaz gününe aittir ve bu bir
     * programlama hatasıdır. Vitir, `Within` sonucu olarak hiçbir zaman dönmez; vitir
     * kaydının yatsıya bağlılığı F2-04'ün işidir.
     */
    fun currentVakit(clock: Clock): CurrentVakit {
        val instant = clock.instant()
        require(instant >= day.imsak && instant < next.imsak) {
            "An bu namaz gününün dışında"
        }

        return when {
            instant < day.gunes ->
                CurrentVakit.Within(Prayer.SABAH, windowOf(Prayer.SABAH))

            instant < day.ogle ->
                CurrentVakit.Outside(Prayer.OGLE, day.ogle)

            instant < day.ikindi ->
                CurrentVakit.Within(Prayer.OGLE, windowOf(Prayer.OGLE))

            instant < day.aksam ->
                CurrentVakit.Within(Prayer.IKINDI, windowOf(Prayer.IKINDI))

            instant < day.yatsi ->
                CurrentVakit.Within(Prayer.AKSAM, windowOf(Prayer.AKSAM))

            else ->
                CurrentVakit.Within(Prayer.YATSI, windowOf(Prayer.YATSI))
        }
    }

    /**
     * Üç kerahat penceresi (BR-07); sıra `GUNES`, `ISTIVA`, `AKSAM`'dır.
     *
     * Süreler yalnızca [ruleSet]'ten okunur; kullanıcı ayarı yoktur.
     */
    fun kerahatWindows(ruleSet: RuleSet): List<KerahatWindow> = listOf(
        KerahatWindow(
            KerahatKind.GUNES,
            TimeWindow(day.gunes, day.gunes.plus(ruleSet.kerahatAfterGunes))
        ),
        KerahatWindow(
            KerahatKind.ISTIVA,
            TimeWindow(day.ogle.minus(ruleSet.kerahatBeforeOgle), day.ogle)
        ),
        KerahatWindow(
            KerahatKind.AKSAM,
            TimeWindow(day.aksam.minus(ruleSet.kerahatBeforeAksam), day.aksam)
        )
    )

    /**
     * Verilen anın kerahat durumu (BR-07).
     *
     * An `[day.imsak, next.imsak)` içinde değilse başka bir namaz gününe aittir ve bu bir
     * programlama hatasıdır. Pencerelerin birbiriyle çakışması burada denetlenmez.
     */
    fun kerahatStatus(clock: Clock, ruleSet: RuleSet): KerahatStatus {
        val instant = clock.instant()
        require(instant >= day.imsak && instant < next.imsak) {
            "An bu namaz gününün dışında"
        }

        val inside = kerahatWindows(ruleSet).firstOrNull { instant in it.window }
        return if (inside == null) KerahatStatus.None else KerahatStatus.Inside(inside)
    }
}

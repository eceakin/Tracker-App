package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Instant

/**
 * Bir kalemin kullanıcı işareti (SRS 6.1, DATA-05).
 *
 * @property prayer İşaretlenen kalem.
 * @property markedAt İşaretin konduğu an.
 * @property asCuma İşaret cuma olarak mı kondu (yalnızca cuma gününün öğlesi).
 * @property asKaza İşaret "kaza olarak kılındı" mı (F2-04b yazacak).
 */
data class PrayerMark(
    val prayer: Prayer,
    val markedAt: Instant,
    val asCuma: Boolean,
    val asKaza: Boolean
)

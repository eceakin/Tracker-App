package com.eceakin.namaztakibi.domain.times

/** Kerahat penceresinin türü (BR-07). */
enum class KerahatKind {
    GUNES,
    ISTIVA,
    AKSAM
}

/**
 * Bir kerahat penceresi (BR-07).
 *
 * @property kind Pencerenin türü.
 * @property window Pencerenin `[başlangıç, bitiş)` aralığı.
 */
data class KerahatWindow(val kind: KerahatKind, val window: TimeWindow)

/**
 * Verilen anın kerahat durumu (BR-07).
 *
 * `Inside` bilgi sonucudur; kaza engeli ve ikindi istisnası F2-05'in işidir.
 */
sealed interface KerahatStatus {
    /** An hiçbir kerahat penceresinde değil. */
    data object None : KerahatStatus

    /**
     * An bir kerahat penceresinde.
     *
     * @property kerahat İçinde bulunulan kerahat penceresi.
     */
    data class Inside(val kerahat: KerahatWindow) : KerahatStatus
}

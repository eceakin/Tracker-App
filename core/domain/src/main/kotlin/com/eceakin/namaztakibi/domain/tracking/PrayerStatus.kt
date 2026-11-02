package com.eceakin.namaztakibi.domain.tracking

/** Günlük kalemin durumu (SRS 6.1, DATA-05). Durum saklanmaz, türetilir. */
enum class PrayerStatus {
    KILINDI,
    KILINMADI,
    MAZERETLI,
    BELIRSIZ
}

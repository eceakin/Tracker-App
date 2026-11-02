package com.eceakin.namaztakibi.domain.times

import java.time.Duration
import java.time.Instant

/**
 * Başlangıç dahil, bitiş hariç zaman aralığı: `[start, end)` (BR-02, BR-04).
 *
 * @property start Aralığın başlangıcı; aralığa dahildir.
 * @property end Aralığın bitişi; aralığa dahil değildir.
 */
data class TimeWindow(val start: Instant, val end: Instant) {
    init {
        require(start < end) { "Pencere başlangıcı bitişinden önce olmalı" }
    }

    operator fun contains(instant: Instant): Boolean = instant >= start && instant < end

    val duration: Duration
        get() = Duration.between(start, end)
}

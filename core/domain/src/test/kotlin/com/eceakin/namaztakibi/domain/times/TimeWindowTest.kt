package com.eceakin.namaztakibi.domain.times

import java.time.Duration
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TimeWindowTest {
    private val start = Instant.parse("2026-03-02T09:30:00Z")
    private val end = Instant.parse("2026-03-02T12:30:00Z")

    @Test
    fun M24_baslangicBitistenOnceOlmali() {
        assertThrows<IllegalArgumentException> { TimeWindow(start, start) }
        assertThrows<IllegalArgumentException> { TimeWindow(end, start) }
    }

    @Test
    fun M23_aralikBaslangicDahilBitisHaric() {
        val window = TimeWindow(start, end)

        assertTrue(start in window)
        assertFalse(end in window)
        assertTrue(end.minus(Duration.ofMinutes(1)) in window)
        assertFalse(start.minus(Duration.ofMinutes(1)) in window)
    }

    @Test
    fun M23_sureDogruHesaplanir() {
        assertEquals(Duration.ofHours(3), TimeWindow(start, end).duration)
    }
}

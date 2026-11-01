package com.eceakin.namaztakibi.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class PrayerTest {
    @Test
    fun BR01_takipKalemleriAltiTanedirVeSiralidir() {
        assertEquals(
            listOf(
                Prayer.SABAH,
                Prayer.OGLE,
                Prayer.IKINDI,
                Prayer.AKSAM,
                Prayer.YATSI,
                Prayer.VITIR
            ),
            Prayer.entries
        )
    }

    @Test
    fun BR01_vitirDisindakiKalemlerFarzdir() {
        assertEquals(
            listOf(
                Prayer.SABAH,
                Prayer.OGLE,
                Prayer.IKINDI,
                Prayer.AKSAM,
                Prayer.YATSI
            ),
            Prayer.entries.filter { it.isFarz }
        )
        assertFalse(Prayer.VITIR.isFarz)
    }
}

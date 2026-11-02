package com.eceakin.namaztakibi.domain.settings

/**
 * Bir günün değerlendirmede kullanılan ayarları (SRS 6.1 Settings).
 *
 * @property autoAddDebt Otomatik borç ekleme açık mı (varsayılan kapalı).
 */
data class DaySettings(val autoAddDebt: Boolean)

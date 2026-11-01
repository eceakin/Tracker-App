package com.eceakin.namaztakibi.domain

/**
 * Günlük takip kalemleri (BR-01): sabah, öğle, ikindi, akşam, yatsı ve vitir.
 *
 * Vitir farz değil vâciptir; bu yüzden [isFarz] değeri yalnızca vitir için `false` döner.
 */
enum class Prayer(val isFarz: Boolean) {
    SABAH(true),
    OGLE(true),
    IKINDI(true),
    AKSAM(true),
    YATSI(true),
    VITIR(false)
}

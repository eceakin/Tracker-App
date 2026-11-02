package com.eceakin.namaztakibi.domain.times

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Instant

/**
 * Verilen anın namaz günü içindeki farz vakit durumu (BR-02, BR-04).
 *
 * Bu tip yalnızca zaman sorusunu cevaplar; vitir kaydının yatsıya bağlılığı F2-04'ün
 * kayıt kuralıdır.
 */
sealed interface CurrentVakit {
    /**
     * An bir farz vaktin içinde. [prayer] hiçbir zaman [Prayer.VITIR] olmaz.
     *
     * @property prayer Anın içinde bulunduğu farz vakit.
     * @property window Vaktin `[başlangıç, bitiş)` penceresi.
     */
    data class Within(val prayer: Prayer, val window: TimeWindow) : CurrentVakit

    /**
     * An güneş ile öğle arasında; hiçbir farz vakit içinde değil.
     *
     * @property nextPrayer Bir sonraki farz vakit; bu durumda her zaman [Prayer.OGLE].
     * @property nextStart Bir sonraki farz vaktin başlangıcı.
     */
    data class Outside(val nextPrayer: Prayer, val nextStart: Instant) : CurrentVakit
}

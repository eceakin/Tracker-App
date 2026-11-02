package com.eceakin.namaztakibi.domain.kaza

/**
 * Tek kullanıcı eylemiyle birlikte yazılan kayıtların ortak işlem kimliği (DATA-04).
 *
 * Kimliği motor değil, yazan katman (Faz 4) üretir; domain'de UUID ya da benzeri bir
 * üretim yoktur.
 */
@JvmInline
value class OperationId(val value: String) {
    init {
        require(value.isNotBlank()) { "İşlem kimliği boş olamaz" }
    }
}

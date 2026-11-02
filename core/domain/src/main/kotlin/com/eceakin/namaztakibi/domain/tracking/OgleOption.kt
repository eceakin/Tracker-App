package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.settings.Gender
import java.time.DayOfWeek
import java.time.LocalDate

/** Öğle kaleminin sunum seçeneği (FR-TRK-06, BR-08). */
enum class OgleOption {
    OGLE_ONLY,
    OGLE_OR_CUMA,
    OGLE_WITH_OPTIONAL_CUMA
}

/**
 * Öğle kaleminin gün ve cinsiyete göre sunumu (FR-TRK-06, BR-08).
 *
 * Cuma işareti erkekte ve kadında aynı kayıttır (DATA-05); bu fonksiyon yalnızca
 * arayüzün hangi seçeneği göstereceğini belirler.
 */
fun ogleOptionOf(date: LocalDate, gender: Gender): OgleOption {
    if (date.dayOfWeek != DayOfWeek.FRIDAY) {
        return OgleOption.OGLE_ONLY
    }
    return when (gender) {
        Gender.MALE -> OgleOption.OGLE_OR_CUMA
        Gender.FEMALE -> OgleOption.OGLE_WITH_OPTIONAL_CUMA
    }
}

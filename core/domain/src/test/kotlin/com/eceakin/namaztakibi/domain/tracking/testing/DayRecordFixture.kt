package com.eceakin.namaztakibi.domain.tracking.testing

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import com.eceakin.namaztakibi.domain.tracking.DayRecord
import com.eceakin.namaztakibi.domain.tracking.PrayerMark
import com.eceakin.namaztakibi.domain.tracking.RecordWrite
import java.time.LocalDate

/**
 * Günlük kaydın bellek içi test karşılığı (K-14).
 *
 * Yazmaları sırayla uygular ve her adımda yeni bir [DayRecord] kurar; böylece kaydın
 * değişmezleri her adımda denetlenir.
 */
object DayRecordFixture {
    fun apply(record: DayRecord, writes: List<RecordWrite>): DayRecord {
        var current = record
        writes.forEach { write ->
            current = when (write) {
                is RecordWrite.Mark -> {
                    require(write.date == current.date) {
                        "Mark tarihi kayıt tarihiyle aynı olmalı"
                    }
                    current.copy(
                        marks = current.marks.filterNot { it.prayer == write.prayer } +
                            PrayerMark(
                                prayer = write.prayer,
                                markedAt = write.markedAt,
                                asCuma = write.asCuma,
                                asKaza = write.asKaza
                            )
                    )
                }

                is RecordWrite.Clear -> current.copy(
                    marks = current.marks.filterNot { it.prayer == write.prayer }
                )
            }
        }
        return current
    }

    fun markedAtStart(
        date: LocalDate,
        windows: PrayerDayWindows,
        vararg prayers: Prayer
    ): DayRecord = DayRecord(
        date = date,
        marks = prayers.map { prayer ->
            PrayerMark(
                prayer = prayer,
                markedAt = windows.windowOf(prayer).start,
                asCuma = false,
                asKaza = false
            )
        }
    )
}

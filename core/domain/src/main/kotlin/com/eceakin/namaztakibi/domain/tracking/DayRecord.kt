package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.KazaEntryKind
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.KazaWrite
import com.eceakin.namaztakibi.domain.settings.DaySettings
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate

/**
 * Bir namaz gününün kullanıcı işaretleri (SRS 6.1, DATA-05).
 *
 * Durum ve kaçırıldı önerisi bu kayıtta saklanmaz; işaretten ve saatten türetilir.
 *
 * @property date Namaz gününün tarihi.
 * @property marks Günün işaretleri.
 */
data class DayRecord(val date: LocalDate, val marks: List<PrayerMark>) {
    init {
        val prayers = marks.map { it.prayer }
        require(prayers.size == prayers.toSet().size) {
            "Her kalem için en fazla bir işaret olmalı"
        }
        if (Prayer.VITIR in prayers) {
            require(Prayer.YATSI in prayers) {
                "Vitir işaretliyse yatsı da işaretli olmalı (BR-03)"
            }
        }
        marks.forEach { mark ->
            require(mark.asKaza == (mark.operationId != null)) {
                "Kaza işareti işlem kimliğiyle birlikte olmalı (DATA-05)"
            }
            if (mark.asCuma) {
                require(mark.prayer == Prayer.OGLE) {
                    "Cuma işareti yalnızca öğle kalemine konur (BR-08)"
                }
                require(date.dayOfWeek == DayOfWeek.FRIDAY) {
                    "Cuma işareti yalnızca cuma gününe konur (BR-08)"
                }
            }
            require(!(mark.asCuma && mark.asKaza)) {
                "Cuma ve kaza işareti birlikte olamaz (BR-08)"
            }
        }
    }

    /** Kalemin işareti; yoksa `null`. */
    fun markOf(prayer: Prayer): PrayerMark? = marks.firstOrNull { it.prayer == prayer }

    /**
     * Kalemin türetilmiş durumu (DATA-05, BR-04).
     *
     * İşaretli kalem her durumda kılındı sayılır. İşaretsiz kalem, "kaçırıldı" anı
     * geçtiyse kılınmadı, aksi hâlde belirsizdir. Mazeretli durumunu mazeret kartları
     * (F2-07) üretecek.
     */
    fun statusOf(prayer: Prayer, windows: PrayerDayWindows, clock: Clock): PrayerStatus {
        require(windows.day.date == date) {
            "Vakitler bu gün kaydına ait olmalı"
        }
        if (markOf(prayer) != null) {
            return PrayerStatus.KILINDI
        }
        return if (clock.instant() >= windows.missedAt(prayer)) {
            PrayerStatus.KILINMADI
        } else {
            PrayerStatus.BELIRSIZ
        }
    }

    /**
     * İşaretleme kararı (FR-TRK-01 – 03, 06, 07; KT-01 – 06, 15, 17, 18; DATA-05).
     *
     * Sıra: zaten işaretli; vakit girmedi; vakit çıktı (seçim gerekir); vitirden önce
     * yatsı; cuma işareti geçersiz; aksi hâlde izin. Vakti çıkmış kuralı, "önce yatsı"
     * kuralından önce gelir. Cinsiyet işaretlemeyi etkilemez.
     */
    fun decideMark(
        prayer: Prayer,
        asCuma: Boolean,
        windows: PrayerDayWindows,
        clock: Clock
    ): MarkDecision {
        require(windows.day.date == date) {
            "Vakitler bu gün kaydına ait olmalı"
        }
        if (markOf(prayer) != null) {
            return MarkDecision.Blocked(MarkBlock.ALREADY_MARKED)
        }

        val now = clock.instant()
        val window = windows.windowOf(prayer)
        if (now < window.start) {
            return MarkDecision.Blocked(MarkBlock.NOT_YET_IN_TIME)
        }
        if (now >= windows.missedAt(prayer)) {
            return MarkDecision.NeedsLateChoice
        }
        if (prayer == Prayer.VITIR && markOf(Prayer.YATSI) == null) {
            return MarkDecision.Blocked(MarkBlock.YATSI_FIRST)
        }
        if (asCuma && (prayer != Prayer.OGLE || date.dayOfWeek != DayOfWeek.FRIDAY)) {
            return MarkDecision.Blocked(MarkBlock.CUMA_NOT_ALLOWED)
        }
        return MarkDecision.Allowed(
            RecordWrite.Mark(
                date = date,
                prayer = prayer,
                markedAt = now,
                asCuma = asCuma,
                asKaza = false
            )
        )
    }

    /**
     * İşaret kaldırma kararı (FR-TRK-03, DATA-05).
     *
     * Sıra: işaretsiz; yatsı geri alınırken vitir işaretli; işaret kaza olarak kılınmış;
     * aksi hâlde izin. Geri almanın süre sınırı yoktur; saat almaz.
     */
    fun decideUnmark(prayer: Prayer): UnmarkDecision {
        val mark = markOf(prayer)
            ?: return UnmarkDecision.Blocked(UnmarkBlock.NOT_MARKED)
        if (prayer == Prayer.YATSI && markOf(Prayer.VITIR) != null) {
            return UnmarkDecision.Blocked(UnmarkBlock.UNMARK_VITIR_FIRST)
        }
        if (mark.asKaza) {
            return UnmarkDecision.Blocked(UnmarkBlock.KAZA_MARK_NEEDS_GROUP_UNDO)
        }
        return UnmarkDecision.Allowed(RecordWrite.Clear(date = date, prayer = prayer))
    }

    /**
     * Vakti çıkmış, işaretlenmemiş kalemler için öneri/otomatik ekleme listesi
     * (FR-KAZ-03, BR-04, FR-ONB-05, DATA-05).
     *
     * Her kalem için sıra: işaretliyse yok; vakit çıkmadıysa yok; "kaçırıldı" anı takip
     * başlangıcından önce **ya da ona eşitse** yok; defterde açık öneri/otomatik kayıt
     * varsa yok; otomatik ekleme açıksa otomatik kayıt; aksi hâlde öneri. Liste
     * [Prayer.entries] sırasındadır.
     */
    fun evaluateMissed(
        windows: PrayerDayWindows,
        ledger: KazaLedger,
        settings: DaySettings,
        trackingStart: Instant,
        clock: Clock
    ): List<MissedOutcome> {
        require(windows.day.date == date) {
            "Vakitler bu gün kaydına ait olmalı"
        }

        val now = clock.instant()
        return Prayer.entries.mapNotNull { prayer ->
            if (markOf(prayer) != null) {
                return@mapNotNull null
            }
            val missedAt = windows.missedAt(prayer)
            if (now < missedAt) {
                return@mapNotNull null
            }
            if (missedAt <= trackingStart) {
                return@mapNotNull null
            }
            if (ledger.hasOpenEntryFor(date, prayer)) {
                return@mapNotNull null
            }
            if (settings.autoAddDebt) {
                MissedOutcome.AutoAdd(
                    KazaWrite.AddEntry(
                        prayer = prayer,
                        sourceDate = date,
                        kind = KazaEntryKind.OTOMATIK,
                        count = 1,
                        corrects = null,
                        missedInSefer = false,
                        createdAt = now
                    )
                )
            } else {
                MissedOutcome.Suggest(date = date, prayer = prayer)
            }
        }
    }

    /**
     * Geç işaretleme kararı (FR-TRK-04, DATA-05, KT-12, KT-13).
     *
     * Sıra: zaten işaretli; vakit çıkmadı; vitirden önce yatsı; "vaktinde kıldım"
     * (açık kayıt yok / borç negatife düşer / onay sorulur); "sonradan kaza olarak
     * kıldım" (açık kayıt ve borç varsa yalnızca kılınan; aksi hâlde öneri + kılınan).
     * Geç işaret hiçbir zaman cuma olarak konmaz; motor kimlik üretmez.
     */
    fun decideLateMark(
        prayer: Prayer,
        choice: LateChoice,
        windows: PrayerDayWindows,
        ledger: KazaLedger,
        clock: Clock
    ): LateMarkDecision {
        require(windows.day.date == date) {
            "Vakitler bu gün kaydına ait olmalı"
        }
        if (markOf(prayer) != null) {
            return LateMarkDecision.Blocked(LateMarkBlock.ALREADY_MARKED)
        }

        val now = clock.instant()
        if (now < windows.missedAt(prayer)) {
            return LateMarkDecision.Blocked(LateMarkBlock.NOT_LATE)
        }
        if (prayer == Prayer.VITIR && markOf(Prayer.YATSI) == null) {
            return LateMarkDecision.Blocked(LateMarkBlock.YATSI_FIRST)
        }

        val open = ledger.openEntriesFor(date, prayer)
        val asKaza = choice == LateChoice.AS_KAZA
        val mark = RecordWrite.Mark(
            date = date,
            prayer = prayer,
            markedAt = now,
            asCuma = false,
            asKaza = asKaza
        )

        if (choice == LateChoice.ON_TIME && open.isEmpty()) {
            return LateMarkDecision.Allowed(
                LateMarkWrites(mark = mark, kazaWrites = emptyList(), grouped = false)
            )
        }
        if (choice == LateChoice.ON_TIME &&
            ledger.balance(prayer) - open.sumOf { it.count } < 0
        ) {
            return LateMarkDecision.Allowed(
                LateMarkWrites(mark = mark, kazaWrites = emptyList(), grouped = false)
            )
        }
        if (choice == LateChoice.ON_TIME) {
            val corrections = open.map { entry ->
                KazaWrite.AddEntry(
                    prayer = entry.prayer,
                    sourceDate = entry.sourceDate,
                    kind = KazaEntryKind.DUZELTME,
                    count = -entry.count,
                    corrects = entry.id,
                    missedInSefer = entry.missedInSefer,
                    createdAt = now
                )
            }
            return LateMarkDecision.NeedsConfirmation(
                question = LateMarkQuestion.UNDO_ADDED_DEBT,
                ifConfirmed = LateMarkWrites(
                    mark = mark,
                    kazaWrites = corrections,
                    grouped = false
                ),
                ifDeclined = LateMarkWrites(
                    mark = mark,
                    kazaWrites = emptyList(),
                    grouped = false
                )
            )
        }
        if (open.isNotEmpty() && ledger.balance(prayer) >= 1) {
            return LateMarkDecision.Allowed(
                LateMarkWrites(
                    mark = mark,
                    kazaWrites = listOf(
                        KazaWrite.AddPerformed(prayer = prayer, performedAt = now)
                    ),
                    grouped = true
                )
            )
        }
        return LateMarkDecision.Allowed(
            LateMarkWrites(
                mark = mark,
                kazaWrites = listOf(
                    KazaWrite.AddEntry(
                        prayer = prayer,
                        sourceDate = date,
                        kind = KazaEntryKind.ONERI,
                        count = 1,
                        corrects = null,
                        missedInSefer = false,
                        createdAt = now
                    ),
                    KazaWrite.AddPerformed(prayer = prayer, performedAt = now)
                ),
                grouped = true
            )
        )
    }

    companion object {
        /** İşaretsiz gün kaydı. */
        fun empty(date: LocalDate): DayRecord = DayRecord(date = date, marks = emptyList())
    }
}

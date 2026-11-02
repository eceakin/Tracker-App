package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.KazaEntry
import com.eceakin.namaztakibi.domain.kaza.KazaEntryKind
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.KazaPerformed
import com.eceakin.namaztakibi.domain.kaza.KazaRecordId
import com.eceakin.namaztakibi.domain.kaza.KazaWrite
import com.eceakin.namaztakibi.domain.kaza.OperationId
import com.eceakin.namaztakibi.domain.settings.DaySettings
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import com.eceakin.namaztakibi.domain.tracking.testing.LateMarkFixture
import java.time.Instant
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LateMarkTest {
    private val windows1 = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))
    private val g1 = ReferenceDay.date(1)

    private fun List<MissedOutcome>.hasOutcomeFor(prayer: Prayer): Boolean = any { outcome ->
        when (outcome) {
            is MissedOutcome.Suggest -> outcome.prayer == prayer
            is MissedOutcome.AutoAdd -> outcome.write.prayer == prayer
        }
    }

    @Test
    fun KT12_BR04_oneriOnaylanmis_sonradanKazaOlarakKildim_borcSifir() {
        val ledger = KazaLedger(
            entries = listOf(
                entry(
                    id = "e1",
                    kind = KazaEntryKind.ONERI,
                    sourceDate = g1,
                    createdAt = ReferenceDay.at(1, "15:40")
                )
            ),
            performed = emptyList()
        )
        val record = DayRecord.empty(g1)
        val decision = record.decideLateMark(
            Prayer.OGLE,
            LateChoice.AS_KAZA,
            windows1,
            ledger,
            ReferenceDay.clockAt(1, "16:00")
        )

        assertEquals(
            LateMarkDecision.Allowed(
                LateMarkWrites(
                    mark = RecordWrite.Mark(
                        date = g1,
                        prayer = Prayer.OGLE,
                        markedAt = ReferenceDay.at(1, "16:00"),
                        asCuma = false,
                        asKaza = true
                    ),
                    kazaWrites = listOf(
                        KazaWrite.AddPerformed(
                            prayer = Prayer.OGLE,
                            performedAt = ReferenceDay.at(1, "16:00")
                        )
                    ),
                    grouped = true
                )
            ),
            decision
        )

        val allowed = decision as LateMarkDecision.Allowed
        val (newRecord, newLedger) = LateMarkFixture.apply(
            record,
            ledger,
            allowed.writes,
            OperationId("op1")
        )

        assertEquals(
            PrayerStatus.KILINDI,
            newRecord.statusOf(Prayer.OGLE, windows1, ReferenceDay.clockAt(1, "16:00"))
        )
        assertEquals(true, newRecord.markOf(Prayer.OGLE)?.asKaza)
        assertEquals(0, newLedger.balance(Prayer.OGLE))
        assertEquals(OperationId("op1"), newLedger.performed.single().operationId)
        assertFalse(
            newRecord.evaluateMissed(
                windows1,
                newLedger,
                DaySettings(autoAddDebt = false),
                ReferenceDay.at(1, "00:00"),
                ReferenceDay.clockAt(1, "16:00")
            ).hasOutcomeFor(Prayer.OGLE)
        )
    }

    @Test
    fun KT13_BR04_oneriYanitlanmamis_vaktindeKildim_borcDegismez() {
        val record = DayRecord.empty(g1)
        val decision = record.decideLateMark(
            Prayer.OGLE,
            LateChoice.ON_TIME,
            windows1,
            KazaLedger.EMPTY,
            ReferenceDay.clockAt(1, "16:00")
        )

        assertEquals(
            LateMarkDecision.Allowed(
                LateMarkWrites(
                    mark = RecordWrite.Mark(
                        date = g1,
                        prayer = Prayer.OGLE,
                        markedAt = ReferenceDay.at(1, "16:00"),
                        asCuma = false,
                        asKaza = false
                    ),
                    kazaWrites = emptyList(),
                    grouped = false
                )
            ),
            decision
        )

        val allowed = decision as LateMarkDecision.Allowed
        val (newRecord, newLedger) = LateMarkFixture.apply(
            record,
            KazaLedger.EMPTY,
            allowed.writes,
            null
        )

        assertEquals(
            PrayerStatus.KILINDI,
            newRecord.statusOf(Prayer.OGLE, windows1, ReferenceDay.clockAt(1, "16:00"))
        )
        assertFalse(
            newRecord.evaluateMissed(
                windows1,
                newLedger,
                DaySettings(autoAddDebt = false),
                ReferenceDay.at(1, "00:00"),
                ReferenceDay.clockAt(1, "16:00")
            ).hasOutcomeFor(Prayer.OGLE)
        )
        assertEquals(KazaLedger.EMPTY, newLedger)
    }

    @Test
    fun KS_TRK_05_ertesiGunVaktindeKildim() {
        val decision = DayRecord.empty(g1).decideLateMark(
            Prayer.OGLE,
            LateChoice.ON_TIME,
            windows1,
            KazaLedger.EMPTY,
            ReferenceDay.clockAt(2, "10:00")
        )

        assertEquals(
            LateMarkDecision.Allowed(
                LateMarkWrites(
                    mark = RecordWrite.Mark(
                        date = g1,
                        prayer = Prayer.OGLE,
                        markedAt = ReferenceDay.at(2, "10:00"),
                        asCuma = false,
                        asKaza = false
                    ),
                    kazaWrites = emptyList(),
                    grouped = false
                )
            ),
            decision
        )
    }

    @Test
    fun KS_TRK_13_kurulumOncesiGunVaktindeKildim() {
        val windows3 = PrayerDayWindows(ReferenceDay.times(3), ReferenceDay.times(4))
        val date = ReferenceDay.date(3)
        val decision = DayRecord.empty(date).decideLateMark(
            Prayer.OGLE,
            LateChoice.ON_TIME,
            windows3,
            KazaLedger.EMPTY,
            ReferenceDay.clockAt(5, "10:00")
        )

        assertEquals(
            LateMarkDecision.Allowed(
                LateMarkWrites(
                    mark = RecordWrite.Mark(
                        date = date,
                        prayer = Prayer.OGLE,
                        markedAt = ReferenceDay.at(5, "10:00"),
                        asCuma = false,
                        asKaza = false
                    ),
                    kazaWrites = emptyList(),
                    grouped = false
                )
            ),
            decision
        )

        val allowed = decision as LateMarkDecision.Allowed
        val (_, newLedger) = LateMarkFixture.apply(
            DayRecord.empty(date),
            KazaLedger.EMPTY,
            allowed.writes,
            null
        )
        assertEquals(KazaLedger.EMPTY, newLedger)
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("gecIsaretlemeKararlari")
    fun FR_TRK_04_gecIsaretlemeKararlari(action: Executable) {
        action.execute()
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("eklenmisBorcDurumlari")
    fun DATA05_vaktindeKildim_eklenmisBorc(action: Executable) {
        action.execute()
    }

    @Test
    fun DATA05_vaktindeKildim_onayVeRet() {
        val ledger = KazaLedger(
            entries = listOf(
                entry(
                    id = "e1",
                    kind = KazaEntryKind.ONERI,
                    sourceDate = g1,
                    createdAt = ReferenceDay.at(1, "15:40")
                )
            ),
            performed = emptyList()
        )
        val record = DayRecord.empty(g1)
        val decision = record.decideLateMark(
            Prayer.OGLE,
            LateChoice.ON_TIME,
            windows1,
            ledger,
            ReferenceDay.clockAt(1, "16:00")
        )
        assertTrue(decision is LateMarkDecision.NeedsConfirmation)
        val confirmation = decision as LateMarkDecision.NeedsConfirmation

        val (confirmedRecord, confirmedLedger) = LateMarkFixture.apply(
            record,
            ledger,
            confirmation.ifConfirmed,
            null
        )
        assertEquals(0, confirmedLedger.balance(Prayer.OGLE))
        assertTrue(confirmedLedger.isCorrected(KazaRecordId("e1")))
        assertFalse(
            confirmedRecord.evaluateMissed(
                windows1,
                confirmedLedger,
                DaySettings(autoAddDebt = false),
                ReferenceDay.at(1, "00:00"),
                ReferenceDay.clockAt(1, "16:00")
            ).hasOutcomeFor(Prayer.OGLE)
        )

        val (declinedRecord, declinedLedger) = LateMarkFixture.apply(
            record,
            ledger,
            confirmation.ifDeclined,
            null
        )
        assertEquals(1, declinedLedger.balance(Prayer.OGLE))
        assertEquals(
            PrayerStatus.KILINDI,
            declinedRecord.statusOf(Prayer.OGLE, windows1, ReferenceDay.clockAt(1, "16:00"))
        )
    }

    private companion object {
        @JvmStatic
        fun gecIsaretlemeKararlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-TRK-04 isaretli kalem",
                Executable {
                    val record = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(
                            mark(Prayer.OGLE, "12:40", asKaza = false)
                        )
                    )
                    assertEquals(
                        LateMarkDecision.Blocked(LateMarkBlock.ALREADY_MARKED),
                        record.decideLateMark(
                            Prayer.OGLE,
                            LateChoice.ON_TIME,
                            cw1(),
                            KazaLedger.EMPTY,
                            ReferenceDay.clockAt(1, "16:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 vakti cikmamis kalem",
                Executable {
                    assertEquals(
                        LateMarkDecision.Blocked(LateMarkBlock.NOT_LATE),
                        DayRecord.empty(ReferenceDay.date(1)).decideLateMark(
                            Prayer.OGLE,
                            LateChoice.ON_TIME,
                            cw1(),
                            KazaLedger.EMPTY,
                            ReferenceDay.clockAt(1, "15:29")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 yatsisiz vitir",
                Executable {
                    assertEquals(
                        LateMarkDecision.Blocked(LateMarkBlock.YATSI_FIRST),
                        DayRecord.empty(ReferenceDay.date(1)).decideLateMark(
                            Prayer.VITIR,
                            LateChoice.ON_TIME,
                            cw1(),
                            KazaLedger.EMPTY,
                            ReferenceDay.clockAt(2, "06:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 yatsi isaretliyken vitir vaktinde",
                Executable {
                    val record = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(mark(Prayer.YATSI, "19:45", asKaza = false))
                    )
                    assertEquals(
                        LateMarkDecision.Allowed(
                            LateMarkWrites(
                                mark = RecordWrite.Mark(
                                    date = ReferenceDay.date(1),
                                    prayer = Prayer.VITIR,
                                    markedAt = ReferenceDay.at(2, "06:00"),
                                    asCuma = false,
                                    asKaza = false
                                ),
                                kazaWrites = emptyList(),
                                grouped = false
                            )
                        ),
                        record.decideLateMark(
                            Prayer.VITIR,
                            LateChoice.ON_TIME,
                            cw1(),
                            KazaLedger.EMPTY,
                            ReferenceDay.clockAt(2, "06:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 kurulum oncesi gun kaza olarak",
                Executable {
                    assertEquals(
                        LateMarkDecision.Allowed(
                            LateMarkWrites(
                                mark = RecordWrite.Mark(
                                    date = ReferenceDay.date(3),
                                    prayer = Prayer.OGLE,
                                    markedAt = ReferenceDay.at(5, "10:00"),
                                    asCuma = false,
                                    asKaza = true
                                ),
                                kazaWrites = listOf(
                                    KazaWrite.AddEntry(
                                        prayer = Prayer.OGLE,
                                        sourceDate = ReferenceDay.date(3),
                                        kind = KazaEntryKind.ONERI,
                                        count = 1,
                                        corrects = null,
                                        missedInSefer = false,
                                        createdAt = ReferenceDay.at(5, "10:00")
                                    ),
                                    KazaWrite.AddPerformed(
                                        prayer = Prayer.OGLE,
                                        performedAt = ReferenceDay.at(5, "10:00")
                                    )
                                ),
                                grouped = true
                            )
                        ),
                        DayRecord.empty(ReferenceDay.date(3)).decideLateMark(
                            Prayer.OGLE,
                            LateChoice.AS_KAZA,
                            cw3(),
                            KazaLedger.EMPTY,
                            ReferenceDay.clockAt(5, "10:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 acik kayit var ama borc sifir",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(ce("e1", KazaEntryKind.ONERI)),
                        performed = listOf(cp("p1"))
                    )
                    assertEquals(
                        LateMarkDecision.Allowed(
                            LateMarkWrites(
                                mark = RecordWrite.Mark(
                                    date = ReferenceDay.date(1),
                                    prayer = Prayer.OGLE,
                                    markedAt = ReferenceDay.at(1, "16:00"),
                                    asCuma = false,
                                    asKaza = true
                                ),
                                kazaWrites = listOf(
                                    KazaWrite.AddEntry(
                                        prayer = Prayer.OGLE,
                                        sourceDate = ReferenceDay.date(1),
                                        kind = KazaEntryKind.ONERI,
                                        count = 1,
                                        corrects = null,
                                        missedInSefer = false,
                                        createdAt = ReferenceDay.at(1, "16:00")
                                    ),
                                    KazaWrite.AddPerformed(
                                        prayer = Prayer.OGLE,
                                        performedAt = ReferenceDay.at(1, "16:00")
                                    )
                                ),
                                grouped = true
                            )
                        ),
                        DayRecord.empty(ReferenceDay.date(1)).decideLateMark(
                            Prayer.OGLE,
                            LateChoice.AS_KAZA,
                            cw1(),
                            ledger,
                            ReferenceDay.clockAt(1, "16:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 otomatik kayit varken kaza olarak",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(ce("e1", KazaEntryKind.OTOMATIK)),
                        performed = emptyList()
                    )
                    assertEquals(
                        LateMarkDecision.Allowed(
                            LateMarkWrites(
                                mark = RecordWrite.Mark(
                                    date = ReferenceDay.date(1),
                                    prayer = Prayer.OGLE,
                                    markedAt = ReferenceDay.at(1, "16:00"),
                                    asCuma = false,
                                    asKaza = true
                                ),
                                kazaWrites = listOf(
                                    KazaWrite.AddPerformed(
                                        prayer = Prayer.OGLE,
                                        performedAt = ReferenceDay.at(1, "16:00")
                                    )
                                ),
                                grouped = true
                            )
                        ),
                        DayRecord.empty(ReferenceDay.date(1)).decideLateMark(
                            Prayer.OGLE,
                            LateChoice.AS_KAZA,
                            cw1(),
                            ledger,
                            ReferenceDay.clockAt(1, "16:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 cuma gunu ogle gec isaret",
                Executable {
                    assertEquals(
                        LateMarkDecision.Allowed(
                            LateMarkWrites(
                                mark = RecordWrite.Mark(
                                    date = ReferenceDay.date(5),
                                    prayer = Prayer.OGLE,
                                    markedAt = ReferenceDay.at(5, "16:00"),
                                    asCuma = false,
                                    asKaza = false
                                ),
                                kazaWrites = emptyList(),
                                grouped = false
                            )
                        ),
                        DayRecord.empty(ReferenceDay.date(5)).decideLateMark(
                            Prayer.OGLE,
                            LateChoice.ON_TIME,
                            cw5(),
                            KazaLedger.EMPTY,
                            ReferenceDay.clockAt(5, "16:00")
                        )
                    )
                }
            )
        )

        @JvmStatic
        fun eklenmisBorcDurumlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "DATA-05 oneri kaydi varken vaktinde",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(ce("e1", KazaEntryKind.ONERI)),
                        performed = emptyList()
                    )
                    val decision = DayRecord.empty(ReferenceDay.date(1)).decideLateMark(
                        Prayer.OGLE,
                        LateChoice.ON_TIME,
                        cw1(),
                        ledger,
                        ReferenceDay.clockAt(1, "16:00")
                    )
                    assertTrue(decision is LateMarkDecision.NeedsConfirmation)
                    val confirmation = decision as LateMarkDecision.NeedsConfirmation
                    assertEquals(
                        LateMarkQuestion.UNDO_ADDED_DEBT,
                        confirmation.question
                    )
                    assertEquals(
                        listOf(
                            KazaWrite.AddEntry(
                                prayer = Prayer.OGLE,
                                sourceDate = ReferenceDay.date(1),
                                kind = KazaEntryKind.DUZELTME,
                                count = -1,
                                corrects = KazaRecordId("e1"),
                                missedInSefer = false,
                                createdAt = ReferenceDay.at(1, "16:00")
                            )
                        ),
                        confirmation.ifConfirmed.kazaWrites
                    )
                    assertTrue(confirmation.ifDeclined.kazaWrites.isEmpty())
                }
            ),
            Arguments.argumentSet(
                "DATA-05 otomatik kaydi varken vaktinde",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(ce("e1", KazaEntryKind.OTOMATIK)),
                        performed = emptyList()
                    )
                    val decision = DayRecord.empty(ReferenceDay.date(1)).decideLateMark(
                        Prayer.OGLE,
                        LateChoice.ON_TIME,
                        cw1(),
                        ledger,
                        ReferenceDay.clockAt(1, "16:00")
                    )
                    assertTrue(decision is LateMarkDecision.NeedsConfirmation)
                    val confirmation = decision as LateMarkDecision.NeedsConfirmation
                    val correction =
                        confirmation.ifConfirmed.kazaWrites.single() as KazaWrite.AddEntry
                    assertEquals(KazaRecordId("e1"), correction.corrects)
                }
            ),
            Arguments.argumentSet(
                "DATA-05 geri alma borcu eksiye dusururse",
                Executable {
                    val ledger = KazaLedger(
                        entries = listOf(ce("e1", KazaEntryKind.ONERI)),
                        performed = listOf(cp("p1"))
                    )
                    assertEquals(
                        LateMarkDecision.Allowed(
                            LateMarkWrites(
                                mark = RecordWrite.Mark(
                                    date = ReferenceDay.date(1),
                                    prayer = Prayer.OGLE,
                                    markedAt = ReferenceDay.at(1, "16:00"),
                                    asCuma = false,
                                    asKaza = false
                                ),
                                kazaWrites = emptyList(),
                                grouped = false
                            )
                        ),
                        DayRecord.empty(ReferenceDay.date(1)).decideLateMark(
                            Prayer.OGLE,
                            LateChoice.ON_TIME,
                            cw1(),
                            ledger,
                            ReferenceDay.clockAt(1, "16:00")
                        )
                    )
                }
            )
        )

        private fun cw1(): PrayerDayWindows =
            PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))

        private fun cw3(): PrayerDayWindows =
            PrayerDayWindows(ReferenceDay.times(3), ReferenceDay.times(4))

        private fun cw5(): PrayerDayWindows =
            PrayerDayWindows(ReferenceDay.times(5), ReferenceDay.times(6))

        private fun mark(prayer: Prayer, time: String, asKaza: Boolean): PrayerMark = PrayerMark(
            prayer = prayer,
            markedAt = ReferenceDay.at(1, time),
            asCuma = false,
            asKaza = asKaza,
            operationId = if (asKaza) OperationId("op1") else null
        )

        private fun entry(
            id: String,
            kind: KazaEntryKind,
            sourceDate: LocalDate?,
            createdAt: Instant
        ): KazaEntry = KazaEntry(
            id = KazaRecordId(id),
            prayer = Prayer.OGLE,
            sourceDate = sourceDate,
            kind = kind,
            count = 1,
            corrects = null,
            missedInSefer = false,
            createdAt = createdAt
        )

        private fun ce(id: String, kind: KazaEntryKind): KazaEntry = entry(
            id = id,
            kind = kind,
            sourceDate = ReferenceDay.date(1),
            createdAt = ReferenceDay.at(1, "15:40")
        )

        private fun cp(id: String): KazaPerformed = KazaPerformed(
            id = KazaRecordId(id),
            prayer = Prayer.OGLE,
            performedAt = ReferenceDay.at(1, "15:45")
        )
    }
}

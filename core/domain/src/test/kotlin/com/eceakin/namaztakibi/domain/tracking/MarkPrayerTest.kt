package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.OperationId
import com.eceakin.namaztakibi.domain.settings.DaySettings
import com.eceakin.namaztakibi.domain.settings.Gender
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import com.eceakin.namaztakibi.domain.tracking.testing.DayRecordFixture
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class MarkPrayerTest {
    private val windows1 = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))
    private val windows5 = PrayerDayWindows(ReferenceDay.times(5), ReferenceDay.times(6))

    @Test
    fun KT01_BR02_ogleVaktiGirmeden1229daIsaretlenemez() {
        assertEquals(
            MarkDecision.Blocked(MarkBlock.NOT_YET_IN_TIME),
            DayRecord.empty(ReferenceDay.date(1)).decideMark(
                Prayer.OGLE,
                asCuma = false,
                windows1,
                ReferenceDay.clockAt(1, "12:29")
            )
        )
    }

    @Test
    fun KT02_BR02_ogle1230daIsaretlenir() {
        val record = DayRecord.empty(ReferenceDay.date(1))
        val decision = record.decideMark(
            Prayer.OGLE,
            asCuma = false,
            windows1,
            ReferenceDay.clockAt(1, "12:30")
        )

        assertTrue(decision is MarkDecision.Allowed)
        val allowed = decision as MarkDecision.Allowed
        assertEquals(
            RecordWrite.Mark(
                date = ReferenceDay.date(1),
                prayer = Prayer.OGLE,
                markedAt = ReferenceDay.at(1, "12:30"),
                asCuma = false,
                asKaza = false
            ),
            allowed.write
        )
        val after = DayRecordFixture.apply(record, listOf(allowed.write))
        assertEquals(
            PrayerStatus.KILINDI,
            after.statusOf(Prayer.OGLE, windows1, ReferenceDay.clockAt(1, "12:30"))
        )
    }

    @Test
    fun KT03_BR02_yatsi1000daIsaretlenemez() {
        assertEquals(
            MarkDecision.Blocked(MarkBlock.NOT_YET_IN_TIME),
            DayRecord.empty(ReferenceDay.date(1)).decideMark(
                Prayer.YATSI,
                asCuma = false,
                windows1,
                ReferenceDay.clockAt(1, "10:00")
            )
        )
    }

    @Test
    fun KT04_BR03_yatsiBosykenVitirIsaretlenemez() {
        assertEquals(
            MarkDecision.Blocked(MarkBlock.YATSI_FIRST),
            DayRecord.empty(ReferenceDay.date(1)).decideMark(
                Prayer.VITIR,
                asCuma = false,
                windows1,
                ReferenceDay.clockAt(1, "20:00")
            )
        )
    }

    @Test
    fun KT05_BR03_yatsiKilindiysaVitirIsaretlenir() {
        val record = record1(mark(Prayer.YATSI, "19:45"))

        assertEquals(
            MarkDecision.Allowed(
                RecordWrite.Mark(
                    date = ReferenceDay.date(1),
                    prayer = Prayer.VITIR,
                    markedAt = ReferenceDay.at(1, "20:00"),
                    asCuma = false,
                    asKaza = false
                )
            ),
            record.decideMark(
                Prayer.VITIR,
                asCuma = false,
                windows1,
                ReferenceDay.clockAt(1, "20:00")
            )
        )
    }

    @Test
    fun KT06_BR03_ertesiGun0529VitirG1KaydinaYazilir() {
        val record = record1(mark(Prayer.YATSI, "19:45"))

        assertEquals(
            MarkDecision.Allowed(
                RecordWrite.Mark(
                    date = ReferenceDay.date(1),
                    prayer = Prayer.VITIR,
                    markedAt = ReferenceDay.at(2, "05:29"),
                    asCuma = false,
                    asKaza = false
                )
            ),
            record.decideMark(
                Prayer.VITIR,
                asCuma = false,
                windows1,
                ReferenceDay.clockAt(2, "05:29")
            )
        )
    }

    @Test
    fun KT15_BR08_erkekCumaGunuCumaSecer_ogleKilinmisSayilir() {
        assertEquals(
            OgleOption.OGLE_OR_CUMA,
            ogleOptionOf(ReferenceDay.date(5), Gender.MALE)
        )

        val record = DayRecord.empty(ReferenceDay.date(5))
        val decision = record.decideMark(
            Prayer.OGLE,
            asCuma = true,
            windows5,
            ReferenceDay.clockAt(5, "13:00")
        )

        assertTrue(decision is MarkDecision.Allowed)
        val allowed = decision as MarkDecision.Allowed
        assertEquals(
            RecordWrite.Mark(
                date = ReferenceDay.date(5),
                prayer = Prayer.OGLE,
                markedAt = ReferenceDay.at(5, "13:00"),
                asCuma = true,
                asKaza = false
            ),
            allowed.write
        )
        val after = DayRecordFixture.apply(record, listOf(allowed.write))
        assertEquals(
            PrayerStatus.KILINDI,
            after.statusOf(Prayer.OGLE, windows5, ReferenceDay.clockAt(5, "13:00"))
        )
    }

    @Test
    fun KT17_BR08_kadindaCumaIstegeBagli() {
        assertEquals(
            OgleOption.OGLE_WITH_OPTIONAL_CUMA,
            ogleOptionOf(ReferenceDay.date(5), Gender.FEMALE)
        )

        val decision = DayRecord.empty(ReferenceDay.date(5)).decideMark(
            Prayer.OGLE,
            asCuma = true,
            windows5,
            ReferenceDay.clockAt(5, "13:00")
        )

        assertTrue(decision is MarkDecision.Allowed)
        assertEquals(true, (decision as MarkDecision.Allowed).write.asCuma)
    }

    @Test
    fun KT18_FR_TRK_03_isaretGeriAlinir_vaktiCikincaOneri() {
        val record = DayRecordFixture.apply(
            DayRecordFixture.markedAtStart(
                ReferenceDay.date(1),
                windows1,
                Prayer.SABAH,
                Prayer.IKINDI,
                Prayer.AKSAM,
                Prayer.YATSI,
                Prayer.VITIR
            ),
            listOf(
                RecordWrite.Mark(
                    date = ReferenceDay.date(1),
                    prayer = Prayer.OGLE,
                    markedAt = ReferenceDay.at(1, "12:40"),
                    asCuma = false,
                    asKaza = false
                )
            )
        )
        val decision = record.decideUnmark(Prayer.OGLE)

        assertTrue(decision is UnmarkDecision.Allowed)
        val allowed = decision as UnmarkDecision.Allowed
        assertEquals(
            RecordWrite.Clear(date = ReferenceDay.date(1), prayer = Prayer.OGLE),
            allowed.write
        )

        val after = DayRecordFixture.apply(record, listOf(allowed.write))
        assertEquals(
            PrayerStatus.BELIRSIZ,
            after.statusOf(Prayer.OGLE, windows1, ReferenceDay.clockAt(1, "12:45"))
        )
        assertEquals(
            listOf(MissedOutcome.Suggest(ReferenceDay.date(1), Prayer.OGLE)),
            after.evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                DaySettings(autoAddDebt = false),
                ReferenceDay.at(1, "00:00"),
                ReferenceDay.clockAt(1, "15:30")
            )
        )
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("kararSiralari")
    fun FR_TRK_kararSiralari(action: Executable) {
        action.execute()
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("geriAlmaKurallari")
    fun FR_TRK_03_geriAlmaKurallari(action: Executable) {
        action.execute()
    }

    @Test
    fun KS_TRK_01_isaretlenmeSaatiYazilir() {
        val before = DayRecord.empty(ReferenceDay.date(1))
        val clock = ReferenceDay.clockAt(1, "13:00")
        val otherPrayers = Prayer.entries.filter { it != Prayer.OGLE }
        val statusesBefore = otherPrayers.associateWith {
            before.statusOf(it, windows1, clock)
        }

        val decision = before.decideMark(
            Prayer.OGLE,
            asCuma = false,
            windows1,
            clock
        )
        assertTrue(decision is MarkDecision.Allowed)
        val allowed = decision as MarkDecision.Allowed
        assertEquals(ReferenceDay.at(1, "13:00"), allowed.write.markedAt)

        val after = DayRecordFixture.apply(before, listOf(allowed.write))
        otherPrayers.forEach { prayer ->
            assertEquals(statusesBefore[prayer], after.statusOf(prayer, windows1, clock))
        }
    }

    private fun record1(vararg marks: PrayerMark): DayRecord = DayRecord(
        date = ReferenceDay.date(1),
        marks = marks.toList()
    )

    private fun mark(
        prayer: Prayer,
        time: String,
        asCuma: Boolean = false,
        asKaza: Boolean = false
    ): PrayerMark = PrayerMark(
        prayer = prayer,
        markedAt = ReferenceDay.at(1, time),
        asCuma = asCuma,
        asKaza = asKaza
    )

    private companion object {
        @JvmStatic
        fun kararSiralari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-TRK-03 isaretli kaleme isaret",
                Executable {
                    val record = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(cm(Prayer.OGLE, "12:40"))
                    )
                    assertEquals(
                        MarkDecision.Blocked(MarkBlock.ALREADY_MARKED),
                        record.decideMark(
                            Prayer.OGLE,
                            asCuma = false,
                            cw1(),
                            ReferenceDay.clockAt(1, "13:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 vakti cikmis kalem secim ister",
                Executable {
                    assertEquals(
                        MarkDecision.NeedsLateChoice,
                        DayRecord.empty(ReferenceDay.date(1)).decideMark(
                            Prayer.OGLE,
                            asCuma = false,
                            cw1(),
                            ReferenceDay.clockAt(1, "15:30")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-04 vakti cikmis vitir de secim ister",
                Executable {
                    assertEquals(
                        MarkDecision.NeedsLateChoice,
                        DayRecord.empty(ReferenceDay.date(1)).decideMark(
                            Prayer.VITIR,
                            asCuma = false,
                            cw1(),
                            ReferenceDay.clockAt(2, "05:30")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "BR-08 cuma olmayan gunde cuma",
                Executable {
                    assertEquals(
                        MarkDecision.Blocked(MarkBlock.CUMA_NOT_ALLOWED),
                        DayRecord.empty(ReferenceDay.date(1)).decideMark(
                            Prayer.OGLE,
                            asCuma = true,
                            cw1(),
                            ReferenceDay.clockAt(1, "13:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "BR-08 ikindide cuma",
                Executable {
                    assertEquals(
                        MarkDecision.Blocked(MarkBlock.CUMA_NOT_ALLOWED),
                        DayRecord.empty(ReferenceDay.date(5)).decideMark(
                            Prayer.IKINDI,
                            asCuma = true,
                            cw5(),
                            ReferenceDay.clockAt(5, "16:00")
                        )
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-06 cuma olmayan gun ogle secenegi",
                Executable {
                    assertEquals(
                        OgleOption.OGLE_ONLY,
                        ogleOptionOf(ReferenceDay.date(1), Gender.MALE)
                    )
                }
            )
        )

        @JvmStatic
        fun geriAlmaKurallari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-TRK-03 isaretsiz kalem",
                Executable {
                    assertEquals(
                        UnmarkDecision.Blocked(UnmarkBlock.NOT_MARKED),
                        DayRecord.empty(ReferenceDay.date(1)).decideUnmark(Prayer.OGLE)
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-03 vitir isaretliyken yatsi",
                Executable {
                    val record = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(cm(Prayer.YATSI, "19:45"), cm(Prayer.VITIR, "20:00"))
                    )
                    assertEquals(
                        UnmarkDecision.Blocked(UnmarkBlock.UNMARK_VITIR_FIRST),
                        record.decideUnmark(Prayer.YATSI)
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-03 kaza isareti",
                Executable {
                    val record = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(cm(Prayer.OGLE, "13:00", asKaza = true))
                    )
                    assertEquals(
                        UnmarkDecision.Blocked(UnmarkBlock.KAZA_MARK_NEEDS_GROUP_UNDO),
                        record.decideUnmark(Prayer.OGLE)
                    )
                }
            ),
            Arguments.argumentSet(
                "FR-TRK-03 vitir geri alinir",
                Executable {
                    val record = DayRecord(
                        date = ReferenceDay.date(1),
                        marks = listOf(cm(Prayer.YATSI, "19:45"), cm(Prayer.VITIR, "20:00"))
                    )
                    assertEquals(
                        UnmarkDecision.Allowed(
                            RecordWrite.Clear(ReferenceDay.date(1), Prayer.VITIR)
                        ),
                        record.decideUnmark(Prayer.VITIR)
                    )
                }
            )
        )

        private fun cw1(): PrayerDayWindows =
            PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))

        private fun cw5(): PrayerDayWindows =
            PrayerDayWindows(ReferenceDay.times(5), ReferenceDay.times(6))

        private fun cm(prayer: Prayer, time: String, asKaza: Boolean = false): PrayerMark =
            PrayerMark(
                prayer = prayer,
                markedAt = ReferenceDay.at(1, time),
                asCuma = false,
                asKaza = asKaza,
                operationId = if (asKaza) OperationId("op1") else null
            )
    }
}

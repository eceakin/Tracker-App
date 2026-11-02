package com.eceakin.namaztakibi.domain.tracking

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.kaza.KazaEntry
import com.eceakin.namaztakibi.domain.kaza.KazaEntryKind
import com.eceakin.namaztakibi.domain.kaza.KazaLedger
import com.eceakin.namaztakibi.domain.kaza.KazaRecordId
import com.eceakin.namaztakibi.domain.kaza.KazaWrite
import com.eceakin.namaztakibi.domain.kaza.testing.LedgerFixture
import com.eceakin.namaztakibi.domain.settings.DaySettings
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import com.eceakin.namaztakibi.domain.times.PrayerDayWindows
import com.eceakin.namaztakibi.domain.tracking.testing.DayRecordFixture
import java.time.Instant
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class MissedSuggestionTest {
    private val windows1 = PrayerDayWindows(ReferenceDay.times(1), ReferenceDay.times(2))
    private val windows2 = PrayerDayWindows(ReferenceDay.times(2), ReferenceDay.times(3))
    private val windows3 = PrayerDayWindows(ReferenceDay.times(3), ReferenceDay.times(4))
    private val windows5 = PrayerDayWindows(ReferenceDay.times(5), ReferenceDay.times(6))
    private val g1 = ReferenceDay.date(1)
    private val g2 = ReferenceDay.date(2)
    private val g3 = ReferenceDay.date(3)
    private val g5 = ReferenceDay.date(5)
    private val settingsOff = DaySettings(autoAddDebt = false)
    private val trackingStart = ReferenceDay.at(1, "00:00")

    @Test
    fun KT07_BR04_ogle1529daHenuzKacirilmadi() {
        assertEquals(
            emptyList<MissedOutcome>(),
            markedExceptOgle(g1, windows1).evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(1, "15:29")
            )
        )
    }

    @Test
    fun KT08_BR04_ogle1530daKilinmadi_oneriGelir() {
        val record = markedExceptOgle(g1, windows1)
        val clock = ReferenceDay.clockAt(1, "15:30")

        assertEquals(PrayerStatus.KILINMADI, record.statusOf(Prayer.OGLE, windows1, clock))
        assertEquals(
            listOf(MissedOutcome.Suggest(g1, Prayer.OGLE)),
            record.evaluateMissed(windows1, KazaLedger.EMPTY, settingsOff, trackingStart, clock)
        )
    }

    @Test
    fun KT09_BR00_otomatikEklemeAcikken_oneriYerineOtomatikKayit() {
        val record = markedExceptOgle(g1, windows1)
        val clock = ReferenceDay.clockAt(1, "15:30")
        val expected = KazaWrite.AddEntry(
            prayer = Prayer.OGLE,
            sourceDate = g1,
            kind = KazaEntryKind.OTOMATIK,
            count = 1,
            corrects = null,
            missedInSefer = false,
            createdAt = ReferenceDay.at(1, "15:30")
        )

        assertEquals(
            listOf(MissedOutcome.AutoAdd(expected)),
            record.evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                DaySettings(autoAddDebt = true),
                trackingStart,
                clock
            )
        )

        val ledger = LedgerFixture.apply(KazaLedger.EMPTY, listOf(expected))
        assertEquals(1, ledger.balance(Prayer.OGLE))
        assertEquals(KazaEntryKind.OTOMATIK, ledger.entries.single().kind)
        assertEquals(
            emptyList<MissedOutcome>(),
            record.evaluateMissed(
                windows1,
                ledger,
                DaySettings(autoAddDebt = true),
                trackingStart,
                clock
            )
        )
    }

    @Test
    fun KT10_BR04_sabah0700deKilinmadi_oneriGelir() {
        val record = DayRecord.empty(g1)
        val clock = ReferenceDay.clockAt(1, "07:00")

        assertEquals(PrayerStatus.KILINMADI, record.statusOf(Prayer.SABAH, windows1, clock))
        assertEquals(
            listOf(MissedOutcome.Suggest(g1, Prayer.SABAH)),
            record.evaluateMissed(windows1, KazaLedger.EMPTY, settingsOff, trackingStart, clock)
        )
    }

    @Test
    fun KT11_BR04_yatsiVeVitirIcinIkiAyriOneri() {
        val record = DayRecordFixture.markedAtStart(
            g1,
            windows1,
            Prayer.SABAH,
            Prayer.OGLE,
            Prayer.IKINDI,
            Prayer.AKSAM
        )

        assertEquals(
            listOf(
                MissedOutcome.Suggest(g1, Prayer.YATSI),
                MissedOutcome.Suggest(g1, Prayer.VITIR)
            ),
            record.evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(2, "05:30")
            )
        )
    }

    @Test
    fun KT16_BR08_cumaKacirilinca_oneriOgleIcin() {
        val record = DayRecordFixture.markedAtStart(
            g5,
            windows5,
            Prayer.SABAH,
            Prayer.IKINDI,
            Prayer.AKSAM,
            Prayer.YATSI,
            Prayer.VITIR
        )

        assertEquals(
            listOf(MissedOutcome.Suggest(g5, Prayer.OGLE)),
            record.evaluateMissed(
                windows5,
                KazaLedger.EMPTY,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(5, "15:30")
            )
        )
    }

    @Test
    fun KT34_BR09_bayramNamaziBorcaYazilmaz() {
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

        val record = DayRecordFixture.markedAtStart(
            g1,
            windows1,
            *Prayer.entries.toTypedArray()
        )
        assertEquals(
            emptyList<MissedOutcome>(),
            record.evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(2, "05:30")
            )
        )
    }

    @Test
    fun KS_ONB_07_kurulumOncesiVakitIcinOneriYok() {
        assertEquals(
            listOf(MissedOutcome.Suggest(g1, Prayer.IKINDI)),
            DayRecord.empty(g1).evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                settingsOff,
                ReferenceDay.at(1, "16:00"),
                ReferenceDay.clockAt(1, "18:00")
            )
        )
    }

    @Test
    fun KS_TRK_13_kurulumdanOncekiGunIcinOneriYok() {
        assertEquals(
            emptyList<MissedOutcome>(),
            DayRecord.empty(g3).evaluateMissed(
                windows3,
                KazaLedger.EMPTY,
                settingsOff,
                ReferenceDay.at(5, "10:00"),
                ReferenceDay.clockAt(5, "10:00")
            )
        )
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("kurulumSinirlari")
    fun FR_ONB_05_kurulumSiniri(trackingStart: Instant, expected: List<MissedOutcome>) {
        assertEquals(
            expected,
            markedExceptOgle(g1, windows1).evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(1, "15:30")
            )
        )
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("borcaYazilmisDurumlar")
    fun DATA05_borcaYazilmisKalemIcinOneriYok(ledger: KazaLedger, expected: List<MissedOutcome>) {
        assertEquals(
            expected,
            markedExceptOgle(g1, windows1).evaluateMissed(
                windows1,
                ledger,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(1, "15:30")
            )
        )
    }

    @Test
    fun KS_KAZ_04_borcaEkleOneriKaydiYazar() {
        val suggestion = MissedOutcome.Suggest(g1, Prayer.OGLE)
        val write = confirmSuggestion(suggestion, ReferenceDay.clockAt(1, "15:40"))

        assertEquals(
            KazaWrite.AddEntry(
                prayer = Prayer.OGLE,
                sourceDate = g1,
                kind = KazaEntryKind.ONERI,
                count = 1,
                corrects = null,
                missedInSefer = false,
                createdAt = ReferenceDay.at(1, "15:40")
            ),
            write
        )

        val ledger = LedgerFixture.apply(KazaLedger.EMPTY, listOf(write))
        assertEquals(1, ledger.balance(Prayer.OGLE))
        assertTrue(
            markedExceptOgle(g1, windows1).evaluateMissed(
                windows1,
                ledger,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(1, "15:40")
            ).isEmpty()
        )
    }

    @Test
    fun KS_KAZ_05_otomatikKapaliykenSessizBorcYok() {
        assertEquals(
            Prayer.entries.map { prayer -> MissedOutcome.Suggest(g1, prayer) },
            DayRecord.empty(g1).evaluateMissed(
                windows1,
                KazaLedger.EMPTY,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(2, "07:00")
            )
        )
        assertEquals(
            listOf(MissedOutcome.Suggest(g2, Prayer.SABAH)),
            DayRecord.empty(g2).evaluateMissed(
                windows2,
                KazaLedger.EMPTY,
                settingsOff,
                trackingStart,
                ReferenceDay.clockAt(2, "07:00")
            )
        )
        assertTrue(KazaLedger.EMPTY.entries.isEmpty())
        assertTrue(KazaLedger.EMPTY.performed.isEmpty())
    }

    private fun markedExceptOgle(date: LocalDate, windows: PrayerDayWindows): DayRecord =
        DayRecordFixture.markedAtStart(
            date,
            windows,
            Prayer.SABAH,
            Prayer.IKINDI,
            Prayer.AKSAM,
            Prayer.YATSI,
            Prayer.VITIR
        )

    private companion object {
        @JvmStatic
        fun kurulumSinirlari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "FR-ONB-05 kurulum ogle bitisine esit",
                ReferenceDay.at(1, "15:30"),
                emptyList<MissedOutcome>()
            ),
            Arguments.argumentSet(
                "FR-ONB-05 kurulum bir dakika once",
                ReferenceDay.at(1, "15:29"),
                listOf(MissedOutcome.Suggest(ReferenceDay.date(1), Prayer.OGLE))
            )
        )

        @JvmStatic
        fun borcaYazilmisDurumlar(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "DATA-05 oneri kaydi var",
                ledgerOf(entry("e1", KazaEntryKind.ONERI, ReferenceDay.date(1))),
                emptyList<MissedOutcome>()
            ),
            Arguments.argumentSet(
                "DATA-05 otomatik kaydi var",
                ledgerOf(entry("e1", KazaEntryKind.OTOMATIK, ReferenceDay.date(1))),
                emptyList<MissedOutcome>()
            ),
            Arguments.argumentSet(
                "DATA-05 geri alinmis oneri",
                KazaLedger(
                    entries = listOf(
                        entry("e1", KazaEntryKind.ONERI, ReferenceDay.date(1)),
                        entry(
                            "d1",
                            KazaEntryKind.DUZELTME,
                            sourceDate = null,
                            count = -1,
                            corrects = KazaRecordId("e1")
                        )
                    ),
                    performed = emptyList()
                ),
                listOf(MissedOutcome.Suggest(ReferenceDay.date(1), Prayer.OGLE))
            ),
            Arguments.argumentSet(
                "DATA-05 baska gunun kaydi",
                ledgerOf(entry("e1", KazaEntryKind.ONERI, ReferenceDay.date(2))),
                listOf(MissedOutcome.Suggest(ReferenceDay.date(1), Prayer.OGLE))
            ),
            Arguments.argumentSet(
                "DATA-05 elle kayit sayilmaz",
                ledgerOf(entry("e1", KazaEntryKind.ELLE, null)),
                listOf(MissedOutcome.Suggest(ReferenceDay.date(1), Prayer.OGLE))
            )
        )

        private fun ledgerOf(entry: KazaEntry): KazaLedger = KazaLedger(
            entries = listOf(entry),
            performed = emptyList()
        )

        private fun entry(
            id: String,
            kind: KazaEntryKind,
            sourceDate: LocalDate?,
            count: Int = 1,
            corrects: KazaRecordId? = null
        ): KazaEntry = KazaEntry(
            id = KazaRecordId(id),
            prayer = Prayer.OGLE,
            sourceDate = sourceDate,
            kind = kind,
            count = count,
            corrects = corrects,
            missedInSefer = false,
            createdAt = ReferenceDay.at(1, "15:30")
        )
    }
}

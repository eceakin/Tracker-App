package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import com.eceakin.namaztakibi.domain.testing.ReferenceDay
import java.time.Instant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class KazaLedgerTest {
    @Test
    fun DATA04_kalanBorcKayitlardanHesaplanir() {
        val ledger = KazaLedger(
            entries = listOf(
                entry("e1", kind = KazaEntryKind.ELLE, count = 8),
                entry("e2", kind = KazaEntryKind.DUZELTME, count = 2)
            ),
            performed = listOf(
                performed("p1"),
                performed("p2")
            )
        )

        assertEquals(8, ledger.balance(Prayer.OGLE))
        assertEquals(0, ledger.balance(Prayer.IKINDI))
    }

    @Test
    fun DATA04_bosDefterdeHerKalemSifir() {
        Prayer.entries.forEach { prayer ->
            assertEquals(0, KazaLedger.EMPTY.balance(prayer))
        }
    }

    @Test
    fun DATA04_geriAlinanKayitTaninir() {
        val ledger = KazaLedger(
            entries = listOf(
                entry("e1", kind = KazaEntryKind.ONERI, count = 1),
                entry(
                    "e2",
                    kind = KazaEntryKind.DUZELTME,
                    count = -1,
                    corrects = KazaRecordId("e1")
                )
            ),
            performed = emptyList()
        )

        assertTrue(ledger.isCorrected(KazaRecordId("e1")))
        assertFalse(ledger.isCorrected(KazaRecordId("e2")))
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("tutarsizDefterler")
    fun M24_tutarsizDefterReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    private companion object {
        private val now: Instant = ReferenceDay.at(1, "14:00")

        @JvmStatic
        fun tutarsizDefterler(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "M-24 yinelenen kimlik",
                Executable {
                    KazaLedger(
                        entries = listOf(entry("e1")),
                        performed = listOf(performed("e1"))
                    )
                }
            ),
            Arguments.argumentSet(
                "M-24 olmayan kayda duzeltme",
                Executable {
                    KazaLedger(
                        entries = listOf(
                            entry(
                                "d1",
                                kind = KazaEntryKind.DUZELTME,
                                count = -1,
                                corrects = KazaRecordId("e9")
                            )
                        ),
                        performed = emptyList()
                    )
                }
            ),
            Arguments.argumentSet(
                "M-24 duzeltmenin duzeltmesi",
                Executable {
                    KazaLedger(
                        entries = listOf(
                            entry("e1", kind = KazaEntryKind.ONERI, count = 1),
                            entry(
                                "d1",
                                kind = KazaEntryKind.DUZELTME,
                                count = -1,
                                corrects = KazaRecordId("e1")
                            ),
                            entry(
                                "d2",
                                kind = KazaEntryKind.DUZELTME,
                                count = 1,
                                corrects = KazaRecordId("d1")
                            )
                        ),
                        performed = emptyList()
                    )
                }
            ),
            Arguments.argumentSet(
                "M-24 iki kez geri alma",
                Executable {
                    KazaLedger(
                        entries = listOf(
                            entry("e1", kind = KazaEntryKind.ONERI, count = 1),
                            entry(
                                "d1",
                                kind = KazaEntryKind.DUZELTME,
                                count = -1,
                                corrects = KazaRecordId("e1")
                            ),
                            entry(
                                "d2",
                                kind = KazaEntryKind.DUZELTME,
                                count = 1,
                                corrects = KazaRecordId("e1")
                            )
                        ),
                        performed = emptyList()
                    )
                }
            ),
            Arguments.argumentSet(
                "M-24 farkli kalemde duzeltme",
                Executable {
                    KazaLedger(
                        entries = listOf(
                            entry("e1", kind = KazaEntryKind.ONERI, count = 1),
                            entry(
                                "e2",
                                prayer = Prayer.IKINDI,
                                kind = KazaEntryKind.ELLE,
                                count = 2
                            ),
                            entry(
                                "d1",
                                prayer = Prayer.IKINDI,
                                kind = KazaEntryKind.DUZELTME,
                                count = -1,
                                corrects = KazaRecordId("e1")
                            )
                        ),
                        performed = emptyList()
                    )
                }
            ),
            Arguments.argumentSet(
                "M-24 negatif bakiye",
                Executable {
                    KazaLedger(
                        entries = emptyList(),
                        performed = listOf(performed("p1"))
                    )
                }
            )
        )

        private fun entry(
            id: String,
            prayer: Prayer = Prayer.OGLE,
            kind: KazaEntryKind = KazaEntryKind.ELLE,
            count: Int = 1,
            corrects: KazaRecordId? = null
        ): KazaEntry = KazaEntry(
            id = KazaRecordId(id),
            prayer = prayer,
            sourceDate = null,
            kind = kind,
            count = count,
            corrects = corrects,
            missedInSefer = false,
            createdAt = now
        )

        private fun performed(id: String, prayer: Prayer = Prayer.OGLE): KazaPerformed =
            KazaPerformed(
                id = KazaRecordId(id),
                prayer = prayer,
                performedAt = now
            )
    }
}

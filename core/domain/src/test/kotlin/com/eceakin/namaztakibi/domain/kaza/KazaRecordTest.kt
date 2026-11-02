package com.eceakin.namaztakibi.domain.kaza

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Instant
import java.time.LocalDate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class KazaRecordTest {
    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("kayitKurallari")
    fun DATA04_kayitTurleriVeAdetKurallari(action: Executable) {
        action.execute()
    }

    @Test
    fun M24_bosKimlikReddedilir() {
        assertThrows<IllegalArgumentException> { KazaRecordId("") }
        assertThrows<IllegalArgumentException> { KazaRecordId("  ") }
    }

    private companion object {
        @JvmStatic
        fun kayitKurallari(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "DATA-04 ELLE adet 0 reddedilir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(kind = KazaEntryKind.ELLE, count = 0)
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 ONERI adet 0 reddedilir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(kind = KazaEntryKind.ONERI, count = 0)
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 OTOMATIK adet 0 reddedilir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(kind = KazaEntryKind.OTOMATIK, count = 0)
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 HESAPLAYICI adet 0 reddedilir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(kind = KazaEntryKind.HESAPLAYICI, count = 0)
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 ELLE negatif adet reddedilir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(kind = KazaEntryKind.ELLE, count = -1)
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 ONERI corrects dolu reddedilir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(
                            kind = KazaEntryKind.ONERI,
                            count = 1,
                            corrects = KazaRecordId("e2")
                        )
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 DUZELTME adet 0 reddedilir",
                Executable {
                    assertThrows<IllegalArgumentException> {
                        entry(kind = KazaEntryKind.DUZELTME, count = 0)
                    }
                }
            ),
            Arguments.argumentSet(
                "DATA-04 DUZELTME negatif adet corrects null kabul edilir",
                Executable {
                    entry(kind = KazaEntryKind.DUZELTME, count = -3)
                }
            ),
            Arguments.argumentSet(
                "DATA-04 DUZELTME pozitif adet corrects dolu kabul edilir",
                Executable {
                    entry(
                        kind = KazaEntryKind.DUZELTME,
                        count = 2,
                        corrects = KazaRecordId("e2")
                    )
                }
            )
        )

        private fun entry(
            kind: KazaEntryKind,
            count: Int,
            corrects: KazaRecordId? = null
        ): KazaEntry = KazaEntry(
            id = KazaRecordId("e1"),
            prayer = Prayer.OGLE,
            sourceDate = LocalDate.of(2026, 3, 2),
            kind = kind,
            count = count,
            corrects = corrects,
            missedInSefer = false,
            createdAt = Instant.parse("2026-03-02T11:00:00Z")
        )
    }
}

package com.eceakin.namaztakibi.domain.rules

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class RuleSetTest {
    private val hanefi = RuleSet.HANEFI

    @Test
    fun BR07_kerahatPencereleri45Dakika() {
        assertEquals(Duration.ofMinutes(45), hanefi.kerahatAfterGunes)
        assertEquals(Duration.ofMinutes(45), hanefi.kerahatBeforeOgle)
        assertEquals(Duration.ofMinutes(45), hanefi.kerahatBeforeAksam)
    }

    @Test
    fun BR12_gusul5ile30Arasi_varsayilan15_tekbir1Dakika() {
        assertEquals(Duration.ofMinutes(5), hanefi.gusulMin)
        assertEquals(Duration.ofMinutes(30), hanefi.gusulMax)
        assertEquals(Duration.ofMinutes(15), hanefi.gusulDefault)
        assertEquals(Duration.ofMinutes(1), hanefi.tekbirDuration)
    }

    @Test
    fun BR13_hayizAltSiniri72Saat() {
        assertEquals(Duration.ofHours(72), hanefi.hayizMin)
    }

    @Test
    fun BR14_hayizUstSiniri240Saat_aliskanlikGirilmemisse10Gun() {
        assertEquals(Duration.ofHours(240), hanefi.hayizMax)
        assertEquals(10, hanefi.defaultAdetHabitDays)
    }

    @Test
    fun BR15_nifasUstSiniri960Saat() {
        assertEquals(Duration.ofHours(960), hanefi.nifasMax)
    }

    @Test
    fun BR16_enAzTemizlik15Gun() {
        assertEquals(Duration.ofDays(15), hanefi.minTuhr)
    }

    @Test
    fun BR17_tertip6FarzBorctaDuser_rekatSuresi2Dakika() {
        assertEquals(6, hanefi.tertipFallsAtFarzDebt)
        assertEquals(Duration.ofMinutes(2), hanefi.rekatDuration)
    }

    @Test
    fun BR17_rekatSayilari() {
        assertEquals(
            mapOf(
                Prayer.SABAH to 2,
                Prayer.OGLE to 4,
                Prayer.IKINDI to 4,
                Prayer.AKSAM to 3,
                Prayer.YATSI to 4,
                Prayer.VITIR to 3
            ),
            hanefi.rekatCounts
        )
    }

    @Test
    fun BR18_seferde4RekatliFarz2Rekat() {
        assertEquals(2, hanefi.seferRekatForFourRekatFarz)
    }

    @Test
    fun BR19_bulugYaslari12ve9_oneri14() {
        assertEquals(12, hanefi.bulughMinAgeMale)
        assertEquals(9, hanefi.bulughMinAgeFemale)
        assertEquals(14, hanefi.bulughSuggestedAge)
    }

    @Test
    fun BR22_bayginlikta6FarzdaNamazlarDuser() {
        assertEquals(6, hanefi.bayginlikFallsAtMissedFarz)
    }

    @Test
    fun M21_surumKimligiHanefi1() {
        assertEquals("hanefi-1", hanefi.version)
    }

    @ParameterizedTest(name = "{argumentSetName}")
    @MethodSource("gecersizRuleSetler")
    fun M21_gecersizRuleSetReddedilir(action: Executable) {
        assertThrows<IllegalArgumentException> { action.execute() }
    }

    private companion object {
        @JvmStatic
        fun gecersizRuleSetler(): List<Arguments> = listOf(
            Arguments.argumentSet(
                "M-21 bos surum reddedilir",
                Executable { RuleSet.HANEFI.copy(version = "") }
            ),
            Arguments.argumentSet(
                "M-21 eksik VITIR anahtari reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(
                        rekatCounts = RuleSet.HANEFI.rekatCounts - Prayer.VITIR
                    )
                }
            ),
            Arguments.argumentSet(
                "M-21 sifir rekat reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(
                        rekatCounts = RuleSet.HANEFI.rekatCounts + (Prayer.SABAH to 0)
                    )
                }
            ),
            Arguments.argumentSet(
                "M-21 sifir sure reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(kerahatAfterGunes = Duration.ZERO)
                }
            ),
            Arguments.argumentSet(
                "M-21 negatif sure reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(kerahatBeforeOgle = Duration.ofMinutes(-1))
                }
            ),
            Arguments.argumentSet(
                "M-21 sifir Int reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(defaultAdetHabitDays = 0)
                }
            ),
            Arguments.argumentSet(
                "M-21 negatif Int reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(tertipFallsAtFarzDebt = -1)
                }
            ),
            Arguments.argumentSet(
                "M-21 hayizMin hayizMax esitken reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(hayizMin = RuleSet.HANEFI.hayizMax)
                }
            ),
            Arguments.argumentSet(
                "M-21 gusulDefault gusulMax ustundeyken reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(
                        gusulDefault = RuleSet.HANEFI.gusulMax + Duration.ofMinutes(1)
                    )
                }
            ),
            Arguments.argumentSet(
                "M-21 gusulMin gusulDefault ustundeyken reddedilir",
                Executable {
                    RuleSet.HANEFI.copy(
                        gusulMin = RuleSet.HANEFI.gusulDefault + Duration.ofMinutes(1)
                    )
                }
            )
        )
    }
}

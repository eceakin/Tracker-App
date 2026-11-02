# Görev Kartı F2-01 — Temel model ve test altyapısı

Faz 2 · Kart 1/13 · 2 Kasım 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

Faz 2'nin kalan 12 kartı karar tablosunun 101 satırını TDD ile koda dökecek. Bu kart, onların üzerine kurulacağı zemini hazırlar:

1. **`RuleSet.HANEFI`:** Bütün fıkhi sayılar tek bir değiştirilemez nesnede (M-21). Sonraki kartlar bu sayıları yalnızca buradan okur.
2. **`PrayerTimesDay`:** Bir ilçenin bir günlük altı vakti (SRS 6.1). Kural motorunun zaman girdisi budur.
3. **`ReferenceDay`:** Karar tablosundaki sabit referans gün fikstürü (K-17). Her KT testi "G6 17:00" gibi anları bu yardımcıyla yazar.
4. **Kapsam kapısı (K-15):** `:core:domain` satır kapsamı %90'ın altına düşerse `ciCheck` başarısız olur (NFR-MNT-01).
5. **KT izlenebilirlik kapısı (K-16):** `ciCheck`, karar tablosundaki her geçerli satırın ya bir testi olduğunu ya da "bekleyen KT listesinde" durduğunu iki yönlü denetler.

**Neden önce bu?** Faz 2'nin bitti tanımı "karar tablosundaki tüm senaryolar yeşil ve kapsam %90'ın üstünde" diyor. Bu iki koşulu kart kart elle saymak yerine ilk kartta CI'a koyuyoruz. Böylece bir test unutulursa, devre dışı bırakılırsa ya da tabloya yeni bir satır eklenirse CI bunu kendisi yakalar. Bu kartın sonunda bekleyen listede 101 kimlik vardır; her sonraki kart kendi kimliklerini listeden siler, F2-13'ün sonunda liste boştur.

**Bu kart kural mantığı yazmaz.** Vakit hesapları (F2-02), kayıtlar, öneriler ve mazeret yok. Yalnızca parametreler, veri modeli, test fikstürü ve iki kapı var.

## Ön koşullar

1. `main`, F1-04'ün birleştirildiği `01ef5cd` commit'inde ya da daha ilerde ve `origin/main` ile aynı.
2. Kullanıcı şu dört dosyayı çalışma ağacına koymuş olmalı (zip):
   - `CLAUDE.md` (sürüm 1.6)
   - `docs/mimari-ve-kodlama-ilkeleri.md` (sürüm 1.6)
   - `docs/gelistirme-yol-haritasi.md` (2 Kasım 2026 güncellemesi, Faz 2 kart tablosu)
   - `docs/tasks/F2-01-temel-model.md` (bu kart)
3. Çalışma ağacında bu dört dosya dışında yalnızca F1-05'e ait izlenmeyen (untracked) dosyalar bulunabilir (`docs/tasks/F1-05-*`). Bunlara **dokunma ve commit'leme**; F1-05'in kendi dalına aittir. Başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler ve kimlikler

- `CLAUDE.md` (tamamı; özellikle Bölüm 4, 7, 8, 10 ve 12; 1.6'da değişti)
- `docs/mimari-ve-kodlama-ilkeleri.md`: M-07, M-21, M-23, M-24, M-25, K-06, K-11, K-15, K-16, K-17
- `docs/karar-tablosu.md`: "Sözleşmeler" bölümü (zaman semantiği, referans gün fikstürü, varsayılan parametreler) ve tabloların satır biçimi. Satırların içeriğini bu kartta uygulamıyorsun; kapı yalnızca kimlikleri okur.
- `docs/gereksinim-analizi-srs.md`: Bölüm 4 (BR-07, BR-12 – BR-19, BR-22), Bölüm 6.1 (veri sözlüğü: `PrayerTimesDay`), NFR-MNT-01
- `docs/gelistirme-yol-haritasi.md`: Faz 2 kart tablosu (bekleyen KT listesinin kartlara göre gruplanması için)
- `docs/tasks/F1-04-hilt-ci.md` ve `docs/tasks/F1-04-rapor.md`: lint kapısının ve `ciCheck`'in nasıl kurulduğu. Yeni kapı aynı kalıbı izler.

## Adım 0 — Plan ve onay

Bu adımda dosya okuyabilir, resmî belgelere ve sürüm notlarına bakabilir, `git status`, `git branch -a`, `git log`, `git fetch`, `git stash list` gibi salt okunur git komutlarını ve `.\gradlew.bat lintDebug` çalıştırabilirsin. Hiçbir dosyayı değiştirme, dal değiştirme. Eski sohbet kayıtlarını (`~/.claude` altı) **okuma.**

Plana şunları yaz, sonra **kullanıcının onayını bekle:**

1. **Git başlangıcı:** `git status`, `git branch -a`, `git log --oneline -5`, `git stash list` çıktılarının özeti ve `git fetch` sonrası `main` ile `origin/main`'in aynı olduğu. Çalışma ağacındaki her değişikliğin hangi gruba girdiği (bu kartın dört dosyası / F1-05 izlenmeyen dosyaları / beklenmeyen).
2. **Faz başı sürüm gözden geçirmesi (mimari K-01):** `.\gradlew.bat lintDebug` sonrası lint XML raporlarındaki `GradleDependency`, `NewerVersionAvailable` ve `AndroidGradlePluginVersion` bulgularını, ayrıca lint'in denetlemediği Kotlin, JUnit, ktlint (motor ve eklenti) ve Gradle sürümlerini tek tabloda ver: catalog'daki sürüm, en yeni kararlı sürüm, kaynak (resmî sürüm notu ya da Maven deposu). **Hiçbir sürümü değiştirme;** karar planlamanındır. Gradle 9.6.1, F1-03'te bilinçli seçildi (F1-03 raporu).
3. **JaCoCo:**
   - Hedef sürüm `0.8.16` (24 Ekim 2026; JaCoCo değişiklik geçmişi: Java 27'ye kadar resmî destek). Bunu `https://www.jacoco.org/jacoco/trunk/doc/changes.html` adresinden doğrula.
   - Gradle 9.6.1'in `jacoco` eklentisinin varsayılan JaCoCo sürümünü, Gradle 9.6.1 kullanıcı kılavuzunun JaCoCo bölümünden yaz. `toolVersion` ile 0.8.16'ya geçmenin desteklendiğini doğrula.
   - Bir kaynağa ulaşamazsan bunu açıkça yaz; doğrulama A3'teki ilk derlemeye kalır.
4. **Dosya ağacı ve imzalar:** Bölüm A'daki sınıfların paketleri ve imzaları. Karttaki imzalardan sapacaksan her sapmanın gerekçesi.
5. **Test listesi:** Yazacağın her testin adı ve neyi sınadığı (A1 – A3).
6. **KT kapısı tasarımı:** Kullanacağın düzenli ifadeler, dosya okuma biçimi (karakter kodlaması dahil), bekleyen listenin koddaki biçimi ve her hata türü için örnek mesaj (bkz. B2).
7. **Commit sırası** (bkz. Bölüm D).

Onaydan sonra planı, onay mesajındaki değişikliklerle birlikte `docs/tasks/F2-01-plan.md` dosyasına yaz (`CLAUDE.md` "Plan dosyası").

## Bölüm A — Domain modeli ve fikstür (TDD)

Dal: güncel `main`'den `feat/f2-01-domain-foundation`. İlk commit, kullanıcının koyduğu dört belgedir; dosyaları **adıyla** ekle:

```
git add CLAUDE.md docs/mimari-ve-kodlama-ilkeleri.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-01-temel-model.md
git commit -m "docs: add F2-01 card and update planning docs"
```

İkinci commit plan dosyasıdır: `docs(tasks): add F2-01 plan`.

Her A adımında sıra şudur:

1. Testi yaz.
2. `.\gradlew.bat :core:domain:test` çalıştır ve testin derlenmediğini ya da kırmızı olduğunu gör. Bunu rapora bir satırla yaz (K-11).
3. Kodu yaz.
4. `.\gradlew.bat :core:domain:test` ve `.\gradlew.bat ktlintCheck` yeşil olsun.
5. Commit at.

**Genel kurallar:**

- Paket kökü `com.eceakin.namaztakibi.domain`. Mevcut `Prayer` enum'u (`SABAH`, `OGLE`, `IKINDI`, `AKSAM`, `YATSI`, `VITIR`; `isFarz`) olduğu gibi kalır; **değiştirme.**
- Tanımlayıcılar (sınıf, fonksiyon, test adları) yalnızca ASCII harf kullanır: `ı`, `ş`, `ğ`, `ü`, `ö`, `ç`, `İ` yok. Yorumlar ve KDoc Türkçe olabilir.
- Domain'de `android.*` import'u, `Instant.now()`, `LocalDate.now()`, `ZoneId.systemDefault()` yok (M-02, M-23).
- `!!` yok. Beklenmeyen, tutarsız girdi için `require(...)` (`IllegalArgumentException`) kullanılır (M-24: bu bir iş sonucu değil, programlama hatasıdır).

### A1 — `RuleSet` ve `RuleSet.HANEFI` (M-21)

Dosya: `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/rules/RuleSet.kt`

```kotlin
package com.eceakin.namaztakibi.domain.rules

import com.eceakin.namaztakibi.domain.Prayer
import java.time.Duration

data class RuleSet(
    val version: String,
    val rekatCounts: Map<Prayer, Int>,
    val kerahatAfterGunes: Duration,
    val kerahatBeforeOgle: Duration,
    val kerahatBeforeAksam: Duration,
    val hayizMin: Duration,
    val hayizMax: Duration,
    val nifasMax: Duration,
    val minTuhr: Duration,
    val defaultAdetHabitDays: Int,
    val tertipFallsAtFarzDebt: Int,
    val rekatDuration: Duration,
    val gusulMin: Duration,
    val gusulMax: Duration,
    val gusulDefault: Duration,
    val tekbirDuration: Duration,
    val seferRekatForFourRekatFarz: Int,
    val bulughMinAgeMale: Int,
    val bulughMinAgeFemale: Int,
    val bulughSuggestedAge: Int,
    val bayginlikFallsAtMissedFarz: Int,
) {
    companion object {
        val HANEFI: RuleSet = RuleSet(/* aşağıdaki tablo */)
    }
}
```

Kurucuda varsayılan değer **yok**; her değer `HANEFI` içinde adlandırılmış argümanla yazılır. Testler bir parametreyi değiştirmek için `RuleSet.HANEFI.copy(...)` kullanır (ör. ileride KT-27).

`HANEFI` değerleri. Her alanın KDoc'u kaynağını yazar (K-05). Değerler aynen bunlardır; yorumlama yok:

| Alan | Değer | Kaynak |
| --- | --- | --- |
| `version` | `"hanefi-1"` | M-21, BR-21 (gün anlık kopyası bu kimliği kaydedecek) |
| `rekatCounts` | `SABAH` 2, `OGLE` 4, `IKINDI` 4, `AKSAM` 3, `YATSI` 4, `VITIR` 3 | BR-17 (KT-31: 4+4+3+4+2 = 17; KT-84: yatsı 4 + vitir 3 = 7), BR-18 |
| `kerahatAfterGunes` | 45 dk | BR-07 |
| `kerahatBeforeOgle` | 45 dk | BR-07 |
| `kerahatBeforeAksam` | 45 dk | BR-07 |
| `hayizMin` | 72 saat | BR-13 |
| `hayizMax` | 240 saat | BR-14, BR-16 |
| `nifasMax` | 960 saat | BR-12, BR-15 |
| `minTuhr` | 15 gün (`Duration.ofDays(15)`) | BR-16 |
| `defaultAdetHabitDays` | 10 | BR-14, FR-MAZ-06 (alışkanlık girilmemişse) |
| `tertipFallsAtFarzDebt` | 6 | BR-17 (vitir hariç farz borcu 6 ve üstündeyse tertip düşer) |
| `rekatDuration` | 2 dk | BR-17 |
| `gusulMin` | 5 dk | BR-12, FR-AYR-01 |
| `gusulMax` | 30 dk | BR-12, FR-AYR-01 |
| `gusulDefault` | 15 dk | BR-12 |
| `tekbirDuration` | 1 dk | BR-12 |
| `seferRekatForFourRekatFarz` | 2 | BR-18 |
| `bulughMinAgeMale` | 12 (kamerî yıl) | BR-19 |
| `bulughMinAgeFemale` | 9 (kamerî yıl) | BR-19 |
| `bulughSuggestedAge` | 14 (kamerî yıl, iki cinsiyette) | BR-19 |
| `bayginlikFallsAtMissedFarz` | 6 | BR-22 (6. farz vaktin çıkışıyla namazlar düşer) |

Süreleri kendi birimiyle yaz (`Duration.ofMinutes(45)`, `Duration.ofHours(72)`, `Duration.ofHours(960)`); 960 saati günlere çevirme.

`init` bloğundaki değişmezler (`require`):

- `version` boş değil.
- `rekatCounts`'un anahtarları tam olarak `Prayer.entries` ve her değer > 0.
- Bütün `Duration` alanları pozitif.
- Bütün `Int` alanları > 0.
- `hayizMin < hayizMax`.
- `gusulMin <= gusulDefault <= gusulMax`.

**Testler:** `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/rules/RuleSetTest.kt`. Her BR için ayrı bir test; beklenen değerler literal yazılır (K-06 test istisnası). Önerilen adlar:

- `BR07_kerahatPencereleri45Dakika`
- `BR12_gusul5ile30Arasi_varsayilan15_tekbir1Dakika`
- `BR13_hayizAltSiniri72Saat`
- `BR14_hayizUstSiniri240Saat_aliskanlikGirilmemisse10Gun`
- `BR15_nifasUstSiniri960Saat`
- `BR16_enAzTemizlik15Gun`
- `BR17_tertip6FarzBorctaDuser_rekatSuresi2Dakika`
- `BR17_rekatSayilari` (altı kalemin tamamı)
- `BR18_seferde4RekatliFarz2Rekat`
- `BR19_bulugYaslari12ve9_oneri14`
- `BR22_bayginlikta6FarzdaNamazlarDuser`
- `M21_surumKimligiHanefi1`
- `M21_gecersizRuleSetReddedilir`: `@ParameterizedTest`; her değişmez için `RuleSet.HANEFI.copy(...)` ile bozulmuş bir örnek ve `IllegalArgumentException` beklentisi (eksik `VITIR` anahtarı, sıfır süre, `gusulDefault > gusulMax`, `hayizMin == hayizMax`, boş `version` vb.). Bu satırlar KT değil; `argumentSet` adları `"M-21 ..."` ile başlasın.

Commit: `feat(domain): add Hanefi RuleSet (M-21)`

### A2 — `PrayerTimesDay` (SRS 6.1)

Dosya: `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/times/PrayerTimesDay.kt` (üç tip aynı dosyada ya da aynı pakette ayrı dosyalarda):

```kotlin
package com.eceakin.namaztakibi.domain.times

@JvmInline
value class DistrictId(val value: Int)   // init: value > 0

enum class TimesOrigin { DIYANET, ESTIMATED }   // SRS 6.1: kaynak (Diyanet / tahmini)

data class PrayerTimesDay(
    val district: DistrictId,
    val date: LocalDate,
    val imsak: Instant,
    val gunes: Instant,
    val ogle: Instant,
    val ikindi: Instant,
    val aksam: Instant,
    val yatsi: Instant,
    val origin: TimesOrigin,
)
```

`init` bloğundaki değişmezler:

- Vakitler kesin artan sırada: `imsak < gunes < ogle < ikindi < aksam < yatsi`.
- Her vakit tam dakikada: saniye ve nanosaniye sıfır (karar tablosu "Hassasiyet dakikadır").

Bu sınıf **hiçbir hesap yapmaz.** "Şu anki vakit", "vaktin bitişi", "ertesi imsak", "namaz günü" F2-02'nin işidir; bu kartta bunlara yardımcı fonksiyon ekleme (P-03). `date` ile vakitlerin aynı yerel güne düştüğünü denetleme; bu, saat dilimi gerektirir ve F2-02'de ele alınacak.

**Testler:** `.../domain/times/PrayerTimesDayTest.kt`

- Geçerli bir gün kurulur ve alanlar aynen okunur.
- `M24_siraliOlmayanVakitlerReddedilir`: `@ParameterizedTest`; ardışık her vakit çifti için ikincisinin birincisine eşit olduğu bir örnek (5 `argumentSet`) ve `IllegalArgumentException`.
- `M24_saniyeliVakitReddedilir` ve nanosaniyeli vakit.
- `DistrictId` için sıfır ve negatif değer reddedilir.

Commit: `feat(domain): add PrayerTimesDay model`

### A3 — `ReferenceDay` test fikstürü (K-17)

Dosya: `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/testing/ReferenceDay.kt` (test kaynaklarında; ürün koduna girmez).

```kotlin
object ReferenceDay {
    val ZONE: ZoneId = ZoneId.of("Europe/Istanbul")
    val G1: LocalDate = LocalDate.of(2026, 3, 2)     // Pazartesi; G5 cuma
    val DISTRICT: DistrictId = DistrictId(1)         // testlere özgü uydurma ilçe

    fun date(day: Int): LocalDate                    // date(1) == G1
    fun at(day: Int, time: String): Instant          // at(6, "17:00") = G6 17:00, Europe/Istanbul
    fun clockAt(day: Int, time: String): Clock       // Clock.fixed(at(day, time), ZONE)
    fun times(day: Int): PrayerTimesDay              // fikstür, origin = DIYANET
}
```

- `times(n)` her gün aynı fikstürü verir: imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30 (karar tablosu "Referans gün fikstürü"). "Ertesi imsak" ayrı bir alan değildir; `times(n + 1).imsak` olarak okunur.
- `day < 1` ise `IllegalArgumentException`.
- `time` tam olarak `HH:mm` biçiminde olmalı (iki basamak saat, iki basamak dakika; `00:00` – `23:59`). Başka bir biçim (`"7:00"`, `"17:00:30"`, `"24:00"`) `IllegalArgumentException`.
- Saat dilimi her zaman `ZONE`'dur; sistemin saat dilimi hiçbir yerde kullanılmaz.
- Bu nesne fıkhi hesap içermez; yalnızca tablodaki sabitleri `Instant`'a çevirir.

**Testler:** `.../domain/testing/ReferenceDayTest.kt`. Önerilen adlar:

- `K17_G1Pazartesi_G5Cuma`
- `K17_vakitlerKararTablosuFiksturuyleAyni` (`times(1)`'in altı vakti, `ZONE`'daki yerel saat olarak)
- `K17_G6_1700_UtcOlarak1400` (`at(6, "17:00") == Instant.parse("2026-03-07T14:00:00Z")`)
- `K17_ertesiGunImsaki_G2_0530` (`times(2).imsak == at(2, "05:30")`)
- `K17_G41_tarihi` (`date(41) == LocalDate.of(2026, 4, 11)`; KT-48 ve KT-102 bu güne uzanıyor)
- `K17_clockAt_sabitAnVeIstanbul`
- `K17_gecersizGirdilerReddedilir`: `@ParameterizedTest`; `day = 0`, `day = -1`, `"7:00"`, `"17:00:30"`, `"24:00"`, `"17.00"`.

Commit: `test(domain): add ReferenceDay fixture (K-17)`

## Bölüm B — Kalite kapıları

Bölüm B'de her adımdan sonra `.\gradlew.bat ciCheck` çalıştır; yeşilse commit at.

### B1 — JaCoCo kapsam kapısı (K-15)

1. **Catalog:** `[versions]` altına `jacoco = "0.8.16"` (Adım 0'da doğrulanan sürüm). Kütüphane ya da eklenti girdisi eklenmez; `jacoco` Gradle'ın yerleşik eklentisidir.
2. **`VersionCatalogExtensions.kt`:** Mevcut `library(...)` yardımcısının yanına aynı kalıpta bir `version(alias): String` yardımcısı ekle (sürüm yoksa anlamlı `GradleException`).
3. **`ProjectConfig.kt`:** `const val MIN_LINE_COVERAGE = 0.90` ve üstüne `// NFR-MNT-01, mimari K-15` yorumu. Diğer değerlere dokunma.
4. **`JvmLibraryConventionPlugin.kt`** (yalnızca bu plugin; Android plugin'lerine JaCoCo ekleme):
   - `jacoco` eklentisini uygula; `toolVersion` catalog'dan.
   - `jacocoTestReport`: `test`'e bağlı; XML ve HTML raporu açık.
   - `jacocoTestCoverageVerification`: `test`'e bağlı; tek kural: `LINE` sayacı, `COVEREDRATIO` en az `ProjectConfig.MIN_LINE_COVERAGE`.
   - `check` görevi `jacocoTestCoverageVerification`'a bağlı.
   - **Hiçbir sınıf, paket ya da dosya ölçümden hariç tutulmaz** (`excludes`, `classDirectories` filtresi yok).
   - Başa mimari M-07 ve K-15'e atıf yapan bir yorum.
5. **Kök `build.gradle.kts`:** `ciCheck`, `jacocoTestCoverageVerification` görevi olan her projenin bu görevine bağlanır (mevcut `ktlintCheck` ve `test` bağlama kalıbıyla, `gradle.projectsEvaluated` içinde).
6. **Negatif deneme (commit edilmez):** `core/domain/src/main/kotlin/.../CoverageProbe.kt` adında, test edilmeyen ve kapsamı %90'ın altına düşürecek kadar satırı olan geçici bir sınıf ekle. `.\gradlew.bat :core:domain:jacocoTestCoverageVerification` kapının durduğunu göstersin. Mesajı rapora yaz, dosyayı sil.

Commit: `build: add JaCoCo coverage gate for :core:domain (K-15)`

### B2 — KT izlenebilirlik kapısı (K-16)

Kök `build.gradle.kts`'te, lint kapısının yanında, aynı yapıyla.

1. **Bekleyen KT listesi:** `val ktPending: Set<String>`. Başına mimari K-16'ya atıf yapan ve "listeye ekleme ya da kartın söylemediği bir silme planlama kararıdır" diyen bir yorum yaz. İçerik: karar tablosunun **101 geçerli kimliğinin tamamı** (KT-01 – KT-13 ve KT-15 – KT-102). Kimlikleri yol haritası Faz 2 tablosundaki kartlara göre grupla; her grubun üstünde kartı adlandıran bir yorum satırı olsun (`// F2-03`, `// F2-04` …), grup içinde sayısal sırayla. Böylece her kart hangi satırları sileceğini listede görür.
2. **`checkKtTraceability` görevi:** `group = "verification"`, Türkçe `description`. Şunları yapar:
   - **Tabloyu okur:** `docs/karar-tablosu.md`, **UTF-8** olarak (`readText(Charsets.UTF_8)` ya da eşdeğeri; Windows'un varsayılan kodlamasına güvenme, "Kaldırıldı" kelimesindeki `ı` bozulur). Satır sonlarını kırp (CRLF olabilir).
     - Bir satır, ilk hücresi `KT-<rakamlar>` olan bir Markdown tablo satırıysa KT satırıdır: `^\|\s*KT-(\d+)\s*\|`.
     - Hücrelerinden biri `*Kaldırıldı` ile başlıyorsa satır kaldırılmıştır (bugün yalnızca KT-14). Kalanlar geçerli satırlardır.
     - Bugünkü tabloda bu kural 102 KT satırı, 101 geçerli satır ve 1 kaldırılmış satır (KT-14) verir. Farklı bir sayı görürsen **dur ve sor.**
   - **Testleri tarar:** `core/domain/src/test/kotlin` altındaki bütün `.kt` dosyaları, UTF-8. İki biçim KT kimliği sayılır (mimari K-11):
     - fonksiyon adı: `\bfun\s+KT(\d{2,3})_` → `KT-<rakamlar>` (ör. `KT01_` → `KT-01`, `KT100_` → `KT-100`)
     - `argumentSet` adı: `argumentSet\(\s*"KT-(\d{2,3})(?!\d)`
     Yorumlar, `@DisplayName` ve başka yazımlar sayılmaz.
   - **`@Disabled` arar:** Aynı dosyalarda `@Disabled` ile başlayan her anotasyon (`@Disabled`, `@DisabledIf...`, `@DisabledOn...`) bir farktır; mesaj dosya ve satırı içerir.
   - **Karşılaştırır.** Her fark ayrı bir satır olur; bütün farklar tek mesajda, sıralı listelenir ve `GradleException` fırlatılır. Mesaj kalıpları (planda örnekle):
     - Eksik test: `KT kapısı: KT-35 için test yok ve bekleyen listede de değil. Testini yaz ya da planlamaya sor.`
     - Unutulmuş liste: `KT kapısı: KT-01'in testi var ama bekleyen listede duruyor. Kimliği ktPending'den sil.`
     - Geçersiz kimlik (listede ya da testte): `KT kapısı: KT-14 karar tablosunda geçerli bir satır değil (kaldırılmış ya da yok). Planlamaya bildir.`
     - Okunamayan tablo: `KT kapısı: docs/karar-tablosu.md içinde KT satırı bulunamadı.`
     - Devre dışı test: `KT kapısı: @Disabled bulundu: core/domain/src/test/kotlin/.../X.kt:12. Testler devre dışı bırakılamaz (CLAUDE.md Bölüm 8).`
   - **Başarıda** tek satır yazar: `KT izlenebilirlik: 101 geçerli satır, 0 testli, 101 bekliyor, fark yok.`
3. `ciCheck`, `checkKtTraceability`'ye bağlanır.
4. **Negatif denemeler (commit edilmez):** Her birini tek tek yap, `.\gradlew.bat checkKtTraceability` ile kapının anlamlı mesajla durduğunu gör, değişikliği geri al:
   - Listeden `KT-35`'i sil → eksik test.
   - `RuleSetTest`'e geçici bir `@Test fun KT01_kapiDenemesi() {}` ekle → unutulmuş liste.
   - Listeye `KT-14` ekle → geçersiz kimlik.
   - `RuleSetTest`'teki bir testin üstüne `@Disabled` ekle → devre dışı test.
   - Geçici bir `@ParameterizedTest` ile `argumentSet("KT-02 deneme", ...)` ekle ve listeden `KT-02`'yi silmeden çalıştır → unutulmuş liste (ikinci biçimin de tanındığını gösterir).

   Mesajları rapora yaz. Sonunda `git status` bu kartın commit'leri dışında temiz olmalı (F1-05'in izlenmeyen dosyaları hariç).

Commit: `build: add KT traceability gate (K-16)`

### B3 — README

README'deki derleme ya da kalite bölümüne iki satır ekle: `ciCheck` artık `:core:domain` kapsam kapısını (%90 satır) ve KT izlenebilirlik kapısını da çalıştırır; kapsam raporunun yolu `core/domain/build/reports/jacoco/test/html/index.html`. Rozet ekleme.

Commit: `docs: document quality gates in README`

## Bilinen tuzaklar

1. **Fıkhi sayı yalnızca `RuleSet.HANEFI`'de.** Ürün kodunda başka bir yerde 45, 72, 240 gibi sayılar görünmez. Testlerde beklenen değer olarak literal yazılabilir (K-06).
2. **Karakter kodlaması.** Windows'ta Java'nın varsayılan kodlaması UTF-8 olmayabilir. Kapı, tabloyu ve test dosyalarını açıkça UTF-8 okumazsa `*Kaldırıldı` tanınmaz ve KT-14 geçerli sayılır; kapı yanlış yeşil ya da yanlış kırmızı olur.
3. **CRLF.** Windows'ta çalışma ağacındaki `.md` dosyaları CRLF olabilir. Satırları kırpmadan düzenli ifadeyle eşlersen son hücre `\r` taşır.
4. **İki basamaklı kimlikler.** Tablo `KT-01` yazar; test adı `KT01_` olmalı. `KT1_` yazılırsa kapı onu tanımaz ve "eksik test" der. Bu bilinçli: kimlik tablodakiyle birebir aynı olmalı.
5. **`ZoneId.systemDefault()` ve `LocalDate.now()`.** Testler bilgisayarın saat dilimine ya da tarihine bağlanırsa CI'da (UTC) farklı sonuç verir. Fikstür her zaman `ReferenceDay.ZONE` kullanır.
6. **İstanbul'da yaz saati yok** (UTC+3 sabit). G1 – G41 arasında saat kayması beklenmez; `K17_G6_1700_UtcOlarak1400` bunu sabitler.
7. **JaCoCo ve Kotlin.** `data class` ve `value class` için Kotlin'in ürettiği bazı yöntemler JaCoCo'nun filtreleriyle sayılmaz; yine de `init` bloklarındaki her `require` dalı en az bir testle sınanmalı. Kapsamı yükseltmek için ölçümden hariç tutma **yasak**; eksik test yaz.
8. **`check` ve `ciCheck`.** `ciCheck`, kök görev içinde `dependsOn("jacocoTestCoverageVerification")` yazılarak bağlanmaz; bu yalnızca kökü arar. Mevcut kalıptaki gibi her projenin görevine açıkça bağla ve `.\gradlew.bat ciCheck --dry-run` çıktısında `:core:domain:jacocoTestCoverageVerification` ve `:checkKtTraceability` satırlarını gör.
9. **`argumentSet`.** JUnit'in `org.junit.jupiter.params.provider.Arguments.argumentSet(name, ...)` yöntemidir (JUnit 5.11'den beri; projede JUnit 6.1.3). Kendi yardımcını yazma.
10. **Kapsam dışı dosyalar.** `.github/workflows/ci.yml`, lint kapısının bilinen uyarı listesi, M-08 haritası ve Android convention plugin'leri bu kartta değişmez. CI zaten `ciCheck` çalıştırdığı için iş akışına dokunmaya gerek yok.
11. **F1-05 dosyaları.** Çalışma ağacında F1-05'e ait izlenmeyen dosyalar olabilir. `git add -A` ya da `git add .` kullanırsan bunları yanlış dala commit'lersin. Dosyaları adıyla ekle.

## Dur ve sor

- Çalışma ağacında ön koşullarda sayılanlar dışında bir değişiklik var ya da `main`, `origin/main`'in gerisinde.
- Karar tablosundan 101'den farklı sayıda geçerli satır çıkıyor.
- Karttaki bir `RuleSet` değeri SRS'teki BR satırıyla ya da karar tablosunun "Varsayılan parametreler" bölümüyle çelişiyor gibi görünüyor.
- JaCoCo 0.8.16, Gradle 9.6.1 ya da Kotlin 2.4.21 ile çalışmıyor.
- Kapsam kapısı ancak bir sınıfı ölçümden çıkararak geçiyor.
- Bir kapı için yeni bir kütüphane ya da Gradle eklentisi gerekiyor gibi görünüyor.
- `Prayer` enum'unu değiştirmek gerekiyor gibi görünüyor.
- `CLAUDE.md` Bölüm 12'deki diğer koşullar.

## Kapsam dışı (bu kartta yapma)

- Vakit hesapları, namaz günü, kerahat pencerelerinin hesabı (F2-02).
- `PrayerRecord`, `KazaEntry`, `KazaPerformed`, `MazeretPeriod`, `DaySnapshot`, cinsiyet, ayarlar, öneri ve sonuç tipleri (sonraki kartlar).
- Vakit kaynağı arayüzü (M-05; Faz 3).
- Herhangi bir KT satırının testi. Bu kartın sonunda bekleyen listede 101 kimliğin tamamı durur.
- Sürüm yükseltmesi (Adım 0 yalnızca tablo verir).
- Android modüllerine JaCoCo, Kover ya da başka bir kapsam aracı.
- `.github/workflows/ci.yml`, lint kapısı, M-08 denetimi ve `ProjectConfig`'teki SDK/JVM değerleri.
- `docs/` altındaki belgeleri değiştirmek (README ve kartın plan/rapor dosyaları hariç).

## Kabul kriterleri

**Domain**

- [ ] `RuleSet` karttaki imzayla var; `RuleSet.HANEFI` değerleri karttaki tabloyla birebir aynı; her alanın KDoc'unda kaynağı yazıyor.
- [ ] `RuleSet` ve `PrayerTimesDay` değişmezleri `require` ile denetleniyor ve her değişmez en az bir testle sınanıyor.
- [ ] `PrayerTimesDay`, `DistrictId` ve `TimesOrigin` var; `PrayerTimesDay` hesap yapmıyor.
- [ ] `Prayer` enum'u değişmemiş; mevcut iki BR-01 testi geçiyor.
- [ ] `ReferenceDay` test kaynaklarında; G1 = 2 Mart 2026, bölge Europe/Istanbul; testleri geçiyor.
- [ ] `:core:domain` ürün kodunda `android.`, `Instant.now`, `LocalDate.now`, `LocalDateTime.now`, `ZoneId.systemDefault`, `System.currentTimeMillis` ve `!!` yok (raporda arama komutu ve sonucu).
- [ ] Her A adımında testin önce kırmızı görüldüğü raporda yazıyor.

**Kapılar**

- [ ] `.\gradlew.bat :core:domain:jacocoTestCoverageVerification` yeşil; satır ve dal kapsamı yüzdeleri raporda. Hiçbir sınıf ölçümden hariç değil.
- [ ] Kapsam kapısının negatif denemesi ve mesajı raporda.
- [ ] `ktPending` 101 kimliği kartlara göre gruplanmış hâlde içeriyor; KT-14 yok.
- [ ] `.\gradlew.bat checkKtTraceability` çıktısı: `101 geçerli satır, 0 testli, 101 bekliyor, fark yok.`
- [ ] KT kapısının beş negatif denemesi ve mesajları raporda.
- [ ] `ciCheck --dry-run` çıktısında iki yeni kapı görünüyor.

**Genel**

- [ ] `.\gradlew.bat ciCheck` yerelde yeşil; lint kapısı "bilinen 6 uyarı eşleşti, fark yok".
- [ ] `.\gradlew.bat help` hatasız (M-08 bozulmamış).
- [ ] `.github/workflows/ci.yml` değişmemiş.
- [ ] Plan `docs/tasks/F2-01-plan.md`'de, rapor `docs/tasks/F2-01-rapor.md`'de; rapor `CLAUDE.md` Bölüm 11 biçiminde ve Adım 0'daki sürüm tablosunu içeriyor.
- [ ] Commit'ler `feat/f2-01-domain-foundation` dalında, Conventional Commits biçiminde; `main`'e dokunulmamış; F1-05 dosyaları commit'lenmemiş.

## Bölüm D — Son doğrulama, commit'ler ve rapor

Aşağıdakileri **bu sırayla** çalıştır ve sonuçları rapora özetle:

```
.\gradlew.bat clean
.\gradlew.bat ciCheck
.\gradlew.bat ciCheck --dry-run
.\gradlew.bat :core:domain:jacocoTestReport
.\gradlew.bat checkKtTraceability
.\gradlew.bat help
git status
git log --oneline main..HEAD
```

Ürün kodunda yasak çağrıların olmadığını göstermek için (Git Bash):

```
grep -rnE "android\.|Instant\.now|LocalDate\.now|LocalDateTime\.now|ZoneId\.systemDefault|System\.currentTimeMillis|!!" core/domain/src/main
```

Beklenen: çıktı yok.

Beklenenler:

- `ciCheck` yeşil.
- `:core:domain` testleri: mevcut 2 BR-01 testi + bu kartın testleri; toplam sayı raporda (parametreli testlerin her çalıştırması ayrı sayılır).
- Satır kapsamı %90 veya üstü (beklenen: %100'e yakın).
- KT kapısı: 101 geçerli, 0 testli, 101 bekliyor.
- Lint kapısı: bilinen 6 uyarı eşleşti.

Commit sırası:

1. `docs: add F2-01 card and update planning docs`
2. `docs(tasks): add F2-01 plan`
3. `feat(domain): add Hanefi RuleSet (M-21)`
4. `feat(domain): add PrayerTimesDay model`
5. `test(domain): add ReferenceDay fixture (K-17)`
6. `build: add JaCoCo coverage gate for :core:domain (K-15)`
7. `build: add KT traceability gate (K-16)`
8. `docs: document quality gates in README`
9. `docs(tasks): add F2-01 report`

Commit mesajlarını tek satır ve düz ASCII tırnakla yaz; Windows kabuğunda mesajın bozulmadığını `git log --oneline` ile kontrol et.

Raporu `docs/tasks/F2-01-rapor.md` dosyasına, `CLAUDE.md` Bölüm 11'e göre yaz ve son commit olarak ekle. Ek olarak şunları belirt:

- Adım 0'daki faz başı sürüm tablosu.
- JaCoCo sürümü ve Gradle 9.6.1'in varsayılan sürümü.
- Kapsam yüzdeleri (satır, dal).
- Kapsam kapısının ve KT kapısının negatif deneme mesajları.

**Push etme, PR açma.** Kullanıcı yapacak.

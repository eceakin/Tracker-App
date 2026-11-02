# F2-01 Uygulama Planı — Temel model ve test altyapısı

Dal: `feat/f2-01-domain-foundation` · 2 Kasım 2026 · Onay: Adım 0 planı onaylandı; D1 – D4 düzeltmeleri işlendi.

Bu plan, `docs/tasks/F2-01-temel-model.md` kartının Adım 0 çıktısıdır. Kartın sırası ve kapsamı esastır; planda karttan sapma yoktur. D1 – D4 yalnızca uygulama ayrıntısını netleştirir.

## 1. Git başlangıcı

- `main` = `origin/main` = `01ef5cd1be12eae5a84f22284f8f65d9013a987a`; `HEAD...origin/main` → `0 0`.
- `git fetch origin` yeni bir şey getirmedi; `git stash list` boş.
- Yerel dallar: `main`, `chore/f1-02-modules-hygiene`, `feat/f1-01-skeleton-domain` (eskiler; dokunulmadı).
- Çalışma ağacı grupları:
  - Bu kartın dört dosyası: `CLAUDE.md`, `docs/mimari-ve-kodlama-ilkeleri.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-01-temel-model.md`.
  - F1-05'e ait izlenmeyen dosyalar: `docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md` — dokunulmaz, commit edilmez.
  - Beklenmeyen değişiklik yok.

İlk commit yalnızca dört belgedir; dosyalar adlarıyla eklenir:

```
git add CLAUDE.md docs/mimari-ve-kodlama-ilkeleri.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-01-temel-model.md
git commit -m "docs: add F2-01 card and update planning docs"
```

İkinci commit bu plan dosyasıdır: `docs(tasks): add F2-01 plan`.

## 2. Faz başı sürüm gözden geçirmesi (K-01)

`.\gradlew.bat lintDebug` → **BUILD SUCCESSFUL (2 dk 22 sn)**, 0 hata. 10 modülün XML raporu okundu. Bilinen 6 `ObsoleteLintCustomCheck` uyarısı dışında uyarı yok.

### Lint XML bulguları

| Lint kimliği | Adet |
| --- | --- |
| `GradleDependency` | 0 |
| `NewerVersionAvailable` | 0 |
| `AndroidGradlePluginVersion` | 0 |

### Sürüm tablosu

Hiçbir sürüm değiştirilmez. Bu bir planlama kararıdır: rapora **"sürüm değişikliği yok (planlama kararı, 2 Kasım 2026)"** yazılır.

| Bileşen | Catalog'daki | En yeni kararlı | Denetleyen / kaynak | Not |
| --- | --- | --- | --- | --- |
| AGP | 9.5.0 | 9.5.0 | lint (`AndroidGradlePluginVersion`); Google Maven `maven-metadata.xml` | Güncel; 9.6 yalnızca alpha |
| Kotlin | 2.4.21 | 2.4.21 | resmî kaynak: Maven Central `kotlin-gradle-plugin` metadata | Güncel |
| JUnit (Jupiter/BOM) | 6.1.3 | 6.1.3 | resmî kaynak: Maven Central `junit-bom` metadata | Güncel |
| ktlint Gradle eklentisi | 14.2.0 | 14.2.0 | resmî kaynak: Gradle Plugin Portal metadata | Güncel |
| ktlint motoru | 1.8.0 | 1.8.0 | resmî kaynak: Maven Central `ktlint-cli` metadata | 2.0 yalnızca alpha |
| Gradle | 9.6.1 | 9.8.2 | resmî kaynak: `services.gradle.org/versions/current` | F1-03'te bilinçli seçim; değiştirilmez |
| JaCoCo | Catalog'da yok; hedef 0.8.16 | 0.8.16 | resmî kaynak: `jacoco.org/jacoco/trunk/doc/changes.html` | B1'de catalog'a eklenecek |
| Hilt | 2.60.1 | 2.60.1 | lint (`GradleDependency`/`NewerVersionAvailable`); Maven Central `hilt-android` metadata | Bu çalıştırmada lint bulgusu 0 |
| KSP | 2.3.13 | 2.3.13 | lint (`GradleDependency`/`NewerVersionAvailable`); Maven Central `symbol-processing-gradle-plugin` metadata | Bu çalıştırmada lint bulgusu 0 |
| Compose BOM | 2026.09.00 | 2026.09.00 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven `compose-bom` metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.core:core-ktx` | 1.19.1 | 1.19.1 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven `core-ktx` metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.11.0 | 2.11.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.activity:activity-compose` | 1.13.0 | 1.13.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |
| JUnit 4 | 4.13.2 | 4.13.2 | lint (`GradleDependency`/`NewerVersionAvailable`); Maven Central `junit` metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.test.ext:junit` | 1.3.0 | 1.3.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.test.espresso:espresso-core` | 3.7.0 | 3.7.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |

Doğrulanamayan satır yok.

## 3. JaCoCo

- **0.8.16 doğrulandı:** JaCoCo değişiklik geçmişinde “Release 0.8.16 (2026/10/24)”; Java 27 resmî, Java 28 deneysel destek.
- **Gradle 9.6.1 varsayılanı 0.8.14:** Gradle kaynağındaki `JacocoPlugin.DEFAULT_JACOCO_VERSION = "0.8.14"` ve yerel Gradle 9.6.1 dağıtımındaki `JacocoPlugin.class` bunu doğruluyor. Kullanıcı kılavuzundaki örnek de `toolVersion = "0.8.14"` kullanıyor.
- `toolVersion` ile 0.8.16'ya geçmek desteklenen bir yapılandırmadır. Kılavuz 0.8.16'yı açıkça anmıyor; kesin doğrulama B1'deki ilk derlemeye kalır. 0.8.16 Java 27'yi desteklediğine ve proje Java 17'de çalıştığına göre uyumsuzluk beklenmiyor.
- Uygulama:
  - `gradle/libs.versions.toml`: yalnızca `jacoco = "0.8.16"`; kütüphane/eklenti girdisi yok.
  - `VersionCatalogExtensions.kt`: mevcut `library(...)` yanına `version(alias): String`; yoksa anlamlı `GradleException`.
  - `ProjectConfig.kt`: `const val MIN_LINE_COVERAGE = 0.90` ve `// NFR-MNT-01, mimari K-15`.
  - `JvmLibraryConventionPlugin.kt`: `jacoco` eklentisi; `toolVersion` catalog'dan; `jacocoTestReport` (`test`e bağlı, XML + HTML); `jacocoTestCoverageVerification` (`test`e bağlı; tek kural `LINE`/`COVEREDRATIO` ≥ 0.90); `check` → doğrulama. Hiçbir sınıf/paket/dosya ölçümden hariç tutulmaz.
  - Kök `build.gradle.kts`: `ciCheck`, `jacocoTestCoverageVerification` görevi olan her projenin görevine bağlanır (mevcut `gradle.projectsEvaluated` kalıbı).

## 4. Dosya ağacı ve imzalar

Karttaki imzalardan **sapma yok**. `DistrictId` ve `TimesOrigin`, kartın izin verdiği gibi `PrayerTimesDay.kt` içinde birlikte durur.

**Üretim kodu**

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/rules/RuleSet.kt`
  - `data class RuleSet(...)` — karttaki 21 alan aynen; kurucuda varsayılan değer yok.
  - `init` → `require` değişmezleri: boş olmayan `version`; `rekatCounts.keys == Prayer.entries.toSet()` ve her değer > 0; tüm `Duration` > 0; tüm `Int` > 0; `hayizMin < hayizMax`; `gusulMin <= gusulDefault <= gusulMax`.
  - `companion object { val HANEFI: RuleSet }` — tüm değerler adlandırılmış argümanla; her alanın KDoc'unda kaynağı (BR/M kimliği).
- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/times/PrayerTimesDay.kt`
  - `@JvmInline value class DistrictId(val value: Int)` — `init`: `value > 0`.
  - `enum class TimesOrigin { DIYANET, ESTIMATED }`.
  - `data class PrayerTimesDay(...)` — karttaki alanlar aynen.
  - `init`: `imsak < gunes < ogle < ikindi < aksam < yatsi`; her `Instant` tam dakikada (`nano == 0` ve `floorMod(epochSecond, 60) == 0`).
  - Hesap/yardımcı fonksiyon yok (P-03).

**Test kodu**

- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/rules/RuleSetTest.kt`
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/times/PrayerTimesDayTest.kt`
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/testing/ReferenceDay.kt`
  - `ZONE`, `G1`, `DISTRICT`; `date(day)`, `at(day, time)`, `clockAt(day, time)`, `times(day)` — karttaki imzalar.
  - `day < 1` → `IllegalArgumentException`; `time` yalnızca `HH:mm` (`00:00`–`23:59`); her zaman `Europe/Istanbul`.
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/testing/ReferenceDayTest.kt`

## 5. Test listesi

Her A adımında önce test yazılır, `:core:domain:test` çalıştırılıp kırmızı/derlenmeyen olduğu görülür, sonra kod yazılır (K-11). Parametreli testlerde bozuk nesne **`Executable` içinde** kurulur; aksi hâlde `copy(...)`/kurucu argument source aşamasında fırlatır ve `assertThrows` testi yanlış yerde yakalar.

**RuleSetTest — 12 normal + 1 parametreli (10 argüman) = 22 çalıştırma**

| Test | Sınadığı |
| --- | --- |
| `BR07_kerahatPencereleri45Dakika` | Üç kerahat penceresi de 45 dk |
| `BR12_gusul5ile30Arasi_varsayilan15_tekbir1Dakika` | 5 / 30 / 15 dk, tekbir 1 dk |
| `BR13_hayizAltSiniri72Saat` | 72 saat |
| `BR14_hayizUstSiniri240Saat_aliskanlikGirilmemisse10Gun` | 240 saat, varsayılan alışkanlık 10 gün |
| `BR15_nifasUstSiniri960Saat` | 960 saat |
| `BR16_enAzTemizlik15Gun` | 15 gün |
| `BR17_tertip6FarzBorctaDuser_rekatSuresi2Dakika` | 6 farz, 2 dk |
| `BR17_rekatSayilari` | Altı kalemin tamamı |
| `BR18_seferde4RekatliFarz2Rekat` | 2 rekât |
| `BR19_bulugYaslari12ve9_oneri14` | 12 / 9 / 14 |
| `BR22_bayginlikta6FarzdaNamazlarDuser` | 6 |
| `M21_surumKimligiHanefi1` | `"hanefi-1"` |
| `M21_gecersizRuleSetReddedilir` | Her değişmez için bir bozuk örnek |

`M21_gecersizRuleSetReddedilir` argümanları: boş `version`; eksik `VITIR`; sıfır rekât; sıfır süre; negatif süre; sıfır `Int`; negatif `Int`; `hayizMin == hayizMax`; `gusulDefault > gusulMax`; `gusulMin > gusulDefault`. Adlar `"M-21 ..."` ile başlar.

**PrayerTimesDayTest — 1 normal + 1 parametreli (5) + 2 + 1 parametreli (2) = 10 çalıştırma**

| Test | Sınadığı |
| --- | --- |
| `M24_gecerliGunKurulurVeAlanlarOkunur` | Geçerli gün; alanlar aynen okunur |
| `M24_siraliOlmayanVakitlerReddedilir` | Ardışık 5 çiftin eşit olduğu 5 örnek |
| `M24_saniyeliVakitReddedilir` | Saniye sıfır değil |
| `M24_nanosaniyeliVakitReddedilir` | Nanosaniye sıfır değil |
| `M24_gecersizDistrictReddedilir` | `DistrictId(0)`, `DistrictId(-1)` |

**ReferenceDayTest — 6 normal + 1 parametreli (6) = 12 çalıştırma**

| Test | Sınadığı |
| --- | --- |
| `K17_G1Pazartesi_G5Cuma` | G1 Pazartesi, G5 Cuma |
| `K17_vakitlerKararTablosuFiksturuyleAyni` | Altı vakit ve `origin = DIYANET` |
| `K17_G6_1700_UtcOlarak1400` | `2026-03-07T14:00:00Z` |
| `K17_ertesiGunImsaki_G2_0530` | `times(2).imsak == at(2, "05:30")` |
| `K17_G41_tarihi` | `2026-04-11` |
| `K17_clockAt_sabitAnVeIstanbul` | Sabit an + `Europe/Istanbul` |
| `K17_gecersizGirdilerReddedilir` | `day = 0`, `day = -1`, `"7:00"`, `"17:00:30"`, `"24:00"`, `"17.00"` |

Toplam: **44 yeni test çalıştırması**; mevcut 2 BR-01 testiyle `:core:domain` 46 çalıştırma.

## 6. KT izlenebilirlik kapısı tasarımı

### Okuma

- `docs/karar-tablosu.md` ve `core/domain/src/test/kotlin` altındaki tüm `.kt` dosyaları **açıkça UTF-8** (`readText(Charsets.UTF_8)`) okunur.
- Satırlar `lineSequence()` ile alınır; her satırın sonundaki `\r` kırpılır (CRLF).
- Bugünkü tablo: **102 KT satırı, 101 geçerli, 1 kaldırılmış (KT-14)**. Uygulama sırasında bu sayılar tutmazsa durulur. Kapıya sabit `101` gömülmez; yarın tabloya satır eklenirse kapı doğal olarak kırmızıya döner.

### Düzenli ifadeler (D2)

```kotlin
val ktRow         = Regex("""^\|\s*KT-(\d+)\s*\|""")
val ktFunction    = Regex("""\bfun\s+KT(\d{2,3})_""")
val ktArgumentSet = Regex("""argumentSet\(\s*"KT-(\d{2,3})(?!\d)""")
```

**Normalizasyon yoktur (D2).** Yakalanan rakamlar yazıldığı gibi kullanılır: kimlik = `"KT-" + rakamlar`. Sayıya çevirme ve sıfırla doldurma yapılmaz. Böylece:

- `KT01_` → `KT-01`
- `KT100_` → `KT-100`
- `KT001_` → `KT-001` (tabloda `KT-01` var; bu kimlik geçersiz sayılır ve kapı kırmızıya döner)

### Kaldırılmış satır

Bir satır, ilk hücresi `KT-<rakamlar>` olan Markdown tablo satırıysa KT satırıdır. Hücrelere ayrıldığında (`split("|")`, `trim`) herhangi bir hücre `*Kaldırıldı` ile başlıyorsa satır kaldırılmıştır. Bugün yalnızca KT-14 böyledir.

### `@Disabled` araması (D3)

```kotlin
val disabled = Regex("""@(?:org\.junit\.jupiter\.api\.)?Disabled\w*""")
```

- Satır başı şartı yoktur; bir satırın **herhangi bir yerinde** bu ifade geçiyorsa farktır.
- Örnekler: `@Test @Disabled fun ...`, `@Disabled @Test fun ...`, `@org.junit.jupiter.api.Disabled`.
- Aynı dosyada birden çok eşleşme varsa hepsi `dosya:satır` olarak listelenir.

### Bekleyen liste

Kök `build.gradle.kts` içinde `val ktPending: Set<String>`; başında K-16'ya atıf yapan ve "listeye ekleme ya da kartın söylemediği bir silme planlama kararıdır" diyen yorum. 101 kimlik, yol haritasındaki kartlara göre gruplu ve grup içinde sayısal sıralı:

| Grup | Kimlikler | Adet |
| --- | --- | --- |
| F2-03 | KT-35, KT-36, KT-81 | 3 |
| F2-04 | KT-01–13, KT-15–18, KT-34 | 18 |
| F2-05 | KT-19–27, KT-32, KT-33, KT-91 | 12 |
| F2-06 | KT-28–31, KT-82–84, KT-92, KT-93 | 9 |
| F2-07 | KT-37–51, KT-90 | 16 |
| F2-08 | KT-52–61, KT-80, KT-87–89, KT-94 | 15 |
| F2-09 | KT-62, KT-63, KT-95–98, KT-101, KT-102 | 8 |
| F2-10 | KT-64–68 | 5 |
| F2-11 | KT-69, KT-70 | 2 |
| F2-12 | KT-78, KT-79, KT-85, KT-86, KT-100 | 5 |
| F2-13 | KT-71–77, KT-99 | 8 |
| **Toplam** | | **101** |

### Karşılaştırma ve mesajlar

Tüm farklar sıralı tek `GradleException` içinde listelenir:

- Eksik test: `KT kapısı: KT-35 için test yok ve bekleyen listede de değil. Testini yaz ya da planlamaya sor.`
- Unutulmuş liste: `KT kapısı: KT-01'in testi var ama bekleyen listede duruyor. Kimliği ktPending'den sil.`
- Geçersiz kimlik: `KT kapısı: KT-14 karar tablosunda geçerli bir satır değil (kaldırılmış ya da yok). Planlamaya bildir.`
- Okunamayan tablo: `KT kapısı: docs/karar-tablosu.md içinde KT satırı bulunamadı.`
- Devre dışı test: `KT kapısı: @Disabled bulundu: core/domain/src/test/kotlin/.../X.kt:12. Testler devre dışı bırakılamaz (CLAUDE.md Bölüm 8).`
- Başarı: `KT izlenebilirlik: 101 geçerli satır, 0 testli, 101 bekliyor, fark yok.`

`checkKtTraceability` → `group = "verification"`, Türkçe `description`; `ciCheck` ona bağlanır.

## 7. Negatif denemeler ve geri alma (D1)

**Sıra (D1):** Negatif denemeler, ilgili kapı commit'i atıldıktan **sonra** yapılır:

- B1'in `CoverageProbe.kt` denemesi, `build: add JaCoCo coverage gate for :core:domain (K-15)` commit'inden sonra.
- B2'nin beş denemesi, `build: add KT traceability gate (K-16)` commit'inden sonra.
- Karttaki B1/5 ve B2/3 **bağlama** adımları commit'ten önce kalır; yalnızca negatif denemeler commit'ten sonraya taşınır.

**Başlamadan önce:** `git status` yalnızca F1-05'in iki izlenmeyen dosyasını göstermelidir.

**Geri alma kuralları:**

- Yalnızca o denemede değiştirilen dosyaya `git restore <dosya yolu>`.
- Geçici olarak oluşturulan dosyayı (ör. `CoverageProbe.kt`) sil.
- Her geri almadan sonra `git diff` boş olmalı.
- `git restore .`, `git checkout -- .`, `git reset` ve `git stash` **kullanılmaz**.

**B1 negatif denemesi:** `core/domain/src/main/kotlin/.../CoverageProbe.kt` eklenir; `.\gradlew.bat :core:domain:jacocoTestCoverageVerification` kırmızıya döner; mesaj rapora yazılır; dosya silinir.

**B2 negatif denemeleri (tek tek, her biri geri alınır):**

1. `ktPending`'den `KT-35` silinir → eksik test.
2. `RuleSetTest`'e geçici `@Test fun KT01_kapiDenemesi() {}` eklenir → unutulmuş liste.
3. `ktPending`'e `KT-14` eklenir → geçersiz kimlik.
4. Bir testin üstüne `@Test @Disabled fun ...` **aynı satırda** yazılır → devre dışı test.
5. Geçici `@ParameterizedTest` + `Arguments.argumentSet("KT-02 deneme", ...)` eklenir; `KT-02` listeden silinmez → unutulmuş liste (ikinci biçimin de tanındığı görülür).

Her denemenin mesajı rapora yazılır. Sonunda `git status` bu kartın commit'leri dışında temiz olmalı (F1-05 hariç).

## 8. Commit sırası

Dal: güncel `main`'den `feat/f2-01-domain-foundation`. İlk commit yalnızca kartın dört belgesi; `git add` dosya adlarıyla yapılır, F1-05 dosyaları asla eklenmez.

1. `docs: add F2-01 card and update planning docs`
2. `docs(tasks): add F2-01 plan`
3. `feat(domain): add Hanefi RuleSet (M-21)`
4. `feat(domain): add PrayerTimesDay model`
5. `test(domain): add ReferenceDay fixture (K-17)`
6. `build: add JaCoCo coverage gate for :core:domain (K-15)`
7. `build: add KT traceability gate (K-16)`
8. `docs: document quality gates in README`
9. `docs(tasks): add F2-01 report`

Bölüm A'da her adımdan sonra `:core:domain:test` + `ktlintCheck`; Bölüm B'de her adımdan sonra `ciCheck` yeşil olacak. Son doğrulamada `clean`, `ciCheck`, `ciCheck --dry-run`, `jacocoTestReport`, `checkKtTraceability`, `help`, `git status`, `git log --oneline main..HEAD` ve yasak çağrı `grep` çalıştırılacak.

**Push ve PR yok.** Birleştirmeyi kullanıcı yapacak.

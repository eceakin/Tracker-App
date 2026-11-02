## Rapor: F2-01 — Temel model ve test altyapısı

Dal: `feat/f2-01-domain-foundation`

### Yapılanlar

- **A1 — `RuleSet` ve `RuleSet.HANEFI` (M-21):** Karttaki 21 alan aynı imzayla; tüm değerler `HANEFI` içinde adlandırılmış argümanla. Her alanın KDoc'unda kaynağı (BR/M kimliği) yazıyor. `init` değişmezleri: boş olmayan sürüm, tam `Prayer.entries` anahtarları ve pozitif rekâtlar, pozitif süreler/sayılar, `hayizMin < hayizMax`, `gusulMin <= gusulDefault <= gusulMax`.
- **A2 — `PrayerTimesDay` (SRS 6.1):** `DistrictId` (`value class`, pozitif), `TimesOrigin` (`DIYANET`, `ESTIMATED`) ve altı `Instant` alanlı `PrayerTimesDay`. Değişmezler: kesin artan vakitler ve tam dakika (saniye/nanosaniye sıfır). Sınıf hesap yapmıyor; `date` ile vakitlerin aynı yerel güne düştüğü denetlenmiyor (F2-02).
- **A3 — `ReferenceDay` fikstürü (K-17):** Test kaynaklarında; G1 = 2 Mart 2026 Pazartesi, G5 cuma, Europe/Istanbul. `date`, `at`, `clockAt`, `times`; `day < 1` ve `HH:mm` dışı biçimler `IllegalArgumentException`.
- **B1 — JaCoCo kapsam kapısı (K-15):** Catalog'a `jacoco = "0.8.16"`; `VersionCatalogExtensions.version(alias)`; `ProjectConfig.MIN_LINE_COVERAGE = 0.90`; `JvmLibraryConventionPlugin` içinde `jacoco`, `jacocoTestReport` (XML + HTML), `jacocoTestCoverageVerification` (LINE/COVEREDRATIO ≥ 0.90), `check` bağlantısı. `ciCheck` her projenin doğrulama görevine açıkça bağlı. Hiçbir sınıf/paket ölçümden hariç tutulmadı.
- **B2 — KT izlenebilirlik kapısı (K-16):** Kök `build.gradle.kts`'te `ktPending` (101 kimlik, kartlara göre gruplu) ve `checkKtTraceability`. Tablo ve testler UTF-8 okunuyor, CRLF kırpılıyor. Fonksiyon adı ve `argumentSet` olmak üzere iki KT biçimi tanınıyor; `@Disabled` satırın herhangi bir yerinde aranıyor. D2 gereği kimlikler yakalandığı gibi kullanılıyor (sayıya çevirme/sıfırla doldurma yok).
- **B3 — README:** `ciCheck`'in kapsam ve KT kapılarını çalıştırdığı ve kapsam raporunun yolu eklendi. Rozet eklenmedi.

### Değişen dosyalar

- `gradle/libs.versions.toml`
- `build-logic/convention/src/main/kotlin/JvmLibraryConventionPlugin.kt`
- `build-logic/convention/src/main/kotlin/ProjectConfig.kt`
- `build-logic/convention/src/main/kotlin/VersionCatalogExtensions.kt`
- `build.gradle.kts`
- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/rules/RuleSet.kt` (yeni)
- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/times/PrayerTimesDay.kt` (yeni)
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/rules/RuleSetTest.kt` (yeni)
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/testing/ReferenceDay.kt` (yeni)
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/testing/ReferenceDayTest.kt` (yeni)
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/times/PrayerTimesDayTest.kt` (yeni)
- `README.md`
- `docs/tasks/F2-01-plan.md` (yeni), `docs/tasks/F2-01-rapor.md` (yeni)
- İlk commit'te: `CLAUDE.md`, `docs/mimari-ve-kodlama-ilkeleri.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-01-temel-model.md`

`.github/workflows/ci.yml` değişmedi. F1-05 dosyaları commit edilmedi.

### Testler

- `.\gradlew.bat :core:domain:test` → **46 test, 0 hata**:
  - Mevcut `PrayerTest` (2)
  - `RuleSetTest` (22)
  - `PrayerTimesDayTest` (10)
  - `ReferenceDayTest` (12)
- Her A adımında önce test yazıldı ve kırmızı görüldü:
  - A1: `Unresolved reference 'RuleSet'` (derlenmedi).
  - A2: `Unresolved reference 'DistrictId'` (derlenmedi).
  - A3: `Unresolved reference 'ReferenceDay'` (derlenmedi).
- `.\gradlew.bat clean` → başarılı.
- `.\gradlew.bat ciCheck` → **başarılı (2 dk 36 sn)**.
- `.\gradlew.bat ciCheck --dry-run` → `:checkKtTraceability` ve `:core:domain:jacocoTestCoverageVerification` görünüyor.
- `.\gradlew.bat :core:domain:jacocoTestReport` → başarılı.
- `.\gradlew.bat checkKtTraceability` → `KT izlenebilirlik: 101 geçerli satır, 0 testli, 101 bekliyor, fark yok.`
- `.\gradlew.bat help` → başarılı (M-08 bozulmadı).
- Lint kapısı: `Lint kapısı: bilinen 6 uyarı eşleşti, fark yok.`
- Yasak çağrı taraması (`android.`, `Instant.now`, `LocalDate.now`, `LocalDateTime.now`, `ZoneId.systemDefault`, `System.currentTimeMillis`, `!!`) → **çıktı yok**.

### Kapsam (JaCoCo)

| Sayaç | Kapsanan | Kaçan | Oran |
| --- | --- | --- | --- |
| Satır (LINE) | 117 | 0 | **%100** |
| Dal (BRANCH) | 60 | 0 | **%100** |
| Talimat (INSTRUCTION) | 828 | 0 | %100 |
| Yöntem (METHOD) | 39 | 0 | %100 |
| Sınıf (CLASS) | 6 | 0 | %100 |

Hiçbir sınıf, paket ya da dosya ölçümden hariç tutulmadı.

### Kapı negatif denemeleri

**Kapsam kapısı (B1, commit sonrası):** Geçici `CoverageProbe.kt` eklendi; satır oranı 0.5'e düştü ve görev şu mesajla durdu:

```
Rule violated for bundle domain: lines covered ratio is 0.5, but expected minimum is 0.9
```

Dosya silindi; `git diff` boş.

**KT kapısı (B2, commit sonrası), beş deneme:**

1. `ktPending`'den `KT-35` silindi:
   `KT kapısı: KT-35 için test yok ve bekleyen listede de değil. Testini yaz ya da planlamaya sor.`
2. `RuleSetTest`'e geçici `@Test fun KT01_kapiDenemesi() {}` eklendi:
   `KT kapısı: KT-01'in testi var ama bekleyen listede duruyor. Kimliği ktPending'den sil.`
3. `ktPending`'e `KT-14` eklendi:
   `KT kapısı: KT-14 karar tablosunda geçerli bir satır değil (kaldırılmış ya da yok). Planlamaya bildir.`
4. Aynı satırda `@Test @Disabled fun ...` yazıldı:
   `KT kapısı: @Disabled bulundu: core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/rules/RuleSetTest.kt:96. Testler devre dışı bırakılamaz (CLAUDE.md Bölüm 8).`
5. Geçici `@ParameterizedTest` + `Arguments.argumentSet("KT-02 deneme", ...)` eklendi; `KT-02` listeden silinmedi:
   `KT kapısı: KT-02'in testi var ama bekleyen listede duruyor. Kimliği ktPending'den sil.`

Her denemeden sonra yalnızca değiştirilen dosya `git restore <dosya>` ile geri alındı; geçici dosya silindi. Her geri almadan sonra `git diff` boştu. `git restore .`, `git checkout -- .`, `git reset` ve `git stash` kullanılmadı. Son `git status` yalnızca F1-05'in iki izlenmeyen dosyasını gösteriyor.

### Adım 0 sürüm tablosu

Sürüm değişikliği yok (planlama kararı, 2 Kasım 2026).

| Bileşen | Catalog'daki | En yeni kararlı | Denetleyen / kaynak | Not |
| --- | --- | --- | --- | --- |
| AGP | 9.5.0 | 9.5.0 | lint (`AndroidGradlePluginVersion`); Google Maven `maven-metadata.xml` | Güncel; 9.6 yalnızca alpha |
| Kotlin | 2.4.21 | 2.4.21 | resmî kaynak: Maven Central `kotlin-gradle-plugin` metadata | Güncel |
| JUnit (Jupiter/BOM) | 6.1.3 | 6.1.3 | resmî kaynak: Maven Central `junit-bom` metadata | Güncel |
| ktlint Gradle eklentisi | 14.2.0 | 14.2.0 | resmî kaynak: Gradle Plugin Portal metadata | Güncel |
| ktlint motoru | 1.8.0 | 1.8.0 | resmî kaynak: Maven Central `ktlint-cli` metadata | 2.0 yalnızca alpha |
| Gradle | 9.6.1 | 9.8.2 | resmî kaynak: `services.gradle.org/versions/current` | F1-03'te bilinçli seçim; değiştirilmedi |
| JaCoCo | Catalog'a eklendi: 0.8.16 | 0.8.16 | resmî kaynak: `jacoco.org/jacoco/trunk/doc/changes.html` | 24 Ekim 2026; Java 27 resmî |
| Hilt | 2.60.1 | 2.60.1 | lint (`GradleDependency`/`NewerVersionAvailable`); Maven Central `hilt-android` metadata | Bu çalıştırmada lint bulgusu 0 |
| KSP | 2.3.13 | 2.3.13 | lint (`GradleDependency`/`NewerVersionAvailable`); Maven Central `symbol-processing-gradle-plugin` metadata | Bu çalıştırmada lint bulgusu 0 |
| Compose BOM | 2026.09.00 | 2026.09.00 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven `compose-bom` metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.core:core-ktx` | 1.19.1 | 1.19.1 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven `core-ktx` metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.11.0 | 2.11.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.activity:activity-compose` | 1.13.0 | 1.13.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |
| JUnit 4 | 4.13.2 | 4.13.2 | lint (`GradleDependency`/`NewerVersionAvailable`); Maven Central `junit` metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.test.ext:junit` | 1.3.0 | 1.3.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |
| `androidx.test.espresso:espresso-core` | 3.7.0 | 3.7.0 | lint (`GradleDependency`/`NewerVersionAvailable`); Google Maven metadata | Bu çalıştırmada lint bulgusu 0 |

Lint XML raporlarında `GradleDependency`, `NewerVersionAvailable` ve `AndroidGradlePluginVersion` bulguları **0**. Doğrulanamayan satır yok.

### JaCoCo sürümü

- Hedef ve catalog değeri: **0.8.16** (24 Ekim 2026; Java 27 resmî, Java 28 deneysel destek).
- Gradle 9.6.1'in varsayılan JaCoCo sürümü: **0.8.14** (`JacocoPlugin.DEFAULT_JACOCO_VERSION`; yerel Gradle dağıtımındaki sınıf ve kılavuz örneği de 0.8.14).
- `toolVersion = "0.8.16"` ile geçiş yapıldı; ilk derlemede doğrulandı ve kapsam kapısı çalıştı.

### Karşılanan kimlikler

BR-07, BR-12, BR-13, BR-14, BR-15, BR-16, BR-17, BR-18, BR-19, BR-22; M-21, M-24, M-25 (RuleSet sözleşmesi), K-05, K-06, K-11, K-15, K-16, K-17, NFR-MNT-01, SRS 6.1. Bu kartta hiçbir KT satırının testi yazılmadı; `ktPending` 101 kimlikle dolu.

### Varsayımlar

- `DistrictId` ve `TimesOrigin`, kartın izin verdiği gibi `PrayerTimesDay.kt` içinde birlikte tutuldu.
- Parametreli testlerde bozuk `RuleSet`/`PrayerTimesDay`/`ReferenceDay` girdileri `Executable` içinde kuruldu; böylece `IllegalArgumentException` argument source aşamasında değil, `assertThrows` içinde yakalandı.
- KT kapısında `@Disabled` bir satırda birden çok geçse bile o satır için tek `dosya:satır` farkı üretildi; farklı satırların tamamı listelendi.
- Test dosyaları `sortedBy { it.path }` ile gezildi; mesajlarda yol ayırıcısı `/` olarak normalize edildi.
- Sürüm güncelliği denetimleri bu çalıştırmada 0 bulgu verdi; tablodaki kütüphaneler ayrıca resmî Maven/Google Maven metadata'sından doğrulandı.
- D1 gereği negatif denemeler ilgili commit'lerden sonra yapıldı; her biri yalnızca değiştirilen dosyada `git restore` ile geri alındı.

### Belge tutarsızlıkları

- Kart ile SRS/karar tablosu arasında çelişki bulunmadı. Karar tablosu 102 KT satırı, 101 geçerli satır ve 1 kaldırılmış satır (KT-14) verdi; karttaki sayıyla aynı.
- Gradle 9.6.1 kılavuzu JaCoCo 0.8.16'yı açıkça anmıyor; `toolVersion` yapılandırılabilir olduğu için ilk derlemede doğrulandı (kartın öngördüğü gibi).

### Açık sorular

Yok.

### Commit'ler

1. `docs: add F2-01 card and update planning docs`
2. `docs(tasks): add F2-01 plan`
3. `feat(domain): add Hanefi RuleSet (M-21)`
4. `feat(domain): add PrayerTimesDay model`
5. `test(domain): add ReferenceDay fixture (K-17)`
6. `build: add JaCoCo coverage gate for :core:domain (K-15)`
7. `build: add KT traceability gate (K-16)`
8. `docs: document quality gates in README`
9. `docs(tasks): add F2-01 report`

Push edilmedi, PR açılmadı.

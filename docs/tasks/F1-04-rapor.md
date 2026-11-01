## Rapor: F1-04 — Hilt, CI, lint kapısı ve `main` koruması

Dal: `feat/f1-04-hilt-ci`

### Yapılanlar

- **A1 — Catalog:** `hilt = 2.60.1`, `ksp = 2.3.13`; `hilt-android`, `hilt-compiler`, `hilt-gradlePlugin`, `ksp-gradlePlugin` kütüphaneleri; `hilt`, `ksp`, `namaztakibi-android-hilt` eklentileri. Kök `build.gradle.kts`'e `apply false` satırları eklendi.
- **A2 — Convention plugin:** `AndroidHiltConventionPlugin` (`namaztakibi.android.hilt`) KSP ve Hilt eklentilerini uygular, `implementation(hilt-android)` + `ksp(hilt-compiler)` ekler. Android plugin'i uygulanmamış modülde anlamlı `GradleException` verir; `:core:domain`'e geçici olarak uygulanarak doğrulandı, değişiklik geri alındı.
- **A3 — `:app`:** `@HiltAndroidApp` `NamazTakibiApplication`, manifest'te `android:name`, `MainActivity`'de `@AndroidEntryPoint`. Başka enjeksiyon eklenmedi.
- **B1 — Lint kapısı:** XML raporu convention plugin'lerde `lint { xmlReport = true }` ile açıkça açıldı. Kökte `k01KnownLintWarnings` (6 kayıt) ve K-01 1.5'e göre ayrı `k01OutOfScopeLintIds` kümesi (`GradleDependency`, `NewerVersionAvailable`, `AndroidGradlePluginVersion`) tanımlandı. `checkLintWarnings` iki yönlü karşılaştırır; rapor yoksa da durur.
- **B2 — `ciCheck`:** Kökte `verification` grubunda; bütün projelerin `ktlintCheck` görevlerine (kök, 11 alt proje, `build-logic`), bütün alt projelerin `test` görevlerine, `checkLintWarnings`'e ve `:app:assembleDebug`'a **açıkça** bağlandı. `--dry-run` çıktısında 14 `ktlintCheck` ve 11 `test` görevi doğrulandı.
- **C1:** `gradlew` modu `100644` → `100755`; `.gitattributes` eklendi (`gradlew`, `*.sh` LF; `*.bat` CRLF; `*.jar` binary).
- **C2:** `.github/workflows/ci.yml`; `ubuntu-24.04`, Temurin JDK 21, `gradle/actions/setup-gradle`, `sdkmanager --install "platforms;android-37.1"`, `./gradlew ciCheck --stacktrace`, yalnızca başarısızlıkta `**/build/reports/**` (7 gün). YAML `npx --yes js-yaml` ile doğrulandı.
- **C3:** README'ye `ciCheck` komutu, CI'ın her PR'da çalıştırdığı ve `main`'in ruleset ile korunduğu eklendi.

### Değişen dosyalar

- `gradle/libs.versions.toml`, `build.gradle.kts`
- `build-logic/convention/build.gradle.kts`
- `build-logic/convention/src/main/kotlin/AndroidHiltConventionPlugin.kt` (yeni)
- `build-logic/convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt`
- `build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt`
- `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/eceakin/namaztakibi/NamazTakibiApplication.kt` (yeni)
- `app/src/main/java/com/eceakin/namaztakibi/MainActivity.kt`
- `.gitattributes` (yeni), `.github/workflows/ci.yml` (yeni)
- `gradlew` (mod değişikliği), `README.md`
- `docs/tasks/F1-04-plan.md`, `docs/tasks/F1-04-rapor.md`

### Testler

- `.\gradlew.bat clean` → başarılı.
- `.\gradlew.bat ciCheck` → **başarılı** (1 dk 57 sn). `:core:domain` **2 test**, `:app` **1 test**, 0 hata.
- `.\gradlew.bat help` → başarılı; M-08 denetimi hata vermedi.
- `git ls-files -s gradlew` → `100755`.
- Lint kapısı son çalıştırma: `Lint kapısı: bilinen 6 uyarı eşleşti, fark yok.` Kapı dışı kimlikler bu temiz çalıştırmada raporlara düşmedi (0).

### Lint kapısının negatif denemeleri

1. `strings.xml`'e kullanılmayan string:
   `Lint kapısı: yeni uyarı UnusedResources (:app) app\src\main\res\values\strings.xml:3. Uyarıyı düzelt; bilinçli bir istisnaysa mimari K-01 tablosuna eklenmesi için planlamaya sor.`
2. Bilinen listeden `:feature:kaza` silindi:
   `Lint kapısı: yeni uyarı ObsoleteLintCustomCheck (:feature:kaza) C:\Users\eceak\.gradle\caches\9.6.1\transforms\...\lifecycle-runtime-release\jars\lint.jar. ...`
3. Bilinen listeye `RedundantLabel, :app` eklendi:
   `Lint kapısı: bilinen uyarı RedundantLabel (:app) artık görünmüyor. Mimari K-01 tablosundan ve bu listeden çıkarılması için planlamaya bildir.`

Üç denemeden sonra değişiklikler geri alındı; çalışma ağacı temizdi.

### Sürüm tablosu ve action SHA'ları

| Bileşen | Sürüm / SHA | Kaynak |
| --- | --- | --- |
| Dagger / Hilt | 2.60.1 | Maven Central metadata; GitHub `dagger-2.60.1` |
| KSP | 2.3.13 | Maven Central metadata; GitHub release `2.3.13`; `kotlin-stdlib:2.4.0` |
| `actions/checkout` | v7.0.1 — `3d3c42e5aac5ba805825da76410c181273ba90b1` | `git ls-remote --tags` |
| `actions/setup-java` | v6.1.0 — `b843ebf9b913146b38da2eaa7aeddf76074ae1ad` | `git ls-remote --tags` |
| `gradle/actions/setup-gradle` | v6.4.1 — `8da46b539cd8fe6ab27ca75128919a18d757604c` | `git ls-remote --tags` (annotated etiketin soyulmuş commit SHA'sı) |
| `actions/upload-artifact` | v7.0.2 — `cf430e030ddbb5b0abf93d22962f4752f3646cd9` | `git ls-remote --tags` |

### Üretilen Hilt sınıfı

- `app/build/generated/hilt/component_sources/debug/com/eceakin/namaztakibi/Hilt_NamazTakibiApplication.java`
- KSP çıktıları: `app/build/generated/ksp/debug/java/...` (`NamazTakibiApplication_GeneratedInjector`, `Hilt_MainActivity`, `hilt_aggregated_deps`).

### Karşılanan kimlikler

M-07 (Hilt yapılandırması convention plugin'de), M-08 (denetim bozulmadı), K-01 (ktlint temiz; lint kapısı bilinen liste + kapı dışı kimlikler), P-03 (yalnızca onaylı kütüphaneler), P-05/K-06 (sürümler catalog'da).

### Varsayımlar

- Action major sürümleri kullanıcı onayıyla en yeni kararlı sürümlerdir.
- `ciCheck` ktlint bağımlılığı, onaydaki düzeltme doğrultusunda her projenin görevine açıkça bağlandı.
- CI'da yalnızca `platforms;android-37.1` kurulur; AGP 9.5'in kullandığı `build-tools 36.0.0` runner'da yoksa lisanslar kabul edilmiş olduğundan AGP tarafından indirilir.
- `gradlew` izni `git update-index --chmod=+x` ile verildi.

### Belge tutarsızlıkları

- **Kart A3:** Üretilen `Hilt_NamazTakibiApplication` sınıfının `app/build/generated/ksp/` altında olması bekleniyordu; Hilt 2.60.1 + AGP 9.5 ile sınıf `app/build/generated/hilt/component_sources/debug/...` altında üretiliyor. KSP çıktıları ayrıca `generated/ksp/` altında. Sınıf oluşuyor; yalnızca yol farklı.
- **Adım 0 / K-01:** Kart 7 uyarı bekliyordu; temiz çalıştırmada 6 uyarı (yalnızca `ObsoleteLintCustomCheck`) ölçüldü. Kullanıcı kararıyla K-01 1.5'e sürüm güncelliği denetimleri kapı dışına alındı ve bilinen liste 6 kayda indi.
- Not: kotlinlang.org KSP uyumluluk sayfası 2.3.10'u "en yeni" gösteriyor; Maven Central ve GitHub 2.3.11–2.3.13'ü listeliyor. Kaynak olarak Maven Central kullanıldı.

### Açık sorular

Yok. KSP 2.3.13 ↔ Kotlin 2.4.21 ve Hilt 2.60.1 ↔ AGP 9.5 uyumluluğu gerçek derlemelerle doğrulandı.

### Adım E — Kullanıcının GitHub'da yapacakları (özet)

1. Dalı push et, `main` hedefli PR aç; **Checks** bölümünde `ciCheck` yeşil olmalı. Kırmızıysa merge etme, kaydı planlamaya getir.
2. Uygulamayı Android Studio'da bir kez çalıştır; "Hello Android!" görünmeli (Hilt uygulama açılışına sınıf ekledi).
3. `main` için ruleset: ad `main-koruma`, Active, default branch; restrict deletions, block force pushes, require PR (0 onay, yalnızca Squash), require status checks → `ciCheck`; bypass listesi boş.
4. Settings → General → Pull Requests: yalnızca **Allow squash merging** ve **Automatically delete head branches** açık.
5. PR'ı "Squash and merge" ile birleştir.

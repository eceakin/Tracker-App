## Rapor: F1-02 — Depo hijyeni, convention plugin'ler ve modül iskeleti

Dal: `chore/f1-02-modules-hygiene`

### Yapılanlar

- `.idea/` izlemeden çıkarıldı (`git rm -r --cached`), kök `.gitignore`'a `/.idea/` eklendi; eski `.idea/*` satırları silindi.
- `rootProject.name = "NamazTakibi"`, `app_name = "Namaz Takibi"`. `applicationId` ve `namespace` değiştirilmedi.
- ktlint kök `.kts` dosyalarını da denetleyecek şekilde `allprojects`'e alındı.
- Kapsamdaki kütüphaneler güncellendi (tablo aşağıda).
- Şablon lint uyarıları düzeltildi: activity'deki gereksiz `android:label` kaldırıldı, kullanılmayan `colors.xml` silindi. Hiçbir uyarı gizlenmedi.
- `build-logic/` kuruldu: dört convention plugin ve `ProjectConfig.kt`. `ktlintCheck` included build'i de denetliyor.
- `:app` ve `:core:domain` convention plugin'lere taşındı.
- Dokuz boş modül eklendi; `:app` hepsine bağlandı.
- M-08 bağımlılık denetimi kök `build.gradle.kts`'e yazıldı; üç negatif deneme ve "tabloda olmayan modül" denemesi doğrulandı.
- Türkçe `README.md` eklendi.

### Değişen dosyalar

- `.gitignore`
- `settings.gradle.kts`, `build.gradle.kts`
- `gradle/libs.versions.toml`
- `README.md`
- `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/src/main/res/values/{strings.xml, colors.xml (silindi)}`
- `core/domain/build.gradle.kts`
- `build-logic/settings.gradle.kts`, `build-logic/convention/build.gradle.kts`
- `build-logic/convention/src/main/kotlin/{ProjectConfig, JvmLibraryConventionPlugin, AndroidLibraryConventionPlugin, AndroidFeatureConventionPlugin, AndroidApplicationConventionPlugin}.kt`
- `core/data/build.gradle.kts`, `core/prayertimes/build.gradle.kts`
- `feature/{onboarding,today,calendar,kaza,mazeret,settings}/build.gradle.kts`
- `widget/build.gradle.kts`
- `.idea/` altındaki 12 dosya (izleme dışı bırakıldı)
- `docs/tasks/F1-02-rapor.md` (bu dosya)

### Sürüm tablosu

| Kütüphane | Mevcut | Hedef | Kaynak | Uyumluluk notu |
| --- | --- | --- | --- | --- |
| `androidx.core:core-ktx` | 1.10.1 | 1.18.0 | Google Maven group-index; AndroidX Core sürüm notları | 1.18.0 `compileSdk`'ı 36.1'e çıkarır (bizde 36.1). 1.19.x AAR metadatası `minCompileSdk=37` istediği için alınamadı. |
| `androidx.lifecycle:lifecycle-runtime-ktx` | 2.6.1 | 2.10.0 | AndroidX Lifecycle sürüm notları; Maven POM | 2.11.0 "Compose `compileSdk` 37, minimum AGP 9.2.0" istiyor. 2.10.0 minSdk 23. |
| `androidx.activity:activity-compose` | 1.8.0 | 1.13.0 | AndroidX Activity sürüm notları; Maven POM | 11 Mart 2026; compileSdk 37 yalnızca 1.14.0-alpha'da. |
| `androidx.compose:compose-bom` | 2024.09.00 | 2026.06.01 | Compose BOM eşleme sayfası; AndroidX Compose Runtime notları | 2026.06.01 → compose 1.11.4, material3 1.4.0. 2026.08/09 BOM'ları 1.12.x ve `compileSdk` 37 + AGP 9.2.0 istiyor. |
| `androidx.test.ext:junit` | 1.1.5 | 1.3.0 | AndroidX Test sürüm notları | minSdk 21; ek kısıt yok. |
| `androidx.test.espresso:espresso-core` | 3.5.1 | 3.7.0 | AndroidX Test sürüm notları | minSdk 21; ek kısıt yok. |
| `junit:junit` (JUnit 4) | 4.13.2 | 4.13.2 (değişmedi) | Maven Central | JUnit 4'ün son sürümü. |

Değişmeyenler: AGP 9.1.1, Gradle 9.3.1, Kotlin 2.2.10, ktlint 14.2.0, JUnit 5 5.14.4, compileSdk 36.1, targetSdk 36, minSdk 26, Java 11, foojay-resolver-convention 1.0.0.

### Lint uyarıları

- Başlangıç: **0 hata, 18 uyarı**.
- Bitiş: **0 hata, 9 uyarı**. Hepsi `:app` raporunda; dokuz modülde "No issues found" (modül bazında tekrar yok). Hiçbiri gizlenmedi; `lint.xml`, baseline, `@SuppressLint`, `tools:ignore` kullanılmadı.

(a) Sabit araç sürümlerinden kaynaklananlar (düzeltilmedi, karar planlamanın):

- `AndroidGradlePluginVersion` ×3 — `com.android.application`, `com.android.library`, `com.android.tools.build:gradle`; üçü de `agp = "9.1.1"` satırını işaret ediyor (9.5.0 mevcut).
- `NewerVersionAvailable` ×2 — `org.jetbrains.kotlin.jvm` ve `org.jetbrains.kotlin.plugin.compose` 2.2.10 → 2.4.21.
- `NewerVersionAvailable` ×1 — `org.junit:junit-bom` 5.14.4 → 6.1.3 (JUnit 5 kalıyor).

(b) Araç zinciri yüzünden geri tutulan kütüphaneler (A5/2 gereği):

- `GradleDependency` — `core-ktx` 1.18.0 → 1.19.1 (1.19.x `compileSdk` 37 ister).
- `GradleDependency` — `lifecycle-runtime-ktx` 2.10.0 → 2.11.0 (AGP 9.2.0 ister).
- `GradleDependency` — `compose-bom` 2026.06.01 → 2026.09.00 (Compose 1.12.x `compileSdk` 37 ister).

Not: Catalog'a eklenen `android-library` plugin alias'ı ve `android-gradlePlugin` kütüphanesi, aynı `agp` satırı için `AndroidGradlePluginVersion` uyarısını üç kez tetikliyor (önce 1, şimdi 3). Uyarı sayısındaki 7 → 9 farkı buradan gelir; gizlenmedi.

### Negatif denemeler (M-08)

Konsol çıktısında Windows kod sayfası nedeniyle `→`, `İ`, `ı` karakterleri `?` görünür; denetimin ürettiği metin kaynaktaki gibidir:

1. `:feature:today` → `:core:data`
   `M-08 ihlali: :feature:today → :core:data (yapılandırma: implementation). İzin verilenler: [:core:domain]`
2. `:feature:today` → `:feature:kaza`
   `M-08 ihlali: :feature:today → :feature:kaza (yapılandırma: implementation). İzin verilenler: [:core:domain]`
3. `:core:domain` → `:core:data`
   `M-08 ihlali: :core:domain → :core:data (yapılandırma: implementation). İzin verilenler: []`
4. (Ek) Tabloda olmayan geçici modül `:core:tester`
   `M-08 ihlali: :core:tester modülü M-08 izin tablosunda tanımlı değil. Modülü kök build.gradle.kts'teki tabloya ekle.`

Dört deneme de `.\gradlew.bat help` ile yapıldı, yapılandırma aşamasında durdu; değişiklikler geri alındı, çalışma ağacı temiz.

### Son doğrulama (`clean` sonrası)

- `projects`: 11 proje — `:app`, `:core:data`, `:core:domain`, `:core:prayertimes`, altı `:feature:*`, `:widget` (ayrıca `:core` ve `:feature` gruplama projeleri, build dosyası yok).
- `ktlintCheck`: başarılı, `:build-logic:convention:ktlintCheck` dahil.
- `test`: başarılı. `:core:domain` **2 test** (0 hata), `:app` **1 test** (`ExampleUnitTest`), dokuz yeni modülde kaynak olmadığı için test yok.
- `assembleDebug`: başarılı (tüm modüller).
- `lintDebug`: 0 hata; modül bazında uyarılar yukarıdaki gibi.
- `:core:domain:dependencies --configuration runtimeClasspath`: yalnızca `kotlin-stdlib:2.2.10` ve `org.jetbrains:annotations:13.0`; Android/AndroidX yok.

### Karşılanan kimlikler

M-02 (domain saf JVM), M-04 (feature'lar veriye bağlanmıyor), M-07 (convention plugin'ler), M-08 (derleme zamanı denetim), M-09 (modül adları ve namespace), K-01 (ktlint, kök ve build-logic dahil), K-06 (adlandırılmış sabitler `ProjectConfig`'te). NFR-MNT-01 kapsamı bu kartta ölçülmedi (domain kodu değişmedi).

### Varsayımlar

- Belgeler zaten `main`'deki `ed8aca6` commit'inde olduğu için ayrı bir belge commit'i atılmadı (kullanıcı onayı).
- `build-logic` kodu Gradle'ın varsayılan JVM hedefiyle (17) derleniyor; bu hedef Android modüllerinin bayt kodunu etkilemiyor, modüller `ProjectConfig` üzerinden Java 11'de kalıyor (kullanıcı onayı).
- M-08 denetimi, build betiği olmayan gruplama projelerini (`:core`, `:feature`) ve AGP'nin androidTest yapılandırmalarına eklediği modülün kendisine bağımlılığı atlar; aksi halde meşru derlemeler dururdu.
- `namaztakibi.android.feature`, `:core:domain` bağımlılığını `add("implementation", project(...))` ile ekler.

### Belge tutarsızlıkları

- Kartın A6 hedefi "0 uyarı", ancak A5/2 en yeni sürüm uyumsuzsa eskisini seçmeyi emrediyor; bu durumda `GradleDependency` uyarıları kaçınılmaz. Kullanıcı bu yorumu onayladı, uyarılar (a)/(b) olarak raporlandı.
- Kartın Adım D sırasındaki 1. commit (`docs: add F1-02 card...`) `main`'de zaten mevcut olduğu için bu dalda yer almıyor.

### Plandan sapmalar

- A1/2 belge commit'i atlandı (belgeler main'de).
- Lint uyarı sayısı planda 7 bekleniyordu, 9 çıktı: catalog'a eklenen `android-library` plugin alias'ı ve `android-gradlePlugin` kütüphanesi aynı `agp` satırı için `AndroidGradlePluginVersion` uyarısını üç kez tetikliyor.
- `ktlintFormat`, A4'te kök dosyalarda düzeltme yapmadı; M-08 bloğu yazılırken ktlint kurallarına göre elle biçimlendirildi.

### Açık sorular

- Yok. AGP/Kotlin yükseltme kararı geldiğinde (b) grubundaki üç `GradleDependency` uyarısı kendiliğinden kapanacaktır.

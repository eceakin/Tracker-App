# Görev Kartı F1-02 — Depo hijyeni, convention plugin'ler ve modül iskeleti

Faz 1 · Kart 2/4 · 1 Kasım 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

F1-01'den sonra proje tek bir uygulama modülü ve saf Kotlin bir `:core:domain` modülünden oluşuyor. Bu kartın sonunda şunlar olacak:

- temiz bir depo: `.idea/` izlenmiyor, proje adında boşluk yok, ktlint her Kotlin dosyasını görüyor,
- güncel kütüphaneler ve sıfır Android Lint uyarısı,
- ortak Gradle ayarlarını tek yerde tutan `build-logic` convention plugin'leri (mimari M-07),
- mimarideki bütün modüller (boş olarak),
- modül bağımlılık kurallarını derleme sırasında denetleyen bir koruma (M-08),
- bir README.

Kart iki bölümden oluşur. **Bölüm A** (hijyen ve sürümler) küçük bir projede yapılır, çünkü sürüm yükseltmek ve lint uyarılarını temizlemek tek modülde ucuzdur. **Bölüm B** (convention plugin'ler ve modüller) ancak Bölüm A'nın ara doğrulaması yeşilse başlar.

## Ön koşullar

- `main`, F1-01'in birleştirildiği `34ffe4a` commit'inde ya da daha ilerde.
- Kullanıcı şu dosyaları çalışma ağacına koymuş olmalı:
  - `CLAUDE.md` (sürüm 1.2)
  - `docs/mimari-ve-kodlama-ilkeleri.md` (sürüm 1.2)
  - `docs/gelistirme-yol-haritasi.md` (1 Kasım 2026 güncellemesi)
  - `docs/hocaya-sorular.md` (yeni)
  - `docs/5te5-yorum-analizi.md` (yeni)
  - `docs/tasks/F1-02-moduller-ve-hijyen.md` (bu kart)
- Çalışma ağacında bu dosyalar dışında yalnızca `.idea/` altında değişiklik olabilir. Başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler

- `CLAUDE.md` (tamamı; özellikle yeni Bölüm 0, Bölüm 5, 6 ve 11)
- `docs/mimari-ve-kodlama-ilkeleri.md`: Bölüm 1 (M-01 – M-09), Bölüm 5 (P-02 – P-04), Bölüm 7 (K-01)
- `docs/gelistirme-yol-haritasi.md`: yalnızca Faz 1

## Adım 0 — Plan ve onay

Bu adımda yalnızca dosya okuyabilir ve şu iki komutu çalıştırabilirsin: `.\gradlew.bat lintDebug` ve `.\gradlew.bat :app:dependencies`. Hiçbir dosyayı değiştirme.

Aşağıdaki altı başlıkla bir plan yaz, sonra **kullanıcının onayını bekle:**

1. **Lint uyarıları:** Mevcut uyarıların tam listesi (lint kimliği, dosya, kısa açıklama). Her biri için ne yapacağın, ya da neden düzeltmeyip raporlayacağın (bkz. A6).
2. **Sürüm tablosu:** Her kütüphane için şu sütunlar: kütüphane, mevcut sürüm, hedef sürüm, kaynak (Google Maven ya da sürüm notları bağlantısı), uyumluluk notu. Kurallar için bkz. A5.
3. **`build-logic` tasarımı:** Dosya ağacı, dört plugin kimliği, SDK ve JVM sabitlerinin tutulacağı yer, convention kodundan version catalog'a nasıl erişeceğin ve ktlint'in `build-logic`'i nasıl kapsayacağı.
4. **Modüller:** Yeni modüllerin yolu, uygulanacak convention plugin ve `namespace` değeri (bkz. B3).
5. **Bağımlılık denetimi:** Denetimin hangi dosyada duracağı, hangi Gradle API'lerini kullanacağı ve ihlal mesajının örnek metni (bkz. B5).
6. **Commit sırası** (bkz. Adım D).

## Bölüm A — Depo hijyeni ve sürümler

### A1 — Dal ve belgeler

1. `main`'den `chore/f1-02-modules-hygiene` dalını aç. Bu kartın tüm işi bu dalda yapılır.
2. Kullanıcının koyduğu altı belgeyi dalın ilk commit'i olarak ekle: `docs: add F1-02 card and update planning docs`. Belgelerin içeriğini değiştirme.

### A2 — `.idea/` depodan çıkar

1. `git rm -r --cached .idea` ile `.idea/` klasörünü izlemeden çıkar. Yerel dosyalar silinmez; Android Studio onları kullanmaya devam eder.
2. Kök `.gitignore`'a `/.idea/` satırını ekle. Artık gereksiz kalan `/.idea/caches`, `/.idea/libraries` gibi satırları sil.
3. Gerekçe (rapora da yaz): Kod stili `.editorconfig` ve ktlint ile tanımlı. IDE dosyaları her oturumda gürültü üretiyor ve proje Gradle'dan yeniden oluşturulabiliyor.

### A3 — Proje ve uygulama adı

1. `settings.gradle.kts` içinde `rootProject.name = "NamazTakibi"` yap. Boşluklu proje adı bazı araçlarda sorun çıkarır.
2. `app/src/main/res/values/strings.xml` içinde `app_name` değerini `Namaz Takibi` yap. Bu, uygulamanın çalışma adıdır.
3. `applicationId` ve `namespace` **değişmez.**

### A4 — ktlint kapsamı

1. Kök `build.gradle.kts`'te ktlint eklentisini `subprojects` yerine `allprojects` ile uygula. Böylece kökteki `build.gradle.kts` ve `settings.gradle.kts` de kontrol edilir.
2. `ktlintFormat` çalıştır. Kök dosyalarda yaptığı düzeltmeleri (ör. eksik dosya sonu satırı) not al.
3. `build-logic` kapsamı B1'de eklenecek.

### A5 — Kütüphane sürümleri

**Kapsam:** Version catalog'daki şu kütüphaneler: `coreKtx`, `lifecycleRuntimeKtx`, `activityCompose`, `composeBom`, `junit` (JUnit 4, yalnızca uygulamadaki şablon testi için), `junitVersion` (`androidx.test.ext:junit`) ve `espressoCore`.

**Değişmeyecekler:**

- AGP `9.1.1`, Gradle `9.3.1`, Kotlin `2.2.10`
- ktlint eklentisi `14.2.0`
- JUnit 5 (`5.14.x` kalır, JUnit 6'ya geçilmez)
- `compileSdk 36` (minor `1`), `targetSdk 36`, `minSdk 26`, Java 11
- `settings.gradle.kts`'teki `foojay-resolver-convention` sürümü. `settings` dosyasındaki `plugins` bloğu catalog'a erişemediği için bu, "sürüm catalog'da" kuralının kabul edilmiş tek istisnasıdır.

**Hedef sürümü seçme kuralları:**

1. Hedef, en yeni **kararlı** sürümdür. Alpha, beta ya da RC olmaz.
2. Hedef sürüm `compileSdk 36.1`, AGP `9.1.1` ve Kotlin `2.2.10` ile uyumlu olmalı. Bir kütüphanenin en yeni sürümü daha yüksek bir `compileSdk`, AGP ya da Kotlin istiyorsa, bu koşulu sağlayan en yeni sürümü seç ve tabloda belirt. Bu durum genellikle derlemede "AAR metadata" hatası olarak görünür.
3. Sürümü hafızandan yazma. Google Maven'dan (`maven.google.com`) ya da AndroidX sürüm notlarından doğrula ve kaynağı tabloya yaz.

**Uygulama:** Sürümleri catalog'da güncelle. Ardından `assembleDebug` ve `test` çalıştır. Bunlar yeşilse tek bir commit at: `build(deps): update AndroidX and Compose libraries`.

### A6 — Android Lint uyarıları

**Hedef:** `.\gradlew.bat lintDebug` sonunda **0 hata, 0 uyarı.**

Kurallar:

1. Lint'in "kullanılmıyor" dediği şablon kaynaklarını (ör. kullanılmayan renkler) sil. Yalnızca lint'in işaret ettiklerini sil.
2. Gereksiz tanımları (ör. uygulama etiketiyle aynı olan activity `android:label`) kaldır.
3. Sürüm kaynaklı uyarılar A5 ile çözülmüş olmalı.
4. **Yasak:** `lint.xml`, lint baseline dosyası, `@SuppressLint`, `tools:ignore` ve `lint { disable(...) }`. Uyarıyı gizlemek düzeltmek değildir.
5. **İstisna:** Kalan uyarılar yalnızca A5'te "değişmeyecek" olarak listelenen araç ya da SDK sürümleriyle ilgiliyse (ör. `OldTargetApi`, AGP ya da Kotlin sürüm uyarısı) onları **düzeltme ve gizleme.** Kimlikleriyle birlikte rapora yaz; kararı planlama verecek.
6. Şablondaki `Greeting` composable'ına, tema dosyalarına ve örnek testlere lint gerektirmedikçe dokunma.

Commit: `fix(lint): resolve template lint warnings`

### A7 — Ara doğrulama (kapı)

Aşağıdakileri çalıştır:

```
.\gradlew.bat clean
.\gradlew.bat ktlintCheck
.\gradlew.bat test
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
```

`:core:domain`'de 2 test geçmiş olmalı. Herhangi bir komut başarısızsa **Bölüm B'ye geçme;** dur ve raporla.

## Bölüm B — Convention plugin'ler ve modüller

### B1 — `build-logic` iskeleti

Hedef dosya ağacı:

```
build-logic/
  settings.gradle.kts
  convention/
    build.gradle.kts
    src/main/kotlin/
      ProjectConfig.kt
      JvmLibraryConventionPlugin.kt
      AndroidLibraryConventionPlugin.kt
      AndroidFeatureConventionPlugin.kt
      AndroidApplicationConventionPlugin.kt
```

1. **`build-logic/settings.gradle.kts`:**
   - `rootProject.name = "build-logic"` ve `include(":convention")`.
   - Ana projenin catalog'unu `versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }` ile paylaş.
   - Repository'ler: `google()`, `mavenCentral()`, `gradlePluginPortal()`.
2. **`build-logic/convention/build.gradle.kts`:**
   - `` `kotlin-dsl` `` eklentisini uygula.
   - AGP, Kotlin Gradle eklentisi ve Compose compiler Gradle eklentisini **`compileOnly`** olarak ekle. Sürümler catalog'dan gelir; catalog'a `libraries` olarak ekle (ör. `android-gradlePlugin`, `kotlin-gradlePlugin`, `compose-gradlePlugin`). `implementation` kullanırsan ana projedeki eklenti sürümleriyle çakışma riski doğar.
   - Dört plugin'i `gradlePlugin { plugins { register(...) } }` ile kaydet:
     - `namaztakibi.jvm.library`
     - `namaztakibi.android.library`
     - `namaztakibi.android.feature`
     - `namaztakibi.android.application`
   - JVM hedefi ana projeyle uyumlu olmalı. Planda belirt.
3. **Kök `settings.gradle.kts`:** `pluginManagement { includeBuild("build-logic") }`.
4. **Catalog `[plugins]`:** Dört plugin'i **sürümsüz** ekle, ör. `namaztakibi-android-library = { id = "namaztakibi.android.library" }`. Modüller plugin'leri `alias(libs.plugins.namaztakibi.android.library)` ile uygular.
5. **`ProjectConfig.kt`:** SDK ve JVM değerlerinin **tek** kaynağıdır. Adlandırılmış sabitler kullan (K-06): compileSdk `36` ve minor `1`, minSdk `26`, targetSdk `36`, Java `11`. Değerler bugünküyle birebir aynı kalır.
6. **Plugin içerikleri:**

   | Plugin | Yaptığı |
   | --- | --- |
   | `namaztakibi.jvm.library` | `org.jetbrains.kotlin.jvm` uygular. Java ve Kotlin hedefini `ProjectConfig`'ten ayarlar. JUnit 5'i BOM ile ekler (`junit-jupiter`, `junit-platform-launcher` runtimeOnly) ve `useJUnitPlatform()` çağırır |
   | `namaztakibi.android.library` | `com.android.library` uygular. `compileSdk`, `minSdk` ve `compileOptions`'ı `ProjectConfig`'ten ayarlar |
   | `namaztakibi.android.feature` | `namaztakibi.android.library` ve `org.jetbrains.kotlin.plugin.compose` uygular. `buildFeatures.compose = true` ayarlar. `implementation(project(":core:domain"))`, Compose BOM, `ui`, `ui-tooling-preview`, `material3` ve `debugImplementation(ui-tooling)` ekler |
   | `namaztakibi.android.application` | `com.android.application` uygular. `compileSdk`, `minSdk`, `targetSdk` ve `compileOptions`'ı `ProjectConfig`'ten ayarlar. `applicationId`, `versionCode`, `versionName`, `buildTypes` ve Compose ayarları `app/build.gradle.kts`'te kalır |

7. **ktlint:** `build-logic/convention` için de ktlint eklentisini uygula. Kökteki tek `.\gradlew.bat ktlintCheck` komutu `build-logic`'i de kontrol etmeli. Included build'lerin görevleri kendiliğinden çalışmaz; kökteki `ktlintCheck`'i `gradle.includedBuild("build-logic").task(":convention:ktlintCheck")` görevine bağlamak bir yoldur. Planda seçtiğin yolu yaz.

Commit: `build: add build-logic convention plugins`

### B2 — Mevcut modülleri taşı

1. `:core:domain`, `namaztakibi.jvm.library` kullanır. Build dosyasında yalnızca plugin satırı kalır; Java/Kotlin hedefi ve JUnit bağımlılıkları dosyadan silinir.
2. `:app`, `namaztakibi.android.application` kullanır. Build dosyasından `compileSdk`, `minSdk`, `targetSdk` ve `compileOptions` silinir; uygulamaya özgü ayarlar kalır.
3. **Davranış değişmemeli:** aynı SDK değerleri, Java 11, `:core:domain`'de 2 test ve uygulamadaki şablon testi çalışıyor, `assembleDebug` başarılı.

Commit: `build: migrate app and core:domain to convention plugins`

### B3 — Yeni modüller

| Yol | Gradle yolu | Convention plugin | `namespace` | Proje bağımlılıkları |
| --- | --- | --- | --- | --- |
| `core/data` | `:core:data` | `namaztakibi.android.library` | `com.eceakin.namaztakibi.core.data` | `:core:domain` |
| `core/prayertimes` | `:core:prayertimes` | `namaztakibi.android.library` | `com.eceakin.namaztakibi.core.prayertimes` | `:core:domain` |
| `feature/onboarding` | `:feature:onboarding` | `namaztakibi.android.feature` | `com.eceakin.namaztakibi.feature.onboarding` | (convention'dan) `:core:domain` |
| `feature/today` | `:feature:today` | `namaztakibi.android.feature` | `com.eceakin.namaztakibi.feature.today` | (convention'dan) `:core:domain` |
| `feature/calendar` | `:feature:calendar` | `namaztakibi.android.feature` | `com.eceakin.namaztakibi.feature.calendar` | (convention'dan) `:core:domain` |
| `feature/kaza` | `:feature:kaza` | `namaztakibi.android.feature` | `com.eceakin.namaztakibi.feature.kaza` | (convention'dan) `:core:domain` |
| `feature/mazeret` | `:feature:mazeret` | `namaztakibi.android.feature` | `com.eceakin.namaztakibi.feature.mazeret` | (convention'dan) `:core:domain` |
| `feature/settings` | `:feature:settings` | `namaztakibi.android.feature` | `com.eceakin.namaztakibi.feature.settings` | (convention'dan) `:core:domain` |
| `widget` | `:widget` | `namaztakibi.android.library` | `com.eceakin.namaztakibi.widget` | `:core:domain`, `:core:data` |

Kurallar:

- Her modülde **yalnızca** `build.gradle.kts` olur: plugin satırı, `android { namespace = ... }` ve tabloda varsa proje bağımlılıkları. Kaynak dosya, yer tutucu sınıf ya da `AndroidManifest.xml` ekleme; AGP kütüphane modüllerinde manifest istemez.
- Proje bağımlılıkları `implementation(project(...))` ile eklenir. **`api(project(...))` kullanma.**
- `settings.gradle.kts`'e dokuz modülü `include` et.

Commit: `build: add empty core, feature and widget modules`

### B4 — `:app` bağlantısı

`:app`, yeni dokuz modülün hepsine `implementation(project(...))` ile bağlanır; alfabetik sırayla yaz. Amaç, bütün modül grafiğinin derlendiğini kanıtlamak. Hilt ve navigasyon F1-03 ve sonrasındadır.

Bu adımı B3'le aynı commit'e koyabilirsin.

### B5 — Bağımlılık denetimi (M-08)

1. Kök `build.gradle.kts`'te, M-08'deki izin tablosunu **tek bir harita** olarak tutan bir denetim yaz. `:feature:*` için tek bir desen kuralı yeterli. Kural tablosunu, mimari belgedeki M-08'e atıf yapan bir yorumla başlat.
2. Denetim her alt projenin **bütün** yapılandırmalarındaki (test yapılandırmaları dahil) `ProjectDependency`'leri kontrol eder.
3. İhlal, yapılandırma aşamasında `GradleException` ile derlemeyi durdurur. Mesaj kuralı, iki modülü ve izin verilenleri adlandırır. Örnek: `M-08 ihlali: :feature:today → :core:data. İzin verilenler: [:core:domain]`.
4. Tabloda karşılığı olmayan bir modül de derlemeyi durdurur. Mesaj: modülün M-08 tablosuna eklenmesi gerektiği. Böylece yeni bir modül, kuralı yazılmadan eklenemez.
5. Gradle 9'da `ProjectDependency.dependencyProject` kaldırıldı; `ProjectDependency.path` kullan.
6. **Negatif denemeler (commit edilmez):** Aşağıdaki üç ihlali **tek tek** geçici olarak ekle, `.\gradlew.bat help` ile derlemenin anlamlı mesajla durduğunu gör, değişikliği geri al:
   - `:feature:today` → `implementation(project(":core:data"))`
   - `:feature:today` → `implementation(project(":feature:kaza"))`
   - `:core:domain` → `implementation(project(":core:data"))`

   Her denemenin hata mesajını rapora yaz. Sonunda `git status` temiz olmalı.

Commit: `build: enforce module dependency rules (M-08)`

### B6 — README

Kök dizine Türkçe, kısa bir `README.md` yaz. İçeriği:

1. **Uygulama:** Bir paragraf: ne olduğu ve kapsamı (Android, Hanefî, Diyanet vakitleri, çevrimdışı öncelikli, reklamsız, hesapsız). Kaynak: `CLAUDE.md` Bölüm 1. Yeni bir iddia ekleme.
2. **Durum:** Geliştirme aşamasında (Faz 1); yayınlanmadı.
3. **Modüller:** `CLAUDE.md` Bölüm 6'daki tablonun kısa hali ve bağımlılık kuralının derlemede denetlendiği bilgisi.
4. **Derleme:** JDK 17 ya da üstü; Windows ve macOS/Linux için `assembleDebug`, `test`, `ktlintCheck` ve `lintDebug` komutları.
5. **Belgeler:** `docs/` altındaki belgelerin bir cümlelik listesi, `docs/tasks/` ve `CLAUDE.md`'nin ne işe yaradığı.
6. **Çalışma akışı:** Kısa ömürlü dal, Pull Request ve "Squash and merge".

Rozet, emoji ya da lisans bölümü ekleme; lisans ayrı bir karar.

Commit: `docs: add README`

## Adım C — Son doğrulama

Aşağıdaki komutların **hepsini gerçekten çalıştır** ve sonuçları rapora özetle:

```
.\gradlew.bat clean
.\gradlew.bat projects
.\gradlew.bat ktlintCheck
.\gradlew.bat test
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
.\gradlew.bat :core:domain:dependencies --configuration runtimeClasspath
```

Beklenenler:

- `projects` çıktısında 11 proje görünür: `:app`, `:core:domain`, `:core:data`, `:core:prayertimes`, altı feature modülü ve `:widget`.
- `:core:domain`'de **2 test** geçer; test sayısını raporda modül bazında ver.
- `lintDebug` bütün modüllerde 0 hata. Uyarılar 0, ya da yalnızca A6/5'teki istisnaya giren uyarılar kalır ve raporda listelenir.
- `:core:domain` runtime classpath'inde Android ya da AndroidX kütüphanesi yoktur.
- `ktlintCheck`, `build-logic` dahil temiz.

## Adım D — Commit'ler ve rapor

Önerilen commit sırası:

1. `docs: add F1-02 card and update planning docs`
2. `chore: stop tracking .idea`
3. `chore: rename root project to NamazTakibi`
4. `build: run ktlint on root build scripts`
5. `build(deps): update AndroidX and Compose libraries`
6. `fix(lint): resolve template lint warnings`
7. `build: add build-logic convention plugins`
8. `build: migrate app and core:domain to convention plugins`
9. `build: add empty core, feature and widget modules`
10. `build: enforce module dependency rules (M-08)`
11. `docs: add README`
12. `docs(tasks): add F1-02 report`

Raporu `CLAUDE.md` Bölüm 11'e göre `docs/tasks/F1-02-rapor.md` dosyasına yaz ve son commit olarak ekle. Ek olarak şunları belirt:

- Sürüm tablosu: mevcut, hedef, kaynak.
- Lint uyarı sayısı: önce ve sonra, kalan uyarılar varsa kimlikleriyle.
- Üç negatif denemenin hata mesajları.
- `projects` çıktısının özeti ve modül bazında test sayıları.
- Plandan sapmalar ve nedenleri.

**Push etme, PR açma.** Kullanıcı yapacak.

## Bilinen tuzaklar (AGP 9 ve Gradle 9)

1. **`org.jetbrains.kotlin.android` eklentisini hiçbir yere uygulama.** AGP 9'da Kotlin derlemesi yerleşiktir ve bu eklenti yeni DSL ile uyumsuzdur. `kapt` da yok.
2. **`CommonExtension` artık generic değil.** `BaseExtension` ve eski variant API'leri gizli. Convention kodunda `ApplicationExtension`, `LibraryExtension` ya da tip parametresiz `CommonExtension` kullan. `defaultConfig` ayarları `defaultConfig.apply { ... }` biçiminde yazılır.
3. **Kotlin JVM hedefi Android modüllerinde kendiliğinden gelir.** Yerleşik Kotlin'de `jvmTarget`, `compileOptions.targetCompatibility`'yi izler. Android modüllerinde ayrıca ayarlama; saf JVM modülünde ise ayarla.
4. **Convention kodunda catalog'a erişim:** `extensions.getByType<VersionCatalogsExtension>().named("libs")`. Type-safe `libs.` erişimi convention sınıflarında doğrudan çalışmaz.
5. **`ProjectDependency.dependencyProject` Gradle 9'da yok;** `path` kullan.
6. **Included build görevleri kendiliğinden çalışmaz.** `build-logic`'in ktlint'i için B1/7'ye bak.
7. **JUnit 5'te "0 test":** Taşımadan sonra `:core:domain`'de test sayısı 0 ise `useJUnitPlatform()` ya da launcher bağımlılığı convention'a eksik taşınmıştır.
8. **Kökteki `apply false` satırlarını silme.** Convention plugin'ler AGP'yi ve Kotlin eklentilerini `compileOnly` alır; bu eklentileri derleme sınıf yoluna kökteki `plugins { ... apply false }` satırları getirir. `com.android.library`, `com.android.application` ile aynı AGP paketindedir; yine de catalog'a `android-library` eklentisini ekleyip kökte `apply false` ile tanımlamak niyeti açık eder.

## Kapsam dışı (bu kartta yapma)

- Hilt ve GitHub Actions CI: F1-03.
- Room, DataStore, Navigation, WorkManager, Glance, KSP, Robolectric ve kotlinx.serialization: ihtiyaç duyulan fazda eklenecek.
- Yeni modüllere herhangi bir Kotlin kaynağı (yer tutucu sınıf dahil).
- `:core:domain`'deki koda ya da testlere değişiklik.
- AGP, Gradle, Kotlin, SDK seviyeleri, ktlint eklentisi sürümü, JUnit 6 ya da Java 17 geçişi.
- `MainActivity`, tema dosyaları ve şablon testlerinin yeniden düzenlenmesi ya da silinmesi.
- `gradle.properties` performans ayarları (parallel, caching, configuration cache).
- Lisans dosyası.

## Kabul kriterleri

**Bölüm A**

- [ ] `.idea/` git'te izlenmiyor ve `.gitignore`'da `/.idea/` var.
- [ ] `rootProject.name = "NamazTakibi"` ve `app_name = "Namaz Takibi"`. `applicationId` ve `namespace` değişmemiş.
- [ ] ktlint kökteki `.kts` dosyalarını da kontrol ediyor.
- [ ] Kapsamdaki kütüphaneler en yeni uyumlu kararlı sürümde; sürüm tablosu kaynaklarıyla raporda.
- [ ] `lintDebug`: 0 hata. Uyarılar 0, ya da yalnızca A6/5 istisnasındakiler kalmış ve raporlanmış. Hiçbir lint uyarısı gizlenmemiş.

**Bölüm B**

- [ ] `build-logic` dört convention plugin'i sağlıyor. SDK ve JVM değerleri yalnızca `ProjectConfig.kt`'te.
- [ ] Hiçbir modül build dosyasında `compileSdk`, `minSdk`, `targetSdk`, `compileOptions` ya da JVM hedefi yok.
- [ ] Hiçbir yerde `org.jetbrains.kotlin.android` ya da `kapt` yok.
- [ ] Dokuz yeni modül tablodaki yol, plugin ve `namespace` ile var; yalnızca `build.gradle.kts` içeriyorlar; `api(project(...))` yok.
- [ ] `:app` bütün modüllere bağlı ve `assembleDebug` başarılı.
- [ ] Bağımlılık denetimi etkin: üç negatif deneme anlamlı mesajla başarısız oldu ve mesajlar raporda. Tabloda olmayan modül de derlemeyi durduruyor.
- [ ] `:core:domain`'de 2 test geçiyor; runtime classpath'inde Android yok.
- [ ] `ktlintCheck`, `build-logic` dahil temiz.
- [ ] README var ve B6'daki altı başlığı kapsıyor.

**Genel**

- [ ] Commit'ler `chore/f1-02-modules-hygiene` dalında; `main`'e dokunulmamış.
- [ ] Rapor `docs/tasks/F1-02-rapor.md` dosyasında ve son commit olarak eklenmiş.

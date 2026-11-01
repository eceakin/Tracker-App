# Görev Kartı F1-03 — Araç zinciri yükseltmesi

Faz 1 · Kart 3/5 · 1 Kasım 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

F1-02'de kütüphaneler güncellendi ama araç zinciri (Gradle, AGP, Kotlin, SDK) sabit tutuldu. Bu yüzden 9 lint uyarısı kaldı ve üç kütüphane geride tutuldu. Bu kart araç zincirini güncel kararlı sürümlere çıkarır. Kartın sonunda:

- Gradle, AGP, Kotlin ve ktlint eklentisi güncel kararlı sürümde,
- `compileSdk` ve `targetSdk` 37 (Android 17),
- JVM hedefi Java 17, test çatısı JUnit 6,
- geri tutulan üç kütüphane en yeni sürümde,
- **Android Lint 0 hata, 0 uyarı.**

CI (F1-04) bu temiz zeminle başlayacak.

**Neden şimdi?** Uygulamada henüz gerçek kod yok. Hedef SDK'yı ve Java sürümünü yükseltmenin en ucuz anı bu. Özellik kodu yazıldıktan sonra aynı iş, davranış değişikliklerinin her ekranda yeniden test edilmesi demektir.

**Neden adım adım?** Her yükseltme ayrı bir commit'tir ve her commit'ten sonra derleme ile testler çalıştırılır. Bir şey kırılırsa hangi adımın kırdığı `git bisect`'e gerek kalmadan bellidir.

## Ön koşullar

- `main`, F1-02'nin birleştirildiği `0cf27ae` commit'inde ya da daha ilerde.
- Kullanıcı şu dosyaları çalışma ağacına koymuş olmalı:
  - `CLAUDE.md` (sürüm 1.3)
  - `docs/mimari-ve-kodlama-ilkeleri.md` (sürüm 1.3)
  - `docs/gelistirme-yol-haritasi.md` (1 Kasım 2026, F1-02 tamamlandı güncellemesi)
  - `docs/tasks/F1-03-arac-zinciri.md` (bu kart)
- Çalışma ağacında başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler

- `CLAUDE.md` (tamamı)
- `docs/tasks/F1-02-rapor.md`: sürüm tablosu ve lint bölümü
- `docs/mimari-ve-kodlama-ilkeleri.md`: M-07 ve K-01

## Adım 0 — Plan ve onay

Bu adımda yalnızca dosya okuyabilir, internette sürüm notlarına bakabilir ve `.\gradlew.bat --version` ile `.\gradlew.bat lintDebug` çalıştırabilirsin. Hiçbir dosyayı değiştirme.

Plana şunları yaz, sonra **kullanıcının onayını bekle:**

1. **Kullanıcıya soru — Android Studio sürümü:** Kullanıcıya Android Studio'nun tam sürümünü sor (Help → About). Her Android Studio sürümü yalnızca belirli bir AGP sürümüne kadar destekler; hedef AGP, kullanıcının Android Studio'sunun desteklediği en yeni sürümü **geçemez.** Aksi halde proje Android Studio'da açılmaz. Gerekirse kullanıcıdan önce Android Studio'yu güncellemesini iste.
2. **Sürüm tablosu:** Her bileşen için mevcut sürüm, hedef sürüm, kaynak bağlantısı ve uyumluluk notu. Bileşenler: Gradle wrapper, AGP, Kotlin (KGP ve Compose compiler eklentisi), ktlint Gradle eklentisi, compileSdk, targetSdk, JVM hedefi, JUnit, `core-ktx`, `lifecycle-runtime-ktx`, `compose-bom`. Ayrıca catalog'daki diğer kütüphanelerin hâlâ en yeni kararlı sürümde olup olmadığı.
3. **Uyumluluk zinciri:** Seçilen AGP'nin istediği en düşük Gradle sürümü, desteklediği en yüksek API seviyesi ve AGP'nin yerleşik Kotlin için çalıştığı KGP sürümü. Kaynak: AGP sürüm notlarındaki uyumluluk tablosu.
4. **Android 17 davranış değişiklikleri:** `targetSdk 37` ile gelen davranış değişikliklerinin kısa listesi (resmi "Behavior changes: apps targeting Android 17" sayfasından) ve her biri için bu projeyi şimdi etkileyip etkilemediği. Şu an uygulama yalnızca şablon ekranından oluşuyor; ileride etkileyecek olanları (özellikle alarm, bildirim, arka plan işi ve widget ile ilgili olanları) ayrıca işaretle.
5. **Commit sırası** (bkz. Adım C).

## Bölüm A — Adım adım yükseltme

Dal: `main`'den `chore/f1-03-toolchain`. Kartın tüm işi bu dalda yapılır. İlk commit, kullanıcının koyduğu dört belgedir: `docs: add F1-03 card and update planning docs`.

**Her adımdan sonra** şu iki komutu çalıştır ve ikisi de yeşilse commit at:

```
.\gradlew.bat assembleDebug
.\gradlew.bat test
```

Bir adım kırmızıysa sonraki adıma geçme. Düzelt; iki denemede düzelmezse dur ve raporla (`CLAUDE.md` Bölüm 12).

### A1 — Gradle wrapper

1. Hedef: A3'teki AGP'nin istediği Gradle sürümü ya da onunla uyumlu en yeni kararlı Gradle.
2. Wrapper'ı Gradle'ın kendi komutuyla güncelle: `.\gradlew.bat wrapper --gradle-version <sürüm>`. Komutu **iki kez** çalıştır; ikinci çalıştırma `gradle-wrapper.jar`'ı da yeni sürümle yeniler. Dosyaları elle düzenleme.
3. `gradle-wrapper.properties`'teki `distributionUrl` ile `distributionSha256Sum` (varsa) tutarlı olmalı.

Commit: `build: update Gradle wrapper to <sürüm>`

### A2 — Kotlin ve ktlint

1. Catalog'daki `kotlin` sürümünü güncel kararlı Kotlin'e çıkar. Bu tek satır `kotlin-jvm`, `kotlin-compose`, `kotlin-gradlePlugin` ve `compose-gradlePlugin`'i birlikte günceller.
2. AGP 9'da KGP sürümünü yükseltmenin yolu, kökteki `plugins { ... apply false }` bloğunda daha yüksek bir Kotlin eklentisi tanımlamaktır. Bu projede `kotlin.jvm` ve `kotlin.compose` zaten orada tanımlı. Yeni bir `buildscript` bloğu ekleme.
3. ktlint Gradle eklentisini güncel kararlı sürüme çıkar. Yeni Kotlin sözdizimini tanıyan bir ktlint motoru gerekir; eski motor yeni Kotlin dosyalarında hatalı sonuç verebilir.
4. `ktlintCheck` çalıştır. Yeni ktlint sürümü yeni kurallar getirdiyse `ktlintFormat` ile düzelt. **Kural kapatma.** Yeni bir kural `BR01_` test adlarıyla çakışırsa dur ve sor.

Commit: `build: update Kotlin to <sürüm> and ktlint plugin to <sürüm>`

### A3 — AGP

1. Catalog'daki `agp` sürümünü Adım 0'da onaylanan sürüme çıkar.
2. Yeni AGP sürümünün sürüm notlarındaki kaldırılan ya da değişen DSL'leri kontrol et. Özellikle `ProjectConfig` ve convention plugin'lerdeki `compileSdk` / `compileSdkMinor` kullanımı yeni sürümde kullanımdan kalkmış olabilir; derleme çıktısında uyarı varsa önerilen sözdizimine geç.
3. Derleme çıktısındaki **bütün** Gradle ve AGP kullanımdan kaldırma (deprecation) uyarılarını listele. `.\gradlew.bat assembleDebug --warning-mode all` ile görünürler. Bu projenin kendi build dosyalarından gelenleri düzelt; üçüncü taraf eklentilerden gelenleri rapora yaz.

Commit: `build: update AGP to <sürüm>`

### A4 — compileSdk ve targetSdk 37

1. `ProjectConfig.kt`'te `COMPILE_SDK = 37` ve `TARGET_SDK = 37` yap. 37 için bir minor API seviyesi yayımlanmışsa planda belirt; yoksa minor ayarını kaldır.
2. Bu değerler başka hiçbir dosyada yazmamalı (M-07).
3. Android SDK Platform 37 bilgisayarda kurulu değilse derleme hata verir. AGP eksik platformu kendisi indirebilir; indiremezse kullanıcıdan Android Studio → SDK Manager üzerinden "Android 17 (API 37)" platformunu kurmasını iste.
4. Bu adımdan sonra uygulamayı bir emülatörde ya da cihazda **açılacak şekilde** çalıştırmak gerekmez; kullanıcı Adım D'de bir kez deneyecek.

Commit: `build: target and compile against Android 17 (API 37)`

### A5 — Java 17 ve JUnit 6

1. `ProjectConfig.kt`'te `JAVA_VERSION = JavaVersion.VERSION_17` ve `JVM_TARGET = JvmTarget.JVM_17` yap. Değer başka hiçbir dosyada yazmamalı.
2. Catalog'da `junit5` sürüm anahtarını `junit` adlı tek bir JUnit Jupiter sürümüyle değiştir ve en yeni kararlı JUnit 6 sürümüne çıkar. Anahtar adını değiştirirsen catalog'daki ve convention'daki bütün atıfları güncelle. JUnit 4 (`junit:junit`, uygulamadaki şablon testi) ayrı kalır; iki anahtar karışmasın diye JUnit 4 anahtarını `junit4` olarak adlandır.
3. JUnit 6, JUnit 5 ile aynı Jupiter API'sini kullanır; `PrayerTest.kt`'te değişiklik **gerekmemeli.** Değişiklik gerekiyorsa dur ve sor.
4. `:core:domain:test` sonunda **2 test** geçmeli. 0 test, yapılandırmanın bozulduğu anlamına gelir.

Commit: `build: move JVM target to Java 17 and tests to JUnit 6`

### A6 — Geri tutulan kütüphaneler

1. `core-ktx`, `lifecycle-runtime-ktx` ve `compose-bom`'u en yeni kararlı sürüme çıkar.
2. Catalog'daki diğer kütüphaneleri (`activity-compose`, `androidx.test.ext:junit`, `espresso-core`) tekrar kontrol et; daha yeni kararlı sürüm çıktıysa onları da güncelle.
3. Alpha, beta ve RC sürümler kullanılmaz.

Commit: `build(deps): update held-back AndroidX and Compose libraries`

### A7 — Küçük temizlikler (F1-02 incelemesinden)

1. `ProjectConfig.kt`'teki `library(alias)` yardımcısı, catalog'da olmayan bir ad için `findLibrary(alias).get()` ile anlamsız bir hata veriyor. Hata mesajı aranan adı söylesin, örneğin `"Version catalog'da '$alias' kütüphanesi yok"`.
2. Bu yardımcılar SDK sabitleriyle aynı dosyada duruyor. `ProjectConfig.kt` yalnızca sabitleri içersin; catalog yardımcılarını `build-logic/convention/src/main/kotlin/VersionCatalogExtensions.kt` dosyasına taşı (tek sorumluluk, P-01).

Commit: `refactor(build-logic): clearer catalog lookup errors`

## Bölüm B — Son doğrulama

Aşağıdaki komutların **hepsini gerçekten çalıştır** ve sonuçları rapora özetle:

```
.\gradlew.bat clean
.\gradlew.bat --version
.\gradlew.bat ktlintCheck
.\gradlew.bat test
.\gradlew.bat assembleDebug --warning-mode all
.\gradlew.bat lintDebug
.\gradlew.bat :core:domain:dependencies --configuration runtimeClasspath
.\gradlew.bat help
```

Beklenenler:

- `lintDebug`: **bütün modüllerde 0 hata, 0 uyarı.** Uyarı kalırsa gizleme. Kalan her uyarıyı nedeniyle raporla ve dur; kararı planlama verecek.
- `assembleDebug --warning-mode all`: projenin kendi build dosyalarından gelen kullanımdan kaldırma uyarısı yok.
- `:core:domain`'de 2 test, `:app`'te 1 test. Runtime classpath'te Android yok.
- `help`: M-08 denetimi hâlâ çalışıyor. Ek doğrulama olarak F1-02'deki negatif denemelerden birini (`:feature:today` → `:core:data`) tekrarla, mesajı rapora yaz, değişikliği geri al.
- `ktlintCheck` `build-logic` dahil temiz.

## Adım C — Commit'ler ve rapor

Önerilen sıra:

1. `docs: add F1-03 card and update planning docs`
2. `build: update Gradle wrapper to <sürüm>`
3. `build: update Kotlin to <sürüm> and ktlint plugin to <sürüm>`
4. `build: update AGP to <sürüm>`
5. `build: target and compile against Android 17 (API 37)`
6. `build: move JVM target to Java 17 and tests to JUnit 6`
7. `build(deps): update held-back AndroidX and Compose libraries`
8. `refactor(build-logic): clearer catalog lookup errors`
9. `docs(tasks): add F1-03 report`

Bir adımın çalışması için önceki adımın sırasını değiştirmek gerekirse (örneğin AGP, Kotlin'den önce gelmeli), sıra değişebilir; nedenini rapora yaz. Her commit kendi başına derlenmeli.

Rapor `docs/tasks/F1-03-rapor.md` dosyasına, `CLAUDE.md` Bölüm 11'e göre yazılır ve son commit olarak eklenir. Ek olarak şunları belirt:

- Sürüm tablosu: önce, sonra, kaynak.
- Lint: önce 9 uyarı, sonra kaç uyarı.
- Her adımda karşılaşılan derleme hataları ve nasıl çözüldükleri.
- Android 17 davranış değişiklikleri listesi ve ileride hangi fazı etkileyecekleri.
- Projenin kendi dosyalarında düzeltilen ve üçüncü taraf eklentilerden kalan kullanımdan kaldırma uyarıları.

**Push etme, PR açma.** Kullanıcı yapacak.

## Adım D — Kullanıcının elle yapacağı kontrol

Rapordan sonra kullanıcıya şunu söyle: "Android Studio'da Sync Project with Gradle Files yap, ardından uygulamayı bir emülatörde (API 37 imajı ya da elindeki cihaz) çalıştır. 'Hello Android!' ekranı açılmalı." Bunu sen yapamazsın; yalnızca kullanıcıya söyle.

## Bilinen tuzaklar

1. **Android Studio ve AGP uyumu.** Yeni AGP'yi eski Android Studio açamaz. Adım 0'daki soru bu yüzden var.
2. **`org.jetbrains.kotlin.android` ve `kapt` hâlâ yasak.** Yeni Kotlin sürümüne geçerken internetteki eski rehberler bu eklentileri önerebilir.
3. **Gradle wrapper'ı elle düzenleme.** `gradle-wrapper.jar` da güncellenmeli; `wrapper` görevini iki kez çalıştır.
4. **JUnit 6, Java 17 ister.** A5'te Java 17'ye geçmeden JUnit 6'ya geçersen bağımlılık çözümü "JVM sürümü uyumsuz" hatasıyla başarısız olur. A5'teki iki değişiklik birlikte yapılır.
5. **Compose BOM ve Kotlin.** Compose compiler eklentisi Kotlin sürümüyle birlikte gelir; ayrı bir Compose compiler sürümü tanımlama.
6. **0 test tuzağı.** JUnit sürüm anahtarı değişince convention'daki atıf eksik güncellenirse test sayısı 0'a düşer ama derleme "başarılı" görünür.
7. **Gizleme yasağı sürüyor.** `lint.xml`, baseline, `@SuppressLint`, `tools:ignore`, `lint { disable(...) }` ve ktlint kural kapatma yasak.

## Kapsam dışı (bu kartta yapma)

- Hilt, CI ve `main` koruması: F1-04.
- Yeni kütüphane eklemek (Room, Navigation, Glance vb.).
- Android 17 davranış değişiklikleri için kod yazmak. Bu kart yalnızca listeler; uygulama ilgili fazda yapılır.
- `minSdk` değişikliği (26 kalır).
- Uygulama koduna, tema dosyalarına ve şablon testlerine, derleme gerektirmedikçe dokunmak.
- M-08 denetiminin mantığını değiştirmek.

## Kabul kriterleri

- [ ] Gradle, AGP, Kotlin ve ktlint eklentisi Adım 0'da onaylanan güncel kararlı sürümlerde; AGP, kullanıcının Android Studio'sunun desteklediği sürümü geçmiyor.
- [ ] `compileSdk 37`, `targetSdk 37`, `minSdk 26` ve Java 17; bu değerler yalnızca `ProjectConfig.kt`'te.
- [ ] JUnit 6 kullanılıyor; `:core:domain`'de 2 test geçiyor; `PrayerTest.kt` değişmemiş.
- [ ] Geri tutulan üç kütüphane en yeni kararlı sürümde.
- [ ] `lintDebug`: bütün modüllerde 0 hata, 0 uyarı; hiçbir uyarı gizlenmemiş.
- [ ] Projenin kendi build dosyalarından gelen kullanımdan kaldırma uyarısı yok.
- [ ] `ktlintCheck` temiz; hiçbir ktlint kuralı kapatılmamış.
- [ ] M-08 denetimi hâlâ çalışıyor (bir negatif deneme tekrarlandı).
- [ ] Catalog yardımcıları ayrı dosyada ve anlamlı hata veriyor.
- [ ] Her adım ayrı commit; her commit kendi başına derleniyor.
- [ ] Rapor `docs/tasks/F1-03-rapor.md`'de, son commit olarak eklenmiş.
- [ ] Commit'ler `chore/f1-03-toolchain` dalında; `main`'e dokunulmamış.

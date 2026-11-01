# Görev Kartı F1-01 — Paket adı, ktlint ve `:core:domain` modülü

Faz 1 · Kart 1/4 · 30 Ekim 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

Android Studio şablonundan gelen boş projeyi Faz 1 iskeletinin ilk adımına getirmek:

- kalıcı paket adı,
- zorunlu ktlint,
- Android'den bağımsız, saf Kotlin bir `:core:domain` modülü ve içinde JUnit 5 ile çalışan ilk test.

Bu kart, yol haritasındaki Faz 1 "bitti tanımı"nın çekirdeğini karşılar: "`:core:domain`'de örnek bir test geçiyor, ktlint temiz". Diğer modüller, Hilt ve CI sonraki kartlardadır.

## Ön koşullar

- Proje kökünde `CLAUDE.md` ve `docs/` klasörü var.
- Depo git ile başlatılmış ve `main`'de ilk commit (boş şablon + `CLAUDE.md` + `docs/`) atılmış. Değilse **dur ve kullanıcıya söyle.**

## Okunacak belgeler

- `CLAUDE.md` (tamamı)
- `docs/mimari-ve-kodlama-ilkeleri.md`: Bölüm 1 (M-01, M-02), Bölüm 7 (K-01 – K-03) ve Bölüm 8 (K-10, K-13)
- `docs/gereksinim-analizi-srs.md`: yalnızca BR-01 satırı

## Kapsam

### Adım 0 — Plan ve onay

Hiçbir dosyayı değiştirmeden önce şunları yap:

1. Mevcut durumu incele: `settings.gradle.kts`, kök ve `app` altındaki `build.gradle.kts`, `gradle/libs.versions.toml`, `.gitignore` ve `app/src` altındaki paket klasörleri.
2. Şunlarla birlikte kısa bir uygulama planı yaz:
   - değiştireceğin ve oluşturacağın dosyalar,
   - kullanacağın ktlint eklentisinin ve JUnit sürümlerinin adı ve gerekçesi,
   - `app` modülünün Java/JVM hedef sürümü (Adım 3'te buna uyacaksın).
3. **Kullanıcının onayını bekle.**

### Adım 1 — Dal

`main`'den `feat/f1-01-skeleton-domain` dalını aç. Tüm iş bu dalda yapılır.

### Adım 2 — Paket adı

1. `app/build.gradle.kts` içinde `namespace` ve `applicationId` değerlerini `com.eceakin.namaztakibi` yap.
2. Kaynak klasörlerini yeni pakete taşı ve her dosyadaki `package` / `import` satırlarını güncelle. Bu, `main`, `test` ve `androidTest` kaynak kümelerinin üçü için de geçerlidir.
3. `AndroidManifest.xml` ve tema dosyalarında eski paket adına atıf kalmadığından emin ol.
4. **Doğrulama:** Depo kökünde `com.example` araması (`build/` ve `.gradle/` klasörleri hariç) **sıfır sonuç** vermeli.

### Adım 3 — `:core:domain` modülü (saf Kotlin/JVM)

1. `core/domain/` klasörünü oluştur. `settings.gradle.kts`'e `include(":core:domain")` ekle.
2. `core/domain/build.gradle.kts` dosyasını oluştur:
   - **Yalnızca** `org.jetbrains.kotlin.jvm` eklentisini kullan; Android eklentisi uygulama (M-02). Kotlin sürümü projenin mevcut Kotlin sürümüyle aynı olmalı ve version catalog'daki alias üzerinden gelmeli.
   - JVM hedefini `app` modülünün hedefiyle uyumlu ayarla. Android modülü, kendisinden daha yeni bir JVM hedefiyle derlenmiş bir modülü tüketemez.
   - Test bağımlılıkları: JUnit 5 (`junit-jupiter`) ve `junit-platform-launcher` (runtimeOnly). `tasks.test { useJUnitPlatform() }`.
3. Bağımlılıkların hepsini `gradle/libs.versions.toml`'a ekle. Build dosyasına sürüm numarası yazma.
4. `app` modülüne `implementation(project(":core:domain"))` ekle. Amaç, modülün Android tarafından tüketilebildiğini derlemeyle kanıtlamak.

### Adım 4 — İlk domain kodu ve test (TDD)

Kalem listesi BR-01'dir: sabah, öğle, ikindi, akşam, yatsı (farz) ve vitir (vâcip).

1. **Önce testi yaz:** `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/PrayerTest.kt`. Test adları kimlik taşımalı (CLAUDE.md Bölüm 8):
   - `BR01_takipKalemleriAltiTanedirVeSiralidir`: Sıra tam olarak SABAH, OGLE, IKINDI, AKSAM, YATSI, VITIR olmalı.
   - `BR01_vitirDisindakiKalemlerFarzdir`: VITIR dışındaki beş kalem farz, VITIR farz değil.
2. Testin kırmızı olduğunu gör.
3. **Sonra kodu yaz:** `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/Prayer.kt`.
   - `enum class Prayer`: değerler `SABAH, OGLE, IKINDI, AKSAM, YATSI, VITIR`. Fıkhi terimler kendi adıyla kalır, ASCII yazılır (K-02).
   - `val isFarz: Boolean` özelliği.
   - KDoc'ta `BR-01` atfı (K-05).
4. Testin yeşil olduğunu gör.

**Kapsam sınırı:** Bu dosyaya başka alan, fonksiyon ya da sınıf ekleme. Rekât sayısı, vakit sınırları gibi şeyler sonraki kartların işidir (P-03).

### Adım 5 — ktlint

1. ktlint'i tüm modüllere uygula. Önerilen yol: `org.jlleitschuh.gradle.ktlint` Gradle eklentisi. Bu eklenti `ktlintCheck` ve `ktlintFormat` görevlerini sağlar.
   - Eklentiyi kök `build.gradle.kts`'ten alt projelere uygula.
   - Sürümü hafızandan yazma; Gradle Plugin Portal'dan güncel kararlı sürümü doğrula.
   - Farklı bir yol seçersen Adım 0 planında gerekçesini yaz.
2. Proje köküne bir `.editorconfig` ekle. İçinde en az şunlar olmalı:
   - `[*.{kt,kts}]` bölümü,
   - `ktlint_code_style = android_studio`,
   - Compose fonksiyonlarının büyük harfle başlamasına izin veren ayar: `ktlint_function_naming_ignore_when_annotated_with = Composable`.
3. Şablondan gelen kodu `ktlintFormat` ile düzelt. Ardından `ktlintCheck` hatasız geçmeli.
4. **Kural kapatma:** Hiçbir ktlint kuralını kapatma; Compose ayarı bunun tek istisnasıdır. Kapatman gereken bir kural çıkarsa dur ve sor.

### Adım 6 — `.gitignore` kontrolü

Kök `.gitignore`'da şunların olduğundan emin ol, yoksa ekle:

- `.claude/settings.local.json`
- `local.properties`
- `build/`
- `.gradle/`
- `*.jks` ve `*.keystore`

`git status` çıktısında `.claude/settings.local.json` **görünmemeli**.

### Adım 7 — Doğrulama

Aşağıdaki komutların **hepsini gerçekten çalıştır.** Çıktı özetlerini rapora koy.

```
.\gradlew.bat clean
.\gradlew.bat ktlintCheck
.\gradlew.bat :core:domain:test
.\gradlew.bat assembleDebug
.\gradlew.bat :app:lintDebug
```

`:core:domain:test` çıktısında **2 test** geçmiş olmalı. Test sayısı 0 ise JUnit 5 yapılandırması yanlıştır: test bulunamadığı için "başarılı" görünür. Bu sık yapılan bir hatadır, kontrol et.

### Adım 8 — Commit'ler

Küçük, Conventional Commits biçiminde commit'ler at. Örnek sıra:

1. `refactor(app): rename package to com.eceakin.namaztakibi`
2. `build: add core:domain pure Kotlin module with JUnit 5`
3. `feat(domain): add Prayer items (BR-01)`
4. `build: add ktlint and .editorconfig`
5. `style: apply ktlintFormat`
6. `chore: update .gitignore`

**Push etme, PR açma.** Kullanıcı yapacak.

## Kapsam dışı (bu kartta yapma)

- Diğer modüller (`:core:data`, `:core:prayertimes`, `:feature:*`, `:widget`): Kart 2.
- Hilt, Room, DataStore, Compose Navigation, WorkManager, Glance: sonraki kartlar.
- GitHub Actions CI: Kart 3.
- `MainActivity` ve tema kodunda paket adı ve ktlint biçimlendirmesi dışında değişiklik.
- `minSdk`, `targetSdk`, AGP, Gradle ve Kotlin sürümlerini değiştirmek. Değişiklik zorunlu görünüyorsa dur ve sor.

## Kabul kriterleri

- [ ] `namespace` ve `applicationId` = `com.eceakin.namaztakibi`. Depoda (build klasörleri hariç) `com.example` geçmiyor.
- [ ] `:core:domain` yalnızca `kotlin.jvm` eklentisiyle derleniyor. Modülde `android` geçen bir eklenti, bağımlılık ya da import yok (M-02).
- [ ] `Prayer` enum'u BR-01'e uygun. İki test var, ikisi de yeşil ve adları `BR01_` ile başlıyor.
- [ ] `app`, `:core:domain`'e bağlı ve `assembleDebug` başarılı.
- [ ] `ktlintCheck` ve `:app:lintDebug` hatasız.
- [ ] Bütün sürümler `libs.versions.toml`'da. Build dosyalarında sürüm numarası yok.
- [ ] `.gitignore` güncel. `.claude/settings.local.json` git'te izlenmiyor.
- [ ] Commit'ler `feat/f1-01-skeleton-domain` dalında. `main`'e dokunulmamış.

## Rapor

CLAUDE.md Bölüm 11'deki biçimde rapor yaz. Ek olarak şunları belirt:

- Seçilen ktlint eklentisi, JUnit 5 ve Kotlin sürümleri.
- `app` modülünün JVM hedefi ve `:core:domain` için seçtiğin hedef.
- `ktlintFormat`'ın şablon kodunda değiştirdiği dosyaların listesi.

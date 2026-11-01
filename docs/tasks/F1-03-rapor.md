## Rapor: F1-03 — Araç zinciri yükseltmesi

Dal: `chore/f1-03-toolchain`

### Yapılanlar

- Android Studio **Rabbit 2 (2026.2.2)** doğrulandı (`product-info.json`: `AI-262.10968.63.2622.16730706`, `AndroidStudio2026.2.2`). Kullanıcı API 37.1 platformunu SDK Manager'dan kurdu.
- Gradle wrapper **9.6.1**'e yükseltildi (kullanıcı kararı: 9.8.2 yerine AGP 9.5'in test edildiği 9.6 serisinin son yaması). `wrapper` görevi iki kez çalıştırıldı; `distributionSha256Sum` korundu.
- AGP **9.5.0**, Kotlin **2.4.21**.
- ktlint Gradle eklentisi 14.2.0'da kaldı; ktlint **motoru 1.8.0**'a sabitlendi ve sürüm catalog'a (`ktlintEngine`) taşındı. Sabitleme hem ana projede hem `build-logic`'te geçerli.
- `compileSdk 37.1`, `targetSdk 37`, `minSdk 26`, Java 17 ve JVM hedefi 17 — hepsi yalnızca `ProjectConfig.kt`'te.
- JUnit **6.1.3**'e geçildi. Catalog anahtarları: `junit5` → `junit` (JUnit Jupiter), eski `junit` (JUnit 4) → `junit4`. `:app`'teki şablon testi `libs.junit4` kullanıyor. `PrayerTest.kt` değişmedi.
- Geri tutulan üç kütüphane güncellendi: `core-ktx` 1.19.1, `lifecycle-runtime-ktx` 2.11.0, `compose-bom` 2026.09.00.
- Catalog yardımcıları `VersionCatalogExtensions.kt`'ye taşındı; eksik alias artık `Version catalog'da '<alias>' kütüphanesi yok` hatası veriyor. `ProjectConfig.kt` yalnızca sabitleri içeriyor.

### Değişen dosyalar

- `gradle/wrapper/gradle-wrapper.properties`, `gradle/wrapper/gradle-wrapper.jar`, `gradlew`, `gradlew.bat`
- `gradle/libs.versions.toml`
- `build.gradle.kts` (ktlint motor sabitlemesi)
- `build-logic/convention/build.gradle.kts` (ktlint motor sabitlemesi)
- `build-logic/convention/src/main/kotlin/ProjectConfig.kt`
- `build-logic/convention/src/main/kotlin/VersionCatalogExtensions.kt` (yeni)
- `app/build.gradle.kts` (`libs.junit` → `libs.junit4`)
- `app/src/main/java/com/eceakin/namaztakibi/ui/theme/Theme.kt` (ktlintFormat: 1 boş satır)
- `docs/tasks/F1-03-rapor.md` (bu dosya)

### Sürüm tablosu

| Bileşen | Önce | Sonra | Kaynak |
| --- | --- | --- | --- |
| Gradle wrapper | 9.3.1 | **9.6.1** | `services.gradle.org/versions/all` |
| AGP | 9.1.1 | **9.5.0** | Google Maven metadata (en yeni kararlı) |
| Kotlin (KGP + Compose eklentisi) | 2.2.10 | **2.4.21** | Maven Central metadata |
| ktlint Gradle eklentisi | 14.2.0 | 14.2.0 (değişmedi) | Gradle Plugin Portal metadata |
| ktlint motoru | 14.2.0 varsayılanı | **1.8.0** | Maven Central metadata (`ktlint-cli`) |
| compileSdk | 36 + minor 1 | **37 + minor 1 (37.1)** | Kullanıcı SDK Manager'dan kurdu |
| targetSdk | 36 | **37** | — |
| minSdk | 26 | 26 (değişmedi) | — |
| Java / JVM hedefi | 11 / JVM_11 | **17 / JVM_17** | AGP 9.5 ve JUnit 6 gereksinimi |
| JUnit (BOM/Jupiter) | 5.14.4 | **6.1.3** | Maven Central metadata |
| `core-ktx` | 1.18.0 | **1.19.1** | Google Maven metadata |
| `lifecycle-runtime-ktx` | 2.10.0 | **2.11.0** | Google Maven metadata |
| `compose-bom` | 2026.06.01 | **2026.09.00** | Google Maven metadata |
| `activity-compose` | 1.13.0 | 1.13.0 (değişmedi) | Yeni kararlı yok (1.14.0 alpha) |
| `androidx.test.ext:junit` | 1.3.0 | 1.3.0 (değişmedi) | Yeni kararlı yok (1.4.0 alpha) |
| `espresso-core` | 3.7.0 | 3.7.0 (değişmedi) | Yeni kararlı yok (3.8.0 alpha) |
| `junit:junit` (JUnit 4) | 4.13.2 | 4.13.2 (değişmedi) | Catalog anahtarı `junit4` oldu |

### Adım adım karşılaşılan sorunlar ve çözümleri

1. **A1 — wrapper:** `wrapper` görevi, `distributionSha256Sum` bulunduğu hâlde yeni sürüm için sağlama toplamı verilmediği için durdu. `services.gradle.org/distributions/gradle-9.6.1-bin.zip.sha256`'dan alınan değer `--gradle-distribution-sha256-sum` ile verildi; ardından görev kartın istediği gibi iki kez çalıştırıldı.
2. **A2 — kök betik:** `allprojects { }` içinde `libs` erişimcisi alt projelerde bulunmadığı için "Extension with name 'libs' does not exist" hatası alındı. Motor sürümü kök betik kapsamında bir `val`'e alınıp blok içinde o kullanıldı.
3. **A2 — ktlint 1.8.0:** Yeni motor, şablon `Theme.kt`'de çok satırlı `when` koşulları arasında boş satır istedi. `ktlintFormat` tek bir boş satır ekledi; hiçbir kural kapatılmadı.

### Android 17 (targetSdk 37) davranış değişiklikleri

| Değişiklik | Şimdi etkiler mi? | İleride hangi faz |
| --- | --- | --- |
| Widget `RemoteViews` bitmap bellek tavanı (`6 × w × h` bayt) | Hayır (widget kodu yok) | F1-05 / Faz 4 — Glance görselleri boyutlandırılmalı |
| Büyük ekranda `screenOrientation`, `resizeableActivity`, en-boy oranı sınırları yok sayılıyor | Hayır | Faz 5 — uyarlanabilir yerleşim |
| Yeni `ACCESS_LOCAL_NETWORK` çalışma zamanı izni | Hayır | — (konum/yerel ağ yok, NFR-SEC-01) |
| ECH varsayılan açık, cleartext kısıtı | Hayır | Faz 3 — vakit istemcisi HTTPS olmalı |
| SMS OTP 3 saat gecikmesi | Hayır | — |
| `static final` alanlar değiştirilemez | Hayır | — |
| BAL / `IntentSender` sertleştirmesi | Hayır | Faz 5 — bildirimden derin bağlantı |
| Arka plan sesi sertleştirmesi | Hayır | — (ezan sesi yok) |
| Klavye/navigasyon/touchscreen/renk modu değişiminde activity yeniden başlatılmıyor | Hayır | Faz 5 — tema/dil davranışı |
| Content Capture kapatma artık `FLAG_SECURE` gerektiriyor | Hayır | Faz 5 — mazeret gizliliği (NFR-SEC-03) |

### Testler

- `.\gradlew.bat clean` → başarılı.
- `.\gradlew.bat --version` → Gradle 9.6.1, launcher JVM 21.0.7.
- `.\gradlew.bat ktlintCheck` → başarılı (`build-logic` dahil).
- `.\gradlew.bat test` → başarılı. `:core:domain` **2 test**, `:app` **1 test**, 0 hata.
- `.\gradlew.bat assembleDebug --warning-mode all` → başarılı.
- `.\gradlew.bat lintDebug` → başarılı; **0 hata, 7 uyarı** (aşağıda).
- `.\gradlew.bat :core:domain:dependencies --configuration runtimeClasspath` → yalnızca `kotlin-stdlib:2.4.21` ve `org.jetbrains:annotations:13.0`; Android yok.
- M-08 negatif denemesi (`:feature:today` → `:core:data`) → `M-08 ihlali: :feature:today → :core:data (yapılandırma: implementation). İzin verilenler: [:core:domain]`; değişiklik geri alındı, çalışma ağacı temiz.

### Lint: 9 → 7 uyarı

Önce (F1-02 sonu): **9 uyarı.** Sonra: **7 uyarı, 0 hata.** Hiçbiri gizlenmedi; `lint.xml`, baseline, `@SuppressLint`, `tools:ignore` kullanılmadı.

1. `AndroidGradlePluginVersion` ×1 (`:app`): "A newer version of Gradle than 9.6.1 is available: 9.8.2". Onaylanan Gradle 9.6.1 hedefinin doğrudan sonucu.
2. `ObsoleteLintCustomCheck` ×6 (altı `:feature:*` modülü): `androidx.lifecycle:2.11.0`'ın lint jar'ı, lint 9.5.0 ortamında geçersiz Kotlin PSI API'sine (`KtLightModifierList#getKotlinOrigin`) atıf yapıyor; `UnsafeLifecycleWhenUsage` ve `RepeatOnLifecycleWrongUsage` denetimleri atlanıyor. Üçüncü taraf artefakt uyumsuzluğu; proje kodundan kaynaklanmıyor.

Kullanıcı kararı: uyarılar gizlenmeden bırakılıp burada raporlanacak; "0 uyarı" kriteri planlamada yeniden değerlendirilecek.

Kullanımdan kaldırma (deprecation): `assembleDebug --warning-mode all` çıktısında projenin kendi dosyalarından **da** üçüncü taraf eklentilerden **de** gelen uyarı yok. `compileSdkMinor` AGP 9.5'te hâlâ geçerli.

### Karşılanan kimlikler

M-07 (SDK/JVM tek kaynak), K-01 (ktlint temiz, kural kapatılmadı), K-06 (sürümler catalog'da), M-08 (denetim çalışıyor — negatif denemeyle doğrulandı), M-02 (domain runtime classpath'te Android yok).

### Varsayımlar

- Gradle hedefi **9.6.1** (kullanıcı onayı; kartın önerdiği 9.8.2 yerine).
- ktlint motoru ana projede ve `build-logic`'te aynı catalog değeriyle sabitlendi; motor sürümünü yalnızca köke yazmak `build-logic` denetimini eklentinin varsayılan motoruna bırakırdı.
- A2 commit başlığı, eklenti sürümü değişmediği için "pin ktlint engine to 1.8.0" olarak atıldı.
- `COMPILE_SDK_MINOR = 1` korundu; kullanıcının kurduğu platform API 37.1.
- JUnit 4 alias'ı `libs.junit4` olarak yeniden adlandırıldı; `:app`'teki tek referans güncellendi.

### Belge tutarsızlıkları

- Kartın kabul kriteri "0 hata, 0 uyarı"; onaylanan sürümlerle en az 7 uyarı kalıyor. Karar kullanıcıya soruldu, "bırak ve raporla" seçildi.
- Kartın Adım 0 sürüm tablosunda Gradle 9.8.2 hedeflenmişti; kullanıcı plan değişikliğiyle 9.6.1'e çekti.
- Kartın önerdiği commit 4 başlığı "ktlint plugin to 14.2.0" idi; eklenti sürümü değişmediği için gerçeği yansıtacak biçimde değiştirildi.

### Açık sorular

- "0 lint uyarısı" kriteri planlamada nasıl güncellenecek? İki uyarı da proje kodundan değil, sürüm seçimlerinden kaynaklanıyor.
- `ObsoleteLintCustomCheck`, lifecycle veya AGP'nin sonraki sürümüyle kapanacak mı? Kapanana kadar iki lifecycle lint denetimi çalışmıyor.

# F1-04 Planı — Hilt, CI, lint kapısı ve `main` koruması

Dal: `feat/f1-04-hilt-ci` · Adım 0: 1 Kasım 2026 · Onay: 1 Kasım 2026

Bu plan, Adım 0'da ölçülen gerçeklere ve kullanıcının onay mesajındaki üç değişikliğe göre yazılmıştır.

## Onayla gelen değişiklikler

1. **Lint listesi:** A/B/C seçenekleri yerine mimari K-01 **1.5** uygulanır. `GradleDependency`, `NewerVersionAvailable` ve `AndroidGradlePluginVersion` lint'te **kapatılmaz**, ama kapının karşılaştırmasına alınmaz; kodda adlandırılmış ayrı bir küme olarak durur. Bilinen liste yalnızca altı feature modülündeki `ObsoleteLintCustomCheck` kayıtlarıdır (6 kayıt).
2. **`ciCheck` ktlint bağımlılığı:** Kök görevdeki `dependsOn("ktlintCheck")` yalnızca kökün görevine bağlanır. `ciCheck`, bütün projelerin (kök, alt projeler, `build-logic`) `ktlintCheck` görevlerine **açıkça** bağlanır ve `ciCheck --dry-run` çıktısında alt projelerin göründüğü doğrulanır.
3. **Sürümler onaylı:** Action major sürümleri, Hilt 2.60.1 ve KSP 2.3.13.

## 1. Sürüm tablosu

| Bileşen | Sürüm | Kaynak / doğrulama |
| --- | --- | --- |
| Dagger / Hilt | **2.60.1** | Maven Central metadata (`hilt-android`, `hilt-android-gradle-plugin`, `com.google.dagger.hilt.android` plugin marker); GitHub `dagger-2.60.1` (6 Temmuz 2026, yalnızca hata düzeltmesi) |
| KSP | **2.3.13** | Maven Central metadata; GitHub release `2.3.13` (30 Ekim 2026); POM'da `kotlin-stdlib:2.4.0` |
| AGP / Kotlin / Gradle | 9.5.0 / 2.4.21 / 9.6.1 | Mevcut; değişmiyor |
| `actions/checkout` | v7.0.1 → `3d3c42e5aac5ba805825da76410c181273ba90b1` | `git ls-remote --tags https://github.com/actions/checkout` |
| `actions/setup-java` | v6.1.0 → `b843ebf9b913146b38da2eaa7aeddf76074ae1ad` | `git ls-remote --tags https://github.com/actions/setup-java` |
| `gradle/actions/setup-gradle` | v6.4.1 → `8da46b539cd8fe6ab27ca75128919a18d757604c` | `git ls-remote --tags https://github.com/gradle/actions`; annotated etiketin `^{}` ile soyulmuş commit SHA'sı |
| `actions/upload-artifact` | v7.0.2 → `cf430e030ddbb5b0abf93d22962f4752f3646cd9` | `git ls-remote --tags https://github.com/actions/upload-artifact` |

SHA'lar hafızadan yazılmadı; her biri resmî deponun etiket listesinden alındı.

## 2. Uyumluluk

- **Hilt 2.60.1 ↔ AGP 9.5:** Dagger 2.59 sürüm notları "Support AGP 9 in HiltGradlePlugin" (#4944, #4979) diyor ve AGP 9 + Gradle 9.1+ istiyor. 2.60.1 bu tabanın üstündedir; projede Gradle 9.6.1 var. AGP 9 + `enableJetifier` çakışması bu projede yok (Jetifier kapalı).
- **KSP 2.3.13 ↔ Kotlin 2.4.21:** KSP 2.3.x bağımsız sürümleme kullanır. 2.3.13, `kotlin-stdlib:2.4.0`'a karşı derlenmiş ve 30 Ekim 2026'da yayınlanmıştır; Kotlin 2.4.21 aynı 2.4 hattındadır. Kesin doğrulama A1/A3 derlemeleridir. "ksp-2.3.13 is too old for kotlin-2.4.21" benzeri hata çıkarsa daha yeni KSP olmadığı için durulur ve kullanıcıya sorulur.
- Not: kotlinlang.org KSP uyumluluk sayfası hâlâ 2.3.10'u "en yeni" gösteriyor; Maven Central ve GitHub 2.3.11–2.3.13'ü listeliyor. Sayfa güncel değil; kaynak olarak Maven Central ve GitHub sürüm notları kullanıldı.

## 3. Lint kapısı tasarımı

### Rapor yolları (ölçülerek doğrulandı)

Her modülde `<modül>/build/reports/lint-results-debug.xml`. Ölçüm: `:app`, `:core:data`, `:core:prayertimes`, `:widget` ve altı `:feature:*` modülü. `:core:domain` saf JVM modülü olduğu için `lintDebug` görevi yoktur.

Android convention plugin'lerinde `lint { xmlReport = true }` açıkça ayarlanır (kapının rapora bağımlı olduğu kodda görünsün diye).

### Bilinen liste

Kök `build.gradle.kts`'te, M-08 haritasının yanında:

```kotlin
// K-01 — mimari-ve-kodlama-ilkeleri.md §7 bilinen lint uyarıları tablosu.
// Liste değişikliği planlama kararıdır: önce belge, sonra bu harita.
val k01KnownLintWarnings: Map<Pair<String, String>, Int> = mapOf(
    ("ObsoleteLintCustomCheck" to ":feature:calendar") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:kaza") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:mazeret") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:onboarding") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:settings") to 1,
    ("ObsoleteLintCustomCheck" to ":feature:today") to 1,
)

// K-01 1.5 — sürüm güncelliği denetimleri zamana ve ağa bağlıdır; kapının
// karşılaştırmasına alınmaz. Lint'te kapatılmaz, raporlarda görünür.
val k01OutOfScopeLintIds: Set<String> = setOf(
    "GradleDependency",
    "NewerVersionAvailable",
    "AndroidGradlePluginVersion",
)
```

### `checkLintWarnings` görevi

- Kök projede kaydedilir; `lintDebug` görevi olan bütün alt projelerin `lintDebug`'ına bağlanır.
- Her rapor XML'inden `severity="Warning"` sorunlar `(kimlik, modül)` bazında sayılır. `k01OutOfScopeLintIds` içindeki kimlikler sayıma girmez.
- Karşılaştırma iki yönlüdür:
  - listesiz/fazla çift → *yeni uyarı* mesajı (kimlik, modül, dosya:satır);
  - listede olup eksik/olmayan çift → *eskimiş istisna* mesajı;
  - rapor dosyası yoksa → *rapor yok* hatası.
- Bütün farklar tek mesajda toplanır, sonra `GradleException` fırlatılır.

Mesaj örnekleri (kart B1'deki biçim):

```
Lint kapısı: yeni uyarı UnusedResources (:app) app/src/main/res/values/x.xml:3.
Uyarıyı düzelt; bilinçli bir istisnaysa mimari K-01 tablosuna eklenmesi için planlamaya sor.
```

```
Lint kapısı: bilinen uyarı ObsoleteLintCustomCheck (:feature:kaza) artık görünmüyor.
Mimari K-01 tablosundan ve bu listeden çıkarılması için planlamaya bildir.
```

### Negatif denemeler (commit edilmez)

1. `app/src/main/res/values/strings.xml`'e kullanılmayan string → yeni uyarı.
2. Bilinen listeden `:feature:kaza` satırını sil → yeni uyarı.
3. Bilinen listeye `RedundantLabel, :app` çifti ekle → eskimiş istisna.

Her denemeden sonra değişiklik geri alınır; mesajlar rapora yazılır.

## 4. CI tasarımı

Dosya: `.github/workflows/ci.yml`

- `name: CI`; tetikleyiciler `pull_request` (main), `push` (main), `workflow_dispatch`.
- `permissions: contents: read`; `concurrency` aynı dal/PR için eskisini iptal eder.
- İş kimliği `ci-check`, görünen ad **`ciCheck`**, `runs-on: ubuntu-24.04`, `timeout-minutes: 45`.
- Adımlar:
  1. `actions/checkout` (SHA sabitli).
  2. `actions/setup-java` — Temurin, JDK **21** (`gradle-daemon-jvm.properties` → `toolchainVersion=21`).
  3. `gradle/actions/setup-gradle` — önbellek ve wrapper doğrulaması.
  4. `sdkmanager --install "platforms;android-37.1"` — yerelde SDK dizini `android-37.1` olarak doğrulandı. AGP 9.5 yerelde `build-tools 36.0.0` kullanıyor; runner'da yoksa lisanslar kabul edilmiş olduğundan AGP kendisi indirir.
  5. `./gradlew ciCheck --stacktrace`.
  6. Yalnızca başarısızlıkta `actions/upload-artifact` ile `**/build/reports/**`, 7 gün.
- Action'lar tam commit SHA'sına sabitlenir; yanına sürüm etiketi yorum olarak yazılır.

YAML doğrulaması: makinede Python yok, Node 24 var; `npx --yes js-yaml .github/workflows/ci.yml` ile sözdizimi doğrulanır. Ardından iş akışının çalıştıracağı komut (`.\gradlew.bat ciCheck`) yerelde koşulur.

## 5. `ciCheck` tasarımı

Kök projede `group = "verification"`, Türkçe `description`. Bağımlılıklar **açıkça** yazılır:

- bütün projelerin `ktlintCheck` görevleri (kök, alt projeler ve `build-logic`);
- bütün alt projelerin `test` görevleri;
- `checkLintWarnings`;
- `:app:assembleDebug`.

`ciCheck --dry-run` çıktısında alt projelerin `ktlintCheck` görevlerinin göründüğü doğrulanır.

## 6. Commit sırası

1. `docs: add F1-04 card and update planning docs`
2. `docs(tasks): add F1-04 plan`
3. `build: add Hilt and KSP to version catalog`
4. `build: add namaztakibi.android.hilt convention plugin`
5. `feat(app): set up Hilt application and entry point`
6. `build: add lint warning gate (K-01)`
7. `build: add ciCheck aggregate task`
8. `build: make gradlew executable and pin line endings`
9. `ci: add GitHub Actions workflow`
10. `docs: document ciCheck and CI in README`
11. `docs(tasks): add F1-04 report`

Push ve PR yapılmaz; `main`'e dokunulmaz.

## Ölçülen durum (Adım 0)

- `lintDebug`: yapı başarılı; XML raporları doğrulandı.
- Temiz derleme dahil 3 çalıştırmada `AndroidGradlePluginVersion` **görünmedi**; yalnızca altı feature modülünde birer `ObsoleteLintCustomCheck` var (toplam 6).
- Bu ölçüm, K-01'in 1.5'e güncellenmesine ve sürüm güncelliği denetimlerinin kapı dışına alınmasına yol açtı.

## Varsayımlar

- Action'larda en yeni kararlı major sürümler kullanılır (onaylı).
- `gradlew` izni `git update-index --chmod=+x` ile verilir; `.gitattributes` sonrası `git status` kontrol edilir, gerekirse `git add --renormalize` kullanılır.
- `.gitattributes` yalnızca kartta istenen dört deseni içerir.

# Görev Kartı F1-04 — Hilt, CI, lint kapısı ve `main` koruması

Faz 1 · Kart 4/5 · 1 Kasım 2026 (Adım 0 sonrası güncelleme: sürüm güncelliği denetimleri kapı dışında) · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek) ve kullanıcı (GitHub ayarları)

## Amaç

Bu kartın sonunda:

- **Hilt** (bağımlılık enjeksiyonu) KSP ile kurulu. `namaztakibi.android.hilt` convention plugin'i hazır, ama Faz 1'de yalnızca `:app` kullanıyor.
- **Tek komutla kalite kontrolü:** `.\gradlew.bat ciCheck` sırasıyla ktlint, birim testleri, Android Lint, lint kapısı ve derlemeyi çalıştırır. Yerel ve CI aynı komutu kullanır.
- **Lint kapısı** iki yönlü çalışır. Mimari K-01'deki bilinen uyarılar listesinde olmayan **yeni** bir uyarı derlemeyi kırar; listede olup artık **görünmeyen** bir uyarı da kırar.
- **GitHub Actions CI:** Her Pull Request'te ve `main`'e her birleştirmede `ciCheck` çalıştırır.
- **`main` korunur:** Doğrudan push yapılamaz; birleştirme yalnızca PR ile ve CI yeşilken olur. Bu adımı kullanıcı GitHub'da yapar (Adım E).

**Neden şimdi?** Faz 2'de kural motoru TDD ile yazılacak ve 100'den fazla test eklenecek. O noktadan sonra her PR'ın otomatik doğrulanması gerekiyor. "Bende çalışıyordu" ile "CI'da çalışıyor" arasındaki farkı en ucuz, kod henüz yokken kapatırız.

## Ön koşullar

- `main`, F1-03'ün birleştirildiği `2f1fe94` commit'inde ya da daha ilerde.
- Kullanıcı şu dosyaları çalışma ağacına koymuş olmalı:
  - `CLAUDE.md` (sürüm 1.4)
  - `docs/mimari-ve-kodlama-ilkeleri.md` (sürüm 1.4)
  - `docs/gelistirme-yol-haritasi.md` (F1-03 tamamlandı güncellemesi)
  - `docs/tasks/F1-04-hilt-ci.md` (bu kart)
- Çalışma ağacında başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler

- `CLAUDE.md` (tamamı; özellikle yeni "Plan dosyası" ve "`main` koruması" bölümleri ile Bölüm 10'daki `ciCheck` ve lint kapısı)
- `docs/mimari-ve-kodlama-ilkeleri.md`: M-07 ve K-01 (bilinen lint uyarıları tablosu)
- `docs/tasks/F1-03-rapor.md`: lint bölümü

## Adım 0 — Plan ve onay

Bu adımda dosya okuyabilir, internette sürüm notlarına ve resmi belgelere bakabilir ve `.\gradlew.bat lintDebug` çalıştırabilirsin. Hiçbir dosyayı değiştirme. Eski sohbet kayıtlarını (`~/.claude` altı) **okuma.**

Plana şunları yaz, sonra **kullanıcının onayını bekle:**

1. **Sürüm tablosu:** Dagger/Hilt, KSP ve kullanacağın her GitHub Action (`actions/checkout`, `actions/setup-java`, `gradle/actions/setup-gradle`, `actions/upload-artifact`) için hedef sürüm ve kaynak. Action'lar için sürüm etiketinin karşılık geldiği **tam commit SHA'sı** da yazılır (bkz. C2).
2. **Uyumluluk:**
   - Hilt Gradle eklentisinin AGP 9.5 ile uyumu. Dagger 2.59'dan beri Hilt eklentisi AGP 9 istiyor; seçilen sürümün AGP 9.5'i desteklediğini sürüm notlarından doğrula.
   - KSP'nin Kotlin 2.4.21 ile uyumu. KSP sürüm numaraları artık Kotlin sürümünden bağımsız olabilir; uyumluluğu KSP sürüm notlarından doğrula.
3. **Lint kapısı tasarımı:** Raporların okunacağı dosya yolları, bilinen uyarı listesinin koddaki biçimi, karşılaştırma kuralı (bkz. B1) ve hata mesajı örnekleri.
4. **CI tasarımı:** İş akışı dosyasının iskeleti, runner imajı, JDK sürümü ve Android SDK platformunun nasıl kurulacağı.
5. **Commit sırası** (bkz. Adım D).

Onaydan sonra planı, onay mesajındaki değişikliklerle birlikte `docs/tasks/F1-04-plan.md` dosyasına yaz (`CLAUDE.md` "Plan dosyası").

## Bölüm A — Hilt

Dal: `main`'den `feat/f1-04-hilt-ci`. İlk commit, kullanıcının koyduğu dört belgedir: `docs: add F1-04 card and update planning docs`. İkinci commit plan dosyasıdır: `docs(tasks): add F1-04 plan`.

Bölüm A ve B'de her adımdan sonra `.\gradlew.bat assembleDebug` ve `.\gradlew.bat test` çalıştır; ikisi de yeşilse commit at.

### A1 — Catalog

1. `[versions]`: `hilt` ve `ksp`.
2. `[libraries]`:
   - `hilt-android` (`com.google.dagger:hilt-android`)
   - `hilt-compiler` (`com.google.dagger:hilt-compiler`). KSP ile kullanılan derleyici budur; kapt'a özgü `hilt-android-compiler` kullanılmaz.
   - `build-logic` için `compileOnly` eklenti artefaktları: `hilt-gradlePlugin` (`com.google.dagger:hilt-android-gradle-plugin`) ve `ksp-gradlePlugin` (`com.google.devtools.ksp:symbol-processing-gradle-plugin`).
3. `[plugins]`: `hilt` (`com.google.dagger.hilt.android`), `ksp` (`com.google.devtools.ksp`) ve sürümsüz `namaztakibi-android-hilt`.
4. Kök `build.gradle.kts`'e `hilt` ve `ksp` için `apply false` satırlarını ekle. Bu satırlar, `compileOnly` alınan eklentileri derleme sınıf yoluna getirir; F1-02'deki tuzak 8 ile aynı neden.

Commit: `build: add Hilt and KSP to version catalog`

### A2 — `namaztakibi.android.hilt` convention plugin'i

1. `build-logic/convention`'a `compileOnly(libs.hilt.gradlePlugin)` ve `compileOnly(libs.ksp.gradlePlugin)` ekle.
2. `AndroidHiltConventionPlugin.kt`:
   - `com.google.devtools.ksp` ve `com.google.dagger.hilt.android` eklentilerini uygular.
   - `implementation(hilt-android)` ve `ksp(hilt-compiler)` bağımlılıklarını ekler.
   - Başına mimari M-07'ye atıf yapan bir yorum ekle.
3. Plugin'i `namaztakibi.android.hilt` kimliğiyle kaydet.
4. Bu plugin bir Android plugin'i **uygulamaz.** Yalnızca `namaztakibi.android.application` ya da `namaztakibi.android.library` uygulanmış bir modülde kullanılır. Bu koşul sağlanmadıysa anlamlı bir mesajla `GradleException` fırlat.

Commit: `build: add namaztakibi.android.hilt convention plugin`

### A3 — Uygulamada Hilt

1. `:app`'e `alias(libs.plugins.namaztakibi.android.hilt)` ekle.
2. `com.eceakin.namaztakibi.NamazTakibiApplication` sınıfını `@HiltAndroidApp` ile oluştur. Gövdesi boş kalır.
3. `AndroidManifest.xml`'de `<application android:name=".NamazTakibiApplication" ...>`.
4. `MainActivity`'ye `@AndroidEntryPoint` ekle. Başka hiçbir değişiklik yapma.
5. **Doğrulama:** `assembleDebug` sonrası KSP'nin ürettiği `Hilt_NamazTakibiApplication` sınıfının oluştuğunu `app/build/generated/ksp/` altında kontrol et ve rapora yaz.

**Kapsam sınırı:** `@Module`, `@Provides`, `@Inject`, `@HiltViewModel` ya da başka bir enjeksiyon ekleme. Hilt'in gerçek kullanımı Faz 2'den sonra başlar (P-03).

Commit: `feat(app): set up Hilt application and entry point`

## Bölüm B — Lint kapısı ve `ciCheck`

### B1 — Lint kapısı (`checkLintWarnings`)

1. **XML raporları:** Lint'in XML raporu ürettiğinden emin ol. Android convention plugin'lerinde `lint { xmlReport = true }` olarak açıkça ayarla, ki kapının bu rapora bağımlı olduğu kodda görünsün. Raporun gerçek dosya yolunu (`build/reports/lint-results-debug.xml` olması beklenir) doğrula.
2. **Bilinen uyarılar listesi:** Kök `build.gradle.kts`'te, M-08 haritasının yanında, mimari K-01'deki tabloyu tek bir harita olarak tut. Anahtar `(lint kimliği, modül yolu)`, değer beklenen adettir. Başına K-01'e atıf yapan ve "liste değişikliği planlama kararıdır" diyen bir yorum yaz. Bugünkü içerik:
   - `ObsoleteLintCustomCheck`: altı `:feature:*` modülünün her birinde 1

   **Kapı dışı kimlikler:** `GradleDependency`, `NewerVersionAvailable` ve `AndroidGradlePluginVersion` karşılaştırmaya alınmaz (mimari K-01 1.5: sürüm güncelliği denetimleri zamana bağlıdır). Bu üç kimliği kodda ayrı, adlandırılmış bir küme olarak tut ve gerekçesini yorumla yaz. Lint'te kapatma; raporlarda görünmeye devam etsinler.
3. **`checkLintWarnings` görevi:**
   - Kök projede kaydedilir.
   - `lintDebug` görevi olan bütün alt projelerin `lintDebug`'ına bağlıdır.
   - Her modülün XML raporunu okur ve `severity` değeri `Warning` olan sorunları `(kimlik, modül)` bazında sayar. Hatalar zaten lint'i kırar.
   - Sonucu bilinen listeyle karşılaştırır:
     - **Yeni ya da fazla uyarı:** Listede olmayan bir çift ya da beklenenden fazla adet. Mesaj kimliği, modülü, dosya ve satırı içerir. Örnek: `Lint kapısı: yeni uyarı UnusedResources (:app) app/src/main/res/values/x.xml:3. Uyarıyı düzelt; bilinçli bir istisnaysa mimari K-01 tablosuna eklenmesi için planlamaya sor.`
     - **Eskimiş istisna:** Listede olup raporda görünmeyen ya da beklenenden az olan çift. Örnek: `Lint kapısı: bilinen uyarı ObsoleteLintCustomCheck (:feature:kaza) artık görünmüyor. Mimari K-01 tablosundan ve bu listeden çıkarılması için planlamaya bildir.`
     - Bir modülün raporu yoksa da başarısız olur; lint çalışmamış demektir.
   - Bütün farkları tek mesajda listeler, sonra `GradleException` fırlatır.
4. **Negatif denemeler (commit edilmez):** Her birini tek tek yap, `.\gradlew.bat checkLintWarnings` ile kapının anlamlı mesajla durduğunu gör, değişikliği geri al:
   - `app/src/main/res/values/strings.xml`'e kullanılmayan bir string ekle → yeni uyarı.
   - Bilinen listeden `:feature:kaza` satırını sil → yeni uyarı.
   - Bilinen listeye uydurma bir çift ekle (ör. `RedundantLabel`, `:app`) → eskimiş istisna.

   Mesajları rapora yaz. Sonunda `git status` temiz olmalı (A ve B'nin commit'leri hariç).

Commit: `build: add lint warning gate (K-01)`

### B2 — `ciCheck` toplu görevi

1. Kök projede `ciCheck` görevini kaydet. Şunlara bağlıdır:
   - **Bütün projelerin** `ktlintCheck` görevleri (kök, bütün alt projeler ve `build-logic`). Dikkat: kök görevin içinde `dependsOn("ktlintCheck")` yazmak yalnızca kökün kendi görevine bağlanır; komut satırındaki gibi alt projeleri kapsamaz. Her projenin görevine açıkça bağla ve `.\gradlew.bat ciCheck --dry-run` çıktısında alt projelerin `ktlintCheck` görevlerinin listelendiğini doğrula,
   - bütün alt projelerin `test` görevleri,
   - `checkLintWarnings` (lint'i de çalıştırır),
   - `:app:assembleDebug`.
2. Görevin `description` alanına ne yaptığını Türkçe yaz. `.\gradlew.bat tasks` çıktısında görünmesi için `group = "verification"` ver.
3. `.\gradlew.bat ciCheck` yerelde yeşil olmalı.

Commit: `build: add ciCheck aggregate task`

## Bölüm C — GitHub Actions

### C1 — Linux uyumluluğu

1. **Çalıştırılabilir `gradlew`:** `gradlew` git'te çalıştırılabilir olarak işaretli değil (mod `100644`). Linux runner'da "Permission denied" verir. `git update-index --chmod=+x gradlew` ile düzelt ve `git ls-files -s gradlew` çıktısında `100755` gör.
2. **Satır sonları:** Kök dizine `.gitattributes` ekle:
   - `gradlew` ve `*.sh` → `text eol=lf`
   - `*.bat` → `text eol=crlf`
   - `*.jar` → `binary`

   Windows'ta CRLF'e dönüşmüş bir `gradlew` Linux'ta çalışmaz.

Commit: `build: make gradlew executable and pin line endings`

### C2 — İş akışı: `.github/workflows/ci.yml`

1. **Adı ve tetikleyiciler:**
   - `name: CI`
   - `pull_request` (hedef `main`), `push` (`main`), `workflow_dispatch`.
2. **Güvenlik ve düzen:**
   - `permissions: contents: read`.
   - `concurrency`: aynı dal ya da PR için yeni bir çalışma eskisini iptal eder.
3. **İş:**
   - İş kimliği `ci-check`, görünen adı **`ciCheck`**. Bu ad Adım E'deki zorunlu durum kontrolünde kullanılacak; değiştirme.
   - `runs-on`: `-latest` değil, açık bir Ubuntu LTS imaj etiketi (ör. `ubuntu-24.04`). Planda belirt.
   - `timeout-minutes: 45`.
4. **Adımlar:**
   1. `actions/checkout`.
   2. `actions/setup-java`: Temurin. Sürüm, `gradle/gradle-daemon-jvm.properties`'teki `toolchainVersion` ile aynı olsun (bugün 21); aksi halde Gradle daemon için ayrıca JDK indirir.
   3. `gradle/actions/setup-gradle`: Gradle önbelleği ve wrapper doğrulaması.
   4. **Android SDK platformu:** Runner'da Android SDK var, ama API 37.1 platformu kurulu olmayabilir. `sdkmanager` ile açıkça kur. Paket adını yerelde doğrula: Android SDK'nın `platforms` klasöründeki dizin adı (ör. `android-37.1`), `platforms;android-37.1` paket adına karşılık gelir.
   5. `./gradlew ciCheck --stacktrace`.
   6. **Yalnızca başarısızlıkta** `actions/upload-artifact` ile `**/build/reports/**` dosyalarını yükle; saklama süresi 7 gün.
5. **Action sabitleme:** Her action, tam commit SHA'sına sabitlenir; yanına sürüm etiketi yorum olarak yazılır, ör. `uses: actions/checkout@<40 karakterlik SHA> # v5.0.0`. SHA'yı action'ın resmi GitHub deposundaki etiketten doğrula; hafızandan yazma. Gerekçe: etiketler yeniden yazılabilir, SHA yazılamaz.
6. **Yerel deneme:** CI'ı yerelde çalıştıramazsın. Bunun yerine YAML'ın sözdizimini doğrula (ör. Python ile yükleyerek) ve iş akışının çalıştıracağı komutu (`.\gradlew.bat ciCheck`) yerelde çalıştır.

Commit: `ci: add GitHub Actions workflow`

### C3 — README

README'deki "Derleme" bölümüne `ciCheck`'i ve CI'ın her PR'da bu komutu çalıştırdığını ekle. "Çalışma akışı" bölümüne `main`'in korunduğunu ve birleştirmenin yalnızca yeşil CI ile mümkün olduğunu yaz. Rozet ekleme.

Commit: `docs: document ciCheck and CI in README`

## Adım D — Son doğrulama, commit'ler ve rapor

Aşağıdakileri çalıştır ve sonuçları rapora özetle:

```
.\gradlew.bat clean
.\gradlew.bat ciCheck
.\gradlew.bat help
git ls-files -s gradlew
```

Beklenenler:

- `ciCheck` yeşil.
- `:core:domain` 2 test, `:app` 1 test.
- Lint kapısı: bilinen 6 uyarı eşleşti, fark yok. Kapı dışı kimliklerden rapora düşen olduysa sayısı rapora yazılır.
- M-08 hâlâ çalışıyor (`help` hatasız). Hilt yeni bir modül bağımlılığı eklemediği için denetim değişmemeli.
- `gradlew` modu `100755`.

Commit sırası:

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

Raporu `docs/tasks/F1-04-rapor.md` dosyasına, `CLAUDE.md` Bölüm 11'e göre yaz ve son commit olarak ekle. Ek olarak şunları belirt:

- Sürüm tablosu ve action SHA'ları.
- Üretilen Hilt sınıfının yolu.
- Lint kapısının üç negatif denemesinin mesajları.
- Kullanıcı için Adım E'nin kısa özeti.

**Push etme, PR açma.** Kullanıcı yapacak.

## Adım E — Kullanıcının GitHub'da yapacakları

Raporun sonunda kullanıcıya bu adımları hatırlat; bunları sen yapamazsın. Planlama tarafı kullanıcıya ayrıca adım adım yardım edecek.

1. **CI'ın ilk çalışması:** Dalı push et ve PR aç. PR sayfasındaki **Checks** bölümünde `ciCheck` çalışır. Yeşil olmalı. Kırmızıysa merge etme; kaydı planlamaya getir.
2. **Uygulamanın açıldığını gör:** Hilt, uygulamanın açılışına bir sınıf ekledi. Android Studio'da uygulamayı bir kez çalıştır; "Hello Android!" görünmeli.
3. **`main` için ruleset (Settings → Rules → Rulesets → New branch ruleset):**
   - Ad: `main-koruma`. Enforcement: **Active**. Target: **Include default branch**.
   - Kurallar:
     - Restrict deletions
     - Block force pushes
     - Require a pull request before merging (gerekli onay sayısı 0; izin verilen birleştirme yöntemi yalnızca **Squash**)
     - Require status checks to pass → `ciCheck` ekle
   - Bypass listesi boş.
4. **Settings → General → Pull Requests:**
   - Yalnızca **Allow squash merging** açık.
   - **Automatically delete head branches** açık.
5. **Birleştir:** PR'ı "Squash and merge" ile birleştir. Ruleset sayesinde bundan sonra her PR'da CI yeşil olmadan birleştirme düğmesi kapalı olacak.

## Bilinen tuzaklar

1. **`kapt` yasak.** Hilt'in eski kurulum rehberleri kapt kullanır. Bu projede yalnızca KSP ve `hilt-compiler` kullanılır.
2. **KSP ve Kotlin uyumu.** Yanlış KSP sürümü "ksp-<sürüm> is too old for kotlin-<sürüm>" benzeri hatalar verir. Sürümü KSP'nin kendi sürüm notlarından seç.
3. **Hilt eklentisi ve AGP.** Hilt'in AGP 9 desteği 2.59 ile geldi. Daha eski bir Hilt sürümü AGP 9.5 ile derlenmez.
4. **Kökteki `apply false` satırları gerekli.** `compileOnly` alınan Hilt ve KSP eklentileri sınıf yoluna bunlarla gelir.
5. **Lint raporunun yolu.** Kapı yanlış dosyayı okursa her modül "rapor yok" der. Yolu gerçek bir çalıştırmadan doğrula.
6. **`gradlew` izni ve satır sonları.** C1 yapılmazsa CI ilk komutta kırılır.
7. **Action etiketleri.** `@v4` gibi etiketler değişebilir; SHA kullan.
8. **Gizleme yasağı sürüyor.** Lint kapısını geçmek için uyarı gizlemek ya da bilinen listeye kendiliğinden ekleme yapmak yasak.

## Kapsam dışı (bu kartta yapma)

- Hilt'i `:app` dışındaki modüllere uygulamak; herhangi bir `@Module`, `@Inject` ya da `@HiltViewModel`.
- CI'da emülatörle çalışan cihaz testleri (instrumented test).
- Release derlemesi, imzalama ve yayın akışları.
- Dependabot ya da otomatik bağımlılık güncellemeleri.
- Gradle configuration cache.
- GitHub ayarlarını değiştirmek ya da `gh` CLI kullanmak; bunlar kullanıcının işi.
- M-08 denetiminin mantığını değiştirmek.

## Kabul kriterleri

**Hilt**

- [ ] Hilt ve KSP sürümleri catalog'da; `kapt` hiçbir yerde yok.
- [ ] `namaztakibi.android.hilt` convention plugin'i var ve yalnızca `:app` kullanıyor; Android plugin'i olmayan modülde anlamlı hata veriyor.
- [ ] `NamazTakibiApplication` (`@HiltAndroidApp`) manifest'te tanımlı; `MainActivity` `@AndroidEntryPoint`. Başka enjeksiyon yok.
- [ ] KSP'nin ürettiği Hilt sınıfı derlemede oluşuyor.

**Lint kapısı ve `ciCheck`**

- [ ] Lint kapısı bilinen listeyi kimlik, modül ve adet bazında karşılaştırıyor; yeni ve eskimiş uyarılarda anlamlı mesajla başarısız oluyor. Üç negatif deneme raporda.
- [ ] Bilinen liste, mimari K-01 tablosuyla birebir aynı.
- [ ] `ciCheck` ktlint, testler, lint kapısı ve derlemeyi tek komutta çalıştırıyor ve yerelde yeşil.

**CI**

- [ ] `gradlew` modu `100755`; `.gitattributes` satır sonlarını sabitliyor.
- [ ] `.github/workflows/ci.yml` var; iş adı `ciCheck`; action'lar SHA'ya sabitli; izinler `contents: read`; başarısızlıkta raporlar yükleniyor.
- [ ] README güncel.

**Genel**

- [ ] Plan `docs/tasks/F1-04-plan.md`'de, rapor `docs/tasks/F1-04-rapor.md`'de.
- [ ] Commit'ler `feat/f1-04-hilt-ci` dalında; `main`'e dokunulmamış.

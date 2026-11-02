# CLAUDE.md — Namaz Takibi (Android)

Bu dosya, bu depoda kod yazan yapay zekâ ajanı için bağlayıcı çalışma talimatıdır. Her oturumun başında baştan sona oku. Buradaki bir kuralla çelişen bir istek gelirse işe başlamadan önce çelişkiyi kullanıcıya bildir.

Sürüm 1.10 · 2 Kasım 2026 · Kaynak: `docs/` altındaki proje belgeleri (SRS 1.0-taslak.12, karar tablosu v1.5, kabul senaryoları 99 senaryo, mimari ilkeler 1.7)

## 0. Oturum başında

Claude Code yeni bir oturumda önceki sohbeti **hatırlamaz.** Nerede kaldığını sohbetten değil depodan öğren:

1. `git status` ve `git log --oneline -10` çalıştır.
2. `docs/tasks/` altındaki en yüksek numaralı görev kartını ve varsa onun `-rapor.md` dosyasını oku.
3. Kullanıcı bir görev vermediyse durumu kısaca özetle ve bekle. Kendiliğinden iş başlatma.

---

## 1. Proje bir bakışta

- **Ne:** Reklamsız, hesapsız, çevrimdışı öncelikli, widget öncelikli bir namaz ve kaza takip uygulaması. Çalışma adı "Namaz Takibi".
- **Kapsam (v1.0):** Yalnızca Android (minSdk 26), yalnızca Hanefî mezhebi, vakit kaynağı Diyanet, Türkçe arayüz.
- **Kalp:** Fıkhi kural motoru. Saf Kotlin, yan etkisiz, yalnızca **öneri** döndürür. Veriyi kullanıcı onayı olmadan asla değiştirmez (BR-00).
- **Geliştirici:** Tek kişi (@ece). C#/.NET geçmişi var, Android'de yeni. Açıklamaların .NET karşılığını vermek faydalı olur (bkz. `docs/mimari-ve-kodlama-ilkeleri.md` Bölüm 10).

## 2. Rol dağılımı

- **Planlama ve gözden geçirme:** Başka bir model, claude.ai projesinde yapar. Görev kartları `docs/tasks/` altına konur.
- **Senin rolün:** Görev kartını uygulamak. Mimari karar vermek, kapsamı genişletmek ya da fıkhi kural yorumlamak senin işin değil.
- **Kartta yazmayan bir karar gerekirse:** En basit, geri alınabilir seçeneği uygula. Seçimi rapordaki "Varsayımlar" bölümüne yaz (Bölüm 11). Seçim veri modelini, fıkhi davranışı ya da modül sınırlarını etkiliyorsa **dur ve sor.**

## 3. Belgeler: tek doğruluk kaynağı

Belgeler `docs/` altındadır. Hepsini her oturumda okuma; görevin gerektirdiğini oku. Belgeler arasında çelişki olursa sıralama:

1. `docs/gereksinim-analizi-srs.md` (SRS) — her zaman esastır.
2. `docs/karar-tablosu.md` — kural motorunun test sözleşmesi (KT-01 – KT-102).
3. `docs/kabul-senaryolari.md` — kullanıcı gözünden davranış (KS-*).
4. `docs/mimari-ve-kodlama-ilkeleri.md` — M-xx, P-xx, K-xx kuralları.
5. `docs/gelistirme-yol-haritasi.md` — fazlar ve "bitti tanımı".
6. `docs/fikhi-gereksinimler.md`, `docs/hocaya-sorular.md` — fıkhi gerekçe ve hoca görüşleri. Yalnızca okumak içindir.
7. `docs/ekran-taslaklari-tur1.md` – `tur5.md` — ekranlar ve mikro metinler (Faz 5).

**Belgeleri asla değiştirme.** Bir belgede hata ya da çelişki bulursan rapora yaz.

### Sık karıştırılanlar

- **ADR yazılmaz.** `docs/adr/` açma (NFR-MNT-03 kaldırıldı).
- **Asr-ı sânî ve işâ-i sânî yoktur.** Hiçbir yerde hesaplama ya da gösterme (BR-06, FR-VKT-05 kaldırıldı).
- **Kerahat dakikaları bir kullanıcı ayarı değildir.** Değer yalnızca `RuleSet`'te durur (FR-AYR-03 kaldırıldı).
- **Gusül aralığı 5–30 dk'dır.** Eski ekran taslaklarında 5–60 dk görebilirsin; o değer eskidir.
- **Varlık alanları için SRS Bölüm 6.1 esastır.**

## 4. Mevcut durum

- **Faz:** Faz 2 (domain modeli ve kural motoru, TDD). Kartlar F2-01 – F2-13 ve F2-04b (14 kart); liste ve sıra `docs/gelistirme-yol-haritasi.md` Faz 2'de. Her kart karar tablosunun belirli satırlarını kapsar. F2-01 (`RuleSet.HANEFI`, `PrayerTimesDay`, `ReferenceDay`, kapsam ve KT kapıları), F2-02 (`domain.times`: vakit aralıkları, şu anki vakit, kerahat pencereleri, namaz günü), F2-03 (`domain.kaza`: kaza defteri) ve F2-04 (`domain.tracking`: günlük işaretleme ve kaçırıldı önerileri) tamamlandı.
- **Faz 1:** F1-01 – F1-04 tamamlandı. F1-05 (widget ve hatırlatma risk prototipi, spike) Faz 2 ile paralel yürür; spike kodu `main`'e girmez (Bölüm 10 "Spike kartları"). Faz 2 kartlarında spike dalındaki koda dokunma.
- **Diyanet API'si:** Başvuru yapıldı, yanıt bekleniyor. Vakit kaynağı bir arayüzün arkasında olduğu için iş beklemez. Gerçek Diyanet istemcisi yazma; sahte (fake) uygulama kullan.
- **Ekran metinleri:** Fıkhi ekran metinleri hoca kontrolünden geçmedi. Faz 5'e kadar `strings.xml`'e fıkhi açıklama metni ekleme. Kural motoru metin değil tipli sonuç döndürür (Bölüm 7, M-25).

## 5. Teknoloji yığını

Kotlin, Jetpack Compose (Material 3), Navigation Compose, Hilt, Room + KSP, DataStore, WorkManager, Jetpack Glance, kotlinx.serialization, Adhan (yalnızca yedek vakit hesabı için). Testlerde JUnit Jupiter (JUnit 6; domain), Robolectric ve Room test yardımcıları (data), Compose UI testleri (feature). Kapsam ölçümü Gradle'ın yerleşik `jacoco` eklentisiyle yapılır (yalnızca `:core:domain`; mimari K-15).

- Bağımlılıklar `gradle/libs.versions.toml` (version catalog) üzerinden eklenir. Sürüm numarası koda yazılmaz.
- **Bu listede olmayan hiçbir kütüphaneyi sormadan ekleme** (P-03). Özellikle şunlar yasaktır: analitik, reklam, çökme raporlama SDK'sı, MVI kütüphanesi (Orbit, Mavericks), mock kütüphanesi (önce fake yaz, K-14), RxJava, Gson, Moshi, Kover, AssertJ, Kotest ve özellik tabanlı test (property-based testing) kütüphaneleri.
- Kütüphane sürümünü hafızandan uydurma. Emin değilsen kullanıcıya sor.
- **Linter:** ktlint (karar: 30 Ekim 2026). detekt ekleme.
- **Paket adı:** `com.eceakin.namaztakibi` (karar: 30 Ekim 2026). Her modülün `namespace`'i bunun altındadır, örneğin `com.eceakin.namaztakibi.feature.kaza`. `applicationId` asla değiştirilmez.
- **Hilt** yalnızca KSP ile kullanılır (`kapt` yasak). Hilt'i bir modüle eklemenin tek yolu `namaztakibi.android.hilt` convention plugin'idir; modül build dosyasına Hilt ya da KSP eklentisini doğrudan yazma.
- **SDK ve JVM değerleri** yalnızca `build-logic/convention/src/main/kotlin/ProjectConfig.kt`'tedir (M-07). Bu değerleri değiştirmek bir planlama kararıdır; görev kartı istemedikçe dokunma.
- **AGP 9 yerleşik Kotlin:** Android modüllerine `org.jetbrains.kotlin.android` eklentisini **uygulama**; AGP 9'da Kotlin derlemesi yerleşiktir ve bu eklenti yeni DSL ile uyumsuzdur. `kapt` da kullanılmaz; gerektiğinde KSP kullanılır.

## 6. Modül yapısı ve bağımlılık kuralları

| Modül | Tür (convention plugin) | İçerik | Bağımlı olabileceği modüller |
| --- | --- | --- | --- |
| `:core:domain` | **Saf Kotlin/JVM** (`namaztakibi.jvm.library`) | Varlıklar, `RuleSet`, kural motoru, repository ve vakit kaynağı arayüzleri, use case'ler | Hiçbiri |
| `:core:data` | Android library (`namaztakibi.android.library`) | Room (ana ve hassas veritabanı), DataStore, repository uygulamaları, yedekleme | `:core:domain` |
| `:core:prayertimes` | Android library (`namaztakibi.android.library`) | Ara sunucu istemcisi, vakit önbelleği, Adhan yedeği | `:core:domain` |
| `:feature:onboarding`, `:feature:today`, `:feature:calendar`, `:feature:kaza`, `:feature:mazeret`, `:feature:settings` | Android library + Compose (`namaztakibi.android.feature`) | Onboarding, Bugün, Takvim/Geçmiş, Kaza, Mazeret, Ayarlar ekranları (Compose + ViewModel) | Yalnızca `:core:domain` |
| `:widget` | Android library (`namaztakibi.android.library`) | Glance widget'ları | `:core:domain`, `:core:data` |
| `:app` | Android application (`namaztakibi.android.application` + `namaztakibi.android.hilt`) | Hilt kurulumu, navigasyon, bildirim ve alarm altyapısı | Hepsi |

Kesin kurallar:

- `:core:domain` içinde `android.*` import'u, `Context`, `LiveData`, Room ya da Hilt anotasyonu **olamaz** (M-02). `javax.inject.Inject` saf Java olduğu için kullanılabilir.
- Feature modülleri birbirine ve `:core:data`'ya bağlanamaz (M-04). Ekranlar arası geçiş yalnızca `:app`'teki navigasyonla olur.
- Repository ve vakit kaynağı **arayüzleri** domain'de, **uygulamaları** data ve prayertimes'tadır (M-03, M-05).
- **Derleme yapılandırması `build-logic/` altındaki convention plugin'lerdedir (M-07).** Bir modülün build dosyasına `compileSdk`, `minSdk`, `targetSdk`, `compileOptions` ya da JVM hedefi yazma; bunlar convention plugin'den gelir. Yeni modül yalnızca uygun convention plugin'le açılır.
- **Bağımlılık kuralları derleme sırasında denetlenir (M-08).** Yukarıdaki tabloyu ihlal eden bir `project(...)` bağımlılığı derlemeyi durdurur. Denetimi kapatma ya da gevşetme; kural değişikliği gerekiyorsa **dur ve sor.**

## 7. Kodlama kuralları (en sık ihlal edilenler)

Tam liste `docs/mimari-ve-kodlama-ilkeleri.md`'de. Aşağıdakiler pazarlıksızdır:

**Zaman**
- Domain'de `LocalDateTime.now()`, `Instant.now()`, `System.currentTimeMillis()` ve `ZoneId.systemDefault()` yasak. Zamana bağlı her fonksiyon `java.time.Clock` alır (M-23).
- Anlar `Instant` + `ZoneId` ile tutulur. Hassasiyet dakikadır.
- **Saat dilimi ilçenindir, saatin değil (M-23, DATA-03).** Takvim günü gerektiren bir fonksiyon (namaz günü gibi) ilçenin `ZoneId`'sini ayrı bir parametre olarak alır. `Clock` yalnızca "şu an" (`clock.instant()`) için kullanılır; `clock.zone` kural hesabında kullanılmaz.
- Vakit sınırları tam dakikadadır; saniyeli bir an (ör. 15:29:59) sınırın hangi tarafında olduğuna göre değerlendirilir, ayrıca yuvarlanmaz.
- Aralıklar **başlangıç dahil, bitiş hariç**: `[başlangıç, bitiş)`.
- **Namaz günü imsakta başlar.** Gece yarısından imsaka kadar önceki namaz günü geçerlidir.

**Kural motoru**
- Saf fonksiyonlardan oluşur, veri yazmaz, öneri döndürür (M-20, BR-00).
- Fıkhi sayılar (72/240/960 saat, 15 gün, 45 dk kerahat, tertip sınırı 6, gusül 5–30, tekbir 1 dk, rekât süresi ve sayıları, bulûğ yaşları vb.) yalnızca `RuleSet`'te durur (M-21, K-06). Ürünün tek kural seti `RuleSet.HANEFI`'dir.
- Beklenen iş sonuçları (vakit girmedi, kerahat vakti, borç yok) istisna değil, `sealed` sonuç tipiyle döner (M-24).
- **Motor sözleşmesi (M-25):** Her kural fonksiyonu ihtiyacı olan her şeyi parametre olarak alır: durum, olay, `Clock`, `RuleSet` ve o günün ayarları. Global, singleton ya da "şu anki ayar" okumaz; böylece her gün kendi ayarıyla değerlendirilir (BR-21).
- **Metin değil tip:** Kural motoru kullanıcıya gösterilecek metin (Türkçe cümle) döndürmez. Bildirim, uyarı ve soru beklentileri tipli sonuçlardır (ör. `CanStillPray(prayer)`); testler metni değil tipi ve değerleri sınar. Metinler Faz 5'te `strings.xml`'e girer.

**Veri**
- Room tek doğruluk kaynağıdır (M-30).
- Kaza borcu bir sayaç değil, **append-only kayıtlardır**. Kalan borç satırlardan hesaplanır, kayıt silinmez; geri alma bir "düzeltme" kaydıdır (M-31, FR-KAZ-05).
- Borç asla negatif olamaz (FR-KAZ-04).
- Kaza defterinin kuralları (tür anlamları, adet, geri alma, elle düzenleme) SRS DATA-04'tedir.
- Günlük kaydın durumu saklanmaz, türetilir; kaçırıldı önerisi de saklanmaz, türetilir (SRS DATA-05).
- Tek eylemle birlikte yazılan kayıtlar ortak bir işlem kimliği taşır; işleme ait bir kayıt tek başına geri alınmaz, işlem bütün olarak geri alınır (SRS DATA-04). Kimlikleri (kayıt ve işlem) motor değil, yazan katman üretir.
- `fallbackToDestructiveMigration` **asla** kullanılmaz. `exportSchema = true` ve şema dosyaları depoya girer (M-33).
- Mazeret verisi ayrı veritabanı dosyasındadır ve otomatik yedeğe girmez (M-34).
- Tüm yazmalar transaction içindedir (NFR-REL-02).

**Kotlin ve sunum katmanı**
- `!!` yasak. Durumlar için `sealed interface`, kimlikler için `value class`, varsayılan olarak `val` (K-03, P-06).
- Her ekranın tek bir değiştirilemez `UiState`'i vardır ve `StateFlow` ile yayınlanır. `MutableStateFlow` dışarı açılmaz (M-10).
- Composable'da iş mantığı olmaz (M-12).
- `GlobalScope` yasak, `runBlocking` yalnızca testlerde kullanılır (P-12).
- Repository'ler main-safe'tir (M-35).
- İş yapmayan use case ya da tek uygulaması olan refleks arayüz açılmaz (M-22, P-05).

**Adlandırma ve yorum**
- Kod İngilizce yazılır. Fıkhi terimler kendi adıyla kalır: `Kaza`, `Mazeret`, `Vitir`, `Hayiz`, `Nifas`, `Istihaze`, `Kerahat`, `Tertip` (K-02). Vakit adları da kendi adıyla kalır: `imsak`, `gunes`, `ogle`, `ikindi`, `aksam`, `yatsi`.
- Kullanıcıya görünen her metin `strings.xml`'de ve Türkçedir. Suçlayıcı dil kullanılmaz (NFR-USA-03, FR-HTR-05).
- Fıkhi kural uygulayan her fonksiyonun yorumunda ilgili kimlik yazar, örneğin `// BR-12, KT-41 – KT-49` (K-05). Yorum "ne"yi değil "neden"i anlatır.

**Gizlilik**
- Mazeret bilgisi widget'ta, bildirimde, kilit ekranında, son uygulamalar önizlemesinde ve bildirim kanal adlarında görünmez. "âdet", "hayız", "mazeret" kelimeleri bu yüzeylerde geçmez (NFR-SEC-03).
- Konum izni istenmez. Ağa yalnızca ilçe kimliği ve ay gider (NFR-SEC-01, NFR-SEC-04).

## 8. Test kuralları

- **Domain'de TDD:** Önce test, sonra kod (K-11). Testi yaz, çalıştır, kırmızı (ya da derlenmeyen) olduğunu gör, sonra kodu yaz. Kırmızı aşamayı rapora bir satırla yaz.
- **Karar tablosu satırları (K-11, K-16):** Her geçerli KT satırı en az bir testle sınanır. Test şu iki biçimden biriyle KT kimliğini taşır:
  1. **Fonksiyon adı** KT kimliğiyle başlar: `fun KT41_BR12_hayiz127SaatteBitti_kalan60dk_ikindiAcilir()`. Kimlikteki rakamlar tablodaki gibi yazılır: `KT01_`, `KT41_`, `KT100_`.
  2. **`@ParameterizedTest` içinde `Arguments.argumentSet`** adı KT kimliğiyle başlar: `argumentSet("KT-19 BR-07 07:00 kerahat başlangıcı", ...)`. Aynı türden satırları (ör. kerahat sınırları) tek tabloda toplamak için bunu kullan.
  Başka bir biçim (yorum, `@DisplayName`, değişken adı) KT kimliği sayılmaz.
- **Diğer test adları da kimlik taşır:** `BR12_...`, `KS_TRK_02_...` (SRS Bölüm 8). Bu adlar büyük harfle başladığı için `.editorconfig`, ktlint'in fonksiyon adı kuralını yalnızca `@Test` ve `@ParameterizedTest` (ve Compose için `@Composable`) fonksiyonlarında atlar. Bu istisnayı genişletme.
- **Sabit referans gün (K-17):** Domain testleri karar tablosundaki fikstürü test kaynaklarındaki `ReferenceDay` yardımcısıyla kullanır: imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30, Europe/Istanbul. G1 = 2 Mart 2026 Pazartesi (G5 cuma). "G6 17:00" testte `ReferenceDay.at(6, "17:00")` olarak yazılır. Testler gerçek tarihe, cihaz saatine ya da sistemin saat dilimine bağlı olamaz.
- **Sınırlar ve değişmezler:** Tam 72. ve 240. saat, vaktin son dakikası, yaz saati geçişi; "borç asla negatif olmaz" gibi değişmezler (K-12). Değişmezler, ek kütüphane kullanmadan parametreli testlerle sınanır.
- **Fake tercih edilir:** Repository'ler için bellek içi fake yaz (K-14).
- **Testi geçirmek için asla şunları yapma:** testi silmek, `@Disabled` eklemek, beklenen değeri koda uydurmak, karar tablosundaki beklentiyi değiştirmek, kapsamı yükseltmek için sınıfları ölçümden hariç tutmak. Kod ile karar tablosu uyuşmuyorsa **kod yanlıştır**. Karar tablosunun kendisi yanlış görünüyorsa dur ve sor.
- **Kapsam kapısı (K-15):** `:core:domain` satır kapsamı en az %90 (NFR-MNT-01). `ciCheck` altında olmazsa başarısız olur.
- **KT izlenebilirlik kapısı (K-16):** `ciCheck`, `docs/karar-tablosu.md`'deki her geçerli KT satırının ya bir testi olduğunu ya da kök `build.gradle.kts`'teki **bekleyen KT listesinde** durduğunu denetler. İki yönlüdür: testi yazılmış bir kimlik listede kalırsa da başarısız olur. Bir Faz 2 kartı, kapsadığı kimlikleri bu listeden siler; kartta yazmayan bir kimliği listeden silme ya da listeye ekleme. Kapı ayrıca `:core:domain` testlerinde `@Disabled` görürse başarısız olur.

## 9. Fıkhi kurallar: kırmızı çizgiler

Bu uygulamada fıkhi bir hata, kullanıcının ibadetini etkiler. Bu yüzden:

- Hiçbir fıkhi kuralı yorumlama, "daha mantıklı" hale getirme ya da genelleme. Kurallar SRS Bölüm 4'teki BR-00 – BR-22 satırları ve karar tablosundaki KT satırlarıdır, o kadar.
- Kural motoru **asla** sessizce borç yazmaz. Belirsiz durumlar (mazeret sınırları, sağlık) otomatik eklemenin kapsamında değildir (BR-00).
- Bir KT satırı iki farklı şekilde okunabiliyorsa ya da hiçbir satırın kapsamadığı bir durumla karşılaşırsan **dur, kodu yazma, kullanıcıya sor.** Tahmin etme.
- İnternetten fıkıh bilgisi arayıp koda uygulama.

## 10. Çalışma akışı

**Git**
- `main`'e doğrudan commit atılmaz. Her görev ayrı ve kısa ömürlü bir dalda yapılır: `feat/<kısa-ad>`, `fix/<kısa-ad>`, `test/<kısa-ad>`, `chore/<kısa-ad>`.
- Commit mesajları İngilizce ve Conventional Commits biçimindedir, örneğin `feat(domain): add kerahat window check (BR-07)`. Küçük ve anlamlı commit'ler at.
- **Belge commit'i yalnızca belge içerir.** `docs:` ve `docs(tasks):` commit'lerine kod, test ya da derleme dosyası girmez; plan ya da rapor yazarken fark ettiğin bir kod düzeltmesi kendi commit'inde (`fix:`, `refactor:`, `style:`) yapılır ve rapora yazılır.
- Dosyaları commit'e **adıyla** ekle (`git add <yol>`). `git add -A`, `git add .` ve `git commit -a` kullanma; çalışma ağacında başka bir kartın dosyaları bulunabilir.
- Birleştirme GitHub'da Pull Request ile ve "Squash and merge" yöntemiyle yapılır. `push` ve PR açma işini kullanıcı istemedikçe yapma.
- `git stash`, `git commit --amend`, `git reset --hard`, `git push --force` ve `main`'e dokunan her işlem yasak; gerekiyorsa dur ve sor.
- Asla commit'leme: `.claude/settings.local.json`, `local.properties`, API anahtarı, keystore, `*.jks`, gizli değişken. Yeni bir dosya türü ekliyorsan `.gitignore`'u kontrol et.

**Komutlar** (Windows; Claude Code'un kabuğu Git Bash ise `./gradlew` da çalışır)
- Derleme: `.\gradlew.bat assembleDebug`
- Domain testleri: `.\gradlew.bat :core:domain:test`
- Domain kapsam raporu: `.\gradlew.bat :core:domain:jacocoTestReport` (HTML: `core/domain/build/reports/jacoco/test/html/index.html`)
- Domain kapsam kapısı: `.\gradlew.bat :core:domain:jacocoTestCoverageVerification`
- KT izlenebilirlik kapısı: `.\gradlew.bat checkKtTraceability`
- Tüm birim testleri: `.\gradlew.bat test`
- Stil: `.\gradlew.bat ktlintCheck` (`build-logic` dahil her şeyi kontrol eder; otomatik düzeltme `ktlintFormat`)
- Android Lint: `.\gradlew.bat lintDebug`
- Modül ağacı: `.\gradlew.bat projects`
- **CI'ın çalıştırdığı tek komut:** `.\gradlew.bat ciCheck` (ktlint, birim testleri, `:core:domain` kapsam kapısı, KT izlenebilirlik kapısı, Android Lint ve lint kapısı, derleme). PR açmadan önce bunu yerelde çalıştır; yerelde yeşilse CI'da da yeşil olmalı.
- **Lint kapısı:** `ciCheck`, bilinen lint uyarıları listesinde (mimari K-01) olmayan **her** yeni uyarıda ve listede olup artık görünmeyen her uyarıda başarısız olur. Listeyi kendiliğinden değiştirme; değişiklik bir planlama kararıdır, **dur ve sor.**

**Bir görevin bitti tanımı**
1. Görev kartındaki kabul kriterlerinin hepsi karşılanmış.
2. Derleme, ilgili testler ve lint yeşil. Bunu **gerçekten çalıştırarak** doğruladın; "çalışması gerekir" yeterli değil.
3. Bölüm 7'deki kurallar ve mimari belgedeki Bölüm 9 kontrol listesi ihlal edilmemiş.
4. Kapsam dışında hiçbir dosya değişmemiş. Fırsatçı refactor yapılmamış.

**Plan dosyası**
- Her kartın Adım 0 planını `docs/tasks/<KART-KİMLİĞİ>-plan.md` dosyasına da yaz. Kullanıcının onay mesajındaki değişiklikleri plana işle ve planı dalın **ilk commit'lerinden biri** olarak ekle: `docs(tasks): add <KART-KİMLİĞİ> plan`.
- Yeni bir oturumda nerede kaldığını bu plan dosyasından ve git geçmişinden öğren; eski sohbet kayıtlarını (`~/.claude` altı) **okuma.**

**Spike (atılabilir prototip) kartları**
- Bazı kartlar ürün kodu değil, bir soruyu ölçümle cevaplayan atılabilir bir prototip ister (ör. F1-05). Böyle bir kart, bu dosyadaki hangi kuralların **o kart için** gevşediğini açıkça listeler. Kartta gevşetildiği yazmayan her kural aynen geçerlidir.
- Spike kodu `main`'e **asla** birleştirilmez. `main`'e yalnızca kart, plan ve sonuç raporu (belgeler) girer.
- Spike'ta ölçüm dürüstlüğü her şeyden önce gelir: bir deneme başarısızsa "başarısız" yaz; sonucu güzelleştirme, eksik ölçümü tahminle doldurma.

**`main` koruması**
- `main` dalı GitHub'da korunur: doğrudan push yapılamaz, birleştirme yalnızca Pull Request ile ve CI yeşilken olur. Bu kuralı aşmaya çalışma.

## 11. Görev sonu raporu

Her görevin sonunda şu biçimde kısa bir rapor yaz. Rapor, gözden geçirmede kullanılır.

- Raporu `docs/tasks/<KART-KİMLİĞİ>-rapor.md` dosyasına yaz (örneğin `docs/tasks/F1-02-rapor.md`) ve dalın **son commit'i** olarak ekle: `docs(tasks): add <KART-KİMLİĞİ> report`. Sohbette yalnızca kısa bir özet ver.
- Raporu yalnızca sohbette bırakma: yeni bir oturum ve gözden geçiren kişi raporu depodan okur.
- Komut çıktılarını raporda **özetle** (sonuç, test sayısı, uyarı sayısı, kapsam yüzdesi, KT kapısının "testli / bekleyen" sayıları). Uzun log yapıştırma.
- **Kapsam dışında kalan kod:** `:core:domain`'de testlerle çalışmayan her satırı ve dalı JaCoCo raporundan (`core/domain/build/reports/jacoco/test/html/`) dosya ve satır olarak rapora yaz ve neden kapsanmadığını bir cümleyle açıkla (ör. Kotlin'in ürettiği kod, ulaşılamayan dal). Kapsam %100 ise "kaçan satır ve dal yok" yaz.

```
## Rapor: <görev kartı adı>
Dal: <dal adı>
Yapılanlar: <madde madde>
Değişen dosyalar: <liste>
Testler: <hangi komut, kaç test, sonuç; :core:domain satır kapsamı>
Karşılanan kimlikler: <BR-xx, KT-xx, KS-xx, FR-xx>
Varsayımlar: <kartta yazmayan ve senin seçtiğin her şey>
Belge tutarsızlıkları: <bulduysan>
Açık sorular: <varsa>
PR başlığı: <tür>(<kapsam>): <açıklama> (<KART-KİMLİĞİ>)
```

- **PR başlığı:** Raporun son satırı, kullanıcının PR'a ve "Squash and merge" penceresine yapıştıracağı başlıktır. Conventional Commits biçiminde, İngilizce, kart kimliğiyle biter; ör. `feat(domain): add prayer time windows (F2-02)`. Squash commit'i `main`'e bu başlıkla girer.

## 12. Dur ve sor (bunlardan biri olursa kodu yazmadan önce sor)

- Bir fıkhi kural belirsiz ya da hiçbir KT satırı durumu kapsamıyor.
- Yeni bir kütüphane, Gradle eklentisi, izin (manifest) ya da ağ çağrısı gerekiyor.
- Room şeması değişiyor. Bu, geçiş (migration) ve testi demektir.
- Modül bağımlılık kuralları (Bölüm 6) ihlal edilmeden görev yapılamıyor.
- Görev kartı bir belgeyle çelişiyor.
- Bekleyen KT listesinde kartın söylemediği bir değişiklik gerekiyor ya da kapsam kapısı ancak sınıfları ölçümden çıkararak geçiyor.
- Çalışma ağacında kartın beklemediği değişiklikler var.
- Aynı hatayı iki denemede çözemedin. Üçüncü kez kör deneme yapma; ne denediğini ve hatayı raporla.

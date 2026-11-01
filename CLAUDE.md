# CLAUDE.md — Namaz Takibi (Android)

Bu dosya, bu depoda kod yazan yapay zekâ ajanı için bağlayıcı çalışma talimatıdır. Her oturumun başında baştan sona oku. Buradaki bir kuralla çelişen bir istek gelirse işe başlamadan önce çelişkiyi kullanıcıya bildir.

Sürüm 1.3 · 1 Kasım 2026 · Kaynak: `docs/` altındaki proje belgeleri (SRS 1.0-taslak.9, karar tablosu v1.5, kabul senaryoları 99 senaryo, mimari ilkeler 1.3)

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

- **Faz:** Faz 1 (mimari ve proje iskeleti). Faz 2 (kural motoru, TDD) bunun hemen ardından gelir.
- **Faz 1 kartları:** F1-01 (paket adı, ktlint, `:core:domain`) ve F1-02 (modüller, convention plugin'ler, depo hijyeni) tamamlandı. Sırada F1-03 (araç zinciri yükseltmesi), F1-04 (Hilt ve CI) ve F1-05 (widget ve hatırlatma risk prototipi) var.
- **Diyanet API'si:** Başvuru yapıldı, yanıt bekleniyor. Vakit kaynağı bir arayüzün arkasında olduğu için iş beklemez. Gerçek Diyanet istemcisi yazma; sahte (fake) uygulama kullan.
- **Ekran metinleri:** Fıkhi ekran metinleri hoca kontrolünden geçmedi. Faz 5'e kadar `strings.xml`'e fıkhi açıklama metni ekleme.

## 5. Teknoloji yığını

Kotlin, Jetpack Compose (Material 3), Navigation Compose, Hilt, Room + KSP, DataStore, WorkManager, Jetpack Glance, kotlinx.serialization, Adhan (yalnızca yedek vakit hesabı için). Testlerde JUnit Jupiter (JUnit 6; domain), Robolectric ve Room test yardımcıları (data), Compose UI testleri (feature).

- Bağımlılıklar `gradle/libs.versions.toml` (version catalog) üzerinden eklenir. Sürüm numarası koda yazılmaz.
- **Bu listede olmayan hiçbir kütüphaneyi sormadan ekleme** (P-03). Özellikle şunlar yasaktır: analitik, reklam, çökme raporlama SDK'sı, MVI kütüphanesi (Orbit, Mavericks), mock kütüphanesi (önce fake yaz, K-14), RxJava, Gson, Moshi.
- Kütüphane sürümünü hafızandan uydurma. Emin değilsen kullanıcıya sor.
- **Linter:** ktlint (karar: 30 Ekim 2026). detekt ekleme.
- **Paket adı:** `com.eceakin.namaztakibi` (karar: 30 Ekim 2026). Her modülün `namespace`'i bunun altındadır, örneğin `com.eceakin.namaztakibi.feature.kaza`. `applicationId` asla değiştirilmez.
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
| `:app` | Android application (`namaztakibi.android.application`) | Hilt kurulumu, navigasyon, bildirim ve alarm altyapısı | Hepsi |

Kesin kurallar:

- `:core:domain` içinde `android.*` import'u, `Context`, `LiveData`, Room ya da Hilt anotasyonu **olamaz** (M-02). `javax.inject.Inject` saf Java olduğu için kullanılabilir.
- Feature modülleri birbirine ve `:core:data`'ya bağlanamaz (M-04). Ekranlar arası geçiş yalnızca `:app`'teki navigasyonla olur.
- Repository ve vakit kaynağı **arayüzleri** domain'de, **uygulamaları** data ve prayertimes'tadır (M-03, M-05).
- **Derleme yapılandırması `build-logic/` altındaki convention plugin'lerdedir (M-07).** Bir modülün build dosyasına `compileSdk`, `minSdk`, `targetSdk`, `compileOptions` ya da JVM hedefi yazma; bunlar convention plugin'den gelir. Yeni modül yalnızca uygun convention plugin'le açılır.
- **Bağımlılık kuralları derleme sırasında denetlenir (M-08).** Yukarıdaki tabloyu ihlal eden bir `project(...)` bağımlılığı derlemeyi durdurur. Denetimi kapatma ya da gevşetme; kural değişikliği gerekiyorsa **dur ve sor.**

## 7. Kodlama kuralları (en sık ihlal edilenler)

Tam liste `docs/mimari-ve-kodlama-ilkeleri.md`'de. Aşağıdakiler pazarlıksızdır:

**Zaman**
- Domain'de `LocalDateTime.now()`, `Instant.now()`, `System.currentTimeMillis()` yasak. Zamana bağlı her fonksiyon `java.time.Clock` alır (M-23).
- Anlar `Instant` + `ZoneId` ile tutulur. Hassasiyet dakikadır.
- Aralıklar **başlangıç dahil, bitiş hariç**: `[başlangıç, bitiş)`.
- **Namaz günü imsakta başlar.** Gece yarısından imsaka kadar önceki namaz günü geçerlidir.

**Kural motoru**
- Saf fonksiyonlardan oluşur, veri yazmaz, öneri döndürür (M-20, BR-00).
- Fıkhi sayılar (72/240/960 saat, 15 gün, 45 dk kerahat, tertip sınırı 6, gusül 5–30, tekbir 1 dk, rekât süresi 2 dk vb.) yalnızca `RuleSet`'te durur (M-21, K-06).
- Beklenen iş sonuçları (vakit girmedi, kerahat vakti, borç yok) istisna değil, `sealed` sonuç tipiyle döner (M-24).

**Veri**
- Room tek doğruluk kaynağıdır (M-30).
- Kaza borcu bir sayaç değil, **append-only kayıtlardır**. Kalan borç satırlardan hesaplanır, kayıt silinmez; geri alma bir "düzeltme" kaydıdır (M-31, FR-KAZ-05).
- Borç asla negatif olamaz (FR-KAZ-04).
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
- Kod İngilizce yazılır. Fıkhi terimler kendi adıyla kalır: `Kaza`, `Mazeret`, `Vitir`, `Hayiz`, `Nifas`, `Istihaze`, `Kerahat`, `Tertip` (K-02).
- Kullanıcıya görünen her metin `strings.xml`'de ve Türkçedir. Suçlayıcı dil kullanılmaz (NFR-USA-03, FR-HTR-05).
- Fıkhi kural uygulayan her fonksiyonun yorumunda ilgili kimlik yazar, örneğin `// BR-12, KT-41 – KT-49` (K-05). Yorum "ne"yi değil "neden"i anlatır.

**Gizlilik**
- Mazeret bilgisi widget'ta, bildirimde, kilit ekranında, son uygulamalar önizlemesinde ve bildirim kanal adlarında görünmez. "âdet", "hayız", "mazeret" kelimeleri bu yüzeylerde geçmez (NFR-SEC-03).
- Konum izni istenmez. Ağa yalnızca ilçe kimliği ve ay gider (NFR-SEC-01, NFR-SEC-04).

## 8. Test kuralları

- **Domain'de TDD:** Önce test, sonra kod (K-11). Karar tablosunun her satırı parametreli bir testtir.
- **Test adı kimlik taşır:** `KT41_BR12_hayiz127SaatteBitti_kalan60dk_ikindiAcilir`, `KS_TRK_02_...`, `BR12_...` (SRS Bölüm 8). Bu adlar büyük harfle başladığı için `.editorconfig`, ktlint'in fonksiyon adı kuralını yalnızca `@Test` ve `@ParameterizedTest` (ve Compose için `@Composable`) fonksiyonlarında atlar. Bu istisnayı genişletme.
- **Sabit referans gün:** Domain testleri karar tablosundaki fikstürü kullanır. Imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30, Europe/Istanbul. Testler gerçek tarihe ya da cihaz saatine bağlı olamaz.
- **Sınırlar ve değişmezler:** Tam 72. ve 240. saat, vaktin son dakikası, yaz saati geçişi; "borç asla negatif olmaz" gibi değişmezler (K-12).
- **Fake tercih edilir:** Repository'ler için bellek içi fake yaz (K-14).
- **Testi geçirmek için asla şunları yapma:** testi silmek, `@Disabled`/`@Ignore` eklemek, beklenen değeri koda uydurmak, karar tablosundaki beklentiyi değiştirmek. Kod ile karar tablosu uyuşmuyorsa **kod yanlıştır**. Karar tablosunun kendisi yanlış görünüyorsa dur ve sor.
- **Kapsam hedefi:** `:core:domain` için en az %90 (NFR-MNT-01).

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
- Birleştirme GitHub'da Pull Request ile ve "Squash and merge" yöntemiyle yapılır. `push` ve PR açma işini kullanıcı istemedikçe yapma.
- Asla commit'leme: `.claude/settings.local.json`, `local.properties`, API anahtarı, keystore, `*.jks`, gizli değişken. Yeni bir dosya türü ekliyorsan `.gitignore`'u kontrol et.

**Komutlar** (Windows; Claude Code'un kabuğu Git Bash ise `./gradlew` da çalışır)
- Derleme: `.\gradlew.bat assembleDebug`
- Domain testleri: `.\gradlew.bat :core:domain:test`
- Tüm birim testleri: `.\gradlew.bat test`
- Stil: `.\gradlew.bat ktlintCheck` (`build-logic` dahil her şeyi kontrol eder; otomatik düzeltme `ktlintFormat`)
- Android Lint: `.\gradlew.bat lintDebug`
- Modül ağacı: `.\gradlew.bat projects`

**Bir görevin bitti tanımı**
1. Görev kartındaki kabul kriterlerinin hepsi karşılanmış.
2. Derleme, ilgili testler ve lint yeşil. Bunu **gerçekten çalıştırarak** doğruladın; "çalışması gerekir" yeterli değil.
3. Bölüm 7'deki kurallar ve mimari belgedeki Bölüm 9 kontrol listesi ihlal edilmemiş.
4. Kapsam dışında hiçbir dosya değişmemiş. Fırsatçı refactor yapılmamış.

## 11. Görev sonu raporu

Her görevin sonunda şu biçimde kısa bir rapor yaz. Rapor, gözden geçirmede kullanılır.

- Raporu `docs/tasks/<KART-KİMLİĞİ>-rapor.md` dosyasına yaz (örneğin `docs/tasks/F1-02-rapor.md`) ve dalın **son commit'i** olarak ekle: `docs(tasks): add <KART-KİMLİĞİ> report`. Sohbette yalnızca kısa bir özet ver.
- Raporu yalnızca sohbette bırakma: yeni bir oturum ve gözden geçiren kişi raporu depodan okur.
- Komut çıktılarını raporda **özetle** (sonuç, test sayısı, uyarı sayısı). Uzun log yapıştırma.

```
## Rapor: <görev kartı adı>
Dal: <dal adı>
Yapılanlar: <madde madde>
Değişen dosyalar: <liste>
Testler: <hangi komut, kaç test, sonuç>
Karşılanan kimlikler: <BR-xx, KT-xx, KS-xx, FR-xx>
Varsayımlar: <kartta yazmayan ve senin seçtiğin her şey>
Belge tutarsızlıkları: <bulduysan>
Açık sorular: <varsa>
```

## 12. Dur ve sor (bunlardan biri olursa kodu yazmadan önce sor)

- Bir fıkhi kural belirsiz ya da hiçbir KT satırı durumu kapsamıyor.
- Yeni bir kütüphane, izin (manifest) ya da ağ çağrısı gerekiyor.
- Room şeması değişiyor. Bu, geçiş (migration) ve testi demektir.
- Modül bağımlılık kuralları (Bölüm 6) ihlal edilmeden görev yapılamıyor.
- Görev kartı bir belgeyle çelişiyor.
- Aynı hatayı iki denemede çözemedin. Üçüncü kez kör deneme yapma; ne denediğini ve hatayı raporla.

# Mimari ve Kodlama İlkeleri — Namaz Takibi

29 Ekim 2026 · @ece · Sürüm 1.7 (2 Kasım 2026: M-23'e ilçe saat dilimi kuralı)

## Amaç ve kullanım

Bu belge, kod yazılırken uyulacak mimari kararları ve prensipleri tek yerde toplar. Her geliştirme sohbetinde ve her Pull Request'te referans alınır. Kurallar numaralıdır (M-xx, P-xx, K-xx); bir kuraldan sapılacaksa önce bu belge güncellenir, sonra kod yazılır.

Kaynak belgeler: gelistirme-yol-haritasi.md (Faz 1 modül yapısı), gereksinim-analizi-srs.md, karar-tablosu.md. Kodlama ajanı için özet talimat: CLAUDE.md.

**Özet:** Clean Architecture (saf Kotlin domain, sıkı) + MVVM/UDF sunum katmanı + çevrimdışı öncelikli, Room'u tek doğruluk kaynağı yapan data katmanı. Üstüne SOLID, KISS, YAGNI ve mobile özgü lifecycle/concurrency prensipleri.

## 1. Genel mimari (M)

```
:feature:*  ──►  :core:domain  ◄──  :core:data
(UI)              (saf Kotlin)       (Room, DataStore)
                       ▲
              :core:prayertimes
:widget ──► :core:domain, :core:data
:app    ──► hepsi (DI, navigasyon, alarm altyapısı)
```

- **M-01 — Bağımlılık içe doğru akar.** `:core:domain` hiçbir modüle bağlı değildir. Onion/Clean'deki "core"un karşılığıdır.
- **M-02 — Domain saf Kotlin/JVM modülüdür.** Android modülü olarak açılmaz; böylece Android bağımlılığı derleyici düzeyinde engellenir. Domain'de `Context`, `LiveData`, Room/Hilt annotation'ı ve `android.*` import'u bulunmaz.
- **M-03 — Dependency Inversion.** Repository ve vakit kaynağı *arayüzleri* domain'de, *uygulamaları* `:core:data` ve `:core:prayertimes`'ta durur.
- **M-04 — Feature modülleri birbirine bağlanmaz.** Ekranlar arası geçiş yalnızca `:app`'teki navigasyonla olur. Feature modülleri veriye repository arayüzü üzerinden erişir, `:core:data`'ya doğrudan bağlanmaz.
- **M-05 — Vakit kaynağı değiştirilebilir.** Diyanet ve Adhan, domain'deki tek bir arayüzün arkasındadır; kaynak değişirse yalnızca `:core:prayertimes` etkilenir.
- **M-06 — Bilinçli fark:** Google'ın rehberinde domain katmanı isteğe bağlıdır ve data'ya bağlı olabilir. Bu projede domain merkezdedir, çünkü kalp fıkhi kural motorudur ve karar tablosu Android olmadan test edilmelidir.
- **M-07 — Derleme yapılandırması convention plugin'lerdedir.** Ortak Gradle ayarları (SDK seviyeleri, JVM hedefi, Compose, test altyapısı) `build-logic/` adlı ayrı bir Gradle derlemesindeki convention plugin'lerde tek yerde tanımlanır. Modül build dosyaları yalnızca bir convention plugin uygular ve kendine özgü bağımlılıklarını yazar. Gerekçe: aynı Android yapılandırması dokuz modülde tekrar edecekti (P-04'teki "üçüncü tekrar" eşiğinin çok üstü); JVM hedefinin modüller arasında farklılaşması gibi sessiz hatalar böylece imkânsız hale gelir. Plugin'ler:

  | Plugin | Uygulandığı modüller | Kapsadığı |
  | --- | --- | --- |
  | `namaztakibi.jvm.library` | `:core:domain` | Kotlin/JVM, JVM hedefi, JUnit Jupiter, JaCoCo kapsam raporu ve kapsam kapısı (K-15) |
  | `namaztakibi.android.library` | `:core:data`, `:core:prayertimes`, `:widget` | Android library, SDK seviyeleri, JVM hedefi |
  | `namaztakibi.android.feature` | `:feature:*` | `android.library` + Compose + `:core:domain` bağımlılığı |
  | `namaztakibi.android.application` | `:app` | Android application, SDK seviyeleri, JVM hedefi |
  | `namaztakibi.android.hilt` | Faz 1'de yalnızca `:app` | KSP ve Hilt eklentileri, Hilt bağımlılıkları. `kapt` kullanılmaz |

- **M-08 — Bağımlılık kuralları derleme sırasında denetlenir.** M-01 ve M-04'teki kurallar yalnızca belgede kalmaz: kök Gradle derlemesindeki bir denetim, kurala aykırı her `project(...)` bağımlılığını yapılandırma aşamasında, kuralı ve modülleri adlandıran bir mesajla durdurur. İzin tablosu:

  | Modül | İzin verilen proje bağımlılıkları |
  | --- | --- |
  | `:core:domain` | Yok |
  | `:core:data`, `:core:prayertimes` | `:core:domain` |
  | `:feature:*` | `:core:domain` |
  | `:widget` | `:core:domain`, `:core:data` |
  | `:app` | Hepsi |

  Tabloya yeni bir modül ya da izin eklemek mimari bir karardır: önce bu belge, sonra denetim kodu değişir.
- **M-09 — Modül adları.** Modül adları İngilizce yazılır; fıkhi terimler K-02'deki gibi kendi adlarını korur: `:feature:onboarding`, `:feature:today` (Bugün), `:feature:calendar` (Takvim/Geçmiş), `:feature:kaza`, `:feature:mazeret`, `:feature:settings` (Ayarlar). `namespace` modül yoluyla eşleşir: `com.eceakin.namaztakibi.feature.kaza`.

## 2. Sunum katmanı: MVVM + UDF (M)

- **M-10 — Her ekranın tek bir `UiState`'i vardır.** Değiştirilemez `data class`; ViewModel bunu `StateFlow` ile yayınlar. Dışarıya hiçbir zaman `MutableStateFlow` açılmaz.
- **M-11 — Olaylar `sealed interface` ile tanımlanır** (ör. `OnPrayerMarked`, `OnUndo`). Ekran yalnızca durumu çizer ve olay gönderir.
- **M-12 — Composable'larda iş mantığı yoktur.** Karar, hesaplama, veri erişimi ViewModel veya altındaki katmanlardadır.
- **M-13 — Tek seferlik efektler** (snackbar, navigasyon) da durumun parçası olarak ya da tüketilince temizlenen bir alanla modellenir; kaybolabilen yöntemler kullanılmaz.
- **M-14 — Hazır MVI kütüphanesi kullanılmaz** (Orbit, Mavericks vb.). Kalıp düz Kotlin ile kurulur.
- **M-15 — Bir ViewModel tek ekrana hizmet eder.** "God ViewModel" yok.
- **M-16 — Süreç ölümüne dayanıklılık.** Kullanıcının girdiği ama henüz kaydedilmemiş değerler `SavedStateHandle` ile korunur.

## 3. Domain katmanı (M)

- **M-20 — Kural motoru saf fonksiyonlardan oluşur.** Girdi alır, öneri döndürür; yan etkisi yoktur, veri yazmaz. Kararı kullanıcı verir ("şüphede sor").
- **M-21 — Kural parametreleri `RuleSet`'te durur.** Hanefî sayıları (72/240 saat, 40 gün, 15 gün, tertip 6, rekât sayıları ve süresi, bulûğ yaşları vb.) koda gömülmez; ürünün kural seti `RuleSet.HANEFI`'dir. İleride diğer mezhepler yeni bir `RuleSet` olarak eklenir. `RuleSet` bir sürüm kimliği taşır; gün anlık kopyası (BR-21) hangi kural setiyle değerlendirildiğini bu kimlikle kaydeder.
- **M-22 — Use case yalnızca değer kattığında yazılır.** İçinde tek bir repository çağrısı olan, iş yapmayan use case açılmaz; böyle durumlarda ViewModel doğrudan repository arayüzünü kullanır.
- **M-23 — Zaman dışarıdan verilir.** Her zaman bağımlı fonksiyon `Clock` alır; anlar `Instant` + `ZoneId` ile tutulur. `LocalDateTime.now()`, `ZoneId.systemDefault()` gibi doğrudan saat ve bölge çağrıları domain'de yasaktır. Takvim günü gerektiren hesaplarda (namaz günü, yerel saat) bölge ilçenin saat dilimidir (SRS DATA-03) ve fonksiyona ayrı bir `ZoneId` parametresi olarak verilir; `Clock`'un kendi bölgesi kural hesabında kullanılmaz. Gerekçe: üretimde `Clock` cihazın bölgesini taşır; yurt dışı bir ilçe seçmiş ya da seyahatteki bir kullanıcıda bu, namaz gününü yanlış güne kaydırır.
- **M-24 — Hatalar tiplidir.** Beklenen iş sonuçları (ör. "vakit girmedi", "kerahat vakti") istisna değil, `sealed` sonuç tipleriyle döndürülür. İstisnalar yalnızca gerçekten beklenmeyen durumlar içindir (ör. tutarsız vakit verisiyle model kurmak).
- **M-25 — Kural motoru sözleşmesi.** Karar tablosundaki her satır "ön koşul + olay → beklenen" biçimindedir; motor fonksiyonları da bu biçimi izler:
  - Girdi olarak durumu (kayıtlar, dönemler, borç kayıtları), olayı, `Clock`'u, `RuleSet`'i ve o günün ayarlarını parametre olarak alır. Global, singleton ya da "şu anki ayar" okumaz. Böylece her gün kendi tarihindeki ayarla değerlendirilir (BR-21) ve testler her girdiyi açıkça verir.
  - Çıktı `sealed` bir sonuçtur: izin, engel (gerekçe tipiyle), öneri listesi ya da bilgi notu. Motor kullanıcıya gösterilecek metin döndürmez; metinler Faz 5'te `strings.xml`'e girer (hoca kontrolü, NFR-SEC-03). Testler tipi ve değerleri sınar.
  - Motor veri yazmaz; "şu kayıtları ekle" önerisi döndürür. Yazma Faz 4'teki repository'nin işidir.

## 4. Data katmanı (M)

- **M-30 — Tek doğruluk kaynağı Room'dur.** Ekranlar her zaman Room'dan okur; ağdan gelen veri önce Room'a yazılır, ekran Room'u gözler. Çevrimdışı çalışma bunun doğal sonucudur.
- **M-31 — Kaza "kayıt, sayaç değil".** Her ekleme ve her kılınan kaza ayrı satırdır (append-only); kalan borç satırlardan hesaplanır. Tek bir sayaç güncellenmez.
- **M-32 — Room entity'si ile domain modeli ayrıdır.** Room annotation'ları domain'i kirletmesin diye eşleme data sınırında yapılır. UI modeli yalnızca gerçekten farklı bir şekil gerekiyorsa açılır (mapper patlaması yok).
- **M-33 — Yıkıcı geçiş yoktur.** `fallbackToDestructiveMigration` asla kullanılmaz; şema dosyaları depoda tutulur ve her geçiş testlidir (Faz 4).
- **M-34 — Hassas veri ayrı dosyadadır.** Mazeret verisi ayrı veritabanında, otomatik yedeğin dışında, yalnızca parolalı dışa aktarmada (Faz 4).
- **M-35 — Repository'ler main-safe'tir.** Disk ve ağ işi kendi dispatcher'ında yapılır; çağıran kod thread düşünmez.

## 5. Genel prensipler (P)

- **P-01 — SOLID.** Özellikle SRP (her sınıfın tek değişme nedeni) ve DIP (mimarinin omurgası).
- **P-02 — KISS.** En basit çalışan çözüm seçilir. Tek geliştiricili projede karmaşıklık en büyük maliyettir.
- **P-03 — YAGNI.** P0 dışı özellik, "ileride lazım olur" soyutlaması veya kütüphane eklenmez. Yeni fikir P1/P2 listesine yazılır.
- **P-04 — DRY, dikkatle.** Fıkhi kurallarda benzer görünen kod farklı gerekçelere dayanabilir; erken birleştirilmez. Kural: aynı bilgi üçüncü kez tekrarlanınca soyutla.
- **P-05 — Arayüz yalnızca gerektiğinde.** Katman sınırında veya testte değiştirilecek yerde arayüz açılır; tek uygulaması olan sınıfa refleksle `IFoo` açılmaz.
- **P-06 — Değiştirilemezlik varsayılandır.** `val`, `data class`, salt okunur koleksiyonlar. `var` gerekçeli istisnadır.
- **P-07 — Test edilebilirlik tasarım ölçütüdür.** Test edilmesi zor bir kod, tasarımının gözden geçirilmesi gerektiğine işarettir.
- **P-08 — Güvenli varsayılan.** Belirsizlikte motor sessizce borç yazmaz, veri silmez; kullanıcıya sorar. Her kayıt elle düzeltilebilir.

## 6. Mobile özgü prensipler (P)

- **P-10 — Lifecycle farkındalığı.** Ekran döndürme, arka plana gitme ve süreç ölümü durumu kaybettirmez (M-16).
- **P-11 — Main-safety.** Ana thread'de disk veya ağ işi yapılmaz (M-35).
- **P-12 — Structured concurrency.** Coroutine'ler bir kapsama bağlıdır (`viewModelScope`, `CoroutineWorker`). `GlobalScope` ve `runBlocking` (testler dışında) kullanılmaz.
- **P-13 — Arka plan kısıtlarına saygı.** Sürekli çalışan servis yoktur; periyodik iş WorkManager'la, hatırlatma pencereli alarmla yapılır.
- **P-14 — Göç güvenliği.** Her kullanıcının cihazı farklı bir sürümde olabilir; şema ve dışa aktarma formatı her zaman sürüm taşır.
- **P-15 — Gizlilik varsayılandır.** Kullanıcı verisi cihazdan çıkmaz; ara sunucu yalnızca ilçe kimliği görür. Analitik/reklam SDK'sı eklenmez.

## 7. Kodlama kuralları (K)

- **K-01 — Stil:** Kotlin resmi stil kılavuzu. Linter **ktlint**'tir (30 Ekim 2026 kararı; gerekçe P-02: kurulumu ve kuralları daha basit). ktlint ve Android Lint CI'da zorunludur; ktlint'te hiç uyarı, Android Lint'te aşağıdaki bilinen uyarılar listesi dışında uyarı olmaz. ktlint kuralı kapatılmaz; tek istisna fonksiyon adı kuralıdır: `@Composable` fonksiyonlar büyük harfle başlar, `@Test` ve `@ParameterizedTest` fonksiyonları ise kimlik taşıyan adlar kullanır (`BR01_...`, `KT41_...`; K-13). Bu istisna `.editorconfig`'te tanımlıdır ve genişletilmez.

  **Bilinen lint uyarıları.** Android Lint uyarıları gizlenmez (`lint.xml`, baseline, `@SuppressLint`, `tools:ignore`, `lint { disable(...) }` yasak). Bilinçli bir karardan ya da üçüncü taraf bir hatadan doğan uyarılar aşağıdaki listede tutulur. CI'daki lint kapısı (`ciCheck`) iki yönlü çalışır: listede olmayan **yeni** bir uyarıda ve listede olup artık **görünmeyen** bir uyarıda başarısız olur. Böylece liste her zaman gerçeği yansıtır. Listeyi değiştirmek bir planlama kararıdır: önce bu tablo, sonra kapının kodu değişir.

  | Lint kimliği | Modüller | Neden | Ne zaman kalkar |
  | --- | --- | --- | --- |
  | `ObsoleteLintCustomCheck` | `:feature:calendar`, `:feature:kaza`, `:feature:mazeret`, `:feature:onboarding`, `:feature:settings`, `:feature:today` | `androidx.lifecycle` 2.11.0'ın lint kuralları AGP 9.5'in lint sürümüyle uyumsuz; `UnsafeLifecycleWhenUsage` ve `RepeatOnLifecycleWrongUsage` denetimleri çalışmıyor (F1-03) | Lifecycle ya da AGP'nin uyumsuzluğu gideren sürümüne geçildiğinde. Faz 5'te `repeatOnLifecycle` kullanılmaya başlanmadan önce yeniden kontrol edilir |

  **Sürüm güncelliği denetimleri kapının dışındadır.** `GradleDependency`, `NewerVersionAvailable` ve `AndroidGradlePluginVersion` kodu değil, internetteki güncel sürüm listesini kontrol eder; sonuçları zamana ve ağa bağlıdır. Kapıya dahil edilseler, kodda hiçbir değişiklik yokken yeni bir kütüphane yayımlandığı gün CI kırmızıya dönerdi. Bu yüzden lint kapısı bu üç kimliği karşılaştırmaya almaz. Kapatılmazlar ve gizlenmezler; lint raporlarında görünmeye devam ederler. Sürüm güncelliği her fazın başında elle gözden geçirilir. Bilinçli sürüm kararları ilgili görev raporunda kayıtlıdır (ör. Gradle 9.6.1, F1-03).
- **K-02 — Adlandırma:** Kod İngilizce; fıkhi terimler domain'de kendi adlarıyla kalır (`Kaza`, `Mazeret`, `Vitir`), çevrilmeye zorlanmaz. Vakit adları da kendi adlarıyla kalır (`imsak`, `gunes`, `ogle`, `ikindi`, `aksam`, `yatsi`). Kullanıcıya görünen tüm metinler `strings.xml`'de, Türkçe.
- **K-03 — Kotlin deyimleri:** Durumlar için `sealed interface`, kimlikler için `value class`, null yerine anlamlı tipler. `!!` kullanılmaz.
- **K-04 — Fonksiyonlar küçük ve tek işlidir.** Uzun fonksiyon, bölünmesi gerektiğine işarettir.
- **K-05 — Yorum "neden"i açıklar, "ne"yi değil.** Fıkhi kural uygulayan her fonksiyon, karar tablosundaki ilgili satıra veya SRS'teki BR numarasına referans verir.
- **K-06 — Sihirli sayı yok.** Fıkhi sayılar `RuleSet`'te (M-21), diğer sabitler adlandırılmış sabitlerde. Testlerdeki beklenen değerler bu kuralın dışındadır: test, beklentiyi belgeden olduğu gibi yazar.

## 8. Test ilkeleri (K)

- **K-10 — Test piramidi:** Testlerin çoğu `:core:domain` birim testleri; ardından Room/geçiş entegrasyon testleri; en üstte kritik akışlar için az sayıda UI testi.
- **K-11 — Domain'de TDD.** Karar tablosunun her geçerli satırı en az bir testle sınanır; önce test, sonra kod. Test, KT kimliğini ya fonksiyon adının başında (`KT41_...`) ya da bir `@ParameterizedTest`'in `Arguments.argumentSet` adının başında (`"KT-19 ..."`) taşır. Aynı türden satırlar (ör. bir kuralın sınır dakikaları) tek bir parametreli testte, her satır ayrı bir `argumentSet` olarak toplanabilir. Bu iki biçim dışındaki yazımlar (yorum, `@DisplayName`) KT kimliği sayılmaz (K-16).
- **K-12 — Sınır ve değişmez testleri.** Tam 72./240. saat, vaktin son dakikası, yaz saati geçişi; "borç asla negatif olmaz" gibi değişmezler. Değişmezler ek bir kütüphane olmadan parametreli testlerle sınanır.
- **K-13 — Test adları davranışı anlatır** (ör. "ögle vakti girmeden işaretleme reddedilir").
- **K-14 — Sahte (fake) tercih edilir.** Repository'ler için bellek içi sahte uygulamalar yazılır; mock kütüphanesi yalnızca gerektiğinde.
- **K-15 — Kapsam kapısı.** `:core:domain`'in satır kapsamı JaCoCo ile ölçülür ve %90'ın altına düşerse `ciCheck` başarısız olur (NFR-MNT-01). Eşik `ProjectConfig`'tedir. Sınıf ya da paket ölçümden hariç tutulmaz; kapsam yeterli değilse eksik test yazılır. Dal (branch) kapsamı raporlanır, kapı yalnızca satır kapsamına bakar.
- **K-16 — KT izlenebilirlik kapısı.** `ciCheck`, `docs/karar-tablosu.md`'deki geçerli KT satırlarını (kaldırılmış satırlar hariç) okur ve `:core:domain` testlerindeki KT kimlikleriyle (K-11'deki iki biçim) karşılaştırır. Kök `build.gradle.kts`'teki **bekleyen KT listesi**, henüz kartı gelmemiş satırları tutar. Kapı iki yönlüdür ve şu durumlarda başarısız olur:
  - geçerli bir satırın ne testi var ne de listede,
  - testi yazılmış bir satır hâlâ listede,
  - listede ya da bir testte geçen kimlik tabloda geçerli bir satır değil (ör. kaldırılmış KT-14),
  - tablodan hiç satır okunamadı,
  - `:core:domain` testlerinde `@Disabled` var.

  Bir Faz 2 kartı kapsadığı kimlikleri listeden siler. Listeye kimlik eklemek ya da kartın söylemediği bir kimliği silmek planlama kararıdır. Faz 2 bittiğinde liste boştur; karar tablosuna yeni bir satır eklenirse kapı onu, testi yazılana kadar kırmızıya çevirir.
- **K-17 — Sabit referans gün.** Domain testleri karar tablosundaki fikstürü kullanır: imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30, ertesi imsak 05:30; Europe/Istanbul. Fikstür test kaynaklarındaki `ReferenceDay` yardımcısındadır; G1 = 2 Mart 2026 Pazartesi, G5 cumadır. Testler gerçek tarihe, cihaz saatine ve sistemin saat dilimine bağlı olmaz.

## 9. Kaçınılacaklar (kontrol listesi)

PR'dan önce kontrol:

- [ ] Composable içinde iş mantığı yok (M-12)
- [ ] Domain'de Android tipi veya annotation yok (M-02)
- [ ] Feature modülü başka bir feature'a veya `:core:data`'ya doğrudan bağlı değil (M-04; M-08 denetimi yeşil)
- [ ] Modül build dosyasında SDK seviyesi, `compileOptions` ya da JVM hedefi tekrar yazılmamış; ortak ayar convention plugin'de (M-07)
- [ ] Dışarıya değiştirilebilir akış açılmamış (M-10)
- [ ] İş yapmayan use case veya gereksiz arayüz eklenmemiş (M-22, P-05)
- [ ] Doğrudan saat ya da saat dilimi çağrısı yok, `Clock` kullanılmış (M-23)
- [ ] Kural fonksiyonu ayarı ve `RuleSet`'i parametre olarak alıyor, metin değil tipli sonuç döndürüyor (M-25)
- [ ] `GlobalScope`, `!!`, sihirli sayı yok (P-12, K-03, K-06)
- [ ] Yeni fıkhi davranış karar tablosuna/SRS'e bağlanmış ve testli (K-05, K-11)
- [ ] Kapsanan KT kimlikleri bekleyen listeden silinmiş; `@Disabled` yok (K-16)
- [ ] `:core:domain` satır kapsamı %90 veya üstü; hiçbir sınıf ölçümden hariç değil (K-15)
- [ ] P0 dışı özellik veya kütüphane eklenmemiş (P-03)
- [ ] ktlint temiz; Android Lint'te bilinen uyarılar listesi dışında uyarı yok; `ciCheck` yeşil (K-01)

## 10. .NET → Android kavram eşlemesi

| .NET | Android/Kotlin |
| --- | --- |
| Core / Domain projesi | `:core:domain` (saf Kotlin modülü) |
| Infrastructure | `:core:data`, `:core:prayertimes` |
| Microsoft.Extensions.DI | Hilt |
| Entity Framework Core + Migrations | Room + Migration |
| `IOptions` / appsettings | DataStore |
| `BackgroundService` / Hangfire | WorkManager |
| `async/await`, `Task` | `suspend` fonksiyonlar, coroutine'ler |
| `IAsyncEnumerable` / Rx `IObservable` | `Flow` / `StateFlow` |
| MediatR handler | Use case sınıfı |
| xUnit / NUnit | JUnit Jupiter (JUnit 6) |
| xUnit `[Theory]` + `[MemberData]` (`TheoryData`) | `@ParameterizedTest` + `@MethodSource` (`Arguments.argumentSet`) |
| `TimeProvider` / `ISystemClock` | `java.time.Clock` (M-23) |
| coverlet + ReportGenerator, kapsam eşiği | JaCoCo (`jacocoTestReport`, `jacocoTestCoverageVerification`; K-15) |
| Azure DevOps / GitHub Actions pipeline | GitHub Actions (`.github/workflows/ci.yml`, `ciCheck`) |
| Branch policy (zorunlu build doğrulaması) | GitHub ruleset (`main` koruması, zorunlu CI kontrolü) |
| Discriminated union / `record` | `sealed interface` / `data class` |
| `.csproj` referansları | Gradle modül bağımlılıkları |
| StyleCop / .editorconfig analizörleri | ktlint (+ `.editorconfig`) |
| `Directory.Build.props` / `.targets` | `build-logic` convention plugin'leri (M-07) |
| NetArchTest / ArchUnitNET kuralları | Modül bağımlılık denetimi (M-08) |

## Sürüm geçmişi

| Sürüm | Tarih | Değişiklik |
| --- | --- | --- |
| 1.0 | 29 Ekim 2026 | İlk sürüm |
| 1.1 | 30 Ekim 2026 | K-01: linter ktlint olarak seçildi; kontrol listesine ktlint maddesi ve eşleme tablosuna karşılığı eklendi; CLAUDE.md'ye atıf |
| 1.2 | 1 Kasım 2026 | M-07 (convention plugin'ler), M-08 (derleme sırasında bağımlılık denetimi) ve M-09 (modül adları) eklendi. K-01'e test ve Compose fonksiyonları için ktlint adlandırma istisnası yazıldı. Kontrol listesi ve .NET eşleme tablosu güncellendi. Uygulama: görev kartı F1-02 |
| 1.3 | 1 Kasım 2026 | JVM hedefi Java 11'den 17'ye, test çatısı JUnit 5'ten 6'ya çıktı (JUnit 6 Java 17 ister). Kod ve test yazımı değişmez; değerler yalnızca `ProjectConfig` ve catalog'da. Uygulama: görev kartı F1-03 |
| 1.4 | 1 Kasım 2026 | K-01'e bilinen lint uyarıları tablosu ve iki yönlü lint kapısı kuralı eklendi; "uyarı sayısı sıfır" hedefi "bilinen liste dışında uyarı yok" olarak netleşti. M-07 tablosuna `namaztakibi.android.hilt` eklendi. .NET eşlemesine CI ve dal koruması eklendi. Uygulama: görev kartı F1-04 |
| 1.5 | 1 Kasım 2026 | K-01: sürüm güncelliği denetimleri (`GradleDependency`, `NewerVersionAvailable`, `AndroidGradlePluginVersion`) zamana bağlı oldukları için lint kapısının dışına alındı; `AndroidGradlePluginVersion` bilinen uyarılar tablosundan çıktı. Gerekçe: F1-04 Adım 0'da bu uyarının çalıştırmadan çalıştırmaya görünüp kaybolduğu ölçüldü |
| 1.6 | 2 Kasım 2026 | Faz 2 başlangıcı. M-25 (kural motoru sözleşmesi: girdiler parametre, çıktı tipli, metin yok) eklendi. M-21: `RuleSet.HANEFI` ve sürüm kimliği. M-23: `ZoneId.systemDefault()` yasağı. K-11: KT kimliğini taşıyan iki test biçimi (fonksiyon adı ve `argumentSet`). K-15 (JaCoCo ile %90 satır kapsamı kapısı), K-16 (iki yönlü KT izlenebilirlik kapısı ve bekleyen KT listesi) ve K-17 (sabit referans gün, G1 = 2 Mart 2026) eklendi. K-02 vakit adları, K-06 test beklentileri. M-07 tablosu, kontrol listesi ve .NET eşlemesi güncellendi; sürüm tablosu sıraya konuldu. Uygulama: görev kartı F2-01 |
| 1.7 | 2 Kasım 2026 | M-23: takvim günü gerektiren hesaplarda bölge ilçenin saat dilimidir ve ayrı `ZoneId` parametresiyle verilir; `Clock`'un bölgesi kullanılmaz (SRS DATA-03). Uygulama: görev kartı F2-02 |

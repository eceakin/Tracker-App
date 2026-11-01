# Mimari ve Kodlama İlkeleri — Namaz Takibi

29 Ekim 2026 · @ece · Sürüm 1.5 (1 Kasım 2026: sürüm güncelliği denetimleri lint kapısının dışında)

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
  | `namaztakibi.jvm.library` | `:core:domain` | Kotlin/JVM, JVM hedefi, JUnit Jupiter |
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
- **M-21 — Kural parametreleri `RuleSet`'te durur.** Hanefî sayıları (72/240 saat, 40 gün, 15 gün, tertip 6 vb.) koda gömülmez; ileride diğer mezhepler yeni bir `RuleSet` olarak eklenir.
- **M-22 — Use case yalnızca değer kattığında yazılır.** İçinde tek bir repository çağrısı olan, iş yapmayan use case açılmaz; böyle durumlarda ViewModel doğrudan repository arayüzünü kullanır.
- **M-23 — Zaman dışarıdan verilir.** Her zaman bağımlı fonksiyon `Clock` alır; anlar `Instant` + `ZoneId` ile tutulur. `LocalDateTime.now()` gibi doğrudan saat çağrıları domain'de yasaktır.
- **M-24 — Hatalar tiplidir.** Beklenen iş sonuçları (ör. "vakit girmedi", "kerahat vakti") istisna değil, `sealed` sonuç tipleriyle döndürülür. İstisnalar yalnızca gerçekten beklenmeyen durumlar içindir.

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
- **K-02 — Adlandırma:** Kod İngilizce; fıkhi terimler domain'de kendi adlarıyla kalır (`Kaza`, `Mazeret`, `Vitir`), çevrilmeye zorlanmaz. Kullanıcıya görünen tüm metinler `strings.xml`'de, Türkçe.
- **K-03 — Kotlin deyimleri:** Durumlar için `sealed interface`, kimlikler için `value class`, null yerine anlamlı tipler. `!!` kullanılmaz.
- **K-04 — Fonksiyonlar küçük ve tek işlidir.** Uzun fonksiyon, bölünmesi gerektiğine işarettir.
- **K-05 — Yorum "neden"i açıklar, "ne"yi değil.** Fıkhi kural uygulayan her fonksiyon, karar tablosundaki ilgili satıra veya SRS'teki BR numarasına referans verir.
- **K-06 — Sihirli sayı yok.** Fıkhi sayılar `RuleSet`'te (M-21), diğer sabitler adlandırılmış sabitlerde.

## 8. Test ilkeleri (K)

- **K-10 — Test piramidi:** Testlerin çoğu `:core:domain` birim testleri; ardından Room/geçiş entegrasyon testleri; en üstte kritik akışlar için az sayıda UI testi.
- **K-11 — Domain'de TDD.** Karar tablosunun her satırı parametreli bir testtir; önce test, sonra kod.
- **K-12 — Sınır ve değişmez testleri.** Tam 72./240. saat, vaktin son dakikası, yaz saati geçişi; "borç asla negatif olmaz" gibi değişmezler.
- **K-13 — Test adları davranışı anlatır** (ör. "ögle vakti girmeden işaretleme reddedilir").
- **K-14 — Sahte (fake) tercih edilir.** Repository'ler için bellek içi sahte uygulamalar yazılır; mock kütüphanesi yalnızca gerektiğinde.

## 9. Kaçınılacaklar (kontrol listesi)

PR'dan önce kontrol:

- [ ] Composable içinde iş mantığı yok (M-12)
- [ ] Domain'de Android tipi veya annotation yok (M-02)
- [ ] Feature modülü başka bir feature'a veya `:core:data`'ya doğrudan bağlı değil (M-04; M-08 denetimi yeşil)
- [ ] Modül build dosyasında SDK seviyesi, `compileOptions` ya da JVM hedefi tekrar yazılmamış; ortak ayar convention plugin'de (M-07)
- [ ] Dışarıya değiştirilebilir akış açılmamış (M-10)
- [ ] İş yapmayan use case veya gereksiz arayüz eklenmemiş (M-22, P-05)
- [ ] Doğrudan saat çağrısı yok, `Clock` kullanılmış (M-23)
- [ ] `GlobalScope`, `!!`, sihirli sayı yok (P-12, K-03, K-06)
- [ ] Yeni fıkhi davranış karar tablosuna/SRS'e bağlanmış ve testli (K-05, K-11)
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
| 1.5 | 1 Kasım 2026 | K-01: sürüm güncelliği denetimleri (`GradleDependency`, `NewerVersionAvailable`, `AndroidGradlePluginVersion`) zamana bağlı oldukları için lint kapısının dışına alındı; `AndroidGradlePluginVersion` bilinen uyarılar tablosundan çıktı. Gerekçe: F1-04 Adım 0'da bu uyarının çalıştırmadan çalıştırmaya görünüp kaybolduğu ölçüldü |
| 1.4 | 1 Kasım 2026 | K-01'e bilinen lint uyarıları tablosu ve iki yönlü lint kapısı kuralı eklendi; "uyarı sayısı sıfır" hedefi "bilinen liste dışında uyarı yok" olarak netleşti. M-07 tablosuna `namaztakibi.android.hilt` eklendi. .NET eşlemesine CI ve dal koruması eklendi. Uygulama: görev kartı F1-04 |

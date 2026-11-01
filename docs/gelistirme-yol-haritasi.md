# Geliştirme Yol Haritası — Namaz & Kaza Takip

26 Ekim 2026 · @ece · Son güncelleme: 1 Kasım 2026 (F1-03 tamamlandı; F1-04 kapsamı: Hilt, CI, lint kapısı, `main` koruması)

## Özet

Proje 8 fazda ilerler ve en riskli iki parça en başa alınır: fıkhi kural motorunun doğruluğu ve Diyanet vakit verisine erişim. Bunlardan biri yanlış çıkarsa üstüne kurulan her ekran yeniden yazılır; 5te5'in en ağır şikayetleri de tam olarak bu iki alandan (yanlış vakit, kaybolan veri) geliyor.

**Kritik yol:** Faz 0 → Faz 2 (kural motoru) → Faz 4 (veri katmanı) → Faz 5 (arayüz) → Faz 6 (widget) → Faz 7 (yayın). Faz 1 (iskelet) ve Faz 3 (vakit verisi) Faz 2 ile paralel yürüyebilir.

Her fazda üç şey var: amaç, yapılacaklar (işaretlenebilir liste) ve **bitti tanımı**. Bitti tanımı karşılanmadan sonraki faza geçilmez; tek kişilik bir projede kapsam kaymasını önleyen şey budur.

**Çalışma düzeni (30 Ekim 2026):** Planlama, görev kartları ve kod incelemesi claude.ai projesinde yapılır; kod, Claude Code üzerinden DeepSeek API ile yazdırılır. Kodlama ajanının bağlayıcı talimatları depo kökündeki `CLAUDE.md`'dedir (proje belgesi: CLAUDE.md). Görev kartları depoda `docs/tasks/` altında tutulur.

Kaynak belgeler: 5te5-yorum-analizi.md, ozellik-listesi.md, fikhi-gereksinimler.md, gereksinim-analizi-srs.md, karar-tablosu.md, kabul-senaryolari.md, mimari-ve-kodlama-ilkeleri.md, ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md (proje dosyaları).

## Faz 0 — Netleştirme ve doğrulama

**Amaç:** Kod yazmadan önce "neyi, hangi kurala göre" yapacağını değiştirilemez hale getirmek. Bu fazın çıktısı belgelerdir, kod değildir.

- [x] **Kapsamı dondur.** MVP = özellik listesindeki 10 P0 maddesi + fıkhi belgedeki P0 eklemeleri (onboarding, kural motoru, vitir/cuma, mazeret kuralları). P1 ve P2'ye dokunulmaz. Durum: SRS 1.0'da 43 P0 gereksinim olarak donduruldu.
- [x] **Gereksinim analizini yaz.** Durum: gereksinim-analizi-srs.md, 1.0-taslak.9 (50 geçerli FR: 43 P0, 7 P1; 22 geçerli BR; 21 geçerli NFR); değişiklikler Bölüm 9'daki sürüm tablosundan izlenir.
- [x] **Kabul kriterlerini senaryoya çevir.** Her P0 maddesini "Verilen / Olduğunda / Beklenen" kalıbında 2–5 senaryoya böl. Örnek: *Verilen* öğle vakti girmemiş, *olduğunda* kullanıcı öğleye dokunur, *beklenen* işaret konmaz ve kısa uyarı görünür. Durum: kabul-senaryolari.md, 99 senaryo; 43 P0 gereksinimin tümü kapsanıyor.
- [x] **Fıkhi kuralları karar tablosuna dök.** Her satır = girdi + beklenen sonuç + kaynak. Bu tablo Faz 2'deki testlerin verisi olacak; hedef en az 40 senaryo. Durum: karar-tablosu.md v1.5, 101 geçerli senaryo.
- [x] **Karar tablosunu bir hocaya kontrol ettir.** Durum: 28 Ekim 2026'da iki görüşmede tamamlandı (hocaya-sorular.md); sonuçlar SRS 1.0-taslak.7 ve 1.0-taslak.8'e, karar tablosu v1.4 ve v1.5'e işlendi. Fıkhi **ekran metinlerinin** hoca kontrolü ayrı bir madde olarak açık; Faz 5'te metinler `strings.xml`'e girmeden önce kapanır (SRS Bölüm 10).
- [ ] **Diyanet erişimini netleştir.** API hesabı başvurusu, kullanım koşulları, istek sınırları. Olmazsa B planı: Adhan ile hesaplama + birkaç ilçede Diyanet takvimiyle dakika karşılaştırması. Durum: API araştırıldı ve SRS IF-02'ye işlendi; hesap başvuru formu 29 Ekim 2026'da gönderildi, yanıt bekleniyor. Başvuruda il/ilçe listesinin uygulamaya gömülmesi için de izin istenecek (SRS A-02). Yanıt beklenirken Faz 1'e başlanır; vakit kaynağı arayüzün arkasında olduğu için iskelet bu yanıttan etkilenmez.
- [x] **Ekran listesi ve kaba taslaklar.** Durum: 5 turda tamamlandı (27 Ekim 2026). Ayrıntılar ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md belgelerinde ve [Ekran Taslakları](https://claude.ai/artifact/JT9ex8zdaWsNBymxW5UhVq) panosunda. 43 P0 gereksinimin tümü en az bir ekranla eşleşiyor.
- [x] **Başarı ölçütlerini yaz.** Durum: SRS Bölüm 5'te NFR olarak yazıldı.

**Bitti tanımı:** Senaryolar ve karar tablosu yazılı, hoca kontrolünden geçmiş; Diyanet erişim yolu (A veya B planı) seçilmiş.

**Durum (30 Ekim 2026):** Belgeler ve hoca kontrolü tamam. Açık kalan tek madde Diyanet erişimi; dış onaya bağlı, başvuru yanıtı bekleniyor. Faz 1'e başlandı.

## Faz 1 — Mimari ve proje iskeleti

**Amaç:** Kural motorunu Android'den bağımsız tutan, test edilebilir bir çok modüllü proje kurmak. Ayrıntılı kurallar: mimari-ve-kodlama-ilkeleri.md.

**Kartlar** (depoda `docs/tasks/`):

| Kart | Kapsam | Durum |
| --- | --- | --- |
| F1-01 | Paket adı `com.eceakin.namaztakibi`, ktlint, saf Kotlin `:core:domain` ve ilk test (BR-01) | Tamamlandı (30 Ekim 2026) |
| F1-02 | Depo hijyeni, kütüphane sürümleri, lint uyarıları, `build-logic` convention plugin'leri, boş modüller, bağımlılık denetimi, README | Tamamlandı (1 Kasım 2026) |
| F1-03 | Araç zinciri yükseltmesi: Gradle 9.6.1, AGP 9.5, Kotlin 2.4, compileSdk 37.1 ve targetSdk 37, Java 17, JUnit 6, geri tutulan kütüphaneler. Lint 9 → 7 uyarı; kalanlar bilinen istisna (mimari K-01) | Tamamlandı (1 Kasım 2026) |
| F1-04 | Hilt (KSP) ve `namaztakibi.android.hilt` convention plugin'i, GitHub Actions CI (`ciCheck`), bilinen uyarı listesine dayalı lint kapısı, `main` dalının korunması | Hazır |
| F1-05 | Risk prototipi: widget'tan işaretleme ve pencereli hatırlatma, Samsung ve Xiaomi'de deneme | Bekliyor |

| Modül | İçerik | Bağımlılık kuralı |
| --- | --- | --- |
| `:core:domain` | Entity'ler, kural motoru, use-case'ler; saf Kotlin | Hiçbir Android kütüphanesine bağımlı değil |
| `:core:data` | Room veritabanı, DataStore ayarları, repository'ler, yedekleme | `:core:domain` |
| `:core:prayertimes` | Ara sunucu istemcisi, vakit önbelleği, Adhan yedeği | `:core:domain` |
| `:feature:*` | onboarding, bugün, takvim, kaza, mazeret, ayarlar; Compose + ViewModel | `:core:domain` (veriye repository arayüzüyle erişir) |
| `:widget` | Jetpack Glance widget'ı | `:core:domain`, `:core:data` |
| `:app` | DI, navigasyon, bildirim/alarm altyapısı | Hepsi |

- [ ] **Teknoloji yığını:** Kotlin, Jetpack Compose (Material 3), Navigation Compose, Hilt, Room + KSP, DataStore, WorkManager, Glance, kotlinx.serialization, Adhan (Java). Bağımlılıklar Gradle version catalog (`libs.versions.toml`) ile yönetilir. Kütüphaneler ihtiyaç duyulan fazda eklenir; Faz 1'de yalnızca Compose ve Hilt girer.
- [x] **minSdk 26:** `java.time` ve hicrî takvim için gereken `HijrahDate` ek kütüphane olmadan kullanılabilir.
- [x] **Paket adı:** `com.eceakin.namaztakibi` (F1-01). `com.example` Play Store'da kabul edilmez ve yayından sonra `applicationId` değiştirilemez.
- [x] **Modül yapısı:** Boş modüller, `build-logic` convention plugin'leri (mimari M-07) ve derleme sırasında bağımlılık denetimi (M-08). F1-02.
- [x] **Araç zinciri:** Güncel AGP, Gradle ve Kotlin; compileSdk ve targetSdk 37 (Android 17); Java 17 hedefi. Yeni bir hedef SDK'nın davranış değişiklikleri ilgili özelliğin fazında (ör. alarmlar Faz 6) ayrıca değerlendirilir. F1-03.
- [ ] **Test araçları:** JUnit Jupiter (domain; F1-01'de JUnit 5 ile kuruldu, F1-03'te JUnit 6), Robolectric ve Room test yardımcıları (data; Faz 4), Compose UI testleri (feature; Faz 5).
- [ ] **Kod kalitesi:** ktlint kuruldu (F1-01), kök betikler ve `build-logic` kapsandı (F1-02). Android Lint 18 → 9 (F1-02) → 7 uyarı (F1-03). Kalan 7 uyarı mimari K-01'de bilinen istisna olarak kayıtlı; F1-04'teki lint kapısı bunların dışındaki her yeni uyarıyı reddeder.
- [ ] **CI:** GitHub Actions'ta her Pull Request'te ve `main`'e her birleştirmede tek komut (`ciCheck`): ktlint, birim testleri, Android Lint ve lint kapısı, derleme. F1-04.
- [ ] **Hilt:** KSP ile, `namaztakibi.android.hilt` convention plugin'i üzerinden; Faz 1'de yalnızca `:app`'e uygulanır. F1-04.
- [ ] **Depo düzeni:** README, `CLAUDE.md`, `docs/` (proje belgelerinin kopyası, `docs/tasks/` görev kartları ve raporları) ve `main` + kısa ömürlü dal akışı (her iş ayrı dalda, Pull Request ile "Squash and merge"; `main` GitHub ruleset'iyle korunur: doğrudan push yok, birleştirme yalnızca PR ile ve CI yeşilken; F1-04). `.idea/` depoda tutulmaz. Karar kayıtları (ADR, `docs/adr/`) yazılmaz; kararlar proje belgelerinde (karar tablosu, SRS, mimari ilkeler) durur. Bu, 29 Ekim 2026'da alınan bir karardır (SRS 1.0-taslak.9'da NFR-MNT-03 kaldırıldı). README ve `.idea/` düzenlemesi F1-02.
- [ ] **Risk prototipi:** Faz 1'in sonunda bir günlük, atılabilir bir "widget'tan işaretle + pencereli hatırlatma" denemesi yap ve iki farklı üreticinin cihazında dene. Bu, projenin ana farkının teknik olarak mümkün olduğunu en baştan gösterir. Aynı prototipte widget'ın işleyen geri sayımı ve "ana ekrana ekle" penceresi de Xiaomi ve Samsung'da denenir (ekran taslakları Tur 5).

**Bitti tanımı:** Boş modüllerle proje CI'da derleniyor, ktlint temiz, Android Lint'te bilinen istisnalar dışında uyarı yok, bağımlılık denetimi etkin ve `:core:domain`'de örnek bir test geçiyor; risk prototipi iki üreticinin cihazında denenmiş.

## Faz 2 — Domain modeli ve kural motoru (TDD)

**Amaç:** Uygulamanın kalbini, ekran olmadan ve tamamen testlerle yazmak. Kural motoru saf fonksiyonlardan oluşur ve hiçbir zaman veri değiştirmez; yalnızca "öneri" döndürür, kararı kullanıcı verir. Bu, "şüphede sor" ilkesinin koddaki karşılığıdır.

**Temel varlıklar.** Alanların bağlayıcı listesi SRS Bölüm 6.1'deki veri sözlüğüdür; aşağıdaki tablo onun özetidir.

| Varlık | Alanlar |
| --- | --- |
| `PrayerRecord` | tarih (namaz günü), kalem (sabah…yatsı, vitir), durum (kılındı / kılınmadı / mazeretli / belirsiz), işaretlenme anı, cuma mı, kaza olarak mı kılındı |
| `KazaEntry` | kalem, kaynak tarih (biliniyorsa), tür (elle / otomatik / hesaplayıcı / düzeltme), seferde kaçtı mı, oluşturulma anı |
| `KazaPerformed` | kalem, kılınma anı |
| `MazeretPeriod` | tür (hayız / nifas), başlangıç anı, bitiş anı, kanama aralıkları (ara temizliği olan dönemde birden çok), uyarılı mı |
| `PrayerTimesDay` | ilçe, tarih, imsak, güneş, öğle, ikindi, akşam, yatsı, kaynak (Diyanet / tahmini) |
| `DaySnapshot` | tarih, o güne ait ayarların ve kural seti sürümünün anlık kopyası (BR-21) |
| `RuleSet` | mezhep parametreleri: namaz listesi, hayız 72/240 saat, nifas 960 saat, temizlik 15 gün, kerahat dakikaları 45/45/45, tertip sınırı 6, rekât süresi 2 dk, gusül aralığı 5–30 dk, tekbir 1 dk, baygınlıkta namazların düştüğü eşik: 6. farz vaktin çıkışı (BR-22) |

- [ ] **Saat bağımlılığını enjekte et.** Her fonksiyon `Clock` alır; zamanlar `Instant` + `ZoneId` ile tutulur. Yurt dışında yaz saati geçişleri bu sayede doğru çalışır.
- [ ] **Vakit fonksiyonları:** şu anki vakit ve namaz günü, işaretlenebilir mi, kerahat vaktinde mi, "kaçırıldı" sınırı, hatırlatma anı.
- [ ] **Mazeret fonksiyonları:** başlangıç ve bitişte hangi vaktin borç olabileceği (gusül + tekbir hesabı dahil), 72 saat altı ve 240 saat üstü, alışkanlık güncellemesi, ara temizlikte dönem birleştirme (hayızda 240, nifasta 960 saat), 15 günlük temizlik kontrolü.
- [ ] **Kaza fonksiyonları:** borç ekleme ve düşme, tertip bilgi notu (farz borcu 1–5, vakit darlığı formülü), kerahatte kaza engeli, seferde rekât sayısı, baygınlıkta vakti çıkan farzların sayımı.
- [ ] **Borç hesaplayıcı:** doğum tarihi → hicrî takvimle bulûğ tarihi, gün sayısı × 6, mazeret günlerinin düşülmesi.
- [ ] **Testler:** Faz 0'daki karar tablosunun her satırı bir parametreli test. Ek olarak sınır testleri (tam 72. ve 240. saat, vaktin son dakikası) ve değişmezler (borç asla negatif olmaz).

**Bitti tanımı:** Karar tablosundaki tüm senaryolar yeşil, `:core:domain` test kapsamı %90'ın üstünde, modülde tek bir Android import'u yok.

## Faz 3 — Vakit verisi

**Amaç:** Diyanet vakitlerini güvenle cihaza getirmek ve internet yokken de doğru vakit göstermek. 5te5'te bir kullanıcının bütün gün erken namaz kılmasına yol açan "konum sıfırlanması" hatası burada önlenir.

**Ara sunucu**

- [ ] Küçük, sunucusuz bir servis kur (ör. Cloudflare Workers veya Firebase Functions). Diyanet hesap bilgileri yalnızca burada, gizli değişken olarak durur; uygulamaya hiç girmez.
- [ ] Üç uç nokta: ülke/il/ilçe listesi, bir ilçenin aylık vakitleri, sağlık kontrolü.
- [ ] Vakitler değişmediği için sunucu tarafında önbellekle; Diyanet'e giden istek sayısını en aza indir.
- [ ] Kullanıcı verisi tutma, IP kaydetme; sunucu yalnızca ilçe kimliği alır.

**Uygulama tarafı**

- [ ] Konum seçimi GPS'siz, listeden il/ilçe ile yapılır. Böylece konum izni gerekmez ve seçim DataStore'da kalıcıdır. İl/ilçe listesi ve koordinatları uygulamaya gömülüdür; ilk kurulum internetsiz yapılır (SRS A-02).
- [ ] İlk seçimde en az 30 günlük vakit indirilir ve Room'a yazılır. WorkManager, önbellekte 10 günden az kaldığında arka planda yeniler.
- [ ] Önbellek biterse ve internet yoksa Adhan ile hesaplanır; ekranda "tahmini vakit" etiketi görünür. Adhan yalnızca bu yedek hesap için kullanılır; asr-ı sânî ve işâ-i sânî hesaplanmaz ve gösterilmez (hoca görüşü, BR-06 ve FR-VKT-05 kaldırıldı).
- [ ] Doğrulama: Adhan yedeğini 5 farklı ilçede, yaz ve kış günleri için Diyanet takvimiyle dakika dakika karşılaştır ve farkları belgeye yaz.

**Bitti tanımı:** Uçak modunda 30 gün boyunca doğru vakit gösteriliyor; sunucu kapalıyken uygulama çalışmaya devam ediyor; seçili konumun hiçbir akışta sıfırlanmadığı testle kanıtlanmış.

## Faz 4 — Veri katmanı, yedekleme ve şifreleme

**Amaç:** "Silip yükledim, her şey gitti" cümlesini bu uygulama için imkânsız hale getirmek.

- [ ] **Sayaç değil, kayıt tut.** Kaza borcunu tek bir sayı olarak güncelleme; her ekleme ve her kılınan kaza ayrı satır olsun, kalan borç bunlardan hesaplansın. Böylece geri alma kolaylaşır, geçmiş korunur ve hatalı bir güncelleme veriyi bozamaz.
- [ ] **Room şema geçmişi.** `exportSchema = true`, şema dosyaları depoda; her sürüm için `MigrationTestHelper` testi. Yıkıcı geçiş (`fallbackToDestructiveMigration`) asla kullanılmaz.
- [ ] **Hassas veriyi ayır.** Mazeret kayıtları ayrı bir veritabanı dosyasında tutulur ve Android otomatik yedeğinin dışında bırakılır. Bu veri yalnızca kullanıcının parolasıyla şifrelenmiş dışa aktarma dosyasına girer.
- [ ] **Şifreleme:** Paroladan PBKDF2 (veya Argon2) ile anahtar türet, AES-GCM ile şifrele. Parola saklanmaz; unutulursa mazeret verisi geri gelmez, bunu kullanıcıya açıkça yaz.
- [ ] **Dışa/içe aktarma:** Şema sürümü taşıyan JSON dosyası. İçe aktarmada sürüm farkı varsa önce dönüştür, sonra yaz.
- [ ] **Android otomatik yedeği:** Ana veritabanı ve ayarlar dahil, hassas dosya hariç (`dataExtractionRules`).
- [ ] **Kurtarma ekranı:** Veritabanı açılamazsa uygulama gri ekranda kalmaz; "verilerini dışa aktar" seçeneği olan bir kurtarma ekranı açılır. 5te5'te önerilen "verileri temizle" çözümü hiçbir zaman ilk seçenek olmaz.

**Bitti tanımı:** v1'den güncel sürüme geçiş testleri yeşil; dışa aktar → sil → içe aktar döngüsü birebir aynı veriyi geri getiriyor; yazma sırasında uygulama öldürüldüğünde veri tutarlı kalıyor.

## Faz 5 — Arayüz ve onboarding

**Amaç:** Kural motorunun önerilerini yeni başlayan birinin korkmadan kullanabileceği ekranlara dökmek.

| Ekran | İçerik | Kritik davranış |
| --- | --- | --- |
| Onboarding | Cinsiyet, konum, isteğe bağlı kaza borcu ve hatırlatma; "Bu sürüm Hanefî mezhebine göre çalışır" bilgisi | Kaza adımı atlanabilir; hiçbir vakit zorla işaretletilmez |
| Bugün | 5 vakit + vitir, sıradaki vakte kalan süre, açık günün tarihi | Vakti gelmemiş namaz kilitli; işaretleme tek dokunuşla geri alınabilir |
| Geçmiş | Önceki günler; geçmiş bir günü açıp işaretleme | Süre sınırı yok; animasyonsuz ve hızlı |
| Kaza | Vakit bazında kalan borç, "kaza kıldım" | Vakit seçimi zorunlu; kerahat vaktinde kapalı; tertip bilgi notu |
| Mazeret | Başlat / bitir, vakit ve saat seçimi | Belirsiz durumlarda soru sorar; süre uyarıları |
| Ayarlar | Konum, cinsiyet, gusül süresi (5–30 dk), hatırlatmalar, yedekleme | Konum ve cinsiyet değişikliği onay ister. Kerahat dakikaları ayarı yoktur; değer yalnızca `RuleSet`'te durur (FR-AYR-03 kaldırıldı) |

**Girdi:** ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md ve tasarım panosu. Ekranlar, durumlar ve mikro metinler oradan alınır; panoda olmayan bir ekran ya da metin önce SRS'e girer. Taslaklarda hoca görüşünden önce çizilmiş değerler (gusül 5–60 dk, kerahat 50 dk, alışkanlık güncelleme diyaloğu) SRS Bölüm 10'daki "Ekran taslaklarına yansıtma" maddesine göre güncellenerek uygulanır.

- [ ] **Ekran taslaklarından devredilenler:** %200 yazı boyutu ve TalkBack ile her ekranın ve iki widget'ın denenmesi (KS-NFR-07); açık ve koyu temada kontrast denetimi (KS-NFR-08). Fıkhi ekran metinleri hoca kontrolünden geçmeden `strings.xml`'e girmez (SRS Bölüm 10).
- [ ] **Durum yönetimi:** Her ekranda tek bir `UiState` ve `StateFlow`; ekranlar yalnızca durumu çizer ve olay gönderir.
- [ ] **Dil ve ton:** Suçlayıcı olmayan metinler ("kılmadın" yerine "bu vakit henüz işaretlenmedi"). Tüm metinler `strings.xml`'de, ileride çeviriye hazır.
- [ ] **Erişilebilirlik:** Büyük yazı tipi, TalkBack etiketleri, yeterli kontrast, koyu tema.
- [ ] **Kullanılabilirlik testi:** Hedef kitleden 3 kişiye (biri yeni başlayan) onboarding'i ve bir günlük işaretlemeyi izleyerek yaptır.

**Bitti tanımı:** Faz 0 senaryoları UI testleriyle geçiyor; tüm akışlar TalkBack ile tamamlanabiliyor; kullanılabilirlik testindeki engelleyici sorunlar giderilmiş.

## Faz 6 — Widget ve hatırlatmalar

**Amaç:** Uygulamanın asıl farkını, yani uygulamayı açmadan işaretlemeyi ve zamanında gelen, doğru hatırlatmayı güvenilir kılmak. Bu fazın en büyük riski Android'in arka plan kısıtlarıdır.

**Widget (Glance)**

- [ ] İki boyut: küçük (sıradaki vakit, kalan süre, "kıldım" düğmesi) ve orta (bugünün 6 kutusu).
- [ ] Dokunma bir `ActionCallback` ile doğrudan repository'ye yazar, uygulama açılmaz; ardından widget kendini günceller.
- [ ] Widget'lar saniye saniye güncellenemez. Güncellemeler vakit geçişlerine göre zamanlanır; geri sayım için sistem saatiyle kendi kendine işleyen bir metin bileşeni kullanılır (Glance'te RemoteViews birlikte çalışabilirliği gerekebilir, erken prototiple doğrula).
- [ ] Mazeret modunda widget nötr görünür; "mazeret" kelimesi hiçbir yerde yazmaz.

**Hatırlatmalar**

- [ ] **Kesin alarm izni:** Android 14'ten itibaren `SCHEDULE_EXACT_ALARM` yeni kurulumlarda varsayılan olarak reddedilmiş geliyor; `USE_EXACT_ALARM` ise yalnızca takvim ve çalar saat uygulamalarına açık. Bu yüzden "vakit çıkmadan X dk önce" hatırlatmaları en az 10 dakikalık pencereli alarmla (`setWindow`) kurulur. Kesin saat isteyen kullanıcıdan izin, ayarlar akışıyla istenir; reddederse pencereli alarma dönülür.
- [ ] **Bildirim izni:** Android 13 ve sonrasında `POST_NOTIFICATIONS` çalışma anında istenir; onboarding'in hatırlatma adımında.
- [ ] **Yeniden zamanlama:** Cihaz yeniden başladığında, saat veya saat dilimi değiştiğinde ve uygulama güncellendiğinde tüm alarmlar yeniden kurulur.
- [ ] **Bildirimde eylem:** "Kıldım" düğmesi bildirimi açmadan işaretler. Vakit zaten işaretlenmişse bildirim hiç gönderilmez; 5te5'teki "10 gündür yoksun" hatası böyle önlenir.
- [ ] **Üretici pil kısıtları:** Xiaomi, Huawei, Samsung gibi cihazlarda alarmlar öldürülebilir. Uygulama içinde bunu açıklayan kısa bir yönlendirme ekranı olsun. Üreticiye göre pil adımları gerçek cihazlarda doğrulanır.
- [ ] **Kilit ekranı:** Bildirimdeki "Kıldım" eyleminin kilit açılmadan çalıştığı ve kilit ekranında mazeret ipucu görünmediği cihazda denenir (KS-NFR-01, KS-NFR-09).

**Bitti tanımı:** Widget'tan işaretleme uygulama açılmadan çalışıyor; hatırlatmalar yeniden başlatma ve Doze modu (adb ile) sonrasında pencere içinde geliyor; en az iki farklı üreticinin cihazında denendi.

## Faz 7 — Test, gizlilik ve Play Store yayını

**Amaç:** Uygulamayı gerçek kullanıcılarla doğrulayıp güvenle yayınlamak.

**Test**

- [ ] **Test piramidi:** Testlerin çoğu `:core:domain` birim testleri; ardından Room ve geçiş entegrasyon testleri; en üstte kritik akışlar için az sayıda UI testi.
- [ ] **Elle test matrisi:** En az üç Android sürümü (minSdk 26, 13, 14 ve üstü) ve iki üretici. Saat dilimi değişimi, yeniden başlatma, uçak modu, düşük depolama senaryoları dahil.
- [ ] **Çökme takibi:** Önce SDK gerektirmeyen Play Console Android vitals. Ek bir çökme raporlama aracı eklenecekse kullanıcı onayıyla (opt-in) açılsın.

**Gizlilik ve yasal**

- [ ] **Veri envanteri:** Hangi veri nerede durur? Takip ve mazeret verisi yalnızca cihazda; ara sunucu yalnızca ilçe kimliği görür. Mazeret bilgisi sağlık verisi sayılabileceği için bu tasarım KVKK açısından da en güvenli yoldur.
- [ ] **Gizlilik politikası:** Play Store zorunlu tutuyor; kısa ve anlaşılır bir sayfa hazırla.
- [ ] **Data safety formu:** Analitik ve reklam SDK'sı yoksa "veri toplanmıyor" beyanı doğru şekilde doldurulur.

**Yayın**

- [ ] **Geliştirici hesabı:** 13 Kasım 2023'ten sonra açılan kişisel hesaplar, üretime çıkmadan önce en az 12 test kullanıcısının 14 gün boyunca kesintisiz katıldığı bir kapalı test yapmak zorunda. Bu zorunluluğu fırsata çevir: test grubunu hedef kitleden (yeni başlayanlar, aile, arkadaş çevresi) kur.
- [ ] **Sürüm akışı:** İç test → kapalı test (12+ kişi, 14+ gün) → üretimde kademeli yayın (%10 → %50 → %100).
- [ ] **Mağaza sayfası:** Hanefî notu, reklamsız ve hesapsız vurgusu, widget ekran görüntüleri.
- [ ] **Yayın sonrası:** Yorumlara yanıt, Android vitals takibi, v1.1 iş listesinin yorumlara göre yeniden sıralanması.

**Bitti tanımı:** Uygulama üretimde; ilk 2 haftada veri kaybı bildirimi yok ve çökme oranı Faz 0'da yazılan hedefin altında.

## Riskler ve önlemler

En yüksek iki risk dış bağımlılıklarda (Diyanet erişimi, Android arka plan kısıtları); ikisi de erken bir prototiple ucuza test edilebilir.

| Risk | Olursa ne olur | Önlem |
| --- | --- | --- |
| Diyanet API erişimi alınamaz | Vakit kaynağı kalmaz | B planı Faz 0'da hazır: Adhan + Diyanet karşılaştırması. Vakit kaynağı bir arayüzün arkasında, değiştirmek tek modülü etkiler |
| Widget veya alarm bazı cihazlarda güvenilmez | Uygulamanın ana farkı çöker | Faz 1 sonunda bir günlük "widget + hatırlatma" prototipi; iki farklı üreticide dene |
| Fıkhi bir kural yanlış uygulanır | Kullanıcı güveni ve ibadeti etkilenir | Karar tablosu + hoca kontrolü; motor asla sessizce borç yazmaz; her kayıt elle düzeltilebilir |
| Kodlama ajanı kuralı yanlış yorumlar ya da testi beklentiye uydurur | Fıkhi hata sessizce koda girer | CLAUDE.md'de "dur ve sor" ve "beklentiyi değiştirme" kuralları; karar tablosu testleri sözleşmedir; her PR gözden geçirilir |
| Veri kaybı | 5te5'in en ağır şikayeti tekrarlanır | Faz 4'teki geçiş testleri, ayrı hassas dosya, kurtarma ekranı |
| Kapsam kayması | Proje bitmez | P0 donduruldu; her fazın bitti tanımı var; yeni fikir doğrudan P1/P2 listesine gider |
| Kapalı test için 12 kişi bulunamaz | Yayın gecikir | Test grubunu Faz 5'te toplamaya başla |
| Rakip boşlukları kapatır (40 gün sınırını kaldırır, widget ekler) | Fark daralır | Farkı güvenilirlik, kaza takibi ve fıkhi doğrulukta kur; MVP'yi küçük tutup erken yayınla |

## Kaynaklar

- [Testing requirements for new personal developer accounts](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en) — Google Play Console Yardım
- [Schedule exact alarms are denied by default](https://developer.android.com/about/versions/14/changes/schedule-exact-alarms) — Android Developers
- Proje dosyaları: 5te5-yorum-analizi.md, ozellik-listesi.md, fikhi-gereksinimler.md, mimari-ve-kodlama-ilkeleri.md, CLAUDE.md, ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md

# Gereksinim Analizi Raporu (SRS) — Namaz & Kaza Takip v1.0

26 Ekim 2026 · @ece · Son güncelleme: 2 Kasım 2026 (1.0-taslak.10, kaza defterinin veri modeli)

## Özet

Bu belge, Namaz & Kaza Takip uygulamasının v1.0 sürümü için bağlayıcı gereksinim listesidir. Kodda, testte ya da tasarımda bir karar verilirken başvurulacak tek kaynak budur. Belgede yazmayan bir davranış uygulamaya eklenmez; yazanlar da değişiklik yönetimi (Bölüm 9) dışında değiştirilmez.

**Gereksinim analizi ne zaman yapılır?** Ana analiz bütün fazlardan önce, ilk satır kod yazılmadan yapılır; yol haritasındaki Faz 0 tam olarak budur. Yazılımda bir hatayı düzeltmenin maliyeti, hata ne kadar geç bulunursa o kadar büyür. Gereksinimdeki bir hata kodlamadan önce bir paragraf düzeltmesiyle giderilir; yayından sonra bulunursa veri göçü, güncelleme ve kullanıcı güveni kaybı demektir. Ancak analiz bir kez yapılıp bırakılmaz. Bu belge yaşayan bir belgedir: her fazın başında ilgili bölüm gözden geçirilir, her fazın sonunda testler buradaki kabul kriterlerine göre doğrulanır.

**Nasıl okunur?**

- **FR** (fonksiyonel gereksinim): Uygulamanın ne yapacağı.
- **BR** (iş kuralı): Fıkhi kurallar ve sayısal parametreleri. FR'ler bu kurallara atıf yapar; kural değişirse yalnızca BR satırı güncellenir.
- **NFR** (fonksiyonel olmayan gereksinim): Ne kadar iyi yapacağı (güvenilirlik, gizlilik, performans).
- **Öncelik:** P0 = v1.0 için zorunlu, P1 = v1.1, P2 = sonrası. Bu belgedeki P1/P2 maddeleri yalnızca mimariyi şimdiden hazırlamak için yazılmıştır.
- **Kaldırılan maddeler** kimliklerini korur ve "Kaldırıldı" olarak durur; böylece testlerde ve diğer belgelerde geçen kimlikler başka bir şeye işaret etmez.

## 1. Giriş

**1.1 Amaç.** Namaza yeni başlayanların ve kılıp düzene oturtamayanların günlük namazlarını süre sınırı olmadan takip etmesini, kaza borçlarını vakit bazında düşürmesini ve bunu uygulamayı açmadan, widget'tan yapabilmesini sağlamak.

**1.2 Kapsam (v1.0).** Yalnızca Android; yalnızca Hanefî mezhebi; vakit kaynağı Diyanet; hesap gerektirmeyen, reklamsız, çekirdeği internetsiz çalışan bir uygulama. Kapsam dışı olanlar Bölüm 7'de.

**1.3 Terimler sözlüğü**

| Terim | Bu belgedeki anlamı |
| --- | --- |
| Vakit | Beş günlük namazdan birinin kılınabileceği zaman aralığı (sabah, öğle, ikindi, akşam, yatsı); sınırları Diyanet vakitleridir |
| Vitir | Yatsıdan sonra kılınan, Hanefî'de vâcip olan namaz; v1.0'da 6. takip kalemi |
| İşaretleme | Kullanıcının bir vakti "kılındı" olarak kaydetmesi |
| Kaza | Vaktinde kılınmayan farz veya vâcip namazın sonradan kılınması; "kaza borcu" bunların vakit bazında sayısı |
| Mazeret | Namazı kaza gerektirmeden düşüren hâl; v1.0'da hayız ve nifas |
| Hayız | Âdet kanaması; Hanefî'de en az 3, en çok 10 gün. 10 gün içindeki ara temizlik hayızdan sayılır |
| Dönem | Bir mazeretin başlangıcından bitişine kadarki kayıt. 10 gün içinde kesilip yeniden başlayan kanama tek dönemdir (BR-16) |
| Alışkanlık | Kadının âdetinin genellikle kaç gün sürdüğü; her geçerli dönemle güncellenir, 10 günü aşan dönemde 10 olur (BR-14) |
| Nifas | Lohusalık; Hanefî'de en çok 40 gün. 40 gün içindeki ara temizlik nifastan sayılır |
| İstihâze | Hayız ve nifas sınırları dışındaki kanama; namazı düşürmez |
| Kerahat vakti | Güneşin doğuşu, istivâ ve batışı; her biri 45 dk; Hanefî'de kaza kılınmaz |
| Tertip | Farz kaza borcu 6 vakitten azken önce kazanın, sonra vaktin namazının kılınması zorunluluğu |
| Bulûğ | Ergenlik; kaza hesabının başlangıcı |
| Gusül süresi | Kullanıcının boy abdesti için ayarladığı dakika (varsayılan 15, aralık 5–30); mazeret bitişi hesabında kullanılır |
| Öneri | Kural motorunun kullanıcıya sunduğu, onaylanmadan veriye yazılmayan sonuç |
| Namaz günü | İmsaktan ertesi imsaka kadar süren gün; Bugün ekranı ve widget gece yarısından imsaka kadar önceki namaz gününü gösterir |

**1.4 Referanslar.** 5te5-yorum-analizi.md, ozellik-listesi.md, fikhi-gereksinimler.md, gelistirme-yol-haritasi.md, karar-tablosu.md, kabul-senaryolari.md, hocaya-sorular.md, mimari-ve-kodlama-ilkeleri.md, CLAUDE.md, ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md (proje dosyaları). Belgeler arasında çelişki olursa bu belge esastır.

## 2. Genel tanım

**2.1 Kullanıcı profilleri**

| Profil | Kim | Temel ihtiyaç | Tasarıma etkisi |
| --- | --- | --- | --- |
| Yeni başlayan | Namaza ilk kez başlayan; çoğu genç | Alışkanlık, motivasyon, korkutmayan dil | Kaza adımı atlanabilir; yumuşak ton; hafif seri |
| Düzene oturtamayan | Kılan ama sık kaçıran | Hatırlatma, hızlı işaretleme, kaçırılanı görme | Widget, akıllı hatırlatma, kaçırılanı kazaya ekleme |
| Kadın kullanıcı | Her iki profilde de | Âdet ve lohusalık günlerinin seriyi ve borcu bozmaması, gizlilik | Mazeret modu, kelimesiz nötr görünüm, şifreli yedek |

**2.2 Çalışma ortamı.** Android 8.0 (API 26) ve üstü telefonlar; Türkiye ve yurt dışı (Diyanet'in vakit yayımladığı ilçeler); Türkçe arayüz.

**2.3 Kısıtlar**

- **C-01** Yalnızca Android, yalnızca Hanefî mezhebi.
- **C-02** Vakit kaynağı Diyanet; Diyanet hesap bilgileri uygulama paketine girmez, bir ara sunucuda tutulur.
- **C-03** Kullanıcı hesabı, giriş veya kişisel bilgi (e-posta, sosyal medya, doğum tarihi zorunluluğu) yok.
- **C-04** Reklam, analitik SDK'sı ve ücretli özellik yok.
- **C-05** Takip, kaza ve mazeret verisi yalnızca cihazda tutulur.
- **C-06** Geliştirici tek kişidir; kapsam P0 ile sınırlıdır.

**2.4 Varsayımlar ve bağımlılıklar**

- **A-01** Diyanet vakit API'sine ara sunucu üzerinden erişim izni alınabilir; alınamazsa Adhan ile hesaplamaya geçilir (bkz. Açık konular).
- **A-02** Kullanıcı ilçesini listeden seçebilir; GPS gerekmez. İl/ilçe listesi ve koordinatları uygulamaya gömülü gelir; böylece ilk kurulum internetsiz yapılabilir. Ön koşul: Diyanet'in yer listesinin uygulama paketine konmasına izin vermesi (Bölüm 10). İzin çıkmazsa liste ilk açılışta ara sunucudan indirilir ve ilk kurulum internet ister.
- **A-03** Bölüm 4'teki fıkhi kurallar 28 Ekim 2026'da bir hoca tarafından kontrol edildi (hocaya-sorular.md; cevaplar fikhi-gereksinimler.md "Hoca görüşü kaydı"nda). Cevaplardan türetilen beş nokta aynı gün ikinci görüşmede netleşti (1.0-taslak.8).
- **A-04** Android'in kesin alarm kısıtları nedeniyle hatırlatmalar birkaç dakikalık pencere içinde gelebilir; bu kabul edilmiştir.

## 3. Fonksiyonel gereksinimler (FR)

Toplam 50 geçerli gereksinim (43'ü P0, 7'si P1), 9 grupta; 2 madde kaldırıldı. Her biri test edilebilir bir kabul kriteri taşır; "Kural" sütunu Bölüm 4'teki iş kuralına bağlar.

### 3.1 Onboarding (ONB)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-ONB-01 | İlk açılışta cinsiyet ve konum (il/ilçe) zorunlu sorulur | P0 | İkisi seçilmeden ana ekrana geçilmez; ikisi de ayarlardan değiştirilebilir. İl/ilçe listesi uygulamaya gömülüdür; ilçe internet olmadan aranıp seçilir (A-02) | Fıkhi belge; DT-1 |
| FR-ONB-02 | "Bu sürüm Hanefî mezhebine göre çalışır" bilgisi gösterilir; mezhep sorulmaz | P0 | Bilgi ekranı onboarding'de bir kez görünür, ayarlarda da okunabilir | C-01 |
| FR-ONB-03 | Kaza borcu adımı "Şimdi gireyim / Sonra eklerim" seçenekleriyle atlanabilir | P0 | "Sonra eklerim" seçilirse borç 0 başlar, uygulama hatırlatma yapmaz | Hedef kitle kararı |
| FR-ONB-04 | Hatırlatma adımında bildirim izni istenir | P0 | İzin reddedilirse uygulama çalışmaya devam eder, ayarlarda yeniden istenebilir | — |
| FR-ONB-05 | Onboarding hiçbir vakti işaretletmez | P0 | Onboarding bittiğinde bugünün tüm kalemleri boştur; kurulumdan önce vakti çıkmış namazlar için öneri üretilmez | 5te5 şikayeti |

### 3.2 Günlük takip (TRK)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-TRK-01 | Her gün için sabah, öğle, ikindi, akşam, yatsı ve vitir ayrı işaretlenir; süre sınırı yoktur | P0 | 400. günde de işaretleme çalışır | BR-01 |
| FR-TRK-02 | Vakti girmemiş namaz işaretlenemez | P0 | Dokunulduğunda işaret konmaz, "vakti henüz girmedi" notu görünür | BR-02 |
| FR-TRK-03 | İşaret tek dokunuşla geri alınır | P0 | Geri alma sonrası kayıt "belirsiz" durumuna döner; ilgili otomatik borç önerisi yeniden değerlendirilir. İşaretten sonra birkaç saniye "Geri al" çubuğu görünür. Aynı kaleme 1 saniye içindeki ikinci dokunuş yok sayılır (uygulamada ve widget'ta) | 5te5 şikayeti; DT-11 |
| FR-TRK-04 | Geçmiş bir gün açılıp işaretlenebilir; vakti geçmiş bir namaz işaretlenirken "vaktinde kıldım / sonradan kaza olarak kıldım" sorulur | P0 | "Kaza" seçilirse kayıt kılındı olur ama kaza kaydı da oluşur. Vakit için borç önerisi bekliyorsa borç ekleme ve kaza düşme tek işlemde yazılır, öneri kapanır ve iki kayıt birlikte geri alınır | BR-04; DT-9 |
| FR-TRK-05 | Açık günün tarihi her ekranda görünür | P0 | Bugün, geçmiş ve kaza ekranlarında tarih başlıkta; gece yarısından imsaka kadar Bugün ekranı önceki namaz gününü gösterir | 5te5 şikayeti |
| FR-TRK-06 | Erkek kullanıcıda cuma günü öğle kalemi "Öğle / Cuma" seçimi sunar | P0 | Cuma işaretlenirse öğle kılınmış sayılır. Widget'ın öğle hücresinde de "Cuma" yazar ve dokunuş cuma olarak işaretler | BR-08; DT-11 |
| FR-TRK-07 | Vitir, o günün yatsısı işaretlenmeden işaretlenemez | P0 | Yatsı boşken vitire dokunulduğunda açıklama görünür | BR-03 |
| FR-TRK-08 | Mevcut ve en uzun seri gösterilir; hiçbir veri sıfırlanmaz | P1 | Mazeret günleri seriyi bozmaz | BR-10 |

### 3.3 Kaza (KAZ)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-KAZ-01 | Vakit bazında başlangıç borcu elle girilir ve düzeltilir | P0 | 6 kalem için ayrı sayı; düzeltme bir kayıt olarak saklanır | Özellik listesi #3 |
| FR-KAZ-02 | Kılınan kaza, kullanıcının seçtiği vakitten 1 düşer | P0 | Vakit seçilmeden kaydedilemez: kaza, kaza ekranında ilgili vaktin satırındaki "Kıldım" ile kaydedilir; vakit belirtmeden kaydeden bir eylem yoktur | Karar: kaza düşme sırası; DT-10 |
| FR-KAZ-03 | Vakti çıkmış, işaretlenmemiş namaz için "kılmadım → borca ekle" önerisi sunulur; otomatik ekleme ayarı varsayılan kapalıdır | P0 | Otomatik ekleme kapalıyken hiçbir borç sessizce yazılmaz. Yanıtlanmayan öneriler "Bekleyen öneriler" ekranında birikir; "Hepsini borca ekle" ayrı bir onaydan sonra yazar | BR-04, BR-05; DT-10 |
| FR-KAZ-04 | Borç negatif olamaz | P0 | Borç 0 iken kaza kaydı istenirse "bu vakitte borç yok, eklemek ister misin?" sorulur | — |
| FR-KAZ-05 | Her borç ekleme ve düşme ayrı kayıt olarak tutulur; geçmişi görüntülenir ve tek tek geri alınır | P0 | Kalan borç kayıtlardan hesaplanır; tek işlemde birlikte yazılan kayıtlar (FR-TRK-04) birlikte geri alınır | NFR-REL-02; DT-9 |
| FR-KAZ-06 | Kerahat vaktinde "kaza kıldım" eylemi kapalıdır | P1 | Kerahat penceresinde (BR-07, 45 dk) düğme pasif ve açıklamalı; vitir kazası da kapalı | BR-07 |
| FR-KAZ-07 | Vitir hariç farz borcu 1 ile 5 vakit arasındayken vaktin namazı işaretlenirken tertip bilgi notu gösterilir; vakit darsa not gösterilmez | P1 | Not engellemez, yalnızca bilgilendirir; vakit darlığı BR-17'deki formülle hesaplanır. Yalnızca vitir borcu varsa not gösterilmez. Borç 6'nın altına indiğinde not yeniden gösterilir | BR-17 |
| FR-KAZ-08 | Borç hesaplayıcı (bulûğ tarihinden tahmini borç) | P1 | Bulûğ yaşı bilinmiyorsa önce tahmin istenir; tahmin de yoksa iki cinsiyette de 14 kamerî yaş önerilir. "Kıldığın gün" alanının açıklaması yalnızca kesin bilinen günlerin girilmesini söyler. Sonuç "tahmini" etiketiyle gösterilir ve elle düzeltilebilir | BR-19, BR-20 |
| FR-KAZ-09 | Seferde kaçan namaz "seferde kaçtı" etiketiyle yazılır, kaza ekranında rekât sayısı gösterilir | P1 | Seferde kaçan 4 rekâtlı için "2 rekât" görünür | BR-18 |
| FR-KAZ-10 | Kaza ekranında niyet hatırlatması gösterilir | P1 | Satırdaki "Kıldım"ın yakınında, vakte göre "Niyet: üzerimdeki ilk öğle namazının kazası" biçiminde bir satır görünür; ilk/son seçimi zorunlu değildir, metin yalnızca örnek verir. Eylemi engellemez | Hoca görüşü (4.6) |

### 3.4 Mazeret (MAZ) — yalnızca kadın kullanıcı

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-MAZ-01 | Mazeret başlatılır ve bitirilir; tür (âdet / lohusalık), tarih ve saat seçilir | P0 | Saat seçimi zorunlu; varsayılan şu an. Başlangıç ve bitiş sonradan düzeltilebilir; düzeltme bir veri düzeltmesidir, BR-21'in kapsamına girmez: onaylanmamış öneriler yeniden değerlendirilir, onaylanmış borç kayıtları değişmez | BR-11, BR-12; DT-4 |
| FR-MAZ-02 | Mazeret sürerken vakitler "mazeretli" görünür, borca eklenmez, seriyi bozmaz | P0 | Takvimde nötr renk | BR-10 |
| FR-MAZ-03 | Mazeret, bir vaktin içinde ve o vakit kılınmadan başladıysa o vakit için borç önerilmez | P0 | Örnek: ikindi vaktinde başladı, ikindi işaretsiz → öneri yok. Vaktin çıkmasına az kalmış olması sonucu değiştirmez | BR-11 |
| FR-MAZ-04 | Mazeret bittiğinde, bittiği vaktin namazı BR-12'ye göre farz olduysa kullanıcıya vakit çıkmadan kılabileceği bildirilir; vakit işaretlenmeden çıkarsa borç önerilir | P0 | Bildirim borç yazmaz; borç yalnızca vakit çıktıktan sonra, kullanıcı onayıyla yazılır | BR-12 |
| FR-MAZ-05 | Süre kuralları kontrol edilir: 72 saatten kısa, 240 saatten uzun hayız; 40 günden uzun nifas; kesilip yeniden başlayan kanama; 15 günden kısa temizlik aralığı | P0 | Her durumda açıklamalı uyarı; gerekiyorsa "borca ekleyeyim mi?" sorusu. Hayız 240., nifas 960. saatte dönem kendiliğinden kapanır; kullanıcıdan ayrıca bitiş kaydı istenmez. Dönemin başlangıcından 240 saat geçmeden yeni kanama başlatılırsa yeni dönem açılmaz, önceki dönem yeniden açılır ve aradaki temiz günler mazeretli olur; aradaki günlerin bekleyen önerileri kapanır, onaylanmış borçların geri alınması tek ekranda sorulur. 240 saatten sonra ve 15 günlük temizlik dolmadan başlatılmak istenirse "âdet sayılmaz, namazların devam eder" uyarısı çıkar ve varsayılan seçenek dönemi başlatmamaktır. Nifasın başlangıcından 960 saat geçmeden başlatılan kanama, seçilen türden bağımsız olarak nifasın devamıdır; aradaki günler aynı şekilde mazeretli olur | BR-13 – BR-16; DT-5 |
| FR-MAZ-06 | İsteğe bağlı "âdetim genellikle kaç gün sürer" alanı; her geçerli dönemle güncellenir | P0 | Süre aşımı hesabında bu değer kullanılır; girilmemişse 10 gün kabul edilir. Bitişi kaydedilen dönem 72 saat ile 240 saat arasında sürdüyse ve süresi alışkanlıktan farklıysa alışkanlık bu süreye, 240 saati aştıysa 10 güne güncellenir; uygulama içinde "Alışkanlığın 8 gün olarak güncellendi · Değiştir" bilgisi görünür, bildirim gönderilmez. Kullanıcı değeri her zaman elle değiştirebilir | BR-14 |

### 3.5 Vakitler (VKT)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-VKT-01 | Seçili ilçe kalıcıdır ve hiçbir akışta sıfırlanmaz | P0 | Uygulama güncellemesi, yeniden başlatma ve önbellek temizliği sonrası ilçe aynı | 5te5 şikayeti |
| FR-VKT-02 | En az 30 günlük vakit cihazda tutulur; 10 günden az kalınca arka planda yenilenir | P0 | Uçak modunda 30 gün doğru vakit | NFR-OFF-01 |
| FR-VKT-03 | Önbellek bitmiş ya da hiç dolmamışsa (internetsiz ilk kurulum) ve internet yoksa vakitler Adhan ile hesaplanır ve "tahmini" etiketiyle gösterilir | P0 | Etiket her ekranda görünür; internet gelince vakitler indirilir ve etiket kalkar | DT-1 |
| FR-VKT-04 | Sıradaki vakte kalan süre gösterilir | P0 | Dakika hassasiyetinde. Yalnızca Diyanet'in 6 vakti gösterilir; asr-ı evvel, asr-ı sânî gibi kavramlar arayüzde geçmez | Hoca görüşü (3.2) |
| FR-VKT-05 | *Kaldırıldı (1.0-taslak.7).* İsteğe bağlı asr-ı sânî vakti gösterilmez | — | — | Hoca görüşü (3.2) |

### 3.6 Widget (WDG)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-WDG-01 | Küçük widget: sıradaki vakit, kalan süre, o anki vakit için "kıldım" düğmesi | P0 | Tek dokunuşla işaretlenir. Kalan süre, sıradaki vaktin kesin saatine göre sistemin kendi işleyen sayacıyla gösterilir. Onboarding'in son adımı atlanabilir bir widget ekleme önerisidir | Temel fikir; DT-7, DT-15 |
| FR-WDG-02 | Orta widget: bugünün 6 kalemi, her biri dokunarak işaretlenir | P0 | Kilitli kalemler görsel olarak ayrışır; kilitli kaleme dokunmak işaret koymaz, uygulamayı açıklamayla açar. Vakti çıkmış boş kaleme dokunmak işaret koymaz, uygulamayı "vaktinde kıldım / sonradan kaza olarak kıldım" seçimiyle açar. Erkek kullanıcıda cuma günü öğle hücresinde "Cuma" yazar | BR-02; DT-11 |
| FR-WDG-03 | Widget'tan işaretleme uygulamayı açmadan, uygulamadaki kuralların aynısıyla yapılır | P0 | Aynı kural motoru çağrılır; uygulama ile widget aynı veriyi gösterir | NFR-PRF-02 |
| FR-WDG-04 | Mazeret modunda widget nötr görünür | P0 | "Mazeret" ya da "âdet" kelimesi yazmaz | NFR-SEC-03 |

### 3.7 Hatırlatma (HTR)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-HTR-01 | Vakit çıkmadan X dakika önce, yalnızca o vakit işaretlenmemişse hatırlatma gelir; X ve her vakit ayrı ayarlanır | P0 | İşaretlenmiş vakit için bildirim gelmez | BR-05 |
| FR-HTR-02 | Bildirimde "Kıldım" eylemi vardır | P0 | Bildirimden işaretleme uygulamayı açmaz; işaretten sonra bildirim 5 saniye "Geri al" gösterir. Yatsı ve vitir bildiriminde "Yatsıyı kıldım" ve "İkisini de kıldım" eylemleri vardır | BR-03; DT-12 |
| FR-HTR-03 | Yeniden başlatma, saat/saat dilimi değişimi ve uygulama güncellemesi sonrası alarmlar yeniden kurulur | P0 | Yeniden başlatma testi geçer. Hatırlatmanın 10 dakikadan fazla geç geldiği fark edilirse uygulama, üreticinin pil ayarına yönlendiren kartı bir kez gösterir | DT-14 |
| FR-HTR-04 | Mazeret sürerken hatırlatmalar sessizce durur | P0 | Mazeret bitince kendiliğinden devam eder | BR-10 |
| FR-HTR-05 | Bildirimler veriye dayalıdır ve suçlayıcı dil kullanmaz | P0 | Aktif kullanıcıya "X gündür yoksun" türü bildirim gitmez | 5te5 şikayeti |
| FR-HTR-06 | Vakit başına ezan sesi ve kerahat uyarısı | P1 | — | BR-07 |

### 3.8 Yedekleme (YDK)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-YDK-01 | Tüm veri, şema sürümü taşıyan bir dosyaya dışa aktarılır | P0 | Dosya başka cihazda içe aktarılabilir | NFR-REL-01 |
| FR-YDK-02 | İçe aktarma eski şema sürümlerini dönüştürerek yükler | P0 | v1 dosyası güncel sürüme kayıpsız yüklenir. İçe aktarma mevcut verinin yerine geçer, birleştirme yapılmaz; öncesinde dışa aktarma önerilir. Bozuk ya da daha yeni şema sürümlü dosya reddedilir; işlem yarıda kalırsa mevcut veri değişmez | DT-8 |
| FR-YDK-03 | Mazeret verisi dışa aktarmada kullanıcı parolasıyla şifrelenir | P0 | Yanlış parolayla mazeret verisi açılmaz, diğer veri açılır; parola en az 8 karakterdir ve iki kez girilir; unutulursa verinin kurtarılamayacağı dışa aktarma ekranında yazar | NFR-SEC-02 |
| FR-YDK-04 | Android otomatik yedeği açıktır, hassas veri dosyası hariç tutulur | P0 | Yeni cihaza geri yüklemede takip ve kaza verisi gelir | NFR-SEC-02 |
| FR-YDK-05 | Veritabanı açılamazsa kurtarma ekranı açılır | P0 | İlk seçenek "verilerini dışa aktar"; "verileri sil" son seçenek. Dışa aktarma başarısızsa ham veritabanı kopyası kaydedilebilir; bu kopya hassas veritabanını içermez. Gösterilen hata ayrıntısı kişisel veri içermez | NFR-REL-03; DT-14 |

### 3.9 Ayarlar (AYR)

| ID | Gereksinim | Öncelik | Kabul kriteri | Kural / kaynak |
| --- | --- | --- | --- | --- |
| FR-AYR-01 | Ayarlarda konum, cinsiyet, gusül süresi, hatırlatmalar ve yedekleme değiştirilebilir | P0 | Konum ve cinsiyet değişikliği onay ister. Gusül süresi yalnızca 5–30 dk arasında kabul edilir. Kerahat dakikaları ayarlarda yoktur; BR-07'deki değerler kullanılır | BR-07, BR-12; DT-6 |
| FR-AYR-02 | Ayar değişiklikleri geçmiş günleri yeniden hesaplamaz | P0 | Her gün, kendi tarihindeki ayarın anlık kopyasıyla değerlendirilir | BR-21 |
| FR-AYR-03 | *Kaldırıldı (1.0-taslak.7).* Kerahat dakikaları kullanıcı tarafından değiştirilmez; hoca üç pencere için 45 dk'yı yeterli gördü. Değer `RuleSet` parametresi olarak kalır | — | — | Hoca görüşü (3.5) |

## 4. İş kuralları (BR)

Bu bölüm kural motorunun tek doğruluk kaynağıdır ve tamamı Hanefî mezhebine göredir. Sayısal değerler `RuleSet` parametresi olarak kodlanır; koda sabit olarak gömülmez. Bir kural değişirse önce bu tablo, sonra test, en son kod değişir. Tüm kurallar 28 Ekim 2026'da hoca kontrolünden geçti; "Kaynak" sütunundaki "Hoca (n.n)" atfı hocaya-sorular.md'deki soru numarasıdır.

**Temel ilke (BR-00):** Kural motoru veriyi kendisi değiştirmez, öneri döndürür. Borç yalnızca kullanıcı onayıyla ya da kullanıcının açtığı "otomatik ekleme" ayarıyla yazılır; belirsiz durumlar (mazeret sınırları, sağlık) otomatik eklemenin kapsamında değildir, her zaman sorulur. Tek istisna borç değil ayardır: kullanıcının kaydettiği dönem bitişi, BR-14'e göre âdet alışkanlığını da günceller; bu güncelleme kullanıcıya gösterilir ve geri alınabilir.

### 4.1 Takip ve vakit kuralları

| ID | Kural | Parametre | Kaynak |
| --- | --- | --- | --- |
| BR-01 | Günlük takip kalemleri: sabah, öğle, ikindi, akşam, yatsı (farz) ve vitir (vâcip) | 6 kalem | Fıkhi belge, matris |
| BR-02 | Bir namaz ancak vakti girdikten sonra işaretlenir; vakit girişleri Diyanet'in imsak, öğle, ikindi, akşam ve yatsı saatleridir | Diyanet verisi | Fıkhi belge, vakitler |
| BR-03 | Vitir, o günün yatsısı kılındıktan sonra imsaka kadar kılınır; kılınmazsa kaza edilir | — | TDV, Vitir Namazı; Hoca (3.4) |
| BR-04 | "Kaçırıldı" anları: sabah → güneş; öğle → ikindi girişi (öğle, öğle ile ikindi arasıdır); ikindi → akşam; akşam → yatsı; yatsı ve vitir → imsak. Vakitler İmam-ı Âzam'ın görüşüne göre değil, Diyanet'e göre belirlenir | Tüm sınırlar Diyanet vakitleri | Fıkhi belge; Hoca (3.1, 3.3) |
| BR-05 | Hatırlatma, BR-04'teki vakit sonundan X dk önce kurulur | X varsayılan 30 dk, ayarlanabilir | Fıkhi belge, vakitler |
| BR-06 | *Kaldırıldı (1.0-taslak.7).* Asr-ı sânî hesaplanmaz ve gösterilmez; kullanıcıya yalnızca vakit saatleri gösterilir | — | Hoca (3.2) |
| BR-07 | Kerahat pencereleri: güneş vaktinden sonra 45 dk; öğle vaktinden önceki 45 dk; akşam vaktinden önceki 45 dk. Bu pencerelerde vitir dahil hiçbir kaza kılınmaz. İstisna: akşam kerahatinde o günün kılınmamış ikindisinin farzı işaretlenebilir. Sabah farzından sonra güneşe kadar ve ikindiden sonra kerahate kadar kaza kılınabilir | 45 / 45 / 45 dk; `RuleSet` parametresi, kullanıcı ayarı değil | Hoca (3.5) |
| BR-08 | Erkekte cuma, öğlenin yerine geçer; kaçırılırsa öğle kılınır ve borç öğle olarak yazılır. Kadında cuma isteğe bağlıdır | — | Fıkhi belge; Hoca (4.1) |
| BR-09 | Borca yalnızca farzlar ve vitir yazılır. Sünnetler, bayram namazı ve cuma borç olmaz (cumanın yerine öğle yazılır) | — | Diyanet, Namazların Kazası; Hoca (4.1) |

### 4.2 Mazeret kuralları

| ID | Kural | Parametre | Kaynak |
| --- | --- | --- | --- |
| BR-10 | Hayız ve nifas günlerinde namaz düşer: borca eklenmez, seri bozulmaz | — | TDV, Nifas; Hayız |
| BR-11 | Mazeret, bir vakit girdikten sonra ve o vakit kılınmadan başladıysa o vaktin kazası gerekmez; vaktin sonuna ne kadar kaldığı sonucu değiştirmez | — | Diyanet fetvası; Hoca (1.1) |
| BR-12 | Hayız 240 saatten, nifas 40 günden (960 saat) önce bittiyse: vakit sonuna kalan süre ≥ gusül süresi + iftitah tekbiri süresi ise o vaktin namazı farz olur. Hayız tam 240 saatte, nifas tam 40 günde bittiyse: kalan süre ≥ iftitah tekbiri süresi yeterlidir. Farz olan namaz vakit içinde kılınabilir; vakit işaretlenmeden çıkarsa BR-04'e göre borç önerilir. Mazeret yatsı vaktinde bittiyse ve yatsı farz olduysa vitir de kılınmalıdır; vakit işaretlenmeden çıkarsa ikisi için ayrı öneri | Gusül süresi varsayılan 15 dk, kullanıcı 5–30 dk arasında ayarlar; tekbir süresi 1 dk | Hoca (1.2, 1.3) |
| BR-13 | 72 saatten kısa süren kanama hayız sayılmaz; o süredeki kılınmamış namazlar borca eklenmek üzere önerilir. Hamilelikteki kanama hayız sayılmaz | 72 saat | Fıkhi belge, matris; Hoca (1.4) |
| BR-14 | Kanama alışkanlık gününü aşar ama 240 saat dolmadan keserse tamamı hayızdır, kaza gerekmez. 240 saati aşarsa alışkanlık günleri hayız, sonrası istihâzedir; alışkanlık gününden sonra kılınmayan namazlar borca eklenmek üzere önerilir ve 240. saatten itibaren namaz kılınır. Alışkanlık girilmemişse 10 gün hayız kabul edilir. **Alışkanlığın değişmesi:** bitişi kaydedilen bir dönem 72 saat ile 240 saat arasında sürdüyse ve süresi alışkanlıktan farklıysa, alışkanlık bu süreye güncellenir; tek dönem yeterlidir. 240 saati aşan dönem alışkanlığı 10 gün yapar (12 gün sürse bile 10 yazılır). O dönemin kendi hayız/istihâze hesabı dönem başındaki alışkanlıkla yapılır; yeni değer sonraki dönemlerde kullanılır. Alışkanlık girilmemişse ilk geçerli dönem alışkanlığı belirler. 72 saatten kısa dönem alışkanlığı değiştirmez. Dönem süresi saat ÷ 24 en yakın tam güne yuvarlanarak (yarım gün yukarı) hesaplanır. Güncelleme ileriye dönüktür, geçmiş dönemler yeniden hesaplanmaz (BR-21). Son dönemin bitişi düzeltilirse alışkanlık düzeltilmiş süreye göre yeniden belirlenir | 240 saat; alışkanlık günü (girilmemişse 10) | Hoca (1.5, 1.6, E1, E2); fıkhi belge |
| BR-15 | Nifasın alt sınırı yoktur; 40 günü aşan kısım istihâzedir. Önceki lohusalık süresi dikkate alınmaz. Nifasın başlangıcından 960 saat dolmadan yeniden başlayan kanama nifasın devamıdır; aradaki temiz süre de nifastır ve o süredeki kılınmamış namazlar kaza gerekmez. Bu durumda BR-16'nın 15 gün uyarısı uygulanmaz | 40 gün (960 saat) | TDV, Nifas; Hoca (1.9, E5) |
| BR-16 | **Ara temizlik:** Bir hayız döneminin başlangıcından itibaren 240 saat dolmadan kanama yeniden başlarsa iki kanama ve aradaki temiz süre tek dönemdir; aradaki süre de hayızdır ve o süredeki kılınmamış namazlar kaza gerekmez. Tek dönemin toplam süresi BR-13 ve BR-14'e göre değerlendirilir; toplam 240 saati aşarsa alışkanlık günleri hayız, sonrası istihâzedir ve dönem kapanınca alışkanlık 10 olur (hocanın uygulamaya bıraktığı karar, E3). **En az temizlik:** iki ayrı hayız arasındaki temizlik en az 15 gündür. Dönemin başlangıcından 240 saat geçtikten sonra ve bitişinden 15 gün dolmadan başlayan kanama hayız değil istihâzedir: namaz düşmez, kılınmayan namaz için normal kurallarla borç önerilir; kullanıcı uyarılır | 240 saat; 15 gün | Hoca (1.7, 1.8, E3); fıkhi belge, matris |

### 4.3 Kaza ve hesap kuralları

| ID | Kural | Parametre | Kaynak |
| --- | --- | --- | --- |
| BR-17 | Tertip: vitir hariç farz borcu 1 ile 5 vakit arasındaysa önce kaza (vitir dahil), sonra vaktin namazı kılınır; vakit darsa önce vaktin namazı. Farz borcu yoksa, yalnızca vitir borcu varsa tertip notu gösterilmez. Borç 6 veya üstüne çıkınca tertip düşer; kaza kılınarak 6'nın altına inerse yeniden geçerli olur. Vakit dar sayılır: kalan süre < (kaza borçlarının farz ve vitir rekâtları + vaktin farz rekâtı) × rekât süresi. Kalan süre vaktin sonuna göre ölçülür; ikindide müstehap vaktin sonu olan akşam kerahatinin başlangıcına göre ölçülür. Vakit darsa tertip notu gösterilmez. Not bilgi amaçlıdır, işareti engellemez | 6; rekât süresi 2 dk | Diyanet, Namazların Kazası; Hoca (4.2, 4.3) |
| BR-18 | Seferde kaçan 4 rekâtlı namaz her yerde 2 rekât; evde kaçan seferde de 4 rekât kaza edilir | — | TDV, Kazâ; Hoca (4.4) |
| BR-19 | Bulûğ alt sınırı erkekte 12, kızda 9 kamerî yaş. Bilinmiyorsa önce kullanıcıdan tahmin istenir; tahmin de yoksa ihtiyaten iki cinsiyette de 14 kamerî yaş önerilir. Tarih, doğum tarihi hicrî takvime çevrilerek tam hesaplanır. Çevrim Ümmü'l-Kurâ takvimiyle yapılır (Java HijrahDate); hicrî takvimler arasında 1–2 gün fark olabileceğinden sonuç "yaklaşık" etiketiyle gösterilir ve kullanıcı düzeltebilir. Örnek: 15 Temmuz 2010 doğumlu → 13 Şubat 2024 | 12 / 9; öneri 14 | TDV, Bulûğ; Hoca (5.1, E4) |
| BR-20 | Tahmini borç = (bulûğdan düzenli başlangıca gün sayısı − mazeret günleri − kılındığı kesin bilinen günler) × 6. Kılındığından emin olunmayan günler düşülmez, borca katılır. Sonradan Müslüman olan kullanıcıda başlangıç Müslüman olduğu tarihtir | — | Fıkhi belge; Hoca (5.2, 5.3) |
| BR-21 | Her gün, o tarihte geçerli olan ayarların anlık kopyasıyla değerlendirilir; ayar değişikliği geçmişi yeniden hesaplamaz | — | Fıkhi belge, onboarding |
| BR-22 | Baygınlık, koma, akıl kaybı, narkoz ve ilaçla bilinç kaybı ya da başını oynatamayacak ağır hastalık süresince vakti çıkan farz namazlar sayılır: vakit bitişi t, başlangıç < t ≤ bitiş ise sayılır; vitir ayrıca sayılmaz. Sayı 6 veya fazlaysa bu süredeki namazlar düşer. 5 veya azsa vakti çıkan farzlar, yatsı da aralarındaysa vitir, kaza edilmek üzere önerilir. Bitiş anında vakti hâlâ süren namaz normal kurallarla kılınır ("sağlık nedeniyle kılınamadı" kaydı, P1) | 6. farz vaktin çıkışı | Diyanet fetvası; Merâki'l-Felâh (SeekersGuidance aracılığıyla); Hoca (2.1–2.3) |

## 5. Fonksiyonel olmayan gereksinimler (NFR)

Rakibin en ağır şikayetleri fonksiyondan değil güvenilirlikten geldiği için bu bölümdeki hedefler fonksiyonel gereksinimler kadar bağlayıcıdır.

| ID | Gereksinim | Ölçüt / doğrulama |
| --- | --- | --- |
| NFR-REL-01 | Hiçbir güncelleme veya şema geçişi kullanıcı verisini silmez; yıkıcı geçiş yasaktır | Her sürüm için geçiş testi; v1 → güncel sürüm testi |
| NFR-REL-02 | Kaza borcu olay kayıtlarından türetilir; tüm yazmalar işlem (transaction) içindedir | Yazma ortasında uygulama öldürülme testi; veri tutarlı |
| NFR-REL-03 | Uygulama hiçbir veri durumunda açılışta takılı kalmaz; hata durumunda kurtarma ekranı açılır | Bozuk veritabanı dosyasıyla açılış testi |
| NFR-REL-04 | Çökmeyen kullanıcı oranı en az %99 | Play Console Android vitals |
| NFR-PRF-01 | Soğuk açılışta bugün ekranı orta segment bir cihazda 2 saniyenin altında görünür | Macrobenchmark |
| NFR-PRF-02 | Widget dokunuşundan widget'ın güncellenmesine kadar 1 saniyenin altında | Elle ölçüm, iki cihaz |
| NFR-PRF-03 | Geçmiş bir günde işaretleme animasyon beklemeden tepki verir | 300 ms altı |
| NFR-OFF-01 | Takip, kaza, mazeret, widget ve hatırlatma internetsiz çalışır; vakitler en az 30 gün önbellekte | Uçak modunda 30 günlük test |
| NFR-ACC-01 | Diyanet verisi kullanılırken vakitler Diyanet takvimiyle birebir aynıdır; Adhan yedeğinin sapması hedefte ±2 dk | 5 ilçe, yaz ve kış karşılaştırması |
| NFR-TIM-01 | Hatırlatmalar planlanan andan en fazla 10 dk sapmayla gelir; kesin alarm izni varsa ±1 dk | Yeniden başlatma ve Doze testleri |
| NFR-SEC-01 | Uygulama kişisel veri toplamaz; ara sunucuya yalnızca ilçe kimliği gider; analitik ve reklam SDK'sı yoktur | Ağ trafiği incelemesi; bağımlılık listesi |
| NFR-SEC-02 | Mazeret verisi ayrı dosyada tutulur, otomatik yedeğe girmez; dışa aktarmada parolayla (PBKDF2/Argon2 + AES-GCM) şifrelenir; parola saklanmaz | Birim testi; yedek dosyası incelemesi |
| NFR-SEC-03 | Mazeret bilgisi widget'ta, bildirimde, kilit ekranında ve son uygulamalar önizlemesinde görünmez; mazeretten doğan bildirimlerin metni nötrdür, açıklama yalnızca uygulama içinde görünür. Mazeret ekranları ve mazeret sürerken Bugün ekranı son uygulamalar önizlemesinde boş görünür ve ekran görüntüsü alınamaz; bildirim kanallarının adlarında mazeret geçmez. Alışkanlık güncellemesi bildirimle değil yalnızca uygulama içinde duyurulur | Elle kontrol listesi |
| NFR-SEC-04 | Yalnızca gerekli izinler istenir: internet, bildirim, isteğe bağlı kesin alarm; konum izni istenmez | Manifest incelemesi |
| NFR-USA-01 | Bir vakti işaretlemek widget'tan 1, uygulamadan en fazla 2 dokunuştur | Kullanılabilirlik testi |
| NFR-USA-02 | Tüm akışlar TalkBack ve %200 yazı boyutuyla tamamlanabilir | Erişilebilirlik testi |
| NFR-USA-03 | Metinler Türkçe, suçlayıcı olmayan dilde; dini terimler sözlükteki anlamıyla kullanılır. Uygulama "bir hocaya danışın" türünde yönlendirme yapmaz; kaynak olarak Din İşleri Yüksek Kurulu fetvaları gösterilebilir | Metin incelemesi |
| NFR-CMP-01 | Android 8.0 (API 26) ve üstü; hedef SDK Play'in güncel zorunluluğuna uyar | Test matrisi |
| NFR-CMP-02 | Yurt dışı ilçelerde ve yaz saati geçişlerinde vakitler ve hatırlatmalar doğru | Saat dilimi testleri |
| NFR-MNT-01 | `:core:domain` test kapsamı en az %90 ve Android bağımlılığı yok | CI raporu |
| NFR-MNT-02 | Mezhep kuralları `RuleSet` parametrelerindedir; yeni mezhep kod yapısı değişmeden eklenebilir | Kod incelemesi |
| NFR-MNT-03 | *Kaldırıldı (1.0-taslak.9).* Mimari kararlar ADR olarak tutulmaz; kararlar proje belgelerinde (SRS, karar tablosu, mimari-ve-kodlama-ilkeleri.md) durur. 29 Ekim 2026 kararı | — |

## 6. Veri ve dış arayüz gereksinimleri

### 6.1 Veri sözlüğü

| Varlık | Temel alanlar | Saklama yeri | Otomatik yedek |
| --- | --- | --- | --- |
| PrayerRecord | tarih, kalem (6), durum (kılındı / kılınmadı / mazeretli / belirsiz), işaretlenme anı, cuma mı, kaza olarak mı kılındı | Ana veritabanı | Evet |
| KazaEntry | kimlik, kalem, kaynak tarih (biliniyorsa), tür (elle / öneri / otomatik / hesaplayıcı / düzeltme), adet, düzelttiği kayıt (yalnızca geri alma düzeltmesinde), seferde kaçtı mı, oluşturulma anı | Ana veritabanı | Evet |
| KazaPerformed | kimlik, kalem, kılınma anı | Ana veritabanı | Evet |
| MazeretPeriod | tür (hayız / nifas), başlangıç anı, bitiş anı, kanama aralıkları (ara temizliği olan dönemde birden çok), uyarılı mı | Hassas veritabanı | Hayır |
| PrayerTimesDay | ilçe, tarih, 6 vakit, kaynak (Diyanet / tahmini) | Önbellek tablosu | Hayır (yeniden indirilir) |
| Settings | ilçe, cinsiyet, gusül süresi, hatırlatma ayarları, otomatik ekleme, âdet alışkanlığı ve son güncellenme kaynağı (elle / dönem) | DataStore | Evet (âdet alışkanlığı hariç) |
| DaySnapshot | tarih, o güne ait ayar ve kural seti sürümü | Ana veritabanı | Evet |

- **DATA-01** Veri süresiz ve yalnızca cihazda tutulur.
- **DATA-02** Kullanıcı ayarlardan tüm verisini silebilir; silmeden önce dışa aktarma önerilir ve iki adımlı onay istenir.
- **DATA-03** Zamanlar `Instant` olarak, gösterim ilçenin saat dilimiyle yapılır.
- **DATA-04 Kaza defteri.** Kaza borcu bir sayaç değil, KazaEntry ve KazaPerformed kayıtlarından oluşan bir defterdir (FR-KAZ-05, NFR-REL-02). Kurallar:
  - **Kimlik:** Her kayıt oluşturulurken benzersiz bir kimlik alır; kimlik iki tabloda ortaktır, böylece bir düzeltme her iki türden kayda işaret edebilir.
  - **Tür:** *elle* = kullanıcının doğrudan girdiği borç (başlangıç borcu, KS-ONB-04; "önce ekleyip sonra düşeyim" onayı, KS-KAZ-07). *öneri* = kullanıcının onayladığı kaçırıldı önerisi (KS-KAZ-04, 08, 09). *otomatik* = otomatik ekleme ayarıyla yazılan borç (KS-KAZ-06). *hesaplayıcı* = borç hesaplayıcının sonucu (FR-KAZ-08). *düzeltme* = borcu elle düzenleme ya da bir kaydı geri alma.
  - **Adet:** Bir kaydın borca etkisidir. Düzeltme dışındaki türlerde pozitif tam sayıdır (başlangıç borcu kalem başına tek kayıttır, ör. öğle 8). Düzeltmede sıfırdan farklı, işaretli tam sayıdır (KS-KAZ-01: "düzeltme +2").
  - **Kalan borç** (kalem başına) = o kalemin KazaEntry adetlerinin toplamı − o kalemin KazaPerformed sayısı. Hiçbir zaman negatif olmaz (FR-KAZ-04).
  - **Geri alma** (FR-KAZ-05): Kayıt silinmez; karşı etkili bir düzeltme kaydı eklenir ve geri alınan kayda işaret eder. KazaEntry geri alınırsa düzeltmenin adedi kaydın adedinin eksi işaretlisidir; KazaPerformed geri alınırsa +1'dir. Bir kayıt yalnızca bir kez geri alınır. Düzeltme kayıtları geri alınmaz; elle düzenlemenin etkisi yeni bir düzenlemeyle değiştirilir. Kalan borcu negatife düşürecek geri alma yapılmaz.
  - **Elle düzenleme** (FR-KAZ-01): Kullanıcı bir kalemin borcunu yeni bir sayıya ayarlar. O kalemde hiç kayıt yoksa *elle* kaydı, varsa aradaki fark kadar *düzeltme* kaydı yazılır. Negatif sayı kabul edilmez.

### 6.2 Dış arayüzler

| ID | Arayüz | Gereksinim |
| --- | --- | --- |
| IF-01 | Uygulama ↔ ara sunucu | HTTPS; `GET /locations?parent={id}`, `GET /times?district={id}&month=YYYY-MM` (günlük 6 vakit), `GET /health`. `/locations` yalnızca gömülü il/ilçe listesini güncellemek için kullanılır (A-02). Kullanıcıya ait hiçbir parametre gönderilmez |
| IF-02 | Ara sunucu ↔ Diyanet API | Resmi servis: awqatsalah.diyanet.gov.tr. Giriş POST /api/Auth/Login (e-posta + şifre) ile, istekler Bearer token ile yapılır; doküman token süresini 30 ya da 45 dk, yenileme süresini 15 dk olarak veriyor. Kullanılacak uç noktalar: /api/Place/Countries, /api/Place/States/{countryId}, /api/Place/Cities/{stateId}, /api/PrayerTime/Monthly/{cityId}. İstek sınırları: Developer rolünde günde toplam 100 istek; Standard rolünde her uç nokta ve her parametre için günde 5 istek. Bu yüzden konum listeleri bir kez çekilip sunucuda saklanır, aylık vakitler ilçe başına ayda bir çekilir. Doküman, verinin geliştiricinin kendi sistemlerinde barındırılabileceğini belirtiyor. Hesap bilgileri ve token yalnızca sunucuda gizli değişkende tutulur |
| IF-03 | Uygulama ↔ Android | AlarmManager (pencereli, izin varsa kesin), vakit başına bildirim kanalı (yatsı ve vitir tek kanal) ve nötr adlı bir "Bilgilendirmeler" kanalı, Glance widget, `BOOT_COMPLETED` / saat ve saat dilimi değişimi / paket güncellemesi alıcıları, `dataExtractionRules` |
| IF-04 | Yedek dosyası | JSON: `schemaVersion`, `exportedAt`, `appVersion`, kayıt dizileri, ayarlar ve şifreli bir `sensitive` bloğu (`salt`, `iv`, `ciphertext`) |

## 7. Kapsam dışı (v1.0)

Aşağıdakiler v1.0'da yapılmaz; geliştirme sırasında bu konularda gelen fikirler doğrudan özellik listesinin P1/P2 bölümüne yazılır.

| Konu | Neden kapsam dışı | Ne zaman |
| --- | --- | --- |
| Şafiî, Mâlikî, Hanbelî | İlk sürüm Hanefî; kural motoru hazır tutulur | Sonraki güncellemeler |
| iOS | Tek geliştirici, Android önce | P2 |
| Günlük video, yazı, soru-cevap içeriği | Rakibin güçlü alanı; tek kişi sürdüremez | Planlanmıyor |
| Ağır oyunlaştırma (ağaç, bahçe) | Hafif seri yeterli | Planlanmıyor |
| Kullanıcı hesabı, bulut senkronizasyonu | Gizlilik ve basitlik; yerel yedek yeterli | Bulut yedek P2 |
| Kıble pusulası | Pek çok uygulama zaten sunuyor | P2 |
| Çoklu profil (aile/çocuk) | Veri modeli tek kullanıcılı | P2 |
| Oruç kazası, nafile namaz takibi | Kapsamı büyütür. Nafile eklendiğinde hocanın bildirdiği nafile kılınmayan vakitler uygulanacak (fıkhi belge, vakitler 5) | P2 |
| Asr-ı sânî ve işâ-i sânî gösterimi | Hoca görüşü: yalnızca Diyanet vakitleri gösterilir | Planlanmıyor |
| Kerahat dakikalarının kullanıcı ayarı | Hoca 45 dk'yı yeterli gördü; kullanıcının değeri düşürmesi yanlış sonuç verir | Planlanmıyor |
| Kaza varken nafile ve sünnet uyarısı | Hoca görüşü: uygulamada gerek yok | Planlanmıyor |
| "Bir hocaya danışın" yönlendirmesi | Hoca görüşü: şimdilik gerek yok | Planlanmıyor |
| Lohusalık alışkanlığı | Hoca görüşü: 40 günü aşan kısım herkes için istihâze | Planlanmıyor |
| Reklam, ücretli özellik, analitik | Kısıt C-04 | Planlanmıyor |

## 8. İzlenebilirlik ve doğrulama

Her gereksinimin en az bir testi olur ve testin adı gereksinim kimliğini taşır. Örnek: `BR12_hayiz240SaattenOnceBitti_gusulVeTekbirSigarsa_vakitBorcOlur`. Böylece bir test kırıldığında hangi kuralın bozulduğu adından okunur; bir kural değiştiğinde de etkilenen testler aramayla bulunur.

| Grup | Yol haritası fazı | Doğrulama yöntemi | Test yeri |
| --- | --- | --- | --- |
| BR-00 – BR-22 (BR-06 hariç) | Faz 2 | Karar tablosundan parametreli birim testleri; sınır değerleri (72. ve 240. saat, vaktin son dakikası, ara temizlikte 240. saat) | `:core:domain` |
| FR-TRK, FR-KAZ, FR-MAZ | Faz 2 (mantık), Faz 5 (ekran) | Birim testleri + kritik akışlar için UI testleri | `:core:domain`, `:feature:*` |
| FR-VKT, NFR-OFF, NFR-ACC | Faz 3 | Entegrasyon testi, uçak modu testi, Diyanet karşılaştırması | `:core:prayertimes` |
| FR-YDK, NFR-REL, NFR-SEC-02 | Faz 4 | Geçiş testleri, dışa/içe aktarma döngüsü, yazma ortasında öldürme | `:core:data` |
| FR-ONB, FR-AYR, NFR-USA | Faz 5 | UI testleri, TalkBack kontrolü, kullanılabilirlik testi | `:feature:*` |
| FR-WDG, FR-HTR, NFR-TIM, NFR-PRF-02 | Faz 6 | Cihaz üzerinde elle test, Doze ve yeniden başlatma senaryoları | `:widget`, `:app` |
| NFR-REL-04, NFR-CMP, NFR-SEC-01/04 | Faz 7 | Android vitals, test matrisi, manifest ve ağ incelemesi | Yayın kontrol listesi |

**v1.0 yayın kapısı:** Tüm P0 FR'lerin kabul kriterleri karşılanmış ve kabul senaryoları (kabul-senaryolari.md) geçmiş, tüm BR testleri yeşil, NFR ölçütleri ölçülmüş ve hedefte, Bölüm 10'daki açık konular kapanmış.

## 9. Değişiklik yönetimi

Gereksinimlerden şaşmamanın yolu değişikliği yasaklamak değil, her değişikliği aynı yoldan geçirmektir.

1. **Talebi yaz:** Ne değişecek, neden, kaynağı ne (yorum, test bulgusu, hoca görüşü).
2. **Sınıflandır:** İş kuralı (BR), fonksiyonel (FR), fonksiyonel olmayan (NFR) ya da kapsam değişikliği.
3. **Etkisini çıkar:** Hangi FR, BR ve testler etkileniyor? Veri modeli değişiyorsa geçiş planı yazılır.
4. **Onayla:**
   - BR değişikliği hoca kontrolü olmadan kabul edilmez.
   - P0'a yeni bir madde ancak başka bir P0 maddesi P1'e indirilerek girer.
5. **Sırayla uygula:** Önce bu belge (sürüm notuyla), sonra test, en son kod.

| Belge sürümü | Tarih | Değişiklik |
| --- | --- | --- |
| 1.0-taslak | 26 Ekim 2026 | İlk sürüm: 50 FR, 23 BR, 22 NFR |
| 1.0-taslak.2 | 26 Ekim 2026 | BR-04: öğlenin kaçırıldı sınırı asr-ı sânîden Diyanet ikindi vaktine çekildi; BR-05 ve BR-06 buna göre güncellendi |
| 1.0-taslak.3 | 26 Ekim 2026 | BR-12: gusül varsayılanı 15 dk ve nifas bitişi eklendi; BR-14: alışkanlık günü uygulaması ayrıntılandırıldı; FR-MAZ-06 ve sözlük buna göre güncellendi. BR-12'nin nifas kısmı ve BR-14 ayrıntısı hoca teyidi bekliyor |
| 1.0-taslak.4 | 26 Ekim 2026 | Karar tablosunda bulunan 6 açık nokta kapatıldı. BR-12 ve FR-MAZ-04: "borç olur" yerine "farz olur, vakit içinde kılınmazsa borç önerilir"; yatsı vaktinde biten mazerette vitir eklendi. BR-14 ve FR-MAZ-06: alışkanlığın değişmesi (art arda iki dönem) eklendi. BR-17 ve FR-KAZ-07: vakit darlığı formülü eklendi (rekât süresi 2 dk, ikindide müstehap vakit sonu). BR-19: Ümmü'l-Kurâ takvimi ve "yaklaşık" etiketi eklendi. BR-22: sayım "vakti çıkan farz namazlar" olarak netleştirildi |
| 1.0-taslak.5 | 27 Ekim 2026 | Kabul senaryoları yazılırken çıkan 5 karar işlendi. FR-ONB-05: kurulumdan önceki vakitler için öneri yok. FR-TRK-05 ve sözlük: namaz günü imsakta değişir. FR-WDG-02: kilitli kalem uygulamayı açıklamayla açar. FR-YDK-03: parola en az 8 karakter, iki kez girilir. NFR-SEC-03: mazeret bildirimleri nötr metinli. Referanslara karar-tablosu.md ve kabul-senaryolari.md eklendi; 1.0-taslak.4 BR değişiklikleri için hoca kontrolü açık konu olarak eklendi |
| 1.0-taslak.6 | 27 Ekim 2026 | Ekran taslakları (Tur 1 – 5) sırasında alınan 13 karar işlendi; talep, sınıf ve etki ekran-taslaklari-tur5.md Bölüm 5'te (DT-1, DT-4 – DT-15). A-02, IF-01, FR-ONB-01, FR-VKT-03: il/ilçe listesi gömülü, ilk kurulum internetsiz; Diyanet izni ön koşul (DT-1). FR-MAZ-01: dönem düzeltme (DT-4). FR-MAZ-05: 240. ve 960. saatte otomatik kapanma (DT-5). FR-AYR-01: kerahat dakikaları ayarı P1'e taşındı ve FR-AYR-03 olarak ayrıldı (DT-6). FR-WDG-01: işleyen geri sayım (DT-7) ve onboarding'de widget adımı (DT-15). FR-YDK-02: içe aktarma mevcut verinin yerine geçer (DT-8). FR-TRK-04, FR-KAZ-05: kaza olarak kılınan öneri tek işlem (DT-9). FR-KAZ-02, FR-KAZ-03: kaza satırdan, Bekleyen öneriler, onaylı toplu ekleme (DT-10). FR-WDG-02, FR-TRK-03, FR-TRK-06: widget'ta cuma, vakti çıkmış hücre, çift dokunuş, geri alma çubuğu (DT-11). FR-HTR-02: 5 saniyelik "Geri al", yatsı–vitir iki eylem (DT-12). NFR-SEC-03, IF-03: önizleme ve ekran görüntüsü engeli, nötr kanal adları (DT-13). FR-YDK-05, FR-HTR-03: kurtarmada ham kopya, pil yönlendirmesi (DT-14). FR-ONB-03: düğme adları ekranla eşitlendi. Yeni P0 yok, hiçbir BR değişmedi; FR sayısı 51 (43 P0, 8 P1). Kabul senaryoları 95'e çıktı; Bölüm 10'a ekran metinlerinin hoca kontrolü eklendi |
| 1.0-taslak.7 | 28 Ekim 2026 | **Hoca görüşü işlendi** (talep: hocaya-sorular.md; cevaplar fikhi-gereksinimler.md "Hoca görüşü kaydı"). Kuralların çoğu olduğu gibi onaylandı; kaynak sütunlarına "Hoca (n.n)" atfı eklendi. **BR değişiklikleri (hoca onaylı):** BR-06 kaldırıldı (asr-ı sânî gösterilmez). BR-07: kerahat 50/45/50 → 45/45/45 dk, vitir kazası açıkça dahil, kullanıcı ayarı değil. BR-12: gusül aralığı 5–60 → 5–30 dk. BR-14: alışkanlık art arda iki dönemde değil, tek dönemde güncellenir; 240 saati aşan dönem alışkanlığı değiştirmez. BR-16: 240 saat içinde yeniden başlayan kanama önceki dönemle birleşir, aradaki temizlik hayızdır; 240 saatten sonra ve 15 gün dolmadan başlayan kanama istihâzedir. BR-17: yalnız vitir borcunda not yok; borç 6'nın altına inince tertip geri döner. BR-19: bulûğ yaşı bilinmiyorsa tahmin, yoksa kızda 14 kamerî yaş. BR-20: emin olunmayan günler borca katılır; Müslüman olma tarihi. BR-11, BR-13, BR-15, BR-22: hocanın netleştirdiği ayrıntılar eklendi (vaktin sonu, hamilelik, lohusalık alışkanlığı yok, narkoz). BR-00: alışkanlık güncellemesi istisnası. **FR değişiklikleri:** FR-VKT-05 kaldırıldı (P1); FR-AYR-03 kaldırıldı (P1, kapsamı daraltıyor); FR-KAZ-10 niyet hatırlatması eklendi (P1). FR-MAZ-05, FR-MAZ-06, FR-KAZ-06 – 08, FR-AYR-01, FR-VKT-04, FR-MAZ-03 kabul kriterleri güncellendi. **NFR:** NFR-SEC-03 (alışkanlık güncellemesi bildirimle duyurulmaz), NFR-USA-03 (danışma yönlendirmesi yok, Diyanet fetvası kaynak gösterilebilir). **Veri:** MazeretPeriod'a kanama aralıkları ve uyarılı alanı, Settings'e alışkanlığın güncellenme kaynağı eklendi; kerahat dakikaları Settings'ten çıktı. Bölüm 7'ye beş kapsam dışı madde eklendi. Yeni P0 yok; FR sayısı 50 (43 P0, 7 P1). Karar tablosu v1.4'e, kabul senaryoları 98 senaryoya güncellendi. Bölüm 10'da hoca kontrolü kapandı; türetilmiş noktaların teyidi ve ekran taslaklarına yansıtma açık konu olarak eklendi |
| 1.0-taslak.8 | 28 Ekim 2026 | **İkinci hoca görüşmesi:** 1.0-taslak.7'de türetilen beş nokta soruldu (E1 – E5, fikhi-gereksinimler.md). BR-14: 240 saati aşan dönem alışkanlığı 10 gün yapar (önceki "değiştirmez" varsayımı yanlıştı); ilk dönemin alışkanlığı belirlemesi onaylandı. BR-16: birleşik dönem 240 saati aşınca BR-14'ün tüm döneme uygulanması, hocanın uygulamaya bıraktığı karar olarak kesinleşti. BR-19: bulûğ yaşı bilinmiyorsa erkekte de 14 kamerî yaş. BR-15: nifasın 960 saati içinde yeniden başlayan kanama nifasın devamı, aradaki temizlik nifastır. FR-MAZ-05, FR-MAZ-06, FR-KAZ-08 ve sözlük güncellendi. Yeni FR ya da P0 yok. Karar tablosu v1.5 (101 senaryo), kabul senaryoları 99 senaryo. Bölüm 10'daki teyit maddesi kapandı |
| 1.0-taslak.9 | 30 Ekim 2026 | **Belge tutarlılığı.** Talep: CLAUDE.md hazırlanırken belgeler arasında çelişki bulundu. Sınıf: NFR (kapsam daraltma). Etki: NFR-MNT-03 kaldırıldı; 29 Ekim 2026'da ADR yazılmaması kararlaştırılmıştı (yol haritası Faz 1), kararlar proje belgelerinde durur. Hiçbir BR, FR ya da test değişmedi. Referanslara mimari-ve-kodlama-ilkeleri.md ve CLAUDE.md eklendi. Bölüm 10'da Diyanet başvurusunun durumu güncellendi. Aynı gün yol haritası ve özellik listesindeki eski ifadeler (asr-ı sânî, kerahat ayarı, baygınlık eşiği, Faz 0 durumu, Faz 2 varlıkları) bu belgeyle uyumlu hale getirildi |
| 1.0-taslak.10 | 2 Kasım 2026 | **Kaza defterinin veri modeli.** Talep: Faz 2 kartı F2-03 (kaza defteri) yazılırken KS-ONB-04, KS-KAZ-01, 07, 08 ve 09'un gerektirdiği alanların veri sözlüğünde olmadığı görüldü: kayıt adedi, onaylanmış önerinin türü ve geri alınan kaydın kimliği. Sınıf: veri modeli (FR değişmedi, BR değişmedi; hoca kontrolü gerekmez). Etki: KazaEntry'ye kimlik, "öneri" türü, adet ve düzelttiği kayıt; KazaPerformed'a kimlik eklendi. DATA-04 (kaza defteri kuralları: tür anlamları, adet, kalan borç, geri alma, elle düzenleme) eklendi. Henüz veritabanı yok (Faz 4); geçiş gerekmez. Birlikte yazılan ve birlikte geri alınan kayıtlar (FR-TRK-04, KS-KAZ-10) için işlem kimliği F2-04'te eklenecek |

## 10. Açık konular

Bu maddeler kapanmadan v1.0 yayın kapısı geçilmez; çoğu Faz 0'da kapanmalıdır.

- [ ] **Diyanet erişimi (A-01):** Araştırıldı (26 Ekim 2026): resmi Namaz Vakitleri REST servisi var; uç noktalar, istek sınırları ve token süreleri IF-02'ye işlendi. Hesap başvuru formu 29 Ekim 2026'da gönderildi, yanıt bekleniyor. Başvuru olumsuz sonuçlanırsa vakit kaynağı Adhan'a geçer ve NFR-ACC-01 yeniden yazılır. Başvuruda il/ilçe listesinin uygulama paketine gömülmesi için de izin istenecek (A-02, DT-1).
- [x] **Karar tablosunun hoca kontrolü (A-03):** Özellikle BR-11 – BR-16 ve BR-22.
- [x] **BR-04, öğlenin sonu:** Karar verildi: öğle, öğle ile ikindi vakti arasıdır; "kaçırıldı" sınırı Diyanet'in ikindi vaktidir. Hoca onayladı (28 Ekim 2026).
- [x] **BR-12 parametreleri:** Karar: tekbir süresi 1 dk; gusül süresi varsayılan 15 dk, kullanıcı 5–30 dk arasında ayarlar (hoca görüşüyle 60 → 30).
- [x] **BR-12'nin nifasa uygulanması:** Karar: evet; nifas 40 günden önce biterse gusül + tekbir, tam 40 günde biterse yalnızca tekbir süresi aranır (BR-12). Hoca onayladı.
- [x] **BR-14 ayrıntısı:** Karar: BR-14 satırına işlendi. Alışkanlığın değişmesi hoca görüşüyle tek döneme çekildi (1.0-taslak.7).
- [x] **BR-05 varsayılanı:** 30 dakikalık hatırlatma süresinin kullanılabilirlik testinde doğrulanması.
- [x] **Karar tablosundaki 6 açık nokta:** Karar verildi (26 Ekim 2026); gerekçeler karar-tablosu.md v1.1'de.
- [x] **Kabul senaryoları:** Yazıldı (27 Ekim 2026): kabul-senaryolari.md; 28 Ekim 2026 itibarıyla 99 senaryo. 43 P0 gereksinimin tümü kapsanıyor.
- [x] **Ekran taslakları:** Tamamlandı (27 Ekim 2026): ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md ve tasarım panosu. 43 P0 gereksinimin tümü en az bir ekranla eşleşiyor; Faz 5'e devredilen kontroller Tur 5 Bölüm 6'da.
- [x] **1.0-taslak.4 BR değişikliklerinin hoca kontrolü:** Kapandı (28 Ekim 2026). Yatsı vaktinde biten mazerette vitir ve baygınlıkta vakit sayımı onaylandı; alışkanlığın değişmesi tek döneme çekildi. Tüm sonuçlar 1.0-taslak.7'de.
- [x] **Hoca görüşünden türetilen noktaların kısa teyidi:** Kapandı (28 Ekim 2026, ikinci görüşme). 10 günü aşan dönem alışkanlığı 10 gün yapar; ilk dönem alışkanlığı belirler; birleşik dönemde BR-14'ün tüm döneme uygulanması hocanın uygulamaya bıraktığı karar; bulûğda iki cinsiyete de 14; lohusalıkta 40 gün içindeki her kan lohusalık. Sonuçlar 1.0-taslak.8'de. Bir okuma notu fikhi-gereksinimler.md'de: 10 günü aşan dönemin kendi hesabı dönem başındaki alışkanlıkla yapılır, 10 günlük alışkanlık sonraki dönemlerden itibaren geçerlidir.
- [ ] **Ekran taslaklarına yansıtma (Faz 5):** Tur 3 ve Tur 4'teki şu ekranlar 1.0-taslak.7 ve 1.0-taslak.8'e göre güncellenecek: Mazeret başlat (15 gün uyarısı iki duruma ayrılır: birleşme ve "âdet sayılmaz"; yeni metinler KS-MAZ-11 ve KS-MAZ-17'de), Geçmiş dönemler ve alışkanlık (güncelleme diyaloğu yerine "Alışkanlığın X gün olarak güncellendi · Değiştir" satırı; KT-87 diyaloğu kaldırılır; 10 günü aşan dönemin "10 gün doldu" sonuç ekranına da aynı satır eklenir), lohusalığın devamı bilgisi (KS-MAZ-19), Ayarlar ve gusül seçici (5–30 dk; hata metni "5 – 30 dk"), Kaza ekranı kerahat bandı (P1; "07:00 – 07:45"), Kaza ekranı niyet satırı (P1, FR-KAZ-10), Borç hesaplayıcı (P1; tahmin adımı ve 14 önerisi). Birleşen dönem için onaylanmış borçları geri alma ekranı (KT-96) Toplu onay bileşeniyle çizilecek.
- [ ] **Ekran metinlerinin hoca kontrolü:** 28 Ekim görüşmesi kuralları kapsadı; ekran metinleri ayrıca gösterilmedi. Kontrol edilecekler: mazeret sonuç metinleri ("bu vaktin namazı sana farz oldu", "vitir de onunla birlikte vâcip oldu", "ikindi bu döneme sayılır"); toplu onay gerekçeleri ("3 günden kısa kanama âdet sayılmaz", alışkanlık sonrası istihâze açıklaması); 15 günlük temizlik ve ara temizlik uyarıları (yeni); alışkanlık güncelleme bilgisi (yeni); niyet satırı (yeni, P1); Hakkında'daki küçük sözlük (kaza, vitir, kerahat vakti, gusül, istihâze). Metinler ekran-taslaklari-tur3.md, tur4.md ve kabul-senaryolari.md'de. Faz 5'te metinler `strings.xml`'e girmeden önce kapanmalı.

## Kaynaklar

- Proje dosyaları: 5te5-yorum-analizi.md, ozellik-listesi.md, fikhi-gereksinimler.md, gelistirme-yol-haritasi.md, karar-tablosu.md, kabul-senaryolari.md, hocaya-sorular.md, mimari-ve-kodlama-ilkeleri.md, CLAUDE.md, ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md
- Hoca görüşü, 28 Ekim 2026 (hocaya-sorular.md; özet fikhi-gereksinimler.md)
- [Müslüman'dan namaz ne zaman ve hangi hâllerde düşer?](https://kurul.diyanet.gov.tr/tr/fetva/muslumandan-namaz-ibadeti-ne-zaman-ve-hangi-hallerde-duser/0193c42d-4d08-7d76-f3e0-b064efb0196f) — Din İşleri Yüksek Kurulu
- [Namazların Kazası](https://kurul.diyanet.gov.tr/tr/fetvalar/namaz/e881815b-9715-405a-8bdf-aeb000d961ec/namazlarin-kazasi/01933665-b68b-7dfe-d55a-1c5b2416a9ba) — Din İşleri Yüksek Kurulu
- [Vakit girdikten sonra namazı kılmadan âdet görmeye başlayan kadın](https://kurul.diyanet.gov.tr/tr/fetva/namaz-vakti-girdigi-halde-namazi-kilmadan-adet-gormeye/0193c42d-53d9-7bd6-ac1d-b9dfbec2f705) — Din İşleri Yüksek Kurulu
- [Kazâ](https://islamansiklopedisi.org.tr/kaza--ibadet), [Vakit](https://islamansiklopedisi.org.tr/vakit), [Vitir Namazı](https://islamansiklopedisi.org.tr/vitir-namazi), [Hayız](https://islamansiklopedisi.org.tr/hayiz), [Nifas](https://islamansiklopedisi.org.tr/nifas), [Bulûğ](https://islamansiklopedisi.org.tr/bulug) — TDV İslâm Ansiklopedisi
- [Schedule exact alarms are denied by default](https://developer.android.com/about/versions/14/changes/schedule-exact-alarms) — Android Developers
- [Namaz Vakitleri REST Servisi teknik dokümanı](https://awqatsalah.diyanet.gov.tr/files/56d83ac4-f7f5-4f6e-9b9e-b1ffeebf1b6a.pdf) — Diyanet İşleri Başkanlığı
- [Âdet süresi 10 günden fazla sürenler ibadetlerini yerine getirmede nasıl hareket etmeli?](https://www.islamveihsan.com/adet-suresi-10-gunden-fazla-surenler-ibadetlerini-yerine-getirmede-nasil-hareket-etmeli.html) — İslam ve İhsan (Diyanet Fetva Kurulu'na atıfla)
- [Making Up Prayers Missed Due to Being Unconscious](https://seekersguidance.org/answers/hanafi-fiqh/making-up-prayers-missed-due-to-being-unconscious/) — SeekersGuidance (Merâki'l-Felâh ve İmdâdü'l-Fettâh'a atıfla)
- [Vaktinde kılınmayan namazların kazası nasıl yapılır?](https://www.islamveihsan.com/vaktinde-kilinmayan-namazlarin-kazasi-nasil-yapilir.html) — İslam ve İhsan (tertibi düşüren sebepler)

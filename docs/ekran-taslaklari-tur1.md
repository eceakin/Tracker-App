# Ekran Taslakları — Tur 1

27 Ekim 2026 · @ece

## Özet

Tur 1, tasarım promptundaki (ekran-taslagi-promptu.md) ilk turun çıktısıdır: ekran envanteri, gezinme haritası, görsel dil önerileri ve Bugün ekranı için 3 alternatif yön. Görsel taslaklar tasarım panosunda: [Ekran Taslakları — Tur 1](https://claude.ai/artifact/JT9ex8zdaWsNBymxW5UhVq).

- **Envanter:** 31 ekran ve 8 ortak bileşen. Promptun listesine 8 madde eklendi (Bölüm 2).
- **Gezinme önerisi:** 3 sekmeli alt gezinme (Bugün · Takvim · Kaza); Ayarlar üst çubukta, mazeret Bugün'ün menüsünde.
- **Bugün önerisi:** A (liste), C'nin odak vurgusuyla.
- **Görsel dil önerisi:** A "Gün ışığı".
- Tur sonunda 3 karar sorusu var (Bölüm 7). Cevaplar gelmeden Tur 2'ye geçilmez.

Kaynaklar: gereksinim-analizi-srs.md (1.0-taslak.5), kabul-senaryolari.md, karar-tablosu.md v1.2, ozellik-listesi.md, fikhi-gereksinimler.md, 5te5-yorum-analizi.md, gelistirme-yol-haritasi.md. Örnek ilçe ve saatler kabul senaryolarının referans gününden (imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30).

## 1. Ekran envanteri

**Tur** sütunu, ekranın ayrıntılı tasarımının yapılacağı turu gösterir. "Yeni" = belgede yok / yeni öneri; "Açık" = karar bekleyen nokta.

| Ekran | Tek ana iş | Öncelik | Tur | Gereksinimler | Kabul senaryoları | Not |
| --- | --- | --- | --- | --- | --- | --- |
| **1 · Kurulum** | | | | | | |
| O1 · Hoş geldin | Uygulamanın ne vaat ettiğini görüp başlamak | P0 | 2 | C-03, C-04, FR-ONB-05 | KS-ONB-07 | Yeni: ayrı adım yok |
| O2 · Hanefî bilgisi | Sürümün Hanefî'ye göre çalıştığını bir kez öğrenmek | P0 | 2 | FR-ONB-02, C-01 | KS-ONB-02 | |
| O3 · Cinsiyet | Mazeret ve cuma davranışını belirleyen seçimi yapmak | P0 | 2 | FR-ONB-01, FR-TRK-06, FR-MAZ-01 | KS-ONB-01, KS-MAZ-01 | |
| O4 · Konum | Vakitlerin geleceği ilçeyi listeden seçmek | P0 | 2 | FR-ONB-01, FR-VKT-01, 02, NFR-SEC-04 | KS-ONB-01, KS-VKT-01 | Açık: internetsiz ilk açılış |
| O5 · Kaza borcu | Bilinen borcu girmek ya da adımı atlamak | P0 | 2 | FR-ONB-03, FR-KAZ-01 | KS-ONB-03, 04 | "Hesapla" P1 (FR-KAZ-08) |
| O6 · Hatırlatma | Hatırlatmayı açıp bildirim iznini vermek | P0 | 2 | FR-ONB-04, FR-HTR-01 | KS-ONB-05, 06 | |
| O7 · Widget ekle | Widget'ı ana ekrana tek dokunuşla yerleştirmek (atlanabilir) | P0 | 2 | FR-WDG-01, 02 | — | Yeni: ürünün ana farkı |
| **2 · Günlük takip** | | | | | | |
| Bugün | O anki vakti işaretlemek | P0 | 1–2 | FR-TRK-01–07, FR-VKT-03, 04, FR-KAZ-03, NFR-USA-01 | KS-TRK-01–11, KS-VKT-04, 05, KS-NFR-06 | Bu turda 3 yön |
| Takvim | Geçmiş bir günü bulup açmak | P0 | 2 | FR-TRK-04, 05 | KS-TRK-05, 07 | Analiz P1 |
| Gün detayı | Geçmiş bir günün kalemlerini işaretlemek ya da düzeltmek | P0 | 2 | FR-TRK-04, NFR-PRF-03 | KS-TRK-05, 06 | |
| Bekleyen öneriler | Biriken önerileri tek tek ya da topluca yanıtlamak | P0 | 3 | FR-KAZ-03, BR-00, BR-04 | KS-KAZ-04, 05, KS-TRK-04 | Yeni |
| Toplu onay | Mazeret kontrolünden çıkan kalemleri seçip borca eklemek | P0 | 3 | FR-MAZ-05, BR-13, BR-14 | KS-MAZ-09, 10 | Yeni |
| **3 · Kaza** | | | | | | |
| Kaza | Kalan borcu görmek ve kılınan kazayı kaydetmek | P0 | 3 | FR-KAZ-01, 02, 04, FR-TRK-05 | KS-KAZ-01, 02, 07, KS-TRK-07 | |
| Kaza kıldım (alt sayfa) | Vakti seçip kazayı borçtan düşmek | P0 | 3 | FR-KAZ-02, 04; FR-KAZ-06 (P1) | KS-KAZ-02, 03, 07 | Açık: kerahat P1 |
| Borç düzenle | Başlangıç borcunu girmek ya da düzeltmek | P0 | 3 | FR-KAZ-01 | KS-KAZ-01, KS-ONB-04 | |
| Kaza geçmişi | Her ekleme ve düşmeyi görmek, birini geri almak | P0 | 3 | FR-KAZ-05, NFR-REL-02 | KS-KAZ-08 | Yeni |
| **4 · Mazeret (yalnızca kadın kullanıcı)** | | | | | | |
| Mazeret başlat | Türü ve başlangıç saatini kaydetmek | P0 | 3 | FR-MAZ-01, 03, 05 | KS-MAZ-01, 02, 04, 11 | |
| Mazeret bitir | Bitiş saatini kaydedip sonucunu görmek | P0 | 3 | FR-MAZ-01, 04, 05 | KS-MAZ-05–10 | |
| Mazeret geçmişi ve alışkanlık | Geçmiş dönemleri görmek, alışkanlık gününü ayarlamak | P0 | 3 | FR-MAZ-06, NFR-SEC-03 | KS-MAZ-11, 12, KS-NFR-02 | Önizlemede gizli |
| **5 · Ayarlar** | | | | | | |
| Ayarlar | Bir ayarı bulup değiştirmek | P0 | 4 | FR-AYR-01, 02 | KS-AYR-01–04 | |
| Hatırlatmalar | Vakit başına hatırlatmayı ve süreyi ayarlamak | P0 | 4 | FR-HTR-01, FR-ONB-04 | KS-HTR-02, KS-ONB-05 | |
| Kesin alarm izni | Dakikası dakikasına hatırlatma için izin yolunu görmek | P0 | 4 | NFR-TIM-01 | KS-HTR-05 | Yeni (yol haritası Faz 6) |
| Dışa aktar | Verinin kopyasını dosyaya almak | P0 | 4 | FR-YDK-01, 03, NFR-SEC-02 | KS-YDK-01, 03 | Parola akışı |
| İçe aktar | Bir yedek dosyasını yüklemek | P0 | 4 | FR-YDK-02, 03 | KS-YDK-02, 03 | |
| Hakkında | Mezhep, vakit kaynağı ve hesap yöntemini okumak | P0 | 4 | FR-ONB-02, özellik listesi #5 | KS-ONB-02 | |
| Verileri sil | Tüm veriyi iki adımlı onayla silmek | P0 | 4 | DATA-02 | — | Yeni: senaryosu yok |
| **6 · Uygulama dışı yüzeyler** | | | | | | |
| Küçük widget | O anki vakti uygulamayı açmadan işaretlemek | P0 | 4 | FR-WDG-01, 03, 04, NFR-PRF-02 | KS-WDG-01, 04, 05 | |
| Orta widget | Bugünün 6 kalemini uygulamayı açmadan işaretlemek | P0 | 4 | FR-WDG-02, 03, 04 | KS-WDG-02–05 | |
| Hatırlatma bildirimi | Bildirimden "Kıldım" demek | P0 | 4 | FR-HTR-01–05, NFR-SEC-03 | KS-HTR-01, 03, 04, 07, KS-NFR-01 | Kilit ekranı nötr |
| **7 · Hata ve sistem** | | | | | | |
| Kurtarma | Veritabanı açılmazsa önce veriyi kurtarmak | P0 | 4 | FR-YDK-05, NFR-REL-03 | KS-YDK-05 | |
| Pil kısıtı yönlendirme | Üretici pil ayarında uygulamayı serbest bırakmak | P0 | 4 | FR-HTR-03, NFR-TIM-01 | KS-HTR-05 | FR yok; yol haritası Faz 6 |
| **8 · Ortak bileşenler** | | | | | | |
| Öneri kartı | Bir öneriyi onaylamak ya da "vaktinde kıldım" demek | P0 | 3 | FR-KAZ-03, BR-00 | KS-KAZ-04, KS-TRK-04, 09 | |
| Engel mesajı | Neden işaretlenemediğini anlamak (vakti girmedi, önce yatsı, kerahat) | P0 · kerahat P1 | 3 | FR-TRK-02, 07, FR-KAZ-06 | KS-TRK-03, 10, KS-WDG-02 | Açık: kerahat P1 |
| Vaktinde / kaza olarak | Geçmiş bir vakti nasıl kıldığını belirtmek | P0 | 2 | FR-TRK-04 | KS-TRK-05, 06 | |
| Öğle / Cuma seçimi | Cuma günü öğle kalemini cuma olarak işaretlemek | P0 | 2 | FR-TRK-06, BR-08 | KS-TRK-08, 09 | |
| Geri al snackbar'ı | Yanlış dokunuşu hemen geri çevirmek | P0 | 2 | FR-TRK-03 | KS-TRK-04 | Yeni: dokunarak geri almaya ek |
| "Tahmini vakit" etiketi | Vaktin cihazda hesaplandığını fark etmek | P0 | 2 | FR-VKT-03 | KS-VKT-04 | Widget'ta da |
| Tertip notu | Kazanın önce kılınması gerektiğini engellenmeden öğrenmek | P1 | 3 | FR-KAZ-07, BR-17 | — (KT-28–31, 82–84) | Açık: P1 |
| Değişiklik onayı | Konum ya da cinsiyet değişikliğinin etkisini görüp onaylamak | P0 | 4 | FR-AYR-01, BR-21 | KS-AYR-01, 02 | |

## 2. Listeye eklenenler ve nedenleri

Promptun ekran listesinde olmayan ama belgelerin gerektirdiği ya da ürünün farkını taşıyan 8 madde eklendi. Hepsi "belgede yok / yeni öneri" olarak işaretli; reddedilirse envanterden çıkarılır.

1. **O1 · Hoş geldin:** Hedef kitle yeni başlayan. İlk ekranın hesap, form ya da borç istememesi, 1 yıldızlı yorumların en büyük grubuna (zorunlu hesap, ~35) doğrudan yanıt. Tek düğme: "Başla".
2. **O7 · Widget ekle:** Ürünün farkı widget'tan tek dokunuşla işaretleme, ama kullanıcı widget'ı kendisi eklemezse bu fark hiç görülmez. Android 8+ uygulamanın içinden "ana ekrana ekle" isteği gösterebilir. Atlanabilir.
3. **Bekleyen öneriler:** KS-KAZ-05'te bir gün hiç açılmayınca 7 kart birikiyor. Bunları Bugün ekranının üstüne yığmak ekranı boğar; Bugün'de en fazla 1 kart + "n öneri daha" bağlantısı, tamamı ayrı ekranda.
4. **Toplu onay:** KS-MAZ-09 (12 kalem) ve KS-MAZ-10 (24 kalem) "tek onay ekranı" istiyor; envanterde karşılığı yoktu.
5. **Kaza geçmişi:** FR-KAZ-05 her ekleme ve düşmenin görüntülenip tek tek geri alınmasını istiyor (KS-KAZ-08); ayrı bir liste ekranı gerekir.
6. **Kesin alarm izni:** Yol haritası Faz 6, kesin saat isteyen kullanıcıdan iznin ayarlar akışıyla istenmesini söylüyor; açıklayıcı bir ara ekran gerekir.
7. **Verileri sil:** DATA-02 iki adımlı onay ve önce dışa aktarma önerisi istiyor; kabul senaryosu yok. Tur 5'te KS eksikliği olarak raporlanacak.
8. **Geri al snackbar'ı:** FR-TRK-03 geri almayı aynı kaleme tekrar dokunmak olarak tanımlıyor (KS-TRK-04). Ancak yeni başlayan biri yanlış satıra dokunduğunu fark etmeyebilir. Snackbar ("Öğle işaretlendi · Geri al", 4 sn) geri almayı görünür kılar; dokunarak geri alma da korunur.

## 3. Gezinme haritası

**Önerilen yapı:** 3 sekmeli alt gezinme. Bugün başlangıç ekranıdır. Takvim ve Kaza diğer iki sekmedir. Ayarlar her sekmenin üst çubuğundaki dişliden, mazeret yalnızca kadın kullanıcıda Bugün'ün menüsünden açılır.

**Neden alt gezinme?**

- Üç ayrı zihinsel mod var: bugünü işaretlemek, geçmişi düzeltmek, borcu yönetmek. Hepsi tek dokunuş uzakta olmalı; Material 3 gezinme çubuğu 3–5 hedef için tasarlandı.
- Bugün başlangıç ekranı olduğu için uygulamadan işaretleme simge + kalem = 2 dokunuş (NFR-USA-01, KS-NFR-06).
- Kaza ayrı sekme olunca borcu olmayan yeni başlayan onu görmezden gelebilir; borç Bugün ekranına taşınmaz, "büyük rakamla korkutma" riski (fıkhi belge, onboarding) azalır.

**Reddedilen alternatif: tek ekran + sayfalar (kaydırmalı).** Sayfalar arası geçiş görünmez kalır; TalkBack'te konum bildirmek zorlaşır; yeni başlayan Kaza'nın varlığını fark etmeyebilir.

**Ayarlar neden sekme değil?** Seyrek kullanılır; sekme olunca günlük üç işi bir kalabalık sekme çubuğuna sıkıştırır.

**Mazeret neden sekme değil?** (1) Gizlilik: alt çubukta sürekli görünen bir "Mazeret" sekmesi, telefon başkasının elindeyken bilgi verir. (2) Erkek kullanıcıda bulunmaz; sekme sayısı cinsiyete göre değişirdi. KS-MAZ-01 de mazereti Bugün ekranının menüsüne koyuyor.

**Uygulama dışından girişler**

| Giriş | Ne olur | Kaynak |
| --- | --- | --- |
| Uygulama simgesi | Açılış kontrolü: veritabanı açılmazsa Kurtarma, ilk açılışsa Onboarding, değilse Bugün | FR-YDK-05, NFR-REL-03 |
| Widget, işaretlenebilir kalem | Uygulama açılmaz, kalem işaretlenir | FR-WDG-01, 03 |
| Widget, kilitli kalem | Uygulama Bugün'de, açıklamayla açılır | FR-WDG-02, KS-WDG-02 |
| Bildirim "Kıldım" eylemi | Uygulama açılmaz, kalem işaretlenir | FR-HTR-02, KS-HTR-04 |
| Bildirim gövdesi | Bugün açılır | — |

## 4. Görsel dil — 3 yön

Ortak kurallar (üç yönde de geçerli):

- Hilal, cami silueti ya da çini deseni kullanılmaz; ikonlar güneşin ufka göre konumunu çizer (sabah doğan güneş, öğle tepede, akşam batan güneş, yatsı yıldızlar).
- Durum hiçbir zaman yalnızca renkle anlatılmaz: kılındı = dolu daire + tik, şu an = kalın halka, kilitli = kilit + açılış saati. Renk körlüğü ve gri tonlama testinden geçer.
- Kırmızı yalnızca gerçek hata için (kurtarma ekranı). "Kaçırıldı" ve "borç" hiçbir yönde kırmızı ya da uyarı rengi taşımaz (ton kuralı, FR-HTR-05).
- Mazeretli kalem nötr ve dokunulamaz görünür; "kaçırıldı" ile aynı şekli kullanmaz.
- Metin–zemin çiftleri en az 4.5:1; koyu tema her yönde tanımlı.
- Material You dinamik rengi ilk sürümde kapalı. Açılırsa marka paleti kaybolur ve kontrast garanti edilemez.

| | A · Gün ışığı | B · Defter | C · Berrak |
| --- | --- | --- | --- |
| His | Sıcak kâğıt; her vakit günün o saatindeki ışığın çok açık tonunu taşır | Kâğıt ve mürekkep; bir ajanda kadar sakin | Erişilebilirlik önce; yüksek kontrast, serin taş zemin |
| Palet (açık) | Zemin #FBF7F1, metin #2B2621, ikincil #6B6158, vurgu kil #A2541A; 6 vakit tonu | Zemin #F5F3EE, mürekkep #1F2430, ikincil #5B6170, vurgu adaçayı #3D5A45 | Zemin #F4F6F5, metin #111818, ikincil #4A5554, vurgu derin camgöbeği #0F5E63 |
| Yazı | Figtree (yuvarlak, samimi) | Literata başlık + Source Sans 3 gövde | Atkinson Hyperlegible (okunurluk için çizilmiş) |
| İkon | Yuvarlak uçlu çizgi, 1.75 | İnce çizgi, 1.25, köşeli | Dolgulu şekiller, 2 |
| Artı | En sıcak ve kişisel; vakit tonları "günün neresindeyim" bilgisini renksiz ikona ek olarak verir | En sakin ve olgun; uzun süreli kullanımda yormaz | Büyük yazıda ve güneş altında en iyi okunan |
| Eksi | 6 ton + durum renkleri dikkatli yönetilmezse kalabalık görünür | Serif başlıklar küçük ekranda ve %200 yazıda yer kaplar; gençlere ağır gelebilir | Sıcaklığı en az olan; "sağlık uygulaması" gibi durabilir |
| Kime | Yeni başlayan, genç | Düzene oturtmak isteyen yetişkin | Yaşlı kullanıcı, görme güçlüğü |

**Önerim: A · Gün ışığı.** Hedef kitle yeni başlayan ve çoğu genç (SRS 2.1); A korkutmayan, sıcak bir ilk izlenim veriyor ve vakit tonları ürünün özüne (zamana) bağlı, süs değil. Erişilebilirlik riskini C'nin iki kuralını alarak kapatıyoruz: durum her zaman şekil + metinle, ikincil metin en az 4.5:1.

## 5. Bugün ekranı — 3 yön

Ortak sahne (üç taslakta aynı): 27 Ekim 2026 Salı, saat 14:47. Sabah 06:12'de işaretlendi; öğle şu anki vakit ve boş; ikindi, akşam, yatsı ve vitir kilitli. Dünkü yatsı için bir öneri bekliyor.

### A · Liste

Altı kalem dikey listede, eşit büyüklükte. Şu anki vakit satırı hafifçe vurgulu; üstte "Şu an öğle vakti · İkindiye 43 dk" şeridi ve bekleyen öneri kartı.

- **Artı:** Tanıdık. %200 yazıda satırlar uzar ama bozulmaz; TalkBack doğal sırayla "Öğle, şu anki vakit, işaretlenmedi" diye okur (NFR-USA-02, KS-NFR-07). Altı kalem eşit dokunma hedefi (FR-TRK-01). Orta widget aynı düzeni küçültür (FR-WDG-02), yani uygulama ile widget arasında öğrenilecek ikinci bir model yok.
- **Eksi:** Görsel olarak sade; "günün neresindeyim" hissi zayıf.
- **Kime:** Herkese; özellikle yeni başlayana.

### B · Gün halkası

İmsaktan ertesi imsaka 24 saatlik bir halka; vakitler dilim, şu an bir nokta. Ortada şu anki vakit ve "Kıldım" düğmesi; altta 6 kalem için düğmeler.

- **Artı:** Günün akışını tek bakışta gösterir; dolan halka motive eder.
- **Eksi:** Dilimler dokunulamayacak kadar küçük, bu yüzden altta düğmeler de gerekiyor (aynı bilgi iki kez). %200 yazıda ve TalkBack'te zayıf; halka ekran okuyucu için tek bir resimdir. Vitir yatsının içinde ayırt edilemez. Boş dilim "eksik kaldın" gibi okunabilir; bu da ton kuralına ters (FR-HTR-05'in ruhu). Glance widget'ında halka çizmek sınırlı.
- **Kime:** Düzenli kılan, görsel motivasyon arayan.

### C · Odak kartı

Üstte büyük bir "Şu an: Öğle" kartı ve tam genişlikte "Kıldım" düğmesi; altında öneri kartı ve "Günün geri kalanı" sıkışık listesi.

- **Artı:** "Şimdi ne yapayım?" sorusuna en net yanıt. Uygulamadan 2 dokunuş (NFR-USA-01). Küçük widget'la aynı model (FR-WDG-01). "Yanlış dokunursan hemen geri alabilirsin" satırı yeni başlayanın korkusunu azaltır.
- **Eksi:** Geçmiş vakitler ve vitir ikinci planda. Kalemi işaretlemenin iki farklı yolu var (büyük düğme ve satır), tutarsızlık yaratabilir. Kart ekranın üçte birini kaplar.
- **Kime:** Düzene oturtamayan, bildirimden gelen.

### Önerim: A, C'nin odak vurgusuyla

Liste kalır; yalnızca şu anki vaktin satırı biraz büyür ve sağında belirgin bir işaret düğmesi taşır. Gerekçe:

1. Erişilebilirlik ve büyük yazı (NFR-USA-02) en güvenli A'da.
2. Uygulama ile orta widget aynı düzeni paylaşır (FR-WDG-02, FR-WDG-03); kullanıcı tek bir model öğrenir.
3. C'nin "şimdi ne yapayım" netliği, ikinci bir kontrol eklemeden yalnızca vurguyla alınır.
4. Halka (B) atılmıyor: v1.1'deki takvim ve analiz ekranında (P1 #1) günün özeti olarak süs görevi görebilir.

## 6. Varsayımlar ve açık noktalar

**Açıkça yaptığım varsayımlar** (itiraz edersen değişir):

- **V1 — Kerahat ve tertip P1 kalır.** Tasarım promptu kerahatte kazayı sabit kural sayıyor, ancak SRS'te FR-KAZ-06 (kerahat) ve FR-KAZ-07 (tertip notu) P1. SRS Bölüm 1.4 gereği belgeler çeliştiğinde SRS esas; Bölüm 9 gereği P0'a yeni madde ancak başka bir P0 indirilerek girer. Tur 3'te ikisini de "P1" etiketiyle çizeceğim ki kod yapısı şimdiden hazır olsun.
- **V2 — Dokunarak geri alma korunur.** İşaretli kaleme dokunmak geri alır (KS-TRK-04); snackbar ek bir güvenlik.
- **V3 — Takvim'in ay görünümü P0.** FR-TRK-04 geçmiş günü açmayı istiyor; bunun için gün seçimi gerekir. "En çok kaçırılan vakit" gibi istatistikler P1.
- **V4 — Hicrî tarih gösterilmez.** Belgelerde yok; eklemedim.

**Açık nokta (karar sorusu 3):** İlk açılışta internet yoksa ne olacak? İl/ilçe listesi ara sunucudan geliyor (IF-01). İnternet yoksa onboarding konum adımında takılır; KS-ONB-01'e göre konum seçilmeden ilerlenemez. Ayrıca Adhan yedeği (FR-VKT-03) ilçe koordinatı istiyor, yani koordinatların zaten cihazda olması gerekiyor.

## 7. Karar soruları (Tur 1)

1. **Bugün ekranı yönü:** Önerim A (liste) + C'nin odak vurgusu. Onaylıyor musun, yoksa B ya da C'yi mi tercih edersin?
2. **Görsel dil:** Önerim A · Gün ışığı. A, B (Defter) ya da C (Berrak) — hangisi?
3. **İnternetsiz ilk açılış:** Seçenekler: (a) il/ilçe listesi ve koordinatları uygulamaya gömülü gelir, vakitler internet gelince indirilir, o zamana kadar "tahmini" gösterilir *(önerim; yeni öneri, Diyanet kullanım koşullarına uygunluğu kontrol edilmeli)*; (b) onboarding konum adımında "İnternete bağlanınca devam edebilirsin" der ve bekler.

### Kararlar (27 Ekim 2026)

- [x] **1. Bugün yönü:** A (liste) + C'nin odak vurgusu.
- [x] **2. Görsel dil:** A · Gün ışığı.
- [x] **3. İnternetsiz ilk açılış:** (a) İl/ilçe listesi ve koordinatları uygulamaya gömülü gelir; vakitler internet gelince indirilir, o zamana kadar "tahmini" etiketiyle gösterilir. Diyanet kullanım koşullarına uygunluk SRS Bölüm 10'daki Diyanet erişimi maddesiyle birlikte kontrol edilecek; SRS'e değişiklik talebi olarak işlenmeli (FR-VKT, IF-01).

Sıradaki: Tur 2 — onboarding, Bugün ve Geçmiş (Takvim + Gün detayı).

# Ekran Taslakları — Tur 2

27 Ekim 2026 · @ece

## Özet

Tur 2, Tur 1'de verilen üç kararla çizildi: Bugün için A (liste) + C'nin odak vurgusu, görsel dil A · Gün ışığı, il/ilçe listesi ve koordinatları uygulamaya gömülü. Taslaklar tasarım panosunun "Tur 2" sayfasında: [Ekran Taslakları](https://claude.ai/artifact/JT9ex8zdaWsNBymxW5UhVq).

- **Onboarding:** 5 adım (O1–O5) ve 2 alt ekran (O2b, O3b). Tur 1 envanterindeki 7 adım ikişer ikişer birleştirildi (Bölüm 1.0).
- **Bugün:** Tek bir çekirdek taslak ve 8 durum: ilk açılış, işaretlendi + geri al, kaçırıldı + öneri, kilitli kaleme dokunuş, cuma (erkek), mazeret (kadın), tahmini vakit, gece yarısı. Panodaki çekirdek taslağın "durum" ayarından her durum tek tıkla görülebilir.
- **Geçmiş:** Takvim (ay görünümü), Gün detayı ve "Vaktinde / kaza olarak" seçimi.
- **Akış (a):** İlk açılıştan ilk işarete simge dahil 11 dokunuş; sonraki günlerde 2 dokunuş (NFR-USA-01).
- Sonda 3 karar sorusu var (Bölüm 6).

Örnek saatler kabul senaryolarının referans gününden: imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30.

## 0. Ortak etkileşim sözleşmesi

Bugün, Gün detayı ve (Tur 4'te) orta widget aynı kuralları uygular (FR-WDG-03). Kalemin durumu neyse dokunuş ona göre davranır:

| Kalem durumu | Görünüm | Dokunuş | Kaynak |
| --- | --- | --- | --- |
| Şu anki vakit, boş | Satır büyür (76 px), sağda dolu "Kıldım" düğmesi | Kılındı olur; titreşim + "Öğle işaretlendi · Geri al" (4 sn) | FR-TRK-01, NFR-USA-01 |
| Kılındı | Dolu daire + tik, sağda işaretlenme saati | Geri alınır; "Öğle işareti kaldırıldı · Geri al" | FR-TRK-03, KS-TRK-04 |
| Vakti geçmiş, boş | Boş halka, "Vakti geçti · işaretlenmedi" | "Vaktinde kıldım / Sonradan kaza olarak kıldım" seçimi | FR-TRK-04, KS-TRK-05, 06 |
| Kilitli | Kilit, "15:30'da girer"; vitirde "Yatsıdan sonra açılır" | İşaret konmaz; engel notu | FR-TRK-02, 07, KS-TRK-03, 10 |
| Mazeretli | Nötr zemin, tire | Dokunulamaz | FR-MAZ-02, KS-MAZ-03 |
| Kılındı (kaza) | Dolu daire + tik, sağda "kaza" | Geri alınır; kaza kaydı da geri alınır | FR-TRK-04, FR-KAZ-05 |

- **Uzun basma (ve TalkBack özel eylemi):** kalem menüsü. v1.0'da tek içeriği, kadın kullanıcıda cuma günü "Cuma olarak işaretle" (KS-TRK-09). Menü boşsa uzun basma bir şey yapmaz.
- **Renk tek başına anlam taşımaz:** her durumun kendi şekli ve metni var; TalkBack satırı "Öğle, kılındı, 14:48" diye okur (NFR-USA-02, KS-NFR-07).
- **Hiçbir yerde kırmızı yok:** "kaçırıldı" bir uyarı değil, bir durum (FR-HTR-05'in tonu).

## 1. Onboarding

### 1.0 Envanter değişikliği

- **O1 Hoş geldin ile O2 Hanefî bilgisi birleşti.** KS-ONB-02 bilginin "bir kez görünmesini" ve Ayarlar › Hakkında'da bulunmasını istiyor; ayrı ekran şartı yok. Ayrı ekran, yeni başlayana hiçbir şey kazandırmadan bir dokunuş ekliyordu.
- **Cinsiyet ve konum tek ekranda.** KS-ONB-01 ikisini aynı "Devam" düğmesine bağlıyor: yalnızca cinsiyet seçilince düğme pasif kalıyor, ilçe de seçilince etkinleşiyor.
- Yeni numaralar: O1 Hoş geldin + Hanefî, O2 Cinsiyet ve ilçe (O2b İlçe seç), O3 Kaza borcu (O3b Borcu gir), O4 Hatırlatma, O5 Widget ekle.

### O1 · Hoş geldin + Hanefî bilgisi

1. **Amaç:** Uygulamanın ne vaat ettiğini görüp güvenle başlamak.
2. **Hiyerarşi:** Başlık → üç vaat (süre sınırı yok, hesap ve reklam yok, ana ekrandan işaretleme) → Hanefî bilgi kutusu → "Başla". Vaatler 5te5'in en büyük üç şikayetine (40 gün sınırı, zorunlu hesap, widget yokluğu) karşılık geldiği için önce gelir; Hanefî kutusu ayrı bir yüzeyde durur ki gözden kaçmasın.
3. **Taslak:** O1_HosGeldin.
4. **Etkileşimler:** "Başla" → O2 (1 dokunuş). Geri tuşu uygulamadan çıkar.
5. **Durumlar:** İlk açılış dışında görünmez. İnternet yok: etkilemez. Diğer durumlar uygulanmaz.
6. **Mikro metinler:** "Namazlarını kendi hızında takip et" · "Süre sınırı yok. Kayıtların hiçbir zaman sıfırlanmaz." · "Hesap yok, reklam yok. Verilerin yalnızca telefonunda." · "Ana ekrandan tek dokunuşla işaretle." · Kutu: "Hanefî mezhebine göre" / "Bu sürüm Hanefî mezhebine göre çalışır. Diğer mezhepler sonraki güncellemelerle gelecek. Bu bilgiyi Ayarlar › Hakkında'da her zaman bulabilirsin." · "Başla".
7. **İzlenebilirlik:** FR-ONB-02, C-01, C-03, C-04 · KS-ONB-02.
8. **Riskler:** Şafiî bir kullanıcı burada vazgeçebilir; bu bilinçli bir kapsam kararı (C-01). "Verilerin yalnızca telefonunda" cümlesi Android otomatik yedeğiyle çelişiyor gibi okunabilir; FR-YDK-04'teki yedek kullanıcının kendi Google hesabına gider, metin Tur 4'te Hakkında ekranında açıklanacak.

### O2 · Cinsiyet ve ilçe (+ O2b İlçe seç)

1. **Amaç:** Kurallar ve vakitler için gereken iki bilgiyi vermek.
2. **Hiyerarşi:** Neden sorulduğu (tek cümle) → Cinsiyet (iki büyük seçenek) ve neyi değiştirdiği → İlçe alanı ve "konum izni istemeyiz" notu → "Devam". Açıklamalar seçeneklerin hemen altında, çünkü cinsiyet sorusu kendi başına tedirgin edici olabilir.
3. **Taslak:** O2_SeniTaniyalim (kadın seçili, ilçe boş, "Devam" pasif), O2b_IlceSec ("bor" araması).
4. **Etkileşimler:** Cinsiyet seçimi (1) → İlçe alanı (1) → O2b'de yazarak arama; sonuç yazarken süzülür, eşleşen harfler kalın; sonuca dokunmak (1) O2'ye döner → "Devam" (1). Toplam 4 dokunuş + birkaç harf. Arama il, ilçe ve ülke adında eşleşir; Türkçe karakterler duyarsız (ör. "cankiri" → Çankırı).
5. **Durumlar:** İnternet yok: liste gömülü olduğu için arama çalışır (karar 3). Hata: aramada sonuç yoksa "Bu adla bir yer bulamadık. Farklı yazmayı dene." Diğer durumlar uygulanmaz.
6. **Mikro metinler:** "İki şey soracağız" · "Vakitleri ve sana uygun kuralları ayarlamak için. İkisini de sonra Ayarlar'dan değiştirebilirsin." · "Cinsiyet" · "Kadınlarda mazeret günleri, erkeklerde cuma namazı için kullanılır." · "İlçe" / "İlçeni seç" · "Vakitler Diyanet'in bu ilçe için yayımladığı takvimden gelir. Konum izni istemeyiz." · Pasif düğmenin üstünde: "Devam etmek için ilçeni seç." · O2b: "İlçe seç" · "İlçe, il ya da ülke yaz" · "3 sonuç" · "Liste uygulamanın içinde; aramak için internet gerekmez. Vakitler internet gelince Diyanet'ten indirilir."
7. **İzlenebilirlik:** FR-ONB-01, FR-VKT-01, FR-TRK-06, FR-MAZ-01, NFR-SEC-04 · KS-ONB-01, KS-VKT-01, KS-NFR-05.
8. **Riskler:** Pasif düğme nedenini söylemezse kullanıcı takılır; bu yüzden altında tek satır açıklama var. Aynı adlı ilçeler (ör. iki ilde aynı ad) il adıyla ayrışır. GPS ile "bulunduğum yer" seçeneği bilinçli olarak yok (NFR-SEC-04).

### O3 · Kaza borcu (+ O3b Borcu gir)

1. **Amaç:** Bilinen kaza borcunu girmek ya da adımı rahatça atlamak.
2. **Hiyerarşi:** Soru yumuşatılmış hâliyle ("Eski namazların için bir sayaç ister misin?") → ne işe yaradığı → "bilmiyorsan sorun değil" güvencesi → iki seçenek. "Sonra eklerim" dolu düğme: hedef kitlenin çoğu yeni başlayan ve borcunu bilmiyor (fıkhi belge: "büyük bir rakamla korkutmamak").
3. **Taslak:** O3_KazaBorcu, O3b_BorcGir (KS-ONB-04'teki sayılarla).
4. **Etkileşimler:** "Sonra eklerim" → O4 (1). "Şimdi gireyim" → O3b; her vakit için − / sayı / + ya da klavyeden sayı; "Kaydet ve devam et" → O4. Sayılar 0'ın altına inmez, en çok 99.999.
5. **Durumlar:** Boş bırakılan alanlar 0 kaydedilir. Hata: sayı olmayan giriş kabul edilmez. Diğer durumlar uygulanmaz.
6. **Mikro metinler:** "Eski namazların için bir sayaç ister misin?" · "Geçmişte kılamadığın namazları vakit vakit tutabilir, kıldıkça azalmasını görebilirsin." · "Bilmiyorsan ya da şimdi istemiyorsan sorun değil. İstediğin zaman Kaza sekmesinden ekleyebilirsin." · "Sonra eklerim" · "Şimdi gireyim" · O3b: "Kaza borcu" · "Her vakit için yaklaşık sayıyı yaz. Sonradan her zaman düzeltebilirsin." · Vitir altında "Hanefî'de vâcip" · "Boş bıraktığın vakitler 0 olarak kaydedilir." · "Kaydet ve devam et".
7. **İzlenebilirlik:** FR-ONB-03, FR-KAZ-01, BR-01 · KS-ONB-03, 04.
8. **Riskler:** O3b'de toplam gösterilmez; altı sayının toplamı (örnekte 49) yeni başlayanı korkutabilir. "Kaza" kelimesini bilmeyen kullanıcı için başlık kelimeyi kullanmıyor; Kaza sekmesi adı ilk kez burada görülür. "Hesapla" seçeneği P1 (FR-KAZ-08) olduğu için v1.0'da hiç gösterilmez.

### O4 · Hatırlatma

1. **Amaç:** Hatırlatmayı açmak ve bildirim iznini vermek.
2. **Hiyerarşi:** Örnek bildirim (ne alacağını görsün) → soru → kural ("yalnızca işaretlemediğin vakitler için") → ayarlardan değiştirilebileceği → iki seçenek.
3. **Taslak:** O4_Hatirlatma.
4. **Etkileşimler:** "Hatırlatmaları aç" → Android 13+ sistem izin penceresi → "İzin ver" (2 dokunuş). Android 12 ve altında pencere çıkmaz (1 dokunuş, KS-ONB-06). "Şimdilik kapalı kalsın" → O5.
5. **Durumlar:** İzin reddedilirse onboarding sürer, hatırlatmalar kapalı başlar ve Ayarlar › Hatırlatmalar'da "Bildirimlere izin ver" düğmesi görünür (KS-ONB-05). Diğer durumlar uygulanmaz.
6. **Mikro metinler:** Örnek: "[Uygulama adı] · şimdi" / "Öğle vaktinin çıkmasına 30 dk" / "Kıldım" · "Vakit çıkmadan hatırlatayım mı?" · "Yalnızca işaretlemediğin vakitler için, vakit çıkmadan 30 dakika önce. Kıldıysan bildirim gelmez." · "Süreyi ve hangi vakitlerin hatırlatılacağını Ayarlar'dan değiştirebilirsin." · "Hatırlatmaları aç" · "Şimdilik kapalı kalsın".
7. **İzlenebilirlik:** FR-ONB-04, FR-HTR-01, 02, 05, BR-05 · KS-ONB-05, 06, KS-HTR-01.
8. **Riskler:** Sistem penceresi kullanıcıyı ürkütebilir; örnek bildirim önceden ne olacağını gösterdiği için reddetme oranını düşürmesi beklenir. Kesin alarm izni burada istenmez (pencereli alarm yeterli; kesin saat isteyen Ayarlar'dan açar, Tur 4).

### O5 · Widget ekle

1. **Amaç:** Widget'ı ana ekrana tek dokunuşla yerleştirmek ya da bunu ertelemek.
2. **Hiyerarşi:** Ana ekranda widget'ın nasıl duracağını gösteren çizim → fayda cümlesi → iki seçenek → sonra nasıl ekleneceği.
3. **Taslak:** O5_Widget.
4. **Etkileşimler:** "Ana ekrana ekle" → sistemin "ana ekrana ekle" penceresi → "Ekle" (2). Başlatıcı bunu desteklemiyorsa düğme yerine yalnızca elle ekleme talimatı görünür. "Şimdi değil" → Bugün (1).
5. **Durumlar:** Diğer durumlar uygulanmaz.
6. **Mikro metinler:** "Uygulamayı açmadan işaretle" · "Ana ekrana küçük bir kutu ekle. O anki vakti tek dokunuşla işaretlersin; sıradaki vakte kalan süreyi de görürsün." · "Ana ekrana ekle" · "Şimdi değil" · "Sonra eklemek istersen: ana ekranda boş bir yere basılı tut › Widget'lar › [Uygulama adı]."
7. **İzlenebilirlik:** FR-WDG-01, 02 · KS yok (Tur 5'te listelenecek).
8. **Riskler:** "Widget" kelimesi herkesçe bilinmez; metin "küçük kutu" diyor, başlık faydayı söylüyor. Xiaomi gibi başlatıcılarda sabitleme penceresi çıkmayabilir (Faz 1 prototipinde denenecek).

## 2. Bugün

1. **Amaç:** O anki vakti işaretlemek.
2. **Hiyerarşi:** Tarih ve ilçe (FR-TRK-05) → sıradaki vakte kalan süre → varsa tek bir bilgi kartı (ilk açılış, mazeret, tahmini vakit, gece yarısı) → varsa bekleyen öneri → altı kalemli liste, şu anki vakit büyütülmüş. Liste her zaman aynı yerde ve aynı sırada; vurgulanan satır gün içinde aşağı doğru kayar. Kartlar listenin üstünde, çünkü onlar istisnadır; ama en fazla bir bilgi kartı ve bir öneri kartı gösterilir ki liste ekrandan taşmasın.
3. **Taslak:** Bugun (çekirdek, 14:47) ve 8 durum taslağı.
4. **Etkileşimler:** Bölüm 0'daki sözleşme. Ek olarak:
   - Şu anki vakti işaretlemek: simge + satır = **2 dokunuş** (NFR-USA-01, KS-NFR-06).
   - Öneri kartı: "Borca ekle" (1) ya da "Vaktinde kıldım" (1); "+1 öneri" Bekleyen öneriler ekranını açar (Tur 3).
   - ⋮ menüsü: "Geçmiş bir günü aç", kadın kullanıcıda "Mazeret başlat / bitir". Dişli: Ayarlar.
   - Kaydırma: liste ekrana sığar; %200 yazıda sayfa dikey kayar, alt gezinme sabit kalır.
5. **Durumlar:**

| Durum | Görünüm | Kaynak |
| --- | --- | --- |
| İlk açılış (16:00 kurulum) | "Hazırsın" kartı; sabah ve öğle "Vakti geçti · işaretlenmedi" ama öneri yok; ikindi şu anki vakit | FR-ONB-05, KS-ONB-07 |
| Normal (14:47) | Sabah kılındı 06:12, öğle şu an, diğerleri kilitli; "İkindiye 43 dk" | KS-TRK-01, KS-VKT-05 |
| İşaretlendi | Satır kılındı olur, alt kısımda "Öğle işaretlendi · Geri al" | FR-TRK-03, KS-TRK-04 |
| Kilitli kaleme dokunuş | İşaret konmaz; satır vurgulanır; "İkindi vakti henüz girmedi. 15:30'da açılır." Vitirde "Önce yatsıyı işaretle." | FR-TRK-02, 07, KS-TRK-03, 10 |
| Kaçırıldı + öneri (15:40) | Öğle "Vakti çıktı · işaretlenmedi"; üstte "Öğle işaretlenmeden vakti çıktı. Kaza borcuna eklensin mi?" Otomatik ekleme açıksa kart çıkmaz, satır "Kılınmadı · borca eklendi" der | FR-KAZ-03, KS-KAZ-04, 06 |
| Kerahat | Bugün'de engel yaratmaz: vaktin kendi namazı (eda) her zaman işaretlenir, akşam kerahatinde o günün ikindisi dahil (BR-07 istisnası). Kerahat yalnızca kaza eylemini etkiler (FR-KAZ-06, P1, Tur 3) | BR-07, KT-25 |
| Mazeret aktif (kadın) | "Mazeret dönemi · 25 Ekim 16:00'dan beri" kartı ve "Bitir" düğmesi; satırlar nötr ve dokunulamaz; geri sayım ve öneri yok; son uygulamalar önizlemesinde ekran gizli | FR-MAZ-02, FR-HTR-04, KS-MAZ-03, KS-NFR-02 |
| Cuma, erkek | Öğle satırı "Öğle / Cuma · Hangisini kıldın?" ve iki düğme; vakit çıkarsa öneri "öğle" der | FR-TRK-06, BR-08, KS-TRK-08, 09 |
| Cuma, kadın | Satırın adı "Öğle"; "Cuma olarak işaretle" yalnızca uzun basma menüsünde | KS-TRK-09 |
| İnternet yok, tahmini | "Tahmini vakit" etiketi ve "Vakitler tahmini" kartı; işaretleme normal çalışır | FR-VKT-03, KS-VKT-04, NFR-OFF-01 |
| Gece yarısı–imsak (00:40) | Başlıkta önceki gün; "Saat 00:40 · dünün vakitleri" kartı; vitir şu anki vakit; "İmsaka 4 sa 50 dk" | KS-TRK-11, KT-06 |
| Hata (yazma başarısız) | İşaret geri döner; "Kaydedilemedi. Tekrar dene." ve "Tekrar dene" düğmesi. Veritabanı hiç açılmazsa Bugün yerine Kurtarma (Tur 4) | NFR-REL-02, 03 |

6. **Mikro metinler:** "İkindiye 43 dk" · "12:30 – 15:30 · şu an" · "Kıldım" · "Vakti geçti · işaretlenmedi" · "Vakti çıktı · işaretlenmedi" · "15:30'da girer" · "Yatsıdan sonra açılır" · "Öğle işaretlendi" / "Geri al" · "Öğle işareti kaldırıldı" · "İkindi vakti henüz girmedi. 15:30'da açılır." · "Önce yatsıyı işaretle." · "Bekleyen öneri · bugün" · "Öğle işaretlenmeden vakti çıktı. Kaza borcuna eklensin mi?" · "Vaktinde kıldım" / "Borca ekle" · "Hazırsın" / "Bir vakti kıldığında satırına dokunman yeterli. Yanlış dokunursan bir daha dokunup geri alabilirsin." · "Mazeret dönemi · 25 Ekim 16:00'dan beri" / "Bu günlerde vakitler borca eklenmez ve hatırlatma gelmez." / "Bitir" · "Tahmini vakit" / "Vakitler tahmini" / "İnternet gelince Diyanet vakitleri indirilecek. O zamana kadar vakitler telefonunda hesaplanıyor; birkaç dakika fark olabilir." · "Saat 00:40 · dünün vakitleri" / "Namaz günü imsakta değişir. 05:30'a kadar 26 Ekim'in vakitlerini görüyorsun." · "Öğle / Cuma" / "Hangisini kıldın?" · "Kaydedilemedi. Tekrar dene."
7. **İzlenebilirlik:** FR-TRK-01–07, FR-KAZ-03, FR-MAZ-02, FR-VKT-03, 04, FR-HTR-04, NFR-USA-01, 02, NFR-SEC-03, NFR-REL-02 · KS-TRK-01–11, KS-KAZ-04–06, KS-MAZ-03, KS-VKT-04, 05, KS-ONB-07, KS-NFR-02, 06, 07.
8. **Riskler:**
   - **Kazara geri alma:** İşaretli satıra tek dokunuş işareti kaldırır (FR-TRK-03). Cebinden çıkarırken ya da kaydırırken dokunan biri fark etmeden işaretini silebilir; snackbar ve titreşim bunu görünür kılar ama tamamen önlemez (karar sorusu 3).
   - **"Vakti geçti" bir suçlama gibi okunabilir.** Metin yalnızca durumu söylüyor; renk nötr; öneri kartı "Vaktinde kıldım" seçeneğini ilk sırada sunuyor.
   - **Gece yarısı:** Kullanıcı 00:40'ta yeni günü bekleyebilir; kart neden dünün göründüğünü açıklıyor.
   - **Mazeret kartı ekranda "mazeret" kelimesini taşır.** Bu uygulama içi ve izinli (NFR-SEC-03); ama ekranı başkasına gösteren kullanıcı için risk. Ekran son uygulamalar önizlemesinde gizlenir.

## 3. Geçmiş

### Takvim

1. **Amaç:** Geçmiş bir günü bulup açmak.
2. **Hiyerarşi:** Ay adı ve geçiş oklarının altında 7 sütunlu ay ızgarası. Her günde gün numarası ve 6 küçük çizgilik bir özet: dolu = kılındı, açık = işaretlenmedi, gri = mazeretli. Öneri bekleyen günlerde sağ üstte nokta. Altta açıklama ve "3 bekleyen öneri" kartı.
3. **Taslak:** Takvim (Ekim 2026, kadın kullanıcı örneği; 14–19 Ekim mazeretli, 20 Ekim seçili, bugün çerçeveli).
4. **Etkileşimler:** Güne dokunmak Gün detayını açar; uygulamadan geçmiş bir kalemi işaretlemek Takvim sekmesi (1) + gün (1) + kalem (1) + seçim (1) = 4 dokunuş. "Bugün" düğmesi bu aya döner. Gelecek günler pasif; gelecek aya gidilemez. Önceki aylara sınırsız gidilir (FR-TRK-01).
5. **Durumlar:** İlk açılış: yalnızca bugün dolu, kurulum öncesi günler özetsiz ama açılabilir (öneri üretilmez, FR-ONB-05). Mazeret: gün nötr zeminli. Cuma: cuma işaretli öğle "kılındı" sayılır. Tahmini vakit: takvimi etkilemez. Kerahat: uygulanmaz. Hata: veri yüklenemezse "Bu ay yüklenemedi. Tekrar dene."
6. **Mikro metinler:** "Takvim" · "Bugün" · "Ekim 2026" · "Kılındı" · "İşaretlenmedi" · "Mazeretli" · "Öneri bekliyor" · "3 bekleyen öneri" / "20, 23 ve 24 Ekim'de işaretlenmemiş vakitler" · TalkBack: "20 Ekim, 6 vakitten 4'ü kılındı, öneri bekliyor".
7. **İzlenebilirlik:** FR-TRK-01, 04, 05, FR-MAZ-02, NFR-PRF-03 · KS-TRK-02, 05, 07, KS-AYR-01.
8. **Riskler:** 6 çizgilik özet küçük; TalkBack ve %200 yazıda gün hücreleri bilgi kaybeder, ama dokunulduğunda gün detayı tam bilgiyi verir. "En çok kaçırılan vakit" gibi analizler P1 (P1 #1); ilk sürümde takvim yalnızca gezinme aracıdır.

### Gün detayı (+ Vaktinde / kaza seçimi)

1. **Amaç:** Geçmiş bir günün kalemlerini işaretlemek ya da düzeltmek.
2. **Hiyerarşi:** Bugün ekranıyla aynı liste (öğrenilecek ikinci bir düzen yok); başlıkta tarih, geri oku ve "Bugüne dön" (KS-TRK-07). Kilitli kalem yok, çünkü günün tüm vakitleri geçmiş.
3. **Taslak:** G_GunDetayi (20 Ekim), G_Secim (alt sayfa açık).
4. **Etkileşimler:** Boş kaleme dokunuş → alt sayfa: "Vaktinde kıldım" (öneri kapanır, borç değişmez; KT-13) ya da "Sonradan kaza olarak kıldım" (KT-12) ya da "Vazgeç". Animasyonsuz, 300 ms altında tepki (NFR-PRF-03). Kılınmış kaleme dokunuş geri alır.
5. **Durumlar:** Öneri bekleyen kalem: "Borç önerisi bekliyor". Borca eklenmiş kalem: "Kılınmadı · borca eklendi"; dokunulunca seçim yine açılır, "kaza olarak kıldım" borcu 1 düşer (KS-TRK-06). Mazeretli gün: tüm kalemler nötr, dokunulamaz. Cuma (erkek): seçim alt sayfası üç seçenekli olur (Vaktinde öğle / Vaktinde cuma / Kaza olarak öğle). Kurulum öncesi gün: normal çalışır, öneri yok. Hata: Bugün'deki gibi.
6. **Mikro metinler:** "20 Ekim 2026 Salı" · "Geçmiş gün · Test İlçesi" · "Bugüne dön" · "Borç önerisi bekliyor" · "Kılınmadı · borca eklendi" · Alt sayfa: "20 Ekim 2026 · Öğle" / "Bu namazı nasıl kıldın?" / "Vaktinde kıldım" — "Sadece işaretlemeyi unutmuşsun. Kaza borcun değişmez." / "Sonradan kaza olarak kıldım" — "'Kılındı (kaza)' olarak görünür ve kaza geçmişine yazılır." / "Vazgeç".
7. **İzlenebilirlik:** FR-TRK-04, 05, FR-KAZ-02, 05, NFR-PRF-03 · KS-TRK-05, 06, 07, KS-KAZ-08 · KT-12, 13.
8. **Riskler:** "Vaktinde mi, kaza olarak mı?" sorusu yeni başlayan için fıkhi bir terim gerektirir; seçenek altlarındaki açıklamalar bu yüzden sonucu sade dille söylüyor. Öneri bekleyen kalemde "kaza olarak kıldım" seçilirse ne olacağı belgelerde tanımlı değil (karar sorusu 1).

## 4. Akış (a): ilk açılıştan ilk işarete

| Adım | Ekran | Eylem | Dokunuş |
| --- | --- | --- | --- |
| 1 | Ana ekran | Uygulama simgesi | 1 |
| 2 | O1 | "Başla" | 1 |
| 3 | O2 + O2b | Cinsiyet · İlçe alanı · birkaç harf + sonuç · "Devam" | 4 + yazma |
| 4 | O3 | "Sonra eklerim" | 1 |
| 5 | O4 | "Hatırlatmaları aç" + sistem "İzin ver" | 2 |
| 6 | O5 | "Şimdi değil" | 1 |
| 7 | Bugün | Şu anki vaktin "Kıldım" düğmesi | 1 |
| | | **Toplam** | **11** |

Sapmalar: borcu şimdi girmek +1 dokunuş ve 6 sayı; widget eklemek +1 (sonraki her işaret widget'tan 1 dokunuş); Android 12 ve altında izin penceresi yok (−1). Sonraki günlerde uygulamadan işaretleme 2 dokunuş.

## 5. Varsayımlar, değişiklik talepleri ve Tur 5'e notlar

**Açıkça yaptığım varsayımlar**

- **V5 — Onboarding birleştirmeleri** (Bölüm 1.0). Envanter 31 ekrandan 29'a iner; O2b ve O3b ayrı ekran değil, alt sayfa sayılır.
- **V6 — Kurulum öncesi günler işaretlenebilir.** FR-TRK-04 geçmiş gün için sınır koymuyor; FR-ONB-05 yalnızca öneri üretilmemesini istiyor.
- **V7 — Bugün'de en fazla bir bilgi kartı ve bir öneri kartı.** Öncelik: mazeret > tahmini > gece yarısı > ilk açılış; fazla öneriler "+n öneri" bağlantısıyla Bekleyen öneriler ekranına gider.
- **V8 — Geri al snackbar'ı 4 saniye kalır,** Material 3'ün kısa süresine yakın; TalkBack açıkken süre sistemin erişilebilirlik ayarına uyar.
- **V9 — Otomatik ekleme açıkken** kaçırılan kalem "Kılınmadı · borca eklendi" der ve snackbar'la "Öğle borca eklendi · Geri al" gösterilir (KS-KAZ-06 kartsız ekleme istiyor, ama sessiz kalmak BR-00'ın ruhuna aykırı).

**SRS'e işlenmesi gereken değişiklik talebi (Bölüm 9)**

- **DT-1 · Gömülü il/ilçe listesi (karar 3, 27 Ekim):** A-02 ("listeden seçer, GPS gerekmez") korunur; IF-01'deki `GET /locations` artık ilk kurulumda gerekli değil, yalnızca liste güncellemesi için. FR-VKT-03'teki Adhan hesabı gömülü koordinatlarla ilk açılıştan itibaren çalışır. Etki: FR-ONB-01, FR-VKT-03, IF-01, A-02; yeni test: uçak modunda temiz kurulum ve onboarding. Ön koşul: Diyanet'in yer listesini uygulama paketine koymaya izin vermesi (SRS Bölüm 10, Diyanet erişimi).

**Tur 5'te raporlanacak kabul senaryosu boşlukları**

- O5 Widget ekle adımı için senaryo yok.
- Geri al snackbar'ı ve yazma hatası mesajı için senaryo yok.
- Kurulum öncesi bir günü işaretleme (V6) için senaryo yok.

## 6. Karar soruları (Tur 2)

1. **Öneri bekleyen geçmiş bir vakit için "Sonradan kaza olarak kıldım":** (a) Önerim: borç kaydı ve kılınan kaza kaydı aynı anda yazılır; borç net değişmez, kaza geçmişinde iki kayıt görünür, kullanıcı tek dokunuşla biter. (b) Önce "Borca ekle" onayı istenir, sonra kaza düşülür (iki adım, ama her kayıt ayrı onaylı). Hangisi?
2. **Uygulama adı:** Hatırlatma örneği, widget talimatı ve ileride bildirimler bir ad gerektiriyor; şu an "[Uygulama adı]" yer tutucusu var. Kafanda bir ad var mı, yoksa Tur 4'e kadar yer tutucuyla mı devam edelim?
3. **İşaretli kaleme tek dokunuşla geri alma:** SRS (FR-TRK-03, KS-TRK-04) tek dokunuş istiyor; snackbar ve titreşim ekledim. Bu yeterli mi, yoksa geri almayı uzun basmaya ya da onaylı hâle mi taşıyalım? Taşırsak FR-TRK-03 ve KS-TRK-04 değişiklik talebiyle güncellenir.

### Kararlar (27 Ekim 2026)

- [x] **1. Öneri bekleyen geçmiş vakitte "Sonradan kaza olarak kıldım":** (a) Borç kaydı ve kılınan kaza kaydı aynı anda, tek işlemde (transaction) yazılır; borç net değişmez, kaza geçmişinde iki kayıt görünür. FR-TRK-04 ve FR-KAZ-05'e açıklama olarak eklenmeli; yeni kabul senaryosu Tur 5'te önerilecek.
- [x] **2. Uygulama adı:** Şimdilik "Namaz Takibi". O4 ve O5 taslaklarında yer tutucunun yerine yazıldı; ad değişirse `strings.xml`'de tek yerden değişecek.
- [x] **3. Tek dokunuşla geri alma:** FR-TRK-03 olduğu gibi kalır; titreşim ve 4 saniyelik "Geri al" çubuğu yeterli kabul edildi. SRS değişikliği gerekmiyor.

Sıradaki: Tur 3 — Kaza, Mazeret, öneri kartı ve engel mesajları.

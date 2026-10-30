# Ekran Taslakları — Tur 3

27 Ekim 2026 · @ece

## Özet

Tur 3 kaza, mazeret, öneri kartı ve engel mesajlarını kapsıyor. Taslaklar tasarım panosunun "Tur 3" sayfasında: [Ekran Taslakları](https://claude.ai/artifact/JT9ex8zdaWsNBymxW5UhVq).

- **Kaza:** Kaza ekranı ve 4 durumu (borç girilmemiş, kaydedildi, borcu 0 olan vakit, kerahat P1). Ayrıca Borcu düzenle, Kaza geçmişi ve Bekleyen öneriler ekranları.
- **Mazeret:** Başlat, 15 gün uyarısı, devam eden dönem, bitişi kaydet, iki sonuç ekranı, toplu onay (72 saatten kısa / 240. saat), geçmiş ve alışkanlık.
- **Ortak bileşenler:** 6 öneri kartı varyantı, 6 engel mesajı, 2 diyalog, tertip bilgi notu (P1).
- **Akış (c):** Kaçırılan öğlenin borca eklenip ertesi gün kaza olarak kılınması 2 + 3 = 5 dokunuş.
- **Bulunan tutarsızlık:** KT-52 ve KS-MAZ-09'daki süre ile kalem sayısı birbirini tutmuyor (Bölüm 5).
- Sonda 3 karar sorusu var (Bölüm 7).

Görsel dil A · Gün ışığı. Bugün ekranındaki etkileşim sözleşmesi (Tur 2, Bölüm 0) burada da geçerli. Örnek saatler kabul senaryolarının referans gününden alındı: imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30.

## 0. Tur 2 kararlarının uygulanması

- Uygulama adı "Namaz Takibi" olarak O4 (örnek bildirim) ve O5'e (widget talimatı) yazıldı.
- Karar 1(a): Gün detayında öneri bekleyen bir vakit için "Sonradan kaza olarak kıldım" seçilirse borç ekleme ve kılınan kaza kaydı aynı anda yazılır. Bu çift, Kaza geçmişinde tek satır ("kaza olarak kılındı · 2 kayıt birlikte") olarak görünür ve tek "Geri al" ile birlikte geri alınır.
- Tek dokunuşla geri alma (FR-TRK-03) olduğu gibi kaldı. Titreşim ve 4 saniyelik "Geri al" çubuğu bütün işaretleme eylemlerinde kullanılıyor; kaza kaydı da buna dahil.

## 1. Kaza

### Kaza (sekme)

1. **Amaç:** Kalan borcu görmek ve kılınan bir kazayı kaydetmek.
2. **Hiyerarşi:** Başlık ve tarih (KS-TRK-07) → toplam kalan borç → varsa "n öneri bekliyor" → tek cümlelik kullanım ipucu → 6 vakit satırı (ad, kalan sayı, "Kıldım") → "Kaza geçmişi" ve "Borcu düzenle". Toplam en üstte, çünkü özellik listesinin kabul kriteri hem toplamı hem vakit bazında borcu istiyor. Satırlar Bugün ile aynı sırada ve aynı ikonlarla dizildi; kullanıcının yeni bir düzen öğrenmesi gerekmiyor.
3. **Taslak:** Kaza (çekirdek), K_Bos, K_Kaydedildi, K_BorcYok, K_Kerahat.
4. **Etkileşimler:**
   - Bir satırdaki "Kıldım" o vaktin borcunu 1 azaltır. Titreşim ve "İkindi kazası kaydedildi · kalan 7 · Geri al" çubuğu çıkar. Uygulama açıldıktan sonra 2 dokunuş: Kaza sekmesi ve satır.
   - Vakit seçimi satırın kendisidir. Bu yüzden vakitsiz bir kaza kaydı yapmak mümkün değil (FR-KAZ-02). Alternatifi karar sorusu 2'de.
   - Borcu 0 olan satırda "Kıldım": "Bu vakitte borcun yok. Önce ekleyip sonra düşeyim mi?" diyaloğu açılır. "Ekle ve düş" iki kayıt yazar; "Vazgeç" hiçbir şey yazmaz (FR-KAZ-04, KS-KAZ-07).
   - Art arda dokunuşlar ayrı ayrı kaydedilir. Aynı vakitten 3 kaza kılan kullanıcı 3 kez dokunur.
   - "n öneri bekliyor" Bekleyen öneriler ekranını açar.
5. **Durumlar:**

| Durum | Görünüm | Kaynak |
| --- | --- | --- |
| Borç girilmemiş | "Henüz kaza borcu girmedin" kartı ve "Borç ekle" düğmesi. Satırlar 0 gösterir ve yine dokunulabilir. Hatırlatma ya da ısrar yok | FR-ONB-03, KS-ONB-03 |
| Normal | Toplam 48, satırlar ve öneri bağlantısı | FR-KAZ-01, 02 |
| Kaydedildi | İkindi satırı kısa süre vurgulanır, sayı 8 → 7, "Geri al" çubuğu çıkar | FR-KAZ-02, 05 |
| Borcu 0 olan vakit | Diyalog açılır, borç asla negatife düşmez | FR-KAZ-04, KS-KAZ-07, KT-35 |
| Kerahat (P1) | Üstte "Kerahat vakti · 07:00 – 07:50" bandı. "Kıldım" düğmeleri pasif ve açıklamalı. v1.0'da bu durum yok, kod yapısı hazırlanır | FR-KAZ-06, BR-07, KT-19 – 27 |
| Vakti girmemiş, cuma | Kaza ekranını etkilemez | — |
| Mazeret aktif | Kaza kaydı açık kalır. Hayız sırasında namaz kılınmaz, ama ekranı kilitlemek mazereti açığa vurmak olurdu; bu yüzden ekran değişmez | Gizlilik ilkesi |
| İnternet yok | Etkilemez (NFR-OFF-01) | KS-NFR-03 |
| Hata | "Kaydedilemedi. Verin değişmedi. · Tekrar dene"; sayı eski değerinde kalır | NFR-REL-02, KS-NFR-04 |

6. **Mikro metinler:** "Kaza" · "Kalan kaza borcu" / "6 vakit toplamı" · "3 öneri bekliyor" / "Gör" · "Bir kaza kıldığında o vaktin satırındaki 'Kıldım'a dokun; borç 1 azalır." · "Kıldım" · "İkindi kazası kaydedildi · kalan 7" / "Geri al" · "Henüz kaza borcu girmedin" / "Geçmişte kılamadığın namazlar varsa buraya ekleyebilirsin; kıldıkça azalır. Kaçırdığın vakitler için sana ayrıca soracağız." / "Borç ekle" · "Akşam borcun yok" / "Bu vakitte borcun yok. Önce ekleyip sonra düşeyim mi? Akşam borcuna 1 eklenir ve hemen düşülür; kaza geçmişine iki kayıt yazılır." / "Vazgeç" / "Ekle ve düş" · Kerahat: "Kerahat vakti · 07:00 – 07:50" / "Güneş doğarken kaza kılınmaz. 07:50'de yeniden açılır." · "Kaza geçmişi" · "Borcu düzenle" · TalkBack: "Öğle kazası kıldım. Kalan 7."
7. **İzlenebilirlik:** FR-KAZ-01, 02, 04, 05, 06 (P1), FR-TRK-05, FR-ONB-03, NFR-REL-02 · KS-KAZ-01, 02, 03, 07, KS-TRK-07, KS-ONB-03, 04 · KT-19 – 27, 35, 36.
8. **Riskler:**
   - **Bugün'deki "Kıldım" ile karışabilir.** Kullanıcı Kaza ekranındaki "Kıldım"ın bugünün namazını işaretlediğini sanabilir. Ekran başlığı, satırda büyük kalan sayısı ve "kazası kaydedildi" çubuğu bu yanılgıyı ilk dokunuşta düzeltir.
   - **Kazara dokunuş borcu düşürür.** "Geri al" çubuğu ve kaza geçmişindeki "Geri al" bunu telafi eder.
   - **Büyük toplam korkutabilir.** Hesaplayıcıyla girilmiş binlerce vakitlik borç ekranda tek bir büyük sayı olur. Kaza planı ("günde X kaza → tahmini bitiş") bu korkuyu yumuşatabilir, ama P2'de.

### Borcu düzenle

1. **Amaç:** Başlangıç borcunu girmek ya da sayılardan birini düzeltmek.
2. **Hiyerarşi:** Ne yapıldığı ve kayıt edildiği → 6 sayı alanı → değişiklik özeti → "Kaydet". Değişen satır vurgulanır ve "7 → 9 · +2 düzeltme" yazar; kullanıcı neyi değiştirdiğini kaydetmeden önce görür.
3. **Taslak:** BorcDuzenle.
4. **Etkileşimler:** −/+ ya da klavyeyle sayı girişi. "Kaydet" her değişen vakit için bir "düzeltme" kaydı yazar (KS-KAZ-01). Değişiklik varken geri tuşuna basılırsa "Değişiklikler kaydedilmedi. Çıkılsın mı?" sorulur.
5. **Durumlar:** Hiç değişiklik yoksa "Kaydet" pasiftir. Sayılar 0 ile 99.999 arasında kalır. Hata durumunda Kaza ekranındaki gibi davranır.
6. **Mikro metinler:** "Borcu düzenle" · "Saydığın sayı farklıysa burada düzelt. Her değişiklik kaza geçmişine 'düzeltme' olarak kaydedilir; eski kayıtlar silinmez." · "7 → 9 · +2 düzeltme" · "1 değişiklik: Öğle +2" · "Kaydet" · "Vazgeç".
7. **İzlenebilirlik:** FR-KAZ-01, 05, NFR-REL-02 · KS-KAZ-01.
8. **Riskler:** Kullanıcı düzenleme ekranını "kaza kıldım" yerine kullanabilir; sonuç sayısal olarak aynıdır ama kayıt türü yanlış olur. Bu yalnızca istatistikleri (P1) etkiler.

### Kaza geçmişi

1. **Amaç:** Borcu değiştiren her kaydı görmek ve gerekirse birini geri almak.
2. **Hiyerarşi:** "Kalan borç bu kayıtlardan hesaplanır" açıklaması → vakit süzgeci → günlere göre gruplanmış kayıtlar (saat, vakit + tür, ayrıntı, miktar, "Geri al"). Kullanıcının çoğu zaman aradığı şey yakın tarihli bir hata olduğu için en yeni kayıt en üstte.
3. **Taslak:** KazaGecmisi.
4. **Etkileşimler:** "Geri al" kaydı silmez; karşılığında bir düzeltme kaydı ekler ve asıl kayda "Geri alındı" etiketi koyar (KS-KAZ-08, KT-81). Bir geri alma kaydının kendisi geri alınamaz; yanlışlıkla geri alındıysa işlem yeniden yapılır. Birlikte yazılan çift (karar 1a) tek "Geri al" ile birlikte geri alınır. Kurulumdaki "Başlangıç borcu" satırı açılınca 6 vaktin dökümü görünür.
5. **Durumlar:** Boş durumda "Henüz bir kayıt yok. Borç eklediğinde ya da kaza kıldığında burada görünür." Seferde kaçan kayıtlar için "seferde kaçtı · 2 rekât" etiketi P1'dir (FR-KAZ-09). Diğer durumlar bu ekranı etkilemez.
6. **Mikro metinler:** "Kaza geçmişi" · "Kalan borç 48" · "Kalan borç bu kayıtlardan hesaplanır. Bir kaydı geri aldığında silinmez; karşılığında bir düzeltme kaydı eklenir." · Türler: "kaza kılındı", "öneri onaylandı", "otomatik eklendi", "elle düzeltme", "düzeltme", "kaza olarak kılındı", "Başlangıç borcu" · "Geri alındı" · "2 kayıt birlikte" · "+1 borç" / "−1 borç" / "±0".
7. **İzlenebilirlik:** FR-KAZ-05, NFR-REL-02 · KS-KAZ-01, 06, 08 · KT-81.
8. **Riskler:** Liste uzadıkça yavaşlayabilir. Sayfalı yükleme Faz 5'te ölçülmeli (NFR-PRF-03'teki 300 ms burada da hedef). "Otomatik" kayıtları gören biri bu özelliği açtığını unutmuş olabilir; satır ayarın adını anıyor.

### Bekleyen öneriler

1. **Amaç:** Biriken önerileri tek tek ya da topluca yanıtlamak.
2. **Hiyerarşi:** "Sen onaylamadan hiçbiri borca yazılmaz" güvencesi → günlere göre gruplanmış öneriler (vakit, ne zaman çıktığı, iki düğme) → "Hepsini borca ekle" ve otomatik ekleme ayarına yönlendirme. En eski gün en üstte, çünkü önce o kapanmalı.
3. **Taslak:** Oneriler (KS-KAZ-05'teki 7 öneri).
4. **Etkileşimler:** Her satırda "Vaktinde" (o kalemi kılındı yapar; KT-13) ya da "Borca ekle" (KT-08), 1 dokunuş. Yanıtlanan satır listeden kalkar ve "Geri al" çubuğu çıkar. "Hepsini borca ekle (7)" onay diyaloğu açar (karar sorusu 3).
5. **Durumlar:** Boş durumda "Bekleyen öneri yok." Cuma günü erkek kullanıcıda öğle satırındaki "Vaktinde" Öğle / Cuma seçtirir. Vitir için "Vaktinde" seçilirse aynı günün yatsısı da kılınmış sayılmalıdır; yatsı borca eklendiyse uyarı gösterilir (BR-03). Mazeretle ilgili öneriler de bu listede, nötr dille görünür.
6. **Mikro metinler:** "Bekleyen öneriler" · "7 vakit" · "Bunlar işaretlenmeden vakti çıkan namazlar. Kıldıysan 'Vaktinde', kılmadıysan 'Borca ekle' de. Sen onaylamadan hiçbiri borca yazılmaz." · "Dün · 26 Ekim Pazartesi" · "07:00'de çıktı" · "Vaktinde" · "Borca ekle" · "Hepsini borca ekle (7)" · "Her seferinde sormamamı istersen: Ayarlar › Otomatik borç ekleme".
7. **İzlenebilirlik:** FR-KAZ-03, FR-TRK-04, BR-00, BR-04 · KS-KAZ-04, 05, KS-TRK-05 · KT-08, 11, 13.
8. **Riskler:** Uzun bir aradan dönen kullanıcı onlarca öneri görebilir; bu, "suçlayıcı başlangıç" hissini doğurabilir. Güvence cümlesi ve tek dokunuşluk "Hepsini borca ekle" yükü hafifletiyor. %200 yazıda iki düğme satıra sığmaz ve alt satıra iner.

## 2. Mazeret

Yalnızca kadın kullanıcıda, Bugün ekranının ⋮ menüsünden açılır (KS-MAZ-01). Mazeret ekranlarının hepsinde ve mazeret sürerken Bugün'de son uygulamalar önizlemesi ve ekran görüntüsü kapalıdır (`FLAG_SECURE`, KS-NFR-02).

### Mazeret başlat (+ 15 gün uyarısı)

1. **Amaç:** Dönemin türünü ve başlangıç anını kaydetmek.
2. **Hiyerarşi:** Tür (iki büyük seçenek ve kısa tarifleri) → başlangıç tarihi ve saati (varsayılan şu an) → neden önemli olduğu → gizlilik notu → "Başlat" → geçmiş bağlantısı. Gizlilik notu "Başlat"ın hemen üstünde duruyor; çünkü kaydetmeden hemen önce sorulan soru "bu nereye gidiyor?" olur.
3. **Taslak:** Mazeret (çekirdek, tür seçilmemiş), M_Temizlik.
4. **Etkileşimler:** Tür seçimi (1) → isteğe bağlı tarih ve saat değişikliği → "Başlat" (1). Bugün ⋮ › Mazeret dahil toplam 4 dokunuş. Tür seçilmeden "Başlat" pasif kalır (KS-MAZ-02). Önceki dönemin bitişinden 15 gün geçmediyse "Başlat"a basınca uyarı diyaloğu çıkar; "Yine de başlat" kaydı "uyarılı" etiketiyle oluşturur (KS-MAZ-11, KT-62).
5. **Durumlar:** Başlangıç bir vaktin içindeyse ve o vakit işaretlenmemişse, o vakit için öneri üretilmez (KS-MAZ-04, KT-37). Bu yüzden saat alanı zorunludur. İnternet yokken çalışır. Hata durumunda genel "Kaydedilemedi" mesajı gösterilir.
6. **Mikro metinler:** "Mazeret" · "Yeni dönem başlat" · "Tür" · "Âdet" / "Aylık dönem" · "Lohusalık" / "Doğum sonrası" · "Başlangıç" · "Bugün, 27 Ekim" · "16:00" · "Başladığını fark ettiğin an. Tam bilmiyorsan en yakın tahminini yaz; o vaktin namazı buna göre değerlendirilir." · "Bu bilgi yalnızca telefonunda durur; bildirimlerde ve widget'ta hiçbir zaman yazmaz. Yedeğe yalnızca parolanla şifrelenerek girer." · "Başlatmak için türü seç." · "Başlat" · "Geçmiş dönemler ve alışkanlık" · Uyarı: "15 günlük temizlik dolmadı" / "Önceki dönemin 20 Ekim 05:40'ta bitti; 15 gün 4 Kasım 05:40'ta doluyor. Hanefî'de iki âdet arasında en az 15 gün temizlik olur, bu yüzden şimdi başlayan kanama âdet sayılmayabilir." / "Yine de başlatabilirsin; kayıt bu uyarıyla saklanır." / "Vazgeç" / "Yine de başlat".
7. **İzlenebilirlik:** FR-MAZ-01, 03, 05, NFR-SEC-02, 03 · KS-MAZ-01, 02, 04, 11, KS-NFR-02 · KT-37 – 39, 62, 63.
8. **Riskler:** Saati tam bilmeyen kullanıcı tahmin girmekten çekinebilir; metin tahmini açıkça kabul ediyor. "Lohusalık" seçeneği nadiren kullanılır, ama yanlışlıkla seçilirse 40 günlük sınır uygulanır; tür, "Başlangıcı düzelt" ekranından değiştirilebilmeli.

### Devam eden dönem ve bitişi kaydetme

1. **Amaç:** Dönemin bittiğini doğru saatle kaydetmek.
2. **Hiyerarşi:** Süre (büyük) ve başlangıç → bu sürede ne olduğu → "Bittiğini kaydet" → "Başlangıcı düzelt".
3. **Taslak:** M_Aktif, M_Bitir (alt sayfa).
4. **Etkileşimler:** "Bittiğini kaydet" (1) → alt sayfada tarih ve saat (varsayılan şu an) → "Kaydet" (1). Geriye dönük kayıt mümkündür (KT-46). Bitiş kaydedilince sonuç ekranı açılır; gerekiyorsa üstüne alışkanlık güncelleme diyaloğu gelir (KS-MAZ-12).
5. **Durumlar:** 240. saat dolunca (alışkanlık ne olursa olsun) dönem kendiliğinden kapanır ve toplu onay ekranı açılır (V11). Bugün ekranında bu sürede mazeret kartı ve "Bitir" kısayolu bulunur (Tur 2).
6. **Mikro metinler:** "Devam eden dönem · Âdet" · "5 gün 7 saat" · "22 Ekim Perşembe 10:00'dan beri" · "Bu sürede vakitler borca eklenmez ve hatırlatma gelmez. 10 gün dolarsa (1 Kasım 10:00) sana haber veririz." · "Bittiğini kaydet" · "Başlangıcı düzelt" · Alt sayfa: "Ne zaman bitti?" / "Kanamanın kesildiğini gördüğün an. Daha önce bittiyse o saati seç; geriye dönük kaydedebilirsin." / "Kaydet" / "Vazgeç".
7. **İzlenebilirlik:** FR-MAZ-01, 02, 04, FR-HTR-04 · KS-MAZ-03, 05 – 08, 10, KS-HTR-06 · KT-40 – 51.
8. **Riskler:** Bitişi kaydetmeyi unutmak en olası hatadır. Hatırlatmalar mazeret boyunca sustuğu için (FR-HTR-04) kullanıcı uzun süre fark etmeyebilir. 240. saatteki bildirim bu riski sınırlıyor; daha erken, nötr bir "Durumunu güncellemek ister misin?" hatırlatması v1.1 için düşünülebilir (belgede yok, yeni öneri).

### Sonuç ekranları

1. **Amaç:** Bitişin o anki vakte etkisini anlamak.
2. **Hiyerarşi:** "Dönem kaydedildi" ve aralık → tek bir sonuç kartı (başlık bir cümlelik sonuç, gövde nedeni) → gusül süresi ve ayar yolu → "Bugün'e git". Sonuç başlığı yapılacak şeyi söylüyor; fıkhi gerekçe gövdede, ikinci planda.
3. **Taslak:** M_SonucFarz (17:00, kalan 60 dk), M_SonucDegil (17:45, kalan 15 dk).
4. **Etkileşimler:** "Bugün'e git" (1). Açılan vakit Bugün'de normal şekilde işaretlenir.
5. **Durumlar:**

| Bitiş | Sonuç | Kaynak |
| --- | --- | --- |
| Vakit içinde, kalan ≥ gusül + tekbir | "İkindiyi vakit çıkmadan kılabilirsin"; vakit açılır; kilit ekranı dışında nötr bildirim | KS-MAZ-05, KT-41, 42 |
| Vakit içinde, kalan < gusül + tekbir | "İkindi için kaza gerekmiyor"; vakit mazeretli kalır | KS-MAZ-07, KT-43, 49 |
| Tam 240 / 960 saatte | Yalnızca tekbir süresi aranır; metin "iftitah tekbiri için yeterli süre" der | KT-44, 45, 48 |
| Yatsı vaktinde | Yatsı ve vitir birlikte açılır; kart ikisini birlikte anar | KS-MAZ-08, KT-51 |
| Vakit dışında (güneş – öğle arası) | "Bir sonraki vakit 12:30'da açılır"; sonuç kartı yerine tek satır | KT-50 |
| Geriye dönük, aradaki vakitler geçmiş | Sonuç kartının altında "5 vakit için öneri oluştu · Gör" | KT-46 |
| 72 saatten kısa | Sonuç ekranı yerine toplu onay açılır | KS-MAZ-09 |

6. **Mikro metinler:** "Dönem kaydedildi" · "Âdet · 22 Ekim 10:00 – 27 Ekim 17:00 · 5 gün 7 saat" · Farz: "İkindiyi vakit çıkmadan kılabilirsin" / "İkindi vakti 18:00'de çıkıyor; 60 dakika var. Bu süre gusül (15 dk) ve iftitah tekbiri için yettiğinden bu vaktin namazı sana farz oldu." / "Kılmadan vakit çıkarsa borca eklemeyi sana soracağız." · Değil: "İkindi için kaza gerekmiyor" / "İkindi vaktinin çıkmasına 15 dakika kalmıştı. Bu, gusül (15 dk) ve iftitah tekbiri (1 dk) için gereken 16 dakikadan az; bu yüzden ikindi bu döneme sayılır." / "Akşam namazı 18:00'de her zamanki gibi açılır." · "Gusül süren 15 dk · Ayarlar'dan değiştirebilirsin." · "Bugün'e git".
7. **İzlenebilirlik:** FR-MAZ-04, BR-12, NFR-SEC-03 · KS-MAZ-05 – 08, KS-NFR-01 · KT-41 – 51.
8. **Riskler:** "Farz oldu" ifadesi baskı gibi okunabilir. Başlık bu yüzden "kılabilirsin" diyor; "farz" kelimesi yalnızca gerekçede geçiyor. Gusül süresini yanlış ayarlamış kullanıcı yanlış sonuç alır; süre her sonuç ekranında görünür.

### Toplu onay

1. **Amaç:** Mazeret kurallarının ortaya çıkardığı kılınmamış vakitleri seçip borca eklemek.
2. **Hiyerarşi:** Ne olduğu ve fıkhi gerekçe → "İşaretini kaldırdığın vakitler eklenmez" → günlere göre gruplanmış onay kutuları → "n vakti borca ekle" ve "Hiçbirini ekleme". Günler kapalı başlar; ilk gün açık örnek olarak çizildi. Her vakit ayrı seçilebilir (KS-MAZ-09).
3. **Taslak:** TopluOnay (72 saatten kısa, 18 kalem, biri işaretsiz), T_Uzun (240. saat, alışkanlık 6 gün, 24 kalem).
4. **Etkileşimler:** Gün kutusu o günün bütün vakitlerini seçer ya da bırakır. Ok düğmesi günü açar. "n vakti borca ekle" (1) seçilenleri tek işlemde yazar. "Hiçbirini ekleme" (1) hiçbir şey yazmaz. Otomatik ekleme açık olsa da bu ekran her zaman çıkar (KT-80, BR-00).
5. **Durumlar:** Varsayılan olarak hepsi seçili (V12). Hiçbiri seçili değilse ana düğme "Borca ekleme" olur ve "Hiçbirini ekleme" ile aynı işi yapar. Ekran kapatılırsa öneriler Bekleyen öneriler'e düşer; hiçbir şey kaybolmaz.
6. **Mikro metinler:** "Borca eklensin mi?" · Kısa: "72 saatten kısa sürdü" / "Kaydettiğin dönem 71 saat 59 dakika sürdü (24 Ekim 08:00 – 27 Ekim 07:59). Hanefî'de 3 günden kısa kanama âdet sayılmaz; bu yüzden bu sürede kılınmayan namazların kazası gerekir." · Uzun: "10 gün doldu" / "Alışkanlığın 6 gün olduğu için 17 Ekim 08:00 – 23 Ekim 08:00 arası âdet sayılır. Sonrası istihâzedir; bu sürede kılınmayan namazların kazası gerekir. Bugünden itibaren namazlarını normal şekilde işaretleyebilirsin." · "İşaretini kaldırdığın vakitler eklenmez." · "4 / 5 vakit seçili" · "Bu durum her zaman sorulur, otomatik eklenmez." · "17 vakti borca ekle" · "Hiçbirini ekleme".
7. **İzlenebilirlik:** FR-MAZ-05, BR-00, BR-13, BR-14 · KS-MAZ-09, 10 · KT-52, 55, 80.
8. **Riskler:** 24 kalemlik bir liste, tam da zor bir dönemin sonunda ağır gelebilir. Metin gerekçeyi bir kez ve sakin söylüyor, karar kullanıcıda kalıyor. Fıkhi gerekçe ("istihâze") yeni başlayan için yabancı bir kelime; Tur 4'te Hakkında'ya kısa bir sözlük eklenebilir.

### Geçmiş dönemler ve alışkanlık

1. **Amaç:** Geçmiş dönemleri görmek ve alışkanlık gününü ayarlamak.
2. **Hiyerarşi:** Alışkanlık (değer ve "Düzenle"), gusül süresi → dönem listesi (tür, süre, aralık, varsa etiket) → gizlilik notu.
3. **Taslak:** MazeretGecmisi (Takvim'deki Ekim dönemiyle tutarlı örnek).
4. **Etkileşimler:** "Düzenle" 1–10 gün seçici açar; "Girilmedi" de seçilebilir. Gusül satırı Ayarlar'a gider. Dönem satırı dönem ayrıntısını açar; bu ekran Tur 4'e bırakıldı (karar sorusu yok, varsayım V14).
5. **Durumlar:** Alışkanlık girilmemişse "Girilmedi (10 gün kabul edilir)" yazar (KS-MAZ-12). Boş listede "Henüz kayıtlı dönem yok." Uyarılı kayıtlar etiket taşır (KS-MAZ-11). Cinsiyet erkeğe çevrilirse bu ekrana giriş kalkar, ama kayıtlar silinmez (KS-AYR-01).
6. **Mikro metinler:** "Geçmiş dönemler" · "Alışkanlık" · "Âdet genellikle kaç gün sürer?" · "Girilmezse 10 gün kabul edilir. 10 günü aşan dönemlerde hesap buna göre yapılır." · "6 gün" / "Düzenle" · "Gusül süresi" / "Ayarlar'da değiştirilir" / "15 dk" · "Dönemler" · "72 saatten kısa · borç soruldu" · "Bu kayıtlar telefonun otomatik yedeğine girmez. Dışa aktarırken parolanla şifrelenir; parolayı unutursan bu kayıtlar geri getirilemez."
7. **İzlenebilirlik:** FR-MAZ-06, FR-YDK-03, 04, NFR-SEC-02 · KS-MAZ-11, 12, KS-YDK-04, KS-AYR-01.
8. **Riskler:** Alışkanlık sorusu kişisel; isteğe bağlı olduğu açıkça yazıyor. Geçmiş bir dönemi düzeltmek, o döneme bağlı önerileri ve onaylanmış borçları etkileyebilir; bu davranış SRS'te tanımlı değil (V14).

## 3. Ortak bileşenler

Hepsi Bileşenler panosunda, telefon genişliğinde ve kaynak kimlikleriyle çizildi.

**Öneri kartı.** Altı varyant var: (a) standart, (b) birden çok öneri ("+6 öneri" bağlantısı, en eski öneri gösterilir), (c) mazeret sonrası farz olan vakit (otomatik ekleme açık olsa da sorulur), (d) yatsı ve vitir için iki sıkı kart, (e) erkek kullanıcıda cuma günü ("cuma" demez, borç öğle olarak yazılır; "Vaktinde kıldım" Öğle / Cuma seçtirir), (f) yanıttan sonra çıkan "Geri al" çubukları. Bugün'de aynı anda en fazla bir öneri kartı görünür (Tur 2, V7). Kaynaklar: FR-KAZ-03, FR-MAZ-04, BR-00, BR-03, BR-08 · KS-KAZ-04 – 06, KS-MAZ-06, 08, KS-TRK-09 · KT-08, 11, 16, 41.

**Engel mesajı.** Hepsi aynı kalıpta: ikon, tek cümle ve "Tamam". Satır vurgulanır, mesaj 4 sn kalır, TalkBack hemen okur. Varyantlar: (a) vakti girmedi, (b) önce yatsı, (c) kerahat (yalnızca kaza, P1), (d) mazeretli kalem (yeni öneri: kalem yine işaretlenmez, ama sessiz kalmak "bozuk" hissi verir), (e) gelecek gün, (f) kaydedilemedi. Widget'tan kilitli kaleme dokunulunca uygulama aynı mesajla açılır (KS-WDG-02). Kerahat hiçbir zaman vaktin kendi namazını engellemez (KT-25).

**Diyaloglar.** "Hepsini borca ekle" onayı, alışkanlık güncelleme (KS-MAZ-12, KT-87) ve borcu 0 olan vakit (Kaza ekranında). Hepsinde metin düğmeleri "Vazgeç / eylem" sırasında; eylem düğmesi ne yapacağını söyler ("7 vakti ekle", "Ekle ve düş").

**Tertip bilgi notu (P1).** Vaktin namazı işaretlendikten sonra Bugün'de öneri kartının yerinde bir kez görünür. İşareti engellemez; vakit darsa hiç gösterilmez (KT-82 – 84). "Kaza'ya git" ve "Anladım" seçenekleri var. v1.0'da gösterilmez.

## 4. Akış (c): kaçırılandan kazaya

| Adım | Ekran | Eylem | Dokunuş |
| --- | --- | --- | --- |
| 1 | — | 15:30'da öğle vakti çıkar, öneri oluşur | 0 |
| 2 | Ana ekran | Uygulama simgesi | 1 |
| 3 | Bugün | Öneri kartında "Borca ekle" (öğle 7 → 8) | 1 |
| 4 | Ana ekran (ertesi gün) | Uygulama simgesi | 1 |
| 5 | Alt gezinme | Kaza sekmesi | 1 |
| 6 | Kaza | Öğle satırında "Kıldım" (8 → 7) | 1 |
| | | **Toplam** | **5** |

Kısa yol: öneri hâlâ bekliyorsa, Takvim › gün › öğle › "Sonradan kaza olarak kıldım" ile iki kayıt tek işlemde yazılır; simge dahil 5 dokunuş, tek oturum. Vakit seçme sayfası kullanılsaydı toplam 7 dokunuş olurdu (karar sorusu 2). Otomatik ekleme açıksa ilk bölüm 0 dokunuş.

## 5. Bulunan tutarsızlık: KT-52 ve KS-MAZ-09

KT-52, "G1 08:00'de başlar, G3 07:59'da biter" diyor ve bunu "71 saat 59 dk" olarak etiketliyor. Ama G1 08:00'den G3 07:59'a geçen süre **47 saat 59 dakika**. Bu aralıkta kaçan kalem sayısı 12 ve tablo da 12 yazıyor; tutarsız olan süre etiketi. KS-MAZ-09 da aynı aralığı ve 12 kalemi kullanıyor. Sayım kodla doğrulandı.

Sorun, testin amacıyla ilgili. KT-52 "Sınır" türünde bir senaryo ve 72 saatlik eşiğin hemen altını sınamalı. 47 saat 59 dakika ise sınır değil. Eşiğin hemen altı **G4 07:59**: 71 saat 59 dakika ve 18 kalem (G1 öğle–vitir 5, G2 6, G3 6, G4 sabah 1). Bu, KT-53'teki tam 72 saatle (G4 08:00) de tutarlı.

Önerilen düzeltme (karar sorusu 1): KT-52'de bitiş G4 07:59 ve 18 kalem; KT-80 bu satıra atıf yaptığı için 18 kalem; KS-MAZ-09'da bitiş G4 07:59 ve 18 kalem. Kural (BR-13) değişmiyor, yalnızca test verisi düzeliyor. Bu nedenle hoca kontrolü gerekmez. Toplu onay taslağı düzeltilmiş hâliyle (18 kalem) çizildi.

## 6. Varsayımlar, değişiklik talepleri ve Tur 5'e notlar

**Açıkça yaptığım varsayımlar** (Tur 1–2'deki V1–V9'un devamı)

- **V10 — Kaza kaydı satırdan yapılır.** Her vakit satırının kendi "Kıldım" düğmesi var; vakitsiz kayıt imkânsız (karar sorusu 2).
- **V11 — 240. saatte dönem kendiliğinden kapanır.** Sonrası istihâze olduğu ve namaz düştüğü için (BR-14) kullanıcıdan ayrıca bitiş istenmez. Dönem kaydının bitişi başlangıç + 240 saat olarak yazılır; nifasta 960 saat (BR-15).
- **V12 — 72 saatten kısa dönem silinmez.** "72 saatten kısa" etiketiyle saklanır. Günleri takvimde mazeretli görünmez; kalemler kullanıcının seçimine göre "Kılınmadı · borca eklendi" ya da "İşaretlenmedi" olur. Toplu onayda varsayılan olarak hepsi seçilidir; bir düğmeye basmadan hiçbir şey yazılmadığı için bu BR-00 ile çelişmez.
- **V13 — Gizlilik.** Mazeret ekranları ve mazeret sürerken Bugün `FLAG_SECURE` kullanır. Ekran görüntüsü de engellenir; kullanıcı mazeret ekranının görüntüsünü alamaz.
- **V14 — Dönem düzeltme.** "Başlangıcı düzelt" ve geçmiş dönem ayrıntısı dönemin tarihlerini değiştirebilir. Bu bir ayar değişikliği değil, veri düzeltmesidir; bu yüzden BR-21'in "geçmişi yeniden hesaplama" yasağına girmez. Düzeltmeden sonra yeni öneriler oluşabilir ya da geçersiz kalan öneriler kapanabilir; onaylanmış borçlara dokunulmaz, gerekirse yeni bir öneri olarak sorulur. Ayrıntılı ekran Tur 4'te; SRS'e değişiklik talebi gerekir.
- **V15 — Mazeret sürerken Kaza ekranı değişmez.** Gizlilik için.

**SRS ve kabul senaryoları için değişiklik talepleri**

- **DT-2 · Kaza kaydı satırdan (karar sorusu 2 onaylanırsa):** KS-KAZ-03 "vakit seçilmeden Kaydet pasif" yerine "Kaza ekranında vakitsiz bir 'Kaza kıldım' eylemi bulunmaz; kayıt yalnızca bir vakit satırından başlar" olarak yeniden yazılır. FR-KAZ-02'nin kabul kriteri değişmez.
- **DT-3 · KT-52 / KT-80 / KS-MAZ-09 test verisi düzeltmesi** (Bölüm 5, karar sorusu 1).
- **DT-4 · Dönem düzeltme davranışı** (V14) FR-MAZ-01'e eklenmeli.
- **DT-5 · 240. saatte otomatik kapanma** (V11) FR-MAZ-05'e açıklama olarak eklenmeli.

**Tur 5'te raporlanacak kabul senaryosu boşlukları**

- Bekleyen öneriler ekranı ve "Hepsini borca ekle".
- Mazeretli kaleme dokunuş mesajı.
- Mazeret başlangıcını ya da geçmiş bir dönemi düzeltme.
- 240. saatte dönemin kapanması ve sonraki kanamanın kaydı.
- Kaza geçmişinde birlikte yazılan çiftin tek "Geri al" ile geri alınması (karar 1a).
- Toplu onayda bazı kalemlerin işaretinin kaldırılması.

## 7. Karar soruları (Tur 3)

1. **KT-52 / KS-MAZ-09 düzeltmesi:** Bölüm 5'teki düzeltmeyi (bitiş G4 07:59, 18 kalem) karar tablosuna ve kabul senaryolarına işleyeyim mi? Kural değişmiyor, yalnızca test verisi düzeliyor.
2. **Kaza kaydı nasıl yapılsın?**
   - (a) Önerim: Her vakit satırının kendi "Kıldım" düğmesi olsun. Uygulama açıksa 2 dokunuş sürer ve vakitsiz kayıt imkânsız olur. KS-KAZ-03 yeniden yazılır (DT-2).
   - (b) Tek bir "Kaza kıldım" düğmesi alt sayfa açsın; orada vakit seçilip "Kaydet"e basılsın. Bu, KS-KAZ-03'e olduğu gibi uyar, ama her kayıt 2 dokunuş fazla sürer.
3. **"Hepsini borca ekle" olsun mu?** Bekleyen öneriler ekranında ve onay diyaloğuyla birlikte. Uzun aradan dönen kullanıcıyı kurtarır, ama kıldığı vakitleri de düşünmeden borca ekletebilir. Önerim: olsun. Bir onay diyaloğu ve kaza geçmişinden tek tek geri alma bu riski karşılıyor.

### Kararlar (27 Ekim 2026)

Üç önerinin üçü de kabul edildi.

- [x] **1. KT-52 / KS-MAZ-09 düzeltmesi:** Karar tablosu v1.3'e (KT-52, KT-80) ve kabul senaryolarına (KS-MAZ-09) işlendi: bitiş G4 07:59, 18 kalem.
- [x] **2. Kaza kaydı satırdan:** (a) Her vakit satırının kendi "Kıldım" düğmesi var. Kabul senaryolarında KS-KAZ-02, 03, 07 ve KS-NFR-04 buna göre yeniden yazıldı (DT-2 kapandı).
- [x] **3. "Hepsini borca ekle":** Bekleyen öneriler ekranında, onay diyaloğuyla birlikte olacak. Kabul senaryosu Tur 5'te eklenecek.

V10 – V15 varsayımları da kabul edildi. DT-4 (dönem düzeltme) ve DT-5 (240. saatte kapanma) SRS'e, Tur 5 sonunda toplu bir sürümle (1.0-taslak.6) işlenecek. DT-1 (gömülü il/ilçe listesi) de aynı sürüme girecek; Diyanet izni ön koşul olarak açık kalacak.

Sıradaki: Tur 4 — widget'lar, bildirim, Ayarlar, kurtarma ve pil kısıtı ekranları.

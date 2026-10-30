# Ekran Taslakları — Tur 4

27 Ekim 2026 · @ece

## Özet

Tur 4; widget'ları, bildirimleri, Ayarlar'ı, kurtarma ekranını ve pil kısıtı yönlendirmesini kapsıyor. Taslaklar tasarım panosunun "Tur 4" sayfasında: [Ekran Taslakları](https://claude.ai/artifact/JT9ex8zdaWsNBymxW5UhVq).

- **Widget'lar:** Ana ekran bağlamı, 6 küçük ve 7 orta widget durumu (koyu tema dahil).
- **Akış (b):** Widget'tan işaretlemek 1 dokunuş, geri almak 1 dokunuş. Uygulama hiç açılmıyor.
- **Bildirimler:** 7 bildirim örneği, hiçbir zaman gönderilmeyecek metinler ve bildirim kanalları.
- **Ayarlar:** Ana liste; Hatırlatmalar, Kesin alarm, Dışa aktar, İçe aktar ve Hakkında alt sayfaları; 4 onay diyaloğu.
- **Sistem:** Kurtarma ve pil kısıtı yönlendirme ekranları.
- Sonda 3 karar sorusu var (Bölüm 9).

Görsel dil A · Gün ışığı. Örnek saatler kabul senaryolarının referans gününden: imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30.

## 0. Tur 3 kararlarının uygulanması

- **Karar tablosu v1.3'e yükseldi.** KT-52'nin bitişi G4 07:59 oldu, kalem sayısı 18'e çıktı. KT-80 de buna göre güncellendi. Değişiklik karar kaydına 7. madde olarak yazıldı.
- **Kabul senaryolarında 5 senaryo güncellendi.**
  - KS-MAZ-09: bitiş G4 07:59, 18 kalem.
  - KS-KAZ-02 ve KS-KAZ-07: kaza kaydı satırdaki "Kıldım" ile yapılıyor.
  - KS-KAZ-03, "Kaza yalnızca bir vakit satırından kaydedilir" olarak yeniden yazıldı.
  - KS-NFR-04'ün metni düzeltildi.
  - Bütün değişiklikler belgenin "Sonradan yapılan değişiklikler" bölümünde.
- **"Hepsini borca ekle" tasarımda kalıyor.** Kabul senaryosu Tur 5'te eklenecek.
- **SRS 1.0-taslak.6, Tur 5 sonunda tek seferde yazılacak.** Kapsamı:
  - Tur 2–4'teki değişiklik talepleri: DT-1, DT-4, DT-5 ve bu turdaki DT-6, DT-7.
  - Varsayımlardan sürekli davranışa dönüşenler.

## 1. Widget'lar

### Küçük widget (2×2)

1. **Amaç:** O anki vakti uygulamayı açmadan tek dokunuşla işaretlemek.
2. **Hiyerarşi:** Vaktin adı büyük yazılır. Altında sıradaki vaktin kesin saati ve geri sayım, en altta tam genişlikte "Kıldım" düğmesi var. Kesin saat geri sayımdan önce geliyor; widget güncellenmese bile kesin saat hiçbir zaman yanlış olmaz.
3. **Taslak:** W_AnaEkran (bağlam) ve Widgetlar (6 durum).
4. **Etkileşimler:**
   - "Kıldım" 1 dokunuşta işaretler. Uygulama açılmaz, kısa bir titreşim olur ve widget 1 saniyeden kısa sürede "Kılındı 14:48" hâline geçer (NFR-PRF-02).
   - "Kılındı"ya dokunmak işareti geri alır (FR-TRK-03).
   - Aynı düğmeye 1 saniye içinde gelen ikinci dokunuş yok sayılır (V17). Böylece yanlışlıkla yapılan çift dokunuş işareti geri almaz.
   - Gövdeye dokunmak Bugün ekranını açar.
5. **Durumlar:**

| Durum | Görünüm | Kaynak |
| --- | --- | --- |
| Normal | "Şu an · Öğle · İkindi 15:30 · 0:43:12 kaldı · Kıldım" | FR-WDG-01, KS-WDG-01 |
| Kılındı | "Kılındı 14:48" (çerçeveli); dokunuş geri alır | FR-TRK-03, KS-WDG-04 |
| Vakit arası (güneş – öğle) | "Sıradaki · Öğle · 12:30'da girer"; düğme yok | BR-02 |
| Mazeret aktif | "Sıradaki · İkindi · 15:30'da girer"; düğme ve kelime yok, vakit arasıyla aynı görünüm | FR-WDG-04, KS-WDG-05 |
| Tahmini vakit | "Şu an · tahmini", "İkindi ~15:30" | FR-VKT-03, KS-VKT-04 |
| Gece yarısı – imsak | "Şu an · Vitir · İmsak 05:30"; işaret önceki namaz gününe yazılır | KS-TRK-11 |
| Koyu tema | Sistem temasını izler | Tur 1 görsel dil |
| Kerahat | Etkilemez (vaktin kendi namazı engellenmez) | BR-07 |
| Hata | Yazma başarısızsa widget eski hâlinde kalır; bir sonraki açılışta uygulama "Kaydedilemedi" gösterir | NFR-REL-02 |

6. **Mikro metinler:** "Şu an" · "Sıradaki" · "İkindi 15:30" · "0:43:12 kaldı" · "Kıldım" · "Kılındı 14:48" · "12:30'da girer" · "Şu an · tahmini" · "İkindi ~15:30" · TalkBack: "Öğle, şu anki vakit. Kıldım" / "Öğle kılındı, 14:48. Geri almak için dokun".
7. **İzlenebilirlik:** FR-WDG-01, 03, 04, FR-TRK-03, FR-VKT-03, NFR-PRF-02, NFR-USA-01 · KS-WDG-01, 04, 05, KS-NFR-06, KS-VKT-04.
8. **Riskler:**
   - **Geri sayımın saniyesi işler.** Android'in kendi sayacı (Chronometer) saniyeyi gizleyemiyor. Bu, uygulamadaki "İkindiye 43 dk" ile farklı görünür ve kabul senaryosunun metniyle de uyuşmaz (DT-7). Dakika hassasiyetinde güncellemek ise pil harcar. Faz 1 prototipinde iki yol da denenecek.
   - **Bazı başlatıcılar widget'ı geç yeniler.** Xiaomi gibi başlatıcılar yenilemeyi geciktirebilir. NFR-PRF-02 iki cihazda ölçülecek.

### Orta widget (4×2)

1. **Amaç:** Günün 6 kalemini uygulamayı açmadan işaretlemek.
2. **Hiyerarşi:** Başlık satırı şu anki vakti ve sıradaki vakti gösterir, altında tarih yazar. Altı hücre Bugün ekranıyla aynı sırada dizilir; durum ikonu üstte, ad altta. Şu anki hücre çerçeve ve zemin rengiyle ayrışır.
3. **Taslak:** Widgetlar (7 durum), W_AnaEkran.
4. **Etkileşimler:**
   - Açık hücreye dokunmak işaretler, işaretli hücreye dokunmak geri alır; ikisi de 1 dokunuş.
   - Kilitli hücreye dokunmak işaret koymaz; uygulama "İkindi vakti henüz girmedi" mesajıyla açılır (KS-WDG-02).
   - Vakti geçmiş ama işaretlenmemiş bir hücreye dokunmak uygulamayı "Vaktinde / kaza olarak" seçimiyle açar. Widget'ta seçim penceresi gösterilemediği için bu yol seçildi.
5. **Durumlar:**
   - **Normal, 14:47:** sabah ✓, öğle şu an, diğerleri kilitli.
   - **Kilitli kaleme dokunuş:** hücre işaretlenmez, uygulama açıklamayla açılır.
   - **Yatsıdan sonra, 20:05:** vitrin kilidi açılır (KS-WDG-03).
   - **Mazeret:** hücrelerin hepsi nötr, tire ile gösterilir; hiçbir kelime yazmaz.
   - **Gece yarısı, 00:40:** önceki namaz günü gösterilir.
   - **Cuma (erkek):** öğle hücresi "Cuma" yazar (karar sorusu 1).
   - **Koyu tema ve tahmini:** saatin önüne "~" konur.
   - **Büyük sistem yazısında:** hücreler yalnızca ikon gösterir; ad TalkBack'te okunur.
6. **Mikro metinler:** "Öğle · şu an" · "İkindi 15:30 · 0:43:12" · "27 Ekim Salı" · "Sıradaki ikindi" · "Vitir · şu an" · "İmsak 05:30 · 9:25:00" · "Cuma · şu an" · TalkBack: "Öğle, şu anki vakit, işaretlemek için dokun" / "İkindi, kilitli" / "Akşam, işaretlenmez".
7. **İzlenebilirlik:** FR-WDG-02, 03, 04, FR-TRK-02, 06, 07, BR-02, BR-03, BR-08 · KS-WDG-02 – 05, KS-TRK-08, 11 · KT-01, 04.
8. **Riskler:**
   - **Hücreler küçük.** Hücreler 52×100 dp civarında. Yanlış hücreye dokunma riski var; geri alma ve titreşim bunu telafi ediyor.
   - **Ekran okuyucuyla yetersiz kalabilir.** Altı hücreli düzen TalkBack kullanıcısı için uzun gelebilir. Küçük widget tek düğmeli olduğu için onlara daha uygun bir alternatif.

## 2. Bildirimler

1. **Amaç:** Vakit çıkmadan haber almak ve bildirimden "Kıldım" demek.
2. **Hiyerarşi:** Başlık ne kadar süre kaldığını söyler. Gövde bir sonraki vaktin saatini verir. Eylem tek ve açıktır.
3. **Taslak:** Bildirimler (7 örnek, hiç gönderilmeyenler, kanallar).
4. **Etkileşimler:**
   - "Kıldım" (1 dokunuş) uygulamayı açmadan işaretler.
   - Ardından bildirim 5 saniye boyunca "Öğle işaretlendi · Geri al" olarak kalır, sonra kendiliğinden kapanır (V18).
   - Gövdeye dokunmak Bugün'ü açar.
   - Yatsı ve vitir için tek bildirim gelir, iki eylemiyle: "Yatsıyı kıldım" ve "İkisini de kıldım" (V19).
   - 240. saat bildirimine dokunmak toplu onay ekranını açar.
5. **Durumlar:**

| Durum | Davranış | Kaynak |
| --- | --- | --- |
| Normal | Yalnızca işaretlenmemiş vakit için, X dk önce | FR-HTR-01, KS-HTR-01, 02, KT-64, 65 |
| Kilit ekranı, hatırlatma | Tam görünür (hassas değil); "Kıldım" kilit açmadan çalışır | FR-HTR-02 |
| Kilit ekranı, mazeretten doğan | Yalnızca "Namaz takibi: yeni bir bilgi var"; eylem yok | NFR-SEC-03, KS-NFR-01 |
| Kilit açık, mazeret bitişi | "İkindi vakti çıkmadan kılabilirsin"; mazeret kelimesi yok | FR-MAZ-04, KS-MAZ-05 |
| Mazeret aktif | Hatırlatma gelmez | FR-HTR-04, KS-MAZ-03 |
| İzin yok | Hiç bildirim yok; Ayarlar'da uyarı bandı | FR-ONB-04, KS-ONB-05 |
| Gecikme | En çok 10 dk (pencereli alarm); kesin alarm izniyle ±1 dk | NFR-TIM-01, KS-HTR-05 |
| Yeniden başlatma, saat dilimi, güncelleme | Alarmlar yeniden kurulur | FR-HTR-03, KS-HTR-05 |

6. **Mikro metinler:** "Öğle vaktinin çıkmasına 30 dk" / "İkindi 15:30'da giriyor." / "Kıldım" · "Öğle işaretlendi" / "Geri al" · "Yatsı ve vitir vaktinin çıkmasına 30 dk" / "Vakit 05:30'da çıkıyor." / "Yatsıyı kıldım" / "İkisini de kıldım" · "Namaz takibi: yeni bir bilgi var" · "İkindi vakti çıkmadan kılabilirsin" / "Vakit 18:00'de çıkıyor." · "Bugünden itibaren namazlarını işaretleyebilirsin" · Kanallar: "Sabah", "Öğle", "İkindi", "Akşam", "Yatsı ve vitir", "Bilgilendirmeler".
7. **İzlenebilirlik:** FR-HTR-01 – 05, FR-MAZ-04, 05, NFR-SEC-03, NFR-TIM-01, IF-03 · KS-HTR-01 – 07, KS-MAZ-05, 10, KS-NFR-01 · KT-41, 55, 60, 64 – 68.
8. **Riskler:**
   - **Bazı Android sürümlerinde eylem kilitli kalabilir.** Kilit ekranından "Kıldım" deneyen kullanıcıdan kilit açması istenebilir; Faz 6'da cihazlarda denenecek.
   - **"Bilgilendirmeler" kanalı kapatılırsa bilgi kaybolur.** Mazeret bitişi gibi önemli bilgiler gelmez. Uygulama bunu fark edip Ayarlar'da nötr bir uyarı gösterir.
   - **Suçlayıcı metin listesi tutarlı kalmalı.** "Hiç gönderilmeyenler" listesi metin incelemesinin bir parçası (NFR-USA-03).

## 3. Ayarlar

### Ayarlar (ana liste)

1. **Amaç:** Bir ayarı bulup değiştirmek.
2. **Hiyerarşi:** Yedi bölüm sıklık ve önem sırasıyla dizildi: Konum, Sen, Kaza, Mazeret (yalnızca kadın), Hatırlatmalar, Yedekleme, Diğer. "Tüm verileri sil" en sonda duruyor.
3. **Taslak:** Ayarlar (kaydırılmış tam görünüm, kadın kullanıcı).
4. **Etkileşimler:**
   - Her satır kendi alt sayfasını ya da onay diyaloğunu açar.
   - "Otomatik borç ekleme" doğrudan açılıp kapanır. Açılınca bir kez şu bilgi gösterilir: "Belirsiz durumlar yine sorulur" (BR-00).
   - İlçe ve cinsiyet değişikliği onay ister (KS-AYR-01, 02).
   - "Âdet alışkanlığı" Mazeret › Geçmiş dönemler ekranına gider.
5. **Durumlar:**
   - **Erkek kullanıcı:** Mazeret bölümü yok.
   - **Vakit önbelleği:** "Vakitler" satırı önbelleğin bitiş tarihini gösterir. Tahmini moddaysa "Tahmini · internet bekleniyor" yazar.
   - **Bildirim izni yok:** Hatırlatmalar satırında "İzin kapalı" yazar.
   - **Hiç yedek alınmadı:** "Son yedek: hiç alınmadı" yazar.
6. **Mikro metinler:** "Ayarlar" · "İlçe" / "Bornova, İzmir" · "Vakitler" / "Diyanet · 26 Kasım'a kadar telefonda" · "Cinsiyet" / "Kadın" · "Otomatik borç ekleme" / "Kapalıyken kaçırılan her vakit için sana sorarım." · "Gusül süresi" / "Mazeret bitişindeki hesap için" / "15 dk" · "Âdet alışkanlığı" / "6 gün" · "Hatırlatmalar" / "5 vakit · 30 dk önce" · "Tam zamanında gelsin" / "Şu an birkaç dakika gecikebilir" / "Kapalı" · "Hatırlatmalar geç mi geliyor?" · "Dışa aktar" / "Son yedek: hiç alınmadı" · "İçe aktar" · "Telefon yedeği" / "Google yedeğiyle açık · mazeret kayıtları hariç" · "Hakkında" · "Tüm verileri sil" · "Namaz Takibi 1.0.0 · Hanefî".
7. **İzlenebilirlik:** FR-AYR-01, 02, FR-KAZ-03, FR-VKT-01, 02, FR-YDK-04, BR-21, DATA-02 · KS-AYR-01 – 04, KS-KAZ-06, KS-ONB-05.
8. **Riskler:**
   - **Otomatik borç ekleme yanlış anlaşılabilir.** Kullanıcı ayarı "her şeyi sessizce yazar" sanabilir. Alt satır ve açılıştaki bilgi notu bunu düzeltiyor.
   - **Kerahat dakikaları listede yok.** FR-AYR-01 bu ayarı P0'da sayıyor, ama v1.0'da onu kullanan hiçbir özellik yok (karar sorusu 3).

### Hatırlatmalar

1. **Amaç:** Hangi vakitlerin ve ne kadar önce hatırlatılacağını ayarlamak.
2. **Hiyerarşi:** Varsa izin bandı en üstte. Altında kural cümlesi, ardından vakit başına satırlar (hatırlatmanın kurulacağı saat, süre ve açma/kapama anahtarı), en altta kesin saat ve pil bağlantıları.
3. **Taslak:** A_Hatirlatmalar (izin kapalı durumu).
4. **Etkileşimler:**
   - Anahtar ilgili vaktin hatırlatmasını açar ya da kapatır.
   - Süre düğmesi 10, 15, 20, 30, 45 ve 60 dk seçeneklerini açar.
   - "Bildirimlere izin ver" Android 13 ve üstünde sistem penceresini açar; reddedilmişse uygulama ayarlarına gider.
5. **Durumlar:**
   - **İzin var:** bant yok.
   - **Mazeret aktif:** satırların üstünde "Şu an hatırlatma gelmez" yazar; ayarlar değiştirilebilir (FR-HTR-04).
   - **Saatler:** satırdaki saat bugünün vakitlerinden hesaplanır; tahmini moddaysa "~" alır.
6. **Mikro metinler:** "Bildirim izni kapalı" / "İzin verene kadar hatırlatma gelmez. Uygulamanın geri kalanı her zamanki gibi çalışır." / "Bildirimlere izin ver" · "Yalnızca işaretlemediğin vakitler için, vakit çıkmadan önce. Kıldıysan bildirim gelmez." · "Öğle" / "15:00'te · ikindi 15:30" / "30 dk" · "Yatsı ve vitir" / "05:00'te · imsak 05:30" · "Tam zamanında gelsin" / "Şu an en çok 10 dk gecikebilir" · "Hatırlatmalar geç mi geliyor?" / "Telefonunun pil ayarlarını kontrol et".
7. **İzlenebilirlik:** FR-HTR-01, 04, FR-ONB-04, BR-05, NFR-TIM-01 · KS-HTR-02, 03, KS-ONB-05 · KT-64 – 68.
8. **Riskler:** "30 dk önce" ifadesi "vakit girdikten 30 dk sonra" diye anlaşılabilir. Satırda hatırlatmanın kurulacağı kesin saat yazdığı için bu karışıklık önleniyor.

### Kesin alarm izni

1. **Amaç:** Hatırlatmaların tam zamanında gelmesini isteyip istemediğine karar vermek.
2. **Hiyerarşi:** Soru, ardından neden gerektiği. Görsel bir karşılaştırma iki durumu gösteriyor: izinsiz 15:00–15:10 arası, izinle 15:00. Sonra iznin kapsamı ve iki seçenek.
3. **Taslak:** A_KesinAlarm.
4. **Etkileşimler:** "Ayarlarda izin ver" Android'in "Alarmlar ve hatırlatıcılar" sayfasını açar. Dönüşte izin durumu okunur ve alarmlar yeniden kurulur. "Gerek yok" sayfayı kapatır.
5. **Durumlar:** Android 12'den eski cihazlarda bu satır ve sayfa görünmez. İzin zaten açıksa satırda "Açık" yazar ve sayfa kapatma yolunu anlatır.
6. **Mikro metinler:** "Hatırlatmalar tam zamanında gelsin mi?" · "Android, pili korumak için hatırlatmaları birkaç dakika geciktirebilir. Tam zamanında gelmeleri için 'Alarmlar ve hatırlatıcılar' iznini açman gerekir." · "İzin yokken" / "15:00 – 15:10 arası" · "İzinle" / "15:00" · "Öğle hatırlatması, vakit 15:30'da çıkıyor. İki durumda da vakit çıkmadan gelir." · "Bu izin yalnızca zamanlamayla ilgilidir; uygulama başka hiçbir şeye erişmez. İstediğin zaman kapatabilirsin." · "Ayarlarda izin ver" · "Gerek yok, böyle kalsın".
7. **İzlenebilirlik:** NFR-TIM-01, NFR-SEC-04, yol haritası Faz 6 · KS-HTR-05.
8. **Riskler:** "Alarm" kelimesi kullanıcıya uyandırma sesini düşündürebilir. Metin, iznin yalnızca zamanlamayla ilgili olduğunu açıkça söylüyor.

### Dışa aktar

1. **Amaç:** Verinin kopyasını bir dosyaya almak.
2. **Hiyerarşi:** Ne yapıldığı → dosyaya nelerin girdiği → mazeret parolası (iki kez) → "parolayı not et" uyarısı → "Dosyayı kaydet".
3. **Taslak:** A_DisaAktar (parolalar eşleşmiyor).
4. **Etkileşimler:** Parola en az 8 karakter olmalı ve iki alan eşleşmeli; ikisi sağlanmadan "Dosyayı kaydet" pasif kalır. Düğme sistemin dosya kaydetme penceresini açar (depolama izni gerekmez, KS-NFR-05). Önerilen dosya adı: `namaz-takibi-yedek-2026-10-27.json`. Başarılı olunca "Yedek kaydedildi" mesajı çıkar ve Ayarlar'daki "Son yedek" satırı güncellenir.
5. **Durumlar:** Erkek kullanıcıda ya da hiç mazeret kaydı yoksa parola alanları görünmez (V25). Kaydetme başarısızsa "Dosya kaydedilemedi. Başka bir yer seçmeyi dene." mesajı çıkar.
6. **Mikro metinler:** "Dışa aktar" · "Tüm verilerin tek bir dosyaya kaydedilir. Bu dosyayla yeni bir telefona taşıyabilir ya da sonra geri yükleyebilirsin." · "Takip kayıtları · 22 gün" · "Kaza borcu ve kaza geçmişi" · "Ayarlar" · "Mazeret kayıtları · parolayla şifrelenir" · "Mazeret kayıtları için parola" / "En az 8 karakter." · "Parolayı tekrar yaz" / "İki parola aynı değil." · "Parolayı bir yere not et. Unutursan mazeret kayıtları bu dosyadan geri getirilemez; diğer veriler yine açılır." · "Dosyayı kaydet" · "Kaydedeceğin yeri bir sonraki adımda seçersin."
7. **İzlenebilirlik:** FR-YDK-01, 03, NFR-SEC-02, NFR-SEC-04, IF-04 · KS-YDK-01, 03, KS-NFR-05.
8. **Riskler:**
   - **Parola unutulursa mazeret kayıtları kaybolur.** Uyarı kaydetmeden önce gösteriliyor.
   - **Dosyanın içi düz metindir.** Mazeret dışındaki veri (takip, kaza) dosyada şifresiz duruyor ve bu FR-YDK-03 ile uyumlu. Kullanıcı yedeği paylaşırken bunu bilmeli; yardım metnine eklenebilir.

### İçe aktar

1. **Amaç:** Bir yedek dosyasını bu telefona yüklemek.
2. **Hiyerarşi:** Seçilen dosyanın kimliği (ad, tarih, sürüm) → içindekiler → varsa mazeret parolası → "mevcut veriler değiştirilir" uyarısı ve önce dışa aktarma bağlantısı → eylemler.
3. **Taslak:** A_IceAktar (mazeret parolası yanlış).
4. **Etkileşimler:**
   - Dosya, sistemin dosya seçicisinden seçilir.
   - Yanlış parolada iki seçenek var: "Parolayı dene" ya da "Mazeret kayıtları olmadan içe aktar" (KS-YDK-03).
   - İçe aktarma tek bir işlem içinde (transaction) yapılır. Yarıda kalırsa telefondaki veri değişmez.
   - Eski şema sürümleri önce dönüştürülür (FR-YDK-02).
5. **Durumlar:**
   - **Dosya bozuk ya da tanınmıyor:** "Bu dosya bir Namaz Takibi yedeği değil ya da bozuk."
   - **Uygulamadan yeni bir sürümle alınmış dosya:** "Bu yedek daha yeni bir sürümle alınmış. Uygulamayı güncelle."
   - **Mazeret kaydı olmayan dosya:** parola alanı görünmez.
6. **Mikro metinler:** "İçe aktar" · "namaz-takibi-yedek-2026-10-20.json" / "20 Ekim 2026 · Namaz Takibi 1.0 · şema 1" · "Takip kayıtları · 15 gün" · "Kaza borcu · 49 vakit, 12 kayıt" · "Mazeret kayıtları · 3 · şifreli" · "Mazeret kayıtlarının parolası" / "Mazeret verisi için parola yanlış, yeniden dene." · "Bu telefondaki veriler yedektekilerle değiştirilir. Şu anki kayıtlarını saklamak istiyorsan önce onları dışa aktar." / "Önce mevcut verimi dışa aktar" · "Parolayı dene" · "Mazeret kayıtları olmadan içe aktar".
7. **İzlenebilirlik:** FR-YDK-01, 02, 03, NFR-REL-01, 02, IF-04 · KS-YDK-01 – 03.
8. **Riskler:** Değiştirme davranışı mevcut veriyi siler. Yeni telefonda bu sorun değil, ama aynı telefonda yanlış dosya seçmek veri kaybettirir. Önce dışa aktarma bağlantısı bu riski azaltıyor (karar sorusu 2).

### Hakkında

1. **Amaç:** Mezhebi, vakit kaynağını, uygulamanın nasıl karar verdiğini ve gizliliği okumak.
2. **Hiyerarşi:** Mezhep → vakitler → "Uygulama nasıl karar verir?" → küçük sözlük → gizlilik → lisanslar ve sürüm. Özellik listesindeki "kaynak ve hesap yöntemi ayarlarda yazılı" maddesi bu sayfayla karşılanıyor.
3. **Taslak:** A_Hakkinda.
4. **Etkileşimler:** Yalnızca okuma; "Açık kaynak lisansları" bir liste açar.
5. **Durumlar:** Tahmini moddayken "Vakitler" bölümüne "Şu an tahmini vakitler kullanılıyor" satırı eklenir.
6. **Mikro metinler:** Taslaktaki metinler. Sözlük: Kaza, Vitir, Kerahat vakti, Gusül, İstihâze.
7. **İzlenebilirlik:** FR-ONB-02, FR-VKT-03, NFR-SEC-01, NFR-USA-03, özellik listesi P0 #5 · KS-ONB-02.
8. **Riskler:** Sözlükteki tanımlar fıkhi; hoca kontrolüne eklenmeli. Tanımlar kısa tutuldu ve tartışmalı ayrıntıya girmiyor.

### Onay diyalogları

- **Konum değişikliği:** "İlçeni Bornova olarak değiştirelim mi?" Bugünden itibaren yeni vakitler kullanılır ve hatırlatmalar yeniden kurulur; geçmiş kayıtlar değişmez (KS-AYR-02, V21).
- **Cinsiyet değişikliği:** Mazeret menüsü gizlenir, kayıtlar silinmez ve takvimde aynen görünür, cuma seçeneği açılır (KS-AYR-01, KT-70).
- **Verileri sil, adım 1:** "Önce bir yedek al". Seçenekler: "Dışa aktar", "Yedeksiz devam et", "Vazgeç" (DATA-02).
- **Verileri sil, adım 2:** "Tüm veriler silinsin mi?" Neyin silineceğini sayar; odak "Vazgeç"te başlar. Silme işleminden sonra uygulama onboarding'e döner.

## 4. Kurtarma

1. **Amaç:** Veritabanı açılmadığında, bir şeyi değiştirmeden önce veriyi kurtarmak.
2. **Hiyerarşi:** Sakin bir başlık ve "verilerin büyük ihtimalle hâlâ telefonunda" güvencesi. Ardından numaralı seçenekler: önce dışa aktarma, sonra tekrar deneme, yedekten geri yükleme ve hata ayrıntısını kopyalama. En altta, ayrı duran son çare "Verileri sil".
3. **Taslak:** Kurtarma.
4. **Etkileşimler:**
   - "Verilerini dışa aktar" okunabilen tabloları bir yedek dosyasına yazar. Hiçbiri okunamazsa ham veritabanı dosyasının kopyasını kaydetmeyi önerir (V23).
   - "Tekrar dene" açılışı yeniden dener.
   - "Yedekten geri yükle" İçe aktar akışını açar.
   - "Hata ayrıntısını kopyala" yalnızca teknik bilgiyi kopyalar (V24).
   - "Verileri sil" iki adımlı silme akışını başlatır (KS-YDK-05).
5. **Durumlar:** Dışa aktarma başarılı olursa "Yedek kaydedildi" yazar ve "Tekrar dene" öne çıkar. Ham kopya da alınamazsa "Kopya alınamadı. Telefonda yer açıp tekrar dene." mesajı çıkar. Uygulama hiçbir durumda gri ekranda takılı kalmaz (NFR-REL-03).
6. **Mikro metinler:** "Verilerin açılamadı" · "Uygulamanın kayıt dosyası şu an okunamıyor. Verilerin büyük ihtimalle hâlâ telefonunda. Bir şeyi değiştirmeden önce bir kopyasını alalım." · "1 · Verilerini dışa aktar" / "Okunabilen her şey bir yedek dosyasına kaydedilir." · "2 · Tekrar dene" / "Telefonu yeniden başlattıysan ya da yer açtıysan" · "3 · Yedekten geri yükle" · "4 · Hata ayrıntısını kopyala" / "Yardım isteyeceksen; kişisel veri içermez" · "Verileri sil ve baştan başla" / "Son çare. Önce dışa aktarmayı dene; silme iki adımda onaylanır."
7. **İzlenebilirlik:** FR-YDK-05, NFR-REL-03, DATA-02, NFR-SEC-01 · KS-YDK-05.
8. **Riskler:**
   - **Ham kopya hassas veriyi şifresiz dışarı çıkarabilir.** Bu yüzden ham kopyaya hassas veritabanı dosyası girmez. Mazeret verisi yalnızca okunabilirse ve parolayla şifrelenerek dışa aktarılır (V23).
   - **Kurtarma ekranı kırmızı kullanmıyor.** Sakin ton kullanıcıyı "verileri sil"e koşturmamak için bilinçli bir seçim.

## 5. Pil kısıtı yönlendirme

1. **Amaç:** Üreticinin pil ayarında uygulamayı serbest bırakmak.
2. **Hiyerarşi:** Ne olduğu ve telefonun markası → kontrol edilebilen durum (pil optimizasyonu) ve edilemeyen durum (otomatik başlatma) → markaya özel 3 adım → "Uygulama ayarlarını aç".
3. **Taslak:** PilKisiti (Xiaomi).
4. **Etkileşimler:**
   - "Ayara git" Android'in pil optimizasyonu listesini açar. Uygulama bu izni doğrudan istemez (V22).
   - "Uygulama ayarlarını aç" uygulamanın sistem ayarlarına gider.
   - "Yaptım, kontrol et" durumu yeniden okur.
5. **Durumlar:**
   - **Markaya göre içerik:** adımlar Xiaomi, Samsung, Huawei, Oppo/Realme ve diğerleri için ayrı. Tablo Faz 6'da gerçek cihazlarda doğrulanacak.
   - **Optimizasyon kapalı:** satır "Kapalı · sorun yok" der.
   - **Ne zaman açılır:** Hatırlatmalar'dan açılır. Ayrıca bir hatırlatmanın 10 dakikadan fazla geciktiği fark edilirse bir kez, nötr bir kartla önerilir (V22, yeni).
6. **Mikro metinler:** "Hatırlatmaların zamanında gelmesi için" · "Bazı telefonlar pili korumak için arka plandaki uygulamaları durdurur. Bu da hatırlatmaların gecikmesine ya da hiç gelmemesine yol açabilir. Telefonun: Xiaomi." · "Pil optimizasyonu" / "Açık · hatırlatmalar gecikebilir" / "Ayara git" · "Otomatik başlatma" / "Uygulama bunu kontrol edemiyor; aşağıdaki adımlarla aç" · "Xiaomi'de adımlar" · "Uygulama ayarlarını aç" · "Yaptım, kontrol et" · "Şimdilik geç".
7. **İzlenebilirlik:** FR-HTR-03, NFR-TIM-01, NFR-SEC-04, yol haritası Faz 6 · KS-HTR-05.
8. **Riskler:**
   - **Menü adları güncellemelerle değişir.** Üreticilerin ayar menüleri sürümden sürüme farklılaşıyor. Adımlar bir veri tablosunda tutulmalı ve uygulama güncellemesiyle düzeltilebilmeli.
   - **Otomatik başlatma doğrulanamıyor.** Durum okunamadığı için "Yaptım" diyen kullanıcıya güvenmek gerekiyor.

## 6. Akış (b): widget'tan işaretle ve geri al

| Adım | Yer | Eylem | Dokunuş |
| --- | --- | --- | --- |
| 1 | Küçük widget | "Kıldım" | 1 |
| 2 | — | Widget 1 sn içinde "Kılındı 14:48" olur; 15:00 hatırlatması iptal edilir | 0 |
| 3 | Küçük widget (yanlış dokunduysan) | "Kılındı 14:48" | 1 |
| 4 | — | Widget yeniden "Kıldım" olur; kayıt "belirsiz"; hatırlatma yeniden kurulur | 0 |

İşaretleme 1 dokunuş, geri alma 1 dokunuş; uygulama açılmaz (NFR-USA-01, FR-WDG-01, FR-TRK-03, KT-18, KT-65). Diğer yollar:

- **Orta widget:** her hücre için aynı şekilde 1 dokunuş.
- **Bildirim:** "Kıldım" 1 dokunuş, ardından 5 saniyelik "Geri al".
- **Uygulama:** simge ve satır, 2 dokunuş.

## 7. Koyu tema eşlemesi

Görsel dil A'nın koyu paleti (Tur 1) bütün ekranlara aynı rollerle uygulanır.

| Rol | Açık | Koyu |
| --- | --- | --- |
| Zemin | #FBF7F1 | #1C1A18 |
| Yüzey (kart) | #FFFFFF | #262320 |
| Metin | #2B2621 | #F2ECE4 |
| İkincil metin | #6B6158 | #B8ADA2 |
| Vurgu (düğme, tik) | #A2541A | #E39A5C |
| Vurgu üstündeki metin | #FFFFFF | #1C1A18 |
| Şu anki vakit zemini | #FDF6E9 | #3A2E24 |
| Ayırıcı | #F0E8DD | #34302B |

Koyu temada vakit tonları yüzde 20 doygunlukla, yalnızca ikon dairelerinde kullanılır. Her metin–zemin çifti Faz 5'te otomatik kontrast testiyle doğrulanacak (en az 4.5:1).

## 8. Varsayımlar, değişiklik talepleri ve Tur 5'e notlar

**Açıkça yaptığım varsayımlar** (V1–V15'in devamı)

- **V16 — Widget geri sayımı.** Android'in kendi işleyen sayacıyla (Chronometer) gösterilir; saniye görünür ve kesin saat her zaman yanında yazar. Yol haritası Faz 6'daki yaklaşımla aynı.
- **V17 — Çift dokunuş koruması.** Widget'ta ve uygulamada aynı kaleme 1 saniye içinde gelen ikinci dokunuş yok sayılır.
- **V18 — Bildirimden işaret sonrası geri alma.** Bildirim 5 saniye boyunca "Öğle işaretlendi · Geri al" olarak kalır.
- **V19 — Yatsı ve vitir bildirimi.** İki eylem içerir: "Yatsıyı kıldım" ve "İkisini de kıldım".
- **V20 — Bildirim görünürlüğü.** Hatırlatmalar kilit ekranında tam görünür. Mazeretten doğan bildirimler gizli içerikle "Bilgilendirmeler" kanalına gider.
- **V21 — Konum değişikliği.** Değişiklik o günden itibaren geçerlidir; o günün vakti geçmiş kalemleri değişmez.
- **V22 — Pil izni.** `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` izni istenmez; Play politikası ve NFR-SEC-04 gereği yalnızca sistem listesi açılır. Gecikme fark edilirse yönlendirme bir kez önerilir.
- **V23 — Kurtarmada ham kopya.** Ham kopya yalnızca ana veritabanını içerir; hassas dosya şifresiz dışarı çıkmaz.
- **V24 — Hata ayrıntısı.** Yalnızca hata kodu, uygulama sürümü, şema sürümü ve cihaz modeli kopyalanır.
- **V25 — Parola alanları.** Mazeret kaydı yoksa dışa aktarmada parola sorulmaz.

**SRS ve kabul senaryoları için değişiklik talepleri**

- **DT-6 · Kerahat dakikaları ayarı** (karar sorusu 3): FR-AYR-01'deki "kerahat dakikaları" P1'e taşınır. Bu ayarı kullanan özellikler (FR-KAZ-06, FR-KAZ-07, FR-HTR-06) zaten P1.
- **DT-7 · KS-WDG-01'in metni:** "İkindiye 2 sa 30 dk" yerine "İkindi 15:30" ve işleyen geri sayım beklenir (V16). Dakika hassasiyeti (FR-VKT-04) uygulamada korunur.
- **DT-8 · İçe aktarma davranışı** (karar sorusu 2): FR-YDK-02'ye "içe aktarma mevcut veriyi değiştirir, öncesinde dışa aktarma önerilir" eklenir.

**Tur 5'te raporlanacak kabul senaryosu boşlukları**

- Widget'ta çift dokunuş koruması ve vakti geçmiş hücreye dokunuş.
- Bildirimden işaret sonrası "Geri al".
- Yatsı ve vitir bildiriminin iki eylemi.
- Dışa aktarmada parola kuralları (en az 8 karakter, eşleşme): KS-YDK-03 kısmen kapsıyor.
- İçe aktarmada bozuk dosya ve yeni sürüm dosyası.
- Kurtarma ekranının dışa aktarma ve ham kopya yolları.
- Pil kısıtı yönlendirmesi ve gecikme tespiti.
- Verileri sil (DATA-02): Tur 1'den beri açık.
- Koyu tema kontrast testi.

## 9. Karar soruları (Tur 4)

1. **Cuma günü widget'ta (erkek kullanıcı):**
   - (a) Önerim: Öğle hücresi "Cuma" yazar ve tek dokunuşla cuma olarak işaretler. Öğleyi kılan kullanıcı uygulamadan düzeltir. BR-08'e göre borç açısından fark yok, ve widget'ın tek dokunuş hedefi cuma günü de korunur.
   - (b) Öğle hücresi uygulamayı "Öğle / Cuma" seçimiyle açar. Kayıt her zaman doğru olur, ama widget o gün 2 dokunuştan fazla sürer.
2. **İçe aktarma, telefonda veri varken ne yapsın?**
   - (a) Önerim: Mevcut verinin yerine yedekteki geçer ve önce dışa aktarma önerilir. Basit ve öngörülebilir; yeni telefona taşıma senaryosuna uyar.
   - (b) İki veri birleştirilir. Aynı gün ve vakitte çakışan kayıtlar, kaza kayıtları ve ayarlar için kural gerekir. Daha karmaşık ve hataya açık.
3. **Kerahat dakikaları ayarı v1.0'da gizlensin mi?**
   - (a) Önerim: Gizlensin ve kerahat özellikleriyle birlikte v1.1'de görünsün (DT-6).
   - (b) FR-AYR-01'deki gibi şimdiden görünsün. Ama değiştirmek v1.0'da hiçbir şeyi etkilemez, kullanıcıyı şaşırtabilir.

### Kararlar (27 Ekim 2026)

Üç önerinin üçü de kabul edildi.

- [x] **1. Cuma günü widget:** (a) Erkek kullanıcıda öğle hücresi "Cuma" yazar ve tek dokunuşla cuma olarak işaretler; öğle kılındıysa uygulamadan düzeltilir.
- [x] **2. İçe aktarma:** (a) Mevcut verinin yerine yedekteki geçer; öncesinde dışa aktarma önerilir (DT-8).
- [x] **3. Kerahat dakikaları ayarı:** (a) v1.0'da gizli; kerahat özellikleriyle birlikte v1.1'de görünür (DT-6).

V16 – V25 varsayımları da kabul edildi. Sıradaki: Tur 5 — kabul senaryolarına karşı kontrol.

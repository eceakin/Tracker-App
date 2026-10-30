# Ekran Taslakları — Tur 5: Kabul Senaryolarına Karşı Kontrol

27 Ekim 2026 · @ece

## Özet

Tur 5, Tur 1–4'te çizilen ekranları kabul senaryolarına (kabul-senaryolari.md) ve P0 gereksinimlere (SRS 1.0-taslak.5) karşı tek tek kontrol eder. Kontrol panosu ve bu turda tamamlanan 3 durum, tasarım panosunun "Tur 5" sayfasında: [Ekran Taslakları](https://claude.ai/artifact/JT9ex8zdaWsNBymxW5UhVq).

- **71 senaryonun 49'u ekranda kapsanıyor.**
  - 14'ü ekran gerektirmeyen ya da cihazda ölçülen davranışlar: kalıcılık, önbellek, alarm, şema geçişi.
  - 7'sinin metni tasarımla uyuşmuyor ve güncellenmeli (Bölüm 3).
  - 1'i (KS-NFR-07, %200 yazı ve TalkBack) Faz 5'te ekran ekran test edilecek.
- **43 P0 gereksinimin tümü en az bir ekranla eşleşti** (Bölüm 2).
- **Bu turda 3 eksik durum çizildi:**
  - Cuma kılındı (KS-TRK-08)
  - Yatsı vaktinde biten mazeret (KS-MAZ-08)
  - Gusül süresi hatası (KS-AYR-03)
- **24 yeni senaryo önerisi.** Tasarımda eklenen ama henüz senaryosu olmayan davranışları kapsıyor (Bölüm 4). Toplam 95 senaryoya çıkılıyor.
- **SRS 1.0-taslak.6 için 13 değişiklik.** Hiçbiri yeni P0 gereksinim açmıyor ve hiçbir iş kuralını değiştirmiyor (Bölüm 5).
- Sonda 3 karar sorusu var (Bölüm 7). Cevaplar gelene kadar kabul senaryoları ve SRS belgelerine dokunulmadı. SRS Bölüm 9 "önce SRS, sonra test" diyor.

**Durum anahtarı:** *Ekranda kapsandı* = senaryodaki her adımın görsel karşılığı panoda var. *Davranış, testle* = senaryo ekran değil sistem davranışı sınıyor; doğrulaması birim, entegrasyon ya da cihaz testinde. *Kısmen* = ekranın bir kısmı eksik. *Senaryo güncellenmeli* = tasarım kararlarıyla senaryo metni artık uyuşmuyor.

## 1. 71 kabul senaryosu: kanıt matrisi

| Senaryo | Gereksinim | Kanıt (pano) | Durum | Not |
| --- | --- | --- | --- | --- |
| KS-ONB-01 | FR-ONB-01 | O2, O2b | Ekranda kapsandı | İl ve ilçe tek aramayla seçiliyor; ilçe seçilmeden "Devam" pasif ve nedeni yazıyor. |
| KS-ONB-02 | FR-ONB-02 | O1, Hakkında | Ekranda kapsandı | Hanefî bilgisi Hoş geldin ekranında, bir kez; Ayarlar › Hakkında'da tekrar okunur. |
| KS-ONB-03 | FR-ONB-03 | O3, K_Bos | Senaryo güncellenmeli | Düğmenin son metni "Sonra eklerim"; senaryoda "Sonra". |
| KS-ONB-04 | FR-ONB-03, FR-KAZ-01 | O3b, Kaza, KazaGecmisi | Senaryo güncellenmeli | Düğme "Şimdi gireyim". Kaza geçmişinde 6 kayıt tek "Başlangıç borcu" satırında toplanıyor, açılınca 6 kayıt görünüyor. |
| KS-ONB-05 | FR-ONB-04 | O4, A_Hatirlatmalar | Ekranda kapsandı | İzin bandı ve "Bildirimlere izin ver" düğmesi çizildi. |
| KS-ONB-06 | FR-ONB-04 | O4 | Davranış, testle | Sistem penceresinin çıkmaması platform davranışı. |
| KS-ONB-07 | FR-ONB-05 | B_Ilk | Ekranda kapsandı | Vakti geçmiş kalemler "işaretlenmedi", öneri kartı yok. |
| KS-TRK-01 | FR-TRK-01 | Bugun, B_Isaretlendi | Ekranda kapsandı | İşaretlenme saati satırın sağında. |
| KS-TRK-02 | FR-TRK-01 | — | Davranış, testle | Süre sınırı yok; ekranda deneme ya da üyelik öğesi bulunmuyor. |
| KS-TRK-03 | FR-TRK-02 | B_Kilitli, Bileşenler 2a | Ekranda kapsandı | "İkindi vakti henüz girmedi. 15:30'da açılır." |
| KS-TRK-04 | FR-TRK-03 | B_Isaretlendi, B_Kacirildi | Ekranda kapsandı | Tek dokunuşla geri alma; ek olarak "Geri al" çubuğu. |
| KS-TRK-05 | FR-TRK-04 | Takvim, G_Secim | Ekranda kapsandı | "Vaktinde kıldım / Sonradan kaza olarak kıldım" alt sayfası. |
| KS-TRK-06 | FR-TRK-04 | G_GunDetayi, G_Secim | Ekranda kapsandı | "kaza" etiketli kılındı satırı. |
| KS-TRK-07 | FR-TRK-05 | Bugun, G_GunDetayi, Kaza | Ekranda kapsandı | Üç ekranda da tarih başlıkta; geçmiş günde "Bugüne dön". |
| KS-TRK-08 | FR-TRK-06 | B_Cuma, B_CumaKilindi | Ekranda kapsandı | Seçim ve seçim sonrası "Cuma · Cuma kılındı" satırı (Tur 5'te tamamlandı). |
| KS-TRK-09 | FR-TRK-06 | B_Cuma, Bileşenler 1e | Ekranda kapsandı | Öneri "cuma" demiyor; kadında cuma uzun basma menüsünde. |
| KS-TRK-10 | FR-TRK-07 | Bileşenler 2b | Ekranda kapsandı | "Vitir yatsıdan sonra kılınır. Önce yatsıyı işaretle." |
| KS-TRK-11 | FR-TRK-01, FR-TRK-05 | B_GeceYarisi, Widgetlar | Ekranda kapsandı | Bugün ve orta widget önceki namaz gününü gösteriyor. |
| KS-KAZ-01 | FR-KAZ-01 | BorcDuzenle, KazaGecmisi | Ekranda kapsandı | "7 → 9 · +2 düzeltme" ve geçmişte düzeltme kaydı. |
| KS-KAZ-02 | FR-KAZ-02 | Kaza, K_Kaydedildi | Ekranda kapsandı | Satırdaki "Kıldım" (Tur 3 kararı 2). |
| KS-KAZ-03 | FR-KAZ-02 | Kaza | Ekranda kapsandı | Vakitsiz kaza eylemi yok (yeniden yazıldı). |
| KS-KAZ-04 | FR-KAZ-03 | B_Kacirildi, Bileşenler 1a | Ekranda kapsandı | Öneri kartı ve iki düğme. |
| KS-KAZ-05 | FR-KAZ-03 | Oneriler, Bileşenler 1b | Senaryo güncellenmeli | Bugün'de tek kart ve "+6 öneri"; 7 öneri Bekleyen öneriler ekranında. Senaryo "alanında 7 kart" diyor. |
| KS-KAZ-06 | FR-KAZ-03 | Bileşenler 1f, KazaGecmisi | Ekranda kapsandı | Kart yok; "otomatik olarak borca eklendi · Geri al" çubuğu ve geçmişte "otomatik" kaydı. |
| KS-KAZ-07 | FR-KAZ-04 | K_BorcYok | Ekranda kapsandı | Diyalog metni senaryoyla aynı. |
| KS-KAZ-08 | FR-KAZ-05 | KazaGecmisi | Ekranda kapsandı | Kayıt silinmiyor, düzeltme kaydı ve "Geri alındı" etiketi. |
| KS-MAZ-01 | FR-MAZ-01 | Bugün ⋮, Ayarlar | Ekranda kapsandı | Erkekte Ayarlar'da Mazeret bölümü ve menü öğesi yok. |
| KS-MAZ-02 | FR-MAZ-01 | Mazeret | Ekranda kapsandı | Tür seçilmeden "Başlat" pasif; saat 16:00 dolu. |
| KS-MAZ-03 | FR-MAZ-02, FR-HTR-04 | B_Mazeret, Widgetlar | Senaryo güncellenmeli | "Seri göstergesi azalmaz" adımı P1'e ait (FR-TRK-08); v1.0'da seri göstergesi yok. |
| KS-MAZ-04 | FR-MAZ-03 | Mazeret, Bugun | Davranış, testle | Kural motoru sonucu; ekranda öğle kartı yerinde kalıyor, ikindi kartı oluşmuyor. |
| KS-MAZ-05 | FR-MAZ-04 | M_SonucFarz, Bildirimler f | Ekranda kapsandı | Sonuç ekranı ve nötr bildirim. |
| KS-MAZ-06 | FR-MAZ-04 | Bileşenler 1c | Ekranda kapsandı | Otomatik ekleme açıkken de kart. |
| KS-MAZ-07 | FR-MAZ-04 | M_SonucDegil | Ekranda kapsandı | "İkindi için kaza gerekmiyor". |
| KS-MAZ-08 | FR-MAZ-04 | M_SonucYatsi, Bileşenler 1d | Ekranda kapsandı | Yatsı ve vitir birlikte açılır; ayrı iki öneri (Tur 5'te tamamlandı). |
| KS-MAZ-09 | FR-MAZ-05 | TopluOnay | Ekranda kapsandı | Düzeltilmiş veriyle (G4 07:59, 18 kalem). |
| KS-MAZ-10 | FR-MAZ-05 | T_Uzun, Bildirimler g | Ekranda kapsandı | 24 kalem ve nötr bildirim. |
| KS-MAZ-11 | FR-MAZ-05 | M_Temizlik, MazeretGecmisi | Ekranda kapsandı | Uyarı diyaloğu ve geçmişte etiket. |
| KS-MAZ-12 | FR-MAZ-06 | MazeretGecmisi, Bileşenler 3b | Ekranda kapsandı | Alışkanlık alanı ve güncelleme diyaloğu. |
| KS-VKT-01 | FR-VKT-01 | — | Davranış, testle | Kalıcılık testi; ekran gerektirmez. |
| KS-VKT-02 | FR-VKT-02 | Ayarlar (Vakitler satırı) | Davranış, testle | Önbellek durumu Ayarlar'da görünür. |
| KS-VKT-03 | FR-VKT-02 | — | Davranış, testle | Kullanıcı hiçbir ekran görmez. |
| KS-VKT-04 | FR-VKT-03 | B_Tahmini, Widgetlar, A_Hatirlatmalar | Ekranda kapsandı | Bugün, widget ve hatırlatma saatlerinde "tahmini" / "~". |
| KS-VKT-05 | FR-VKT-04 | Bugun | Ekranda kapsandı | "İkindiye 43 dk" çipi. |
| KS-WDG-01 | FR-WDG-01 | Widgetlar, W_AnaEkran | Senaryo güncellenmeli | Geri sayım "İkindi 15:30 · 0:43:12 kaldı" biçiminde (DT-7). |
| KS-WDG-02 | FR-WDG-02, FR-WDG-03 | Widgetlar | Ekranda kapsandı | Kilitli hücre ve uygulamanın açıklamayla açılması. |
| KS-WDG-03 | FR-WDG-03 | Widgetlar | Ekranda kapsandı | Yatsıdan sonra vitir açık. |
| KS-WDG-04 | FR-WDG-03 | Akis_Widget | Davranış, testle | Senkron ve 1 sn ölçümü cihazda. |
| KS-WDG-05 | FR-WDG-04 | Widgetlar | Ekranda kapsandı | Mazeret görünümü vakit arasıyla aynı. |
| KS-HTR-01 | FR-HTR-01 | Bildirimler a | Ekranda kapsandı | Metin senaryoyla aynı. |
| KS-HTR-02 | FR-HTR-01 | A_Hatirlatmalar | Ekranda kapsandı | Vakit başına süre ve anahtar. |
| KS-HTR-03 | FR-HTR-01 | Bildirimler c | Ekranda kapsandı | Tek bildirim, iki eylem. |
| KS-HTR-04 | FR-HTR-02 | Bildirimler a, b | Senaryo güncellenmeli | Bildirim hemen kapanmıyor; 5 sn "Geri al" hâlinde kalıp kapanıyor (V18). |
| KS-HTR-05 | FR-HTR-03 | A_KesinAlarm, PilKisiti | Davranış, testle | Zamanlama cihazda ölçülür; destek ekranları çizildi. |
| KS-HTR-06 | FR-HTR-04 | — | Davranış, testle | Kural ve alarm davranışı. |
| KS-HTR-07 | FR-HTR-05 | Bildirimler h | Ekranda kapsandı | Gönderilmeyecek metinler listesi. |
| KS-YDK-01 | FR-YDK-01 | Ayarlar, A_DisaAktar | Ekranda kapsandı | Ayarlar › Yedekleme › Dışa aktar. |
| KS-YDK-02 | FR-YDK-02 | A_IceAktar | Davranış, testle | Şema dönüşümü testle doğrulanır. |
| KS-YDK-03 | FR-YDK-03 | A_DisaAktar, A_IceAktar | Ekranda kapsandı | Parola tekrarı ve yanlış parola mesajı senaryoyla aynı. |
| KS-YDK-04 | FR-YDK-04 | Ayarlar (Telefon yedeği) | Davranış, testle | Geri yükleme cihazda denenir. |
| KS-YDK-05 | FR-YDK-05 | Kurtarma, A_Diyaloglar | Ekranda kapsandı | İlk seçenek dışa aktar, son seçenek sil; silme iki adımlı. |
| KS-AYR-01 | FR-AYR-01, FR-ONB-01 | A_Diyaloglar b | Ekranda kapsandı | Onay metni etkiyi anlatıyor. |
| KS-AYR-02 | FR-AYR-01 | A_Diyaloglar a | Ekranda kapsandı | "Geçmiş günlerin kayıtları ve önerileri değişmez." |
| KS-AYR-03 | FR-AYR-01 | A_Gusul | Ekranda kapsandı | "5 – 60 dk arasında bir değer gir." ve pasif Kaydet (Tur 5'te tamamlandı). |
| KS-AYR-04 | FR-AYR-02 | — | Davranış, testle | Kural motoru (BR-21). |
| KS-NFR-01 | NFR-SEC-03 | Bildirimler e, f | Ekranda kapsandı | Kilit ekranı ve kilit açık metinleri. |
| KS-NFR-02 | NFR-SEC-03 | Mazeret ekranları | Senaryo güncellenmeli | Önizleme gizleme mazeret sürerken Bugün'ü de kapsıyor (V13); senaryo yalnızca mazeret ekranını sınıyor. |
| KS-NFR-03 | NFR-OFF-01 | — | Davranış, testle | Uçak modu testi. |
| KS-NFR-04 | NFR-REL-02 | Bileşenler 2f | Davranış, testle | Hata mesajı çizildi; tutarlılık testle. |
| KS-NFR-05 | NFR-SEC-04, NFR-SEC-01 | O2, A_DisaAktar | Ekranda kapsandı | GPS yok; dosya sistem penceresiyle kaydediliyor. |
| KS-NFR-06 | NFR-USA-01 | Akis_IlkIsaret, Akis_Widget | Ekranda kapsandı | Widget 1, uygulama 2 dokunuş. |
| KS-NFR-07 | NFR-USA-02 | Tüm ekranlar | Kısmen | TalkBack etiketleri taslaklarda var; %200 yazı yalnızca orta widget ve Bekleyen öneriler için düşünüldü. Faz 5'te ekran ekran test. |

**Kontrol yöntemi.** Her senaryonun "O zaman" adımları panodaki ekran ve metinlerle karşılaştırıldı. Metinler kelimesi kelimesine aynıysa ya da tasarım aynı bilgiyi daha açık veriyorsa "kapsandı" sayıldı. Metin farklı ve anlamı değişiyorsa "güncellenmeli" sayıldı. Sayımlar elle değil, matrisin kendisinden hesaplandı.

## 2. 43 P0 gereksinim: ekran eşlemesi

Ekranı olmayan P0 gereksinim yok. FR-MAZ-03 ve FR-AYR-02 yalnızca kural motoruyla ilgili. Ekranda dolaylı görünüyorlar: öneri kartının oluşmaması ve onay metnindeki "geçmiş değişmez" cümlesi.

| Gereksinim | Ekran(lar) |
| --- | --- |
| FR-ONB-01 | O2, O2b, A_Diyaloglar |
| FR-ONB-02 | O1, A_Hakkinda |
| FR-ONB-03 | O3, O3b, K_Bos |
| FR-ONB-04 | O4, A_Hatirlatmalar |
| FR-ONB-05 | B_Ilk |
| FR-TRK-01 | Bugun, B_Isaretlendi |
| FR-TRK-02 | B_Kilitli, Bileşenler 2a, Widgetlar |
| FR-TRK-03 | B_Isaretlendi, Akis_Widget, Bildirimler b |
| FR-TRK-04 | Takvim, G_GunDetayi, G_Secim |
| FR-TRK-05 | Bugun, G_GunDetayi, Kaza |
| FR-TRK-06 | B_Cuma, Widgetlar |
| FR-TRK-07 | Bileşenler 2b, Widgetlar |
| FR-KAZ-01 | O3b, BorcDuzenle |
| FR-KAZ-02 | Kaza, K_Kaydedildi |
| FR-KAZ-03 | B_Kacirildi, Oneriler, Bileşenler 1 |
| FR-KAZ-04 | K_BorcYok |
| FR-KAZ-05 | KazaGecmisi |
| FR-MAZ-01 | Mazeret, M_Aktif, M_Bitir |
| FR-MAZ-02 | B_Mazeret, Widgetlar |
| FR-MAZ-03 | Mazeret (kural) |
| FR-MAZ-04 | M_SonucFarz, M_SonucDegil, Bildirimler f |
| FR-MAZ-05 | TopluOnay, T_Uzun, M_Temizlik |
| FR-MAZ-06 | MazeretGecmisi, Bileşenler 3b |
| FR-VKT-01 | O2b, Ayarlar, A_Diyaloglar a |
| FR-VKT-02 | Ayarlar (Vakitler) |
| FR-VKT-03 | B_Tahmini, Widgetlar, A_Hakkinda |
| FR-VKT-04 | Bugun, Widgetlar |
| FR-WDG-01 | Widgetlar, W_AnaEkran, O5 |
| FR-WDG-02 | Widgetlar |
| FR-WDG-03 | Akis_Widget |
| FR-WDG-04 | Widgetlar |
| FR-HTR-01 | Bildirimler, A_Hatirlatmalar |
| FR-HTR-02 | Bildirimler a, b, c |
| FR-HTR-03 | A_KesinAlarm, PilKisiti |
| FR-HTR-04 | B_Mazeret, A_Hatirlatmalar |
| FR-HTR-05 | Bildirimler h |
| FR-YDK-01 | A_DisaAktar |
| FR-YDK-02 | A_IceAktar |
| FR-YDK-03 | A_DisaAktar, A_IceAktar |
| FR-YDK-04 | Ayarlar (Telefon yedeği), MazeretGecmisi |
| FR-YDK-05 | Kurtarma, A_Diyaloglar |
| FR-AYR-01 | Ayarlar, A_Diyaloglar |
| FR-AYR-02 | A_Diyaloglar (metin) |

## 3. Güncellenmesi gereken 7 senaryo

Önerilen yeni metinler aşağıda. Değişmeyen satırlar aynen korunuyor. Onaylanırsa kabul-senaryolari.md'deki karşılıklarının yerine geçecek (karar sorusu 1).

```gherkin
# language: tr

  @FR-ONB-03 @otomatik
  Senaryo: KS-ONB-03 Kaza adımı "Sonra eklerim" ile atlanır
    Diyelim ki kullanıcı kaza borcu adımında
    Eğer ki "Sonra eklerim" seçilir ve onboarding biter
    O zaman kaza ekranında 6 kalemin borcu 0 görünür ve "Henüz kaza borcu girmedin" kartı bulunur
    Ve sonraki 7 gün boyunca kaza girişini hatırlatan hiçbir bildirim gelmez

  @FR-ONB-03 @FR-KAZ-01 @otomatik
  Senaryo: KS-ONB-04 Kaza adımında borç girilir
    Diyelim ki kullanıcı kaza borcu adımında
    Eğer ki "Şimdi gireyim" seçilir ve sabah 10, öğle 8, ikindi 8, akşam 5, yatsı 9, vitir 9 girilir
    Ve "Kaydet ve devam et" seçilir
    O zaman kaza ekranında bu 6 sayı aynen görünür
    Ve kaza geçmişinde "Başlangıç borcu" satırı +49 olarak görünür
    Ve satır açıldığında 6 kayıt "elle" türüyle listelenir

  @FR-KAZ-03 @otomatik
  Senaryo: KS-KAZ-05 Otomatik ekleme kapalıyken hiçbir borç sessizce yazılmaz
    Diyelim ki G1'de hiçbir kalem işaretlenmedi ve hiçbir öneri yanıtlanmadı
    Eğer ki saat G2 07:00 olur
    O zaman Bugün ekranında en eski öneri kartı ve "+6 öneri" bağlantısı görünür
    Ve Bekleyen öneriler ekranında 7 öneri bulunur (G1'in 6 kalemi ve G2 sabahı)
    Ve tüm kalemlerde kaza borcu 0'dır

  @FR-MAZ-02 @FR-HTR-04 @otomatik @KT-40 @KT-67
  Senaryo: KS-MAZ-03 Mazeret sürerken vakitler nötr görünür
    Diyelim ki kullanıcı K ve hayız G1 16:00'dan beri sürüyor
    Eğer ki G2 boyunca vakitler girip çıkar
    O zaman G2'nin 6 kalemi nötr renkte görünür ve işaretlenmez
    Ve hiçbir öneri kartı oluşmaz, hiçbir hatırlatma gelmez
    # Seri göstergesi adımı FR-TRK-08 (P1) ile birlikte v1.1'de eklenir

  @FR-WDG-01 @elle
  Senaryo: KS-WDG-01 Küçük widget o anki vakti tek dokunuşla işaretler
    Diyelim ki küçük widget ana ekranda ve saat G1 13:00
    O zaman widget "Öğle", "İkindi 15:30", işleyen geri sayım ve "Kıldım" düğmesini gösterir
    Eğer ki "Kıldım"a dokunulur
    O zaman uygulama açılmaz ve düğme 1 saniye içinde "Kılındı 13:00" durumuna geçer

  @FR-HTR-02 @elle
  Senaryo: KS-HTR-04 Bildirimden işaretleme ve geri alma
    Diyelim ki öğle hatırlatması bildirim alanında
    Eğer ki bildirimdeki "Kıldım" eylemine dokunulur
    O zaman uygulama açılmaz ve öğle "kılındı" olur
    Ve bildirim "Öğle işaretlendi · Geri al" hâline geçer ve 5 saniye içinde kapanır
    Eğer ki bu 5 saniye içinde "Geri al"a dokunulur
    O zaman öğle boş görünür ve 15:00 hatırlatması yeniden kurulur

  @NFR-SEC-03 @elle
  Senaryo: KS-NFR-02 Son uygulamalar önizlemesinde ve ekran görüntüsünde mazeret görünmez
    Diyelim ki kullanıcı K ve hayız sürüyor
    Eğer ki mazeret ekranı ya da Bugün ekranı açıkken son uygulamalar listesi açılır
    O zaman uygulamanın önizlemesi boş görünür
    Ve bu iki ekranda ekran görüntüsü alınamaz
```

## 4. Önerilen 24 yeni senaryo

Tur 1–4'te karara bağlanan ve senaryosu olmayan her davranış için bir senaryo yazıldı. Ortak ön koşullar ("Geçmiş" bloğu) aynı.

```gherkin
# language: tr
Özellik: Onboarding (ek)

  @FR-WDG-01 @elle
  Senaryo: KS-ONB-08 Widget ekleme adımı atlanabilir
    Diyelim ki kullanıcı onboarding'in son adımında
    Eğer ki "Şimdi değil" seçilir
    O zaman Bugün ekranı açılır ve ana ekrana widget eklenmez
    Eğer ki onboarding yeniden kurulumla tekrarlanır ve "Ana ekrana ekle" seçilir
    O zaman sistemin widget ekleme penceresi açılır

  @FR-ONB-01 @FR-VKT-03 @otomatik
  Senaryo: KS-ONB-09 İnternetsiz ilk kurulumda ilçe seçilir
    Diyelim ki uygulama ilk kez açıldı ve cihaz uçak modunda
    Eğer ki ilçe aramasına "bor" yazılır
    O zaman sonuçlar internet olmadan listelenir
    Eğer ki onboarding tamamlanır
    O zaman Bugün ekranında vakitler "tahmini" etiketiyle görünür
    Eğer ki internet geri gelir
    O zaman vakitler Diyanet'ten indirilir ve etiket kaybolur

Özellik: Günlük takip (ek)

  @FR-TRK-03 @otomatik
  Senaryo: KS-TRK-12 İşaretten sonra geri alma çubuğu çıkar
    Diyelim ki saat G1 14:48 ve öğle boş
    Eğer ki Bugün ekranında öğleye dokunulur
    O zaman "Öğle işaretlendi · Geri al" çubuğu 4 saniye görünür
    Eğer ki "Geri al"a dokunulur
    O zaman öğle boş görünür

  @FR-TRK-04 @FR-ONB-05 @otomatik
  Senaryo: KS-TRK-13 Kurulumdan önceki bir gün işaretlenebilir, öneri üretilmez
    Diyelim ki onboarding G5'te tamamlandı
    Eğer ki takvimden G3 açılır
    O zaman G3'ün kalemleri boş ve hiçbiri için öneri yok
    Eğer ki G3 öğlesine dokunulur ve "Vaktinde kıldım" seçilir
    O zaman G3 öğlesi "kılındı" olur ve kaza borcu değişmez

  @FR-TRK-03 @otomatik
  Senaryo: KS-TRK-14 Çift dokunuş işareti geri almaz
    Diyelim ki saat G1 14:48 ve öğle boş
    Eğer ki öğleye 1 saniye içinde iki kez dokunulur
    O zaman öğle "kılındı" olarak kalır
    Eğer ki 2 saniye sonra öğleye yeniden dokunulur
    O zaman öğle boş görünür

Özellik: Kaza (ek)

  @FR-KAZ-03 @BR-00 @otomatik
  Senaryo: KS-KAZ-09 Bekleyen öneriler topluca borca eklenir
    Diyelim ki KS-KAZ-05'teki 7 öneri bekliyor
    Eğer ki Bekleyen öneriler ekranında "Hepsini borca ekle (7)" seçilir
    O zaman "7 vakit borca eklensin mi?" onayı görünür
    Eğer ki "Vazgeç" seçilir
    O zaman hiçbir borç yazılmaz
    Eğer ki yeniden seçilir ve "7 vakti ekle" onaylanır
    O zaman kaza borcu toplam 7 artar ve kaza geçmişinde 7 "öneri onaylandı" kaydı görünür

  @FR-TRK-04 @FR-KAZ-05 @otomatik
  Senaryo: KS-KAZ-10 Öneri bekleyen vakit "kaza olarak kıldım" ile tek işlemde kapanır
    Diyelim ki G1 öğlesinin önerisi bekliyor ve öğle borcu 0
    Eğer ki G2'de takvimden G1 açılır, öğleye dokunulur ve "Sonradan kaza olarak kıldım" seçilir
    O zaman G1 öğlesi "kılındı (kaza)" olur, öneri kapanır ve öğle borcu 0 kalır
    Ve kaza geçmişinde "kaza olarak kılındı · 2 kayıt birlikte" satırı görünür
    Eğer ki bu satırda "Geri al" seçilir
    O zaman iki kayıt birlikte geri alınır ve G1 öğlesi için öneri yeniden bekler

Özellik: Mazeret (ek)

  @FR-MAZ-02 @otomatik
  Senaryo: KS-MAZ-13 Mazeretli kaleme dokunuş açıklama gösterir
    Diyelim ki kullanıcı K ve hayız sürüyor
    Eğer ki Bugün ekranında öğleye dokunulur
    O zaman işaret konmaz ve "Bu vakit mazeret dönemine denk geliyor; işaretlemen gerekmiyor." görünür

  @FR-MAZ-05 @BR-00 @otomatik @KT-52
  Senaryo: KS-MAZ-14 Toplu onayda bazı kalemler bırakılabilir
    Diyelim ki KS-MAZ-09'daki 18 kalemlik onay ekranı açık
    Eğer ki G1 yatsısının işareti kaldırılır
    O zaman ana düğme "17 vakti borca ekle" olur
    Eğer ki düğmeye dokunulur
    O zaman 17 kalem borca eklenir ve G1 yatsısı "işaretlenmedi" kalır

  @FR-MAZ-05 @otomatik @KT-55
  Senaryo: KS-MAZ-15 240. saatte dönem kendiliğinden kapanır
    Diyelim ki kullanıcı K, alışkanlık 6 gün ve hayız G1 08:00'den beri sürüyor
    Eğer ki saat G11 08:00 olur
    O zaman dönem G11 08:00 bitişiyle kaydedilir ve mazeret ekranında devam eden dönem görünmez
    Ve Bugün ekranında mazeret kartı kalkar
    Ve kullanıcıdan ayrıca bitiş kaydı istenmez

  @FR-MAZ-01 @otomatik @KT-37
  Senaryo: KS-MAZ-16 Dönem başlangıcı düzeltilir, öneriler güncellenir
    Diyelim ki kullanıcı K, G1 ikindisi boş ve hayız G1 16:00'da başlatıldı
    Eğer ki başlangıç "Başlangıcı düzelt" ile G1 18:30'a çekilir
    O zaman G1 ikindisi için öneri kartı oluşur ve G1 akşamı mazeretli olur
    Ve önceden onaylanmış hiçbir borç kaydı değişmez

Özellik: Widget (ek)

  @FR-WDG-02 @FR-TRK-06 @elle @KT-15
  Senaryo: KS-WDG-06 Cuma günü erkek kullanıcıda widget cuma olarak işaretler
    Diyelim ki kullanıcı E, bugün cuma, orta widget ana ekranda ve saat 13:00
    O zaman öğle hücresinde "Cuma" yazar
    Eğer ki hücreye dokunulur
    O zaman uygulama açılmaz ve kayıt "Cuma kılındı" olur

  @FR-WDG-02 @FR-TRK-04 @elle
  Senaryo: KS-WDG-07 Vakti geçmiş hücre uygulamayı seçimle açar
    Diyelim ki orta widget ana ekranda, saat 16:00 ve öğle boş
    Eğer ki öğle hücresine dokunulur
    O zaman işaret konmaz ve uygulama "Vaktinde kıldım / Sonradan kaza olarak kıldım" seçimiyle açılır

Özellik: Hatırlatma (ek)

  @FR-HTR-02 @BR-03 @elle @KT-66
  Senaryo: KS-HTR-08 Yatsı ve vitir bildiriminin iki eylemi
    Diyelim ki G1 yatsısı ve vitri boş ve saat G2 05:00
    O zaman bildirimde "Yatsıyı kıldım" ve "İkisini de kıldım" eylemleri bulunur
    Eğer ki "Yatsıyı kıldım" seçilir
    O zaman yatsı "kılındı", vitir boş kalır
    Ve 05:30'da yalnızca vitir için öneri oluşur

  @FR-HTR-03 @elle
  Senaryo: KS-HTR-09 Gecikme fark edilince pil yönlendirmesi bir kez önerilir
    Diyelim ki öğle hatırlatması 15:00'e kurulu
    Eğer ki bildirim 15:12'de gelir
    O zaman uygulama bir sonraki açılışta "Hatırlatmalar geç mi geliyor?" kartını bir kez gösterir
    Eğer ki kart kapatılır ve bir gecikme daha olur
    O zaman kart yeniden gösterilmez

Özellik: Yedekleme (ek)

  @FR-YDK-03 @otomatik
  Senaryo: KS-YDK-06 Parola kuralları
    Diyelim ki kullanıcı K ve mazeret kaydı var
    Eğer ki dışa aktarmada 7 karakterli bir parola girilir
    O zaman "En az 8 karakter." uyarısı görünür ve "Dosyayı kaydet" pasif kalır
    Eğer ki iki alana farklı parolalar girilir
    O zaman "İki parola aynı değil." görünür ve "Dosyayı kaydet" pasif kalır

  @FR-YDK-02 @otomatik
  Senaryo: KS-YDK-07 İçe aktarma mevcut veriyi değiştirir
    Diyelim ki telefonda 10 günlük kayıt var
    Eğer ki 15 günlük bir yedek dosyası seçilir
    O zaman "Bu telefondaki veriler yedektekilerle değiştirilir." uyarısı ve "Önce mevcut verimi dışa aktar" bağlantısı görünür
    Eğer ki içe aktarma onaylanır
    O zaman telefonda yalnızca yedekteki 15 günlük kayıt bulunur

  @FR-YDK-02 @NFR-REL-02 @otomatik
  Senaryo: KS-YDK-08 Bozuk ya da daha yeni sürüm dosyası reddedilir
    Diyelim ki telefonda 10 günlük kayıt var
    Eğer ki bozuk bir dosya seçilir
    O zaman "Bu dosya bir Namaz Takibi yedeği değil ya da bozuk." görünür
    Eğer ki daha yeni bir şema sürümüyle alınmış bir dosya seçilir
    O zaman "Bu yedek daha yeni bir sürümle alınmış. Uygulamayı güncelle." görünür
    Ve iki durumda da telefondaki 10 günlük kayıt değişmez

  @FR-YDK-05 @NFR-SEC-02 @otomatik
  Senaryo: KS-YDK-09 Kurtarmada ham kopya hassas veriyi içermez
    Diyelim ki kullanıcı K, mazeret kaydı var ve ana veritabanı bozuk
    Eğer ki kurtarma ekranında dışa aktarma başarısız olur ve ham kopya kaydedilir
    O zaman ham kopya yalnızca ana veritabanı dosyasını içerir
    Ve mazeret veritabanı dosyası kopyaya girmez

  @DATA-02 @otomatik
  Senaryo: KS-YDK-10 Tüm verileri silme iki adımlıdır
    Diyelim ki Ayarlar açık
    Eğer ki "Tüm verileri sil" seçilir
    O zaman "Önce bir yedek al" adımı "Dışa aktar", "Yedeksiz devam et" ve "Vazgeç" seçenekleriyle görünür
    Eğer ki "Yedeksiz devam et" seçilir
    O zaman "Tüm veriler silinsin mi?" onayı görünür ve odak "Vazgeç"tedir
    Eğer ki "Tümünü sil" seçilir
    O zaman tüm veri silinir ve uygulama onboarding'e döner

Özellik: Ayarlar (ek)

  @FR-AYR-01 @otomatik
  Senaryo: KS-AYR-05 Kerahat dakikaları v1.0'da ayarlarda yoktur
    Eğer ki Ayarlar'ın tüm bölümleri gezilir
    O zaman kerahat dakikalarını değiştiren bir alan bulunmaz

  @FR-KAZ-03 @BR-00 @otomatik
  Senaryo: KS-AYR-06 Otomatik borç ekleme açılınca bilgi notu bir kez gösterilir
    Eğer ki Ayarlar'da "Otomatik borç ekleme" ilk kez açılır
    O zaman "Belirsiz durumlar yine sorulur" bilgisi görünür
    Eğer ki ayar kapatılıp yeniden açılır
    O zaman bilgi yeniden gösterilmez

Özellik: Gizlilik ve erişilebilirlik (ek)

  @NFR-USA-02 @otomatik
  Senaryo: KS-NFR-08 Her metin yeterli kontrastta
    Eğer ki açık ve koyu temada tüm ekranların ekran görüntüsü alınır ve kontrast denetimi koşar
    O zaman her metin–zemin çifti en az 4.5:1'dir

  @NFR-SEC-03 @IF-03 @elle
  Senaryo: KS-NFR-09 Bildirim kanalları ve kilit ekranı görünürlüğü
    Diyelim ki kullanıcı K
    Eğer ki sistem bildirim ayarları açılır
    O zaman kanallar "Sabah", "Öğle", "İkindi", "Akşam", "Yatsı ve vitir" ve "Bilgilendirmeler"dir
    Ve hiçbir kanalın adında mazeret ya da âdet kelimesi geçmez
    Eğer ki cihaz kilitliyken bir hatırlatma ve bir bilgilendirme gelir
    O zaman hatırlatma tam görünür, bilgilendirme yalnızca "Namaz takibi: yeni bir bilgi var" yazar
```

## 5. SRS 1.0-taslak.6 için değişiklik listesi

SRS Bölüm 9'daki sırayla hazırlandı: talep, sınıf, etki. Onaylanırsa SRS'e uygulanacak ve sürüm tablosuna "1.0-taslak.6" satırı eklenecek (karar sorusu 2).

| No | Değişiklik | Sınıf | Etkilenen | Kaynak karar |
| --- | --- | --- | --- | --- |
| DT-1 | İl/ilçe listesi ve koordinatları uygulamaya gömülü; ilk kurulum internetsiz yapılır, vakitler internet gelene kadar tahmini. Ön koşul: Diyanet'in yer listesinin pakete konmasına izin vermesi | FR, dış arayüz | A-02, IF-01, FR-ONB-01, FR-VKT-03 | Tur 1, karar 3 |
| DT-4 | Mazeret döneminin başlangıç ve bitişi sonradan düzeltilebilir; düzeltme bir veri düzeltmesidir, BR-21'in kapsamına girmez; onaylanmış borçlar değişmez | FR | FR-MAZ-01 | Tur 3, V14 |
| DT-5 | Hayız 240., nifas 960. saatte dönem kendiliğinden kapanır; kullanıcıdan ayrıca bitiş istenmez | FR | FR-MAZ-05 | Tur 3, V11 |
| DT-6 | "Kerahat dakikaları" ayarı P1'e taşınır; kullanan özellikler (FR-KAZ-06, 07, FR-HTR-06) zaten P1 | Kapsam (P0 → P1) | FR-AYR-01 | Tur 4, karar 3 |
| DT-7 | Widget'ta kalan süre, kesin saat ve sistemin işleyen sayacıyla gösterilir | FR | FR-WDG-01 | Tur 4, V16 |
| DT-8 | İçe aktarma mevcut verinin yerine geçer; öncesinde dışa aktarma önerilir; yarıda kalırsa veri değişmez | FR | FR-YDK-02 | Tur 4, karar 2 |
| DT-9 | Öneri bekleyen geçmiş vakit "kaza olarak kıldım" ile işaretlenirse borç ekleme ve düşme tek işlemde yazılır ve birlikte geri alınır | FR | FR-TRK-04, FR-KAZ-05 | Tur 2, karar 1 |
| DT-10 | Kaza, ilgili vaktin satırından kaydedilir; Bekleyen öneriler ekranı ve onaylı "Hepsini borca ekle" | FR | FR-KAZ-02, FR-KAZ-03 | Tur 3, karar 2 ve 3 |
| DT-11 | Widget'ta erkek kullanıcının cuma günü öğle hücresi cuma olarak işaretler; vakti geçmiş hücre uygulamayı seçimle açar; 1 sn içindeki ikinci dokunuş yok sayılır | FR | FR-WDG-02, FR-TRK-03, FR-TRK-06 | Tur 4, karar 1, V17 |
| DT-12 | Bildirimden işaret sonrası 5 sn "Geri al"; yatsı ve vitir bildiriminde iki eylem | FR | FR-HTR-02 | Tur 4, V18, V19 |
| DT-13 | Mazeret ekranları ve mazeret sürerken Bugün ekranı son uygulamalar önizlemesinde boş görünür ve ekran görüntüsü alınamaz | NFR | NFR-SEC-03 | Tur 3, V13 |
| DT-14 | Kurtarmada ham kopya hassas dosyayı içermez; hata ayrıntısı kişisel veri içermez. Pil kısıtı yönlendirmesi FR-HTR-03'ün kabul kriterine eklenir | FR | FR-YDK-05, FR-HTR-03 | Tur 4, V22 – V24 |
| DT-15 | Onboarding'in son adımı atlanabilir widget ekleme önerisidir | FR | FR-WDG-01 | Tur 1, envanter |

**Bölüm 9 uyumu.**

- Hiçbir değişiklik yeni bir P0 gereksinim açmıyor; hepsi mevcut gereksinimlerin kabul kriterlerine ekleniyor. Tek kapsam değişikliği DT-6 ve o da kapsamı daraltıyor.
- Hiçbir BR satırı değişmediği için kural değişikliği hoca kontrolü gerekmiyor.
- Ekran metinlerindeki fıkhi ifadeler yine de hoca kontrolüne gitmeli (Bölüm 6).
- DT-2 (kaza satırdan) ve DT-3 (KT-52 düzeltmesi) yalnızca test belgelerini etkilediği için Tur 3 sonunda kapandı.

## 6. Faz 5'e devredilenler ve hoca kontrol listesi

**Ekran tasarımında kalan ve Faz 5'te kapanacak işler**

- %200 yazı boyutu ve TalkBack ile her ekranın ve iki widget'ın denenmesi (KS-NFR-07).
- Koyu tema kontrast denetimi; KS-NFR-08 önerisi bunu otomatik testle sabitliyor.
- Widget geri sayımının ve "ana ekrana ekle" penceresinin Faz 1 prototipinde Xiaomi ve Samsung'da denenmesi.
- Üreticiye göre pil adımları tablosunun gerçek cihazlarda doğrulanması.
- Kilit ekranından bildirim eyleminin kilit açmadan çalıştığının doğrulanması.

**Hoca kontrolüne eklenmesi önerilen ekran metinleri.** SRS Bölüm 10'daki açık konuya eklenecek.

- Mazeret sonuç metinleri: "bu vaktin namazı sana farz oldu", "vitir de onunla birlikte vâcip oldu", "ikindi bu döneme sayılır".
- Toplu onay gerekçeleri: "3 günden kısa kanama âdet sayılmaz", alışkanlık sonrası "istihâze" açıklaması.
- 15 gün uyarısının metni.
- Hakkında'daki küçük sözlük: kaza, vitir, kerahat vakti, gusül, istihâze.
- V11: 240. saatte dönemin kapanması ve sonrasının istihâze sayılması (BR-14 ile tutarlı, ama ekran davranışı olarak ayrıca teyit edilmeli).

**Faz 0 durumu.** Yol haritasında "Ekran listesi ve kaba taslaklar" maddesi bu turla tamamlanmış oluyor. Faz 0'ın bitti tanımı için açık kalanlar:

- Hoca kontrolü.
- Diyanet erişimi (hesap başvurusu ve DT-1'in ön koşulu).

## 7. Karar soruları (Tur 5)

1. **Kabul senaryoları:** Bölüm 3'teki 7 güncellemeyi ve Bölüm 4'teki 24 yeni senaryoyu kabul-senaryolari.md'ye işleyeyim mi? Toplam 95 senaryoya çıkılır; izlenebilirlik tablosu da güncellenir.
2. **SRS 1.0-taslak.6:** Bölüm 5'teki 13 değişikliği SRS'e işleyeyim mi? DT-1, Diyanet izni gelene kadar "ön koşullu" olarak işaretlenecek.
3. **Faz 0 kapanışı:** Yol haritasındaki "Ekran listesi ve kaba taslaklar" maddesini tamamlandı olarak işaretleyeyim mi? Bölüm 6'daki ekran metinlerini de SRS Bölüm 10'daki hoca kontrolü maddesine ekleyeyim mi?

## Kararlar (27 Ekim 2026)

@ece üç öneriyi de onayladı; aynı gün uygulandı.

- [x] **1. Kabul senaryoları:** Bölüm 3'teki 7 güncelleme ve Bölüm 4'teki 24 yeni senaryo kabul-senaryolari.md'ye işlendi.
  - Toplam 95 senaryo: 73 otomatik, 22 elle ya da kısmen elle.
  - İzlenebilirlik tablosunda 19 satır güncellendi.
  - Fonksiyonel olmayan gereksinimler ve DATA-02 için ayrı bir eşleme paragrafı eklendi.
  - Senaryo etiketleri ile tablo betikle karşılaştırıldı; tek eksik (KS-WDG-07 → FR-TRK-04) giderildi.
- [x] **2. SRS 1.0-taslak.6:** 13 değişiklik gereksinim-analizi-srs.md'ye işlendi; değişen her satırın kaynak sütununda DT numarası var.
  - DT-6 uygulanırken kerahat dakikaları ayarı kaybolmasın diye P1 olarak FR-AYR-03 açıldı. FR sayısı 51 oldu (43 P0, 8 P1); P0 sayısı değişmedi.
  - FR-ONB-03'teki düğme adları ekranla eşitlendi ("Şimdi gireyim / Sonra eklerim").
  - IF-03'e bildirim kanallarının listesi eklendi.
  - Hiçbir BR satırı değişmedi.
- [x] **3. Faz 0 kapanışı:** Yol haritasında "Ekran listesi ve kaba taslaklar" tamamlandı olarak işaretlendi.
  - Fıkhi ekran metinleri SRS Bölüm 10'a ayrı bir hoca kontrolü maddesi olarak eklendi.
  - Bölüm 6'daki devir işleri yol haritasında ilgili fazlara yazıldı:
    - Widget geri sayımı Faz 1 risk prototipine.
    - Erişilebilirlik ve kontrast Faz 5'e.
    - Pil adımları ve kilit ekranı Faz 6'ya.
  - Faz 0'da açık kalanlar: hoca kontrolü ve Diyanet erişimi (DT-1 izni dahil).

Ekran taslakları çalışması bu turla tamamlandı. Sonraki adım, Faz 0'ın iki açık maddesi kapanırken Faz 1 iskeletine başlamak.

# Namaz & Kaza Takip — Önceliklendirilmiş Özellik Listesi

Oct 26, 2026 · @ece · Son güncelleme: 30 Ekim 2026 (asr-ı sânî kaldırıldı, Diyanet başvuru durumu)

## Özet

MVP'nin hedefi: 40 gün sonra da ömür boyu kullanılan, kaza borcunu düşüren, widget'tan tek dokunuşla işaretlenen ve verisi asla kaybolmayan bir namaz takip aracı. Hedef kitle: namaza yeni başlayacaklar ve kılıp düzene oturtamayanlar. 5te5'ten farkı: süre sınırı yok, widget'tan işaretleme, veri güvenliği ve kaza takibi.

Her özellik üç ölçüte göre sıralandı:

- **Sinyal gücü:** 962 tekil 5te5 yorumundaki yaklaşık yorum sayısı ve faydalı oy (ayrıntı: proje dosyalarındaki 5te5-yorum-analizi.md).
- **Rakip boşluğu:** 5te5'in yapmadığı ya da kötü yaptığı şeyler öne alındı; onun güçlü olduğu içerik ve oyunlaştırma geri planda.
- **Efor:** S = birkaç gün, M = 1–2 hafta, L = 2 haftadan fazla. Tek geliştirici ve öğrenci temposu varsayıldı.

Durum sütunları ilerledikçe güncellenebilir.

## P0 — MVP

MVP 10 özellikten oluşuyor: ilk dördü takip ve kaza çekirdeği, gerisi güven ve günlük kullanım. Mazeret modu MVP'de, çünkü o olmadan kaçırılan vakitler kaza borcuna yanlış eklenir.

| # | Özellik | Neden (yorum kanıtı) | Kabul kriteri | Efor | Durum |
| --- | --- | --- | --- | --- | --- |
| 1 | Sınırsız, tarihli günlük takip | 40 gün sınırı en çok istenen değişiklik (\~66 yorum, en yüksek faydalı oy); kullanıcılar uygulamayı silip yeniden kuruyor | Her tarihte 5 vakit işaretlenebilir; süre sınırı yok; açık günün tarihi her ekranda görünür | M | Yapılacak |
| 2 | Geri al ve gelecek vakit kilidi | \~17 yorum; yanlış işaret kaldırılamıyor, vakti gelmemiş namaz işaretlenebiliyor | İşaret tek dokunuşla kaldırılır; vakti girmemiş namaz işaretlenemez; ilk açılışta hiçbir vakit zorla işaretletilmez | S | Yapılacak |
| 3 | Kaza borcu sayacı | \~17 yorum, \~190 faydalı oy; "borç miktarını girsin, kıldıkça ilerlesin"; bazıları kaza için ikinci uygulama kullanıyor | Vakit bazında başlangıç borcu elle girilir; kılınan kaza borcu 1 azaltır; kalan borç toplam ve vakit bazında görünür | M | Yapılacak |
| 4 | Kaçırılan vakti kazaya ekleme | Günlük takip ile kaza birbirinden kopuk; "kılmadım, kaza gereken" seçeneği isteniyor | Vakti çıkmış, işaretlenmemiş namaz için "kılmadım → borca ekle" seçeneği; otomatik ekleme ayarı varsayılan kapalı | S | Yapılacak |
| 5 | Doğru vakitler ve kaydedilen konum | Şehir kaydedilmiyor, Ankara'ya dönüyor; bir kullanıcı bütün gün erken kılmış | Konum bir kez seçilir ve kalır; vakitler internetsiz hazır; kaynak ve hesap yöntemi ayarlarda yazılı | M | Yapılacak |
| 6 | İşaretlenebilir ana ekran widget'ı | Temel fikir; \~12 yorum widget ya da kilit ekranında vakit istiyor | Sıradaki vakit ve kalan süre görünür; o anki vakit uygulama açılmadan tek dokunuşla işaretlenir | L | Yapılacak |
| 7 | Yerel yedek ve dışa aktarma | \~80 açılmama/gri ekran, \~20 veri kaybı yorumu; çözüm "verileri sil" olunca tüm kayıt gidiyor | Dosyaya dışa/içe aktarma; Android otomatik yedeği açık; veritabanı şeması sürümlü, her güncellemede geçiş testi var | M | Yapılacak |
| 8 | Mazeret (regl) modu | \~21 yorum, \~146 faydalı oy; seri bozuluyor, "sadece erkekler için tasarlanmış gibi" | Tarih aralığı mazeretli işaretlenir; seri bozulmaz; o günler kaza borcuna eklenmez | S | Yapılacak |
| 9 | Basit hatırlatma | Bildirim en çok faydalı oy alan konu (eksik, hatalı ve istek karışık); "vakit çıkmadan kıldın mı diye sorsa" | Vakit çıkmadan X dk önce, yalnızca işaretlenmemişse bildirim; vakit başına aç/kapat; suçlayıcı olmayan dil | M | Yapılacak |
| 10 | Hesapsız, reklamsız, internetsiz | Zorunlu hesap 1 yıldızların en büyük grubu (\~35); reklamsızlık \~38 yorumda övülüyor | İlk açılışta kayıt yok; çekirdek özellikler internetsiz çalışır; reklam yok | S | Yapılacak |

## P1 — v1.1

MVP'den hemen sonra gelecek altı özellik geçmişi anlamlı kılıyor ve günlük kullanımı hızlandırıyor.

| # | Özellik | Neden (yorum kanıtı) | Kabul kriteri | Efor | Durum |
| --- | --- | --- | --- | --- | --- |
| 1 | Takvim ve vakit bazlı analiz | "Hiç analiz yok"; hangi vaktin kaçırıldığını ve geçmişi sayfa çevirir gibi görmek isteniyor | Ay görünümü; en çok kaçırılan vakit ve haftalık kılma oranı görünür | M | Yapılacak |
| 2 | Hafif seri ve kilometre taşları | "80, 120, 160 gün serileri olsa"; 40. günde her şeyin sıfırlanması heves kırıyor | Mevcut ve en uzun seri; 40/80/120 gün rozetleri; hiçbir veri sıfırlanmaz | S | Yapılacak |
| 3 | Kaza borcu hesaplayıcı | Kaza hesaplama ayrıca isteniyor; birçok kullanıcı borcunu bilmiyor | Ergenlik yaşı ve düzenli kılmaya başlama tarihinden tahmini borç; sonuç elle düzeltilebilir | S | Yapılacak |
| 4 | Hızlı geriye dönük işaretleme | Birkaç günü sonradan işaretlemek animasyon yüzünden uzun sürüyor; bir kullanıcıda çökme | Geçmiş bir günün 5 vakti tek ekranda, animasyonsuz işaretlenir | S | Yapılacak |
| 5 | Kalıcı bildirimde vakitler | Vakitleri bildirim çubuğunda ve kilit ekranında görmek isteniyor | Ayarla açılan sessiz kalıcı bildirimde sıradaki vakit ve kalan süre | S | Yapılacak |
| 6 | Ezan sesi, vakit başına alarm, kerahat uyarısı | "Her vakte özel ayrı seçilebilmeli"; sabah namazına bildirimle uyanılmıyor | Her vakit için ses, titreşim ya da kapalı; kerahat uyarısı isteğe bağlı | M | Yapılacak |

## P2 — Sonrası

Bu özelliklere talep var ama MVP'yi geciktirmeye değmez; ilk sürüm tutunduktan sonra yeniden değerlendirilecek.

| # | Özellik | Neden (yorum kanıtı) | Efor |
| --- | --- | --- | --- |
| 1 | Kıble (pusula + sensörsüz cihazlar için harita) | Yanlış kıble ve pusula sensörü olmayan telefon şikayetleri; birçok uygulama zaten sunuyor | M |
| 2 | Çoklu profil (aile, çocuk) | "8 yaşındaki oğlum da istiyor, uygulamada tek kişilik hesap var" | M |
| 3 | Bulut yedek (Google Drive) | Telefon değişiminde veri taşıma; yerel yedek MVP'de zaten var | L |
| 4 | Oruç kaza borcu | "Oruç borçlarımızı da dahil edebileceğimiz paket program" | S |
| 5 | Nafile namazlar (teheccüd, işrak, kuşluk) | Birkaç yorumda nafile takibi ve teheccüd hatırlatması isteniyor | S |
| 6 | Türkçe mealli tesbihat ve zikirmatik | Meal isteği \~30 yorumda geçiyor; ancak içerik özelliği ve rakibin güçlü olduğu alan | M |
| 7 | iOS sürümü | "Apple'da yüklenmiyor" şikayeti; Android sürümü doğrulandıktan sonra | L |
| 8 | Şafiî, Mâlikî ve Hanbelî desteği | İlk sürüm yalnızca Hanefî; mezhep seçimi, cem istisnası, farklı mazeret süreleri ve kaza kuralları sonraki güncellemelerde | L |

## Bilinçli olarak yapılmayacaklar

Bunlar rakibin güçlü olduğu ya da kullanıcıların açıkça tepki gösterdiği alanlar.

- **Günlük video, yazı ve soru-cevap içeriği:** 5te5'in en çok övülen yanı ve Sözler Köşkü kitlesine dayanıyor; tek kişinin sürdürmesi mümkün değil.
- **Ağır oyunlaştırma (ağaç, bahçe):** 10 günde bir sıfırlanma ve "ağaç çok az büyüyor" şikayetleri var; hafif seri ve rozetler yeterli.
- **Zorunlu hesap veya sosyal medya bağlantısı:** 1 yıldızlı yorumların en büyük grubu bu yüzden; kullanıcılar kişisel veri istenmesine güvensizlikle tepki veriyor.
- **Reklam veya temel özelliği premium'a kilitleme:** Dini uygulamalarda en sert tepkiyi alan konu.
- **Suçlayıcı ya da yanlış bildirimler:** "10 gündür yoksun" gibi mesajlar aktif kullanıcıları rahatsız ediyor; ton yumuşak, mesaj veriye dayalı olmalı.
- **Asr-ı sânî ve işâ-i sânî gösterimi:** Hoca görüşüyle (28 Ekim 2026) yalnızca Diyanet vakitlerinin saatleri gösterilir.

## Kararlar

Altı açık sorunun tamamı 26 Ekim 2026'da karara bağlandı; fıkhi ayrıntılar Fıkhi Gereksinimler belgesinde.

| Konu | Karar | Uygulamaya etkisi |
| --- | --- | --- |
| Platform | Şimdilik yalnızca Android | iOS P2'de kalır |
| Teknoloji | Kotlin + Jetpack Compose; widget için Jetpack Glance; yerel veri için Room | Widget'a dokunmak uygulamayı açmadan vakti işaretler; Room şema dışa aktarımıyla her sürümde geçiş testi yapılır |
| Vakit kaynağı | Diyanet | Resmi API hesapla token alıyor, bilgiler uygulamaya gömülmemeli. İlçe vakitleri küçük bir ara sunucudan aylık indirilip cihazda saklanır; kullanıcı hesabı gerekmez |
| Kaza düşme sırası | Kılınan kaza, kullanıcının seçtiği vakitten düşer | Kaza işaretlerken vakit seçimi zorunlu |
| Mazeret verisi | Yedek dosyasında şifreli | Kullanıcı parolasıyla şifrelenir; cihaza bağlı anahtar kullanılmaz, yoksa yeni telefonda açılamaz |
| Hedef kitle | Yeni başlayanlar ve düzene oturtamayanlar | 5te5'in çekirdek kitlesiyle aynı; ilk deneyim ve hatırlatma tonu kritik |
| Mezhep | İlk sürüm yalnızca Hanefî; Şafiî, Mâlikî ve Hanbelî sonraki güncellemelerde | Günlük takipte vitir 6. namaz ve kaza borcuna dahil; kural motoru mezhep parametreli yazılır ki diğer mezhepler kod yapısı değişmeden eklenebilsin. Ayrıntılar: Fıkhi Gereksinimler belgesi |

### Hâlâ açık

- [x] **Fıkhi ayrıntılar:** İlk sürüm Hanefî'ye göre; vitir borca dahil; vakitler Diyanet takvimine göre. Asr-ı sânî seçeneği hoca görüşüyle kaldırıldı (28 Ekim 2026).
- [ ] **Diyanet erişimi:** API araştırıldı (SRS IF-02); hesap başvuru formu 29 Ekim 2026'da gönderildi, yanıt bekleniyor. Ara sunucu altyapısı Faz 3'te seçilecek.

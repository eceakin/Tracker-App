# Karar Tablosu — Kural Motoru Test Senaryoları (v1.5)

26 Ekim 2026 · @ece · Son güncelleme: 28 Ekim 2026 (hoca görüşü, ikinci görüşme)

## Özet

Bu tablo, SRS'teki iş kurallarını (BR-00 – BR-22; BR-06 1.0-taslak.7'de kaldırıldı) 101 geçerli senaryoya çevirir. Her satır bir birim testinin sözleşmesidir: aynı ön koşul ve aynı olay verildiğinde kural motoru tam olarak "Beklenen" sütunundakini döndürmelidir.

**Nasıl kullanılır?**

- Faz 2'de her satır bir parametreli test olur. Test adı satır kimliğini ve kuralı taşır, ör. `KT41_BR12_hayiz127SaatteBitti_kalan60dk_ikindiIcinSoru`.
- Tüm senaryolar Bölüm "Sözleşmeler"deki sabit referans günü kullanır. Böylece testler gerçek Diyanet verisine, tarihe ya da saat dilimine bağlı kalmaz ve her çalıştırmada aynı sonucu verir.
- **Tür** sütunu: *Normal* (beklenen akış), *Sınır* (eşiğin tam üstü, tam altı) ve *Negatif* (sistemin reddetmesi gereken durum). Her kural için en az bir sınır senaryosu vardır; hataların çoğu eşiklerde çıkar.
- "Beklenen" sütunundaki **öneri**, kullanıcı onaylamadan veriye yazılmayan sonuçtur (BR-00). **Engel**, eylemin yapılmadığı ve açıklama gösterildiği sonuçtur.
- Kaldırılan satırlar kimliklerini korur ve "Kaldırıldı" olarak durur.

**Sürüm geçmişi.** v1.0 yazılırken SRS'te tanımsız kalan 6 nokta bulunmuştu. v1.1'de bu noktalar karara bağlandı ve SRS 1.0-taslak.4'e işlendi; 8 yeni senaryo eklendi (KT-82 – KT-89) ve baygınlık sayımındaki bir hata nedeniyle KT-78 ile KT-79 düzeltildi. v1.2'de KT-41, KT-55 ve KT-60'taki bildirim metinleri NFR-SEC-03 ile uyumlu olacak şekilde nötrleştirildi. v1.3'te KT-52'nin test verisi 72 saatlik sınırı sınayacak şekilde düzeltildi (18 kalem). **v1.4'te hocanın 28 Ekim 2026 görüşü işlendi** (SRS 1.0-taslak.7): kerahat pencereleri 45 dk'ya, gusül aralığı 5–30 dk'ya çekildi; alışkanlık tek dönemde güncellenir; kesilip yeniden başlayan kanama birleşir; asr-ı sânî senaryosu kaldırıldı. 12 satır güncellendi, 1 satır kaldırıldı, 11 satır eklendi (KT-90 – KT-100). **v1.5'te ikinci hoca görüşmesi işlendi** (SRS 1.0-taslak.8): 10 günü aşan dönem alışkanlığı 10 gün yapar, bulûğ önerisi erkekte de 14, lohusalıkta 40 gün içindeki kan birleşir; 7 satır güncellendi, 2 satır eklendi (KT-101, KT-102). Ayrıntı son bölümdeki karar kaydında.

## Sözleşmeler

Aşağıdaki kurallar tüm senaryolarda geçerlidir; bir satır aksini yazmıyorsa bunlar varsayılır.

**Zaman semantiği**

- Hassasiyet dakikadır; saniyeler kesilir.
- Tüm aralıklar **başlangıç dahil, bitiş hariç** tanımlanır: `[başlangıç, bitiş)`. Örneğin öğle vakti 12:30'da başlar; 15:30 artık ikindidir.
- Saat dilimi Europe/Istanbul (yaz saati yok). Yaz saati geçişleri bu tablonun değil, NFR-CMP-02 testlerinin konusudur.
- **Gün gösterimi:** G1 senaryonun başladığı gündür; "G6 17:00", 6. günün saat 17:00'ı demektir. Mazeret süreleri başlangıç anından bitiş anına saat olarak ölçülür.
- **Kalan süre** = vaktin bitişi − mazeretin bitiş anı.

**Referans gün fikstürü (her gün aynı)**

| İmsak | Güneş | Öğle | İkindi | Akşam | Yatsı | Ertesi imsak |
| --- | --- | --- | --- | --- | --- | --- |
| 05:30 | 07:00 | 12:30 | 15:30 | 18:00 | 19:30 | 05:30 |

**Fikstürden türeyen pencereler**

| Pencere | Aralık |
| --- | --- |
| Sabah kerahati | [07:00, 07:45) |
| İstivâ kerahati | [11:45, 12:30) |
| Akşam kerahati | [17:15, 18:00) |
| Hatırlatmalar (X = 30) | sabah 06:30, öğle 15:00, ikindi 17:30, akşam 19:00, yatsı ve vitir ertesi gün 05:00 |

**Varsayılan parametreler**

| Parametre | Değer |
| --- | --- |
| Gusül süresi | 15 dk (kabul aralığı 5–30) |
| Tekbir süresi | 1 dk |
| Kerahat dakikaları | 45 / 45 / 45 (`RuleSet` parametresi, kullanıcı ayarı değil) |
| Hatırlatma (X) | 30 dk |
| Otomatik borç ekleme | Kapalı |
| Cinsiyet | Mazeret senaryolarında kadın, diğerlerinde fark etmez |
| Âdet alışkanlığı | Girilmemiş |
| Başlangıç kaza borcu | Tüm kalemlerde 0 |

## Tablo A — Vakit, işaretleme ve kaçırıldı

17 geçerli senaryo; işaretlemenin ne zaman açıldığını ve bir vaktin ne zaman "kaçırıldı" sayıldığını sabitler.

| ID | Kural | Ön koşul | Olay | Beklenen | Tür |
| --- | --- | --- | --- | --- | --- |
| KT-01 | BR-02 | Öğle boş | 12:29'da öğleye dokunulur | Engel: işaret konmaz, "vakti henüz girmedi" | Sınır |
| KT-02 | BR-02 | Öğle boş | 12:30'da öğleye dokunulur | Öğle = kılındı | Sınır |
| KT-03 | BR-02 | Yatsı boş | 10:00'da yatsıya dokunulur | Engel | Negatif |
| KT-04 | BR-03 | Yatsı boş | 20:00'de vitire dokunulur | Engel: "önce yatsı işaretlenmeli" | Negatif |
| KT-05 | BR-03 | Yatsı kılındı | 20:00'de vitire dokunulur | Vitir = kılındı | Normal |
| KT-06 | BR-03 | Yatsı kılındı, vitir boş | Ertesi gün 05:29'da vitire dokunulur | Vitir = kılındı (G1 kaydına yazılır) | Sınır |
| KT-07 | BR-04 | Öğle boş | Saat 15:29 olur | Öneri yok; öğle hâlâ vaktinde | Sınır |
| KT-08 | BR-04 | Öğle boş | Saat 15:30 olur | Öğle = kılınmadı; öneri: "öğle borca eklensin mi?" | Sınır |
| KT-09 | BR-04, BR-00 | Öğle boş, otomatik ekleme açık | Saat 15:30 olur | Borç(öğle) +1, kayıt türü "otomatik"; öneri gösterilmez | Normal |
| KT-10 | BR-04 | Sabah boş | Saat 07:00 olur | Sabah = kılınmadı; borç önerisi | Sınır |
| KT-11 | BR-04 | Yatsı ve vitir boş | Ertesi gün 05:30 olur | İki ayrı öneri: yatsı ve vitir | Sınır |
| KT-12 | BR-04, FR-TRK-04 | G1 öğlesi kaçırıldı, öneri onaylandı; borç(öğle) = 1 | 16:00'da G1 öğlesi "sonradan kaza olarak kıldım" diye işaretlenir | Kayıt = kılındı (kaza); kılınan kaza kaydı eklenir; borç(öğle) = 0 | Normal |
| KT-13 | BR-04, FR-TRK-04 | G1 öğlesi kaçırıldı, öneri yanıtlanmadı | 16:00'da "vaktinde kıldım" seçilir | Kayıt = kılındı; öneri kapanır; borç değişmez | Normal |
| KT-14 | — | *Kaldırıldı (v1.4).* Asr-ı sânî gösterilmediği için (BR-06 kaldırıldı) sınanacak bir davranış kalmadı. Öğlenin 15:30'da kaçırıldı sayılması KT-08 ile sınanır | — | — | — |
| KT-15 | BR-08 | Cuma günü, erkek kullanıcı | 13:00'te "Cuma" seçilir | Öğle kalemi = kılındı (cuma) | Normal |
| KT-16 | BR-08 | Cuma günü, erkek, kalem boş | Saat 15:30 olur | Borç önerisinin kalemi "öğle" (cuma değil) | Normal |
| KT-17 | BR-08 | Cuma günü, kadın kullanıcı | Bugün ekranı açılır | Kalem "Öğle"; "Cuma" yalnızca isteğe bağlı seçenek | Normal |
| KT-18 | BR-04, FR-TRK-03 | Öğle 12:40'ta işaretlendi | 12:45'te geri alınır, sonra saat 15:30 olur | Kayıt = belirsiz; 15:30'da borç önerisi çıkar | Normal |

## Tablo B — Kerahat ve kaza eylemleri

24 senaryo; kazanın ne zaman kılınabileceğini, tertip notunu, rekât sayısını ve borcun hiç negatife düşmemesini sabitler.

| ID | Kural | Ön koşul | Olay | Beklenen | Tür |
| --- | --- | --- | --- | --- | --- |
| KT-19 | BR-07 | Borç(sabah) = 1 | 07:00'da "kaza kıldım" | Engel: kerahat vakti | Sınır |
| KT-20 | BR-07 | Borç(sabah) = 1 | 07:44'te "kaza kıldım" | Engel | Sınır |
| KT-21 | BR-07 | Borç(sabah) = 1 | 07:45'te "kaza kıldım" | Kaza kaydedilir; borç(sabah) = 0 | Sınır |
| KT-22 | BR-07 | Borç(sabah) = 1 | 11:44'te "kaza kıldım" | Kaza kaydedilir | Sınır |
| KT-23 | BR-07 | Borç(sabah) = 1 | 11:45'te "kaza kıldım" | Engel: istivâ | Sınır |
| KT-24 | BR-07 | Borç(sabah) = 1 | 17:15'te "kaza kıldım" | Engel: akşam kerahati | Sınır |
| KT-91 | BR-07 | Borç(sabah) = 1 | 17:14'te "kaza kıldım" | Kaza kaydedilir (ikindiden sonra kerahate kadar kaza açık) | Sınır |
| KT-25 | BR-07 | G1 ikindisi boş | 17:30'da G1 ikindisine dokunulur | İkindi = kılındı (eda); istisna uygulanır | Normal |
| KT-26 | BR-07 | Önceki günden borç(ikindi) = 1 | 17:30'da ikindi için "kaza kıldım" | Engel: istisna yalnızca o günün ikindisi için | Negatif |
| KT-27 | BR-07, NFR-MNT-02 | Test için `RuleSet`'te sabah kerahati 40 dk; borç(sabah) = 1 | 07:40'ta "kaza kıldım" | Kaza kaydedilir (pencere [07:00, 07:40)). Pencerenin koddan değil parametreden geldiğini sınar; kullanıcı arayüzünde bu ayar yoktur | Sınır |
| KT-28 | BR-17 | Farz borcu toplam 5 (vitir hariç) | 12:35'te öğle işaretlenir | Öğle = kılındı; tertip bilgi notu gösterilir; işaret engellenmez | Sınır |
| KT-29 | BR-17 | Farz borcu toplam 6 | 12:35'te öğle işaretlenir | Öğle = kılındı; not gösterilmez | Sınır |
| KT-30 | BR-17 | Farz borcu 5, vitir borcu 3 | 12:35'te öğle işaretlenir | Not gösterilir (vitir sayılmaz) | Sınır |
| KT-92 | BR-17 | Farz borcu 0, vitir borcu 2 | 12:35'te öğle işaretlenir | Öğle = kılındı; not gösterilmez (yalnız vitir borcunda tertip notu yok) | Negatif |
| KT-93 | BR-17 | Farz borcu 6 (öğle 6) | G1 13:00'te öğle kazası kılınır (farz borcu 5); 15:40'ta ikindi işaretlenir | Tertip geri döner: not gösterilir. Kalan süre 15:40 → 17:15 = 95 dk ≥ (20 + 4) × 2 = 48 dk | Normal |
| KT-31 | BR-17 | Farz borcu 5 (öğle, ikindi, akşam, yatsı, sabah; 17 rekât), vitir borcu 0 → gerekli süre (17 + 4) × 2 = 42 dk | 14:48'de öğle işaretlenir (kalan 42 dk) | Vakit dar değil (42 ≥ 42); tertip notu gösterilir | Sınır |
| KT-82 | BR-17 | KT-31 ile aynı (gerekli süre 42 dk) | 14:49'da öğle işaretlenir (kalan 41 dk) | Vakit dar (41 < 42); öğle = kılındı, tertip notu gösterilmez | Sınır |
| KT-83 | BR-17 | Farz borcu 1 (sabah, 2 rekât) → gerekli (2 + 4) × 2 = 12 dk; ikindide ölçü akşam kerahatinin başlangıcı (17:15) | 17:04'te ikindi işaretlenir (kerahate 11 dk) | Vakit dar (11 < 12); not gösterilmez. 17:03'te işaretlenseydi (12 dk) not gösterilirdi | Sınır |
| KT-84 | BR-17 | Farz borcu 1 (yatsı), vitir borcu 1 → kaza 4 + 3 = 7 rekât; gerekli (7 + 2) × 2 = 18 dk | 06:43'te sabah işaretlenir (kalan 17 dk) | Vakit dar (17 < 18); not gösterilmez. Vitir rekâtları hesaba katılır | Sınır |
| KT-32 | BR-18 | Öğle seferî mod açıkken kaçırıldı ve borca eklendi | Seferî mod kapalıyken kaza ekranı açılır | Kalem: "öğle — 2 rekât (seferde kaçtı)" | Normal (P1) |
| KT-33 | BR-18 | Öğle seferî mod kapalıyken kaçırıldı | Seferî mod açıkken kaza ekranı açılır | Kalem: "öğle — 4 rekât" | Normal (P1) |
| KT-34 | BR-09 | Bayram günü, bayram namazı işaretlenmedi | Gün biter | Borç önerisi yok | Negatif |
| KT-35 | BR-00, FR-KAZ-04 | Borç(öğle) = 0 | 14:00'te öğle için "kaza kıldım" | Soru: "bu vakitte borç yok, önce ekleyip sonra düşeyim mi?"; onay yoksa hiçbir şey yazılmaz, borç asla −1 olmaz | Negatif |
| KT-36 | FR-KAZ-02 | Borç(ikindi) = 2 | Vakit seçilmeden "kaza kıldım" kaydedilmek istenir | Engel: vakit seçimi zorunlu | Negatif |

## Tablo C — Mazeret başlangıç ve bitişi

16 senaryo. BR-12'deki "farz olur" ifadesi şu anlama gelir: vakit henüz çıkmamışsa kadın o namazı vaktinde kılabilir; borç önerisi ancak vakit işaretlenmeden biterse doğar (karar 3).

**Terim:** "Açık" = kalem mazeretli değil, normal kurallarla işaretlenebilir.

| ID | Kural | Ön koşul | Olay | Beklenen | Tür |
| --- | --- | --- | --- | --- | --- |
| KT-37 | BR-11 | G1 ikindisi boş | Hayız G1 16:00'da başlatılır | İkindi = mazeretli; ikindi için borç önerisi yok | Normal |
| KT-38 | BR-11 | G1 ikindisi 15:40'ta kılındı | Hayız G1 16:00'da başlatılır | İkindi kaydı "kılındı" olarak kalır | Normal |
| KT-39 | BR-11, BR-04 | G1 öğlesi boş, 15:30'da kaçırıldı, öneri bekliyor | Hayız G1 16:00'da başlatılır | Öğle önerisi geçerli kalır (mazeretten önce kaçırıldı); ikindi mazeretli | Normal |
| KT-40 | BR-10 | Hayız G1 16:00'dan beri sürüyor | G2 boyunca vakitler girip çıkar | G2'nin 6 kalemi = mazeretli; borç önerisi yok; hatırlatma kurulmaz; seri bozulmaz | Normal |
| KT-41 | BR-12 | Hayız G1 10:00'da başladı | G6 17:00'de bitiş kaydedilir (127 saat); ikindinin çıkmasına 60 dk var, 60 ≥ 15 + 1 | İkindi = açık; nötr bildirim: "İkindi vakti çıkmadan kılabilirsin" (açıklama uygulama içinde); 18:00'e kadar işaretlenmezse borç önerisi | Normal |
| KT-42 | BR-12 | Hayız G1 10:00'da başladı | G6 17:44'te bitiş; kalan 16 dk = 15 + 1 | İkindi = açık (eşitlik farz kılar) | Sınır |
| KT-43 | BR-12 | Hayız G1 10:00'da başladı | G6 17:45'te bitiş; kalan 15 dk < 16 | İkindi = mazeretli kalır; akşam 18:00'de normal açılır | Sınır |
| KT-44 | BR-12 | Hayız G1 17:50'de başladı | G11 17:50'de bitiş (tam 240 saat); kalan 10 dk ≥ 1 | İkindi = açık (yalnızca tekbir süresi aranır) | Sınır |
| KT-45 | BR-12 | Hayız G1 17:59'da başladı | G11 17:59'da bitiş (tam 240 saat); kalan 1 dk ≥ 1 | İkindi = açık | Sınır |
| KT-46 | BR-12, BR-04 | Hayız G1 10:00'da başladı | Bitiş "G6 17:00" olarak G7 09:00'da geriye dönük kaydedilir; aradaki kalemler işaretsiz | 5 borç önerisi: G6 ikindi (BR-12), G6 akşam, yatsı, vitir ve G7 sabah (BR-04) | Normal |
| KT-47 | BR-12 | Nifas G1 08:00'de başladı | G20 17:30'da bitiş (< 960 saat); kalan 30 dk ≥ 16 | İkindi = açık | Normal |
| KT-48 | BR-12 | Nifas G1 17:55'te başladı | G41 17:55'te bitiş (tam 960 saat); kalan 5 dk ≥ 1 | İkindi = açık | Sınır |
| KT-49 | BR-12 | Gusül süresi 30 dk'ya ayarlı (izin verilen en yüksek değer); hayız G1 10:00'da başladı | G6 17:35'te bitiş; kalan 25 dk < 31 | İkindi = mazeretli kalır | Sınır |
| KT-90 | BR-12, FR-AYR-01 | Gusül 15 dk | Gusül süresi sırasıyla 31, 4, 30 ve 5 dk olarak girilmek istenir | 31 ve 4: engel, değer kaydedilmez ("5 – 30 dk"); 30 ve 5: kabul | Sınır |
| KT-50 | BR-12 | Hayız G1 10:00'da başladı | G6 09:00'da bitiş (vakit dışı: güneş ile öğle arası) | Hiçbir kalem açılmaz veya sorulmaz; öğle 12:30'da normal açılır | Normal |
| KT-51 | BR-12 | Hayız G1 10:00'da başladı | G6 05:00'te bitiş (G5'in yatsı vakti, 115 saat); imsaka 30 dk ≥ 16 | G5 yatsısı ve vitri = açık; bildirim ikisini birlikte anar. G6 05:30'a kadar işaretlenmezse iki ayrı borç önerisi (yatsı, vitir) | Normal |

## Tablo D — Mazeret süre kontrolleri

22 senaryo; 72, 240 ve 960 saat sınırlarını, alışkanlık gününü, alışkanlığın değişmesini, hayızda ve nifasta ara temizliği ve 15 günlük temizlik aralığını sabitler. Kalem sayıları, fikstürdeki vakitlerle kodla sayıldı.

| ID | Kural | Ön koşul | Olay | Beklenen | Tür |
| --- | --- | --- | --- | --- | --- |
| KT-52 | BR-13 | — | Hayız G1 08:00'de başlar, G4 07:59'da biter (71 saat 59 dk) | Uyarı: "72 saatten kısa kanama hayız sayılmaz". Dönemdeki 18 kalem için tek ekranda borç önerisi: G1 öğle–vitir (5), G2 (6), G3 (6), G4 sabah (1) | Sınır |
| KT-53 | BR-13 | — | Hayız G1 08:00'de başlar, G4 08:00'de biter (tam 72 saat) | Hayız geçerli; uyarı ve öneri yok | Sınır |
| KT-94 | BR-14, BR-13 | Alışkanlık 6 gün | KT-52'deki 71 saat 59 dakikalık kanama kaydedilir | Alışkanlık 6 gün kalır; güncelleme bilgisi gösterilmez (hayız sayılmayan dönem alışkanlığı değiştirmez) | Negatif |
| KT-54 | BR-14 | Alışkanlık 6 gün | Hayız G1 08:00'de başlar, G9 08:00'de biter (192 saat) | Tamamı hayız; borç önerisi yok. Alışkanlık 8 güne güncellenir (KT-87) | Normal |
| KT-55 | BR-14 | Alışkanlık 6 gün; hayız G1 08:00'den beri sürüyor | Saat G11 08:00 olur (240. saat) | Nötr bildirim: "Bugünden itibaren namazlarını işaretleyebilirsin"; açıklama ("10 gün doldu, sonrası istihâze") uygulama içinde. Alışkanlık sonrasındaki 24 kalem için borç önerisi: G7 öğle–vitir (5), G8–G10 (18), G11 sabah (1). Bu dönemin hesabı dönem başındaki alışkanlıkla (6 gün) yapılır; dönem kapanınca alışkanlık 10 güne güncellenir ve uygulama içinde bilgi görünür | Sınır |
| KT-56 | BR-14 | Alışkanlık girilmemiş; hayız G1 08:00'den beri sürüyor | Saat G11 08:00 olur | Aynı bildirim; borç önerisi yok (10 gün hayız kabul edilir). Alışkanlık 10 gün olarak kaydedilir | Sınır |
| KT-57 | BR-14 | KT-55'in devamı; G11 öğlesi işaretlenmedi | Saat G11 15:30 olur | Normal kaçırıldı önerisi (kalem mazeretli değil) | Normal |
| KT-58 | BR-14 | Önceki dönem 168 saat sürdü, alışkanlık 7 gün; bu dönem G1 08:00'de başladı ve sürüyor | Saat G11 08:00 olur (240 saat aşıldı) | Bu dönemin hesabı dönem başındaki alışkanlıkla (7 gün) yapılır: G8 08:00'den sonrası istihâze. Dönem kapanınca alışkanlık 10 gün olur; 12 gün sürmüş olsa bile 10 yazılır. Sonraki 240 saati aşan dönemde ilk 10 gün hayız sayılır, yani istihâze önerisi çıkmaz | Normal |
| KT-59 | BR-14 | Alışkanlık girilmemiş | Hayız G1 08:00'de başlar, G8 08:00'de biter (168 saat) | Tamamı hayız; alışkanlık 7 gün olarak kaydedilir ve uygulama içinde "Alışkanlığın 7 gün olarak kaydedildi · Değiştir" bilgisi görünür | Normal |
| KT-87 | BR-14 | Alışkanlık 6 gün | Hayız 190 saatte biter (190 ÷ 24 = 7,9 → 8 gün) | Tamamı hayız. Alışkanlık tek dönemde 8 güne güncellenir; uygulama içinde "Alışkanlığın 8 gün olarak güncellendi · Değiştir" bilgisi görünür, bildirim gitmez. Onay sorusu sorulmaz | Normal |
| KT-88 | BR-14 | Alışkanlık 6 gün | Hayız 155 saatte biter (6,46 → 6 gün) | Alışkanlık değişmez; bilgi gösterilmez. 156 saatte bitseydi (6,5 → 7 gün) alışkanlık 7 olurdu | Sınır |
| KT-89 | BR-14 | Alışkanlık 7 gün | Hayız 120 saatte biter (5 gün) | Alışkanlık 5 güne güncellenir (kısalma yönünde de çalışır) | Normal |
| KT-60 | BR-15 | Nifas G1 08:00'den beri sürüyor | Saat G41 08:00 olur (960. saat) | Nötr bildirim: "Bugünden itibaren namazlarını işaretleyebilirsin"; açıklama ("40 gün doldu, sonrası istihâze") uygulama içinde; sonraki kalemler normal kurallarla açılır. Önceki lohusalık süresi sorulmaz | Sınır |
| KT-61 | BR-15, BR-13 | — | Nifas G1 08:00'de başlar, G3 08:00'de biter (48 saat) | Nifas geçerli; 72 saat uyarısı yok (nifasın alt sınırı yok) | Negatif |
| KT-95 | BR-16 | Alışkanlık girilmemiş; hayız G1 08:00 – G4 08:00 kaydedildi; G4 – G7 arasında bazı kalemler işaretlendi, kalanların önerisi bekliyor | Yeni kanama G7 08:00'de başlatılır (başlangıçtan 144 saat), G9 08:00'de biter | Yeni dönem açılmaz; G1 08:00 – G9 08:00 tek dönem (192 saat), tamamı hayız. Aradaki 18 kalem (G4 öğle–vitir 5, G5 6, G6 6, G7 sabah 1) mazeretli sayılır; bekleyen önerileri kapanır; işaretlenmiş kalemler "kılındı" kalır. Alışkanlık 8 gün olarak kaydedilir (KT-59) | Normal |
| KT-96 | BR-16, BR-00 | KT-95 ile aynı; ayrıca G5 öğlesinin önerisi onaylanmıştı (borç(öğle) = 1) | Yeni kanama G7 08:00'de başlatılır | Tek ekranda "Bu günler âdet döneminden sayıldı. Eklediğin 1 borcu geri alayım mı?" sorusu. Onaylanırsa "düzeltme" kaydı eklenir, borç(öğle) = 0; onaylanmazsa borç değişmez. Otomatik ekleme açık olsa da sorulur | Normal |
| KT-97 | BR-16, BR-14 | Alışkanlık 6 gün; hayız G1 08:00 – G4 08:00 kaydedildi | Yeni kanama G6 08:00'de başlatılır ve sürer; saat G11 08:00 olur | Tek dönem 240. saatte kapanır, KT-55'teki bildirim. G1 08:00 – G7 08:00 hayız: aradaki 12 kalem (G4 öğle–vitir 5, G5 6, G6 sabah 1) mazeretli. G7 08:00'den sonrası istihâze: 24 kalem için borç önerisi (KT-55 ile aynı). Dönem kapanınca alışkanlık 10 olur. Toplam süre kuralı hocanın uygulamaya bıraktığı karardır (karar 17) | Normal |
| KT-98 | BR-16 | Alışkanlık girilmemiş; hayız G1 08:00 – G4 08:00 kaydedildi | Yeni kanama G11 07:59'da başlatılır (başlangıçtan 239 saat 59 dk) | Birleşir: aradaki 42 kalem (G4 öğle – G11 sabah) mazeretli, bekleyen önerileri kapanır; dönem bir dakika sonra, G11 08:00'de 240. saatte kapanır ve KT-56'daki gibi borç önerisi çıkmaz; alışkanlık 10 gün olarak kaydedilir. G11 08:00'de başlatılsaydı birleşmezdi ve KT-62 uyarısı çıkardı | Sınır |
| KT-101 | BR-15 | Nifas G1 08:00 – G11 08:00 kaydedildi; aradaki bazı kalemlerin önerisi bekliyor | Yeni kanama G20 08:00'de "Âdet" türüyle başlatılır (nifas başlangıcından 456 saat) | Yeni dönem açılmaz; nifas yeniden açılır ve tür nifas kalır. Aradaki 54 kalem (G11 öğle–vitir 5, G12–G19 48, G20 sabah 1) mazeretli; bekleyen önerileri kapanır, onaylanmış borçların geri alınması KT-96'daki gibi sorulur. 15 gün uyarısı çıkmaz | Normal |
| KT-102 | BR-15 | Nifas G1 08:00 – G11 08:00 kaydedildi | Yeni kanama G41 07:59'da başlatılır (959 saat 59 dk) | Birleşir: aradaki 180 kalem (G11 öğle – G41 sabah) mazeretli; nifas bir dakika sonra, G41 08:00'de 960. saatte kapanır. G41 08:00'de başlatılsaydı birleşmez, yeni bir âdet dönemi açılırdı; nifasın bitişinden 30 gün geçtiği için uyarı çıkmazdı | Sınır |
| KT-62 | BR-16 | Önceki hayız G4 08:00 – G10 08:00 | Yeni kanama G25 07:59'da başlatılmak istenir (başlangıçtan 240 saatten fazla, bitişten 14 gün 23 saat 59 dk) | Birleşmez. Uyarı: "15 günlük temizlik dolmadı; bu kanama âdet sayılmaz, namazların devam eder". Varsayılan seçenek dönemi başlatmamak; vakitler normal kurallarla açık kalır. Kullanıcı "Yine de başlat"ı seçerse kayıt "uyarılı" işaretiyle tutulur | Sınır |
| KT-63 | BR-16 | Önceki hayız G4 08:00 – G10 08:00 | Yeni hayız G25 08:00'de başlatılır (tam 15 gün) | Uyarı yok; yeni dönem açılır | Sınır |

## Tablo E — Hatırlatma, ayarlar, hesaplayıcı, sağlık ve temel ilke

22 senaryo. Hesaplayıcı ve baygınlık satırlarındaki sayılar ve hicrî tarihler kodla doğrulandı.

| ID | Kural | Ön koşul | Olay | Beklenen | Tür |
| --- | --- | --- | --- | --- | --- |
| KT-64 | BR-05 | Öğle boş | Öğle vakti girer (12:30) | Hatırlatma 15:00'e kurulur | Normal |
| KT-65 | BR-05 | Öğle 14:00'te işaretlendi | — | 15:00 hatırlatması iptal edilir; bildirim gitmez | Normal |
| KT-66 | BR-05 | Yatsı ve vitir boş | Yatsı vakti girer | Ertesi gün 05:00'e iki kalemi kapsayan tek hatırlatma kurulur | Normal |
| KT-67 | BR-05, BR-10 | Mazeret sürüyor | Vakitler girer | Hiçbir hatırlatma kurulmaz | Normal |
| KT-68 | BR-05 | X = 45 dk'ya ayarlı; öğle boş | Öğle vakti girer | Hatırlatma 14:45'e kurulur | Normal |
| KT-69 | BR-21 | Gusül 15 dk iken G6 17:45 bitişi kaydedildi; G6 ikindisi mazeretli (KT-43) | G8'de gusül süresi 10 dk'ya düşürülür | G6 ikindisi mazeretli kalır; geçmiş yeniden hesaplanmaz | Normal |
| KT-70 | BR-21 | Kadın kullanıcı; G1–G5 mazeret kaydı var | G10'da cinsiyet "erkek" yapılır | Onay istenir; geçmiş kayıtlar silinmez, G1–G5 mazeretli kalır; mazeret menüsü gizlenir | Normal |
| KT-71 | BR-19 | Erkek, doğum 15 Temmuz 2010, bulûğ yaşı bilinmiyor ve tahmin de edilemiyor | Hesaplayıcı açılır | Önce tahmin istenir; "Tahmin edemiyorum" seçilince önerilen bulûğ tarihi 13 Şubat 2024 (14 kamerî yıl, Ümmü'l-Kurâ takvimi; miladî 14 yıldan 153 gün önce) | Normal (P1) |
| KT-99 | BR-19 | Kız, doğum 15 Temmuz 2010, bulûğ yaşı bilinmiyor ve tahmin de edilemiyor | Hesaplayıcı açılır | Önce tahmin istenir; "Tahmin edemiyorum" seçilince önerilen bulûğ tarihi 13 Şubat 2024 (14 kamerî yıl, Ümmü'l-Kurâ takvimi; miladî 14 yıldan 153 gün önce). Tahmin girilirse (ör. 12) o yaş kullanılır. Erkekte de aynı yaş önerilir (KT-71) | Normal (P1) |
| KT-72 | BR-19 | Kız | Bulûğ yaşı 8 girilir | Engel: alt sınır 9 | Negatif |
| KT-73 | BR-19 | Erkek | Bulûğ yaşı 12 girilir | Kabul | Sınır |
| KT-74 | BR-19 | Erkek | Bulûğ yaşı 11 girilir | Engel: alt sınır 12 | Sınır |
| KT-75 | BR-20 | Erkek; bulûğ 1 Ocak 2015, düzenli başlangıç 1 Ocak 2025; kılındığı kesin bilinen gün 0 | Hesapla | 3.653 gün × 6 = 21.918 kalem, "tahmini" etiketiyle | Normal (P1) |
| KT-76 | BR-20 | Kadın; aynı aralık; alışkanlık 7 gün; 120 tam ay | Hesapla | (3.653 − 840) × 6 = 16.878 kalem | Normal (P1) |
| KT-77 | BR-20 | Erkek; aynı aralık; kılındığı kesin bilinen 365 gün | Hesapla | (3.653 − 365) × 6 = 19.728 kalem. Ekran yalnızca kesin bilinen günlerin girilmesini söyler; emin olunmayan günler borçta kalır | Normal (P1) |
| KT-78 | BR-22 | — | Baygınlık G1 12:00 – G2 06:00 kaydedilir | Vakti çıkan farzlar: öğle, ikindi, akşam, yatsı → 4 ≤ 5: öğle, ikindi, akşam, yatsı ve vitir için borç önerisi (5 kalem). G2 sabahının vakti sürdüğünden sabah normal kurallarla açık | Normal (P1) |
| KT-100 | BR-22 | — | Ameliyat narkozu G1 12:00 – G2 06:00 kaydedilir | KT-78 ile aynı sonuç: 5 kalem için borç önerisi. Bilinç kaybının türü sonucu değiştirmez | Normal (P1) |
| KT-79 | BR-22 | — | Baygınlık G1 12:00 – G2 15:29 kaydedilir | Vakti çıkan farzlar: G1 öğle, ikindi, akşam, yatsı ve G2 sabah → 5: 6 kalem için borç önerisi (5 farz + vitir). G2 öğlesi açık (1 dk kaldı) | Sınır (P1) |
| KT-85 | BR-22 | — | Baygınlık G1 12:00 – G2 15:30 kaydedilir | G2 öğlesinin de vakti çıktı → 6: namazlar düşer; 7 kalem (G1 öğle, ikindi, akşam, yatsı, vitir, G2 sabah, öğle) için öneri yok. G2 ikindisi normal kurallarla açık | Sınır (P1) |
| KT-86 | BR-22 | — | Baygınlık G1 15:30 – G2 15:30 kaydedilir | G1 öğlesinin bitişi (15:30) başlangıca eşit, sayılmaz → 5 (ikindi, akşam, yatsı, sabah, G2 öğle): 6 kalem için borç önerisi (5 farz + vitir) | Sınır (P1) |
| KT-80 | BR-00 | Otomatik borç ekleme açık | KT-52'deki 71 saat 59 dakikalık kanama kaydedilir | 18 kalem otomatik yazılmaz, yine kullanıcıya sorulur | Negatif |
| KT-81 | BR-00, FR-KAZ-05 | Bir öneri onaylandı; borç(öğle) = 1 | Kullanıcı bu kaydı geri alır | Kayıt silinmez, "düzeltme" kaydı eklenir; borç(öğle) = 0; geçmişte iki kayıt görünür | Normal |

## Kapsama özeti ve açık noktalar

Geçerli 22 kuralın (BR-06 kaldırıldı) tamamı en az bir senaryoyla kapsanıyor. Toplam 101 geçerli senaryo var (102 kimlik, KT-14 kaldırıldı).

| Kural | Senaryolar | Kural | Senaryolar |
| --- | --- | --- | --- |
| BR-00 | KT-09, 35, 80, 81, 96 | BR-12 | KT-41 – 51, 90 |
| BR-01 | KT-40, 75 | BR-13 | KT-52, 53, 61, 80, 94 |
| BR-02 | KT-01 – 03 | BR-14 | KT-54 – 59, 87 – 89, 94, 97 |
| BR-03 | KT-04 – 06 | BR-15 | KT-60, 61, 101, 102 |
| BR-04 | KT-07 – 13, 18, 39, 46, 57 | BR-16 | KT-62, 63, 95 – 98 |
| BR-05 | KT-64 – 68 | BR-17 | KT-28 – 31, 82 – 84, 92, 93 |
| BR-06 | Kaldırıldı | BR-18 | KT-32, 33 |
| BR-07 | KT-19 – 27, 91 | BR-19 | KT-71 – 74, 99 |
| BR-08 | KT-15 – 17 | BR-20 | KT-75 – 77 |
| BR-09 | KT-34 | BR-21 | KT-69, 70 |
| BR-10 | KT-40, 67 | BR-22 | KT-78, 79, 85, 86, 100 |
| BR-11 | KT-37 – 39 | | |

### Karar kaydı (v1.1): tablo yazılırken çıkan 6 nokta

v1.0'da açık bırakılan 6 nokta 26 Ekim 2026'da karara bağlandı ve SRS 1.0-taslak.4'e işlendi. 28 Ekim 2026'da hocaya soruldu; her maddenin sonunda hocanın cevabı var.

- [x] **1. "Vakit dar" eşiği (BR-17; KT-31, 82 – 84):** Karar: sabit 15 dk yerine fıkhi tanıma dayanan formül. Vakit dar = kalan süre < (kaza borçlarının farz ve vitir rekâtları + vaktin farz rekâtı) × 2 dk. Kalan süre vaktin sonuna, ikindide akşam kerahatinin başlangıcına göre ölçülür. Gerekçe: Hanefî kaynaklarda tertip, müstehap vakit kaza ile vakit namazını birlikte kılmaya yetmeyecek kadar darsa düşer; ikindinin müstehap vakti güneş sararmadan biter. **Hoca: hesap makul.**
- [x] **2. Yatsı vaktinde biten mazerette vitir (BR-12; KT-51):** Karar: yatsı farz olduysa vitir de açılır; vakit çıkarsa iki ayrı borç önerisi verilir. **Hoca: doğru, vitri de kılması gerekir.**
- [x] **3. BR-12 ve FR-MAZ-04'ün ifadesi:** Karar: "o vaktin namazı farz olur; vakit içinde kılınmazsa borç önerilir". **Hoca: kural doğru.**
- [x] **4. Baygınlıkta vakit sayımı (BR-22; KT-78, 79, 85, 86):** Karar: vakti çıkan farz namazlar sayılır (başlangıç < vakit bitişi ≤ bitiş); vitir sayılmaz; altıncı farz vaktin çıkmasıyla namazlar düşer. **Hoca: doğru; narkoz, ilaçla bilinç kaybı ve ağır hasta da aynı kurala tabi** (KT-100 eklendi).
- [x] **5. Hicrî takvim çeşidi (BR-19; KT-71):** Karar: Ümmü'l-Kurâ takvimi (Java `HijrahDate`), sonuç "yaklaşık" etiketiyle. Hocaya ayrıca sorulmadı; teknik bir karar.
- [x] **6. Alışkanlığın değişmesi (BR-14; KT-87 – 89):** v1.1 kararı (art arda iki dönem) **hoca görüşüyle değişti:** alışkanlık tek dönemde değişir; ayrıntısı aşağıdaki karar 8'de.

### Karar kaydı (v1.3): test verisi düzeltmesi

- [x] **7. KT-52'nin sınırı (BR-13; KT-52, KT-80):** "G1 08:00 → G3 07:59" aralığı 47 saat 59 dakikaydı; 72 saatlik eşiğin hemen altını sınamıyordu. Karar (27 Ekim 2026): bitiş G4 07:59 (71 saat 59 dk), kalem sayısı 18; KT-80 de buna göre güncellendi. Kural değişmediği için hoca kontrolü gerekmedi.

### Karar kaydı (v1.4): hoca görüşü (28 Ekim 2026)

Sorular ve cevaplar: hocaya-sorular.md ve fikhi-gereksinimler.md "Hoca görüşü kaydı". SRS karşılığı 1.0-taslak.7.

- [x] **8. Alışkanlık tek dönemde değişir (BR-14; KT-54, 58, 59, 87 – 89, 94):** Hoca: kadın ilk başta alışkanlığını girer; bir ay 8 gün sürerse alışkanlık 8 olur. KT-87 ve KT-89 onay sorusu yerine doğrudan güncelleme ve uygulama içi bilgi bekleyecek şekilde yeniden yazıldı. KT-88 artık "iki dönem eşit değil" durumunu değil yuvarlama sınırını sınıyor. KT-58 ve KT-59 eski iki dönem kuralının parçasıydı; yeni kuralın iki türetilmiş sonucunu sınayacak şekilde yeniden yazıldı. KT-94 eklendi. Ürün kararı: güncelleme sorulmadan yapılır, çünkü hocaya göre alışkanlık bir tercih değil gözlenen bir süredir; kullanıcı yine de "Değiştir" ile düzeltebilir (SRS BR-00 istisnası).
- [x] **9. Kesilip yeniden başlayan kanama (BR-16; KT-62, 95 – 98):** Hoca: 1.–3. gün kan, 4.–6. gün temiz, 7.–8. gün kan olursa 1.–8. günlerin hepsi âdet; 7.–8. günler kaza değil; 10 günü aşarsa sonrası istihâze. Uygulama kararı: başlangıçtan 240 saat içinde başlatılan yeni kanama önceki dönemi yeniden açar. Önceden onaylanmış borçlar sessizce silinmez, geri alınması sorulur (KT-96, BR-00). KT-62'nin beklentisi değişti: 240 saatten sonra ve 15 gün dolmadan gelen kan artık "sayılmayabilir" değil, istihâzedir.
- [x] **10. Kerahat 45 dk (BR-07; KT-20, 21, 24, 27, 83, 91):** Sabah ve akşam pencereleri 50'den 45 dk'ya indi. Sınır satırları yeni değerlere çekildi; akşam penceresinin alt sınırı için KT-91 eklendi. KT-83'teki ikindi ölçüsü 17:10'dan 17:15'e kaydı. KT-27 kullanıcı ayarı yerine `RuleSet` parametresini sınıyor (FR-AYR-03 kaldırıldı).
- [x] **11. Gusül aralığı 5–30 dk (BR-12; KT-49, 90):** KT-49'daki 30 dk artık izin verilen en yüksek değer; aralığı KT-90 sınıyor.
- [x] **12. Tertip ayrıntıları (BR-17; KT-92, 93):** Hoca: not yeterli; yalnız vitir borcu varsa not gerekmez; borç 6'nın altına inince tertip geri döner.
- [x] **13. Bulûğ yaşı (BR-19; KT-71, 99):** Hoca: bilmeyene tahmin ettirilebilir, bilmiyorsa ihtiyaten 14. KT-99 (kız, 14 kamerî yıl → 13 Şubat 2024) eklendi; tarih kodla hesaplandı.
- [x] **14. Asr-ı sânî (BR-06; KT-14):** Hoca: asr-ı evvel/sânî gösterilmez, yalnızca vakit saatleri. BR-06 ve KT-14 kaldırıldı.

### Karar kaydı (v1.5): ikinci hoca görüşmesi (28 Ekim 2026)

v1.4'te hocanın cevaplarından türetilen beş nokta soruldu (fikhi-gereksinimler.md, E1 – E5).

- [x] **15. 10 günü aşan dönem (BR-14; KT-55, 56, 58, 97, 98):** Hoca: alışkanlık 10 olarak değişir; 12 gün sürse bile 10 yazılır, çünkü 10'dan fazlası istihâzedir. v1.4'teki "değiştirmez" varsayımı yanlıştı. KT-58 yeni kuralı sınayacak şekilde yeniden yazıldı. Okuma: dönemin kendi hesabı dönem başındaki alışkanlıkla yapılır (hocanın 1.5 cevabı), 10 günlük alışkanlık sonraki dönemlerde geçerlidir. Hoca dönemin kendisinde de ilk 10 günü kastettiyse yalnızca KT-55, 58 ve 97'nin istihâze sayımları değişir.
- [x] **16. İlk dönem alışkanlığı belirler (BR-14; KT-59, 95):** Hoca: evet. Satırlardaki teyit notu kaldırıldı.
- [x] **17. Birleşik dönem 240 saati aşarsa (BR-16; KT-97):** Hoca soruyu uygulamanın kararına bıraktı. Karar: tek dönem kuralları uygulanır; alışkanlık günleri hayız, sonrası istihâze, dönem kapanınca alışkanlık 10. Gerekçe: iki kanama tek dönem sayıldığına göre tek dönemin kuralları geçerlidir; alışkanlık hesabı kılınacak namazı artırdığı için ihtiyata uygundur; alışkanlık girilmemişse sonuç hocanın 1.7 cevabıyla (10 günden sonrası istihâze) aynıdır.
- [x] **18. Bulûğ yaşı erkekte de 14 (BR-19; KT-71):** KT-71'in beklenen tarihi 2 Şubat 2025'ten 13 Şubat 2024'e değişti (kodla hesaplandı).
- [x] **19. Lohusalıkta ara temizlik (BR-15; KT-101, 102):** Hoca: 40 gün içindeki her şey lohusalık sayılır. Hayızdaki birleştirme nifasa 960 saatlik pencereyle uygulandı; kalem sayıları kodla sayıldı.

### Tablo yazılırken alınan tasarım kararları

Bunlar fıkhi değil ürün kararları; itirazın varsa değiştirilebilir.

- **KT-35:** Borç sıfırken "kaza kıldım" denirse önce ekleme sonra düşme önerilir; borç hiçbir zaman negatife düşmez.
- **KT-41:** Mazeret bittiğinde farz olan vakit için kullanıcıya "vakit çıkmadan kılabilirsin" bildirimi gönderilir. Mazeretle ilgili tüm bildirimlerin metni nötrdür; açıklama yalnızca uygulama içinde görünür (NFR-SEC-03, v1.2).
- **KT-62:** 15 gün dolmadan başlatılmak istenen mazeret engellenmez; uyarı gösterilir, varsayılan seçenek başlatmamaktır ve kullanıcı yine de başlatırsa kayıt "uyarılı" olarak işaretlenir.
- **KT-66:** Yatsı ve vitir için iki ayrı bildirim yerine tek hatırlatma gönderilir.
- **KT-101:** Nifasın 960 saati içinde başlatılan kanama, kullanıcı "Âdet" seçse bile nifasın devamı olarak kaydedilir; tür seçimi ikinci kez sorulmaz.
- **KT-87:** Alışkanlık güncellemesi bildirimle değil yalnızca uygulama içinde duyurulur (NFR-SEC-03).
- **KT-96:** Birleşen dönemde önceden onaylanmış borçlar tek ekranda, toplu onay bileşeniyle sorulur.

## Kaynaklar

- Hoca görüşü, 28 Ekim 2026 (hocaya-sorular.md)
- [Making Up Prayers Missed Due to Being Unconscious](https://seekersguidance.org/answers/hanafi-fiqh/making-up-prayers-missed-due-to-being-unconscious/) — SeekersGuidance (Merâki'l-Felâh ve İmdâdü'l-Fettâh'a atıfla)
- [Vaktinde kılınmayan namazların kazası nasıl yapılır?](https://www.islamveihsan.com/vaktinde-kilinmayan-namazlarin-kazasi-nasil-yapilir.html) — İslam ve İhsan (tertibi düşüren sebepler)
- [Âdet süresi 10 günden fazla sürenler ibadetlerini yerine getirmede nasıl hareket etmeli?](https://www.islamveihsan.com/adet-suresi-10-gunden-fazla-surenler-ibadetlerini-yerine-getirmede-nasil-hareket-etmeli.html) — İslam ve İhsan (Diyanet Fetva Kurulu'na atıfla)
- [What Prayer Should I Make Up after My Menstruation?](https://seekersguidance.org/answers/hanafi-fiqh/what-prayer-should-i-make-up-after-my-menstruation/) — SeekersGuidance (yatsı vaktinde biten hayız)

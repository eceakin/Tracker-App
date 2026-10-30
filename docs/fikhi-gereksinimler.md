# Fıkhi Gereksinimler — Namaz & Kaza Takip

Oct 26, 2026 · @ece · Hoca görüşüyle güncellendi: 28 Ekim 2026

## Özet

İlk sürüm yalnızca Hanefî mezhebine göre çalışır; Şafiî, Mâlikî ve Hanbelî sonraki güncellemelerde eklenecek. Mezhep, uygulamada dört şeyi belirliyor: günlük takip edilen namaz sayısı (Hanefî'de vitir dahil 6), vakitlerin bitiş anı (dolayısıyla "kaçırıldı" ve kaza eşiği), mazeret modunun süre kuralları ve kaza borcunun nasıl kılınacağı (tertip, kerahat vakitleri, seferde rekât sayısı).

**28 Ekim 2026 hoca kontrolü.** Bu belgedeki kurallar hocaya danışıldı (hocaya-sorular.md). Kuralların büyük çoğunluğu olduğu gibi onaylandı. Değişen beş nokta: vakitler yalnızca Diyanet'e göre gösterilir ve asr-ı sânî seçeneği kaldırıldı; kerahat süreleri üçü için de 45 dk; gusül süresi aralığı 5–30 dk; âdet alışkanlığı tek bir dönemde değişir; arada kesilip yeniden başlayan kanama 10 gün içinde tek dönem sayılır. Aynı gün ikinci bir görüşmede cevaplardan türetilen beş nokta da netleşti: 10 günü aşan dönem alışkanlığı 10 gün yapar, bulûğ yaşı bilinmiyorsa iki cinsiyette de 14 önerilir ve 40 gün içindeki her kan lohusalıktır. Ayrıntılar son bölümdeki "Hoca görüşü kaydı"nda.

Tasarım ilkeleri:

1. **Uygulama fetva vermez, kayıt tutar.** Her mezhep kuralı kaynaklı bir varsayılan olarak uygulanır; kullanıcı her zaman elle düzeltebilir.
2. **Şüphede ihtiyat.** Bir namazın kazaya kalıp kalmadığı belirsizse uygulama otomatik borç yazmaz, kullanıcıya sorar.
3. **Hassas hâller gizli kalır.** Mazeret ve sağlık bilgisi yalnızca cihazda ve şifreli yedekte tutulur.
4. **Kural motoru mezhepten bağımsız yazılır.** Mezhep, bir parametre setidir. İlk sürümde yalnızca Hanefî seti doludur; diğer mezhepler kod yapısı değişmeden eklenir.
5. **Kaynak gösterimi.** Uygulama içinde kaynak olarak Din İşleri Yüksek Kurulu fetvaları gösterilebilir (hoca onayı, 28 Ekim 2026). Uygulama "bu konuda bir hocaya danışın" türünde bir yönlendirme göstermez; hocanın görüşüne göre şimdilik gerekmiyor.

## Onboarding

İlk açılışta iki zorunlu ve iki isteğe bağlı soru sorulur; hepsi sonradan ayarlardan değiştirilebilir. Mezhep ilk sürümde sorulmaz, yalnızca bilgi olarak gösterilir.

| Sıra | Soru | Seçenekler | Neyi belirler |
| --- | --- | --- | --- |
| 1 | Mezhep | Soru yok; "Bu sürüm Hanefî mezhebine göre çalışır" bilgisi | Diğer mezhepler eklendiğinde bu adım seçime dönüşür; o zaman "Emin değilim" seçeneği kısa bir açıklama gösterip seçim ister |
| 2 | Cinsiyet | Kadın · Erkek | Kadında mazeret modu (hayız, nifas); erkekte cuma günü öğle yerine "Cuma" seçeneği |
| 3 | Konum | Diyanet il/ilçe listesi | Vakitler ve kıble |
| 4 | Kaza borcu (isteğe bağlı) | Şimdi gir · Hesapla · Sonra | Başlangıç borcu; yeni başlayanı büyük bir rakamla korkutmamak için atlanabilir |
| 5 | Hatırlatma (isteğe bağlı) | Vakit başına aç/kapat | Bildirim ayarları |

Diğer mezhepler eklendiğinde mezhep değişikliği geçmişi yeniden hesaplamamalı. Bu yüzden ilk sürümden itibaren her gün kaydı, o gün geçerli olan mezhep ayarının anlık kopyasıyla saklanır; yeni kural yalnızca değişiklik tarihinden sonrasına uygulanır.

## Mezhep karşılaştırma matrisi

Kural motoruna girecek 14 hüküm aşağıda. İlk sürümde yalnızca Hanefî sütunu uygulanır; diğer sütunlar sonraki güncellemeler için referans olarak duruyor. "Doğrulanacak" yazan hücreler, açtığım kaynaklarda o mezhep için net ifade bulunamayan ya da ikincil kaynaktan gelen bilgilerdir. Hoca kontrolü yalnızca Hanefî sütununu ve uygulamadaki karşılıklarını kapsadı.

| Konu | Hanefî | Şafiî | Mâlikî | Hanbelî |
| --- | --- | --- | --- | --- |
| Vitir | Vâcip; günlük takipte 6. namaz, kaza edilir | Müekked sünnet; kazası müstehap | Müekked sünnet; sabah kılındıktan sonra kaza edilmez | Ağırlıklı görüş sünnet |
| Öğlenin sonu / ikindinin başı | Ebû Hanîfe: gölge 2 misli (asr-ı sânî). İmameyn: 1 misli (asr-ı evvel). **Uygulamada Diyanet vakti esas alınır (hoca görüşü)** | Asr-ı evvel | Asr-ı evvel | Asr-ı evvel |
| Akşamın sonu / yatsının başı | Ebû Hanîfe: beyaz şafağın kaybolması. İmameyn: kızıllığın kaybolması. **Uygulamada Diyanet'in yatsı vakti esas alınır (hoca görüşü)** | Kızıllığın kaybolması | Kızıllığın kaybolması | Kızıllığın kaybolması |
| Kerahat vakitlerinde (doğuş, istivâ, batış) kaza | Kılınmaz; tek istisna o günün ikindisi | Kılınır | Kılınır (istivâyı kerahat saymaz) | Kılınır |
| Tertip (kazaların sırası) | Farz borcu 6 vakitten azsa gerekli (vitir sayılmaz); borç 6'nın altına inince yeniden gerekli olur | Sünnet | Doğrulanacak | Borç ne kadar olursa olsun gerekli |
| Seferde kaçan 4 rekâtlının kazası | Kaçtığı şekliyle: seferde kaçan, evde de 2 rekât | Kazanın kılındığı yer ve zaman esas | Doğrulanacak | Kazanın kılındığı yer ve zaman esas |
| Seferîlik | \~90 km; 15 gün kalma niyeti bitirir; kısaltma vâcip | \~88 km; 4 günden fazla kalma bitirir; kısaltma ruhsat | \~88 km; 4 günden fazla; kısaltma ağırlıkla azîmet | \~88 km; 4 günden fazla veya 20 vakit; ruhsat |
| Cem (iki namazı birleştirme) | Arafat ve Müzdelife dışında yok | Seferde var | Var | Var |
| Hayız süresi | En az 3, en çok 10 gün; 10 gün içindeki ara temizlik hayızdan sayılır | En az 1 gün 1 gece, en çok 15 gün | Alt sınır yok, en çok 15 gün | En az 1 gün 1 gece, en çok 15 gün  |
| İki hayız arası en az temizlik | 15 gün | 15 gün | 15 gün | 13 gün  |
| Nifas (lohusalık) en çok | 40 gün; aşan kısım istihâze; 40 gün içindeki ara temizlik de nifastır | 60 gün | 60 gün | 40 gün |
| İstihâzede abdest | Her vakit için bir abdest | Her farz için yeni abdest | Her farz için yeni abdest | Her vakit için bir abdest |
| Uzun baygınlık / akıl kaybı | Süresince 6 farz vaktin çıkarsa kaza yok; 5 veya daha azsa kaza (narkoz ve ilaçla bilinç kaybı dahil) | Bir vaktin tamamı boyunca baygınsa o vakit kaza edilmez | Doğrulanacak | Ağırlıklı görüş: kaza gerekir |
| Bulûğ (kaza hesabının başlangıcı) | Alt sınır erkek 12, kız 9. Üst sınır Ebû Hanîfe'ye göre erkek 18, kız 17; çoğunluk ve Mecelle 15. **Uygulama, yaşı bilinmeyen ve tahmin edilemeyen kullanıcıya ihtiyaten 14 önerir (hoca görüşü)** | Üst sınır 15 | Üst sınır 15 | Üst sınır 15 |

Türkiye pratiği için önemli not: Diyanet takvimi ikindiyi asr-ı evvele göre veriyor. Hocanın görüşü: vakitler İmam-ı Âzam'ın görüşüne göre değil, Diyanet'in vaktine göre ayarlanır; kullanıcı öğleyi ikindi vakti girene kadar kılabilir.

## Vakitler ve günlük takip mantığı

Temel kural: vakit sınırları Diyanet vakitleridir; öğle, öğle ile ikindi vakti arasıdır. Kullanıcıya asr-ı evvel, asr-ı sânî gibi kavramlar gösterilmez, yalnızca vakitlerin saatleri gösterilir. Hatırlatma vakit sonundan önce kurulur ve borç hiçbir zaman kullanıcı onayı olmadan yazılmaz.

| Namaz | Vakit girişi (işaretleme açılır) | Kaçırıldı sayılma anı |
| --- | --- | --- |
| Sabah | İmsak | Güneşin doğuşu |
| Öğle / Cuma | Öğle | İkindinin girişi (Diyanet) |
| İkindi | İkindi (Diyanet) | Güneşin batışı |
| Akşam | Akşam | Yatsının girişi (Diyanet) |
| Yatsı | Yatsı | İmsak |
| Vitir | Yatsı işaretlendikten sonra | İmsak; kılınmazsa borç önerilir |

Günlük takipteki özel durumlar:

1. **Gelecek vakit kilidi:** Vakti girmemiş namaz işaretlenemez, çünkü vaktinden önce kılınan namaz geçerli değildir.
2. **Cem:** Hanefî'de cem yoktur; gelecek vakit kilidinin seferde de istisnası olmaz. Seferî modda cem istisnası diğer mezheplerle birlikte eklenecek.
3. **Cuma:** Erkek kullanıcıda cuma günü öğle kutusu "Öğle / Cuma" olur. Cuma kılınamazsa öğle kılınır; borca öğle olarak yazılır. Kadında cuma isteğe bağlı bir seçenektir.
4. **Kerahat vakitleri:** Hanefî kullanıcıda güneşin doğuşu, istivâ ve batışında "kaza kıl" eylemi kapatılır ve kısa bir uyarı gösterilir. Süreler üçü için de 45 dk'dır (hoca görüşü): güneş vaktinden sonra 45 dk, öğle vaktinden önce 45 dk, akşam vaktinden önce 45 dk. Süreler kural motorunda parametre olarak tutulur ama kullanıcı ayarı değildir. Akşam kerahatinde yalnızca o günün kılınmamış ikindisinin farzı güneş batana kadar işaretlenebilir. Kaza kapalıdır; vitir kazası da buna dahildir. Sabah farzından sonra güneş doğana kadar ve ikindi farzından sonra kerahate kadar kaza kılınabilir.
5. **Nafile namaz vakitleri (referans):** Hocanın bildirdiğine göre sabah vaktinde (imsak ile güneşin doğuşu arası) sabahın sünneti dışında nafile kılınmaz; sabahın farzından sonra güneş doğana kadar ve ikindinin farzından sonra güneş batana kadar nafile kılınmaz. Uygulama v1.0'da nafile takip etmediği için bu kuralın uygulamada karşılığı yoktur; nafile takibi (P2) eklendiğinde kullanılacak.
6. **Bayram namazı:** Takip edilebilir ama borca yazılmaz; kazası yoktur.
7. **Yüksek enlem (Avrupa yazı vb.):** Uygulama kendi hesap yapmaz, Diyanet'in o şehir için yayımladığı vakitleri kullanır.
8. **Asr-ı sânî ve işâ-i sânî:** Hesaplanmaz ve gösterilmez (hoca görüşü). Diyanet API'si zaten yalnızca 6 temel vakti döndürüyor. Adhan kütüphanesi yalnızca Diyanet verisine ulaşılamadığında "tahmini" vakit hesabı için kullanılır.

## Mazeretler

Namazı kaza gerektirmeyecek şekilde düşüren mazeretler yalnızca üç grup: hayız, nifas ve akıl kaybı/uzun baygınlık/ağır hastalık (süresince 6 farz vaktin çıkarsa). Yolculuk, hastalık ve istihâze gibi diğer hâller namazı düşürmez; yalnızca kılınış şeklini değiştirir. Uyku ve unutma ise kazayı gerektirir.

| Hâl | Namaz düşer mi? | Kaza | Uygulamada karşılığı |
| --- | --- | --- | --- |
| Hayız (âdet) | Evet | Yok (oruç kaza edilir) | Mazeret modu: "Âdet" |
| Nifas (lohusalık); organları belirmiş ceninin düşüğü dahil | Evet | Yok | Mazeret modu: "Lohusalık", ayrı süre sınırı |
| İstihâze: süre dışı kan, hamilelikte kanama, hastalık kanaması | Hayır | Kılınmazsa kaza | Mazeret sayılmaz; mazeret modu süre aşımında uyarı verir |
| Sürekli özür (idrar kaçırma, kapanmayan yara vb.) | Hayır | Kılınmazsa kaza | Mazeret sayılmaz; yalnızca bilgi notu |
| Baygınlık, koma, akıl kaybı; narkoz ve ilaçla bilinç kaybı | Süresince 6 farz vaktin çıkarsa (vitir sayılmaz) | 5 veya daha az vakitse kaza | "Sağlık nedeniyle kılınamadı" kaydı; borç önerisi vakti çıkan farz sayısına göre (BR-22) |
| Başını bile oynatamayacak ağır hastalık | Süresince 6 farz vaktin çıkarsa (vitir sayılmaz) | 5 veya daha az vakitse kaza | Aynı kayıt |
| Hastalık (genel) | Hayır; oturarak veya îmâ ile kılınır | Kılınmazsa kaza | Mazeret sayılmaz |
| Yolculuk | Hayır; dört rekâtlılar kısaltılır (vâcip), cem yok | Kılınmazsa kaza | Seferî modu |
| Uyku, unutma | Hayır; günah yok | Kaza gerekir | Normal "kaçırıldı" akışı |
| Bilerek terk | Hayır | Kaza ve tövbe gerekir (çoğunluk) | Normal "kaçırıldı" akışı; yargılayıcı dil kullanılmaz |
| Ergenlik öncesi, Müslüman olmadan önceki dönem | Yükümlülük yok | Yok | Kaza hesaplayıcısının başlangıç tarihi |

## Mazeret modu kuralları

Mazeret modu basit bir "duraklat" düğmesi değil; Hanefî süre sınırlarına göre kural kontrolü yapmalı. Ancak hiçbir zaman sessizce borç yazmaz, her belirsiz durumda kullanıcıya sorar.

1. **Başlangıç:** Kullanıcı tarihi ve içinde bulunduğu vakti seçer. Vakit girdikten sonra, namaz kılınmadan başlayan âdette o vaktin kazası gerekmez. Âdetin vaktin sonuna yakın başlaması bu hükmü değiştirmez (hoca görüşü).
2. **Bitiş:** Kullanıcı bittiği vakti seçer. Bittiği vaktin namazının farz olup olmadığı kanamanın kaçıncı saatte kesildiğine bağlıdır. 10 günden (240 saat) önce kesilirse vaktin çıkmasına gusül + iftitah tekbiri kadar süre kalmışsa o namaz farz olur (vakit içinde kılınabilir; kılınmazsa borç önerilir), daha az kalmışsa olmaz. Tam 10 günde kesilirse yalnızca iftitah tekbiri kadar süre kalması yeterlidir. Lohusalıkta aynı kural 40 gün (960 saat) sınırıyla uygulanır. Bu hesap için uygulama başlangıç ve bitişin saatini kaydeder; gusül süresi varsayılan 15 dk'dır, kullanıcı 5–30 dk arasında ayarlar (hoca görüşü). Uygulama bunu nötr bir bildirimle haber verir ("İkindi vakti çıkmadan kılabilirsin"); "bu vaktin namazı sana farz oldu" açıklaması yalnızca uygulama içinde görünür; vakit işaretlenmeden çıkarsa borç önerir ve kullanıcı onaylarsa yazar. Mazeret yatsı vaktinde biterse ve yatsı farz olduysa vitir de kılınmalıdır (hoca görüşü); ikisi birlikte açılır.
3. **Asgari süre kontrolü:** Mazeret, asgari süreden (3 gün, 72 saat) kısa sürerse o kan hayız sayılmaz. Uygulama "bu günlerde kılınmayan namazlar kazaya kalmış olabilir, borca ekleyeyim mi?" diye sorar. Hamilelikteki kanama hayız sayılmaz.
4. **Azami süre kontrolü:** 10 gün (240 saat) dolunca nötr metinli bir bildirim gider, açıklama uygulama içinde görünür; aşan günler istihâze sayılır ve namaz kılınması gerekir. Nifasta sınır 40 gündür; 40 günü aşan kısım istihâzedir. Önceki lohusalık süresi dikkate alınmaz; uygulama lohusalık alışkanlığı sormaz. Lohusalığın başlangıcından itibaren 40 gün içindeki her kan lohusalıktır: kan kesilip 40 gün dolmadan yeniden başlarsa aradaki temiz günler de lohusalıktan sayılır (hoca görüşü). Uygulama bu kanamayı kullanıcının seçtiği türden bağımsız olarak lohusalığın devamı olarak kaydeder.
5. **Âdet alışkanlığı:** Kanama alışkanlık gününü aşar ama 10 gün dolmadan keserse tamamı hayızdır. 10 günü aşarsa alışkanlık günleri hayız, sonrası istihâzedir ve o günlerde kılınmayan namazlar kaza edilir; alışkanlık girilmemişse 10 gün esas alınır. Bu yüzden profilde isteğe bağlı "genellikle kaç gün sürer" alanı tutulur.
6. **Alışkanlığın değişmesi:** Âdet süresi kadının elinde olmayan bir şekilde değişebilir. Kadın ilk başta alışkanlık süresini girer; bir dönem farklı sürerse (10 gün içinde kalmak şartıyla) yeni süre alışkanlık olur. Örnek: alışkanlık 6 gün, bu ay 8 gün sürdü → alışkanlık 8 gün olarak güncellenir (hoca görüşü). Uygulama dönemin bitişi kaydedildiğinde alışkanlığı bu süreye günceller ve bunu uygulama içinde bildirir; kullanıcı değeri her zaman elle değiştirebilir. Alışkanlık girilmemişse ilk geçerli dönem alışkanlığı belirler (hoca görüşü). 10 günü aşan dönem alışkanlığı 10 gün yapar; kanama 12 gün sürse bile 10 yazılır, çünkü 10 günden fazlası istihâzedir (hoca görüşü). O dönemin kendi hesabı 5. maddeye göre, dönem başındaki alışkanlıkla yapılır; yeni değer sonraki dönemlerde kullanılır. 72 saatten kısa dönem hayız sayılmadığı için alışkanlığı değiştirmez.
7. **Arada kesilip yeniden başlayan kanama:** Kanama kesilir ve ilk kanamanın başlangıcından itibaren 10 gün dolmadan yeniden başlarsa iki kanama ve aradaki temiz günler tek bir dönem sayılır; aradaki günler de hayızdır ve o günlerde kılınmayan namazlar kaza edilmez (hoca görüşü). Örnek: 1.–3. gün kan, 4.–6. gün temiz, 7.–8. gün kan → 1.–8. günlerin hepsi hayız. Toplam süre 10 günü aşarsa 5. maddedeki hesap tüm döneme uygulanır: alışkanlık günleri hayız, sonrası istihâze; dönem kapanınca alışkanlık 10 gün olur (6. madde). Bu son nokta hocanın uygulamaya bıraktığı bir karardır; gerekçe: iki kanama tek dönem sayıldığına göre tek dönemin kuralları geçerlidir ve alışkanlık hesabı kılınacak namazı artırdığı için ihtiyata uygundur. Alışkanlık girilmemiş kadında sonuç hocanın 1.7'deki "10 günden sonrası istihâze" cevabıyla aynıdır. Uygulama ikinci kanamayı yeni bir dönem olarak değil, önceki dönemin devamı olarak kaydeder; aradaki günler için bekleyen borç önerileri kapanır, önceden onaylanmış borçların geri alınması kullanıcıya sorulur.
8. **Temizlik aralığı kontrolü:** İki hayız arasında en az 15 gün temizlik olur (hoca görüşü). İlk kanamanın başlangıcından 10 gün geçtikten sonra ve önceki dönemin bitişinden 15 gün dolmadan başlayan kanama hayız sayılmaz, istihâzedir; namazlar kılınır, kılınmazsa kaza edilir. Uygulama bu durumda uyarı verir ve mazeret başlatmaz; kullanıcı yine de kaydetmek isterse kayıt "uyarılı" olarak tutulur.
9. **Seri ve borç:** Mazeret günleri seriyi bozmaz, borca eklenmez ve takvimde nötr bir renkle görünür.
10. **Gizlilik:** Widget, bildirim ve kilit ekranında "âdet" ya da "mazeret" kelimesi görünmez; hatırlatmalar sessizce durur. Veri yalnızca cihazda ve şifreli yedekte tutulur.
11. **Oruç bağlantısı:** Ramazan'a denk gelen mazeret günleri ileride oruç kazası özelliğine (P2) aktarılabilir.

## Kaza kuralları

Kaza borcu vakit bazında tutulur ve kılınan kaza kullanıcının seçtiği vakitten düşer; aşağıdaki kurallar bu modelin üstüne eklenir.

**Neler borca yazılır**

- Beş vakit farz; Hanefî'de ayrıca vitir.
- Sünnetler genelde kaza edilmez. Hanefî'de tek istisna: aynı gün öğleden önce kaza edilen sabah namazı sünnetiyle birlikte kılınır.
- Cuma kaçırılırsa borç öğle olarak yazılır. Bayram namazının kazası yoktur.

**Borç hesaplayıcı (P1)**

```latex
\text{Borç} = (\text{Gün}_{\text{bulûğ} \to \text{düzenli başlangıç}} - \text{Mazeret günleri} - \text{Kılındığı kesin bilinen günler}) \times n
```

Burada n = 6'dır (beş vakit farz + vitir). Hoca formülü onayladı.

- **Başlangıç (bulûğ):** Kullanıcı ilk alametlerin görüldüğü yaşı girer. Bilmiyorsa önce tahmin etmesi istenir. Tahmin de edemiyorsa ihtiyaten kızda da erkekte de 14 kamerî yıl önerilir (hoca görüşü). Yaşlar kamerî yılla hesaplanır. Uygulama tarihi yaklaşık formülle değil, doğum tarihini hicrî takvime çevirip yaşı ekleyerek tam hesaplar. Örnek (15 Temmuz 2010 doğumlu, Ümmü'l-Kurâ takvimi): 14 kamerî yıl → 13 Şubat 2024 (miladî 14 yıldan 153 gün önce). Farklı hicrî takvimler 1–2 gün farklı sonuç verebilir. Alt sınır erkekte 12, kızda 9; bunun altı kabul edilmez.
- **Kılındığı bilinen günler:** Yalnızca kesin bilinen günler düşülür. Kıldığından emin olmadığı günler kesinlikle borca katılır (hoca görüşü). Ekran bunu açıkça söyler.
- **Kadınlarda mazeret düşümü:** "Genellikle kaç gün sürer" alanı × ay sayısı ve bilinen lohusalık dönemleri düşülür.
- **Müslüman olmadan önceki dönem:** Sonradan Müslüman olan kullanıcıda borç, Müslüman olduğu tarihten başlar (hoca onayı).
- **Sonuç bir tahmindir:** Ekranda böyle yazılır ve elle düzeltilebilir.

**Kılarken uygulanacak kurallar**

1. **Tertip:** Farz borcu 6 vakitten azsa (vitir sayılmaz) önce kaza, sonra vaktin namazı kılınır (vakit darsa vaktin namazı önce; vakit, kaza borçları ile vaktin farzını birlikte kılmaya yetmiyorsa dar sayılır, ikindide ölçü güneşin sararmasıdır; hesap rekât başına 2 dk). Uygulama bu kullanıcıya vaktin namazını işaretlerken kısa bir bilgi notu gösterir; not yeterlidir, işaret engellenmez (hoca görüşü). Vakit darsa not gösterilmez. Farz borcu yok, yalnızca vitir borcu varsa not gösterilmez (hoca görüşü). Borç 6'yı geçip tertip düştükten sonra kaza kılınarak 6'nın altına inerse tertip yeniden geçerli olur ve not yeniden gösterilir (hoca görüşü). Otomatik eklenen borçlar tarihleriyle saklanır ki en eskisi önerilebilsin.
2. **Kerahat:** Üç kerahat vaktinde (45'er dk) kaza kılınmaz; "kaza kıl" eylemi kapanır.
3. **Seferde kaçanın rekâtı:** Seferî modda kaçan 4 rekâtlı namaz borca "seferde kaçtı" etiketiyle yazılır. Hanefî'de kaza, kaçtığı şekliyle kılınır: seferde kaçan her yerde 2, evde kaçan seferde de 4 rekât. Uygulama kaza ekranında rekât sayısını buna göre gösterir. Sefer mesafesi yaklaşık 90 km, 15 gün kalma niyeti seferîliği bitirir (hoca onayı).
4. **Kaza varken nafile:** Uygulama bu konuda hiçbir şey göstermez ve kullanıcıyı engellemez (hoca görüşü: uygulamada gerek yok). Şafiî ve Hanbelî'deki kısıtlamalar o mezheplerle birlikte değerlendirilecek.
5. **Geciktirmemek:** Kaza borcunu özürsüz geciktirmek uygun görülmüyor. Uygulama yargılamadan bir "kaza planı" sunabilir: "günde X kaza ile tahmini bitiş tarihi".
6. **Niyet:** Kaza ekranında "üzerimdeki ilk (veya son) öğle namazının kazası" şeklinde niyet hatırlatması gösterilir; hoca bu niyet şeklini onayladı (P1).

## Özellik listesine yansıması

Fıkhi gereksinimler MVP'ye iki yeni altyapı parçası ekliyor (onboarding ve kural motoru) ve mazeret modunun eforunu S'den M'ye çıkarıyor.

| Değişiklik | Öncelik | İlgili özellik |
| --- | --- | --- |
| Cinsiyet soran onboarding ve Hanefî bilgilendirmesi | P0 (yeni) | — |
| Mezhep parametre setiyle çalışan kural motoru (ilk sürümde yalnız Hanefî seti); her güne ayarın anlık kopyası | P0 (yeni) | Tüm takip ve kaza özellikleri |
| Vitir kutusu, erkekte "Öğle / Cuma" seçeneği | P0 | #1 Günlük takip |
| "Kaçırıldı" eşiğinin Diyanet vakitlerine göre hesaplanması; hatırlatma ve kaza eşiğinin ayrılması | P0 | #4 Kazaya ekleme, #9 Hatırlatma |
| Mazeret modunda başlangıç/bitiş vakti, süre kontrolleri, alışkanlığın tek dönemde güncellenmesi, kesilip yeniden başlayan kanamanın birleştirilmesi, lohusalık türü, gizlilik | P0 (efor M) | #8 Mazeret modu |
| Borç hesaplayıcıda bulûğ yaşı (tahmin, yoksa 14) ve mazeret günü düşümü | P1 | #3 Borç hesaplayıcı |
| Kerahat vaktinde kaza eyleminin kapanması | P1 | #6 Kerahat uyarısı |
| Seferî modu: rekât etiketi | P1 (yeni) | — |
| Tertip bilgi notu ve tarihli borç kaydı | P1 (yeni) | #3 Kaza sayacı |
| Kaza ekranında niyet hatırlatması | P1 (yeni) | #3 Kaza sayacı |
| "Sağlık nedeniyle kılınamadı" kaydı | P1 (yeni) | — |
| Kaza planı (günde X kaza → tahmini bitiş) | P2 (yeni) | — |
| Şafiî, Mâlikî ve Hanbelî desteği: onboarding'de mezhep seçimi, cem istisnası, mezhebe göre mazeret süreleri ve kaza kuralları | Sonraki güncellemeler | Kural motoru, Onboarding |

Asr-ı sânî seçeneği (önceden P1, #5 Vakitler) hoca görüşüyle kaldırıldı.

## Hoca görüşü kaydı (28 Ekim 2026)

Sorular hocaya-sorular.md'de. Hoca genel olarak uygulamanın tüm kurallarının doğru olduğunu söyledi. Her soru için cevap ve bu belgedeki karşılığı:

| Soru | Hocanın cevabı (özet) | Sonuç |
| --- | --- | --- |
| 1.1 Vakit içinde başlayan âdet | Doğru; vaktin sonuna yakın başlaması hükmü değiştirmez | Değişiklik yok |
| 1.2 Âdetin bittiği vaktin namazı | Kural doğru; gusül için 15 dk ideal; aralık 5–30 dk olmalı; lohusalık sınırı doğru | Gusül aralığı 5–60 → 5–30 dk |
| 1.3 Yatsıda biten âdette vitir | Doğru; vitri de kılması gerekir | Onaylandı |
| 1.4 Üç günden kısa kanama | Hepsi doğru | Onaylandı |
| 1.5 On günü aşan kanama | Hepsi doğru; 10 günü geçerse istihâze, namaz kılınır | Onaylandı |
| 1.6 Alışkanlığın değişmesi | Kadın ilk başta alışkanlığını girer; bir ay 8 gün sürerse alışkanlık 8 olur | İki dönem kuralı → tek dönem |
| 1.7 Kesilip yeniden başlayan kanama | 1.–8. günlerin hepsi âdet; 7.–8. günler kaza değil; 10 günü aşarsa sonrası istihâze | Dönem birleştirme eklendi |
| 1.8 En az temizlik | 15 gün doğru | Onaylandı; 10 günden sonra gelen kan istihâze |
| 1.9 Lohusalık | 40 gün en çok; aşarsa istihâze | Lohusalık alışkanlığı sorulmaz |
| 2.1–2.3 Baygınlık, narkoz, ağır hasta | Hepsi aynı kurala tabi; sayım doğru | Onaylandı; narkoz açıkça yazıldı |
| 3.1 Öğlenin sonu | Vakitler Diyanet'e göre; öğle ikindi okunana kadar kılınabilir | Onaylandı |
| 3.2 İkindinin başlangıcı | Asr-ı evvel/sânî gösterilmez, yalnızca vakit saatleri | Asr-ı sânî seçeneği kaldırıldı |
| 3.3 Akşamın sonu | Diyanet'in yatsı vakti kabul | Onaylandı |
| 3.4 Vitrin vakti | Doğru | Onaylandı |
| 3.5 Kerahat | 45 dk hepsi için yeterli; nafile kılınmayan vakitler bildirildi | 50/45/50 → 45/45/45; nafile kuralı referans olarak eklendi |
| 4.1 Borca yazılanlar | Doğru | Onaylandı |
| 4.2 Tertip | Not yeterli; yalnız vitir borcunda not gerekmez; borç 6'nın altına inince tertip geri döner | İki ayrıntı eklendi |
| 4.3 Vaktin darlığı | Hesap makul | Onaylandı |
| 4.4 Seferde kaçan | Doğru | Onaylandı |
| 4.5 Kaza varken nafile | Uygulamada bir şey söylenmesin | Değişiklik yok |
| 4.6 Niyet | Niyet şekli doğru, gösterilebilir | Niyet hatırlatması eklendi (P1) |
| 5.1 Bulûğ yaşı | Doğru; bilmeyene tahmin ettirilebilir, bilmiyorsa ihtiyaten 14 | Tahmin adımı; kızda 14 kamerî yıl |
| 5.2 Borç formülü | Doğru; emin olunmayan günler kesinlikle borca katılır | İfade netleşti |
| 5.3 Müslüman olmadan önceki dönem | Doğru | Onaylandı |
| 6.1 Dil ve sınır | Yaklaşım yeterli; "hocaya danışın" alanı şimdilik gerekmez; Diyanet fetvaları kaynak gösterilebilir | Danışma uyarısı kaldırıldı; kaynak gösterimi ilkesi eklendi |

**İkinci görüşme (28 Ekim 2026): türetilmiş noktalar.** İlk cevaplardan türetilen beş nokta hocaya ayrıca soruldu:

| Soru | Hocanın cevabı | Sonuç |
| --- | --- | --- |
| E1. 10 günü aşan dönem alışkanlığı değiştirir mi? | Alışkanlığı 10 gün olarak değiştirir; 12 gün sürse bile 10 yazılır, çünkü 10'dan fazlası istihâzedir | Türetilen "değiştirmez" varsayımı yanlıştı; alışkanlık 10 olur |
| E2. Alışkanlık girilmemişse ilk dönem alışkanlık olur mu? | Evet | Onaylandı |
| E3. Birleşen dönem 10 günü aşınca alışkanlık hesabı tüm döneme uygulanır mı? | Hoca soruyu uygulamanın kararına bıraktı | Ürün kararı: evet, tek dönem kuralları uygulanır (Mazeret modu kuralları, 7. madde) |
| E4. Bulûğ yaşını bilmeyen erkeğe 14 mü 15 mi? | 14 | İki cinsiyette de 14 |
| E5. Lohusalıkta 40 gün dolmadan kesilip yeniden başlayan kan? | 40 gün içindeki her şey lohusalık sayılır | Lohusalıkta da dönem birleştirme eklendi |

**Okuma notu (E1).** Hocanın 1.5 cevabı, 10 günü aşan kanamada o dönemin hesabının önceki alışkanlıkla yapıldığını onaylıyor (alışkanlık 6 ise ilk 6 gün hayız). E1 cevabı ise dönemden sonra alışkanlığın 10 olacağını söylüyor. İki cevap birlikte şöyle uygulandı: dönemin kendi hesabı önceki alışkanlıkla, sonraki dönemler 10 günlük alışkanlıkla. Hoca E1'de dönemin kendisinin de ilk 10 gününün hayız sayılacağını kastettiyse yalnızca BR-14'ün bir cümlesi ile KT-55, KT-58 ve KT-97 değişir.

## Kaynaklar

- Hoca görüşü, 28 Ekim 2026 (sorular: hocaya-sorular.md)
- [Müslüman'dan namaz ne zaman ve hangi hâllerde düşer?](https://kurul.diyanet.gov.tr/tr/fetva/muslumandan-namaz-ibadeti-ne-zaman-ve-hangi-hallerde-duser/0193c42d-4d08-7d76-f3e0-b064efb0196f) — Din İşleri Yüksek Kurulu
- [Namazların Kazası](https://kurul.diyanet.gov.tr/tr/fetvalar/namaz/e881815b-9715-405a-8bdf-aeb000d961ec/namazlarin-kazasi/01933665-b68b-7dfe-d55a-1c5b2416a9ba) — Din İşleri Yüksek Kurulu
- [Vakit girdikten sonra namazı kılmadan âdet görmeye başlayan kadın](https://kurul.diyanet.gov.tr/tr/fetva/namaz-vakti-girdigi-halde-namazi-kilmadan-adet-gormeye/0193c42d-53d9-7bd6-ac1d-b9dfbec2f705) — Din İşleri Yüksek Kurulu
- [Hamile bir kadından gelen kanın hükmü](https://kurul.diyanet.gov.tr/Cevap-Ara/119/hamile-bir-kadindan-gelen-kanin-hukmu-nedir-bu-esnada-ibadet-yapilabilir-mi) — Din İşleri Yüksek Kurulu
- [Kazâ](https://islamansiklopedisi.org.tr/kaza--ibadet) — TDV İslâm Ansiklopedisi
- [Vakit](https://islamansiklopedisi.org.tr/vakit) — TDV İslâm Ansiklopedisi
- [Vitir Namazı](https://islamansiklopedisi.org.tr/vitir-namazi) — TDV İslâm Ansiklopedisi
- [Sefer](https://islamansiklopedisi.org.tr/sefer--fikih) — TDV İslâm Ansiklopedisi
- [Hayız](https://islamansiklopedisi.org.tr/hayiz) — TDV İslâm Ansiklopedisi
- [Nifas](https://islamansiklopedisi.org.tr/nifas) — TDV İslâm Ansiklopedisi
- [Bulûğ](https://islamansiklopedisi.org.tr/bulug) — TDV İslâm Ansiklopedisi
- [Diyanet takvimi ikindiyi hangi vakte göre belirlemiştir?](https://sorularlaislamiyet.com/node/91604) — Sorularla İslamiyet
- [Şafiî ve Hanefî'de hayız süreleri](https://sorularlaislamiyet.com/node/16210) — Sorularla İslamiyet

# Hocaya Danışılacak Konular — Namaz Takibi

27 Ekim 2026 · @ece · Cevaplar: 28 Ekim 2026

## Hocam, kısaca uygulama

Android için bir namaz ve kaza takip uygulaması geliştiriyorum. Uygulama fetva vermiyor; kullanıcının kıldığı ve kaçırdığı namazları kaydediyor ve kaza borcunu tutuyor. İlk sürüm yalnızca Hanefî mezhebine göre çalışacak, vakitleri Diyanet takviminden alıyor ve günde 6 namaz takip ediyor (beş vakit + vitir).

Aşağıdaki kuralları Din İşleri Yüksek Kurulu fetvalarından, TDV İslâm Ansiklopedisi'nden ve bazı Hanefî fıkıh kaynaklarından derledik. Her maddede uygulamanın şu an ne yaptığını yazdım. Sizden öğrenmek istediğim: **bu doğru mu, doğru değilse nasıl olmalı?**

Bilmeniz gereken bir nokta: uygulama hiçbir durumda kullanıcıya sormadan borç yazmıyor. Namaz kaçırıldı sayıldığında "borca ekleyeyim mi?" diye soruyor; karar her zaman kullanıcıda.

Öncelik sırası: 1. ve 2. bölümler kod yazılmadan önce mutlaka netleşmeli. Diğerleri de önemli ama sonradan düzeltilebilir.

**Görüşmenin sonucu (28 Ekim 2026).** Hoca genel olarak tüm sorulardaki yaklaşımın doğru olduğunu söyledi. Cevaplar her sorunun altına özetlendi. Belgelere yansıması: fikhi-gereksinimler.md ("Hoca görüşü kaydı"), SRS 1.0-taslak.7, karar tablosu v1.4, kabul senaryoları (98 senaryo); ikinci görüşmeden sonra SRS 1.0-taslak.8, karar tablosu v1.5, kabul senaryoları (99 senaryo).

---

## 1. Âdet (hayız) ve lohusalık — öncelikli

**1.1 Vakit içinde başlayan âdet**

Uygulamada: Vakit girdikten sonra, o vaktin namazı henüz kılınmadan âdet başlarsa o vaktin namazı borca yazılmıyor. Örnek: öğle 12:30'da girdi, kadın henüz kılmamıştı, 14:00'te âdet başladı; öğlenin kazası yok.

Sorum: Bu doğru mu? Vaktin sonuna yakın başlaması (ör. vaktin çıkmasına 5 dakika kala) hükmü değiştirir mi?

Hocanın cevabı: Doğru. Âdetin vaktin sonuna yakın başlaması hükmü değiştirmez.

*Bizim için: BR-11 · KT-37–39 → değişiklik yok*

**1.2 Âdetin bittiği vaktin namazı**

Uygulamada: Âdet 10 günden önce biterse, vaktin çıkmasına gusül ve iftitah tekbiri alacak kadar süre kalmışsa o vaktin namazı farz oluyor. Tam 10 günde biterse yalnızca tekbir alacak kadar süre yetiyor. Gusül süresini varsayılan 15 dakika, tekbiri 1 dakika aldık; kullanıcı gusül süresini 5–60 dakika arasında değiştirebiliyor.

Örnek: İkindi 18:00'de çıkıyor. Âdet 17:44'te biterse 16 dakika kalmış olur, ikindi farz olur. 17:45'te biterse 15 dakika kalır, ikindi farz olmaz.

Sorularım:
- Kural doğru mu?
- Gusül için 15 dakika makul bir varsayılan mı? Giyinmek ve örtünmek de bu süreye dahil mi?
- Lohusalıkta aynı kuralı 40 gün sınırıyla uyguluyoruz (40 günden önce biterse gusül + tekbir, tam 40'ta yalnız tekbir). Doğru mu?

Hocanın cevabı: Kural doğru. Gusül için 15 dakika gayet ideal. Ancak gusül süresi için 60 dakikalık üst sınır çok fazla; aralık 5–30 dakika olmalı. Lohusalık için sınır doğru.

*Bizim için: BR-12 · KT-41–49 → gusül aralığı 5–30 dk; KT-90 eklendi*

**1.3 Yatsı vaktinde biten âdette vitir**

Uygulamada: Âdet yatsı vaktinde biter ve yatsı farz olursa, vitir de kılınacak olarak açılıyor. İmsaka kadar kılınmazsa ikisi için ayrı ayrı "borca ekleyeyim mi?" soruluyor.

Sorum: Yatsı farz olunca vitir de vâcip olur mu?

Hocanın cevabı: İşlenen kural doğru. Vitri de kılması lazım.

*Bizim için: BR-12 · KT-51 · karar 2 → onaylandı*

**1.4 Üç günden kısa kanama**

Uygulamada: Kanama 72 saatten (dakikası dakikasına) kısa sürerse âdet sayılmıyor. Uygulama "bu günlerde kılınmayan namazlar kazaya kalmış olabilir, borca ekleyeyim mi?" diye soruyor. Hamilelikteki kanamayı da âdet saymıyoruz.

Sorum: Ölçüyü 72 saat olarak almak doğru mu? Bu durumda o günlerin namazlarının kazası gerçekten gerekir mi?

Hocanın cevabı: Evet, her şey doğru.

*Bizim için: BR-13 · KT-52–53 → onaylandı*

**1.5 On günü aşan kanama ve kadının âdet süresi**

"Âdet süresi" derken, kadının âdetinin her ay genellikle kaç gün sürdüğünü kastediyorum (ör. "benim âdetim hep 6 gün sürer").

Uygulamada: Kullanıcı isterse "âdetim genellikle kaç gün sürer" bilgisini giriyor.
- Kanama 10 günden önce kesilirse, bu süreyi aşsa bile tamamı âdet sayılıyor. Örnek: âdet süresi 6 gün, bu ay 8 gün sürdü → 8 günün hepsi âdet.
- 10 günü aşarsa, âdet süresi kadar gün âdet, kalanı istihâze sayılıyor; istihâze günlerinde kılınmayan namazlar için borç öneriliyor. Örnek: âdet süresi 6 gün, kanama 11 gün sürdü → ilk 6 gün âdet, 7.–11. günlerde kılınmayan namazlar kaza.
- Kadın âdet süresini girmemişse 10 gün âdet kabul ediliyor.

Sorum: Bu doğru mu? Âdet süresini girmemiş kadın (ve ilk kez âdet gören kız) için 10 gün kabul etmek uygun mu?

Hocanın cevabı: Tamamıyla doğru, hepsi. 10 günü geçerse istihâze sayılır ve namazı kılması gerekir.

*Bizim için: BR-14 · KT-54–57 → onaylandı*

**1.6 Kadının âdet süresi ne zaman değişmiş sayılır?**

Bu soru 1.5'teki hesap yüzünden önemli: 10 günü aşan bir kanamada hangi günlerin âdet, hangilerinin istihâze sayılacağı, kadının âdet süresine göre belirleniyor.

Örnek: Kadının âdeti yıllardır 6 gün sürüyor. Bu ay 8 gün sürdü.
- Artık âdet süresi 8 gün mü sayılır?
- Yoksa bunun için arka arkaya iki ay 8 gün sürmesi mi gerekir?

Uygulamada: Tek bir ayın farklı sürmesi âdet süresini değiştirmiyor. Arka arkaya iki ay aynı farklı süre görülürse (ör. iki ay üst üste 8 gün) uygulama "âdet süreni 8 gün olarak güncelleyelim mi?" diye soruyor. Bunu Diyanet Fetva Kurulu'na atıfla aktarılan bir fetvaya dayandırdık.

Ama bazı Hanefî kaynaklar âdet süresinin tek seferde değiştiğini ve fetvanın bu görüşte olduğunu söylüyor.

Sorum: Hangisini esas almalıyız: âdet süresi bir ayda mı, iki ayda mı değişir?

Hocanın cevabı: Âdet süresi değişkenlik gösterebilir; bu kadının elinde olan bir şey değildir. Kadın ilk başta alışkanlık süresini girer, örneğin 6. Bir ay 8 gün sürerse 8 olarak değiştirir ve yeni süre 8 olarak güncellenir.

*Bizim için: BR-14 · KT-58–59, 87–89 · karar 6 → alışkanlık tek dönemde güncellenir (karar tablosu karar 8)*

**1.7 Arada kesilip yeniden başlayan kanama**

Uygulamada: Âdet tek bir başlangıç ve bitiş olarak kaydediliyor. Kanama kesilir ve 15 gün dolmadan yeniden başlarsa, uygulama bunu yeni bir dönem sayıyor ve "15 günlük temizlik dolmadı, bu âdet sayılmayabilir" uyarısı veriyor.

Bunun yanlış olabileceğini düşünüyorum. Örnek: 1.–3. gün kan, 4.–6. gün temiz (kadın gusledip namaz kıldı), 7.–8. gün yine kan.

Sorularım:
- Bu durumda 1.–8. günlerin hepsi âdet mi sayılır?
- 7.–8. günlerde kılınmayan namazlar kaza mı?
- Kanama bu şekilde gidip gelip toplamda 10 günü aşarsa ne olur?
- Uygulama bu durumu nasıl kaydetmeli?

Hocanın cevabı: Evet, 1.–8. günlerin hepsi âdet sayılır. 7.–8. günlerde kılınmayan namazlar kaza değildir. Toplamda 10 günü aşarsa 10 günden sonrası istihâze sayılır. (Kaydetme şekli uygulamanın kararına bırakıldı.)

*Bizim için: BR-16 · KT-62–63 → dönem birleştirme eklendi; KT-95–98, KS-MAZ-17–18*

**1.8 İki âdet arasındaki en az temizlik**

Uygulamada: Önceki âdetin bitiminden 15 gün geçmeden yeni kanama başlarsa uyarı veriliyor; kullanıcı yine de kaydedebiliyor.

Sorum: 15 gün doğru mu? Bu durumda gelen kanın günlerinde namaz kılınmalı mı, kılınmadıysa kaza mı?

Hocanın cevabı: 15 gün doğrudur.

*Bizim için: BR-16 → onaylandı; 1.7 ile birlikte: 10 günlük pencereden sonra, 15 gün dolmadan gelen kan istihâze*

**1.9 Lohusalık**

Uygulamada: Lohusalık en çok 40 gün, alt sınırı yok. Organları belirmiş bir düşükten sonraki kan da lohusalık sayılıyor. 40 günü aşan kan istihâze sayılıyor, herkes için aynı şekilde.

Sorum: Daha önce doğum yapmış ve lohusalığı belli bir süre sürmüş kadında, kan 40 günü aşarsa önceki lohusalık süresi mi esas alınır (âdetteki alışkanlık gibi)? Uygulama bunu sormalı mı?

Hocanın cevabı: 40 gün en çok süredir. 40'ı aşarsa istihâze sayılır.

*Bizim için: BR-15 · KT-60–61 → lohusalık alışkanlığı sorulmaz*

---

## 2. Baygınlık ve ağır hastalık — öncelikli

**2.1 Baygınlıkta namazın düşmesi**

Uygulamada: Baygınlık, koma ya da akıl kaybı süresince 6 farz namazın vakti çıkarsa o namazlar düşüyor, kaza gerekmiyor. 5 veya daha azsa kaza gerekiyor. Vitri bu sayıya katmıyoruz. Vakit girişlerini değil, vakti tamamen çıkan farzları sayıyoruz.

Örnek: Öğlen 12:00'de bayılıp ertesi gün 15:29'da ayılan kişide öğle, ikindi, akşam, yatsı ve sabahın vakti çıkmış (5 farz) → kaza gerekir. 15:30'da ayılırsa ikinci günün öğlesi de çıkmış olur (6 farz) → namazlar düşer.

Sorum: Sayım şekli doğru mu? Vitri saymamak doğru mu?

Hocanın cevabı: Doğru (2. bölümdeki üç soru için ortak cevap: hepsi aynı kurala tabidir).

*Bizim için: BR-22 · KT-78–79, 85–86 · karar 4 → onaylandı*

**2.2 Narkoz ve ilaçla bayılma**

Uygulamada: Şu an her türlü bilinç kaybı aynı kurala tabi.

Sorum: Ameliyat narkozu ya da ilaçla bilinç kaybı da aynı hükümde mi, yoksa bu durumlarda süre ne olursa olsun kaza mı gerekir?

Hocanın cevabı: Aynı kurala tabidir.

*Bizim için: BR-22 → narkoz açıkça yazıldı; KT-100*

**2.3 Başını bile oynatamayan hasta**

Uygulamada: İmâ ile bile kılamayacak kadar ağır hastaya baygınlıktaki kuralın aynısını uyguluyoruz (6 farz vakti çıkarsa düşer).

Sorum: Doğru mu?

Hocanın cevabı: Doğru, aynı kurala tabidir.

---

## 3. Vakitler

**3.1 Öğlenin sonu**

Uygulamada: Diyanet takvimindeki ikindi vakti (asr-ı evvel) girince öğle "kaçırıldı" sayılıyor ve "borca ekleyeyim mi?" soruluyor. Kullanıcı "vaktinde kıldım" diyerek bunu kapatabiliyor. İmam-ı Âzam'a göre öğlenin asr-ı sânîye kadar sürdüğünü biliyoruz.

Sorum: Asr-ı evvel ile asr-ı sânî arasında kılınan öğle eda mı kaza mı kaydedilmeli? Kullanıcıya bu saatte nasıl bir şey söylemeliyiz?

Hocanın cevabı: Vakitler İmam-ı Âzam'a göre değil, vakte göre, yani Diyanet'e göre ayarlanacak. Kullanıcı ikindi okunana kadar öğleyi kılabilir; sıkıntı yok.

*Bizim için: BR-04, BR-06 · KT-07–08, 14 → BR-04 onaylandı*

**3.2 İkindinin başlangıcı**

Uygulamada: İkindi, Diyanet'in ikindi vaktinden (asr-ı evvel) itibaren işaretlenebiliyor. Kullanıcı isterse asr-ı sânî vakti de gösteriliyor, ama işaretlemeyi engellemiyor.

Sorum: Hanefî bir kullanıcının asr-ı evvelde kıldığı ikindi geçerli mi? Bir uyarı ya da öneri göstermeli miyiz?

Hocanın cevabı: Kullanıcıya asr-ı evvel, asr-ı sânî gibi şeyleri göstermeye gerek yok; vaktin saatlerini göstermek yeterli. Diyanet'e göre bakılmalı.

*Bizim için: BR-06, FR-VKT-05, KT-14 kaldırıldı; KS-VKT-06 eklendi*

**3.3 Akşamın sonu**

Uygulamada: Diyanet'in yatsı vakti girince akşam kaçırıldı sayılıyor. İmam-ı Âzam'a göre akşamın beyaz şafak kaybolana kadar sürdüğünü biliyoruz.

Sorum: Öğledeki gibi bu arada kılınan akşam nasıl kaydedilmeli?

Hocanın cevabı: "Diyanet'in yatsı vakti girince akşam kaçırıldı sayılır" kabul edilsin.

*Bizim için: BR-04 → onaylandı*

**3.4 Vitrin vakti**

Uygulamada: Vitir ancak yatsı işaretlendikten sonra işaretlenebiliyor ve imsaka kadar kılınmazsa borç öneriliyor.

Sorum: Doğru mu?

Hocanın cevabı: Doğru.

*Bizim için: BR-03 · KT-04–06, 11 → onaylandı*

**3.5 Kerahat vakitleri**

Uygulamada: Üç kerahat vaktinde kaza kılınamıyor:
- Güneş doğduktan sonra 50 dakika
- Öğle vaktinden önce 45 dakika
- Akşam vaktinden önce 50 dakika

Akşamdan önceki kerahatte tek istisna, o günün henüz kılınmamış ikindisinin farzı. Sabah namazından sonra güneş doğana kadar ve ikindiden sonra kerahate kadar kaza kılınabiliyor.

Sorular:
- Süreler doğru mu?
- Bu vakitlerde vitir kazası da kapalı olmalı mı?
- Sabahtan sonra ve ikindiden sonra kaza kılınabilmesi doğru mu?

Hocanın cevabı: Kerahat vakitleri için 50 dakika çok; üçü için de 45 dakika yeterli. Ayrıca nafile namaz için:
- Sabah vakti: imsak ile güneşin doğuşu arasında sabahın sünneti dışında nafile namaz kılınmaz.
- Sabah farzından sonra: güneş doğuncaya kadar nafile namaz kılınmaz.
- İkindi namazından sonra: ikindinin farzı kılındıktan sonra güneş batıncaya kadar nafile namaz kılınmaz.

*Bizim için: BR-07 · KT-19–27 → 45/45/45 dk; vitir kazası da kapalı kalır; bu vakitlerde kısıtlama nafile için olduğundan kaza açık kalır; nafile kuralı ileride nafile takibi için referans*

---

## 4. Kaza kuralları

**4.1 Borca yazılanlar**

Uygulamada: Beş vakit farz ve vitir borca yazılıyor. Sünnetler yazılmıyor; tek istisna, aynı gün öğleden önce kaza edilen sabahın sünnetiyle birlikte kılınması. Cuma kaçırılırsa öğle olarak yazılıyor. Bayram namazı takip edilebiliyor ama borca yazılmıyor.

Sorum: Doğru mu?

Hocanın cevabı: Doğru.

*Bizim için: BR-08, BR-09 · KT-15–17, 34 → onaylandı*

**4.2 Tertip (sıra)**

Uygulamada: Farz kaza borcu 6 vakitten azsa (vitri saymadan), kullanıcı vaktin namazını işaretlerken "önce kazayı kılman gerekir" bilgi notu gösteriliyor. Uygulama işaretlemeyi engellemiyor, sadece hatırlatıyor.

Sorular:
- Not göstermek yeterli mi, yoksa farklı bir şey mi yapmalıyız?
- Farz borcu yok ama sadece vitir borcu varsa, sabahtan önce vitrin kazası gerekir mi? Not gösterilmeli mi?
- Borç 6'yı geçip tertip düştükten sonra, kaza kılınarak 6'nın altına inerse tertip geri döner mi?

Hocanın cevabı: Not yeterli. Yalnızca vitir borcu varsa gerek yok. Borç kaza kılınarak 6'nın altına inerse tertip geri döner.

*Bizim için: BR-17 · KT-28–30 → KT-92, KT-93 eklendi*

**4.3 Vaktin darlığı**

Uygulamada: Vakit kaza borçlarıyla vaktin farzını birlikte kılmaya yetmiyorsa tertip notu gösterilmiyor. Bunu rekât başına 2 dakika hesaplıyoruz. İkindide sınır, güneşin sararması (akşamdan önceki kerahatin başlangıcı).

Örnek: 5 vakit farz kazası (17 rekât) + öğle (4 rekât) = 21 rekât × 2 = 42 dakika. Öğlenin çıkmasına 42 dakika varsa not gösteriliyor, 41 dakika varsa gösterilmiyor.

Sorum: Bu hesap makul mu? İkindide sınırı güneşin sararması olarak almak doğru mu?

Hocanın cevabı: Hesap makul.

*Bizim için: BR-17 · KT-31, 82–84 · karar 1 → onaylandı*

**4.4 Seferde kaçan namazın kazası**

Uygulamada: Sefer mesafesi yaklaşık 90 km, 15 gün kalma niyeti seferîliği bitiriyor. Seferde kaçan 4 rekâtlı namaz evde de 2 rekât kaza ediliyor; evde kaçan namaz seferde de 4 rekât kaza ediliyor.

Sorum: Doğru mu?

Hocanın cevabı: Her şey doğru.

*Bizim için: BR-18 · KT-32–33 → onaylandı*

**4.5 Kaza borcu varken nafile ve sünnet**

Uygulamada: Bu konuda hiçbir şey söylemiyoruz, kullanıcıyı engellemiyoruz.

Sorum: Kazası olan birine "sünnet ve nafile yerine kaza kıl" gibi bir not göstermeli miyiz? Müekked sünnetler için durum farklı mı?

Hocanın cevabı: Bu konuda bir şey söylenmesin; uygulamada gerek yok.

*Bizim için: değişiklik yok*

**4.6 Kazanın hangi vakitten düşeceği ve niyet**

Uygulamada: Kaza kılan kullanıcı hangi vakitten düşeceğini seçmek zorunda. Kaza ekranında "üzerimdeki ilk (veya son) öğle namazı" şeklinde bir niyet hatırlatması göstermeyi düşünüyoruz.

Sorum: Bu niyet şekli doğru mu? Ekranda başka bir şey yazmalı mıyız?

Hocanın cevabı: Niyet şekli doğru, gösterilebilir.

*Bizim için: FR-KAZ-10 (P1) eklendi*

---

## 5. Kaza borcu hesaplayıcı

**5.1 Bulûğ yaşı**

Uygulamada: Kullanıcı ilk bulûğ alametinin görüldüğü yaşı giriyor. Alt sınır erkekte 12, kızda 9. Bilmiyorsa 15 kamerî yaş öneriliyor (doğum tarihi hicrî takvime çevrilip hesaplanıyor).

Sorular:
- Doğru mu?
- Kızlarda bulûğ genellikle 15'ten önce olduğu için, bilmeyen bir kadına 15'i önermek borcunu eksik gösterebilir. İhtiyaten daha erken bir yaş mı önermeliyiz, yoksa "tahmin et" mi demeliyiz?

Hocanın cevabı: Doğru. Bilmeyen birine "tahmin et" denebilir; tahmin de edemiyorsa ihtiyaten 14 önerilebilir.

*Bizim için: BR-19 · KT-71–74 → tahmin adımı; 14 kamerî yaş (KT-99). Erkekte de 14 (ikinci görüşme, KT-71)*

**5.2 Borç formülü**

Uygulamada: Borç = (bulûğdan düzenli kılmaya başladığı tarihe kadar geçen gün − mazeret günleri − kıldığını bildiği günler) × 6. Kadında âdet günleri "alışkanlık × ay sayısı" olarak düşülüyor, bilinen lohusalık dönemleri de düşülüyor. Sonuç ekranda "tahmini" yazıyor ve kullanıcı elle düzeltebiliyor.

Sorular:
- Formül doğru mu?
- Kesin bilinmeyen durumda ihtiyat ne gerektirir: âdet günlerini daha az mı düşmeliyiz, kıldığından emin olmadığı günleri borca mı katmalıyız?

Hocanın cevabı: Formül doğru. Kıldığından emin değilse borca kesinlikle katılsın.

*Bizim için: BR-20 · KT-75–77 → yalnızca kesin bilinen günler düşülür*

**5.3 Müslüman olmadan önceki dönem**

Uygulamada: Sonradan Müslüman olan biri için borç, Müslüman olduğu tarihten başlıyor.

Sorum: Doğru mu?

Hocanın cevabı: Doğru.

*Bizim için: BR-20 → onaylandı*

---

## 6. Genel

**6.1 Uygulamanın dili ve sınırı**

Uygulamada: Her yerde "Bu sürüm Hanefî mezhebine göre çalışır" bilgisi var. Sonuçlar "öneri" ve "tahmini" olarak gösteriliyor, kullanıcı her şeyi düzeltebiliyor. Bilerek terk edilen namaz için de suçlayıcı olmayan bir dil kullanıyoruz.

Sorular:
- Bu yaklaşım yeterli mi?
- Hangi durumlarda kullanıcıya "bu konuda bir hocaya danışın" demeliyiz? (Benim aklımdaki: uzun süreli baygınlık, düzensiz kanama, borç hesabı.)
- Kaynak olarak Diyanet fetvalarını göstermemiz uygun mu?

Hocanın cevabı: Bu yaklaşım yeterli. "Bu konuda hocaya danışın" gibi bir alana şimdilik gerek yok. Kaynak olarak Diyanet fetvalarını göstermek uygun.

*Bizim için: NFR-USA-03 güncellendi; danışma yönlendirmesi kapsam dışı*

---

## Görüşmeden sonra

Cevaplar 28 Ekim 2026'da şu belgelere işlendi: Fıkhi Gereksinimler (hoca görüşü kaydı), SRS 1.0-taslak.7 (iş kuralları, FR'ler ve Bölüm 10), Karar Tablosu v1.4 ve Kabul Senaryoları.

**İkinci görüşme (28 Ekim 2026).** Cevaplardan türetilen beş nokta soruldu:

1. *10 günü aşan bir dönem alışkanlığı değiştirir mi?* Hocanın cevabı: Alışkanlığı 10 olarak değiştirir. 12 gün sürse bile 10 yazılır, çünkü 10'dan fazlası istihâze sayılır. → BR-14; KT-55, 56, 58, 97, 98
2. *Alışkanlığını hiç girmemiş bir kadında ilk geçerli dönemin süresi alışkanlık olur mu?* Hocanın cevabı: Evet. → BR-14; KT-59
3. *Kesilip yeniden başlayan kanamada toplam süre 10 günü aşarsa alışkanlık hesabı tüm döneme uygulanır mı?* Hocanın cevabı: Karar uygulamaya bırakıldı. → Karar: evet, tek dönem kuralları uygulanır (BR-16; karar tablosu karar 17)
4. *Bulûğ yaşını bilmeyen ve tahmin de edemeyen erkekte 14 mü 15 mi?* Hocanın cevabı: 14. → BR-19; KT-71
5. *Lohusalıkta 40 gün dolmadan kan kesilip yeniden başlarsa?* Hocanın cevabı: 40 gün içindeki her şey lohusalık sayılır. → BR-15; KT-101, 102

Belgelere yansıması: SRS 1.0-taslak.8, karar tablosu v1.5, kabul senaryoları (99 senaryo).

Ayrıca ekran metinleri (mazeret sonuç metinleri, toplu onay gerekçeleri, yeni uyarılar, Hakkında sözlüğü) hocaya ayrıca gösterilecek.

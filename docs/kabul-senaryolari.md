# Kabul Senaryoları — Namaz & Kaza Takip v1.0

27 Ekim 2026 · @ece · Son güncelleme: 28 Ekim 2026 (hoca görüşü)

## Özet

Bu belge, SRS 1.0-taslak.8'deki 43 P0 gereksinimin her birini en az bir kabul senaryosuna bağlar. Bir P0 gereksinim ancak ona bağlı tüm senaryolar geçtiğinde "kabul edildi" sayılır; v1.0 yayın kapısı da bu koşula dayanır.

**Karar tablosundan farkı.** Karar tablosu kural motorunu ekransız, birim testi düzeyinde sınar: "bu girdiye bu öneri". Kabul senaryosu aynı davranışı kullanıcının gözünden sınar: hangi ekranda ne görür, neye dokunur, widget ve bildirim ne yapar. Kural ayrıntısı burada tekrar edilmez; senaryo ilgili KT satırına atıf yapar.

**Biçim.** Senaryolar Cucumber'ın Türkçe Gherkin anahtar kelimeleriyle yazıldı; doğrudan `.feature` dosyasına kopyalanabilir.

| Anahtar kelime | Karşılığı | Ne yazılır |
| --- | --- | --- |
| Senaryo | Scenario | Senaryo kimliği ve adı |
| Diyelim ki | Given | Başlangıç durumu |
| Eğer ki | When | Kullanıcının ya da sistemin eylemi |
| O zaman | Then | Gözlenebilir sonuç |
| Ve / Fakat | And / But | Ek adım |

**Nasıl kullanılır?**

- Her senaryonun kimliği `KS-<grup>-<no>` biçimindedir; başlığında bağlı olduğu FR ve KT satırları yazar.
- **Otomatik** senaryolar Faz 5'te Compose UI testi olarak yazılır; test adı senaryo kimliğiyle başlar (ör. `KS_TRK_02_vaktiGirmemisNamazIsaretlenmez`).
- **Elle** etiketli senaryolar (widget, bildirim, Doze, yeniden başlatma) Faz 6 ve 7'de gerçek cihazda kontrol listesiyle yürütülür; sonuç tarihli olarak kaydedilir.
- P1 gereksinimler (7 adet) son bölümde yalnızca izlenebilirlik için listelenir; senaryoları v1.1'de yazılır.

## Ortak ön koşullar

Aksi yazmadıkça her senaryo aşağıdaki durumdan başlar. Vakitler karar tablosundaki referans günle aynıdır; böylece aynı saat iki belgede aynı anlama gelir.

```gherkin
# language: tr
Geçmiş:
  Diyelim ki uygulama temiz kurulmuş ve onboarding tamamlanmış
  Ve seçili ilçe "Test İlçesi" (saat dilimi Europe/Istanbul)
  Ve ilçenin vakitleri her gün: imsak 05:30, güneş 07:00, öğle 12:30, ikindi 15:30, akşam 18:00, yatsı 19:30
  Ve tüm kalemlerde kaza borcu 0
  Ve ayarlar varsayılan: hatırlatma 30 dk, gusül 15 dk, otomatik borç ekleme kapalı
  Ve kural parametreleri varsayılan: kerahat 45/45/45 dk
```

| Terim | Senaryolardaki anlamı |
| --- | --- |
| Kullanıcı E / K | Onboarding'de cinsiyeti erkek / kadın seçilmiş kullanıcı |
| "Saat X olur" | Testte enjekte edilen sahte saat X'e ilerletilir; elle testte cihaz saati değiştirilir |
| G1, G2 | Senaryonun başladığı gün ve ertesi gün |
| Bugün ekranı | Uygulama açılınca gelen, 6 kalemin listelendiği ana ekran |
| Öneri kartı | Kural motorunun önerisini gösteren, onay ve ret düğmeli kart |

**Test cihazları.** Otomatik testler Android 8.0 (API 26) ve Android 14 (API 34) emülatörlerinde koşar. Elle testler en az bir Samsung ve bir Xiaomi cihazda yapılır; bu iki marka arka plan işlerini en sert kısıtlayan üreticiler arasındadır.

## 1. Onboarding

9 senaryo, 5 gereksinim. Bu bölümde "Geçmiş"teki onboarding tamamlanmış koşulu geçerli değildir; her senaryo temiz kurulumla başlar. Etiketler (`@FR-...`, `@otomatik`, `@elle`) izlenebilirlik ve test seçimi içindir.

```gherkin
# language: tr
Özellik: Onboarding

  @FR-ONB-01 @otomatik
  Senaryo: KS-ONB-01 Cinsiyet ve konum seçilmeden ilerlenemez
    Diyelim ki uygulama ilk kez açıldı
    Eğer ki kullanıcı yalnızca cinsiyeti seçer
    O zaman "Devam" düğmesi pasif kalır
    Eğer ki kullanıcı il ve ilçeyi de seçer
    O zaman "Devam" düğmesi etkinleşir

  @FR-ONB-02 @otomatik
  Senaryo: KS-ONB-02 Mezhep sorulmaz, Hanefî bilgisi bir kez gösterilir
    Diyelim ki kullanıcı onboarding'de
    O zaman hiçbir adımda mezhep seçimi sunulmaz
    Ve "Bu sürüm Hanefî mezhebine göre çalışır" ekranı bir kez görünür
    Eğer ki uygulama yeniden açılır
    O zaman bilgi ekranı tekrar gösterilmez
    Fakat aynı metin Ayarlar > Hakkında altında okunabilir

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

  @FR-ONB-04 @otomatik
  Senaryo: KS-ONB-05 Bildirim izni reddedilirse uygulama çalışmaya devam eder
    Diyelim ki cihaz Android 13 veya üstü
    Eğer ki hatırlatma adımında sistem izin penceresinde "İzin verme" seçilir
    O zaman onboarding tamamlanır ve Bugün ekranı açılır
    Ve Ayarlar > Hatırlatmalar'da "Bildirimlere izin ver" düğmesi görünür

  @FR-ONB-04 @otomatik
  Senaryo: KS-ONB-06 Eski Android sürümünde izin penceresi çıkmaz
    Diyelim ki cihaz Android 12 veya altı
    Eğer ki hatırlatma adımı geçilir
    O zaman sistem izin penceresi gösterilmez ve hatırlatmalar açık başlar

  @FR-ONB-05 @otomatik
  Senaryo: KS-ONB-07 Onboarding hiçbir vakti işaretlemez
    Diyelim ki saat G1 16:00
    Eğer ki onboarding tamamlanır
    O zaman Bugün ekranındaki 6 kalemin hiçbiri işaretli değildir
    Ve kaza borcu kaydedilmemiştir
    Ve kurulumdan önce vakti çıkmış sabah ve öğle için öneri kartı oluşmaz

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
```

## 2. Günlük takip

14 senaryo, 7 P0 gereksinim (FR-TRK-08 seri P1'dir). KS-TRK-11, SRS'te yazılı olmayan bir ekran kararını sabitler; gerekçesi son bölümde.

```gherkin
# language: tr
Özellik: Günlük takip

  @FR-TRK-01 @otomatik
  Senaryo: KS-TRK-01 Altı kalem ayrı ayrı işaretlenir
    Diyelim ki saat G1 13:00
    Eğer ki Bugün ekranında öğleye dokunulur
    O zaman öğle "kılındı" olarak görünür ve işaretlenme saati 13:00 yazar
    Ve diğer 5 kalemin durumu değişmez

  @FR-TRK-01 @otomatik
  Senaryo: KS-TRK-02 Takip için süre sınırı yoktur
    Diyelim ki kurulumun üzerinden 400 gün geçmiş
    Eğer ki o günün öğlesi işaretlenir
    O zaman öğle "kılındı" olur
    Ve hiçbir süre, deneme ya da üyelik uyarısı görünmez

  @FR-TRK-02 @otomatik @KT-01 @KT-02
  Senaryo: KS-TRK-03 Vakti girmemiş namaz işaretlenemez
    Diyelim ki saat G1 12:29
    Eğer ki öğleye dokunulur
    O zaman işaret konmaz ve "Vakti henüz girmedi" notu görünür
    Eğer ki saat 12:30 olur ve öğleye yeniden dokunulur
    O zaman öğle "kılındı" olur

  @FR-TRK-03 @otomatik @KT-18
  Senaryo: KS-TRK-04 İşaret tek dokunuşla geri alınır
    Diyelim ki öğle G1 12:40'ta işaretlendi
    Eğer ki 12:45'te işaretli öğleye dokunulur
    O zaman öğle boş görünür
    Eğer ki saat 15:30 olur
    O zaman "Öğle borca eklensin mi?" öneri kartı görünür

  @FR-TRK-04 @otomatik @KT-13
  Senaryo: KS-TRK-05 Geçmiş bir namaz "vaktinde kıldım" diye işaretlenir
    Diyelim ki G1 öğlesi boş kaldı ve öneri kartı yanıtlanmadı
    Eğer ki G2 10:00'da takvimden G1 açılır ve öğleye dokunulur
    O zaman "Vaktinde kıldım / Sonradan kaza olarak kıldım" seçimi sorulur
    Eğer ki "Vaktinde kıldım" seçilir
    O zaman G1 öğlesi "kılındı" olur, öneri kartı kapanır ve kaza borcu değişmez

  @FR-TRK-04 @otomatik @KT-12
  Senaryo: KS-TRK-06 Geçmiş bir namaz "kaza olarak kıldım" diye işaretlenir
    Diyelim ki G1 öğlesi kaçırıldı, öneri onaylandı ve öğle borcu 1
    Eğer ki G1 16:00'da G1 öğlesi için "Sonradan kaza olarak kıldım" seçilir
    O zaman G1 öğlesi "kılındı (kaza)" etiketiyle görünür
    Ve öğle borcu 0 olur ve kaza geçmişinde bir "kılınan kaza" kaydı eklenir

  @FR-TRK-05 @otomatik
  Senaryo: KS-TRK-07 Açık günün tarihi her ekranda görünür
    Diyelim ki bugün 27 Ekim 2026
    O zaman Bugün ekranının başlığında "27 Ekim 2026 Salı" yazar
    Eğer ki takvimden 20 Ekim açılır
    O zaman başlıkta "20 Ekim 2026 Salı" yazar ve bugüne dönüş düğmesi görünür
    Ve kaza ekranının başlığında da günün tarihi yazar

  @FR-TRK-06 @otomatik @KT-15 @KT-16
  Senaryo: KS-TRK-08 Erkek kullanıcıda cuma
    Diyelim ki kullanıcı E ve bugün cuma
    O zaman öğle kalemi "Öğle / Cuma" seçimi sunar
    Eğer ki 13:00'te "Cuma" seçilir
    O zaman kalem "Cuma kılındı" olarak görünür ve öğle kılınmış sayılır

  @FR-TRK-06 @otomatik @KT-16 @KT-17
  Senaryo: KS-TRK-09 Cuma kılınmazsa borç öğle olarak önerilir; kadında cuma isteğe bağlıdır
    Diyelim ki kullanıcı E, bugün cuma ve öğle kalemi boş
    Eğer ki saat 15:30 olur
    O zaman öneri kartı "Öğle borca eklensin mi?" der, "cuma" demez
    Diyelim ki kullanıcı K ve bugün cuma
    O zaman kalemin adı "Öğle"dir ve "Cuma" yalnızca kalemin menüsünde seçenek olarak bulunur

  @FR-TRK-07 @otomatik @KT-04 @KT-05
  Senaryo: KS-TRK-10 Vitir yatsıdan önce işaretlenemez
    Diyelim ki saat G1 20:00 ve yatsı boş
    Eğer ki vitre dokunulur
    O zaman işaret konmaz ve "Önce yatsıyı işaretle" açıklaması görünür
    Eğer ki yatsı işaretlenir ve vitre yeniden dokunulur
    O zaman vitir "kılındı" olur

  @FR-TRK-01 @FR-TRK-05 @otomatik @KT-06
  Senaryo: KS-TRK-11 Gece yarısından imsaka kadar Bugün ekranı önceki namaz gününü gösterir
    Diyelim ki G1 yatsısı kılındı ve vitir boş
    Eğer ki saat G2 00:30 olur ve uygulama açılır
    O zaman Bugün ekranı G1'i gösterir ve başlıkta G1'in tarihi yazar
    Eğer ki 05:29'da vitre dokunulur
    O zaman vitir G1 kaydına "kılındı" olarak yazılır
    Eğer ki saat 05:30 olur
    O zaman Bugün ekranı G2'ye geçer

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
```

## 3. Kaza

10 senaryo, 5 P0 gereksinim (FR-KAZ-06 – 10 P1'dir). Öneri kartında iki düğme vardır: "Borca ekle" ve "Vaktinde kıldım". Yanıtlanmayan öneriler "Bekleyen öneriler" ekranında birikir; Bugün ekranında en eskisi kart olarak, kalanların sayısı bağlantı olarak görünür (ekran taslakları Tur 3, karar 3). Kaza ekranında her vakit satırının kendi "Kıldım" düğmesi vardır; kılınan kaza o satırdan kaydedilir (ekran taslakları Tur 3, karar 2).

```gherkin
# language: tr
Özellik: Kaza borcu

  @FR-KAZ-01 @otomatik
  Senaryo: KS-KAZ-01 Borç elle düzeltilir ve düzeltme kayıt olarak saklanır
    Diyelim ki öğle borcu 8
    Eğer ki kaza ekranında öğle borcu 10 olarak düzenlenir
    O zaman öğle borcu 10 görünür
    Ve kaza geçmişinde "düzeltme +2" kaydı tarihiyle listelenir

  @FR-KAZ-02 @otomatik
  Senaryo: KS-KAZ-02 Kılınan kaza seçilen vakitten düşer
    Diyelim ki öğle borcu 3 ve ikindi borcu 2
    Eğer ki saat 14:00'te kaza ekranında ikindi satırındaki "Kıldım"a dokunulur
    O zaman ikindi borcu 1 olur
    Ve öğle borcu 3 kalır

  @FR-KAZ-02 @otomatik @KT-36
  Senaryo: KS-KAZ-03 Kaza yalnızca bir vakit satırından kaydedilir
    Diyelim ki ikindi borcu 2
    Eğer ki kaza ekranı açılır
    O zaman her vakit satırında kendi "Kıldım" düğmesi bulunur
    Ve vakit belirtmeden kaza kaydeden bir eylem bulunmaz

  @FR-KAZ-03 @otomatik @KT-08
  Senaryo: KS-KAZ-04 Kaçırılan namaz için borç önerisi onaylanır
    Diyelim ki G1 öğlesi boş
    Eğer ki saat 15:30 olur
    O zaman "Öğle borca eklensin mi?" öneri kartı görünür
    Eğer ki "Borca ekle" seçilir
    O zaman öğle borcu 1 olur ve G1 öğlesi "kılınmadı" görünür

  @FR-KAZ-03 @otomatik
  Senaryo: KS-KAZ-05 Otomatik ekleme kapalıyken hiçbir borç sessizce yazılmaz
    Diyelim ki G1'de hiçbir kalem işaretlenmedi ve hiçbir öneri yanıtlanmadı
    Eğer ki saat G2 07:00 olur
    O zaman Bugün ekranında en eski öneri kartı ve "+6 öneri" bağlantısı görünür
    Ve Bekleyen öneriler ekranında 7 öneri bulunur (G1'in 6 kalemi ve G2 sabahı)
    Ve tüm kalemlerde kaza borcu 0'dır

  @FR-KAZ-03 @otomatik @KT-09
  Senaryo: KS-KAZ-06 Otomatik ekleme açıkken borç sorulmadan yazılır
    Diyelim ki Ayarlar'da otomatik borç ekleme açık ve G1 öğlesi boş
    Eğer ki saat 15:30 olur
    O zaman öneri kartı görünmez
    Ve öğle borcu 1 olur, kaza geçmişinde kayıt "otomatik" türüyle listelenir

  @FR-KAZ-04 @otomatik @KT-35
  Senaryo: KS-KAZ-07 Borç negatife düşmez
    Diyelim ki öğle borcu 0
    Eğer ki kaza ekranında öğle satırındaki "Kıldım"a dokunulur
    O zaman "Bu vakitte borcun yok. Önce ekleyip sonra düşeyim mi?" sorusu görünür
    Eğer ki "Vazgeç" seçilir
    O zaman öğle borcu 0 kalır ve kaza geçmişine kayıt eklenmez

  @FR-KAZ-05 @otomatik @KT-81
  Senaryo: KS-KAZ-08 Bir kayıt geri alınır, geçmiş silinmez
    Diyelim ki G1 öğlesinin önerisi onaylandı ve öğle borcu 1
    Eğer ki kaza geçmişinde bu kayıt açılır ve "Geri al" seçilir
    O zaman öğle borcu 0 olur
    Ve geçmişte iki kayıt görünür: öneri onayı ve onun düzeltmesi

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
```

## 4. Mazeret

19 senaryo, 6 gereksinim. Bildirim metinleri nötrdür, açıklama yalnızca uygulama içinde görünür (NFR-SEC-03; son bölüm, karar 3). 28 Ekim 2026 hoca görüşüyle KS-MAZ-11 ve KS-MAZ-12 yeniden yazıldı; kesilip yeniden başlayan kanama için KS-MAZ-17 ve KS-MAZ-18, ikinci görüşmeden sonra lohusalıktaki ara temizlik için KS-MAZ-19 eklendi.

```gherkin
# language: tr
Özellik: Mazeret

  @FR-MAZ-01 @otomatik
  Senaryo: KS-MAZ-01 Mazeret menüsü yalnızca kadın kullanıcıda görünür
    Diyelim ki kullanıcı E
    O zaman Bugün ekranında ve ayarlarda mazeret seçeneği bulunmaz
    Diyelim ki kullanıcı K
    O zaman Bugün ekranının menüsünde mazeret seçeneği bulunur

  @FR-MAZ-01 @otomatik
  Senaryo: KS-MAZ-02 Mazeret tür ve saatle başlatılır
    Diyelim ki kullanıcı K ve saat G1 16:00
    Eğer ki mazeret başlatma ekranı açılır
    O zaman tür seçenekleri "Âdet" ve "Lohusalık", saat alanı 16:00 dolu gelir
    Eğer ki tür seçilmeden "Başlat" seçilmek istenir
    O zaman düğme pasiftir

  @FR-MAZ-02 @FR-HTR-04 @otomatik @KT-40 @KT-67
  Senaryo: KS-MAZ-03 Mazeret sürerken vakitler nötr görünür
    Diyelim ki kullanıcı K ve hayız G1 16:00'dan beri sürüyor
    Eğer ki G2 boyunca vakitler girip çıkar
    O zaman G2'nin 6 kalemi nötr renkte görünür ve işaretlenmez
    Ve hiçbir öneri kartı oluşmaz, hiçbir hatırlatma gelmez
    # Seri göstergesi adımı FR-TRK-08 (P1) ile birlikte v1.1'de eklenir

  @FR-MAZ-03 @otomatik @KT-37 @KT-39
  Senaryo: KS-MAZ-04 Vakit içinde başlayan mazerette o vakit için öneri çıkmaz
    Diyelim ki kullanıcı K, G1 öğlesi boş kaldı ve öğle öneri kartı bekliyor
    Ve G1 ikindisi boş
    Eğer ki hayız G1 16:00'da başlatılır
    O zaman ikindi için öneri kartı oluşmaz
    Fakat öğle öneri kartı yerinde kalır

  @FR-MAZ-04 @otomatik @KT-41
  Senaryo: KS-MAZ-05 Mazeret bitince farz olan vakit vaktinde kılınabilir
    Diyelim ki kullanıcı K ve hayız G1 10:00'da başladı
    Eğer ki G6 17:00'de bitiş kaydedilir
    O zaman G6 ikindisi işaretlenebilir hale gelir
    Ve "İkindi vakti çıkmadan kılabilirsin" bildirimi gelir
    Eğer ki ikindi 17:20'de işaretlenir
    O zaman 18:00'de öneri kartı oluşmaz

  @FR-MAZ-04 @otomatik @KT-41
  Senaryo: KS-MAZ-06 Farz olan vakit kılınmazsa borç her zaman sorulur
    Diyelim ki KS-MAZ-05'teki bitiş kaydedildi ve otomatik borç ekleme açık
    Eğer ki ikindi işaretlenmeden saat 18:00 olur
    O zaman "İkindi borca eklensin mi?" öneri kartı görünür
    Ve borç kart onaylanmadan yazılmaz

  @FR-MAZ-04 @otomatik @KT-43
  Senaryo: KS-MAZ-07 Kalan süre yetmezse o vakit mazeretli kalır
    Diyelim ki kullanıcı K ve hayız G1 10:00'da başladı
    Eğer ki G6 17:45'te bitiş kaydedilir
    O zaman G6 ikindisi nötr kalır ve bildirim gelmez
    Ve akşam 18:00'de normal şekilde işaretlenebilir olur

  @FR-MAZ-04 @otomatik @KT-51
  Senaryo: KS-MAZ-08 Yatsı vaktinde biten mazerette yatsı ve vitir birlikte açılır
    Diyelim ki kullanıcı K ve hayız G1 10:00'da başladı
    Eğer ki G6 05:00'te bitiş kaydedilir
    O zaman G5 yatsısı ve vitri işaretlenebilir hale gelir
    Eğer ki ikisi de işaretlenmeden saat 05:30 olur
    O zaman yatsı ve vitir için iki ayrı öneri kartı görünür

  @FR-MAZ-05 @otomatik @KT-52 @KT-80
  Senaryo: KS-MAZ-09 72 saatten kısa kanamada kılınmayanlar tek ekranda sorulur
    Diyelim ki kullanıcı K ve otomatik borç ekleme açık
    Eğer ki hayız G1 08:00'de başlatılır ve G4 07:59'da bitirilir
    O zaman "72 saatten kısa kanama hayız sayılmaz" açıklaması görünür
    Ve 18 kalem tek bir onay ekranında listelenir, her biri ayrı seçilebilir
    Ve kullanıcı onaylamadan hiçbir borç yazılmaz

  @FR-MAZ-05 @otomatik @KT-55
  Senaryo: KS-MAZ-10 240. saatte namaz yeniden başlar, bildirim nötrdür
    Diyelim ki kullanıcı K, alışkanlık 6 gün ve hayız G1 08:00'den beri sürüyor
    Eğer ki saat G11 08:00 olur
    O zaman "Bugünden itibaren namazlarını işaretleyebilirsin" bildirimi gelir
    Ve uygulama açılınca açıklama ve 24 kalemlik onay ekranı görünür
    Ve açıklamanın altında "Alışkanlığın 10 gün olarak güncellendi · Değiştir" satırı görünür
    Ve G11 öğlesi 12:30'da normal şekilde işaretlenebilir olur

  @FR-MAZ-05 @otomatik @KT-62 @KT-63
  Senaryo: KS-MAZ-11 15 günlük temizlik dolmadan gelen kanama âdet sayılmaz
    Diyelim ki kullanıcı K ve önceki hayız G4 08:00 – G10 08:00 kaydedildi
    Eğer ki G25 07:59'da yeni dönem başlatılmak istenir
    O zaman "15 günlük temizlik dolmadı" uyarısı görünür ve "Bu kanama âdet sayılmaz; namazların devam eder." yazar
    Ve odak "Başlatma" seçeneğindedir; "Yine de başlat" ikinci seçenektir
    Eğer ki "Başlatma" seçilir
    O zaman mazeret başlamaz ve G25 öğlesi 12:30'da normal şekilde işaretlenebilir olur
    Eğer ki aynı saatte yeniden denenir ve "Yine de başlat" seçilir
    O zaman mazeret başlar ve kayıt geçmişinde uyarı işareti taşır

  @FR-MAZ-06 @otomatik @KT-56 @KT-87
  Senaryo: KS-MAZ-12 Alışkanlık isteğe bağlıdır ve tek dönemde güncellenir
    Diyelim ki kullanıcı K ve alışkanlık alanı boş
    O zaman mazeret ayarlarında "Girilmedi (10 gün kabul edilir)" yazar
    Diyelim ki alışkanlık 6 gün
    Eğer ki 190 saat süren bir hayızın bitişi kaydedilir
    O zaman onay sorusu sorulmadan alışkanlık 8 gün olur
    Ve sonuç ekranında "Alışkanlığın 8 gün olarak güncellendi · Değiştir" satırı görünür
    Ve bu güncelleme için bildirim gelmez
    Eğer ki "Değiştir" ile alışkanlık 6 gün yapılır
    O zaman alışkanlık 6 gün olarak kaydedilir

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

  @FR-MAZ-05 @otomatik @KT-95
  Senaryo: KS-MAZ-17 On gün dolmadan yeniden başlayan kanama önceki dönemin devamı sayılır
    Diyelim ki kullanıcı K ve hayız G1 08:00 – G4 08:00 kaydedildi
    Ve G5 öğlesi işaretlendi, G5 ikindisinin öneri kartı bekliyor
    Eğer ki G7 08:00'de mazeret başlatılır
    O zaman yeni dönem açılmaz ve "Önceki dönemin devamı olarak kaydedildi" bilgisi görünür
    Ve açıklamada "Kanama, önceki dönemin başlangıcından 10 gün geçmeden yeniden başladı. Hanefî'de aradaki temiz günler de âdetten sayılır." yazar
    Ve geçmiş dönemler listesinde G1 08:00'de başlayıp süren tek dönem görünür
    Ve G4 öğlesinden G7 sabahına kadar işaretlenmemiş kalemler nötr renkte görünür ve G5 ikindisinin öneri kartı kapanır
    Fakat G5 öğlesi "kılındı" olarak kalır

  @FR-MAZ-05 @BR-00 @otomatik @KT-96
  Senaryo: KS-MAZ-18 Birleşen dönemde önceden eklenen borçların geri alınması sorulur
    Diyelim ki KS-MAZ-17'deki durum ve G5 akşamının önerisi onaylanmış, akşam borcu 1
    Ve otomatik borç ekleme açık
    Eğer ki G7 08:00'de mazeret başlatılır
    O zaman "Bu günler âdet döneminden sayıldı. Eklediğin 1 borcu geri alayım mı?" ekranı "Geri al" ve "Kalsın" seçenekleriyle görünür
    Eğer ki "Geri al" seçilir
    O zaman akşam borcu 0 olur ve kaza geçmişinde "düzeltme" kaydı görünür

  @FR-MAZ-05 @otomatik @KT-101
  Senaryo: KS-MAZ-19 Lohusalığın 40 günü içinde yeniden başlayan kanama lohusalığın devamıdır
    Diyelim ki kullanıcı K ve lohusalık G1 08:00 – G11 08:00 kaydedildi
    Eğer ki G20 08:00'de mazeret "Âdet" türüyle başlatılır
    O zaman yeni dönem açılmaz ve "Lohusalığın 40 günü dolmadığı için bu, lohusalığın devamı olarak kaydedildi" bilgisi görünür
    Ve geçmiş dönemler listesinde G1 08:00'de başlayıp süren tek bir lohusalık dönemi görünür
    Ve G11 öğlesinden G20 sabahına kadar işaretlenmemiş kalemler nötr renkte görünür
    Ve "15 günlük temizlik dolmadı" uyarısı çıkmaz
```

## 5. Vakitler

6 senaryo, 4 P0 gereksinim. FR-VKT-05 (asr-ı sânî) hoca görüşüyle kaldırıldı; yalnızca Diyanet vakitlerinin gösterildiğini KS-VKT-06 sınar. Ağ testlerinde ara sunucu sahte bir sunucuyla değiştirilir; elle testte gerçek sunucu kullanılır.

```gherkin
# language: tr
Özellik: Vakitler

  @FR-VKT-01 @otomatik @elle
  Senaryo: KS-VKT-01 Seçili ilçe hiçbir akışta sıfırlanmaz
    Diyelim ki seçili ilçe "Test İlçesi"
    Eğer ki uygulama süreci sonlandırılıp yeniden açılır
    Ve cihaz yeniden başlatılır
    Ve sistem ayarlarından uygulamanın önbelleği temizlenir
    Ve uygulama yeni sürümüyle güncellenir
    O zaman her adımdan sonra seçili ilçe "Test İlçesi" olarak kalır

  @FR-VKT-02 @NFR-OFF-01 @otomatik
  Senaryo: KS-VKT-02 İnternetsiz 30 gün doğru vakit
    Diyelim ki vakitler G1'de indirildi ve cihaz uçak modunda
    Eğer ki saat G30 12:00 olur
    O zaman Bugün ekranındaki vakitler sunucudan gelen G30 vakitleriyle birebir aynıdır
    Ve "tahmini" etiketi görünmez

  @FR-VKT-02 @otomatik
  Senaryo: KS-VKT-03 Önbellek azalınca arka planda yenilenir
    Diyelim ki önbellekte 9 günlük vakit kaldı ve internet var
    Eğer ki arka plan yenileme işi çalışır
    O zaman önbellekte en az 30 günlük vakit bulunur
    Ve kullanıcı hiçbir ekran ya da bildirim görmez

  @FR-VKT-03 @otomatik
  Senaryo: KS-VKT-04 Önbellek bitince vakitler hesaplanır ve etiketlenir
    Diyelim ki önbellek bitti ve internet yok
    Eğer ki uygulama açılır
    O zaman vakitler ilçe koordinatından hesaplanarak gösterilir
    Ve Bugün ekranında, widget'ta ve vakit listesinde "tahmini" etiketi görünür
    Eğer ki internet geri gelir ve vakitler indirilir
    O zaman etiket kaybolur

  @FR-VKT-04 @otomatik
  Senaryo: KS-VKT-05 Sıradaki vakte kalan süre dakika hassasiyetindedir
    Diyelim ki saat G1 14:47
    O zaman Bugün ekranında "İkindiye 43 dk" yazar
    Eğer ki saat 15:29 olur
    O zaman "İkindiye 1 dk" yazar
    Eğer ki saat 15:30 olur
    O zaman "Akşama 2 sa 30 dk" yazar

  @FR-VKT-04 @otomatik
  Senaryo: KS-VKT-06 Yalnızca Diyanet'in vakit saatleri gösterilir
    Eğer ki Bugün, vakit listesi, widget ve Ayarlar ekranları gezilir
    O zaman yalnızca imsak, güneş, öğle, ikindi, akşam ve yatsı saatleri görünür
    Ve hiçbir ekranda ve metin kaynağında "asr-ı evvel", "asr-ı sânî" ya da "işâ-i sânî" geçmez
    Ve ayarlarda vakit hesaplama yöntemi ya da mezhebe göre ikindi seçeneği bulunmaz
```

## 6. Widget ve hatırlatma

16 senaryo, 9 P0 gereksinim (FR-HTR-06 ezan sesi P1'dir). Bu bölümün çoğu gerçek cihazda elle yürütülür: widget ve alarm davranışı emülatörde üreticinin pil kısıtlarını yansıtmaz.

```gherkin
# language: tr
Özellik: Widget

  @FR-WDG-01 @elle
  Senaryo: KS-WDG-01 Küçük widget o anki vakti tek dokunuşla işaretler
    Diyelim ki küçük widget ana ekranda ve saat G1 13:00
    O zaman widget "Öğle", "İkindi 15:30", işleyen geri sayım ve "Kıldım" düğmesini gösterir
    Eğer ki "Kıldım"a dokunulur
    O zaman uygulama açılmaz ve düğme 1 saniye içinde "Kılındı 13:00" durumuna geçer

  @FR-WDG-02 @FR-WDG-03 @elle @KT-01
  Senaryo: KS-WDG-02 Orta widget'ta vakti girmemiş kalem kilitlidir
    Diyelim ki orta widget ana ekranda ve saat G1 13:00
    O zaman sabah ve öğle dokunulabilir, ikindi, akşam, yatsı ve vitir kilitli görünür
    Eğer ki kilitli ikindiye dokunulur
    O zaman ikindi işaretlenmez ve uygulama "Vakti henüz girmedi" açıklamasıyla açılır

  @FR-WDG-03 @elle @KT-04
  Senaryo: KS-WDG-03 Widget uygulamayla aynı kuralları uygular
    Diyelim ki orta widget ana ekranda, saat G1 20:00 ve yatsı boş
    O zaman vitir widget'ta kilitli görünür
    Eğer ki widget'tan yatsı işaretlenir
    O zaman vitrin kilidi açılır

  @FR-WDG-03 @NFR-PRF-02 @elle
  Senaryo: KS-WDG-04 Widget ile uygulama aynı veriyi gösterir
    Eğer ki widget'tan öğle işaretlenir
    O zaman widget 1 saniye içinde güncellenir
    Ve uygulama açılınca öğle "kılındı" görünür
    Eğer ki uygulamada işaret geri alınır ve ana ekrana dönülür
    O zaman widget'ta öğle boş görünür

  @FR-WDG-04 @NFR-SEC-03 @elle
  Senaryo: KS-WDG-05 Mazeret sürerken widget nötr görünür
    Diyelim ki kullanıcı K ve hayız sürüyor
    O zaman widget'ta "mazeret", "âdet", "hayız" ya da benzeri bir kelime yazmaz
    Ve kalemler nötr renkte, "Kıldım" düğmesi görünmez

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

Özellik: Hatırlatma

  @FR-HTR-01 @otomatik @KT-64 @KT-65
  Senaryo: KS-HTR-01 Hatırlatma yalnızca işaretlenmemiş vakit için gelir
    Diyelim ki öğle boş
    Eğer ki saat 15:00 olur
    O zaman "Öğle vaktinin çıkmasına 30 dk" bildirimi gelir
    Diyelim ki ertesi gün öğle 14:00'te işaretlendi
    Eğer ki saat 15:00 olur
    O zaman bildirim gelmez

  @FR-HTR-01 @otomatik @KT-68
  Senaryo: KS-HTR-02 Hatırlatma süresi ve vakitler ayrı ayarlanır
    Diyelim ki öğle için X 45 dk ve sabah hatırlatması kapalı
    O zaman öğle hatırlatması 14:45'te gelir
    Ve sabah için hiç bildirim gelmez

  @FR-HTR-01 @otomatik @KT-66
  Senaryo: KS-HTR-03 Yatsı ve vitir için tek hatırlatma gelir
    Diyelim ki G1 yatsısı ve vitri boş
    Eğer ki saat G2 05:00 olur
    O zaman iki kalemi birlikte anan tek bildirim gelir

  @FR-HTR-02 @elle
  Senaryo: KS-HTR-04 Bildirimden işaretleme ve geri alma
    Diyelim ki öğle hatırlatması bildirim alanında
    Eğer ki bildirimdeki "Kıldım" eylemine dokunulur
    O zaman uygulama açılmaz ve öğle "kılındı" olur
    Ve bildirim "Öğle işaretlendi · Geri al" hâline geçer ve 5 saniye içinde kapanır
    Eğer ki bu 5 saniye içinde "Geri al"a dokunulur
    O zaman öğle boş görünür ve 15:00 hatırlatması yeniden kurulur

  @FR-HTR-03 @NFR-TIM-01 @elle
  Senaryo: KS-HTR-05 Alarmlar yeniden başlatma ve saat değişikliğinden sonra kurulur
    Diyelim ki öğle boş ve hatırlatma 15:00'e kurulu
    Eğer ki cihaz 14:00'te yeniden başlatılır ve kilidi açılmaz
    O zaman hatırlatma 15:00 ile 15:10 arasında gelir
    Eğer ki cihazın saat dilimi Europe/Berlin yapılır
    O zaman hatırlatmalar ilçenin saat dilimine göre yeniden kurulur ve aynı anı hedefler
    Eğer ki uygulama güncellenir ve açılmaz
    O zaman bir sonraki hatırlatma yine zamanında gelir

  @FR-HTR-04 @otomatik
  Senaryo: KS-HTR-06 Mazeret bitince hatırlatmalar kendiliğinden devam eder
    Diyelim ki kullanıcı K ve hayız sürerken hiçbir hatırlatma gelmedi
    Eğer ki bitiş G6 09:00'da kaydedilir ve öğle boş kalır
    O zaman öğle hatırlatması 15:00'te gelir

  @FR-HTR-05 @otomatik @elle
  Senaryo: KS-HTR-07 Bildirimler suçlayıcı dil kullanmaz
    Diyelim ki kullanıcı bugün en az bir vakit işaretledi
    O zaman "X gündür yoksun" türünde hiçbir bildirim gelmez
    Ve uygulamadaki tüm bildirim metinleri metin listesinde "suçlayıcı değil" olarak işaretlenmiştir

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
```

## 7. Yedekleme ve ayarlar

16 senaryo, 7 gereksinim ve DATA-02. Bu gereksinimlerdeki bir hata veri kaybı demektir. Bu yüzden şema geçişi ve içe aktarma senaryoları her sürümde, önceki tüm sürümlerin örnek dosyalarıyla yeniden koşar.

```gherkin
# language: tr
Özellik: Yedekleme

  @FR-YDK-01 @otomatik
  Senaryo: KS-YDK-01 Veri dışa aktarılır ve başka cihazda aynı haliyle açılır
    Diyelim ki 30 günlük takip kaydı, 6 kalemde borç ve kaza geçmişi var
    Eğer ki Ayarlar > Yedekleme'den dışa aktarılır
    O zaman bir yedek dosyası oluşur ve içinde şema sürümü yazar
    Eğer ki dosya temiz kurulmuş ikinci cihazda içe aktarılır
    O zaman takip kayıtları, borçlar ve kaza geçmişi birinci cihazla aynıdır

  @FR-YDK-02 @NFR-REL-01 @otomatik
  Senaryo: KS-YDK-02 Eski sürüm yedeği kayıpsız yüklenir
    Diyelim ki v1.0 ile alınmış örnek yedek dosyası
    Eğer ki dosya uygulamanın güncel sürümünde içe aktarılır
    O zaman dosyadaki her kayıt güncel sürümde görünür ve borçlar aynı hesaplanır

  @FR-YDK-03 @NFR-SEC-02 @otomatik
  Senaryo: KS-YDK-03 Mazeret verisi parolayla şifrelenir
    Diyelim ki kullanıcı K ve 3 mazeret kaydı var
    Eğer ki dışa aktarılır
    O zaman parola ve parola tekrarı istenir; ikisi eşleşmeden dışa aktarılmaz
    Eğer ki dosya başka cihazda yanlış parolayla içe aktarılır
    O zaman takip ve kaza verisi açılır, mazeret kayıtları açılmaz
    Ve "Mazeret verisi için parola yanlış, yeniden dene" seçeneği görünür
    Eğer ki doğru parola girilir
    O zaman 3 mazeret kaydı da açılır

  @FR-YDK-04 @elle
  Senaryo: KS-YDK-04 Android otomatik yedeği hassas veriyi taşımaz
    Diyelim ki kullanıcı K; takip, borç, mazeret kaydı ve âdet alışkanlığı var
    Eğer ki cihazın Google yedeğinden yeni cihaza geri yükleme yapılır
    O zaman takip kayıtları ve borçlar gelir
    Fakat mazeret kayıtları ve âdet alışkanlığı gelmez

  @FR-YDK-05 @NFR-REL-03 @otomatik
  Senaryo: KS-YDK-05 Veritabanı açılamazsa kurtarma ekranı açılır
    Diyelim ki veritabanı dosyası bozuk
    Eğer ki uygulama açılır
    O zaman uygulama takılmadan kurtarma ekranı açılır
    Ve ilk seçenek "Verilerini dışa aktar", son seçenek "Verileri sil"dir
    Eğer ki "Verileri sil" seçilir
    O zaman iki adımlı onay istenir

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

Özellik: Ayarlar

  @FR-AYR-01 @FR-ONB-01 @otomatik @KT-70
  Senaryo: KS-AYR-01 Cinsiyet değişikliği onay ister ve geçmişi silmez
    Diyelim ki kullanıcı K ve G1 – G5 mazeret kaydı var
    Eğer ki G10'da cinsiyet "Erkek" yapılır
    O zaman değişikliğin etkisini anlatan bir onay penceresi açılır
    Eğer ki onaylanır
    O zaman mazeret menüsü gizlenir
    Fakat G1 – G5 takvimde mazeretli görünmeye devam eder

  @FR-AYR-01 @otomatik
  Senaryo: KS-AYR-02 Konum değişikliği onay ister, geçmiş vakitler değişmez
    Diyelim ki seçili ilçe "Test İlçesi" ve geçmişte 10 günlük kayıt var
    Eğer ki ilçe başka bir ilçeyle değiştirilir ve onaylanır
    O zaman bugünden itibaren yeni ilçenin vakitleri kullanılır
    Ve geçmiş 10 günün kayıtları ve önerileri değişmez

  @FR-AYR-01 @otomatik @KT-90
  Senaryo: KS-AYR-03 Gusül süresi 5 – 30 dk arasında kalır
    Eğer ki gusül süresi 4 dk girilmek istenir
    O zaman değer kabul edilmez ve "5 – 30 dk" açıklaması görünür
    Eğer ki gusül süresi 31 dk girilmek istenir
    O zaman değer kabul edilmez ve aynı açıklama görünür
    Eğer ki gusül süresi 20 dk girilir
    O zaman değer kaydedilir

  @FR-AYR-02 @otomatik @KT-69
  Senaryo: KS-AYR-04 Ayar değişikliği geçmişi yeniden hesaplamaz
    Diyelim ki gusül 15 dk iken G6 17:45 bitişi kaydedildi ve G6 ikindisi mazeretli
    Eğer ki G8'de gusül süresi 10 dk yapılır
    O zaman G6 ikindisi mazeretli kalır ve yeni öneri oluşmaz

  @FR-AYR-01 @otomatik
  Senaryo: KS-AYR-05 Kerahat dakikaları ayarlarda yoktur
    Eğer ki Ayarlar'ın tüm bölümleri gezilir
    O zaman kerahat dakikalarını değiştiren bir alan bulunmaz

  @FR-KAZ-03 @BR-00 @otomatik
  Senaryo: KS-AYR-06 Otomatik borç ekleme açılınca bilgi notu bir kez gösterilir
    Eğer ki Ayarlar'da "Otomatik borç ekleme" ilk kez açılır
    O zaman "Belirsiz durumlar yine sorulur" bilgisi görünür
    Eğer ki ayar kapatılıp yeniden açılır
    O zaman bilgi yeniden gösterilmez
```

## 8. Gizlilik, çevrimdışı ve güvenilirlik

9 senaryo. Fonksiyonel olmayan gereksinimlerin çoğu ölçümle doğrulanır (performans, kapsama, Android vitals); bu bölüm yalnızca kullanıcının doğrudan gördüğü olanları senaryoya çevirir.

```gherkin
# language: tr
Özellik: Gizlilik, çevrimdışı çalışma ve güvenilirlik

  @NFR-SEC-03 @elle @KT-41
  Senaryo: KS-NFR-01 Kilit ekranında mazeret ipucu görünmez
    Diyelim ki kullanıcı K, cihaz kilitli ve KS-MAZ-05'teki bildirim geldi
    O zaman kilit ekranında yalnızca "Namaz takibi: yeni bir bilgi var" yazar
    Eğer ki cihazın kilidi açılır
    O zaman bildirimde "İkindi vakti çıkmadan kılabilirsin" yazar ve mazeret kelimesi geçmez

  @NFR-SEC-03 @elle
  Senaryo: KS-NFR-02 Son uygulamalar önizlemesinde ve ekran görüntüsünde mazeret görünmez
    Diyelim ki kullanıcı K ve hayız sürüyor
    Eğer ki mazeret ekranı ya da Bugün ekranı açıkken son uygulamalar listesi açılır
    O zaman uygulamanın önizlemesi boş görünür
    Ve bu iki ekranda ekran görüntüsü alınamaz

  @NFR-OFF-01 @otomatik @elle
  Senaryo: KS-NFR-03 Çekirdek akışlar internetsiz çalışır
    Diyelim ki cihaz uçak modunda ve vakitler önbellekte
    O zaman işaretleme, geri alma, kaza ekleme ve düşme, mazeret başlatma ve bitirme hatasız çalışır
    Ve widget'tan işaretleme çalışır ve hatırlatmalar zamanında gelir
    Ve hiçbir ekranda internet hatası görünmez

  @NFR-REL-02 @otomatik
  Senaryo: KS-NFR-04 Yazma ortasında uygulama öldürülürse veri tutarlı kalır
    Diyelim ki öğle borcu 3
    Eğer ki öğle satırındaki "Kıldım" ile kaza kaydedilirken uygulama süreci öldürülür
    Ve uygulama yeniden açılır
    O zaman öğle borcu ya 3 ya 2'dir
    Ve borç, kaza geçmişindeki kayıtlardan hesaplanan değerle aynıdır

  @NFR-SEC-04 @NFR-SEC-01 @elle
  Senaryo: KS-NFR-05 Yalnızca gerekli izinler istenir
    Eğer ki onboarding ve tüm ayar ekranları gezilir
    O zaman konum, kişiler, kamera ya da depolama izni istenmez
    Ve ara sunucuya giden her istekte yalnızca ilçe kimliği ve ay bulunur

  @NFR-USA-01 @elle
  Senaryo: KS-NFR-06 Bir vakti işaretlemek en fazla iki dokunuştur
    Eğer ki o anki vakit widget'tan işaretlenir
    O zaman 1 dokunuş yeterlidir
    Eğer ki uygulama simgesinden açılıp o anki vakit işaretlenir
    O zaman en fazla 2 dokunuş gerekir

  @NFR-USA-02 @elle
  Senaryo: KS-NFR-07 Akışlar ekran okuyucu ve büyük yazıyla tamamlanır
    Diyelim ki TalkBack açık ve yazı boyutu %200
    O zaman onboarding, işaretleme, kaza ekleme ve düşme ile mazeret başlatma ve bitirme akışları tamamlanır
    Ve her düğme ve kalem TalkBack'te durumuyla okunur, ör. "Öğle, kılındı"

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

## 9. İzlenebilirlik ve kararlar

43 P0 gereksinimin tümü en az bir senaryoyla kapsanıyor; toplam 99 senaryonun 77'si otomatik, 22'si elle ya da kısmen elle yürütülür. Bunların 24'ü ekran taslakları Tur 5'te, 4'ü hoca görüşmelerinden sonra eklendi; liste aşağıda, "Sonradan yapılan değişiklikler" altında.

| Grup | Gereksinim | Senaryolar |
| --- | --- | --- |
| Onboarding | FR-ONB-01 | KS-ONB-01, 09, KS-AYR-01 |
| Onboarding | FR-ONB-02 | KS-ONB-02 |
| Onboarding | FR-ONB-03 | KS-ONB-03, 04 |
| Onboarding | FR-ONB-04 | KS-ONB-05, 06 |
| Onboarding | FR-ONB-05 | KS-ONB-07, KS-TRK-13 |
| Takip | FR-TRK-01 | KS-TRK-01, 02, 11 |
| Takip | FR-TRK-02 | KS-TRK-03 |
| Takip | FR-TRK-03 | KS-TRK-04, 12, 14 |
| Takip | FR-TRK-04 | KS-TRK-05, 06, 13, KS-KAZ-10, KS-WDG-07 |
| Takip | FR-TRK-05 | KS-TRK-07, 11 |
| Takip | FR-TRK-06 | KS-TRK-08, 09, KS-WDG-06 |
| Takip | FR-TRK-07 | KS-TRK-10 |
| Kaza | FR-KAZ-01 | KS-KAZ-01, KS-ONB-04 |
| Kaza | FR-KAZ-02 | KS-KAZ-02, 03 |
| Kaza | FR-KAZ-03 | KS-KAZ-04, 05, 06, 09, KS-AYR-06 |
| Kaza | FR-KAZ-04 | KS-KAZ-07 |
| Kaza | FR-KAZ-05 | KS-KAZ-08, 10 |
| Mazeret | FR-MAZ-01 | KS-MAZ-01, 02, 16 |
| Mazeret | FR-MAZ-02 | KS-MAZ-03, 13 |
| Mazeret | FR-MAZ-03 | KS-MAZ-04 |
| Mazeret | FR-MAZ-04 | KS-MAZ-05, 06, 07, 08 |
| Mazeret | FR-MAZ-05 | KS-MAZ-09, 10, 11, 14, 15, 17, 18, 19 |
| Mazeret | FR-MAZ-06 | KS-MAZ-12, 10 |
| Vakitler | FR-VKT-01 | KS-VKT-01 |
| Vakitler | FR-VKT-02 | KS-VKT-02, 03 |
| Vakitler | FR-VKT-03 | KS-VKT-04, KS-ONB-09 |
| Vakitler | FR-VKT-04 | KS-VKT-05, 06 |
| Widget | FR-WDG-01 | KS-WDG-01, KS-ONB-08 |
| Widget | FR-WDG-02 | KS-WDG-02, 06, 07 |
| Widget | FR-WDG-03 | KS-WDG-02, 03, 04 |
| Widget | FR-WDG-04 | KS-WDG-05 |
| Hatırlatma | FR-HTR-01 | KS-HTR-01, 02, 03 |
| Hatırlatma | FR-HTR-02 | KS-HTR-04, 08 |
| Hatırlatma | FR-HTR-03 | KS-HTR-05, 09 |
| Hatırlatma | FR-HTR-04 | KS-MAZ-03, KS-HTR-06 |
| Hatırlatma | FR-HTR-05 | KS-HTR-07 |
| Yedekleme | FR-YDK-01 | KS-YDK-01 |
| Yedekleme | FR-YDK-02 | KS-YDK-02, 07, 08 |
| Yedekleme | FR-YDK-03 | KS-YDK-03, 06 |
| Yedekleme | FR-YDK-04 | KS-YDK-04 |
| Yedekleme | FR-YDK-05 | KS-YDK-05, 09 |
| Ayarlar | FR-AYR-01 | KS-AYR-01, 02, 03, 05 |
| Ayarlar | FR-AYR-02 | KS-AYR-04 |

**Fonksiyonel olmayan ve veri gereksinimleri.** NFR-SEC-03: KS-WDG-05, KS-NFR-01, 02, 09, KS-MAZ-12. NFR-SEC-01 ve 04: KS-NFR-05. NFR-SEC-02: KS-YDK-03, 04, 09. NFR-OFF-01: KS-VKT-02, KS-NFR-03. NFR-REL-01: KS-YDK-02. NFR-REL-02: KS-NFR-04, KS-YDK-08. NFR-REL-03: KS-YDK-05. NFR-USA-01: KS-NFR-06. NFR-USA-02: KS-NFR-07, 08. NFR-PRF-02: KS-WDG-04. NFR-TIM-01: KS-HTR-05. DATA-02: KS-YDK-10. Kalan NFR'ler (performans, doğruluk, uyumluluk, bakım) senaryoyla değil SRS Bölüm 5'teki ölçütle doğrulanır.

**P1 gereksinimler (v1.1).** Kural düzeyinde karar tablosu kapsıyor; ekran senaryoları v1.1'de yazılır: FR-TRK-08 (KT-40), FR-KAZ-06 (KT-19 – 27, 91), FR-KAZ-07 (KT-28 – 31, 82 – 84, 92, 93), FR-KAZ-08 (KT-71 – 77, 99), FR-KAZ-09 (KT-32, 33), FR-KAZ-10 niyet hatırlatması (kural yok; metin hoca onaylı), FR-HTR-06 (kural yok). FR-VKT-05 ve FR-AYR-03 SRS 1.0-taslak.7'de kaldırıldı.

### Bu belgede alınan kararlar

Senaryoları yazarken SRS'in tanımlamadığı 5 davranış çıktı. Hepsine karar verildi ve SRS 1.0-taslak.5'e işlendi.

- [x] **1. Kurulumdan önceki vakitler (FR-ONB-05; KS-ONB-07):** Kurulumdan önce vakti çıkmış namazlar için öneri üretilmez. Gerekçe: kullanıcının o vakitleri kılıp kılmadığı bilinmiyor; ilk açılışta borç önerisi görmek, 5te5 yorumlarında şikayet edilen "suçlayıcı başlangıç" hissini doğurur. Geçmiş borç kaza adımından girilir.
- [x] **2. Namaz günü (FR-TRK-01, FR-TRK-05; KS-TRK-11):** Uygulamadaki "gün" imsakta başlar, ertesi imsakta biter. Bugün ekranı ve widget gece yarısından imsaka kadar önceki namaz gününü gösterir. Gerekçe: yatsı ve vitrin vakti imsaka kadar sürer; gece yarısı gün değiştirmek, gece 00:30'da vitir kılan kullanıcıya boş bir yeni gün gösterirdi.
- [x] **3. Mazeret bildirimleri (NFR-SEC-03; KS-MAZ-05, KS-MAZ-10, KS-NFR-01):** Mazeretten doğan bildirimlerin metni nötrdür ("İkindi vakti çıkmadan kılabilirsin"); açıklama yalnızca uygulama içinde görünür. Kilit ekranında genel metin gösterilir. Gerekçe: karar tablosundaki KT-41 ve KT-55 metinleri NFR-SEC-03 ile çelişiyordu; karar tablosu v1.2'de bu metinler düzeltildi.
- [x] **4. Widget'ta kilitli kalem (FR-WDG-02; KS-WDG-02):** Kilitli kaleme dokunmak işaret koymaz; uygulamayı açıklamayla açar. Gerekçe: widget açıklama penceresi gösteremez; sessiz kalmak kullanıcıya "bozuk" görünür.
- [x] **5. Yedek parolası (FR-YDK-03; KS-YDK-03):** Parola en az 8 karakterdir ve iki kez girilir; parola unutulursa mazeret verisi kurtarılamaz ve bu, dışa aktarma ekranında yazar. Gerekçe: parola saklanmadığı için (NFR-SEC-02) kurtarma yolu yoktur; kullanıcı bunu önceden bilmelidir.

### Sonradan yapılan değişiklikler

- [x] **27 Ekim 2026 · Ekran taslakları Tur 3, karar 1 (KS-MAZ-09):** Bitiş G3 07:59'dan G4 07:59'a çekildi, kalem sayısı 12'den 18'e çıktı. Önceki aralık 47 saat 59 dakikaydı ve 72 saatlik sınırı sınamıyordu. Karar tablosu v1.3'te KT-52 ve KT-80 aynı şekilde düzeltildi.
- [x] **27 Ekim 2026 · Ekran taslakları Tur 3, karar 2 (KS-KAZ-02, 03, 07, KS-NFR-04):** Kılınan kaza, Kaza ekranında ilgili vaktin satırındaki "Kıldım" düğmesiyle kaydedilir. KS-KAZ-03 "Kaydet düğmesi pasif" yerine "vakit belirtmeden kaza kaydeden bir eylem bulunmaz" olarak yeniden yazıldı. FR-KAZ-02'nin kabul kriteri ("vakit seçilmeden kaydedilemez") değişmedi; satır, vakit seçiminin kendisidir. KT-36 kural motoru düzeyinde geçerliliğini korur.
- [x] **27 Ekim 2026 · Ekran taslakları Tur 5 (kabul senaryolarına karşı kontrol):** Tasarım kararlarıyla uyuşmayan 7 senaryo güncellendi: KS-ONB-03 ve 04 (düğme adları, "Henüz kaza borcu girmedin" kartı, "Başlangıç borcu" satırı), KS-KAZ-05 (öneriler Bekleyen öneriler ekranında), KS-MAZ-03 (mazeretli kaleme dokunmak açıklama gösterir; seri adımı FR-TRK-08 P1 olduğu için çıkarıldı), KS-WDG-01 (işleyen geri sayım, "Kılındı 13:00"), KS-HTR-04 (5 saniyelik "Geri al"), KS-NFR-02 (Bugün ekranı ve ekran görüntüsü engeli). Tasarımda eklenen ama senaryosu olmayan davranışlar için 24 senaryo eklendi; toplam 71'den 95'e çıktı. Değişikliklerin SRS karşılığı 1.0-taslak.6'da (DT-1 – DT-15).
- [x] **28 Ekim 2026 · Hoca görüşü (SRS 1.0-taslak.7, karar tablosu v1.4):** Ortak ön koşullarda kerahat 50/45/50'den 45/45/45'e indi ve kullanıcı ayarından kural parametresine taşındı. 3 senaryo güncellendi: KS-MAZ-11 (15 gün dolmadan gelen kan artık "sayılmayabilir" değil "âdet sayılmaz"; varsayılan seçenek başlatmamak), KS-MAZ-12 (alışkanlık tek dönemde, sorulmadan güncellenir), KS-AYR-03 (gusül aralığı 5–30 dk). KS-AYR-05 artık geçici değil kalıcı bir davranışı sınar. 3 senaryo eklendi: KS-MAZ-17 ve 18 (kesilip yeniden başlayan kanama), KS-VKT-06 (yalnızca Diyanet vakitleri). Toplam 95'ten 98'e çıktı. KS-MAZ-11, 12, 17 ve 18'deki yeni metinler SRS Bölüm 10'daki ekran metni kontrolüne dahil.
- [x] **28 Ekim 2026 · İkinci hoca görüşmesi (SRS 1.0-taslak.8, karar tablosu v1.5):** KS-MAZ-10'a 10 günü aşan dönemden sonra alışkanlığın 10 güne güncellendiği adım eklendi. Lohusalıkta 40 gün içindeki ara temizlik için KS-MAZ-19 eklendi; toplam 99 senaryo.

### Uygulama notları

- Cucumber bir dosyada tek Özellik kabul eder. Her Özellik ayrı bir `.feature` dosyasına konur ve "Ortak ön koşullar"daki Geçmiş bloğu her dosyanın başına eklenir.
- Bir KT satırı değişirse etiketinden (`@KT-41` gibi) etkilenen kabul senaryoları aramayla bulunur.

## Kaynaklar

- [Gherkin languages: Turkish keywords](https://cucumber.io/docs/gherkin/languages) — Cucumber
- Proje dosyaları: gereksinim-analizi-srs.md (1.0-taslak.8), karar-tablosu.md (v1.5), fikhi-gereksinimler.md, hocaya-sorular.md, ekran-taslaklari-tur1.md – ekran-taslaklari-tur5.md

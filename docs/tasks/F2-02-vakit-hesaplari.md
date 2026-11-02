# Görev Kartı F2-02 — Vakit hesapları

Faz 2 · Kart 2/13 · 2 Kasım 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

Kural motorunun bütün kararları "şu an hangi vakitteyiz, bu vakit ne zaman bitiyor, bugün hangi namaz günü" sorularına dayanır. Bu kart bu soruların cevabını saf fonksiyonlar olarak yazar:

1. **Vakit aralıkları:** Bir namaz gününün altı kaleminin `[başlangıç, bitiş)` aralıkları (BR-02).
2. **"Kaçırıldı" anları:** Her kalemin vaktinin çıktığı an (BR-04).
3. **Şu anki vakit:** Verilen anda hangi farz vaktin içindeyiz, ya da iki vakit arasında mıyız (güneş ile öğle arası).
4. **Kerahat pencereleri:** Güneşten sonra, öğleden önce ve akşamdan önce; süreler `RuleSet`'ten (BR-07).
5. **Namaz günü:** Gece yarısından imsaka kadar önceki namaz günü geçerlidir (FR-TRK-05, KS-TRK-11).
6. **Yaz saati:** Bütün hesaplar `Instant` üzerinden yapıldığı için yaz saati geçiş gecelerinde de doğrudur; bunu bir Avrupa bölgesiyle test ederek kanıtlar (NFR-CMP-02).

Ayrıca F2-01 incelemesinden çıkan üç küçük temizlik bu kartta yapılır (Bölüm C).

**Bu kart karar tablosunun hiçbir satırını kapatmaz.** KT satırları "öğleye dokunulur → engel" gibi işaretleme, öneri ve kaza davranışlarıdır; onlar F2-04 ve sonrasının işi. Bu kartın testleri BR, M ve NFR kimlikleriyle adlandırılır. Bekleyen KT listesi değişmez; kart sonunda KT kapısı yine "101 geçerli satır, 0 testli, 101 bekliyor" der.

## Ön koşullar

1. `main`, F2-01'in birleştirildiği `9631cc3` commit'inde ya da daha ilerde ve `origin/main` ile aynı.
2. Kullanıcı şu dört dosyayı çalışma ağacına koymuş olmalı (zip):
   - `CLAUDE.md` (sürüm 1.7)
   - `docs/mimari-ve-kodlama-ilkeleri.md` (sürüm 1.7)
   - `docs/gelistirme-yol-haritasi.md` (F2-01 tamamlandı güncellemesi)
   - `docs/tasks/F2-02-vakit-hesaplari.md` (bu kart)
3. Çalışma ağacında bu dört dosya dışında yalnızca F1-05'e ait iki izlenmeyen dosya bulunabilir (`docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md`). Bunlara **dokunma ve commit'leme.** Başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler ve kimlikler

- `CLAUDE.md` (tamamı; 1.7'de Bölüm 7 "Zaman" ve Bölüm 11 "PR başlığı" değişti)
- `docs/mimari-ve-kodlama-ilkeleri.md`: M-23 (1.7'de ilçe saat dilimi kuralı eklendi), M-24, M-25, K-03, K-12, K-15 – K-17
- `docs/gereksinim-analizi-srs.md`: BR-02, BR-03, BR-04, BR-07; FR-TRK-05; Bölüm 1.3 "Namaz günü"; NFR-CMP-02; DATA-03
- `docs/karar-tablosu.md`: "Sözleşmeler" (zaman semantiği, referans gün fikstürü, fikstürden türeyen pencereler). Tabloların satırlarını bu kartta uygulamıyorsun; yalnızca pencere değerlerini doğrulamak için oku.
- `docs/kabul-senaryolari.md`: KS-TRK-11
- `docs/tasks/F2-01-temel-model.md` ve `docs/tasks/F2-01-rapor.md`: `RuleSet`, `PrayerTimesDay`, `ReferenceDay` ve iki kapının nasıl kurulduğu

## Adım 0 — Plan ve onay

Bu adımda dosya okuyabilir, resmî belgelere bakabilir ve salt okunur git komutlarını (`git status`, `git branch -a`, `git log`, `git fetch`, `git stash list`) çalıştırabilirsin. Hiçbir dosyayı değiştirme, dal değiştirme. Eski sohbet kayıtlarını (`~/.claude` altı) **okuma.**

Plana şunları yaz, sonra **kullanıcının onayını bekle:**

1. **Git başlangıcı:** `git fetch` sonrası `main` ile `origin/main`'in aynı olduğu ve `9631cc3`'ü içerdiği; çalışma ağacındaki her değişikliğin hangi gruba girdiği (bu kartın dört dosyası / F1-05'in iki izlenmeyen dosyası / beklenmeyen). Yerelde `feat/f2-01-domain-foundation` dalı hâlâ duruyorsa yaz; **silme**, kullanıcıya bırak.
2. **Dosya ağacı ve imzalar:** Bölüm A'daki tiplerin paketleri ve imzaları. Karttaki imzalardan sapacaksan her sapmanın gerekçesi.
3. **Test listesi:** Her testin adı, sınadığı şey ve `argumentSet` satırlarının listesi (A1 – A5). Hiçbir test adı ya da `argumentSet` adı `KT` ile başlamaz.
4. **Java zaman API'si notları:** `ZonedDateTime`/`LocalDateTime.atZone` yaz saati boşluğunda ve çakışmasında ne yapar (bkz. tuzak 3); testlerde bu yüzden hangi anların `Instant` olarak verileceği.
5. **Bölüm C temizliklerinin planı.**
6. **Commit sırası** (bkz. Bölüm D).

Onaydan sonra planı, onay mesajındaki değişikliklerle birlikte `docs/tasks/F2-02-plan.md` dosyasına yaz.

## Bölüm A — Vakit hesapları (TDD)

Dal: güncel `main`'den `feat/f2-02-prayer-time-windows`. İlk commit, kullanıcının koyduğu dört belgedir; dosyaları **adıyla** ekle:

```
git add CLAUDE.md docs/mimari-ve-kodlama-ilkeleri.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-02-vakit-hesaplari.md
git commit -m "docs: add F2-02 card and update planning docs"
```

İkinci commit plan dosyasıdır: `docs(tasks): add F2-02 plan`.

Her A adımında sıra F2-01'deki gibidir: testi yaz → `.\gradlew.bat :core:domain:test` ile derlenmediğini ya da kırmızı olduğunu gör ve rapora bir satır yaz → kodu yaz → `.\gradlew.bat :core:domain:test` ve `.\gradlew.bat ktlintCheck` yeşil → commit.

**Genel kurallar:**

- Bütün yeni tipler `com.eceakin.namaztakibi.domain.times` paketindedir.
- Hesaplar `Instant` üzerinden yapılır. Yerel saat (`LocalTime`, `LocalDateTime`) yalnızca namaz gününün takvim tarihini bulmak için kullanılır (A5).
- `Clock` yalnızca "şu an" içindir (`clock.instant()`); `clock.zone` hiçbir hesapta kullanılmaz (CLAUDE.md Bölüm 7, M-23). Bölge gerekiyorsa ayrı bir `ZoneId` parametresiyle gelir.
- Fıkhi sayılar yalnızca `RuleSet`'ten okunur. Bu kartta kullanılan tek fıkhi sayılar üç kerahat süresidir.
- Beklenen sonuçlar `sealed` tiplerle döner; "yok" anlamında `null` döndürülmez (K-03, M-24). Tutarsız girdi (ör. yanlış günün vakitleri) programlama hatasıdır: `require(...)`.
- Fıkhi kural uygulayan her fonksiyonun yorumunda BR kimliği yazar (K-05).

### A1 — `TimeWindow`

```kotlin
data class TimeWindow(val start: Instant, val end: Instant) {
    // init: require(start < end)
    operator fun contains(instant: Instant): Boolean   // start <= instant < end
    val duration: Duration                             // Duration.between(start, end)
}
```

**Testler** (`TimeWindowTest`):

- `M24_baslangicBitistenOnceOlmali`: `start == end` ve `start > end` reddedilir.
- `M23_aralikBaslangicDahilBitisHaric`: `start` içeride, `end` dışarıda, `end` − 1 dk içeride, `start` − 1 dk dışarıda. Karar tablosu "Zaman semantiği"ni sınar.
- `duration` doğru hesaplanır.

Commit: `feat(domain): add TimeWindow`

### A2 — `PrayerDayWindows`: vakit aralıkları ve "kaçırıldı" anları (BR-02, BR-03, BR-04)

```kotlin
data class PrayerDayWindows(val day: PrayerTimesDay, val next: PrayerTimesDay) {
    // init:
    //   require(next.district == day.district)
    //   require(next.date == day.date.plusDays(1))
    //   require(day.yatsi < next.imsak)
    fun windowOf(prayer: Prayer): TimeWindow
    fun missedAt(prayer: Prayer): Instant
}
```

`day` namaz gününün kendi vakitleridir, `next` ertesi günün vakitleridir; `next`'ten yalnızca `imsak` kullanılır.

`windowOf` değerleri (BR-02, BR-04). **Aynen bunlar; yorumlama yok:**

| Kalem | Başlangıç | Bitiş | Kaynak |
| --- | --- | --- | --- |
| `SABAH` | `day.imsak` | `day.gunes` | BR-02, BR-04 |
| `OGLE` | `day.ogle` | `day.ikindi` | BR-02, BR-04 (öğle, öğle ile ikindi arasıdır) |
| `IKINDI` | `day.ikindi` | `day.aksam` | BR-02, BR-04 |
| `AKSAM` | `day.aksam` | `day.yatsi` | BR-02, BR-04 |
| `YATSI` | `day.yatsi` | `next.imsak` | BR-02, BR-04 |
| `VITIR` | `day.yatsi` | `next.imsak` | BR-03, BR-04 |

- `VITIR`'in penceresi yatsınınkiyle aynıdır. "Vitir ancak yatsı işaretlendikten sonra işaretlenir" kuralı bir zaman kuralı değil, kayıt kuralıdır; **F2-04'ün işidir, burada uygulanmaz.**
- `[day.gunes, day.ogle)` aralığı hiçbir kalemin penceresi değildir.
- `missedAt(prayer)` = `windowOf(prayer).end`. Ayrı bir fonksiyon olarak durur, çünkü BR-04 "kaçırıldı anı"nı ayrı bir kural olarak tanımlar ve ileride (F2-04, F2-10) bu adla çağrılacak. Yorumunda BR-04 yazar.

**Testler** (`PrayerDayWindowsTest`, `ReferenceDay.times(1)` ve `ReferenceDay.times(2)` ile):

- `BR02_BR04_vakitAraliklari`: `@ParameterizedTest`, altı kalem için birer `argumentSet` (`"BR-02 SABAH 05:30-07:00"` gibi); beklenenler `ReferenceDay.at(...)` ile yazılır. `YATSI` ve `VITIR` için bitiş `ReferenceDay.at(2, "05:30")`.
- `BR04_kacirildiAnlari`: `@ParameterizedTest`, altı kalem: sabah 07:00, öğle 15:30, ikindi 18:00, akşam 19:30, yatsı ve vitir G2 05:30.
- `BR02_gunesIleOgleArasiHicbirKaleminPenceresiDegil`: 07:00, 09:00 ve 12:29 hiçbir kalemin penceresinde değil; 12:30 öğlenin penceresinde.
- `M24_gecersizGunCiftiReddedilir`: `@ParameterizedTest`; farklı ilçe, ardışık olmayan tarih (`next` = G3), `day.yatsi >= next.imsak`. Bozuk nesneler `Executable` içinde kurulur (F2-01'deki gibi).

Commit: `feat(domain): add prayer day windows (BR-02, BR-04)`

### A3 — Şu anki vakit

```kotlin
sealed interface CurrentVakit {
    /** Verilen an bir farz vaktin içinde. [prayer] SABAH, OGLE, IKINDI, AKSAM ya da YATSI'dır. */
    data class Within(val prayer: Prayer, val window: TimeWindow) : CurrentVakit
    /** Verilen an güneş ile öğle arasında; hiçbir farz vakit içinde değil. */
    data class Outside(val nextPrayer: Prayer, val nextStart: Instant) : CurrentVakit
}

// PrayerDayWindows içinde:
fun currentVakit(clock: Clock): CurrentVakit
```

- `clock.instant()`, `[day.imsak, next.imsak)` aralığında olmalı; değilse `IllegalArgumentException` (bu an başka bir namaz gününe aittir; doğru günü bulmak A5'in işi).
- `Within.prayer` hiçbir zaman `VITIR` olmaz. Yatsı penceresindeki an `Within(YATSI, …)` döner; vitrin durumu F2-04'te kayıtlarla birlikte değerlendirilir.
- `[day.gunes, day.ogle)` → `Outside(nextPrayer = OGLE, nextStart = day.ogle)`. Başka bir `Outside` durumu yoktur.

**Testler** (`CurrentVakitTest`):

- `BR02_BR04_suAnkiVakit`: `@ParameterizedTest`; her satır bir `argumentSet` (`"BR-02 G1 12:30 OGLE"` gibi), saat `ReferenceDay.clockAt(...)` ile:

  | An | Beklenen |
  | --- | --- |
  | G1 05:30 | `Within(SABAH)` |
  | G1 06:59 | `Within(SABAH)` |
  | G1 07:00 | `Outside(OGLE, G1 12:30)` |
  | G1 12:29 | `Outside(OGLE, G1 12:30)` |
  | G1 12:30 | `Within(OGLE)` |
  | G1 15:29 | `Within(OGLE)` |
  | G1 15:30 | `Within(IKINDI)` |
  | G1 17:59 | `Within(IKINDI)` |
  | G1 18:00 | `Within(AKSAM)` |
  | G1 19:29 | `Within(AKSAM)` |
  | G1 19:30 | `Within(YATSI)` |
  | G2 00:30 | `Within(YATSI)` |
  | G2 05:29 | `Within(YATSI)` |

  `Within` satırlarında dönen `window`, A2'deki `windowOf` ile aynı olmalı.
- `BR04_saniyeliAnSinirinDogruTarafinda`: G1 15:29:59 → `Within(OGLE)`; G1 15:30:00 → `Within(IKINDI)`. Saniyeli saat için `Clock.fixed(ReferenceDay.at(1, "15:29").plusSeconds(59), ReferenceDay.ZONE)`.
- `M23_namazGunuDisindakiAnReddedilir`: G1 05:29 ve G2 05:30 → `IllegalArgumentException`.

Commit: `feat(domain): add current vakit (BR-02, BR-04)`

### A4 — Kerahat pencereleri (BR-07)

```kotlin
enum class KerahatKind { GUNES, ISTIVA, AKSAM }

data class KerahatWindow(val kind: KerahatKind, val window: TimeWindow)

sealed interface KerahatStatus {
    data object None : KerahatStatus
    data class Inside(val kerahat: KerahatWindow) : KerahatStatus
}

// PrayerDayWindows içinde:
fun kerahatWindows(ruleSet: RuleSet): List<KerahatWindow>     // sıra: GUNES, ISTIVA, AKSAM
fun kerahatStatus(clock: Clock, ruleSet: RuleSet): KerahatStatus
```

Pencereler (BR-07). **Aynen bunlar:**

| Tür | Başlangıç | Bitiş |
| --- | --- | --- |
| `GUNES` | `day.gunes` | `day.gunes + ruleSet.kerahatAfterGunes` |
| `ISTIVA` | `day.ogle − ruleSet.kerahatBeforeOgle` | `day.ogle` |
| `AKSAM` | `day.aksam − ruleSet.kerahatBeforeAksam` | `day.aksam` |

- `kerahatStatus`: `clock.instant()` A3'teki gibi `[day.imsak, next.imsak)` içinde olmalı; değilse `IllegalArgumentException`.
- Bu kart yalnızca "bu an bir kerahat penceresinde mi" sorusunu cevaplar. **Kerahatte kazanın engellenmesi ve o günün ikindisinin istisnası (BR-07'nin geri kalanı, KT-19 – KT-27, KT-91) F2-05'in işidir.**
- Pencerelerin birbiriyle ya da vakitlerle çakışması denetlenmez.

**Testler** (`KerahatTest`):

- `BR07_kerahatPencereleri`: `RuleSet.HANEFI` ile üç pencere karar tablosunun "Fikstürden türeyen pencereler" tablosuyla aynı: `[07:00, 07:45)`, `[11:45, 12:30)`, `[17:15, 18:00)`.
- `BR07_kerahatSinirlari`: `@ParameterizedTest`:

  | An (G1) | Beklenen |
  | --- | --- |
  | 06:59 | `None` |
  | 07:00 | `Inside(GUNES)` |
  | 07:44 | `Inside(GUNES)` |
  | 07:45 | `None` |
  | 11:44 | `None` |
  | 11:45 | `Inside(ISTIVA)` |
  | 12:29 | `Inside(ISTIVA)` |
  | 12:30 | `None` |
  | 17:14 | `None` |
  | 17:15 | `Inside(AKSAM)` |
  | 17:59 | `Inside(AKSAM)` |
  | 18:00 | `None` |

- `BR07_pencereSuresiRuleSettenGelir`: `RuleSet.HANEFI.copy(kerahatAfterGunes = Duration.ofMinutes(40))` ile `GUNES` penceresi `[07:00, 07:40)`; 07:40 → `None`. **Bu testin adı `KT27_` ile başlamaz;** KT-27 satırı F2-05'te kaza eylemiyle birlikte sınanacak. Adı `KT` ile başlarsa KT kapısı "testi var ama bekleyen listede" diye kırmızıya döner.

Commit: `feat(domain): add kerahat windows (BR-07)`

### A5 — Namaz günü (FR-TRK-05, KS-TRK-11, M-23)

Dosya: `PrayerDate.kt`, üst düzey fonksiyon:

```kotlin
/**
 * Verilen anın ait olduğu namaz gününün tarihi. Namaz günü imsakta başlar; gece yarısından
 * imsaka kadar önceki namaz günü geçerlidir (SRS 1.3, FR-TRK-05).
 *
 * @param zone İlçenin saat dilimi (DATA-03). [clock]'un bölgesi kullanılmaz (M-23).
 * @param localDayTimes Anın [zone]'daki takvim gününe ait vakitler.
 */
fun prayerDateOf(clock: Clock, zone: ZoneId, localDayTimes: PrayerTimesDay): LocalDate
```

- `val localDate = clock.instant().atZone(zone).toLocalDate()`
- `require(localDayTimes.date == localDate)`; değilse `IllegalArgumentException`.
- `clock.instant() < localDayTimes.imsak` ise `localDate.minusDays(1)`, değilse `localDate`.

**Testler** (`PrayerDateTest`):

- `KS_TRK_11_geceYarisindanImsakaOncekiNamazGunu`: `@ParameterizedTest`, `localDayTimes` her satırda anın yerel gününe ait `ReferenceDay.times(n)`:

  | An | `localDayTimes` | Beklenen namaz günü |
  | --- | --- | --- |
  | G1 23:59 | `times(1)` | G1 |
  | G2 00:00 | `times(2)` | G1 |
  | G2 00:30 | `times(2)` | G1 |
  | G2 05:29 | `times(2)` | G1 |
  | G2 05:29:59 | `times(2)` | G1 |
  | G2 05:30 | `times(2)` | G2 |

- `M23_yanlisGununVakitleriReddedilir`: G2 00:30 için `times(1)` verilirse `IllegalArgumentException`.
- `M23_saatinBolgesiKullanilmaz`: Saat `Clock.fixed(ReferenceDay.at(2, "01:00"), ZoneOffset.UTC)`, `zone = ReferenceDay.ZONE`, `localDayTimes = times(2)` → beklenen G1. Bu an UTC'de 2 Mart 22:00'dir (G1), İstanbul'da 3 Mart 01:00'dir (G2). Fonksiyon yanlışlıkla `clock.zone`'u kullanırsa yerel günü G1 bulur, `require` kırılır ve test başarısız olur.

Commit: `feat(domain): add prayer date resolution (FR-TRK-05, M-23)`

### A6 — Yaz saati geçişleri (NFR-CMP-02, K-12)

Bu testler kod zaten yazıldıktan sonra eklenir; kırmızı görülmesi beklenmez, yeşil olmaları beklenir. Kırmızı çıkarsa test değil kod yanlıştır; düzelt ve rapora yaz.

Test dosyası: `DaylightSavingTest`. Bölge `Europe/Berlin`. **Vakitler uydurmadır, Diyanet verisi değildir**; yalnızca geçiş gecesini sınamak için seçildi ve hiçbiri 02:00 – 03:00 arasına düşmez. Her gün aynı yerel saatler: imsak 05:00, güneş 06:30, öğle 12:30, ikindi 15:30, akşam 18:30, yatsı 20:00. Testteki küçük bir yardımcı bu yerel saatlerden `PrayerTimesDay` kurar (`ReferenceDay`'e ekleme yapma; o İstanbul fikstürüdür).

2026'da Berlin'de yaz saati 29 Mart Pazar 02:00 CET'te başlar (saat 03:00 CEST'e atlar) ve 25 Ekim Pazar 03:00 CEST'te biter (saat 02:00 CET'e döner). Aşağıdaki UTC değerleri planlamada `zoneinfo` ile hesaplandı; testte beklenenler **`Instant.parse(...)` ile UTC olarak** yazılır, yerel saatten türetilmez:

| Gün | İmsak | Güneş | Öğle | İkindi | Akşam | Yatsı |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-03-28 (CET, +01) | 04:00Z | 05:30Z | 11:30Z | 14:30Z | 17:30Z | 19:00Z |
| 2026-03-29 (CEST, +02) | 03:00Z | 04:30Z | 10:30Z | 13:30Z | 16:30Z | 18:00Z |
| 2026-10-24 (CEST, +02) | 03:00Z | 04:30Z | 10:30Z | 13:30Z | 16:30Z | 18:00Z |
| 2026-10-25 (CET, +01) | 04:00Z | 05:30Z | 11:30Z | 14:30Z | 17:30Z | 19:00Z |

Testler:

- `NFR_CMP_02_ilkbaharGecisi_yatsiPenceresi8Saat`: `PrayerDayWindows(28 Mart, 29 Mart).windowOf(YATSI)` = `[2026-03-28T19:00:00Z, 2026-03-29T03:00:00Z)`, `duration` = 8 saat. (Duvar saatinde 20:00 → 05:00 dokuz saat görünür; bir saat kaybolur.)
- `NFR_CMP_02_sonbaharGecisi_yatsiPenceresi10Saat`: `PrayerDayWindows(24 Ekim, 25 Ekim).windowOf(YATSI)` = `[2026-10-24T18:00:00Z, 2026-10-25T04:00:00Z)`, `duration` = 10 saat.
- `NFR_CMP_02_ilkbaharGecisi_namazGunu`: `zone = Europe/Berlin`, `localDayTimes` = 29 Mart:
  - `2026-03-29T00:59:00Z` (yerel 01:59 CET) → 28 Mart
  - `2026-03-29T01:30:00Z` (yerel 03:30 CEST) → 28 Mart
  - `2026-03-29T03:00:00Z` (yerel 05:00 CEST, imsak) → 29 Mart
- `NFR_CMP_02_sonbaharGecisi_ikiKezYasanan0230`: `localDayTimes` = 25 Ekim:
  - `2026-10-25T00:30:00Z` (ilk 02:30, CEST) → 24 Ekim
  - `2026-10-25T01:30:00Z` (ikinci 02:30, CET) → 24 Ekim
  - `2026-10-25T04:00:00Z` (yerel 05:00 CET, imsak) → 25 Ekim
- `NFR_CMP_02_gecisGunundeVakitlerUtcDogru`: 29 Mart'ın öğle penceresi `[10:30Z, 13:30Z)`; 25 Ekim'in öğle penceresi `[11:30Z, 14:30Z)`. Bunun için `next` olarak 30 Mart ve 26 Ekim günleri de yardımcıyla kurulur.

Commit: `test(domain): add daylight saving transition tests (NFR-CMP-02)`

## Bölüm B — Kapılar

Kapılara kod değişikliği gerekmez. Bu kartın bütün yeni testleri BR, M, KS ve NFR kimlikli olduğu için:

- `.\gradlew.bat checkKtTraceability` → `KT izlenebilirlik: 101 geçerli satır, 0 testli, 101 bekliyor, fark yok.` olmalı. `ktPending`'e **dokunma.**
- `.\gradlew.bat :core:domain:jacocoTestCoverageVerification` yeşil olmalı. Satır ve dal kapsamını rapora yaz.

## Bölüm C — F2-01 incelemesinden temizlikler

Planlama F2-01 PR #5'te üç öneri yazdı. Davranış değiştirmeyen düzeltmelerdir:

1. **`build.gradle.kts`, `checkKtTraceability`:** Tablo dosyası okunamadığında (yok ya da okuma hatası) şu an "KT satırı bulunamadı" mesajı çıkıyor. Ayrı bir mesaj ver: `KT kapısı: docs/karar-tablosu.md okunamadı: <hata mesajı>.` "KT satırı bulunamadı" mesajı yalnızca dosya okunduğu hâlde geçerli satır çıkmadığında kalır.
2. **Aynı görev, ek uyumu:** `"$id'in testi var ama bekleyen listede duruyor..."` Türkçe ek uyumunu bozuyor (`KT-02'in`). Eksiz yaz: `"KT kapısı: $id kimliğinin testi var ama bekleyen listede duruyor. Kimliği ktPending'den sil."`
3. **`PrayerTimesDayTest`:** `baseDay()` ve `baseDayForArguments()` aynı nesneyi kuruyor. Yalnızca companion'daki fonksiyonu bırak ve testleri ona bağla. Test sayısı ve adları değişmez.

**Doğrulama (commit edilmez):** 1 ve 2 için kapının negatif denemelerini yeniden yap: `ktPending`'e geçici `KT-14` ekleyerek "geçersiz kimlik", geçici `@Test fun KT01_kapiDenemesi() {}` ile "testi var" mesajını gör; tablo yolunu geçici olarak yanlış bir adla değiştirerek "okunamadı" mesajını gör. Denemeler commit'ten **sonra** yapılır ve yalnızca dokunulan dosyada `git restore <yol>` ile geri alınır; ardından `git diff` boş olmalı (F2-01 D1 ile aynı kural). Mesajları rapora yaz.

Commit: `refactor: apply F2-01 review cleanups`

## Bilinen tuzaklar

1. **`clock.zone` kullanma.** Testlerde `ReferenceDay.clockAt` İstanbul bölgeli bir saat verir; kod yanlışlıkla `clock.zone`'u kullansa da İstanbul testleri geçer. `M23_saatinBolgesiKullanilmaz` bu hatayı yakalamak için var; onu atlama.
2. **Namaz günü ≠ takvim günü.** `PrayerDayWindows(day, next)` bir namaz gününü anlatır ve `[day.imsak, next.imsak)` aralığını kapsar. G2 00:30, takvimde G2'dir ama G1'in namaz günündedir.
3. **Yaz saati boşluğu ve çakışması.** Java'da `LocalDateTime.atZone(...)` boşluktaki bir saati (29 Mart 02:30 Berlin) **ileri kaydırır** (03:30 CEST), çakışmadaki bir saat için (25 Ekim 02:30) **önceki ofseti** seçer. Bu yüzden A6'da sınanan anlar yerel saatten değil `Instant.parse("...Z")` ile verilir. Fikstür vakitleri bilerek 02:00 – 03:00 dışında seçildi.
4. **KT ile başlayan test adı yok.** Bu karttaki bir test KT satırına benzese de (ör. kerahat süresinin `RuleSet`'ten gelmesi ile KT-27) adı `KT` ile başlamaz; aksi hâlde KT kapısı kırmızıya döner. KT satırlarının testleri kendi kartlarında yazılır.
5. **Vitir burada özel davranmaz.** Vitrin penceresi yatsınınkiyle aynıdır; "önce yatsı" kuralı F2-04'te. `CurrentVakit.Within` hiçbir zaman `VITIR` döndürmez.
6. **Kerahatte "engel" yok.** `KerahatStatus.Inside` bir bilgi sonucudur. Kaza engeli ve ikindi istisnası F2-05'te.
7. **Sıradaki vakte kalan süre (KS-VKT-05) bu kartta yok.** Geri sayımın güneş vaktini sayıp saymayacağı Faz 5'te karara bağlanacak; `Outside.nextStart` yalnızca güneş–öğle arası için var.
8. **Saniye yuvarlama yok.** Vakit sınırları tam dakikada olduğu için 15:29:59 zaten 15:30'dan önce gelir; anı dakikaya yuvarlama.
9. **`git add -A`, `git add .`, `stash`, `--amend`, `reset --hard` yasak** (CLAUDE.md Bölüm 10). F1-05'in iki izlenmeyen dosyası çalışma ağacında duruyor.

## Dur ve sor

- Çalışma ağacında ön koşullarda sayılanlar dışında bir değişiklik var ya da `main`, `origin/main`'in gerisinde.
- Karttaki pencere tablolarından biri SRS'teki BR-02, BR-04 ya da BR-07 ile veya karar tablosunun "Fikstürden türeyen pencereler" tablosuyla çelişiyor gibi görünüyor.
- Bir hesap için `RuleSet`'e yeni bir alan gerekiyor gibi görünüyor.
- İmsakın gece yarısından önce ya da yatsının gece yarısından sonra olduğu bir durumu (yüksek enlem) ele almak gerekiyor gibi görünüyor. Bu kart bunu kapsamaz.
- `ktPending`'i değiştirmek ya da bir testi `KT` adıyla yazmak gerekiyor gibi görünüyor.
- `CLAUDE.md` Bölüm 12'deki diğer koşullar.

## Kapsam dışı (bu kartta yapma)

- İşaretleme, geri alma, kaçırıldı önerisi, otomatik ekleme, cuma, vitirin yatsıya bağlılığı (F2-04).
- Kerahatte kaza engeli ve ikindi istisnası (F2-05).
- Hatırlatma anı (F2-10).
- Sıradaki vakte kalan süre ve geri sayım metni (Faz 5).
- İlçenin saat diliminin nereden geleceği (Faz 3, vakit verisi). Bu kartta bölge her zaman parametredir.
- Yüksek enlem özel durumları.
- `PrayerRecord`, `KazaEntry` ya da başka bir kayıt modeli.
- `ktPending`, `ci.yml`, lint kapısı, M-08 ve `ProjectConfig`.
- `docs/` altındaki belgeleri değiştirmek (kartın plan ve rapor dosyaları hariç).

## Kabul kriterleri

**Domain**

- [ ] `TimeWindow`, `PrayerDayWindows`, `CurrentVakit`, `KerahatKind`, `KerahatWindow`, `KerahatStatus` ve `prayerDateOf` karttaki imzalarla `domain.times` paketinde.
- [ ] `windowOf` ve `missedAt` A2 tablosuyla, kerahat pencereleri A4 tablosuyla birebir aynı.
- [ ] `CurrentVakit.Within` hiçbir zaman `VITIR` döndürmüyor; `Outside` yalnızca güneş–öğle arasında.
- [ ] `prayerDateOf` bölgeyi parametreden alıyor; `clock.zone` kural kodunda hiçbir yerde kullanılmıyor (raporda arama komutu ve sonucu).
- [ ] Her A adımında testin önce kırmızı ya da derlenmeyen olduğu raporda yazıyor (A6 hariç; A6'nın ilk çalıştırma sonucu ayrıca yazılır).
- [ ] Yaz saati testleri iki geçiş gecesinde pencere süresini ve namaz gününü doğru veriyor.
- [ ] `:core:domain` ürün kodunda `android.`, `Instant.now`, `LocalDate.now`, `LocalDateTime.now`, `ZoneId.systemDefault`, `System.currentTimeMillis` ve `!!` yok.

**Kapılar ve temizlik**

- [ ] KT kapısı: `101 geçerli satır, 0 testli, 101 bekliyor, fark yok.`; `ktPending` değişmemiş.
- [ ] Kapsam kapısı yeşil; satır ve dal yüzdeleri raporda.
- [ ] Bölüm C'nin üç maddesi yapılmış; yeni mesajlar negatif denemelerle raporda gösterilmiş.

**Genel**

- [ ] `.\gradlew.bat ciCheck` yerelde yeşil; lint kapısı "bilinen 6 uyarı eşleşti, fark yok".
- [ ] `.\gradlew.bat help` hatasız (M-08).
- [ ] `.github/workflows/ci.yml` değişmemiş.
- [ ] Plan `docs/tasks/F2-02-plan.md`'de, rapor `docs/tasks/F2-02-rapor.md`'de; rapor `CLAUDE.md` Bölüm 11 biçiminde ve son satırı **PR başlığı**.
- [ ] Commit'ler `feat/f2-02-prayer-time-windows` dalında, Conventional Commits biçiminde; `main`'e dokunulmamış; F1-05 dosyaları commit'lenmemiş.

## Bölüm D — Son doğrulama, commit'ler ve rapor

Aşağıdakileri **bu sırayla** çalıştır ve sonuçları rapora özetle:

```
.\gradlew.bat clean
.\gradlew.bat ciCheck
.\gradlew.bat :core:domain:jacocoTestReport
.\gradlew.bat checkKtTraceability
.\gradlew.bat help
git status
git log --oneline main..HEAD
```

Yasak çağrılar ve `clock.zone` için (Git Bash):

```
grep -rnE "android\.|Instant\.now|LocalDate\.now|LocalDateTime\.now|ZoneId\.systemDefault|System\.currentTimeMillis|!!" core/domain/src/main
grep -rn "\.zone\b" core/domain/src/main
```

Beklenen: ikisinde de çıktı yok.

Beklenenler:

- `ciCheck` yeşil.
- `:core:domain` testleri: F2-01'in 46 testi + bu kartın testleri; toplam raporda.
- Satır kapsamı %90 veya üstü.
- KT kapısı: 101 geçerli, 0 testli, 101 bekliyor.
- Lint kapısı: bilinen 6 uyarı eşleşti.

Commit sırası:

1. `docs: add F2-02 card and update planning docs`
2. `docs(tasks): add F2-02 plan`
3. `feat(domain): add TimeWindow`
4. `feat(domain): add prayer day windows (BR-02, BR-04)`
5. `feat(domain): add current vakit (BR-02, BR-04)`
6. `feat(domain): add kerahat windows (BR-07)`
7. `feat(domain): add prayer date resolution (FR-TRK-05, M-23)`
8. `test(domain): add daylight saving transition tests (NFR-CMP-02)`
9. `refactor: apply F2-01 review cleanups`
10. `docs(tasks): add F2-02 report`

Commit mesajlarını tek satır ve düz ASCII tırnakla yaz; `git log --oneline` ile bozulmadığını kontrol et.

Raporu `docs/tasks/F2-02-rapor.md` dosyasına, `CLAUDE.md` Bölüm 11'e göre yaz ve son commit olarak ekle. Ek olarak:

- Test sayısı dosya dosya.
- A6'nın ilk çalıştırma sonucu (yeşil mi, kırmızıysa neyin düzeltildiği).
- Kapsam yüzdeleri.
- Bölüm C'nin negatif deneme mesajları.
- Son satır: `PR başlığı: feat(domain): add prayer time windows and prayer date (F2-02)`

**Push etme, PR açma.** Kullanıcı yapacak.

# Görev Kartı F2-04 — Günlük işaretleme ve kaçırıldı önerileri

Faz 2 · Kart 4/14 · 2 Kasım 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

Uygulamanın her gün en çok kullanılan davranışını kural motoruna yazar:

1. **Vakit içinde işaretleme** (FR-TRK-01, 02, 07): Vakti girmemiş namaz işaretlenemez (KT-01 – 03). Vitir, o günün yatsısı işaretlenmeden işaretlenemez (KT-04 – 06).
2. **Geri alma** (FR-TRK-03): İşaret kaldırılır, kalem yeniden "belirsiz" olur; vakti çıkınca öneri gelir (KT-18).
3. **Türetilen durum** (SRS DATA-05): Bir kalemin "kılındı / kılınmadı / belirsiz" durumu saklanmaz, işaretten ve saatten türetilir (KT-08, 10).
4. **Kaçırıldı önerisi** (FR-KAZ-03, BR-04): Vakti çıkmış, işaretlenmemiş kalem için borç önerisi; otomatik ekleme açıksa öneri yerine "otomatik" kaydı (KT-07 – 11). Kurulumdan önce vakti çıkmış namazlar için öneri yok (FR-ONB-05).
5. **Cuma ve bayram** (BR-08, BR-09, FR-TRK-06): Cuma öğlenin yerine geçer; kaçırılırsa öneri öğle içindir; kadında cuma isteğe bağlıdır (KT-15 – 17). Bayram namazı borç olmaz (KT-34).

Motor veri yazmaz (BR-00, M-25): kararlar yazılacak kayıtları öneri olarak döndürür.

**Bu kart 16 KT satırını kapatır:** KT-01 – 11, KT-15 – 18, KT-34. Kart sonunda KT kapısı `101 geçerli satır, 19 testli, 82 bekliyor, fark yok.` der.

**Bu kartta olmayan:** Vakti çıkmış bir kalemi sonradan işaretlemek ("vaktinde kıldım / sonradan kaza olarak kıldım", KT-12, KT-13, FR-TRK-04) ve birlikte geri alma. Bunlar veri modeline işlem kimliği ekliyor; F2-04b'nin işidir. Bu kart o durumda yalnızca "seçim gerekiyor" sonucunu döndürür.

## Ön koşullar

1. `main`, F2-03'ün birleştirildiği `a61651c` commit'inde ya da daha ilerde ve `origin/main` ile aynı.
2. Kullanıcı şu dört dosyayı çalışma ağacına koymuş olmalı (zip):
   - `CLAUDE.md` (sürüm 1.9)
   - `docs/gereksinim-analizi-srs.md` (1.0-taslak.11)
   - `docs/gelistirme-yol-haritasi.md` (F2-03 tamamlandı; F2-04 bölündü)
   - `docs/tasks/F2-04-gunluk-isaretleme.md` (bu kart)
3. Çalışma ağacında bu dört dosya dışında yalnızca F1-05'e ait iki izlenmeyen dosya bulunabilir (`docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md`). Bunlara **dokunma ve commit'leme.** Başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler ve kimlikler

- `CLAUDE.md` (tamamı; 1.9'da Bölüm 4, Bölüm 7 "Veri" ve Bölüm 11 "Kapsam dışında kalan kod" değişti)
- `docs/gereksinim-analizi-srs.md`: BR-00, BR-02, BR-03, BR-04, BR-08, BR-09; FR-TRK-01 – 03, 06, 07; FR-KAZ-03; FR-ONB-05; Bölüm 1.3 "Namaz günü"; Bölüm 6.1 (PrayerRecord, Settings); DATA-04; **DATA-05** (1.0-taslak.11'de eklendi; bu kartın kuralları oradadır)
- `docs/karar-tablosu.md`: Tablo A (KT-01 – 18), KT-34 ve "Sözleşmeler"
- `docs/kabul-senaryolari.md`: KS-TRK-01, 03, 04, 08, 09, 10, 11, 13; KS-KAZ-04, 05, 06; KS-ONB-07
- `docs/mimari-ve-kodlama-ilkeleri.md`: M-20, M-23, M-24, M-25, K-03, K-11, K-16
- `docs/tasks/F2-02-vakit-hesaplari.md` ve `docs/tasks/F2-03-kaza-defteri.md`: `PrayerDayWindows`, `KazaLedger`, `KazaWrite`, `LedgerFixture` ve test kalıpları

## Adım 0 — Plan ve onay

Bu adımda dosya okuyabilir ve salt okunur git komutlarını (`git status`, `git branch -a`, `git log`, `git fetch`, `git stash list`) çalıştırabilirsin. Hiçbir dosyayı değiştirme, dal değiştirme. Eski sohbet kayıtlarını (`~/.claude` altı) **okuma.**

Plana şunları yaz, sonra **kullanıcının onayını bekle:**

1. **Git başlangıcı:** `git fetch` sonrası `main` ile `origin/main`'in aynı olduğu ve `a61651c`'yi içerdiği; çalışma ağacındaki her değişikliğin grubu (bu kartın dört dosyası / F1-05'in iki izlenmeyen dosyası / beklenmeyen).
2. **Dosya ağacı ve imzalar:** Bölüm A'daki tiplerin dosyaları ve imzaları; karttan her sapmanın gerekçesi.
3. **Test listesi:** Her testin adı, sınadığı şey ve `argumentSet` satırları. Tam olarak 16 KT kimliği (KT-01 – 11, 15 – 18, 34) test adında ya da `argumentSet` adında geçer; KT-12 ve KT-13 **geçmez**.
4. **Test yardımcısı:** A5'teki `DayRecordFixture`'ın imzası.
5. **`ktPending` değişikliği:** Silinecek 16 satır ve yeni `// F2-04b` grubu.
6. **Commit sırası** (bkz. Bölüm D).

Onaydan sonra planı, onay mesajındaki değişikliklerle birlikte `docs/tasks/F2-04-plan.md` dosyasına yaz.

## Bölüm A — Günlük işaretleme (TDD)

Dal: güncel `main`'den `feat/f2-04-daily-tracking`. İlk commit, kullanıcının koyduğu dört belgedir; dosyaları **adıyla** ekle:

```
git add CLAUDE.md docs/gereksinim-analizi-srs.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-04-gunluk-isaretleme.md
git commit -m "docs: add F2-04 card and update planning docs"
```

İkinci commit plan dosyasıdır: `docs(tasks): add F2-04 plan`.

Her A adımında sıra önceki kartlardaki gibidir: testi yaz → `.\gradlew.bat :core:domain:test` ile derlenmediğini ya da kırmızı olduğunu gör ve rapora bir satır yaz → kodu yaz → `.\gradlew.bat :core:domain:test` ve `.\gradlew.bat ktlintCheck` yeşil → commit.

**Genel kurallar:**

- Yeni tipler iki pakettedir: `com.eceakin.namaztakibi.domain.settings` (`Gender`, `DaySettings`) ve `com.eceakin.namaztakibi.domain.tracking` (geri kalanı).
- `Prayer`, `RuleSet`, `domain.times` tiplerini **değiştirme.** `KazaLedger`'a yalnızca A4'teki tek fonksiyon eklenir.
- Zaman yalnızca `clock.instant()` ile okunur; `clock.zone` kullanılmaz (M-23). Bir namaz gününün vakitleri her zaman bir `PrayerDayWindows` olarak verilir.
- Motor hiçbir şey yazmaz; kararlar `RecordWrite` ve `KazaWrite` önerileri döndürür (BR-00, M-25). Yazılacak kaydın anı `clock.instant()`'tır.
- Beklenen sonuçlar `sealed` tiplerle döner (M-24); tutarsız girdi `require(...)`.
- Kural uygulayan her fonksiyonun yorumunda ilgili kimlik yazar (K-05): BR-xx, FR-xx, KT-xx, DATA-05.

### A1 — Ayar tipleri ve günlük kayıt (DATA-05)

```kotlin
// domain.settings
enum class Gender { MALE, FEMALE }                       // SRS 6.1 Settings: cinsiyet
data class DaySettings(val autoAddDebt: Boolean)         // SRS 6.1 Settings: otomatik ekleme

// domain.tracking
enum class PrayerStatus { KILINDI, KILINMADI, MAZERETLI, BELIRSIZ }   // SRS 6.1, DATA-05

data class PrayerMark(
    val prayer: Prayer,
    val markedAt: Instant,
    val asCuma: Boolean,
    val asKaza: Boolean,
)

data class DayRecord(val date: LocalDate, val marks: List<PrayerMark>) {
    fun markOf(prayer: Prayer): PrayerMark?          // yoksa null; yalnızca okuma kolaylığı
    companion object { fun empty(date: LocalDate): DayRecord }
}
```

- `DaySettings` bu kartta yalnızca otomatik eklemeyi taşır. Diğer ayarlar (gusül süresi, cinsiyet anlık kopyası) kendi kartlarında eklenecek; BR-21'in gün anlık kopyası F2-11'dedir.
- `PrayerStatus.MAZERETLI` bu kartta hiçbir fonksiyon tarafından üretilmez; mazeret kartları (F2-07) üretecek. SRS 6.1'deki dört durum tam olsun diye tanımlıdır.
- `PrayerMark.asKaza` bu kartta hiçbir kararla `true` yazılmaz (F2-04b yazacak), ama `DayRecord` böyle bir işareti taşıyabilir ve doğru yorumlamalıdır (A2'deki durum, A3'teki geri alma).

`DayRecord` değişmezleri (`require`). **Aynen bunlar:**

1. Her kalem için en fazla bir işaret.
2. `VITIR` işaretliyse `YATSI` da işaretli (BR-03).
3. `asCuma` doğruysa kalem `OGLE` ve `date` bir cumadır (BR-08, DATA-05).
4. `asCuma` ile `asKaza` birlikte doğru olamaz (cuma kaza edilmez; kaçırılırsa öğle kaza edilir, BR-08).

**Testler** (`DayRecordTest`):

- `DATA05_gunlukKayitDegismezleri`: `@ParameterizedTest`; dört değişmezin her biri için reddedilen bir örnek (`"DATA-05 ayni kaleme iki isaret"`, `"DATA-05 yatsisiz vitir"`, `"DATA-05 cuma olmayan gunde cuma"`, `"DATA-05 ogle disinda cuma"`, `"DATA-05 cuma ve kaza birlikte"`) ve kabul edilen iki örnek (`"DATA-05 G5 ogle cuma kabul"`, `"DATA-05 yatsi ve vitir kabul"`). Bozuk nesneler `Executable` içinde kurulur. G5, `ReferenceDay`'de cumadır.

Commit: `feat(domain): add day record and settings types (DATA-05)`

### A2 — Türetilen durum (DATA-05, BR-04)

```kotlin
// DayRecord içinde:
fun statusOf(prayer: Prayer, windows: PrayerDayWindows, clock: Clock): PrayerStatus
```

- `require(windows.day.date == date)`.
- Kurallar, sırayla:

| Durum | Sonuç |
| --- | --- |
| Kalem işaretli (`asKaza` ne olursa olsun) | `KILINDI` |
| `clock.instant() >= windows.missedAt(prayer)` | `KILINMADI` |
| Aksi hâlde | `BELIRSIZ` |

**Testler** (`PrayerStatusTest`):

- `DATA05_durumTuretilir`: `@ParameterizedTest`; öğle için: boş ve G1 15:29 → `BELIRSIZ`; boş ve G1 15:30 → `KILINMADI`; 12:40'ta işaretli ve G1 16:00 → `KILINDI`; `asKaza = true` işaretli ve G1 16:00 → `KILINDI`. Vitir için: boş ve G2 05:29 → `BELIRSIZ`; boş ve G2 05:30 → `KILINMADI`.
- `M24_yanlisGununVakitleriReddedilir`: `DayRecord.empty(G1)` ile `PrayerDayWindows(times(2), times(3))` → `IllegalArgumentException`.

Commit: `feat(domain): derive prayer status (DATA-05)`

### A3 — İşaretleme ve geri alma (FR-TRK-01 – 03, 06, 07; KT-01 – 06, 15, 17, 18)

```kotlin
sealed interface RecordWrite {
    data class Mark(
        val date: LocalDate,
        val prayer: Prayer,
        val markedAt: Instant,
        val asCuma: Boolean,
        val asKaza: Boolean,
    ) : RecordWrite
    data class Clear(val date: LocalDate, val prayer: Prayer) : RecordWrite
}

sealed interface MarkDecision {
    data class Allowed(val write: RecordWrite.Mark) : MarkDecision
    data class Blocked(val reason: MarkBlock) : MarkDecision
    /** Vakti çıkmış kalem: "vaktinde / sonradan kaza olarak" seçimi gerekir (FR-TRK-04; F2-04b). */
    data object NeedsLateChoice : MarkDecision
}
enum class MarkBlock { ALREADY_MARKED, NOT_YET_IN_TIME, YATSI_FIRST, CUMA_NOT_ALLOWED }

sealed interface UnmarkDecision {
    data class Allowed(val write: RecordWrite.Clear) : UnmarkDecision
    data class Blocked(val reason: UnmarkBlock) : UnmarkDecision
}
enum class UnmarkBlock { NOT_MARKED, UNMARK_VITIR_FIRST, KAZA_MARK_NEEDS_GROUP_UNDO }

enum class OgleOption { OGLE_ONLY, OGLE_OR_CUMA, OGLE_WITH_OPTIONAL_CUMA }

// DayRecord içinde:
fun decideMark(prayer: Prayer, asCuma: Boolean, windows: PrayerDayWindows, clock: Clock): MarkDecision
fun decideUnmark(prayer: Prayer): UnmarkDecision

// üst düzey (dosya: OgleOption.kt):
fun ogleOptionOf(date: LocalDate, gender: Gender): OgleOption
```

**`decideMark` kuralları.** `require(windows.day.date == date)`. Sırayla değerlendirilir; ilk uyan sonuç döner. `now = clock.instant()`, `window = windows.windowOf(prayer)`:

| # | Durum | Sonuç | Kimlik |
| --- | --- | --- | --- |
| 1 | Kalem zaten işaretli | `Blocked(ALREADY_MARKED)` | FR-TRK-03 (işaretli kaleme dokunuş geri almadır; o `decideUnmark`'tır) |
| 2 | `now < window.start` | `Blocked(NOT_YET_IN_TIME)` | BR-02, KT-01, KT-03 |
| 3 | `now >= windows.missedAt(prayer)` | `NeedsLateChoice` | FR-TRK-04 (F2-04b) |
| 4 | `prayer == VITIR` ve `YATSI` işaretli değil | `Blocked(YATSI_FIRST)` | BR-03, KT-04 |
| 5 | `asCuma` ve (`prayer != OGLE` ya da `date` cuma değil) | `Blocked(CUMA_NOT_ALLOWED)` | BR-08, DATA-05 |
| 6 | Aksi hâlde | `Allowed(Mark(date, prayer, now, asCuma, asKaza = false))` | BR-02, KT-02, KT-05, KT-06, KT-15 |

- Kural 3 kural 4'ten önce gelir: vakti çıkmış vitir de önce seçime gider; F2-04b yatsı kuralını orada uygular.
- Cinsiyet `decideMark`'ı etkilemez: cuma işareti erkekte ve kadında aynı kayıttır (DATA-05). Cinsiyet yalnızca öğle kaleminin nasıl sunulacağını belirler (`ogleOptionOf`).

**`decideUnmark` kuralları** (FR-TRK-03, DATA-05). Sırayla:

| # | Durum | Sonuç |
| --- | --- | --- |
| 1 | Kalem işaretli değil | `Blocked(NOT_MARKED)` |
| 2 | `prayer == YATSI` ve `VITIR` işaretli | `Blocked(UNMARK_VITIR_FIRST)` |
| 3 | İşaret `asKaza = true` | `Blocked(KAZA_MARK_NEEDS_GROUP_UNDO)` |
| 4 | Aksi hâlde | `Allowed(Clear(date, prayer))` |

- Kural 3 kalıcı bir kuraldır: "kaza olarak kılındı" işareti, birlikte yazıldığı kaza kayıtlarıyla birlikte geri alınır (FR-TRK-04, FR-KAZ-05). Bunu F2-04b'deki birlikte geri alma kararı yapacak.
- Geri almanın süre sınırı yoktur; `clock` almaz.

**`ogleOptionOf` kuralları** (FR-TRK-06, BR-08):

| `date` | `gender` | Sonuç |
| --- | --- | --- |
| Cuma değil | herhangi | `OGLE_ONLY` |
| Cuma | `MALE` | `OGLE_OR_CUMA` |
| Cuma | `FEMALE` | `OGLE_WITH_OPTIONAL_CUMA` |

**Testler** (`MarkPrayerTest`; vakitler `ReferenceDay`, `windows1 = PrayerDayWindows(times(1), times(2))`):

- `KT01_BR02_ogleVaktiGirmeden1229daIsaretlenemez`: boş G1, 12:29, öğle → `Blocked(NOT_YET_IN_TIME)`.
- `KT02_BR02_ogle1230daIsaretlenir`: 12:30 → `Allowed(Mark(G1, OGLE, G1 12:30, false, false))`; uygulanınca `statusOf(OGLE) == KILINDI`.
- `KT03_BR02_yatsi1000daIsaretlenemez`: 10:00, yatsı → `Blocked(NOT_YET_IN_TIME)`.
- `KT04_BR03_yatsiBosykenVitirIsaretlenemez`: 20:00, yatsı boş, vitir → `Blocked(YATSI_FIRST)`.
- `KT05_BR03_yatsiKilindiysaVitirIsaretlenir`: yatsı 19:45'te işaretli; 20:00 vitir → `Allowed(Mark(G1, VITIR, G1 20:00, false, false))`.
- `KT06_BR03_ertesiGun0529VitirG1KaydinaYazilir`: yatsı işaretli, vitir boş; saat **G2 05:29**, `windows1` → `Allowed(Mark(date = G1, VITIR, G2 05:29, false, false))`.
- `KT15_BR08_erkekCumaGunuCumaSecer_ogleKilinmisSayilir`: G5, `ogleOptionOf(G5, MALE) == OGLE_OR_CUMA`; G5 13:00 `decideMark(OGLE, asCuma = true, windows5)` → `Allowed(Mark(G5, OGLE, G5 13:00, true, false))`; uygulanınca `statusOf(OGLE) == KILINDI`.
- `KT17_BR08_kadindaCumaIstegeBagli`: `ogleOptionOf(G5, FEMALE) == OGLE_WITH_OPTIONAL_CUMA`; kadının cuma işareti de `decideMark(OGLE, true, …)` ile `Allowed` olur.
- `KT18_FR_TRK_03_isaretGeriAlinir_vaktiCikincaOneri`: öğle G1 12:40'ta işaretli; `decideUnmark(OGLE)` → `Allowed(Clear(G1, OGLE))`; uygulanınca G1 12:45'te `statusOf(OGLE) == BELIRSIZ`; G1 15:30'da `evaluateMissed` (A4) öğle için `Suggest` döndürür. (A4'ten önce yazılıyorsa bu testin son adımı A4'te eklenir; planda belirt.)
- `FR_TRK_kararSiralari`: `@ParameterizedTest`, KT olmayan sınır durumları:
  - `"FR-TRK-03 isaretli kaleme isaret"` → `Blocked(ALREADY_MARKED)`
  - `"FR-TRK-04 vakti cikmis kalem secim ister"`: G1 15:30, öğle boş → `NeedsLateChoice`
  - `"FR-TRK-04 vakti cikmis vitir de secim ister"`: G2 05:30, yatsı boş, vitir → `NeedsLateChoice`
  - `"BR-08 cuma olmayan gunde cuma"`: G1 13:00, `asCuma = true` → `Blocked(CUMA_NOT_ALLOWED)`
  - `"BR-08 ikindide cuma"`: G5 16:00, ikindi, `asCuma = true` → `Blocked(CUMA_NOT_ALLOWED)`
  - `"FR-TRK-06 cuma olmayan gun ogle secenegi"`: `ogleOptionOf(G1, MALE) == OGLE_ONLY`
- `FR_TRK_03_geriAlmaKurallari`: `@ParameterizedTest`: `"FR-TRK-03 isaretsiz kalem"` → `Blocked(NOT_MARKED)`; `"FR-TRK-03 vitir isaretliyken yatsi"` → `Blocked(UNMARK_VITIR_FIRST)`; `"FR-TRK-03 kaza isareti"` (`asKaza = true` öğle) → `Blocked(KAZA_MARK_NEEDS_GROUP_UNDO)`; `"FR-TRK-03 vitir geri alinir"` → `Allowed(Clear(G1, VITIR))`.
- `KS_TRK_01_isaretlenmeSaatiYazilir`: G1 13:00 öğle → `Mark.markedAt == G1 13:00`; diğer beş kalemin durumu değişmez.

Commit: `feat(domain): add mark and unmark decisions (BR-02, BR-03, BR-08, KT-01 to KT-06)`

### A4 — Kaçırıldı önerisi ve otomatik ekleme (FR-KAZ-03, BR-04, FR-ONB-05; KT-07 – 11, 16, 34)

```kotlin
sealed interface MissedOutcome {
    data class Suggest(val date: LocalDate, val prayer: Prayer) : MissedOutcome
    data class AutoAdd(val write: KazaWrite.AddEntry) : MissedOutcome
}

// DayRecord içinde:
fun evaluateMissed(
    windows: PrayerDayWindows,
    ledger: KazaLedger,
    settings: DaySettings,
    trackingStart: Instant,
    clock: Clock,
): List<MissedOutcome>

// üst düzey (dosya: MissedOutcome.kt):
fun confirmSuggestion(suggestion: MissedOutcome.Suggest, clock: Clock): KazaWrite.AddEntry

// KazaLedger'a eklenecek tek fonksiyon (domain.kaza):
fun hasOpenEntryFor(date: LocalDate, prayer: Prayer): Boolean
```

**`hasOpenEntryFor`** (DATA-05): Defterde `kind` `ONERI` ya da `OTOMATIK`, `sourceDate == date`, `prayer == prayer` olan ve geri alınmamış (`!isCorrected(id)`) bir kayıt var mı. `ELLE`, `HESAPLAYICI` ve `DUZELTME` sayılmaz.

**`evaluateMissed` kuralları.** `require(windows.day.date == date)`. `now = clock.instant()`. Kalemler `Prayer.entries` sırasıyla tek tek değerlendirilir; bir kalem için **ilk uyan** satır geçerlidir:

| # | Durum | O kalem için sonuç | Kimlik |
| --- | --- | --- | --- |
| 1 | Kalem işaretli | yok | DATA-05 |
| 2 | `now < windows.missedAt(prayer)` | yok | BR-04, KT-07 |
| 3 | `windows.missedAt(prayer) <= trackingStart` | yok | FR-ONB-05, KS-ONB-07, KS-TRK-13 |
| 4 | `ledger.hasOpenEntryFor(date, prayer)` | yok | DATA-05 (zaten borca yazılmış) |
| 5 | `settings.autoAddDebt` | `AutoAdd(AddEntry(prayer, sourceDate = date, kind = OTOMATIK, count = 1, corrects = null, missedInSefer = false, createdAt = now))` | BR-00, KT-09 |
| 6 | Aksi hâlde | `Suggest(date, prayer)` | FR-KAZ-03, KT-08, 10, 11, 16 |

- Dönen liste `Prayer.entries` sırasındadır; sonuç olmayan kalemler listede yer almaz.
- `missedInSefer` bu kartta her zaman `false`tır; seferî mod P1'dir (BR-18, F2-05).
- Mazeret dönemleri burada henüz yoktur; mazeretli kalemlerin öneri dışı kalması (KT-40) ve mazeret önerilerinin otomatik eklemeye girmemesi (KT-80) mazeret kartlarında bu fonksiyona eklenecek.

**`confirmSuggestion`** ("Borca ekle", KS-KAZ-04): `AddEntry(prayer = suggestion.prayer, sourceDate = suggestion.date, kind = ONERI, count = 1, corrects = null, missedInSefer = false, createdAt = clock.instant())`.

**Testler** (`MissedSuggestionTest`; aksi yazmadıkça `settings = DaySettings(autoAddDebt = false)`, `trackingStart = ReferenceDay.at(1, "00:00")`, `ledger = KazaLedger.EMPTY`). Önkoşulda "yalnız X boş" denen yerde diğer kalemler, vakitlerinin içinde işaretlenmiş sayılır (fixture'da `markedAt` vaktin başlangıcı):

- `KT07_BR04_ogle1529daHenuzKacirilmadi`: G1, yalnız öğle boş (sabah işaretli), 15:29 → sonuç listesi boş.
- `KT08_BR04_ogle1530daKilinmadi_oneriGelir`: aynı, 15:30 → `statusOf(OGLE) == KILINMADI`; sonuç `[Suggest(G1, OGLE)]`.
- `KT09_BR00_otomatikEklemeAcikken_oneriYerineOtomatikKayit`: `autoAddDebt = true`, 15:30 → `[AutoAdd(AddEntry(OGLE, G1, OTOMATIK, 1, null, false, G1 15:30))]`; uygulanınca (`LedgerFixture`) `balance(OGLE) == 1`, kaydın türü `OTOMATIK`; aynı anda yeniden değerlendirince sonuç boş (öneri gösterilmez).
- `KT10_BR04_sabah0700deKilinmadi_oneriGelir`: G1, hiçbir kalem işaretli değil, 07:00 → `statusOf(SABAH) == KILINMADI`; sonuç `[Suggest(G1, SABAH)]`.
- `KT11_BR04_yatsiVeVitirIcinIkiAyriOneri`: G1 sabah – akşam işaretli, yatsı ve vitir boş; saat **G2 05:30**, `windows1` → `[Suggest(G1, YATSI), Suggest(G1, VITIR)]`.
- `KT16_BR08_cumaKacirilinca_oneriOgleIcin`: G5, erkek, yalnız öğle boş; G5 15:30 → `[Suggest(G5, OGLE)]`.
- `KT34_BR09_bayramNamaziBorcaYazilmaz`: `Prayer.entries` yalnızca `SABAH, OGLE, IKINDI, AKSAM, YATSI, VITIR`'dir (bayram ya da cuma kalemi yok); G1'in altı kalemi de işaretli; G2 05:30'da `evaluateMissed` → boş.
- `KS_ONB_07_kurulumOncesiVakitIcinOneriYok`: `trackingStart = G1 16:00`, hiçbir kalem işaretli değil; G1 18:00'de → `[Suggest(G1, IKINDI)]` (sabahın ve öğlenin vakti kurulumdan önce çıktı; ikindininki 18:00'de, kurulumdan sonra).
- `KS_TRK_13_kurulumdanOncekiGunIcinOneriYok`: `trackingStart = G5 10:00`; G3'ün boş kaydı, G5 10:00'da → boş.
- `DATA05_borcaYazilmisKalemIcinOneriYok`: `@ParameterizedTest`: `"DATA-05 oneri kaydi var"` (öğle `ONERI`, `sourceDate = G1`) → öğle için sonuç yok; `"DATA-05 otomatik kaydi var"` → yok; `"DATA-05 geri alinmis oneri"` (`ONERI` ve ona işaret eden `DUZELTME`) → `Suggest(G1, OGLE)`; `"DATA-05 baska gunun kaydi"` (`ONERI`, `sourceDate = G2`) → `Suggest(G1, OGLE)`; `"DATA-05 elle kayit sayilmaz"` (`ELLE`, `sourceDate = null`) → `Suggest(G1, OGLE)`.
- `KS_KAZ_04_borcaEkleOneriKaydiYazar`: `confirmSuggestion(Suggest(G1, OGLE), G1 15:40)` → `AddEntry(OGLE, G1, ONERI, 1, null, false, G1 15:40)`; uygulanınca `balance(OGLE) == 1` ve `evaluateMissed` öğle için sonuç döndürmez.
- `KS_KAZ_05_otomatikKapaliykenSessizBorcYok`: hiçbir kalem işaretli değil, G2 07:00, `windows1` → G1'in altı kalemi için `Suggest`, `Prayer.entries` sırasıyla; defter `EMPTY` kalır (motor yazmaz). KS-KAZ-05'in yedinci önerisi (G2 sabahı) G2'nin kendi `DayRecord`'undan gelir: `DayRecord.empty(G2)`, `PrayerDayWindows(times(2), times(3))`, G2 07:00 → `[Suggest(G2, SABAH)]`. Liste ekranı kapsam dışıdır.

Commit: `feat(domain): add missed prayer suggestions and auto add (FR-KAZ-03, KT-07 to KT-11)`

### A5 — Test yardımcısı (K-14)

```kotlin
// test kaynaklarında, com.eceakin.namaztakibi.domain.tracking.testing
object DayRecordFixture {
    /** Yazmaları sırayla uygular ve her adımda yeni bir DayRecord kurar (değişmezler her adımda denetlenir). */
    fun apply(record: DayRecord, writes: List<RecordWrite>): DayRecord
    /** Verilen kalemleri vaktinin başlangıcında işaretlenmiş bir DayRecord. */
    fun markedAtStart(date: LocalDate, windows: PrayerDayWindows, vararg prayers: Prayer): DayRecord
}
```

- `Mark`, kaydın `date`'iyle aynı tarihte olmalı; değilse `IllegalArgumentException` (test hatası).
- `Clear`, kalemin işaretini kaldırır.
- `markedAtStart`, `VITIR` verilirse `YATSI`'nın da verilmesini bekler; vermezsen `DayRecord` değişmezi kırılır.
- `DayRecordFixture`'ı A3'te, ilk ihtiyaç duyulduğunda oluştur ve o adımın commit'ine koy (F2-03'teki `LedgerFixture` gibi). Kaza yazmaları için mevcut `LedgerFixture` kullanılır.

## Bölüm B — Kapılar

1. **`ktPending`:** Kök `build.gradle.kts`'te `// F2-04` grubundan şu 16 satırı sil: `KT-01` – `KT-11`, `KT-15` – `KT-18`, `KT-34`. Grupta kalan `KT-12` ve `KT-13`'ün üstündeki yorum satırını `// F2-04b` yap. Başka hiçbir satıra dokunma.
2. `.\gradlew.bat checkKtTraceability` → `KT izlenebilirlik: 101 geçerli satır, 19 testli, 82 bekliyor, fark yok.`
3. `.\gradlew.bat :core:domain:jacocoTestCoverageVerification` yeşil; satır ve dal kapsamı ile kaçan satır ve dallar rapora yazılır (CLAUDE.md 1.9 Bölüm 11).

Commit: `build: remove F2-04 rows from pending KT list (K-16)`

## Bilinen tuzaklar

1. **Namaz günü ≠ takvim günü.** KT-06 ve KT-11'de saat G2 05:29 / 05:30'dur ama kalemler G1'in namaz gününe aittir: `DayRecord(G1)` ve `PrayerDayWindows(times(1), times(2))` verilir; yazılan `Mark.date` G1'dir.
2. **Durum saklanmaz.** "Kılınmadı" bir yazma değildir; `statusOf` saatten türetir (DATA-05). Hiçbir karar `KILINMADI` yazmaz.
3. **Öneri saklanmaz.** `evaluateMissed` her çağrıda yeniden hesaplar. "Öneri kapandı" demek, kalemin işaretlenmesi ya da o tarih ve kalem için açık bir `ONERI`/`OTOMATIK` kaydı olmasıdır.
4. **Kural sırası önemlidir.** `decideMark`'ta "vakti çıkmış" kuralı "önce yatsı" kuralından önce gelir; `evaluateMissed`'te "kurulumdan önce" kuralı otomatik eklemeden önce gelir. Tablolardaki sırayı koru.
5. **Cinsiyet işaretlemeyi değiştirmez.** KT-17'deki fark yalnızca sunumdur (`ogleOptionOf`).
6. **KT-12 ve KT-13 bu kartta değil.** Bu iki kimlikle test yazma; KT kapısı "testi var ama bekleyen listede" diye kırılır. Vakti çıkmış kalem için bu kartın tek sonucu `NeedsLateChoice`'tır.
7. **KT kimliği tablodaki gibi yazılır:** `KT01_`, …, `KT09_`, `KT10_`, `KT34_`.
8. **Çift dokunuş koruması** (FR-TRK-03, KS-TRK-14: 1 saniye içindeki ikinci dokunuş yok sayılır) bir arayüz davranışıdır; domain'e yazılmaz (Faz 5 ve 6).
9. **`git add -A`, `git add .`, `stash`, `--amend`, `reset --hard` yasak.** Belge commit'lerine kod girmez.

## Dur ve sor

- Çalışma ağacında ön koşullarda sayılanlar dışında bir değişiklik var ya da `main`, `origin/main`'in gerisinde.
- Kartın bir kuralı SRS DATA-05, BR-02/03/04/08/09, FR-KAZ-03, FR-ONB-05 ya da karar tablosundaki 16 satırdan biriyle çelişiyor gibi görünüyor.
- `Prayer`, `RuleSet` ya da `domain.times` tiplerini değiştirmek ya da `KazaLedger`'a `hasOpenEntryFor` dışında bir şey eklemek gerekiyor gibi görünüyor.
- Bir test için KT-12 ya da KT-13'ü kullanmak gerekiyor gibi görünüyor.
- `CLAUDE.md` Bölüm 12'deki diğer koşullar.

## Kapsam dışı (bu kartta yapma)

- Vakti çıkmış kalemi sonradan işaretleme, "kaza olarak kıldım", işlem kimliği, birlikte geri alma (F2-04b; KT-12, KT-13, FR-TRK-04, KS-KAZ-10).
- Kerahat ve kaza engeli (F2-05), tertip notu (F2-06).
- Mazeret durumları ve mazeretin öneriye etkisi (F2-07 – F2-09).
- Hatırlatma anı (F2-10), gün anlık kopyası (F2-11).
- Birden çok günün bekleyen önerilerini toplayan liste ("Bekleyen öneriler" ekranı) ve toplu onay ekranı; bunlar tek günlük `evaluateMissed` ve `confirmSuggestion` üzerine Faz 4–5'te kurulacak.
- Çift dokunuş koruması ve geri alma çubuğu (arayüz).
- `Prayer`, `RuleSet`, `domain.times`, `ci.yml`, lint kapısı, M-08 ve `ProjectConfig`.
- `docs/` altındaki belgeleri değiştirmek (kartın plan ve rapor dosyaları hariç).

## Kabul kriterleri

**Domain**

- [ ] `Gender`, `DaySettings` (`domain.settings`); `PrayerStatus`, `PrayerMark`, `DayRecord`, `RecordWrite`, `MarkDecision`, `UnmarkDecision`, `OgleOption`, `MissedOutcome` ve yardımcı enum'lar (`domain.tracking`); `KazaLedger.hasOpenEntryFor` karttaki imzalarla.
- [ ] `DayRecord` değişmezleri, `statusOf`, `decideMark`, `decideUnmark`, `ogleOptionOf`, `evaluateMissed` ve `confirmSuggestion` A1 – A4 tablolarıyla birebir; hiçbir karar `DayRecord` ya da `KazaLedger`'ı değiştirmiyor.
- [ ] 16 KT satırının beklenenleri karar tablosuyla aynı.
- [ ] Her A adımında testin önce kırmızı ya da derlenmeyen olduğu raporda yazıyor.
- [ ] Yasak çağrı araması boş (Bölüm D).

**Kapılar**

- [ ] `ktPending`'den yalnızca 16 satır silinmiş; KT-12 ve KT-13 `// F2-04b` grubunda.
- [ ] KT kapısı: `101 geçerli satır, 19 testli, 82 bekliyor, fark yok.`
- [ ] Kapsam kapısı yeşil; satır ve dal yüzdeleri ve kaçan satır/dallar (dosya:satır) raporda.

**Genel**

- [ ] `.\gradlew.bat ciCheck` yerelde yeşil; lint kapısı "bilinen 6 uyarı eşleşti, fark yok".
- [ ] `.\gradlew.bat help` hatasız (M-08).
- [ ] `.github/workflows/ci.yml` değişmemiş.
- [ ] Belge commit'lerinde (`docs:`, `docs(tasks):`) kod, test ya da derleme dosyası yok.
- [ ] Plan `docs/tasks/F2-04-plan.md`'de, rapor `docs/tasks/F2-04-rapor.md`'de; rapor `CLAUDE.md` Bölüm 11 biçiminde ve son satırı **PR başlığı**.
- [ ] Commit'ler `feat/f2-04-daily-tracking` dalında, Conventional Commits biçiminde; `main`'e dokunulmamış; F1-05 dosyaları commit'lenmemiş.

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
git log --stat --format="%h %s" main..HEAD -- docs CLAUDE.md
```

Yasak çağrılar için (Git Bash; yorum satırları hariç):

```
grep -rnE "android\.|Instant\.now|LocalDate\.now|LocalDateTime\.now|ZoneId\.systemDefault|System\.currentTimeMillis|!!|clock\.zone" core/domain/src/main | grep -vE "^[^:]+:[0-9]+:[[:space:]]*(\*|//)"
```

Beklenen: çıktı yok.

Beklenenler:

- `ciCheck` yeşil.
- `:core:domain` testleri: F2-03 sonrası 149 + bu kartın testleri; toplam dosya dosya raporda.
- Satır kapsamı %90 veya üstü; kaçan satır ve dallar dosya:satır olarak raporda.
- KT kapısı: 101 geçerli, 19 testli, 82 bekliyor.
- Lint kapısı: bilinen 6 uyarı eşleşti.

Commit sırası:

1. `docs: add F2-04 card and update planning docs`
2. `docs(tasks): add F2-04 plan`
3. `feat(domain): add day record and settings types (DATA-05)`
4. `feat(domain): derive prayer status (DATA-05)`
5. `feat(domain): add mark and unmark decisions (BR-02, BR-03, BR-08, KT-01 to KT-06)`
6. `feat(domain): add missed prayer suggestions and auto add (FR-KAZ-03, KT-07 to KT-11)`
7. `build: remove F2-04 rows from pending KT list (K-16)`
8. `docs(tasks): add F2-04 report`

Commit mesajlarını tek satır ve düz ASCII tırnakla yaz; `git log --oneline` ile bozulmadığını kontrol et.

Raporu `docs/tasks/F2-04-rapor.md` dosyasına, `CLAUDE.md` Bölüm 11'e göre yaz ve son commit olarak ekle. Ek olarak:

- Test sayısı dosya dosya.
- Kapsam yüzdeleri ve kaçan satır/dallar.
- KT kapısının çıktısı.
- Son satır: `PR başlığı: feat(domain): add daily marking, derived status and missed suggestions (F2-04)`

**Push etme, PR açma.** Kullanıcı yapacak.

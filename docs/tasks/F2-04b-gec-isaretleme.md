# Görev Kartı F2-04b — Geç işaretleme ve birlikte geri alma

Faz 2 · Kart 5/14 · 2 Kasım 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

F2-04, vakti çıkmış bir kalem işaretlenmek istendiğinde yalnızca `NeedsLateChoice` döndürüyordu. Bu kart o seçimin kurallarını yazar (FR-TRK-04):

1. **"Vaktinde kıldım"** (KT-13): Kalem kılındı olur, borç değişmez, öneri kapanır. Borç daha önce eklendiyse (onaylanmış öneri ya da otomatik kayıt) bu borcun geri alınması **sorulur**. Sessizce silinmez (BR-00).
2. **"Sonradan kaza olarak kıldım"** (KT-12, KS-KAZ-10): Kalem "kılındı (kaza)" olur ve bir kılınan kaza kaydı yazılır. Borç önceden eklenmişse borç 1 azalır. Öneri bekliyorsa ya da gün takip başlangıcından önceyse, önce bir öneri kaydı sonra kılınan kaza yazılır ve borç değişmez.
3. **İşlem kimliği** (DATA-04): "Sonradan kaza olarak kıldım" ile birlikte yazılan kayıtlar (günlük işaret, gerekiyorsa öneri kaydı ve kılınan kaza) tek bir işlemdir. Ortak bir `OperationId` taşırlar. Tek tek geri alınmazlar; işlem bütün olarak geri alınır (FR-KAZ-05).

Motor veri yazmaz ve kimlik üretmez (BR-00, M-25). Kararlar yazılacak kayıtları ve bunların tek işlem olup olmadığını döndürür; işlem kimliğini yazan katman (Faz 4) üretir.

**Bu kart 2 KT satırını kapatır:** KT-12, KT-13. Kart sonunda KT kapısı `101 geçerli satır, 21 testli, 80 bekliyor, fark yok.` der.

**Kurallar** SRS 1.0-taslak.12'dedir: DATA-04 "İşlem" ve DATA-05 "Geç işaretleme". Kart onları koda çevirir. Bir kural belirsiz görünürse SRS esastır, **dur ve sor.**

## Ön koşullar

1. `main`, F2-04'ün birleştirildiği `bdbea1d` commit'inde ya da daha ilerde ve `origin/main` ile aynı.
2. Kullanıcı şu dört dosyayı çalışma ağacına koymuş olmalı (zip):
   - `CLAUDE.md` (sürüm 1.10)
   - `docs/gereksinim-analizi-srs.md` (1.0-taslak.12)
   - `docs/gelistirme-yol-haritasi.md` (F2-04 tamamlandı; F2-04b yazıldı)
   - `docs/tasks/F2-04b-gec-isaretleme.md` (bu kart)
3. Çalışma ağacında bu dört dosya dışında yalnızca F1-05'e ait iki izlenmeyen dosya bulunabilir (`docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md`). Bunlara **dokunma ve commit'leme.** Başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler ve kimlikler

- `CLAUDE.md` (tamamı). 1.10'da Bölüm 4 ve Bölüm 7 "Veri" değişti.
- `docs/gereksinim-analizi-srs.md`:
  - BR-00, BR-03, BR-04, BR-08
  - FR-TRK-03, FR-TRK-04, FR-KAZ-04, FR-KAZ-05, FR-ONB-05
  - Bölüm 6.1 (PrayerRecord, KazaEntry, KazaPerformed)
  - **DATA-04** ("Tür", "Geri alma" ve yeni **"İşlem"** maddeleri)
  - **DATA-05** (yeni **"Geç işaretleme"** maddesi)
- `docs/karar-tablosu.md`: Tablo A (KT-12, KT-13, KT-18), KT-96 (soru kalıbı) ve "Sözleşmeler"
- `docs/kabul-senaryolari.md`: KS-TRK-05, KS-TRK-06, KS-TRK-13, KS-KAZ-08, KS-KAZ-10, KS-WDG-07
- `docs/mimari-ve-kodlama-ilkeleri.md`: M-20, M-23, M-24, M-25, M-31, K-03, K-11, K-12, K-14, K-16
- `docs/tasks/F2-03-kaza-defteri.md`, `docs/tasks/F2-04-gunluk-isaretleme.md` ve iki kartın raporları: `KazaLedger`, `DayRecord`, `LedgerFixture`, `DayRecordFixture` ve test kalıpları

## Adım 0 — Plan ve onay

Bu adımda dosya okuyabilir ve salt okunur git komutlarını (`git status`, `git branch -a`, `git log`, `git fetch`, `git stash list`) çalıştırabilirsin. Hiçbir dosyayı değiştirme, dal değiştirme. Eski sohbet kayıtlarını (`~/.claude` altı) **okuma.**

Plana şunları yaz, sonra **kullanıcının onayını bekle:**

1. **Git başlangıcı:** `git fetch` sonrası `main` ile `origin/main`'in aynı olduğu ve `bdbea1d`'yi içerdiği. Çalışma ağacındaki her değişikliği üç gruba ayır: bu kartın dört dosyası, F1-05'in iki izlenmeyen dosyası, beklenmeyen değişiklikler.
2. **Dosya ağacı ve imzalar:** Bölüm A'daki yeni ve değişen tiplerin dosyaları ve imzaları. Karttan her sapmanın gerekçesi.
3. **Değişen mevcut testler:** A1'de listelenen üç F2-04 testinin hangi satırının nasıl değiştiği. Beklenen değerler değişmez; yalnızca kurulan işarete `operationId` eklenir.
4. **Test listesi:** Her testin adı, neyi sınadığı ve `argumentSet` satırları. KT-12 ve KT-13 test adında ya da `argumentSet` adında geçer; başka yeni bir KT kimliği geçmez.
5. **Test yardımcıları:** A4'teki üç yardımcının imzası.
6. **Kural sıraları:** `decideLateMark` ve `decideUndoOperation` için kural sırası, kendi cümlelerinle. Karttaki tablolarla aynı olduğunu belirt.
7. **`ktPending` değişikliği** ve **commit sırası** (Bölüm D).

Onaydan sonra planı, onay mesajındaki değişikliklerle birlikte `docs/tasks/F2-04b-plan.md` dosyasına yaz.

## Bölüm A — Uygulama (TDD)

Dal: güncel `main`'den `feat/f2-04b-late-marking`. İlk commit, kullanıcının koyduğu dört belgedir; dosyaları **adıyla** ekle:

```
git add CLAUDE.md docs/gereksinim-analizi-srs.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-04b-gec-isaretleme.md
git commit -m "docs: add F2-04b card and update planning docs"
```

İkinci commit plan dosyasıdır: `docs(tasks): add F2-04b plan`.

Her A adımında sıra önceki kartlardaki gibidir:

1. Testi yaz.
2. `.\gradlew.bat :core:domain:test` ile testin derlenmediğini ya da kırmızı olduğunu gör ve rapora bir satır yaz.
3. Kodu yaz.
4. `.\gradlew.bat :core:domain:test` ve `.\gradlew.bat ktlintCheck` yeşil olsun.
5. Commit at.

**Genel kurallar:**

- `Prayer`, `RuleSet`, `domain.times` ve `domain.settings` değişmez.
- `KazaLedger`'da yalnızca A1 ve A3'te yazanlar değişir. `DayRecord`'da yalnızca A1 ve A2'de yazanlar değişir. F2-03 ve F2-04 kararlarının mevcut davranışı aynen kalır.
- Zaman yalnızca `clock.instant()` ile okunur; `clock.zone` kullanılmaz (M-23).
- Yazılacak kayıtların anı `clock.instant()`'tır. `missedInSefer` her zaman `false`tır (seferî mod P1, F2-05).
- Beklenen sonuçlar `sealed` tiplerle döner (M-24). Tutarsız girdi `require(...)` ile reddedilir.
- Kural uygulayan her fonksiyonun yorumunda ilgili kimlik yazar (K-05).

### A1 — İşlem kimliği ve değişmezler (DATA-04, DATA-05)

```kotlin
// domain.kaza, dosya: OperationId.kt
@JvmInline
value class OperationId(val value: String)       // init: require(value.isNotBlank())
```

Üç kayıt tipine **son parametre** olarak, varsayılan değeri `null` olan bir alan eklenir:

| Tip | Yeni alan |
| --- | --- |
| `KazaEntry` (domain.kaza) | `val operationId: OperationId? = null` |
| `KazaPerformed` (domain.kaza) | `val operationId: OperationId? = null` |
| `PrayerMark` (domain.tracking) | `val operationId: OperationId? = null` |

- Varsayılan değerin gerekçesi: kayıtların çoğu bir işleme ait değildir; F2-03 ve F2-04 testlerindeki kurucu çağrıları değişmeden kalır.
- `KazaWrite` ve `RecordWrite` **değişmez.** Hangi yazmaların tek işlem olduğunu kararın sonucu söyler (A2); kimliği yazan katman atar.

**Yeni değişmezler** (`require`):

| Yer | Değişmez | Kaynak |
| --- | --- | --- |
| `KazaEntry.init` | `kind == DUZELTME` ise `operationId == null` | DATA-04 "Düzeltme kayıtları hiçbir işleme ait değildir" |
| `KazaLedger.init` | Aynı `operationId`'yi taşıyan bütün kayıtlar (`entries` ve `performed`) aynı kalemdedir | DATA-04 "Bir işlemin kayıtları aynı kalemdedir" |
| `DayRecord.init` | `asKaza == true` ise `operationId != null`; `operationId != null` ise `asKaza == true` | DATA-04, DATA-05 (tek işlem türü "kaza olarak kıldım") |

**Değişen mevcut testler.** Yeni `DayRecord` değişmezi yüzünden, `asKaza = true` olan ama işlem kimliği taşımayan üç F2-04 işareti artık kurulamaz. Bu üç işarete `operationId = OperationId("op1")` ekle. **Beklenen değerleri değiştirme:**

1. `PrayerStatusTest`, `argumentSet("DATA-05 ogle kaza isaretli 16:00 kilindi")` (yardımcı `markedRecord`)
2. `MarkPrayerTest`, `argumentSet("FR-TRK-03 kaza isareti")` (yardımcı `cm`)
3. `DayRecordTest`, `argumentSet("DATA-05 cuma ve kaza birlikte")`. Bu satır `IllegalArgumentException` beklemeye devam eder. İşlem kimliği verilince hatayı yine "cuma ve kaza birlikte olamaz" kuralı üretir.

Başka bir mevcut test değişirse **dur ve sor.**

**Testler** (`OperationInvariantTest`, domain.tracking test paketinde):

`DATA04_islemDegismezleri`: `@ParameterizedTest`, `Executable` satırlarıyla.

- `"DATA-04 duzeltme isleme ait olamaz"`: `KazaEntry(kind = DUZELTME, count = -1, corrects = …, operationId = OperationId("op1"))` → `IllegalArgumentException`
- `"DATA-04 islem kayitlari ayni kalemde"`: aynı `op1` ile bir öğle `KazaEntry` ve bir ikindi `KazaPerformed` içeren `KazaLedger` → `IllegalArgumentException`
- `"DATA-05 kaza isareti islemsiz olamaz"`: `PrayerMark(OGLE, …, asKaza = true, operationId = null)` içeren `DayRecord` → `IllegalArgumentException`
- `"DATA-05 islemli isaret kaza olmali"`: `PrayerMark(OGLE, …, asKaza = false, operationId = OperationId("op1"))` içeren `DayRecord` → `IllegalArgumentException`
- `"DATA-04 islemli kayitlar kabul"`: `op1` ile bir öğle `ONERI` kaydı ve bir öğle `KazaPerformed` içeren defter ile `op1` taşıyan `asKaza = true` öğle işaretli `DayRecord(G1)` kurulur, hata yok.
- `"DATA-04 bos islem kimligi"`: `OperationId(" ")` → `IllegalArgumentException`.

Testte toplam 6 satır olur.

Commit: `feat(domain): add operation id to kaza and day records (DATA-04)`

### A2 — Geç işaretleme kararı (FR-TRK-04, DATA-05; KT-12, KT-13)

```kotlin
// domain.tracking
enum class LateChoice { ON_TIME, AS_KAZA }

/**
 * Bir geç işaretlemenin yazacakları. [grouped] doğruysa [mark] ve [kazaWrites]'ın
 * hepsi yazan katmanın üreteceği tek bir yeni OperationId'yi taşır (DATA-04).
 */
data class LateMarkWrites(
    val mark: RecordWrite.Mark,
    val kazaWrites: List<KazaWrite>,
    val grouped: Boolean,
)

sealed interface LateMarkDecision {
    data class Allowed(val writes: LateMarkWrites) : LateMarkDecision
    /** Soru sorulur; cevaba göre ifConfirmed ya da ifDeclined yazılır. */
    data class NeedsConfirmation(
        val question: LateMarkQuestion,
        val ifConfirmed: LateMarkWrites,
        val ifDeclined: LateMarkWrites,
    ) : LateMarkDecision
    data class Blocked(val reason: LateMarkBlock) : LateMarkDecision
}
enum class LateMarkQuestion { UNDO_ADDED_DEBT }
enum class LateMarkBlock { ALREADY_MARKED, NOT_LATE, YATSI_FIRST }

// DayRecord içinde:
fun decideLateMark(
    prayer: Prayer,
    choice: LateChoice,
    windows: PrayerDayWindows,
    ledger: KazaLedger,
    clock: Clock,
): LateMarkDecision

// KazaLedger içinde (domain.kaza), hasOpenEntryFor'un yanına:
fun openEntriesFor(date: LocalDate, prayer: Prayer): List<KazaEntry>
```

**`openEntriesFor`** (DATA-05 "açık kayıt"): `hasOpenEntryFor` ile aynı koşulu sağlayan kayıtları `entries` sırasıyla döndürür. `hasOpenEntryFor`, `openEntriesFor(date, prayer).isNotEmpty()` olarak yeniden yazılabilir; davranışı ve testleri değişmez.

**`decideLateMark` kuralları.** Önce `require(windows.day.date == date)`. Sonra sırayla değerlendirilir ve ilk uyan sonuç döner. Kısaltmalar: `now = clock.instant()`, `open = ledger.openEntriesFor(date, prayer)`, `mark(asKaza) = RecordWrite.Mark(date, prayer, now, asCuma = false, asKaza)`.

| # | Durum | Sonuç | Kimlik |
| --- | --- | --- | --- |
| 1 | Kalem zaten işaretli | `Blocked(ALREADY_MARKED)` | FR-TRK-03 |
| 2 | `now < windows.missedAt(prayer)` | `Blocked(NOT_LATE)` (vakit içindeki kalem `decideMark` ile işaretlenir) | FR-TRK-04 |
| 3 | `prayer == VITIR` ve `YATSI` işaretli değil | `Blocked(YATSI_FIRST)` | BR-03 |
| 4 | `ON_TIME` ve `open` boş | `Allowed(LateMarkWrites(mark(false), [], grouped = false))` | KT-13, KS-TRK-05, KS-TRK-13 |
| 5 | `ON_TIME`, `open` dolu ve `ledger.balance(prayer) - open.sumOf { it.count } < 0` | `Allowed(LateMarkWrites(mark(false), [], grouped = false))` (soru yok, borç değişmez) | DATA-05 |
| 6 | `ON_TIME`, `open` dolu | `NeedsConfirmation(UNDO_ADDED_DEBT, ifConfirmed = LateMarkWrites(mark(false), düzeltmeler, false), ifDeclined = LateMarkWrites(mark(false), [], false))` | BR-00, DATA-05 |
| 7 | `AS_KAZA`, `open` dolu ve `ledger.balance(prayer) >= 1` | `Allowed(LateMarkWrites(mark(true), [AddPerformed(prayer, now)], grouped = true))` | KT-12, KS-TRK-06 |
| 8 | `AS_KAZA` (diğer her durum) | `Allowed(LateMarkWrites(mark(true), [AddEntry(prayer, sourceDate = date, ONERI, 1, corrects = null, missedInSefer = false, createdAt = now), AddPerformed(prayer, now)], grouped = true))` | KS-KAZ-10, DATA-05 |

- **Kural 6'daki "düzeltmeler":** `open`'daki her kayıt için, `entries` sırasıyla bir `AddEntry` yazılır: `prayer = kayıt.prayer`, `sourceDate = kayıt.sourceDate`, `kind = DUZELTME`, `count = -kayıt.count`, `corrects = kayıt.id`, `missedInSefer = kayıt.missedInSefer`, `createdAt = now`. Bu, F2-03'teki `decideUndo`'nun bir `KazaEntry` için ürettiği düzeltmenin aynısıdır.
- **Kural 5:** Eklenen borç başka bir kazayla zaten düşülmüşse geri alma borcu negatife düşürür. Bu durumda soru sorulmaz (FR-KAZ-04, DATA-05).
- **Kural 7 ile 8 arasındaki fark:** Borç bu vakit için daha önce eklenmişse ve hâlâ en az 1 ise, kaza o borcu düşer (KT-12). Aksi hâlde borç bu işlemle eklenip aynı işlemle düşülür, net etki sıfırdır (KS-KAZ-10). Takip başlangıcından önceki günler de kural 8'e girer: o günün başlangıç borcuna katılıp katılmadığı bilinmez ve borcu azaltmamak ihtiyata uygundur (DATA-05).
- **Cuma:** Geç işaret hiçbir zaman cuma olarak konmaz (`asCuma = false`, DATA-05). Cuma günü öğlesi geç işaretlenirse öğle olarak işaretlenir.
- **Takip başlangıcı:** Kural tablosuna girmez. Geç işaretlemenin süre sınırı yoktur (DATA-05).

**Testler** (`LateMarkTest`). Kısaltmalar: `windows1 = PrayerDayWindows(times(1), times(2))`, `windows3 = PrayerDayWindows(times(3), times(4))`, `windows5 = PrayerDayWindows(times(5), times(6))`. Uygulama adımı `LateMarkFixture` ile yapılır (A4).

- `KT12_BR04_oneriOnaylanmis_sonradanKazaOlarakKildim_borcSifir`:
  - Kurulum: `DayRecord.empty(G1)`. Defterde `e1` var: öğle, `ONERI`, `sourceDate = G1`, adet 1, `createdAt = G1 15:40`. Borç(öğle) = 1.
  - G1 16:00'da `decideLateMark(OGLE, AS_KAZA, windows1, ledger, clock)` → `Allowed(LateMarkWrites(Mark(G1, OGLE, G1 16:00, false, true), [AddPerformed(OGLE, G1 16:00)], grouped = true))`.
  - `op1` ile uygulanınca:
    - `statusOf(OGLE) == KILINDI` ve işaretin `asKaza == true`
    - `balance(OGLE) == 0` ve `performed` tek kayıt, `operationId == op1`
    - `evaluateMissed` öğle için sonuç döndürmez
- `KT13_BR04_oneriYanitlanmamis_vaktindeKildim_borcDegismez`:
  - Kurulum: `DayRecord.empty(G1)`, `KazaLedger.EMPTY`.
  - G1 16:00'da `ON_TIME` → `Allowed(LateMarkWrites(Mark(G1, OGLE, G1 16:00, false, false), [], grouped = false))`.
  - Uygulanınca:
    - `statusOf(OGLE) == KILINDI`
    - `evaluateMissed` (takip başlangıcı G1 00:00) öğle için sonuç döndürmez
    - defter `EMPTY` kalır
- `KS_TRK_05_ertesiGunVaktindeKildim`: Saat G2 10:00, kayıt ve vakitler G1'in (`windows1`), `ON_TIME` → `Allowed(... Mark(G1, OGLE, G2 10:00, false, false) ...)`.
- `KS_TRK_13_kurulumOncesiGunVaktindeKildim`: `DayRecord.empty(G3)`, `windows3`, saat G5 10:00, `ON_TIME` → `Mark(G3, OGLE, G5 10:00, false, false)`, `kazaWrites` boş. Uygulanınca defter `EMPTY` kalır.
- `FR_TRK_04_gecIsaretlemeKararlari`: `@ParameterizedTest`, `Executable` satırlarıyla.
  - `"FR-TRK-04 isaretli kalem"`: öğle G1 12:40'ta işaretli, G1 16:00 → `Blocked(ALREADY_MARKED)`
  - `"FR-TRK-04 vakti cikmamis kalem"`: G1 15:29, öğle → `Blocked(NOT_LATE)`
  - `"FR-TRK-04 yatsisiz vitir"`: yatsı ve vitir boş, G2 06:00, vitir → `Blocked(YATSI_FIRST)`
  - `"FR-TRK-04 yatsi isaretliyken vitir vaktinde"`: yatsı G1 19:45'te işaretli, G2 06:00, vitir `ON_TIME` → `Allowed(... Mark(G1, VITIR, G2 06:00, false, false), [], false)`
  - `"FR-TRK-04 kurulum oncesi gun kaza olarak"`: `DayRecord.empty(G3)`, `windows3`, `KazaLedger.EMPTY`, G5 10:00, `AS_KAZA` → kural 8'in iki yazması, `grouped = true`
  - `"FR-TRK-04 acik kayit var ama borc sifir"`: defterde `e1` (öğle `ONERI`, G1) ve `p1` (öğle kılınan), G1 16:00, `AS_KAZA` → kural 8'in iki yazması
  - `"FR-TRK-04 otomatik kayit varken kaza olarak"`: defterde `e1` (öğle `OTOMATIK`, G1), G1 16:00, `AS_KAZA` → yalnızca `AddPerformed`, `grouped = true`
  - `"FR-TRK-04 cuma gunu ogle gec isaret"`: `DayRecord.empty(G5)`, `windows5`, G5 16:00, `ON_TIME` → `Mark(G5, OGLE, G5 16:00, asCuma = false, asKaza = false)`
- `DATA05_vaktindeKildim_eklenmisBorc`: `@ParameterizedTest`, `Executable` satırlarıyla.
  - `"DATA-05 oneri kaydi varken vaktinde"`: defterde `e1` (öğle `ONERI`, G1, adet 1), G1 16:00, `ON_TIME` → `NeedsConfirmation(UNDO_ADDED_DEBT, ifConfirmed = LateMarkWrites(mark, [AddEntry(OGLE, G1, DUZELTME, -1, corrects = e1, false, G1 16:00)], false), ifDeclined = LateMarkWrites(mark, [], false))`
  - `"DATA-05 otomatik kaydi varken vaktinde"`: aynı, `e1` `OTOMATIK`
  - `"DATA-05 geri alma borcu eksiye dusururse"`: defterde `e1` (öğle `ONERI`, G1) ve `p1` (öğle kılınan), G1 16:00, `ON_TIME` → `Allowed(LateMarkWrites(mark, [], false))`
- `DATA05_vaktindeKildim_onayVeRet`: `"DATA-05 oneri kaydi varken vaktinde"` kurulumuyla:
  - `ifConfirmed` uygulanınca `balance(OGLE) == 0`, `isCorrected(e1)` doğru ve `evaluateMissed` öğle için sonuç döndürmez (kalem işaretli).
  - `ifDeclined` uygulanınca `balance(OGLE) == 1` ve kalem `KILINDI`.
- `PrayerStatusTest.M24_yanlisGununVakitleriReddedilir` testine bir satır eklenir: `argumentSet("M-24 decideLateMark yanlis gun")` (`DayRecord.empty(G1)`, `PrayerDayWindows(times(2), times(3))`) → `IllegalArgumentException`.

Commit: `feat(domain): add late marking decision (FR-TRK-04, KT-12, KT-13)`

### A3 — İşlemin birlikte geri alınması (FR-KAZ-05, DATA-04)

```kotlin
// domain.tracking, dosya: OperationUndo.kt
sealed interface OperationUndoDecision {
    data class Allowed(
        val clear: RecordWrite.Clear,
        val kazaWrites: List<KazaWrite.AddEntry>,
    ) : OperationUndoDecision
    data class Blocked(val reason: OperationUndoBlock) : OperationUndoDecision
}
enum class OperationUndoBlock { NOT_FOUND, ALREADY_UNDONE, WOULD_GO_NEGATIVE }

fun decideUndoOperation(
    operationId: OperationId,
    record: DayRecord,
    ledger: KazaLedger,
    clock: Clock,
): OperationUndoDecision

// KazaLedger'daki decideUndo'ya yeni engel:
enum class UndoBlock { NOT_FOUND, PART_OF_OPERATION, ALREADY_CORRECTED, CORRECTION_NOT_UNDOABLE, WOULD_GO_NEGATIVE }
```

**`decideUndoOperation` kuralları.** Kısaltmalar: `entries` = defterde `operationId`'yi taşıyan `KazaEntry`'ler, `performed` = defterde `operationId`'yi taşıyan `KazaPerformed`'lar, `mark` = `record`'da `operationId`'yi taşıyan işaret. Sırayla değerlendirilir:

| # | Durum | Sonuç |
| --- | --- | --- |
| 1 | `entries` ve `performed` ikisi de boş | `Blocked(NOT_FOUND)` |
| 2 | `entries` ya da `performed`'dan en az biri geri alınmış (`ledger.isCorrected(id)`) | `Blocked(ALREADY_UNDONE)` |
| 3 | `mark` yok | `Blocked(NOT_FOUND)` |
| 4 | Düzeltmelerin toplam adedi ile kalemin borcu toplamı < 0 | `Blocked(WOULD_GO_NEGATIVE)` |
| 5 | Aksi hâlde | `Allowed(Clear(record.date, mark.prayer), düzeltmeler)` |

- **Düzeltmeler,** sırası: önce `entries` (defter sırasıyla), sonra `performed` (defter sırasıyla).
  - Her `KazaEntry` için: `AddEntry(prayer, sourceDate = kayıt.sourceDate, DUZELTME, count = -kayıt.count, corrects = kayıt.id, missedInSefer = kayıt.missedInSefer, createdAt = now)`.
  - Her `KazaPerformed` için: `AddEntry(prayer, sourceDate = null, DUZELTME, count = 1, corrects = kayıt.id, missedInSefer = false, createdAt = now)`.
  - Bunlar `decideUndo`'nun ürettiği düzeltmelerle aynıdır.
- Düzeltmeler hiçbir işleme ait değildir (A1 değişmezi). Yazan katman onlara `operationId` vermez.
- Bu fonksiyon kural 2'den sonra işlemin tamamen geri alınıp alınmadığına bakar. Kısmi geri alma olamaz, çünkü işleme ait bir kayıt tek başına geri alınamaz (aşağıda).

**`decideUndo` değişikliği (F2-03).** Hedef kayıt bulunduktan hemen sonra, mevcut bütün kontrollerden önce, kayıt bir `KazaEntry` ya da `KazaPerformed` olsun, `operationId != null` ise `Blocked(PART_OF_OPERATION)` döner. Mevcut sıralar ve sonuçlar başka hiçbir yerde değişmez. `KazaLedger` yorumuna DATA-04 "İşlem" atfını ekle.

**Testler** (`OperationUndoTest`). Kurulumlar `LateMarkFixture` ile A2 kararlarından üretilir; elle defter kurulmaz, aksi yazıyorsa elle kurulur.

- `KS_KAZ_10_oneriBekleyenVakitKazaOlarak_tekIslem_birlikteGeriAlinir`:
  - Kurulum: `DayRecord.empty(G1)`, `KazaLedger.EMPTY`.
  - G2 10:00'da `decideLateMark(OGLE, AS_KAZA, windows1, …)` → kural 8. `op1` ile uygulanınca:
    - `balance(OGLE) == 0`, işaret `asKaza == true`
    - `evaluateMissed` (G2 10:00) öğle için sonuç döndürmez
    - defterdeki iki kayıt da `op1` taşır
  - G2 10:05'te `decideUndoOperation(op1, record, ledger, clock)` → `Allowed(Clear(G1, OGLE), [AddEntry(OGLE, G1, DUZELTME, -1, corrects = ilk kayıt, false, G2 10:05), AddEntry(OGLE, null, DUZELTME, 1, corrects = kılınan kayıt, false, G2 10:05)])`.
  - Uygulanınca:
    - `balance(OGLE) == 0`, kalem işaretsiz
    - `evaluateMissed` (G2 10:05) → `[Suggest(G1, OGLE)]` (öneri yeniden bekler)
- `FR_KAZ_05_islemBirlikteGeriAlinir`: `@ParameterizedTest`, `Executable` satırlarıyla.
  - `"FR-KAZ-05 yalniz kilinan kaza islemi"`: KT-12 kurulumu (`e1` `ONERI` + `AS_KAZA` → kural 7), `op1` ile uygulanır. `decideUndoOperation` → `Allowed(Clear(G1, OGLE), [AddEntry(OGLE, null, DUZELTME, 1, corrects = kılınan kayıt, false, now)])`. Uygulanınca:
    - `balance(OGLE) == 1`
    - `statusOf(OGLE) == KILINMADI`
    - `evaluateMissed` öğle için sonuç döndürmez (`e1` hâlâ açık)
  - `"FR-KAZ-05 islem bulunamadi"`: `KazaLedger.EMPTY`, `op9` → `Blocked(NOT_FOUND)`
  - `"FR-KAZ-05 islem zaten geri alindi"`: KS-KAZ-10 işlemi uygulanmış ve geri alınmış defter; işaret elle yeniden kurulmuş `DayRecord` (`asKaza = true`, `op1`). → `Blocked(ALREADY_UNDONE)`
  - `"FR-KAZ-05 gun kaydinda isaret yok"`: KS-KAZ-10 işlemi uygulanmış defter, `DayRecord.empty(G1)` → `Blocked(NOT_FOUND)`
  - `"FR-KAZ-05 borc eksiye duserse"`: Elle kurulur. Defter:
    - `e1`: öğle `ONERI`, G1, adet 1, `op1`
    - `p1`: öğle kılınan, işlemsiz
    - `DayRecord(G1)`: `op1` taşıyan `asKaza` öğle işareti
    - Borç(öğle) = 0; geri alma −1 → `Blocked(WOULD_GO_NEGATIVE)`
- `FR_KAZ_05_islemKaydiTekBasinaGeriAlinmaz`: `@ParameterizedTest`. KS-KAZ-10 işlemi uygulanmış defter.
  - `"FR-KAZ-05 islemdeki oneri kaydi"`: `ledger.decideUndo(öneri kaydının kimliği, clock)` → `Blocked(PART_OF_OPERATION)`
  - `"FR-KAZ-05 islemdeki kilinan kaza"`: `ledger.decideUndo(kılınan kaydın kimliği, clock)` → `Blocked(PART_OF_OPERATION)`

Commit: `feat(domain): add operation undo and block single undo of grouped records (FR-KAZ-05)`

### A4 — Test yardımcıları (K-14)

Mevcut iki yardımcıya, varsayılanı `null` olan bir parametre eklenir. Mevcut çağrılar değişmez:

```kotlin
// domain.kaza.testing
object LedgerFixture {
    fun apply(ledger: KazaLedger, writes: List<KazaWrite>, operationId: OperationId? = null): KazaLedger
}
// domain.tracking.testing
object DayRecordFixture {
    fun apply(record: DayRecord, writes: List<RecordWrite>, operationId: OperationId? = null): DayRecord
    // markedAtStart değişmez
}
```

- `LedgerFixture.apply`, verilen `operationId`'yi yazdığı her `KazaEntry` ve `KazaPerformed`'a koyar. Kimlik üretimi (`w<n>`) değişmez.
- `DayRecordFixture.apply`, verilen `operationId`'yi her `Mark`'tan kurulan işarete koyar.

Yeni yardımcı:

```kotlin
// domain.tracking.testing
object LateMarkFixture {
    /** grouped ise operationId zorunlu, değilse null olmalı (test hatası: IllegalArgumentException). */
    fun apply(
        record: DayRecord,
        ledger: KazaLedger,
        writes: LateMarkWrites,
        operationId: OperationId?,
    ): Pair<DayRecord, KazaLedger>
    fun applyUndo(
        record: DayRecord,
        ledger: KazaLedger,
        decision: OperationUndoDecision.Allowed,
    ): Pair<DayRecord, KazaLedger>
}
```

- `apply`: `writes.grouped` ise `operationId` dolu olmalı, değilse `null`. İşareti `DayRecordFixture.apply` ile, kaza yazmalarını `LedgerFixture.apply` ile, aynı `operationId` vererek uygular.
- `applyUndo`: `clear` işlemini `DayRecordFixture.apply` ile, düzeltmeleri `LedgerFixture.apply` ile (`operationId = null`) uygular.

Yardımcılar ilk ihtiyaç duyuldukları adımda (A2) oluşturulur ve o adımın commit'ine girer.

### A5 — Kapsam kaçakları (F2-03 ve F2-04 incelemesinden)

Mevcut parametreli testlere birer satır eklenir. Beklenen değerler şunlardır:

| Test | Yeni `argumentSet` | Kurulum ve beklenen |
| --- | --- | --- |
| `MarkPrayerTest.FR_TRK_03_geriAlmaKurallari` | `"FR-TRK-03 vitir bosken yatsi geri alinir"` | Yatsı G1 19:45'te işaretli, vitir boş → `decideUnmark(YATSI) == Allowed(Clear(G1, YATSI))` |
| `MissedSuggestionTest.DATA05_borcaYazilmisKalemIcinOneriYok` | `"DATA-05 baska kalemin kaydi"` | Defterde ikindi `ONERI`, `sourceDate = G1` → `[Suggest(G1, OGLE)]`. `entry` yardımcısına bir `prayer` parametresi eklenebilir (varsayılan `OGLE`) |
| `UndoAndSetDebtTest.FR_KAZ_05_geriAlmaKurallari` | `"FR-KAZ-05 kilinan kaza ikinci geri alma"` | Defterde `e1` (`ELLE`, adet 2), `p1` (kılınan) ve `p1`'i düzelten `d1` (`DUZELTME`, +1). `decideUndo(p1) == Blocked(ALREADY_CORRECTED)` |

Bu satırlar A2'nin commit'ine girer. A2'den ayrı bir commit istersen planda belirt: `test(domain): cover remaining undo and suggestion branches`.

## Bölüm B — Kapılar

1. **`ktPending`:** Kök `build.gradle.kts`'teki `// F2-04b` grubunu (yorum satırı, `"KT-12"`, `"KT-13"`) tamamen sil. Başka hiçbir satıra dokunma.
2. `.\gradlew.bat checkKtTraceability` şunu yazmalı: `KT izlenebilirlik: 101 geçerli satır, 21 testli, 80 bekliyor, fark yok.`
3. `.\gradlew.bat :core:domain:jacocoTestCoverageVerification` yeşil olmalı. Satır ve dal kapsamı ile kapsanmayan satır ve dallar dosya:satır olarak rapora yazılır (CLAUDE.md Bölüm 11).

Commit: `build: remove F2-04b rows from pending KT list (K-16)`

## Bilinen tuzaklar

1. **Motor kimlik üretmez.** `UUID.randomUUID()` ya da benzeri hiçbir çağrı domain'e girmez. İşlem kimliği yalnızca test yardımcılarında (`op1`) ve Faz 4'teki yazan katmanda üretilir. `KazaWrite` ve `RecordWrite`'a `operationId` alanı **eklenmez.**
2. **"Vaktinde kıldım" bir işlem değildir.** Onaylanan borç geri alması bile `grouped = false`'tır. İşaret `decideUnmark` ile tek başına geri alınır. Düzeltme kayıtları zaten geri alınmaz (DATA-04).
3. **Kural 7'nin koşulu iki parçalıdır.** Açık kayıt olması yetmez, borç da en az 1 olmalıdır. Yoksa `AddPerformed` defterin değişmezini bozar (borç negatif olur).
4. **Namaz günü ≠ takvim günü.** KS-TRK-05'te saat G2 10:00'dur ama kayıt G1'indir ve `windows1` verilir. `decideLateMark` "şu an bu namaz gününde mi" diye bakmaz, yalnızca `now >= missedAt` olmasına bakar.
5. **Sıra önemlidir.** `decideLateMark`'ta "vakti çıkmadı" kuralı "önce yatsı" kuralından önce gelir. Vakit içindeki vitir `decideMark`'ın işidir. `decideUndoOperation`'da "zaten geri alındı" kuralı "işaret yok" kuralından önce gelir.
6. **F2-03 ve F2-04 testlerinin beklentileri değişmez.** A1'deki üç test yalnızca kurulumda `operationId` alır. Bunun dışında bir test kırılırsa kodu düzelt; testi değil.
7. **KT kimliği tablodaki gibi yazılır:** `KT12_`, `KT13_`.
8. **`git add -A`, `git add .`, `stash`, `--amend`, `reset --hard` yasak.** Belge commit'lerine kod girmez.

## Dur ve sor

- Çalışma ağacında ön koşullarda sayılanlar dışında bir değişiklik var ya da `main`, `origin/main`'in gerisinde.
- Kartın bir kuralı SRS DATA-04, DATA-05, FR-TRK-04, FR-KAZ-05 ya da KT-12, KT-13 ile çelişiyor gibi görünüyor.
- `KazaWrite` ya da `RecordWrite`'ı değiştirmek, domain'de kimlik üretmek ya da A1'dekilerden başka bir mevcut testi değiştirmek gerekiyor gibi görünüyor.
- `Prayer`, `RuleSet`, `domain.times` ya da `domain.settings`'e dokunmak gerekiyor gibi görünüyor.
- `CLAUDE.md` Bölüm 12'deki diğer koşullar.

## Kapsam dışı (bu kartta yapma)

- Kerahat engeli ve sefer (F2-05). Geç "kaza olarak kıldım" kerahat vaktinde yapılırsa ne olacağı F2-05'te `decideLateMark`'a eklenecek.
- Tertip notu (F2-06), mazeret (F2-07 – F2-09). Mazeretli kalemin geç işaretlenmesi mazeret kartlarında ele alınacak.
- Başlangıç borcu satırının ("+49", KS-ONB-04) tek işlem olarak gösterilmesi. Bu bir geçmiş ekranı gösterimidir; gerekirse Faz 4–5'te işlem kimliğiyle ele alınacak.
- Widget'tan geç işaretleme (KS-WDG-07), kaza geçmişi ekranında "2 kayıt birlikte" satırının gösterimi (Faz 5–6).
- Room şeması, repository, kimlik üretimi (Faz 4).
- `Prayer`, `RuleSet`, `domain.times`, `domain.settings`, `ci.yml`, lint kapısı, M-08 ve `ProjectConfig`.
- `docs/` altındaki belgeleri değiştirmek (kartın plan ve rapor dosyaları hariç).

## Kabul kriterleri

**Domain**

- [ ] `OperationId`. `KazaEntry`, `KazaPerformed` ve `PrayerMark`'ta `operationId` (son parametre, varsayılan `null`). Üç yeni değişmez A1 tablosundaki gibi.
- [ ] `LateChoice`, `LateMarkWrites`, `LateMarkDecision`, `LateMarkQuestion`, `LateMarkBlock`, `DayRecord.decideLateMark`, `KazaLedger.openEntriesFor` kartın imzalarıyla.
- [ ] `OperationUndoDecision`, `OperationUndoBlock`, `decideUndoOperation`. `UndoBlock.PART_OF_OPERATION` ve `decideUndo`'daki yeni kural.
- [ ] Kurallar A2 ve A3 tablolarıyla birebir. Hiçbir karar `DayRecord` ya da `KazaLedger`'ı değiştirmiyor; domain'de kimlik üretilmiyor.
- [ ] KT-12 ve KT-13'ün beklenenleri karar tablosuyla aynı.
- [ ] F2-03 ve F2-04 testlerinin beklenen değerleri değişmemiş. A1'deki üç test yalnızca kurulumda `operationId` almış.
- [ ] Her A adımında testin önce kırmızı ya da derlenmeyen olduğu raporda yazıyor.
- [ ] Yasak çağrı araması boş (Bölüm D).

**Kapılar**

- [ ] `ktPending`'den yalnızca `// F2-04b` grubu silinmiş.
- [ ] KT kapısı: `101 geçerli satır, 21 testli, 80 bekliyor, fark yok.`
- [ ] Kapsam kapısı yeşil. Satır ve dal yüzdeleri ile kapsanmayan satır ve dallar (dosya:satır, gerekçeli) raporda.

**Genel**

- [ ] `.\gradlew.bat ciCheck` yerelde yeşil; lint kapısı "bilinen 6 uyarı eşleşti, fark yok".
- [ ] `.\gradlew.bat help` hatasız (M-08).
- [ ] `.github/workflows/ci.yml` değişmemiş.
- [ ] Belge commit'lerinde (`docs:`, `docs(tasks):`) kod, test ya da derleme dosyası yok.
- [ ] Plan `docs/tasks/F2-04b-plan.md`'de, rapor `docs/tasks/F2-04b-rapor.md`'de. Rapor `CLAUDE.md` Bölüm 11 biçiminde ve son satırı **PR başlığı**.
- [ ] Commit'ler `feat/f2-04b-late-marking` dalında ve Conventional Commits biçiminde. `main`'e dokunulmamış, F1-05 dosyaları commit'lenmemiş.

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
grep -rnE "android\.|Instant\.now|LocalDate\.now|LocalDateTime\.now|ZoneId\.systemDefault|System\.currentTimeMillis|UUID|!!|clock\.zone" core/domain/src/main | grep -vE "^[^:]+:[0-9]+:[[:space:]]*(\*|//)"
```

Bu arama çıktı vermemeli.

Beklenenler:

- `ciCheck` yeşil.
- `:core:domain` testleri: F2-04 sonrası 203 + bu kartın testleri. Toplamı ve dosya dosya sayıları rapora yaz. Planlamanın hesabı: 34 yeni çalıştırma (OperationInvariantTest 6, LateMarkTest 16, OperationUndoTest 8, M-24'e 1, A5'te 3), toplam 237. Fark varsa nedenini yaz.
- Satır kapsamı %90 veya üstü. Kapsanmayan satır ve dallar dosya:satır olarak raporda.
- KT kapısı: 101 geçerli, 21 testli, 80 bekliyor.
- Lint kapısı: bilinen 6 uyarı eşleşti.

Commit sırası:

1. `docs: add F2-04b card and update planning docs`
2. `docs(tasks): add F2-04b plan`
3. `feat(domain): add operation id to kaza and day records (DATA-04)`
4. `feat(domain): add late marking decision (FR-TRK-04, KT-12, KT-13)`
5. `feat(domain): add operation undo and block single undo of grouped records (FR-KAZ-05)`
6. `build: remove F2-04b rows from pending KT list (K-16)`
7. `docs(tasks): add F2-04b report`

Commit mesajlarını tek satır yaz ve düz ASCII tırnak kullan; `git log --oneline` ile bozulmadıklarını kontrol et.

Raporu `docs/tasks/F2-04b-rapor.md` dosyasına, `CLAUDE.md` Bölüm 11'e göre yaz ve son commit olarak ekle. Ek olarak:

- Test sayısı dosya dosya.
- Kapsam yüzdeleri ve kapsanmayan satır ve dallar.
- KT kapısının çıktısı.
- A1'de değişen üç testin önceki ve sonraki kurulum satırı (birer satır).
- Son satır: `PR başlığı: feat(domain): add late marking and grouped undo with operation ids (F2-04b)`

**Push etme, PR açma.** Kullanıcı yapacak.

# F2-04b Uygulama Planı — Geç işaretleme ve birlikte geri alma

Dal: `feat/f2-04b-late-marking` · 2 Kasım 2026 · Onay: Adım 0 planı onaylandı; D1 (test sayısı) işlendi.

Bu plan, `docs/tasks/F2-04b-gec-isaretleme.md` kartının Adım 0 çıktısıdır. Kartın sırası ve kapsamı esastır. D1 yalnızca test sayısı hesabını netleştirir: **34 yeni çalıştırma, toplam 237**.

## 1. Git başlangıcı

- `main` = `origin/main` = `bdbea1d50564e938b3e113bd2bdce0e12b8ed0b7`; `HEAD...origin/main` → `0 0`.
- `bdbea1d` `HEAD`'in atasıdır; `git fetch origin` yeni bir şey getirmedi; `git stash list` boş.
- Yerel dallar: `main`, `chore/f1-02-modules-hygiene`, `feat/f1-01-skeleton-domain`.
- Çalışma ağacı grupları:
  - Bu kartın dört dosyası: `CLAUDE.md`, `docs/gereksinim-analizi-srs.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-04b-gec-isaretleme.md`.
  - F1-05'in iki izlenmeyen dosyası: `docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md` — dokunulmaz, commit edilmez.
  - Beklenmeyen değişiklik yok.

İlk commit yalnızca dört belgedir; dosyalar adlarıyla eklenir:

```
git add CLAUDE.md docs/gereksinim-analizi-srs.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-04b-gec-isaretleme.md
git commit -m "docs: add F2-04b card and update planning docs"
```

İkinci commit bu plan dosyasıdır: `docs(tasks): add F2-04b plan`.

## 2. Dosya ağacı ve imzalar

Karttaki imzalardan **sapma yok**. `Prayer`, `RuleSet`, `domain.times` ve `domain.settings` değişmiyor. `KazaWrite` ve `RecordWrite` değişmiyor; domain'de kimlik üretilmiyor (UUID yok).

**Yeni tipler**

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/kaza/OperationId.kt`
  - `@JvmInline value class OperationId(val value: String)` — `init`: `value.isNotBlank()`.
- `.../tracking/LateMarkDecision.kt`
  - `enum class LateChoice { ON_TIME, AS_KAZA }`
  - `data class LateMarkWrites(val mark: RecordWrite.Mark, val kazaWrites: List<KazaWrite>, val grouped: Boolean)`
  - `sealed interface LateMarkDecision`; `Allowed(writes)`, `NeedsConfirmation(question, ifConfirmed, ifDeclined)`, `Blocked(reason)`.
  - `enum class LateMarkQuestion { UNDO_ADDED_DEBT }`
  - `enum class LateMarkBlock { ALREADY_MARKED, NOT_LATE, YATSI_FIRST }`
- `.../tracking/OperationUndo.kt`
  - `sealed interface OperationUndoDecision`; `Allowed(clear: RecordWrite.Clear, kazaWrites: List<KazaWrite.AddEntry>)`, `Blocked(reason)`.
  - `enum class OperationUndoBlock { NOT_FOUND, ALREADY_UNDONE, WOULD_GO_NEGATIVE }`
  - `fun decideUndoOperation(operationId, record, ledger, clock): OperationUndoDecision`

**Değişen tipler**

- `.../kaza/KazaRecord.kt`
  - `KazaEntry` ve `KazaPerformed`'a **son parametre** `val operationId: OperationId? = null`.
  - `KazaEntry.init`: `kind == DUZELTME` ise `operationId == null`.
- `.../tracking/PrayerMark.kt`
  - Son parametre `val operationId: OperationId? = null`.
- `.../tracking/DayRecord.kt`
  - `init`: `asKaza == true` ise `operationId != null`; `operationId != null` ise `asKaza == true`.
  - `fun decideLateMark(prayer, choice, windows, ledger, clock): LateMarkDecision`.
- `.../kaza/KazaLedger.kt`
  - `init`: aynı `operationId`'yi taşıyan bütün `entries` ve `performed` aynı kalemde.
  - `fun openEntriesFor(date: LocalDate, prayer: Prayer): List<KazaEntry>`
  - `hasOpenEntryFor` aynı koşulla `openEntriesFor(...).isNotEmpty()` olarak sadeleştirilebilir; davranış değişmez.
  - `UndoBlock`'a `PART_OF_OPERATION`.
  - `decideUndo`: hedef kayıt bulunduktan hemen sonra, mevcut kontrollerden önce, `operationId != null` ise `Blocked(PART_OF_OPERATION)`.

**Test dosyaları**

- `.../tracking/OperationInvariantTest.kt` (yeni)
- `.../tracking/LateMarkTest.kt` (yeni)
- `.../tracking/OperationUndoTest.kt` (yeni)
- `.../kaza/testing/LedgerFixture.kt` (değişir)
- `.../tracking/testing/DayRecordFixture.kt` (değişir)
- `.../tracking/testing/LateMarkFixture.kt` (yeni)

## 3. A1'de değişen üç F2-04 testi

Beklenen değerler **değişmez**; yalnızca `asKaza = true` olan işarete `operationId` eklenir. Üç yardımcıda da koşullu yazılır, çünkü aynı yardımcı `asKaza = false` için de kullanılıyor.

1. **`PrayerStatusTest` — `markedRecord` (satır 127):**
   - Önce: `asKaza = asKaza`
   - Sonra: `asKaza = asKaza,` + `operationId = if (asKaza) OperationId("op1") else null`
   - Etkilenen argumentSet: `"DATA-05 ogle kaza isaretli 16:00 kilindi"`; beklenen `KILINDI` aynı.

2. **`MarkPrayerTest` — `cm` (satır 444):**
   - Önce: `asKaza = asKaza`
   - Sonra: `asKaza = asKaza,` + `operationId = if (asKaza) OperationId("op1") else null`
   - Etkilenen argumentSet: `"FR-TRK-03 kaza isareti"`; beklenen `KAZA_MARK_NEEDS_GROUP_UNDO` aynı.

3. **`DayRecordTest` — `mark` (satır 105):**
   - Önce: `asKaza = asKaza`
   - Sonra: `asKaza = asKaza,` + `operationId = if (asKaza) OperationId("op1") else null`
   - Etkilenen argumentSet: `"DATA-05 cuma ve kaza birlikte"`; `IllegalArgumentException` beklentisi aynı; hata yine "cuma ve kaza birlikte olamaz" kuralından gelir.

Başka bir mevcut test değişmeyecek.

## 4. Test listesi

Yeni KT kimlikleri yalnızca **KT-12 ve KT-13**. Başka yeni KT kimliği yok. `KT-18` mevcut testi değişmiyor; `"M-24 decideLateMark yanlis gun"` KT kimliği değil.

D1 sonrası beklenen: **34 yeni çalıştırma**, F2-04 sonrası 203 ile toplam **237**.

### A1 — `OperationInvariantTest` (6)

`DATA04_islemDegismezleri` argumentSet satırları:
- `"DATA-04 duzeltme isleme ait olamaz"`
- `"DATA-04 islem kayitlari ayni kalemde"`
- `"DATA-05 kaza isareti islemsiz olamaz"`
- `"DATA-05 islemli isaret kaza olmali"`
- `"DATA-04 islemli kayitlar kabul"`
- `"DATA-04 bos islem kimligi"`

### A2 — `LateMarkTest` (16) + `PrayerStatusTest` (+1)

| Test | Sınadığı |
| --- | --- |
| `KT12_BR04_oneriOnaylanmis_sonradanKazaOlarakKildim_borcSifir` | Açık borç varken `AS_KAZA` yalnızca `AddPerformed`; borç 0; işlem `op1` |
| `KT13_BR04_oneriYanitlanmamis_vaktindeKildim_borcDegismez` | `ON_TIME` yalnızca işaret; defter değişmez |
| `KS_TRK_05_ertesiGunVaktindeKildim` | G2 10:00, G1 kaydı, `ON_TIME` → `Mark.date = G1` |
| `KS_TRK_13_kurulumOncesiGunVaktindeKildim` | G3 kaydı, G5 10:00, `ON_TIME`; defter boş |
| `FR_TRK_04_gecIsaretlemeKararlari` | 8 sınır/karar satırı |
| `DATA05_vaktindeKildim_eklenmisBorc` | 3 açık-borç satırı |
| `DATA05_vaktindeKildim_onayVeRet` | Onay ve ret sonuçları |

`FR_TRK_04_gecIsaretlemeKararlari` argumentSet satırları:
- `"FR-TRK-04 isaretli kalem"`
- `"FR-TRK-04 vakti cikmamis kalem"`
- `"FR-TRK-04 yatsisiz vitir"`
- `"FR-TRK-04 yatsi isaretliyken vitir vaktinde"`
- `"FR-TRK-04 kurulum oncesi gun kaza olarak"`
- `"FR-TRK-04 acik kayit var ama borc sifir"`
- `"FR-TRK-04 otomatik kayit varken kaza olarak"`
- `"FR-TRK-04 cuma gunu ogle gec isaret"`

`DATA05_vaktindeKildim_eklenmisBorc` argumentSet satırları:
- `"DATA-05 oneri kaydi varken vaktinde"`
- `"DATA-05 otomatik kaydi varken vaktinde"`
- `"DATA-05 geri alma borcu eksiye dusururse"`

`PrayerStatusTest`'e eklenen satır: `"M-24 decideLateMark yanlis gun"` (`DayRecord.empty(G1)` + `PrayerDayWindows(times(2), times(3))` → `IllegalArgumentException`).

### A3 — `OperationUndoTest` (8)

| Test | Sınadığı |
| --- | --- |
| `KS_KAZ_10_oneriBekleyenVakitKazaOlarak_tekIslem_birlikteGeriAlinir` | İki kayıt birlikte geri alınır; öneri yeniden bekler |
| `FR_KAZ_05_islemBirlikteGeriAlinir` | 5 işlem geri alma durumu |
| `FR_KAZ_05_islemKaydiTekBasinaGeriAlinmaz` | 2 tek kayıt geri alma engeli |

`FR_KAZ_05_islemBirlikteGeriAlinir` argumentSet satırları:
- `"FR-KAZ-05 yalniz kilinan kaza islemi"`
- `"FR-KAZ-05 islem bulunamadi"`
- `"FR-KAZ-05 islem zaten geri alindi"`
- `"FR-KAZ-05 gun kaydinda isaret yok"`
- `"FR-KAZ-05 borc eksiye duserse"`

`FR_KAZ_05_islemKaydiTekBasinaGeriAlinmaz` argumentSet satırları:
- `"FR-KAZ-05 islemdeki oneri kaydi"`
- `"FR-KAZ-05 islemdeki kilinan kaza"`

### A5 — Mevcut testlere eklenen 3 satır

- `MarkPrayerTest.FR_TRK_03_geriAlmaKurallari`: `"FR-TRK-03 vitir bosken yatsi geri alinir"` → Yatsı işaretli, vitir boş → `Allowed(Clear(G1, YATSI))`.
- `MissedSuggestionTest.DATA05_borcaYazilmisKalemIcinOneriYok`: `"DATA-05 baska kalemin kaydi"` → İkindi `ONERI`, `sourceDate = G1` → `[Suggest(G1, OGLE)]`.
- `UndoAndSetDebtTest.FR_KAZ_05_geriAlmaKurallari`: `"FR-KAZ-05 kilinan kaza ikinci geri alma"` → `e1` ELLE +2, `p1` kılınan, `p1`'i düzelten `d1`; `decideUndo(p1)` → `Blocked(ALREADY_CORRECTED)`.

Bu üç satır ve test yardımcıları **A2 commit'ine** girer (kart A4'te "ilk ihtiyaç duyuldukları adımda" diyor; ayrı A4 commit'i yok).

## 5. Test yardımcıları

- `LedgerFixture.apply(ledger: KazaLedger, writes: List<KazaWrite>, operationId: OperationId? = null): KazaLedger`
- `DayRecordFixture.apply(record: DayRecord, writes: List<RecordWrite>, operationId: OperationId? = null): DayRecord`
- `LateMarkFixture.apply(record: DayRecord, ledger: KazaLedger, writes: LateMarkWrites, operationId: OperationId?): Pair<DayRecord, KazaLedger>`
- `LateMarkFixture.applyUndo(record: DayRecord, ledger: KazaLedger, decision: OperationUndoDecision.Allowed): Pair<DayRecord, KazaLedger>`

- `LateMarkFixture.apply`: `writes.grouped` ise `operationId` zorunlu, değilse `null` olmalı; işareti `DayRecordFixture.apply` ile, kaza yazmalarını `LedgerFixture.apply` ile aynı `operationId` vererek uygular.
- `LateMarkFixture.applyUndo`: `clear` işlemini işaretsiz, düzeltmeleri `operationId = null` ile uygular.
- Mevcut çağrılar varsayılan `null` sayesinde değişmez.

## 6. Kural sıraları

### `decideLateMark`

1. `require(windows.day.date == date)`.
2. Kalem zaten işaretli → `Blocked(ALREADY_MARKED)`.
3. `now < missedAt(prayer)` → `Blocked(NOT_LATE)`; vakit içindeki kalem `decideMark`'ın işi.
4. `VITIR` ve yatsı işaretli değil → `Blocked(YATSI_FIRST)`.
5. `ON_TIME`, açık kayıt yok → yalnızca işaret, `grouped = false`.
6. `ON_TIME`, açık kayıt var, `balance - open.sum(count) < 0` → yalnızca işaret, soru yok.
7. `ON_TIME`, açık kayıt var → `NeedsConfirmation(UNDO_ADDED_DEBT)`: onayda açık kayıtların her biri için `-count` düzeltmesi, rette düzeltme yok; ikisi de yalnızca işaret ve `grouped = false`.
8. `AS_KAZA`, açık kayıt var ve `balance >= 1` → yalnızca `AddPerformed`, `grouped = true`.
9. `AS_KAZA`, diğer her durum → `AddEntry(ONERI, sourceDate = date, +1)` + `AddPerformed`, `grouped = true`.

Bu sıra kartın A2 tablosuyla **birebir aynıdır**: "vakti çıkmadı" kuralı "önce yatsı" kuralından önce gelir; kural 8'in koşulu iki parçalıdır (açık kayıt + borç ≥ 1).

### `decideUndoOperation`

1. `operationId` taşıyan `entries` ve `performed` ikisi de boş → `Blocked(NOT_FOUND)`.
2. Bunlardan en az biri `isCorrected` → `Blocked(ALREADY_UNDONE)`.
3. `record`'da `operationId` taşıyan işaret yok → `Blocked(NOT_FOUND)`.
4. Düzeltmelerin toplam adedi + kalemin bakiyesi < 0 → `Blocked(WOULD_GO_NEGATIVE)`.
5. Aksi hâlde `Allowed(Clear(record.date, mark.prayer), düzeltmeler)`.

Düzeltmeler önce `entries` (defter sırasıyla), sonra `performed` (defter sırasıyla) üretilir; hiçbiri işlem kimliği taşımaz. Bu sıra kartın A3 tablosuyla **birebir aynıdır**; "zaten geri alındı" kuralı "işaret yok" kuralından önce gelir.

`decideUndo` ise hedef kayıt bulunduktan sonra, mevcut `CORRECTION_NOT_UNDOABLE` / `ALREADY_CORRECTED` / `WOULD_GO_NEGATIVE` kontrollerinden önce `operationId != null` ise `PART_OF_OPERATION` döndürür.

## 7. `ktPending` değişikliği ve commit sırası

`ktPending`'den yalnızca şu grup tamamen silinir:

```kotlin
// F2-04b
"KT-12",
"KT-13",
```

Sonuç: `KT izlenebilirlik: 101 geçerli satır, 21 testli, 80 bekliyor, fark yok.`

Dal: güncel `main`'den `feat/f2-04b-late-marking`. İlk commit yalnızca dört belge; adlarıyla eklenir. F1-05 dosyaları commit edilmez; belge commit'lerine kod girmez.

1. `docs: add F2-04b card and update planning docs`
2. `docs(tasks): add F2-04b plan`
3. `feat(domain): add operation id to kaza and day records (DATA-04)`
4. `feat(domain): add late marking decision (FR-TRK-04, KT-12, KT-13)`
5. `feat(domain): add operation undo and block single undo of grouped records (FR-KAZ-05)`
6. `build: remove F2-04b rows from pending KT list (K-16)`
7. `docs(tasks): add F2-04b report`

A5 satırları ve üç yardımcı A2 commit'ine (4. commit) girer. Son doğrulama Bölüm D sırasıyla yapılır; yasak çağrı araması `UUID` dahil çalıştırılır ve boş çıkmalıdır. Raporun son satırı tam olarak:

```
PR başlığı: feat(domain): add late marking and grouped undo with operation ids (F2-04b)
```

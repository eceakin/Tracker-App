## Rapor: F2-04b — Geç işaretleme ve birlikte geri alma

Dal: `feat/f2-04b-late-marking`

### Yapılanlar

- **A1 — İşlem kimliği:** `OperationId`; `KazaEntry`, `KazaPerformed` ve `PrayerMark`'a son parametre `operationId` (varsayılan `null`). Yeni değişmezler: düzeltme işleme ait olamaz; aynı işlemdeki kayıtlar aynı kalemde; `asKaza` ile `operationId` birlikte var/yok. A1'deki üç F2-04 testi yalnızca kurulumda `operationId` aldı; beklenen değerler değişmedi.
- **A2 — Geç işaretleme:** `LateChoice`, `LateMarkWrites`, `LateMarkDecision`, `LateMarkQuestion`, `LateMarkBlock`, `KazaLedger.openEntriesFor`, `DayRecord.decideLateMark`. "Vaktinde kıldım" açık borçta onay soruyor; "sonradan kaza olarak kıldım" açık borç ve bakiye varsa yalnızca `KazaPerformed`, aksi hâlde `ONERI` + `KazaPerformed` yazıyor.
- **A3 — İşlem geri alma:** `OperationUndoDecision`, `OperationUndoBlock`, `decideUndoOperation`; `UndoBlock.PART_OF_OPERATION` ve `decideUndo`'daki yeni engel. Düzeltmeler önce `entries`, sonra `performed` sırasıyla üretiliyor; hiçbiri işlem kimliği taşımıyor.
- **A5 — Kapsam kaçakları:** Üç mevcut parametreli teste birer satır eklendi; `UndoAndSetDebtTest`'teki satır F2-04'te kaçan `KazaLedger.kt:113` performed-ikinci-geri-alma dalını kapsıyor.
- **B:** `ktPending`'den `// F2-04b`, `KT-12`, `KT-13` tamamen silindi.

### Değişen dosyalar

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/kaza/OperationId.kt` (yeni)
- `.../kaza/KazaRecord.kt`, `.../kaza/KazaLedger.kt`, `.../kaza/UndoDecision.kt`
- `.../tracking/PrayerMark.kt`, `.../tracking/DayRecord.kt`
- `.../tracking/LateMarkDecision.kt` (yeni), `.../tracking/OperationUndo.kt` (yeni)
- `core/domain/src/test/kotlin/.../tracking/OperationInvariantTest.kt` (yeni)
- `.../tracking/LateMarkTest.kt` (yeni), `.../tracking/OperationUndoTest.kt` (yeni)
- `.../kaza/testing/LedgerFixture.kt`, `.../tracking/testing/DayRecordFixture.kt`, `.../tracking/testing/LateMarkFixture.kt` (yeni)
- `.../tracking/PrayerStatusTest.kt`, `.../tracking/MarkPrayerTest.kt`, `.../tracking/DayRecordTest.kt`
- `.../tracking/MissedSuggestionTest.kt`, `.../kaza/UndoAndSetDebtTest.kt`
- `build.gradle.kts`
- `docs/tasks/F2-04b-plan.md` (yeni), `docs/tasks/F2-04b-rapor.md` (yeni)
- İlk commit'te: `CLAUDE.md`, `docs/gereksinim-analizi-srs.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-04b-gec-isaretleme.md`

`.github/workflows/ci.yml` değişmedi. F1-05 dosyaları commit edilmedi.

### Testler

`.\gradlew.bat :core:domain:test` → **237 test, 0 hata**. Dosya dosya:

| Test dosyası | Test sayısı |
| --- | --- |
| `PrayerTest` | 2 |
| `RuleSetTest` | 22 |
| `ReferenceDayTest` | 12 |
| `PrayerTimesDayTest` | 10 |
| `TimeWindowTest` | 3 |
| `PrayerDayWindowsTest` | 16 |
| `CurrentVakitTest` | 16 |
| `KerahatTest` | 16 |
| `PrayerDateTest` | 8 |
| `DaylightSavingTest` | 5 |
| `KazaRecordTest` | 10 |
| `KazaLedgerTest` | 9 |
| `PerformKazaTest` | 3 |
| `UndoAndSetDebtTest` | 13 |
| `LedgerInvariantTest` | 5 |
| `DayRecordTest` | 7 |
| `PrayerStatusTest` | 10 |
| `MarkPrayerTest` | 21 |
| `MissedSuggestionTest` | 19 |
| `OperationInvariantTest` | 6 |
| `LateMarkTest` | 16 |
| `OperationUndoTest` | 8 |
| **Toplam** | **237** |

Her A adımında test önce yazıldı ve kırmızı/derlenmeyen görüldü:
- A1: `Unresolved reference 'OperationId'` / `operationId`.
- A2: `Unresolved reference 'LateChoice'` / `LateMarkDecision` / `decideLateMark`.
- A3: `Unresolved reference 'decideUndoOperation'` / `PART_OF_OPERATION`.
- A5 test-only satırlar; ilk çalıştırma yeşil (kod A1–A3'te hazırdı).

### A1'de değişen üç F2-04 testi

Beklenen değerler değişmedi; yalnızca `asKaza = true` işaretine işlem kimliği eklendi:

| Dosya / yardımcı | Önce | Sonra |
| --- | --- | --- |
| `PrayerStatusTest.markedRecord` | `asKaza = asKaza` | `asKaza = asKaza,` + `operationId = if (asKaza) OperationId("op1") else null` |
| `MarkPrayerTest.cm` | `asKaza = asKaza` | `asKaza = asKaza,` + `operationId = if (asKaza) OperationId("op1") else null` |
| `DayRecordTest.mark` | `asKaza = asKaza` | `asKaza = asKaza,` + `operationId = if (asKaza) OperationId("op1") else null` |

Etkilenen satırlar: `"DATA-05 ogle kaza isaretli 16:00 kilindi"`, `"FR-TRK-03 kaza isareti"`, `"DATA-05 cuma ve kaza birlikte"`.

### Kapsam (JaCoCo)

| Sayaç | Kapsanan | Kaçan | Oran |
| --- | --- | --- | --- |
| Satır (LINE) | 613 | 0 | **%100** |
| Dal (BRANCH) | 362 | 7 | **%98,1** |
| Talimat (INSTRUCTION) | 3917 | 54 | %98,6 |
| Yöntem (METHOD) | 185 | 13 | %93,4 |
| Sınıf (CLASS) | 64 | 0 | %100 |

Kapsam kapısı yeşil; hiçbir sınıf/paket ölçümden hariç tutulmadı.

### Kapsanmayan dallar (CLAUDE.md 1.9)

Kaçan **satır yok**. Kaçan 7 dal:

| Dosya:satır | Dal | Neden |
| --- | --- | --- |
| `KazaLedger.kt:52` | 1 | `targetEntry?.prayer ?: targetPerformed?.prayer` Elvis ifadesinin performed-hedef dalı; düzeltmenin `KazaPerformed`'a işaret ettiği kurulum yok. |
| `KazaLedger.kt:75` | 1 | `isCorrected` predicate'inin kısa devre dalı; düzeltme olmayan kayıt tarafı tam kapsanmıyor. |
| `KazaLedger.kt:186` | 3 | `hasRecords = entries.any(...) \|\| performed.any(...)`; yalnızca `KazaPerformed` bulunan defter kombinasyonu test edilmiyor. |
| `OperationUndo.kt:49` | 1 | `entries.isEmpty() && performed.isEmpty()` bileşik koşulunun kısa devre dalı. |
| `OperationUndo.kt:54` | 1 | `entries.any(isCorrected) \|\| performed.any(isCorrected)` kısa devre dalı; yalnızca bir tarafın düzeltilmiş olduğu kurulum yok. |

Ayrıca `data class` üretilen `componentN`/`copy` yöntemleri talimat sayacında kaçıyor: `PerformDecision.kt:19`, `SetDebtDecision.kt:16`, `UndoDecision.kt:13`, `KazaRecord.kt:50`, `KazaRecord.kt:74`, `PrayerMark.kt:17`, `RecordWrite.kt:23`, `LateMarkDecision.kt:36`, `MissedOutcome.kt:20`, `OperationUndo.kt:20`, `MarkDecision.kt:13`, `MarkDecision.kt:33`, `PrayerDayWindows.kt:17`. Satır kapsamı bu satırlarda tamdır.

### KT kapısı

```
KT izlenebilirlik: 101 geçerli satır, 21 testli, 80 bekliyor, fark yok.
```

`ktPending`'den yalnızca `// F2-04b`, `KT-12`, `KT-13` silindi. Yeni KT kimlikleri yalnızca KT-12 ve KT-13.

### Son doğrulama

- `.\gradlew.bat clean` → başarılı.
- `.\gradlew.bat ciCheck` → **başarılı (1 dk 56 sn)**.
- `.\gradlew.bat :core:domain:jacocoTestReport` → başarılı.
- `.\gradlew.bat checkKtTraceability` → yukarıdaki 21 testli / 80 bekliyor satırı.
- `.\gradlew.bat help` → başarılı (M-08 bozulmadı).
- Lint kapısı: `Lint kapısı: bilinen 6 uyarı eşleşti, fark yok.`
- `git status` → yalnızca F1-05'in iki izlenmeyen dosyası.
- Yasak çağrı araması (`UUID` dahil, yorumlar hariç) → **çıktı yok**.
- Belge commit'lerinde yalnızca belge dosyaları var; kod/test/derleme dosyası yok.

### Karşılanan kimlikler

BR-00, BR-03, BR-04, BR-08, FR-TRK-03, FR-TRK-04, FR-KAZ-04, FR-KAZ-05, FR-ONB-05, DATA-04, DATA-05; KT-12, KT-13; KS-TRK-05, KS-TRK-06, KS-TRK-13, KS-KAZ-08, KS-KAZ-10, KS-WDG-07; M-20, M-23, M-24, M-25, M-31, K-03, K-11, K-12, K-14, K-16.

### Varsayımlar

- D1 doğrultusunda beklenen **34 yeni çalıştırma / 237 toplam**; gerçekleşen sayılar bununla aynı.
- `OperationUndoDecision` ve `OperationUndoBlock` tipleri, `LateMarkFixture.applyUndo` imzası derlenebilsin diye A2 commit'ine kondu; `decideUndoOperation` fonksiyonu A3 commit'inde eklendi.
- İşlem geri alma düzeltmeleri **atomik** uygulanır: `LateMarkFixture.applyUndo`, `LedgerFixture.applyAtomically` kullanır. Sıralı `LedgerFixture.apply` ile `KazaEntry` düzeltmesi (-1) ve `KazaPerformed` düzeltmesi (+1) arasında bakiye geçici olarak negatife düşüp `KazaLedger` init'ini kırıyordu; atomik uygulama işlemin tek yazma olduğu semantiğine uygundur.
- Bu nedenle kartın 7 commit'lik sırasına ek olarak `test(domain): apply operation undo atomically` commit'i eklendi; toplam 8 commit.
- `evaluateMissed` testlerinde "öğle için sonuç yok" kontrolü, tüm listenin boş olması yerine öğle kalemi için sonuç bulunmaması (`hasOutcomeFor(OGLE)`) olarak yapıldı; diğer kalemlerin işaretsiz olması listeyi doğal olarak dolduruyor.
- Cinsiyet, `Prayer`, `RuleSet`, `domain.times`, `domain.settings` değişmedi; domain'de UUID/kimlik üretimi yok.

### Belge tutarsızlıkları

Kart, SRS DATA-04 "İşlem" ve DATA-05 "Geç işaretleme" ile çelişki bulunmadı. Tek uygulama ayrıntısı, kartın A4'teki "düzeltmeleri `LedgerFixture.apply` ile uygula" ifadesinin atomik olması gerektiğidir; yukarıda gerekçelendirildi.

### Açık sorular

Yok.

### Commit'ler

1. `docs: add F2-04b card and update planning docs`
2. `docs(tasks): add F2-04b plan`
3. `feat(domain): add operation id to kaza and day records (DATA-04)`
4. `feat(domain): add late marking decision (FR-TRK-04, KT-12, KT-13)`
5. `feat(domain): add operation undo and block single undo of grouped records (FR-KAZ-05)`
6. `build: remove F2-04b rows from pending KT list (K-16)`
7. `test(domain): apply operation undo atomically`
8. `docs(tasks): add F2-04b report`

Push edilmedi, PR açılmadı.

PR başlığı: feat(domain): add late marking and grouped undo with operation ids (F2-04b)

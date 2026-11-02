## Rapor: F2-03 — Kaza defteri

Dal: `feat/f2-03-kaza-ledger`

### Yapılanlar

- **A1 — Kayıt tipleri:** `KazaRecordId`, `KazaEntryKind`, `KazaEntry`, `KazaPerformed`. `KazaEntry` değişmezleri: düzeltme dışındaki türlerde adet pozitif ve `corrects` boş; `DUZELTME` için adet sıfırdan farklı, `corrects` boş ya da dolu. Boş kimlik reddediliyor.
- **A2 — `KazaLedger`:** `balance` kayıtlardan hesaplanıyor; `isCorrected` düzeltmeyi tanıyor; `EMPTY` tüm kalemlerde 0. Altı değişmez `init` içinde: benzersiz kimlik, var olan hedef, düzeltmenin düzeltilmemesi, hedefe tek düzeltme, aynı kalem, negatif olmayan bakiye.
- **A3 — Kaza kıldım:** `KazaWrite`, `PerformDecision`, `PerformQuestion`, `PerformBlock`, `decidePerform`. Borç varsa `AddPerformed`; borç 0 ise `NeedsConfirmation` ve `ELLE +1` + `AddPerformed`; kalem seçilmemişse `Blocked(NO_PRAYER_SELECTED)`. Motor defteri değiştirmiyor.
- **A4 — Geri al ve elle düzenle:** `UndoDecision`, `UndoBlock`, `SetDebtDecision`, `SetDebtBlock`, `decideUndo`, `decideSetDebt`. Geri alma kaydı silmiyor, karşı etkili `DUZELTME` ekliyor; düzeltme geri alınamıyor, bir kayıt bir kez geri alınıyor, negatife düşüren geri alma engelleniyor. Elle düzenleme ilk kayıtta `ELLE`, sonrasında fark kadar `DUZELTME` yazıyor; negatif hedef engelleniyor.
- **A5 — Test yardımcısı ve değişmez testi:** `LedgerFixture` yazmaları sırayla uyguluyor, her adımda yeni `KazaLedger` kuruyor ve `w1`, `w2`, … kimliklerini üretiyor. Beş komut dizisinin öğle bakiyeleri aynen doğrulandı.
- **B — Kapı:** `ktPending`'den yalnızca `// F2-03` ve `KT-35`, `KT-36`, `KT-81` satırları silindi.

### Değişen dosyalar

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/kaza/KazaRecord.kt` (yeni)
- `.../kaza/KazaLedger.kt` (yeni)
- `.../kaza/KazaWrite.kt` (yeni)
- `.../kaza/PerformDecision.kt` (yeni)
- `.../kaza/UndoDecision.kt` (yeni)
- `.../kaza/SetDebtDecision.kt` (yeni)
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/kaza/KazaRecordTest.kt` (yeni)
- `.../kaza/KazaLedgerTest.kt` (yeni)
- `.../kaza/PerformKazaTest.kt` (yeni)
- `.../kaza/UndoAndSetDebtTest.kt` (yeni)
- `.../kaza/LedgerInvariantTest.kt` (yeni)
- `.../kaza/testing/LedgerFixture.kt` (yeni)
- `build.gradle.kts`
- `docs/tasks/F2-03-plan.md` (yeni), `docs/tasks/F2-03-rapor.md` (yeni)
- İlk commit'te: `CLAUDE.md`, `docs/gereksinim-analizi-srs.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-03-kaza-defteri.md`

`.github/workflows/ci.yml` değişmedi. F1-05 dosyaları commit edilmedi.

### Testler

`.\gradlew.bat :core:domain:test` → **149 test, 0 hata**. Dosya dosya:

| Test dosyası | Test sayısı |
| --- | --- |
| `PrayerTest` (F2-01) | 2 |
| `RuleSetTest` (F2-01) | 22 |
| `ReferenceDayTest` (F2-01) | 12 |
| `PrayerTimesDayTest` (F2-01) | 10 |
| `TimeWindowTest` (F2-02) | 3 |
| `PrayerDayWindowsTest` (F2-02) | 16 |
| `CurrentVakitTest` (F2-02) | 16 |
| `KerahatTest` (F2-02) | 16 |
| `PrayerDateTest` (F2-02) | 8 |
| `DaylightSavingTest` (F2-02) | 5 |
| `KazaRecordTest` | 10 |
| `KazaLedgerTest` | 9 |
| `PerformKazaTest` | 3 |
| `UndoAndSetDebtTest` | 12 |
| `LedgerInvariantTest` | 5 |
| **Toplam** | **149** |

Her A adımında test önce yazıldı ve kırmızı/derlenmeyen görüldü:
- A1: `Unresolved reference 'KazaRecordId'` / `KazaEntry`.
- A2: `Unresolved reference 'KazaLedger'`.
- A3: `Unresolved reference 'KazaWrite'` / `PerformDecision`.
- A4: `Unresolved reference 'UndoDecision'` / `SetDebtDecision`.
- A5: Test-only adım; kod A1–A4'te hazır olduğu için ilk çalıştırma **yeşildi** (5/5), kırmızı beklenmiyordu.

### Kapsam (JaCoCo)

| Sayaç | Kapsanan | Kaçan | Oran |
| --- | --- | --- | --- |
| Satır (LINE) | 325 | 1 | **%99,7** |
| Dal (BRANCH) | 199 | 6 | **%97,1** |
| Talimat (INSTRUCTION) | 2317 | 39 | %98,3 |
| Yöntem (METHOD) | 113 | 7 | %94,2 |
| Sınıf (CLASS) | 33 | 0 | %100 |

Kapsam kapısı yeşil; hiçbir sınıf/paket ölçümden hariç tutulmadı.

### KT kapısı

```
KT izlenebilirlik: 101 geçerli satır, 3 testli, 98 bekliyor, fark yok.
```

`ktPending`'den yalnızca F2-03 grubu silindi; başka satıra dokunulmadı.

### Son doğrulama

- `.\gradlew.bat clean` → başarılı.
- `.\gradlew.bat ciCheck` → **başarılı (2 dk 9 sn)**.
- `.\gradlew.bat :core:domain:jacocoTestReport` → başarılı.
- `.\gradlew.bat checkKtTraceability` → yukarıdaki 3 testli / 98 bekliyor satırı.
- `.\gradlew.bat help` → başarılı (M-08 bozulmadı).
- Lint kapısı: `Lint kapısı: bilinen 6 uyarı eşleşti, fark yok.`
- `git status` → yalnızca `docs/tasks/F1-05-plan.md` ve `docs/tasks/F1-05-risk-prototipi.md` izlenmeyen.
- Yasak çağrı araması (yorumlar hariç) → **çıktı yok**.
- `git log --stat --format="%h %s" main..HEAD -- docs CLAUDE.md` → belge commit'lerinde yalnızca belge dosyaları var; kod/test/derleme dosyası yok.

### Karşılanan kimlikler

BR-00, BR-09, FR-KAZ-01, FR-KAZ-02, FR-KAZ-04, FR-KAZ-05, DATA-04, KS-KAZ-01, KS-KAZ-02, KS-KAZ-03, KS-KAZ-07, KS-KAZ-08, KS-ONB-04; KT-35, KT-36, KT-81; M-24, M-25, M-31, K-12, K-14, K-16.

BR-09 tip düzeyinde sağlanıyor: `Prayer` yalnızca farzları ve vitiri içeriyor; sünnet, cuma ya da bayram borç kalemi yok.

### Varsayımlar

- `KazaLedger` ve `KazaPerformed` ktlint'in `class-signature` kuralı nedeniyle tek satırda yazıldı; imzalar karttakiyle aynı.
- `LedgerFixture` kimlikleri `w<n>` biçiminde üretiyor; `n`, kayıt sayısından başlayıp çakışma varsa artırılıyor. Böylece `e1`, `p1` gibi elle verilmiş kimliklerle çakışmıyor.
- A4 parametreli testlerinde her `argumentSet` tam bir `Executable` taşıyor; reddedilen ve kabul edilen durumlar aynı test metodunda çalışıyor.
- `decideSetDebt`, "o kalemde hiç kayıt yok" kontrolünü `entries` ve `performed` üzerinden yapıyor; ilk kayıt `ELLE`, sonraki farklar `DUZELTME`.
- A5 test-only olduğu için ilk çalıştırma yeşil; kod zaten A1–A4'te tamamlanmıştı.
- `KT35_`, `KT36_`, `KT81_` dışında `KT` ile başlayan test adı/`argumentSet` yok.

### Belge tutarsızlıkları

Kart, SRS DATA-04 ve KT-35/36/81 ile çelişki bulunmadı. A1–A5 tabloları DATA-04'ün tür, adet, kalan borç, geri alma ve elle düzenleme kurallarıyla uyumlu.

### Açık sorular

Yok.

### Commit'ler

1. `docs: add F2-03 card and update planning docs`
2. `docs(tasks): add F2-03 plan`
3. `feat(domain): add kaza record types (DATA-04)`
4. `feat(domain): add kaza ledger and balance (DATA-04)`
5. `feat(domain): add perform kaza decision (FR-KAZ-02, KT-35, KT-36)`
6. `feat(domain): add undo and set debt decisions (FR-KAZ-01, FR-KAZ-05, KT-81)`
7. `test(domain): add balance invariant test (FR-KAZ-04, K-12)`
8. `build: remove F2-03 rows from pending KT list (K-16)`
9. `docs(tasks): add F2-03 report`

Push edilmedi, PR açılmadı.

PR başlığı: feat(domain): add kaza ledger with perform, undo and set debt decisions (F2-03)

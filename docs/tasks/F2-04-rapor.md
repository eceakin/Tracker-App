## Rapor: F2-04 — Günlük işaretleme ve kaçırıldı önerileri

Dal: `feat/f2-04-daily-tracking`

### Yapılanlar

- **A1 — Kayıt ve ayar tipleri:** `Gender`, `DaySettings`, `PrayerStatus`, `PrayerMark`, `DayRecord`. `DayRecord` değişmezleri: kalem başına tek işaret, vitir varsa yatsı da var, cuma yalnızca cuma gününün öğlesinde, cuma ve kaza birlikte olamaz.
- **A2 — Türetilen durum:** `statusOf`; işaretli → `KILINDI`, işaretsiz ve kaçırıldı anı geçtiyse → `KILINMADI`, aksi hâlde `BELIRSIZ`. `MAZERETLI` bu kartta üretilmiyor (F2-07).
- **A3 — İşaretleme ve geri alma:** `RecordWrite`, `MarkDecision`, `UnmarkDecision`, `MarkBlock`, `UnmarkBlock`, `OgleOption`, `ogleOptionOf`; `decideMark` ve `decideUnmark`. `DayRecordFixture` bu adımda oluşturuldu.
- **A4 — Kaçırıldı önerisi ve otomatik ekleme:** `MissedOutcome`, `confirmSuggestion`, `KazaLedger.hasOpenEntryFor`, `evaluateMissed`. Motor defteri ve kaydı değiştirmiyor; yalnızca öneri döndürüyor.
- **D1:** `FR_ONB_05_kurulumSiniri` eklendi; `evaluateMissed` kuralı `missedAt <= trackingStart` eşitliğini kapsıyor.
- **D2:** `M24_yanlisGununVakitleriReddedilir` parametreli test oldu; `statusOf` satırı A2'de, `decideMark` A3'te, `evaluateMissed` A4'te eklendi.
- **B:** `ktPending`'den yalnızca 16 satır silindi; `KT-12` ve `KT-13` `// F2-04b` grubunda kaldı.

### Değişen dosyalar

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/settings/Gender.kt` (yeni)
- `.../settings/DaySettings.kt` (yeni)
- `.../tracking/PrayerStatus.kt` (yeni)
- `.../tracking/PrayerMark.kt` (yeni)
- `.../tracking/DayRecord.kt` (yeni)
- `.../tracking/RecordWrite.kt` (yeni)
- `.../tracking/MarkDecision.kt` (yeni)
- `.../tracking/OgleOption.kt` (yeni)
- `.../tracking/MissedOutcome.kt` (yeni)
- `.../kaza/KazaLedger.kt` (yalnızca `hasOpenEntryFor`)
- `core/domain/src/test/kotlin/.../tracking/DayRecordTest.kt` (yeni)
- `.../tracking/PrayerStatusTest.kt` (yeni)
- `.../tracking/MarkPrayerTest.kt` (yeni)
- `.../tracking/MissedSuggestionTest.kt` (yeni)
- `.../tracking/testing/DayRecordFixture.kt` (yeni)
- `build.gradle.kts`
- `docs/tasks/F2-04-plan.md` (yeni), `docs/tasks/F2-04-rapor.md` (yeni)
- İlk commit'te: `CLAUDE.md`, `docs/gereksinim-analizi-srs.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-04-gunluk-isaretleme.md`

`.github/workflows/ci.yml` değişmedi. F1-05 dosyaları commit edilmedi.

### Testler

`.\gradlew.bat :core:domain:test` → **203 test, 0 hata**. Dosya dosya:

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
| `KazaRecordTest` (F2-03) | 10 |
| `KazaLedgerTest` (F2-03) | 9 |
| `PerformKazaTest` (F2-03) | 3 |
| `UndoAndSetDebtTest` (F2-03) | 12 |
| `LedgerInvariantTest` (F2-03) | 5 |
| `DayRecordTest` | 7 |
| `PrayerStatusTest` | 9 |
| `MarkPrayerTest` | 20 |
| `MissedSuggestionTest` | 18 |
| **Toplam** | **203** |

Her A adımında test önce yazıldı ve kırmızı/derlenmeyen görüldü:
- A1: `Unresolved reference 'DayRecord'` / `PrayerMark`.
- A2: `Unresolved reference 'statusOf'`.
- A3: `Unresolved reference 'RecordWrite'` / `MarkDecision` / `OgleOption` / `decideMark`.
- A4: `Unresolved reference 'MissedOutcome'` / `evaluateMissed` / `confirmSuggestion` / `hasOpenEntryFor`.
- A5 test içermiyor; `DayRecordFixture` A3'te oluşturuldu ve A3 commit'ine girdi.

### Kapsam (JaCoCo)

| Sayaç | Kapsanan | Kaçan | Oran |
| --- | --- | --- | --- |
| Satır (LINE) | 462 | 1 | **%99,8** |
| Dal (BRANCH) | 283 | 8 | **%97,3** |
| Talimat (INSTRUCTION) | 3072 | 51 | %98,4 |
| Yöntem (METHOD) | 155 | 11 | %93,4 |
| Sınıf (CLASS) | 52 | 0 | %100 |

Kapsam kapısı yeşil; hiçbir sınıf/paket ölçümden hariç tutulmadı.

### Kapsanmayan satır ve dallar (CLAUDE.md 1.9)

| Dosya:satır | Eksik | Neden |
| --- | --- | --- |
| `.../kaza/KazaLedger.kt:113` | 1 satır, 6 talimat | `decideUndo` içinde geri alınmış bir `KazaPerformed` kaydı için `ALREADY_CORRECTED` dalı. Mevcut testler ikinci geri almayı bir `KazaEntry` üzerinden sınıyor; performed kaydın ikinci kez geri alınması kartın test listesinde yok. |
| `.../kaza/KazaLedger.kt:39` | 1 dal | `targetEntry?.prayer ?: targetPerformed?.prayer` Elvis ifadesinin performed hedef dalı; düzeltmenin bir `KazaPerformed` kaydına işaret ettiği durum test edilmiyor. |
| `.../kaza/KazaLedger.kt:62` | 1 dal | `isCorrected` predicate'inin kısa devre dalı; düzeltme olmayan kayıt tarafı tam kapsanmıyor. |
| `.../kaza/KazaLedger.kt:112` | 1 dal | `decideUndo` içinde performed kaydın `isCorrected` doğru dalı; 113. satırdaki senaryoyla aynı. |
| `.../kaza/KazaLedger.kt:167` | 3 dal | `hasRecords = entries.any(...) \|\| performed.any(...)` ifadesinin yalnızca `KazaPerformed` bulunan defter dalı; `decideSetDebt` testleri bu kombinasyonu kapsamıyor. |
| `.../kaza/KazaLedger.kt:192` | 1 dal | `hasOpenEntryFor` içindeki `!isCorrected(entry.id)` dalı; açık/kapalı kayıt kombinasyonunun bir tarafı kapsanmıyor. |
| `.../tracking/DayRecord.kt:126` | 1 dal | `prayer == Prayer.YATSI && markOf(Prayer.VITIR) != null` bileşik koşulunun bir kısa devre dalı; yatsı dışındaki kalemlerde vitir kontrolüne girilmeyen dal. |
| `PerformDecision.kt:19`, `UndoDecision.kt:13`, `SetDebtDecision.kt:16`, `KazaRecord.kt:50`, `KazaRecord.kt:69`, `PrayerMark.kt:16`, `RecordWrite.kt:23`, `MissedOutcome.kt:20`, `MarkDecision.kt:13`, `MarkDecision.kt:33`, `PrayerDayWindows.kt:17` | Üretilen yöntemler | Kotlin'in `data class` için ürettiği `componentN`/`copy` yöntemleri; doğrudan çağrılmadıkları için JaCoCo talimat sayacında kaçıyor. Satır kapsamı bu satırlarda tamamlanmış görünüyor; asıl satır kaçağı yalnızca `KazaLedger.kt:113`. |

### KT kapısı

```
KT izlenebilirlik: 101 geçerli satır, 19 testli, 82 bekliyor, fark yok.
```

`ktPending`'den yalnızca 16 satır silindi; `KT-12` ve `KT-13` `// F2-04b` grubunda.

### Son doğrulama

- `.\gradlew.bat clean` → başarılı.
- `.\gradlew.bat ciCheck` → **başarılı (3 dk 33 sn)**.
- `.\gradlew.bat :core:domain:jacocoTestReport` → başarılı.
- `.\gradlew.bat checkKtTraceability` → yukarıdaki 19 testli / 82 bekliyor satırı.
- `.\gradlew.bat help` → başarılı (M-08 bozulmadı).
- Lint kapısı: `Lint kapısı: bilinen 6 uyarı eşleşti, fark yok.`
- `git status` → yalnızca `docs/tasks/F1-05-plan.md` ve `docs/tasks/F1-05-risk-prototipi.md` izlenmeyen.
- Yasak çağrı araması (yorumlar hariç) → **çıktı yok**.
- `git log --stat --format="%h %s" main..HEAD -- docs CLAUDE.md` → belge commit'lerinde yalnızca belge dosyaları var; kod/test/derleme dosyası yok.

### Karşılanan kimlikler

BR-00, BR-02, BR-03, BR-04, BR-08, BR-09, FR-TRK-01, FR-TRK-02, FR-TRK-03, FR-TRK-06, FR-TRK-07, FR-KAZ-03, FR-ONB-05, DATA-05; KT-01 – KT-11, KT-15 – KT-18, KT-34; KS-TRK-01, KS-TRK-03, KS-TRK-04, KS-TRK-08, KS-TRK-09, KS-TRK-10, KS-TRK-11, KS-TRK-13; KS-KAZ-04, KS-KAZ-05, KS-KAZ-06; KS-ONB-07; M-24, M-25, K-03, K-11, K-14, K-16.

KT-12 ve KT-13 bu kartta yok; `NeedsLateChoice` yalnızca seçim gerektiğini bildiriyor. F2-04b'nin işi.

### Varsayımlar

- D1'deki eşitlik kuralı (`missedAt <= trackingStart`) `evaluateMissed` içinde korunuyor; `FR_ONB_05_kurulumSiniri` iki tarafı da sınıyor.
- D2'deki üç `M24_yanlisGununVakitleriReddedilir` satırı tek parametreli testte toplandı; satırlar A2, A3 ve A4 commit'lerinde büyüdü.
- `PrayerStatus.MAZERETLI` tanımlı ama bu kartta üretilmiyor; mazeret kartları (F2-07) üretecek.
- `KT18` testi A3'te geri alma ve `BELIRSIZ` durumunu, A4'te `evaluateMissed` → `Suggest` adımını sınıyor.
- `DayRecordFixture` her yazmada yeni `DayRecord` kuruyor; `markedAtStart` işaretleri vaktin başlangıcına koyuyor.
- `KazaLedger`'a yalnızca `hasOpenEntryFor` eklendi; başka F2-03 davranışı değişmedi.
- Cinsiyet `decideMark`'ı etkilemiyor; fark yalnızca `ogleOptionOf` sunumunda.

### Belge tutarsızlıkları

Kart, SRS DATA-05, BR-02/03/04/08/09 ve KT-01–11/15–18/34 ile çelişki bulunmadı. DATA-05'in `MAZERETLI` önceliği bu kartta bilinçli olarak uygulanmıyor; mazeret kartlarına bırakıldı.

### Açık sorular

Yok.

### Commit'ler

1. `docs: add F2-04 card and update planning docs`
2. `docs(tasks): add F2-04 plan`
3. `feat(domain): add day record and settings types (DATA-05)`
4. `feat(domain): derive prayer status (DATA-05)`
5. `feat(domain): add mark and unmark decisions (BR-02, BR-03, BR-08, KT-01 to KT-06)`
6. `feat(domain): add missed prayer suggestions and auto add (FR-KAZ-03, KT-07 to KT-11)`
7. `build: remove F2-04 rows from pending KT list (K-16)`
8. `docs(tasks): add F2-04 report`

Push edilmedi, PR açılmadı.

PR başlığı: feat(domain): add daily marking, derived status and missed suggestions (F2-04)

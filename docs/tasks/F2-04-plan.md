# F2-04 Uygulama Planı — Günlük işaretleme ve kaçırıldı önerileri

Dal: `feat/f2-04-daily-tracking` · 2 Kasım 2026 · Onay: Adım 0 planı onaylandı; D1 – D2 düzeltmeleri işlendi.

Bu plan, `docs/tasks/F2-04-gunluk-isaretleme.md` kartının Adım 0 çıktısıdır. Kartın sırası ve kapsamı esastır; D1 – D2 yalnızca uygulama ayrıntısını netleştirir.

## 1. Git başlangıcı

- `main` = `origin/main` = `a61651cd22698725f048a0f572c8f70da346b870`; `HEAD...origin/main` → `0 0`.
- `a61651c` `HEAD`'in atasıdır; `git fetch origin` yeni bir şey getirmedi; `git stash list` boş.
- Yerel dallar: `main`, `chore/f1-02-modules-hygiene`, `feat/f1-01-skeleton-domain`.
- Çalışma ağacı grupları:
  - Bu kartın dört dosyası: `CLAUDE.md`, `docs/gereksinim-analizi-srs.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-04-gunluk-isaretleme.md`.
  - F1-05'in iki izlenmeyen dosyası: `docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md` — dokunulmaz, commit edilmez.
  - Beklenmeyen değişiklik yok.

İlk commit yalnızca dört belgedir; dosyalar adlarıyla eklenir:

```
git add CLAUDE.md docs/gereksinim-analizi-srs.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-04-gunluk-isaretleme.md
git commit -m "docs: add F2-04 card and update planning docs"
```

İkinci commit bu plan dosyasıdır: `docs(tasks): add F2-04 plan`.

## 2. Dosya ağacı ve imzalar

Karttaki imzalardan **sapma yok**. `Prayer`, `RuleSet` ve `domain.times` değişmiyor. `KazaLedger`'a yalnızca `hasOpenEntryFor` ekleniyor.

**`domain.settings`**

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/settings/Gender.kt`
  - `enum class Gender { MALE, FEMALE }`
- `.../settings/DaySettings.kt`
  - `data class DaySettings(val autoAddDebt: Boolean)`

**`domain.tracking`**

- `.../tracking/PrayerStatus.kt`
  - `enum class PrayerStatus { KILINDI, KILINMADI, MAZERETLI, BELIRSIZ }`
- `.../tracking/PrayerMark.kt`
  - `data class PrayerMark(prayer, markedAt, asCuma, asKaza)`
- `.../tracking/DayRecord.kt`
  - `data class DayRecord(val date: LocalDate, val marks: List<PrayerMark>)`
  - `fun markOf(prayer: Prayer): PrayerMark?`
  - `companion object { fun empty(date: LocalDate): DayRecord }`
  - `init` değişmezleri: kalem başına en fazla bir işaret; `VITIR` varsa `YATSI` da var; `asCuma` yalnızca cuma gününün `OGLE` kaleminde; `asCuma` ve `asKaza` birlikte olamaz.
  - `fun statusOf(prayer, windows, clock): PrayerStatus`
  - `fun decideMark(prayer, asCuma, windows, clock): MarkDecision`
  - `fun decideUnmark(prayer): UnmarkDecision`
  - `fun evaluateMissed(windows, ledger, settings, trackingStart, clock): List<MissedOutcome>`
- `.../tracking/RecordWrite.kt`
  - `sealed interface RecordWrite`; `Mark(date, prayer, markedAt, asCuma, asKaza)` ve `Clear(date, prayer)`.
- `.../tracking/MarkDecision.kt`
  - `sealed interface MarkDecision`; `Allowed(write: RecordWrite.Mark)`, `Blocked(reason)`, `data object NeedsLateChoice`.
  - `enum class MarkBlock { ALREADY_MARKED, NOT_YET_IN_TIME, YATSI_FIRST, CUMA_NOT_ALLOWED }`
  - `sealed interface UnmarkDecision`; `Allowed(write: RecordWrite.Clear)`, `Blocked(reason)`.
  - `enum class UnmarkBlock { NOT_MARKED, UNMARK_VITIR_FIRST, KAZA_MARK_NEEDS_GROUP_UNDO }`
- `.../tracking/OgleOption.kt`
  - `enum class OgleOption { OGLE_ONLY, OGLE_OR_CUMA, OGLE_WITH_OPTIONAL_CUMA }`
  - Üst düzey `fun ogleOptionOf(date: LocalDate, gender: Gender): OgleOption`
- `.../tracking/MissedOutcome.kt`
  - `sealed interface MissedOutcome`; `Suggest(date, prayer)`, `AutoAdd(write: KazaWrite.AddEntry)`.
  - Üst düzey `fun confirmSuggestion(suggestion: MissedOutcome.Suggest, clock: Clock): KazaWrite.AddEntry`

**`domain.kaza`**

- `.../kaza/KazaLedger.kt` (yalnızca ekleme)
  - `fun hasOpenEntryFor(date: LocalDate, prayer: Prayer): Boolean`

**Test dosyaları**

- `.../tracking/DayRecordTest.kt`
- `.../tracking/PrayerStatusTest.kt`
- `.../tracking/MarkPrayerTest.kt`
- `.../tracking/MissedSuggestionTest.kt`
- `.../tracking/testing/DayRecordFixture.kt`

### Kural sıraları (karttaki tablolarla aynı)

- **`decideMark`:** Önce kalem zaten işaretli mi (`ALREADY_MARKED`); sonra vaktin başlangıcı gelmedi mi (`NOT_YET_IN_TIME`); sonra vakit çıktı mı (`NeedsLateChoice`); sonra `VITIR` için yatsı işareti yok mu (`YATSI_FIRST`); sonra `asCuma` geçersiz mi (`CUMA_NOT_ALLOWED`); hepsi geçerse `Allowed(Mark(...))`. **Vakti çıkmış kuralı, "önce yatsı" kuralından önce gelir** (kart A3 tablosuyla birebir).
- **`decideUnmark`:** Önce kalem işaretsiz mi (`NOT_MARKED`); sonra `YATSI` geri alınırken `VITIR` işaretli mi (`UNMARK_VITIR_FIRST`); sonra işaret `asKaza` mı (`KAZA_MARK_NEEDS_GROUP_UNDO`); hepsi geçerse `Allowed(Clear(...))`. **Vitir kontrolü, kaza işareti kontrolünden önce gelir** (kart A3 tablosuyla birebir).
- **`evaluateMissed`:** Her kalem için sırayla: kalem işaretliyse sonuç yok; vakit çıkmadıysa sonuç yok; "kaçırıldı" anı **takip başlangıcından önce ya da ona eşitse** sonuç yok; defterde açık `ONERI`/`OTOMATIK` kaydı varsa sonuç yok; otomatik ekleme açıksa `AutoAdd`; aksi hâlde `Suggest`. **Kurulum başlangıcı kuralı, otomatik ekleme kuralından önce gelir** (kart A4 tablosuyla birebir). `<=` eşitliği D1 gereği korunur. Liste `Prayer.entries` sırasındadır.

## 3. Test listesi

Tam olarak **16 KT kimliği** kullanılıyor: `KT01`–`KT11`, `KT15`–`KT18`, `KT34`. **`KT12` ve `KT13` hiçbir test adında ya da `argumentSet` adında geçmiyor.** D1 – D2 sonrası beklenen: **54 yeni çalıştırma**, F2-03 sonrası 149 ile toplam **203**.

### A1 — `DayRecordTest` (7)

| Test | Sınadığı |
| --- | --- |
| `DATA05_gunlukKayitDegismezleri` | Dört değişmez; 7 argumentSet |

ArgumentSet satırları:
- `"DATA-05 ayni kaleme iki isaret"`
- `"DATA-05 yatsisiz vitir"`
- `"DATA-05 cuma olmayan gunde cuma"`
- `"DATA-05 ogle disinda cuma"`
- `"DATA-05 cuma ve kaza birlikte"`
- `"DATA-05 G5 ogle cuma kabul"`
- `"DATA-05 yatsi ve vitir kabul"`

### A2 — `PrayerStatusTest` (7)

| Test | Sınadığı |
| --- | --- |
| `DATA05_durumTuretilir` | Öğle ve vitir için türetilen durum; 6 argumentSet |
| `M24_yanlisGununVakitleriReddedilir` | G1 kaydı + G2 vakitleri → `IllegalArgumentException`; bu adımda 1 argumentSet |

`DATA05_durumTuretilir` argumentSet satırları:
- `"DATA-05 ogle bos 15:29 belirsiz"`
- `"DATA-05 ogle bos 15:30 kilinmadi"`
- `"DATA-05 ogle 12:40 isaretli 16:00 kilindi"`
- `"DATA-05 ogle kaza isaretli 16:00 kilindi"`
- `"DATA-05 vitir bos G2 05:29 belirsiz"`
- `"DATA-05 vitir bos G2 05:30 kilinmadi"`

`M24_yanlisGununVakitleriReddedilir` D2 gereği parametreli testtir; üç argumentSet'in tamamı şunlardır:
- `"M-24 statusOf yanlis gun"` — **A2 commit'ine girer**
- `"M-24 decideMark yanlis gun"` — **A3 commit'inde eklenir**
- `"M-24 evaluateMissed yanlis gun"` — **A4 commit'inde eklenir**

Her satır `DayRecord.empty(G1)` ve `PrayerDayWindows(times(2), times(3))` ile `IllegalArgumentException` bekler.

### A3 — `MarkPrayerTest` (21)

| Test | Sınadığı |
| --- | --- |
| `KT01_BR02_ogleVaktiGirmeden1229daIsaretlenemez` | 12:29 → `NOT_YET_IN_TIME` |
| `KT02_BR02_ogle1230daIsaretlenir` | 12:30 → `Allowed(Mark(...))`; uygulanınca `KILINDI` |
| `KT03_BR02_yatsi1000daIsaretlenemez` | 10:00, yatsı → `NOT_YET_IN_TIME` |
| `KT04_BR03_yatsiBosykenVitirIsaretlenemez` | 20:00, vitir → `YATSI_FIRST` |
| `KT05_BR03_yatsiKilindiysaVitirIsaretlenir` | Yatsı işaretli; 20:00 vitir → `Allowed` |
| `KT06_BR03_ertesiGun0529VitirG1KaydinaYazilir` | G2 05:29 → `Mark.date = G1` |
| `KT15_BR08_erkekCumaGunuCumaSecer_ogleKilinmisSayilir` | `OGLE_OR_CUMA`; cuma işareti → `KILINDI` |
| `KT17_BR08_kadindaCumaIstegeBagli` | `OGLE_WITH_OPTIONAL_CUMA`; cuma işareti `Allowed` |
| `KT18_FR_TRK_03_isaretGeriAlinir_vaktiCikincaOneri` | Geri alma → `BELIRSIZ`; 15:30'da `Suggest` (son adım A4'te eklenir) |
| `FR_TRK_kararSiralari` | 6 sınır durumu |
| `FR_TRK_03_geriAlmaKurallari` | 4 geri alma durumu |
| `KS_TRK_01_isaretlenmeSaatiYazilir` | `markedAt == G1 13:00`; diğer beş kalem değişmez |

`FR_TRK_kararSiralari` argumentSet satırları:
- `"FR-TRK-03 isaretli kaleme isaret"`
- `"FR-TRK-04 vakti cikmis kalem secim ister"`
- `"FR-TRK-04 vakti cikmis vitir de secim ister"`
- `"BR-08 cuma olmayan gunde cuma"`
- `"BR-08 ikindide cuma"`
- `"FR-TRK-06 cuma olmayan gun ogle secenegi"`

`FR_TRK_03_geriAlmaKurallari` argumentSet satırları:
- `"FR-TRK-03 isaretsiz kalem"`
- `"FR-TRK-03 vitir isaretliyken yatsi"`
- `"FR-TRK-03 kaza isareti"`
- `"FR-TRK-03 vitir geri alinir"`

A3'te `M24_yanlisGununVakitleriReddedilir` testine `"M-24 decideMark yanlis gun"` satırı eklenir. `KT18`'in son adımı (`evaluateMissed` → `Suggest`) A4'te eklenir.

### A4 — `MissedSuggestionTest` (19)

| Test | Sınadığı |
| --- | --- |
| `KT07_BR04_ogle1529daHenuzKacirilmadi` | Sonuç listesi boş |
| `KT08_BR04_ogle1530daKilinmadi_oneriGelir` | `KILINMADI`; `[Suggest(G1, OGLE)]` |
| `KT09_BR00_otomatikEklemeAcikken_oneriYerineOtomatikKayit` | `[AutoAdd(...)]`; uygulanınca `OTOMATIK`, bakiye 1; yeniden değerlendirme boş |
| `KT10_BR04_sabah0700deKilinmadi_oneriGelir` | `[Suggest(G1, SABAH)]` |
| `KT11_BR04_yatsiVeVitirIcinIkiAyriOneri` | G2 05:30 → `[Suggest(G1, YATSI), Suggest(G1, VITIR)]` |
| `KT16_BR08_cumaKacirilinca_oneriOgleIcin` | G5 15:30 → `[Suggest(G5, OGLE)]` |
| `KT34_BR09_bayramNamaziBorcaYazilmaz` | Altı kalem işaretli; sonuç boş |
| `KS_ONB_07_kurulumOncesiVakitIcinOneriYok` | Yalnız `[Suggest(G1, IKINDI)]` |
| `KS_TRK_13_kurulumdanOncekiGunIcinOneriYok` | G3 kaydı, G5 10:00 → boş |
| `FR_ONB_05_kurulumSiniri` | Kurulum sınırında eşitlik; 2 argumentSet (D1) |
| `DATA05_borcaYazilmisKalemIcinOneriYok` | Beş defter durumu; 5 argumentSet |
| `KS_KAZ_04_borcaEkleOneriKaydiYazar` | `ONERI` kaydı; bakiye 1; öneri kapanır |
| `KS_KAZ_05_otomatikKapaliykenSessizBorcYok` | G1'in altı + G2 sabahı; defter `EMPTY` kalır |

`FR_ONB_05_kurulumSiniri` argumentSet satırları:
- `"FR-ONB-05 kurulum ogle bitisine esit"`: `trackingStart = G1 15:30`, yalnız öğle boş (diğerleri `markedAtStart` ile işaretli), saat G1 15:30 → sonuç listesi boş.
- `"FR-ONB-05 kurulum bir dakika once"`: `trackingStart = G1 15:29`, aynı kayıt, saat G1 15:30 → `[Suggest(G1, OGLE)]`.

`DATA05_borcaYazilmisKalemIcinOneriYok` argumentSet satırları:
- `"DATA-05 oneri kaydi var"`
- `"DATA-05 otomatik kaydi var"`
- `"DATA-05 geri alinmis oneri"`
- `"DATA-05 baska gunun kaydi"`
- `"DATA-05 elle kayit sayilmaz"`

A4'te `M24_yanlisGununVakitleriReddedilir` testine `"M-24 evaluateMissed yanlis gun"` satırı ve `KT18`'in `evaluateMissed` son adımı eklenir.

### A5 — `DayRecordFixture`

A5 test içermiyor; A3'te oluşturulup A3 commit'ine giren test yardımcısıdır. Ayrıntısı 4. maddede.

## 4. `DayRecordFixture` imzası

Test kaynaklarında, `com.eceakin.namaztakibi.domain.tracking.testing` paketinde; ürün koduna girmez:

```kotlin
object DayRecordFixture {
    fun apply(record: DayRecord, writes: List<RecordWrite>): DayRecord
    fun markedAtStart(date: LocalDate, windows: PrayerDayWindows, vararg prayers: Prayer): DayRecord
}
```

- `Mark`, kaydın `date`'iyle aynı tarihte olmalı; değilse `IllegalArgumentException` (test hatası).
- `Clear`, kalemin işaretini kaldırır.
- `markedAtStart`, verilen kalemleri vaktin başlangıcında işaretler; `VITIR` verilirse `YATSI` da verilmelidir, aksi hâlde `DayRecord` değişmezi kırılır.
- Her yazmadan sonra yeni bir `DayRecord` kurulur; değişmezler her adımda denetlenir.

## 5. `ktPending` değişikliği

Kök `build.gradle.kts`'te `// F2-04` grubundan **16 satır** silinecek: `KT-01` – `KT-11`, `KT-15` – `KT-18`, `KT-34`. Grupta kalan `KT-12` ve `KT-13`'ün üstündeki yorum `// F2-04b` yapılacak. Başka hiçbir satıra dokunulmayacak.

Sonuç:

```
KT izlenebilirlik: 101 geçerli satır, 19 testli, 82 bekliyor, fark yok.
```

## 6. Commit sırası

Dal: güncel `main`'den `feat/f2-04-daily-tracking`. İlk commit yalnızca kartın dört belgesi; `git add` dosya adlarıyla yapılacak, F1-05 dosyaları asla eklenmeyecek. CLAUDE.md 1.9 gereği `docs:` ve `docs(tasks):` commit'lerine kod/test/derleme dosyası girmeyecek.

1. `docs: add F2-04 card and update planning docs`
2. `docs(tasks): add F2-04 plan`
3. `feat(domain): add day record and settings types (DATA-05)`
4. `feat(domain): derive prayer status (DATA-05)`
5. `feat(domain): add mark and unmark decisions (BR-02, BR-03, BR-08, KT-01 to KT-06)`
6. `feat(domain): add missed prayer suggestions and auto add (FR-KAZ-03, KT-07 to KT-11)`
7. `build: remove F2-04 rows from pending KT list (K-16)`
8. `docs(tasks): add F2-04 report`

`DayRecordFixture` A3'te oluşturulup 5. commit'e girer. Her A adımında önce test yazılıp `:core:domain:test` ile kırmızı/derlenmeyen görülecek, sonra kod yazılıp `:core:domain:test` + `ktlintCheck` yeşil olacak. Bölüm B'den sonra `ciCheck` çalıştırılacak.

**Son doğrulama (Bölüm D):**

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

Yasak çağrılar (yorum satırları hariç):

```
grep -rnE "android\.|Instant\.now|LocalDate\.now|LocalDateTime\.now|ZoneId\.systemDefault|System\.currentTimeMillis|!!|clock\.zone" core/domain/src/main | grep -vE "^[^:]+:[0-9]+:[[:space:]]*(\*|//)"
```

Beklenen: çıktı yok. `ciCheck` yeşil; `:core:domain` 203 test; satır kapsamı ≥ %90; KT kapısı 19 testli / 82 bekliyor; lint kapısı bilinen 6 uyarı. Raporun son satırı tam olarak:

```
PR başlığı: feat(domain): add daily marking, derived status and missed suggestions (F2-04)
```

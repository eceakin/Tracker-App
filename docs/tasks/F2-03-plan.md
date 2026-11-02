# F2-03 Uygulama Planı — Kaza defteri

Dal: `feat/f2-03-kaza-ledger` · 2 Kasım 2026 · Onay: Adım 0 planı düzeltmesiz onaylandı.

Bu plan, `docs/tasks/F2-03-kaza-defteri.md` kartının Adım 0 çıktısıdır. Kartın sırası ve kapsamı esastır; planda karttan sapma yoktur.

## 1. Git başlangıcı

- `main` = `origin/main` = `c7638ace718882efd6e262efcddc2e0fcadb1c62`; `HEAD...origin/main` → `0 0`.
- `c7638ac` `HEAD`'in atasıdır; `git fetch origin` yeni bir şey getirmedi; `git stash list` boş.
- Yerel dallar: `main`, `chore/f1-02-modules-hygiene`, `feat/f1-01-skeleton-domain`. Yerel `feat/f2-01-domain-foundation` yok; `origin/feat/f2-02-prayer-time-windows` uzakta duruyor; silme yapılmadı.
- Çalışma ağacı grupları:
  - Bu kartın dört dosyası: `CLAUDE.md`, `docs/gereksinim-analizi-srs.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-03-kaza-defteri.md`.
  - F1-05'in iki izlenmeyen dosyası: `docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md` — dokunulmaz, commit edilmez.
  - Beklenmeyen değişiklik yok.

İlk commit yalnızca dört belgedir; dosyalar adlarıyla eklenir:

```
git add CLAUDE.md docs/gereksinim-analizi-srs.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-03-kaza-defteri.md
git commit -m "docs: add F2-03 card and update planning docs"
```

İkinci commit bu plan dosyasıdır: `docs(tasks): add F2-03 plan`.

## 2. Dosya ağacı ve imzalar

Karttaki imzalardan **sapma yok**. Bütün yeni tipler `com.eceakin.namaztakibi.domain.kaza` paketinde. `Prayer` enum'u değişmiyor.

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/kaza/KazaRecord.kt`
  - `@JvmInline value class KazaRecordId(val value: String)` — `init`: `value.isNotBlank()`.
  - `enum class KazaEntryKind { ELLE, ONERI, OTOMATIK, HESAPLAYICI, DUZELTME }`.
  - `data class KazaEntry(id, prayer, sourceDate, kind, count, corrects, missedInSefer, createdAt)` — karttaki alanlar ve tipler aynen.
  - `KazaEntry.init`: `kind == DUZELTME` ise `count != 0`; değilse `count > 0` ve `corrects == null`.
  - `data class KazaPerformed(id, prayer, performedAt)`.
- `.../kaza/KazaLedger.kt`
  - `data class KazaLedger(val entries: List<KazaEntry>, val performed: List<KazaPerformed>)`
  - `init`: altı değişmez (benzersiz kimlik; `corrects` var olan kayda işaret eder; hedef `DUZELTME` değil; hedefe en fazla bir düzeltme; düzeltme hedefle aynı kalemde; her kalemin bakiyesi ≥ 0).
  - `fun balance(prayer: Prayer): Int`
  - `fun isCorrected(id: KazaRecordId): Boolean`
  - `fun decidePerform(prayer: Prayer?, clock: Clock): PerformDecision`
  - `fun decideUndo(id: KazaRecordId, clock: Clock): UndoDecision`
  - `fun decideSetDebt(prayer: Prayer, target: Int, clock: Clock): SetDebtDecision`
  - `companion object { val EMPTY: KazaLedger }`
- `.../kaza/KazaWrite.kt`
  - `sealed interface KazaWrite`; `AddEntry` ve `AddPerformed` karttaki alanlarla.
- `.../kaza/PerformDecision.kt`
  - `sealed interface PerformDecision`; `Allowed`, `NeedsConfirmation`, `Blocked`.
  - `enum class PerformQuestion { NO_DEBT_ADD_FIRST }`
  - `enum class PerformBlock { NO_PRAYER_SELECTED }`
- `.../kaza/UndoDecision.kt`
  - `sealed interface UndoDecision`; `Allowed(write: KazaWrite.AddEntry)`, `Blocked(reason: UndoBlock)`.
  - `enum class UndoBlock { NOT_FOUND, ALREADY_CORRECTED, CORRECTION_NOT_UNDOABLE, WOULD_GO_NEGATIVE }`
- `.../kaza/SetDebtDecision.kt`
  - `sealed interface SetDebtDecision`; `NoChange`, `Allowed(write: KazaWrite.AddEntry)`, `Blocked(reason: SetDebtBlock)`.
  - `enum class SetDebtBlock { NEGATIVE_TARGET }`

Test dosyaları:

- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/kaza/KazaRecordTest.kt`
- `.../kaza/KazaLedgerTest.kt`
- `.../kaza/PerformKazaTest.kt`
- `.../kaza/UndoAndSetDebtTest.kt`
- `.../kaza/LedgerInvariantTest.kt`
- `.../kaza/testing/LedgerFixture.kt`

Hiçbir üretim fonksiyonu defteri değiştirmez; yalnızca `KazaWrite` önerisi döndürür. `clock.instant()` dışında saat/bölge çağrısı yoktur.

## 3. Test listesi

Tam olarak **üç KT kimliği** test adında geçiyor: `KT35_`, `KT36_`, `KT81_`. Başka hiçbir test adı ya da `argumentSet` adı `KT` ile başlamıyor. Beklenen: **39 yeni çalıştırma**, F2-02 sonrası 110 ile toplam **149**.

### A1 — `KazaRecordTest` (10)

| Test | Sınadığı |
| --- | --- |
| `DATA04_kayitTurleriVeAdetKurallari` | Tür/adet/`corrects` kuralları; 9 argumentSet |
| `M24_bosKimlikReddedilir` | `KazaRecordId("")` ve `KazaRecordId("  ")` reddedilir |

`DATA04_kayitTurleriVeAdetKurallari` argumentSet satırları:
- `"DATA-04 ELLE adet 0 reddedilir"`
- `"DATA-04 ONERI adet 0 reddedilir"`
- `"DATA-04 OTOMATIK adet 0 reddedilir"`
- `"DATA-04 HESAPLAYICI adet 0 reddedilir"`
- `"DATA-04 ELLE negatif adet reddedilir"`
- `"DATA-04 ONERI corrects dolu reddedilir"`
- `"DATA-04 DUZELTME adet 0 reddedilir"`
- `"DATA-04 DUZELTME negatif adet corrects null kabul edilir"`
- `"DATA-04 DUZELTME pozitif adet corrects dolu kabul edilir"`

### A2 — `KazaLedgerTest` (9)

| Test | Sınadığı |
| --- | --- |
| `DATA04_kalanBorcKayitlardanHesaplanir` | Öğle +8 ve +2 düzeltme, iki kılınan → 8; ikindi 0 |
| `DATA04_bosDefterdeHerKalemSifir` | `KazaLedger.EMPTY` altı kalemde 0 |
| `DATA04_geriAlinanKayitTaninir` | `isCorrected` doğru/yanlış |
| `M24_tutarsizDefterReddedilir` | Altı değişmez; 6 argumentSet |

`M24_tutarsizDefterReddedilir` argumentSet satırları:
- `"M-24 yinelenen kimlik"`
- `"M-24 olmayan kayda duzeltme"`
- `"M-24 duzeltmenin duzeltmesi"`
- `"M-24 iki kez geri alma"`
- `"M-24 farkli kalemde duzeltme"`
- `"M-24 negatif bakiye"`

### A3 — `PerformKazaTest` (3)

| Test | Sınadığı |
| --- | --- |
| `KT35_FR_KAZ_04_borcSifirkenKazaKildim_onceEkleSonraDusSorulur` | Borç 0 → `NeedsConfirmation`; iki öneri; uygulanmazsa defter değişmez; uygulanırsa bakiye 0 ve −1'e düşmez |
| `KT36_FR_KAZ_02_vakitSecilmedenKazaKaydedilemez` | `null` → `Blocked(NO_PRAYER_SELECTED)`; defter değişmez |
| `KS_KAZ_02_kilinanKazaSecilenVakittenDuser` | İkindi 2 → `Allowed(AddPerformed)`; uygulanınca ikindi 1, öğle 3 |

Saat: `ReferenceDay.clockAt(1, "14:00")`. `LedgerFixture` bu adımda oluşturulup A3 commit'ine girer.

### A4 — `UndoAndSetDebtTest` (12)

| Test | Sınadığı |
| --- | --- |
| `KT81_FR_KAZ_05_onaylananOneriGeriAlinir_duzeltmeKaydiEklenir` | Düzeltme kaydı; kayıt silinmez; bakiye 0; `isCorrected` |
| `FR_KAZ_05_geriAlmaKurallari` | Beş geri alma sonucu; 5 argumentSet |
| `KS_KAZ_01_borcElleDuzenlenir_duzeltmeKaydiSaklanir` | 8 → 10; `DUZELTME +2` |
| `KS_ONB_04_baslangicBorcuKalemBasinaTekElleKaydi` | Altı kalem tek `ELLE`; toplam 49 |
| `FR_KAZ_01_elleDuzenlemeSinirlari` | Negatif hedef, aynı sayı, sıfıra indirme, boş kalemde sıfır; 4 argumentSet |

`FR_KAZ_05_geriAlmaKurallari` argumentSet satırları:
- `"FR-KAZ-05 olmayan kayit"`
- `"FR-KAZ-05 duzeltme geri alinmaz"`
- `"FR-KAZ-05 ikinci geri alma"`
- `"FR-KAZ-05 negatife dusuren geri alma"`
- `"FR-KAZ-05 kilinan kaza geri alinir"`

`FR_KAZ_01_elleDuzenlemeSinirlari` argumentSet satırları:
- `"FR-KAZ-01 negatif hedef"`
- `"FR-KAZ-01 ayni sayi"`
- `"FR-KAZ-01 sifira indirme"`
- `"FR-KAZ-01 bos kalemde sifir"`

### A5 — `LedgerInvariantTest` (5)

`FR_KAZ_04_borcAslaNegatifOlmaz` argumentSet satırları ve beklenen öğle bakiyeleri:

| argumentSet | Beklenen bakiye dizisi |
| --- | --- |
| `"FR-KAZ-04 sifirdan onayli kaza ve geri alma"` | 0, 1 |
| `"FR-KAZ-04 vazgecilen soru"` | 0, 0 |
| `"FR-KAZ-04 borc biterken"` | 2, 1, 0, 0 |
| `"FR-KAZ-04 negatife dusuren geri alma engellenir"` | 1, 0, 0 |
| `"FR-KAZ-04 sifira indirme ve negatif hedef"` | 5, 0, 0 |

Test sürücüsü komutları sırayla karara çevirir; yalnızca `Allowed` yazmalarını (onay adımında yanıta göre) uygular ve her adımdan sonra öğle bakiyesini kaydeder.

## 4. `LedgerFixture` imzası ve kimlik üretimi

Test kaynaklarında, `com.eceakin.namaztakibi.domain.kaza.testing` paketinde; ürün koduna girmez:

```kotlin
object LedgerFixture {
    fun apply(ledger: KazaLedger, writes: List<KazaWrite>): KazaLedger
}
```

- Her yazma için yeni bir `KazaLedger` kurulur; böylece `init` değişmezleri her adımda denetlenir.
- `AddEntry` → `KazaEntry`; `AddPerformed` → `KazaPerformed`.
- Kimlik üretimi deterministiktir: sıradaki kimlik `w<n>`; `n` kayıt sayısından (`entries.size + performed.size + 1`) başlar, çakışma varsa artırılır. Böylece `e1`, `p1` gibi elle verilmiş kimliklerle çakışmaz.
- Faz 4'teki repository'nin bellek içi sahte karşılığıdır (K-14); yalnızca testler kullanır.

## 5. `ktPending` değişikliği

Kök `build.gradle.kts`'te yalnızca şunlar silinecek:

```kotlin
// F2-03
"KT-35",
"KT-36",
"KT-81",
```

Başka hiçbir satıra dokunulmayacak. Sonuç:

```
KT izlenebilirlik: 101 geçerli satır, 3 testli, 98 bekliyor, fark yok.
```

## 6. Commit sırası

Dal: güncel `main`'den `feat/f2-03-kaza-ledger`. İlk commit yalnızca kartın dört belgesi; `git add` dosya adlarıyla yapılacak, F1-05 dosyaları asla eklenmeyecek. CLAUDE.md 1.8 gereği `docs:` ve `docs(tasks):` commit'lerine kod/test/derleme dosyası girmeyecek.

1. `docs: add F2-03 card and update planning docs`
2. `docs(tasks): add F2-03 plan`
3. `feat(domain): add kaza record types (DATA-04)`
4. `feat(domain): add kaza ledger and balance (DATA-04)`
5. `feat(domain): add perform kaza decision (FR-KAZ-02, KT-35, KT-36)`
6. `feat(domain): add undo and set debt decisions (FR-KAZ-01, FR-KAZ-05, KT-81)`
7. `test(domain): add balance invariant test (FR-KAZ-04, K-12)`
8. `build: remove F2-03 rows from pending KT list (K-16)`
9. `docs(tasks): add F2-03 report`

Her A adımında önce test yazılıp `:core:domain:test` ile kırmızı/derlenmeyen görülecek, sonra kod yazılıp `:core:domain:test` + `ktlintCheck` yeşil olacak. Bölüm B'den sonra `ciCheck` çalıştırılacak.

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

Beklenen: çıktı yok. `ciCheck` yeşil; `:core:domain` 149 test; satır kapsamı ≥ %90; KT kapısı 3 testli / 98 bekliyor; lint kapısı bilinen 6 uyarı. Raporun son satırı tam olarak:

```
PR başlığı: feat(domain): add kaza ledger with perform, undo and set debt decisions (F2-03)
```

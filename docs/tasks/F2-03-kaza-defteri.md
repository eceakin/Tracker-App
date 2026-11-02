# Görev Kartı F2-03 — Kaza defteri

Faz 2 · Kart 3/13 · 2 Kasım 2026 · Hazırlayan: planlama (Claude) · Uygulayan: kodlama ajanı (DeepSeek)

## Amaç

Kaza borcu bu uygulamada bir sayaç değil, bir **defterdir** (M-31, SRS DATA-04): her borç ekleme ve her kılınan kaza ayrı bir kayıttır, kalan borç bu kayıtlardan hesaplanır, hiçbir kayıt silinmez, geri alma bir "düzeltme" kaydıdır. Bu kart defterin domain modelini ve üç kararını yazar:

1. **Kaza kıldım** (FR-KAZ-02, FR-KAZ-04): Seçilen kalemden bir borç düşer. Borç 0 ise önce ekleyip sonra düşmek sorulur; onay yoksa hiçbir şey yazılmaz (KT-35). Kalem seçilmeden kaydedilemez (KT-36).
2. **Geri al** (FR-KAZ-05): Bir kayıt silinmez, ona işaret eden bir düzeltme kaydı eklenir (KT-81).
3. **Borcu elle düzenle** (FR-KAZ-01): Kalemin borcu yeni bir sayıya ayarlanır; fark bir kayıt olarak saklanır (KS-KAZ-01, KS-ONB-04).

Motor veri yazmaz (BR-00, M-20, M-25): her karar, uygulanırsa yazılacak kayıtları (`KazaWrite`) **öneri olarak** döndürür. Yazma Faz 4'ün işidir.

**Bu kart üç KT satırını kapatır:** KT-35, KT-36, KT-81. Kart sonunda KT kapısı `101 geçerli satır, 3 testli, 98 bekliyor, fark yok.` der.

## Ön koşullar

1. `main`, F2-02'nin birleştirildiği `c7638ac` commit'inde ya da daha ilerde ve `origin/main` ile aynı.
2. Kullanıcı şu dört dosyayı çalışma ağacına koymuş olmalı (zip):
   - `CLAUDE.md` (sürüm 1.8)
   - `docs/gereksinim-analizi-srs.md` (1.0-taslak.10)
   - `docs/gelistirme-yol-haritasi.md` (F2-02 tamamlandı güncellemesi)
   - `docs/tasks/F2-03-kaza-defteri.md` (bu kart)
3. Çalışma ağacında bu dört dosya dışında yalnızca F1-05'e ait iki izlenmeyen dosya bulunabilir (`docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md`). Bunlara **dokunma ve commit'leme.** Başka bir değişiklik varsa ya da dosyalardan biri eksikse **dur ve kullanıcıya söyle.**

## Okunacak belgeler ve kimlikler

- `CLAUDE.md` (tamamı; 1.8'de Bölüm 7 "Veri" ve Bölüm 10 "Belge commit'i yalnızca belge içerir" eklendi)
- `docs/gereksinim-analizi-srs.md`: BR-00, BR-09; FR-KAZ-01, 02, 04, 05; Bölüm 6.1 (KazaEntry, KazaPerformed) ve **DATA-04** (1.0-taslak.10'da eklendi; bu kartın kuralları oradadır)
- `docs/karar-tablosu.md`: KT-35, KT-36, KT-81 ve "Sözleşmeler" (başlangıç kaza borcu tüm kalemlerde 0)
- `docs/kabul-senaryolari.md`: KS-KAZ-01, 02, 03, 07, 08; KS-ONB-04
- `docs/mimari-ve-kodlama-ilkeleri.md`: M-20, M-24, M-25, M-31, K-03, K-11, K-12, K-14, K-16
- `docs/tasks/F2-01-temel-model.md` ve `docs/tasks/F2-02-vakit-hesaplari.md`: mevcut tipler ve test kalıpları (`ReferenceDay`, `argumentSet`, `Executable`)

## Adım 0 — Plan ve onay

Bu adımda dosya okuyabilir ve salt okunur git komutlarını (`git status`, `git branch -a`, `git log`, `git fetch`, `git stash list`) çalıştırabilirsin. Hiçbir dosyayı değiştirme, dal değiştirme. Eski sohbet kayıtlarını (`~/.claude` altı) **okuma.**

Plana şunları yaz, sonra **kullanıcının onayını bekle:**

1. **Git başlangıcı:** `git fetch` sonrası `main` ile `origin/main`'in aynı olduğu ve `c7638ac`'yi içerdiği; çalışma ağacındaki her değişikliğin grubu (bu kartın dört dosyası / F1-05'in iki izlenmeyen dosyası / beklenmeyen).
2. **Dosya ağacı ve imzalar:** Bölüm A'daki tiplerin dosyaları ve imzaları; karttan her sapmanın gerekçesi.
3. **Test listesi:** Her testin adı, sınadığı şey ve `argumentSet` satırları (A1 – A5). Tam olarak üç KT kimliği (KT-35, KT-36, KT-81) test adında ya da `argumentSet` adında geçer; başka hiçbir test `KT` ile başlamaz.
4. **Test yardımcısı:** A5'teki `LedgerFixture`'ın imzası ve kimlik üretme biçimi.
5. **`ktPending` değişikliği:** Silinecek üç satır.
6. **Commit sırası** (bkz. Bölüm D).

Onaydan sonra planı, onay mesajındaki değişikliklerle birlikte `docs/tasks/F2-03-plan.md` dosyasına yaz.

## Bölüm A — Kaza defteri (TDD)

Dal: güncel `main`'den `feat/f2-03-kaza-ledger`. İlk commit, kullanıcının koyduğu dört belgedir; dosyaları **adıyla** ekle:

```
git add CLAUDE.md docs/gereksinim-analizi-srs.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-03-kaza-defteri.md
git commit -m "docs: add F2-03 card and update planning docs"
```

İkinci commit plan dosyasıdır: `docs(tasks): add F2-03 plan`.

Her A adımında sıra F2-01 ve F2-02'deki gibidir: testi yaz → `.\gradlew.bat :core:domain:test` ile derlenmediğini ya da kırmızı olduğunu gör ve rapora bir satır yaz → kodu yaz → `.\gradlew.bat :core:domain:test` ve `.\gradlew.bat ktlintCheck` yeşil → commit.

**Genel kurallar:**

- Bütün yeni tipler `com.eceakin.namaztakibi.domain.kaza` paketindedir.
- Kalem tipi mevcut `Prayer` enum'udur. BR-09 ("borca yalnızca farzlar ve vitir yazılır") tip düzeyinde sağlanır: `Prayer`'da sünnet, cuma ya da bayram yoktur. **`Prayer`'ı değiştirme.**
- Motor hiçbir şey yazmaz; kararlar `KazaWrite` önerileri döndürür (BR-00, M-25). Bir karar uygulanırsa yazılacak kayıtların `createdAt` / `performedAt` değeri `clock.instant()`'tır.
- Beklenen sonuçlar `sealed` tiplerle döner (M-24). Tutarsız girdi (ör. kendisiyle çelişen bir defter) programlama hatasıdır: `require(...)`.
- Kural uygulayan her fonksiyonun yorumunda ilgili kimlik yazar (K-05): FR-KAZ-xx, KT-xx, DATA-04.
- Bu kart kerahat engelini **içermez** (F2-05); bekleyen öneriler, otomatik ekleme ve işaretleme yoktur (F2-04).

### A1 — Kayıt tipleri (SRS 6.1, DATA-04)

```kotlin
@JvmInline
value class KazaRecordId(val value: String)          // init: value boş olamaz

enum class KazaEntryKind { ELLE, ONERI, OTOMATIK, HESAPLAYICI, DUZELTME }

data class KazaEntry(
    val id: KazaRecordId,
    val prayer: Prayer,
    val sourceDate: LocalDate?,          // kaynak tarih, biliniyorsa
    val kind: KazaEntryKind,
    val count: Int,                      // adet
    val corrects: KazaRecordId?,         // düzelttiği kayıt
    val missedInSefer: Boolean,
    val createdAt: Instant,
)

data class KazaPerformed(
    val id: KazaRecordId,
    val prayer: Prayer,
    val performedAt: Instant,
)
```

`KazaEntry` değişmezleri (DATA-04). **Aynen bunlar:**

| Tür | `count` | `corrects` |
| --- | --- | --- |
| `ELLE`, `ONERI`, `OTOMATIK`, `HESAPLAYICI` | > 0 | `null` olmalı |
| `DUZELTME` | ≠ 0 (işaretli) | `null` (elle düzenleme) ya da bir kimlik (geri alma) |

`sourceDate` için kısıt yok; `null` olabilir.

**Testler** (`KazaRecordTest`):

- `DATA04_kayitTurleriVeAdetKurallari`: `@ParameterizedTest`; her satır bir `argumentSet` (`"DATA-04 ELLE adet 0 reddedilir"` gibi). Reddedilenler: dört pozitif türün her biri için `count = 0`, `ELLE` için `count = -1`, `ONERI` için `corrects` dolu, `DUZELTME` için `count = 0`. Kabul edilenler: `DUZELTME` için `count = -3` ve `corrects = null`; `DUZELTME` için `count = 2` ve `corrects` dolu. Bozuk nesneler `Executable` içinde kurulur.
- `M24_bosKimlikReddedilir`: `KazaRecordId("")` ve `KazaRecordId("  ")`.

Commit: `feat(domain): add kaza record types (DATA-04)`

### A2 — `KazaLedger` ve kalan borç (DATA-04, FR-KAZ-04)

```kotlin
data class KazaLedger(
    val entries: List<KazaEntry>,
    val performed: List<KazaPerformed>,
) {
    fun balance(prayer: Prayer): Int
    fun isCorrected(id: KazaRecordId): Boolean
    companion object { val EMPTY: KazaLedger }   // karar tablosu: başlangıç borcu her kalemde 0
}
```

- `balance(prayer)` = o kalemin `entries` `count` toplamı − o kalemin `performed` sayısı (DATA-04).
- `isCorrected(id)` = `corrects == id` olan bir `DUZELTME` kaydı var mı.

`init` değişmezleri (`require`). Defter, Faz 4'te veritabanından okunacak tutarlı veriyi temsil eder; bunlardan biri bozuksa bu bir programlama ya da veri hatasıdır:

1. Kimlikler `entries` ve `performed` boyunca benzersiz (iki tablo aynı kimlik uzayını paylaşır).
2. `corrects` dolu her kayıt, defterde var olan bir kayda işaret eder.
3. İşaret edilen kayıt bir `DUZELTME` değildir (düzeltmeler geri alınmaz).
4. Bir kayda en fazla bir düzeltme işaret eder (bir kayıt bir kez geri alınır).
5. Düzeltme, işaret ettiği kayıtla aynı kalemdedir.
6. Her kalemin `balance`'ı ≥ 0 (FR-KAZ-04).

**Testler** (`KazaLedgerTest`):

- `DATA04_kalanBorcKayitlardanHesaplanir`: öğle `ELLE` +8, öğle `DUZELTME` +2, iki öğle `KazaPerformed` → `balance(OGLE) == 8`; `balance(IKINDI) == 0`.
- `DATA04_bosDefterdeHerKalemSifir`: `KazaLedger.EMPTY` için altı kalemin hepsi 0.
- `DATA04_geriAlinanKayitTaninir`: bir kayıt ve ona işaret eden düzeltme → `isCorrected` doğru; başka kayıt için yanlış.
- `M24_tutarsizDefterReddedilir`: `@ParameterizedTest`, yukarıdaki altı değişmezin her biri için bir `argumentSet` (`"M-24 yinelenen kimlik"`, `"M-24 olmayan kayda duzeltme"`, `"M-24 duzeltmenin duzeltmesi"`, `"M-24 iki kez geri alma"`, `"M-24 farkli kalemde duzeltme"`, `"M-24 negatif bakiye"`).

Commit: `feat(domain): add kaza ledger and balance (DATA-04)`

### A3 — Kaza kıldım (FR-KAZ-02, FR-KAZ-04; KT-35, KT-36)

```kotlin
sealed interface KazaWrite {
    data class AddEntry(
        val prayer: Prayer,
        val sourceDate: LocalDate?,
        val kind: KazaEntryKind,
        val count: Int,
        val corrects: KazaRecordId?,
        val missedInSefer: Boolean,
        val createdAt: Instant,
    ) : KazaWrite
    data class AddPerformed(val prayer: Prayer, val performedAt: Instant) : KazaWrite
}

sealed interface PerformDecision {
    data class Allowed(val writes: List<KazaWrite>) : PerformDecision
    data class NeedsConfirmation(
        val question: PerformQuestion,
        val writesIfConfirmed: List<KazaWrite>,
    ) : PerformDecision
    data class Blocked(val reason: PerformBlock) : PerformDecision
}

enum class PerformQuestion { NO_DEBT_ADD_FIRST }
enum class PerformBlock { NO_PRAYER_SELECTED }

// KazaLedger içinde:
fun decidePerform(prayer: Prayer?, clock: Clock): PerformDecision
```

Karar kuralları. **Aynen bunlar:**

| Durum | Sonuç | Kimlik |
| --- | --- | --- |
| `prayer == null` | `Blocked(NO_PRAYER_SELECTED)` | KT-36, FR-KAZ-02 |
| `balance(prayer) >= 1` | `Allowed([AddPerformed(prayer, now)])` | FR-KAZ-02 |
| `balance(prayer) == 0` | `NeedsConfirmation(NO_DEBT_ADD_FIRST, [AddEntry(prayer, sourceDate = null, kind = ELLE, count = 1, corrects = null, missedInSefer = false, createdAt = now), AddPerformed(prayer, now)])` | KT-35, FR-KAZ-04, DATA-04 |

- `prayer` parametresi `Prayer?`'dir, çünkü KT-36 "vakit seçilmeden kaydetmek" isteğini sınar. Kullanıcı arayüzünde böyle bir eylem yoktur (KS-KAZ-03); domain yine de bu girdiyi reddeder.
- `NeedsConfirmation`'da kullanıcı "Vazgeç" derse hiçbir şey yazılmaz; bu, motorun yazma yapmamasının doğal sonucudur. Testte "uygulanmayan karar defteri değiştirmez" olarak sınanır.
- `PerformBlock`'a kerahat nedeni F2-05'te eklenecek; bu kartta tek değer var.

**Test yardımcısı:** A3'ün testleri yazmaları uygulamak için `LedgerFixture`'a ihtiyaç duyar. `LedgerFixture`'ı (tanımı A5'te) **bu adımda, A3'ün testleriyle birlikte** oluştur ve A3'ün commit'ine koy.

**Testler** (`PerformKazaTest`, saat `ReferenceDay.clockAt(1, "14:00")`):

- `KT35_FR_KAZ_04_borcSifirkenKazaKildim_onceEkleSonraDusSorulur`:
  - `KazaLedger.EMPTY.decidePerform(OGLE, clock)` → `NeedsConfirmation(NO_DEBT_ADD_FIRST, …)`; `writesIfConfirmed` yukarıdaki iki kayıtla birebir aynı (`createdAt` ve `performedAt` = G1 14:00).
  - Hiçbir yazma uygulanmazsa defter `EMPTY`'ye eşit kalır ve `balance(OGLE) == 0`.
  - `writesIfConfirmed` uygulanırsa (A5'teki `LedgerFixture`) `balance(OGLE) == 0`, bir `ELLE` kaydı ve bir `KazaPerformed` vardır; bakiye hiçbir adımda −1 olmaz.
- `KT36_FR_KAZ_02_vakitSecilmedenKazaKaydedilemez`: ikindi borcu 2 olan defterde `decidePerform(null, clock)` → `Blocked(NO_PRAYER_SELECTED)`; defter değişmez.
- `KS_KAZ_02_kilinanKazaSecilenVakittenDuser`: öğle 3, ikindi 2; `decidePerform(IKINDI, clock)` → `Allowed([AddPerformed(IKINDI, G1 14:00)])`; uygulanınca ikindi 1, öğle 3.

Commit: `feat(domain): add perform kaza decision (FR-KAZ-02, KT-35, KT-36)`

### A4 — Geri al ve elle düzenle (FR-KAZ-05, FR-KAZ-01; KT-81)

```kotlin
sealed interface UndoDecision {
    data class Allowed(val write: KazaWrite.AddEntry) : UndoDecision
    data class Blocked(val reason: UndoBlock) : UndoDecision
}
enum class UndoBlock { NOT_FOUND, ALREADY_CORRECTED, CORRECTION_NOT_UNDOABLE, WOULD_GO_NEGATIVE }

sealed interface SetDebtDecision {
    data object NoChange : SetDebtDecision
    data class Allowed(val write: KazaWrite.AddEntry) : SetDebtDecision
    data class Blocked(val reason: SetDebtBlock) : SetDebtDecision
}
enum class SetDebtBlock { NEGATIVE_TARGET }

// KazaLedger içinde:
fun decideUndo(id: KazaRecordId, clock: Clock): UndoDecision
fun decideSetDebt(prayer: Prayer, target: Int, clock: Clock): SetDebtDecision
```

**`decideUndo` kuralları** (DATA-04, FR-KAZ-05). Sırayla değerlendirilir; ilk uyan sonuç döner:

| Durum | Sonuç |
| --- | --- |
| `id` defterde yok | `Blocked(NOT_FOUND)` |
| Kayıt bir `DUZELTME` | `Blocked(CORRECTION_NOT_UNDOABLE)` |
| Kayıt zaten geri alınmış (`isCorrected(id)`) | `Blocked(ALREADY_CORRECTED)` |
| Kayıt bir `KazaEntry` ve `balance(prayer) − entry.count < 0` | `Blocked(WOULD_GO_NEGATIVE)` |
| Kayıt bir `KazaEntry` | `Allowed(AddEntry(prayer = entry.prayer, sourceDate = entry.sourceDate, kind = DUZELTME, count = −entry.count, corrects = id, missedInSefer = entry.missedInSefer, createdAt = now))` |
| Kayıt bir `KazaPerformed` | `Allowed(AddEntry(prayer = performed.prayer, sourceDate = null, kind = DUZELTME, count = +1, corrects = id, missedInSefer = false, createdAt = now))` |

**`decideSetDebt` kuralları** (FR-KAZ-01, DATA-04):

| Durum | Sonuç |
| --- | --- |
| `target < 0` | `Blocked(NEGATIVE_TARGET)` |
| `target == balance(prayer)` | `NoChange` |
| O kalemde hiç `KazaEntry` ve `KazaPerformed` yok | `Allowed(AddEntry(prayer, sourceDate = null, kind = ELLE, count = target, corrects = null, missedInSefer = false, createdAt = now))` |
| Aksi hâlde | `Allowed(AddEntry(prayer, sourceDate = null, kind = DUZELTME, count = target − balance(prayer), corrects = null, missedInSefer = false, createdAt = now))` |

(Hiç kaydı olmayan kalemde bakiye 0'dır; `target > 0` olduğu için `ELLE` kaydının adedi pozitiftir.)

**Testler** (`UndoAndSetDebtTest`):

- `KT81_FR_KAZ_05_onaylananOneriGeriAlinir_duzeltmeKaydiEklenir`: Defterde tek kayıt: öğle, `ONERI`, `count = 1`, `sourceDate = G1`, kimlik `e1` (karar tablosu: "bir öneri onaylandı; borç(öğle) = 1"). `decideUndo(e1, G2 10:00)` → `Allowed(AddEntry(OGLE, G1, DUZELTME, −1, e1, false, G2 10:00))`. Uygulanınca: `entries` iki kayıt (orijinal silinmemiş), `balance(OGLE) == 0`, `isCorrected(e1)`.
- `FR_KAZ_05_geriAlmaKurallari`: `@ParameterizedTest`; her satır bir `argumentSet`:
  - `"FR-KAZ-05 olmayan kayit"` → `Blocked(NOT_FOUND)`
  - `"FR-KAZ-05 duzeltme geri alinmaz"` → `Blocked(CORRECTION_NOT_UNDOABLE)`
  - `"FR-KAZ-05 ikinci geri alma"` → `Blocked(ALREADY_CORRECTED)`
  - `"FR-KAZ-05 negatife dusuren geri alma"`: öğle `ONERI` +1 ve bir öğle `KazaPerformed` (bakiye 0); `ONERI` kaydını geri almak → `Blocked(WOULD_GO_NEGATIVE)`
  - `"FR-KAZ-05 kilinan kaza geri alinir"`: öğle `ELLE` +2, bir öğle `KazaPerformed` `p1`; `decideUndo(p1)` → `Allowed(AddEntry(OGLE, null, DUZELTME, +1, p1, false, now))`; uygulanınca bakiye 2
- `KS_KAZ_01_borcElleDuzenlenir_duzeltmeKaydiSaklanir`: öğle borcu 8 (tek `ELLE` +8); `decideSetDebt(OGLE, 10)` → `Allowed(AddEntry(OGLE, null, DUZELTME, +2, null, false, now))`; uygulanınca 10.
- `KS_ONB_04_baslangicBorcuKalemBasinaTekElleKaydi`: boş defterde altı kalem için sırayla sabah 10, öğle 8, ikindi 8, akşam 5, yatsı 9, vitir 9; her biri tek bir `ELLE` kaydı; uygulanınca bakiyeler aynen bu sayılar, toplam 49.
- `FR_KAZ_01_elleDuzenlemeSinirlari`: `@ParameterizedTest`: `"FR-KAZ-01 negatif hedef"` (−1 → `Blocked(NEGATIVE_TARGET)`), `"FR-KAZ-01 ayni sayi"` (8 → 8 → `NoChange`), `"FR-KAZ-01 sifira indirme"` (8 → 0 → `DUZELTME −8`), `"FR-KAZ-01 bos kalemde sifir"` (0 → 0 → `NoChange`).

Commit: `feat(domain): add undo and set debt decisions (FR-KAZ-01, FR-KAZ-05, KT-81)`

### A5 — Test yardımcısının tanımı ve değişmez testi (K-12, K-14)

**`LedgerFixture`** (A3'te oluşturulur; test kaynaklarında `com.eceakin.namaztakibi.domain.kaza.testing` paketinde; ürün koduna girmez):

```kotlin
object LedgerFixture {
    /** Yazmaları sırayla deftere uygular; kimlikleri "w1", "w2", … diye üretir. */
    fun apply(ledger: KazaLedger, writes: List<KazaWrite>): KazaLedger
}
```

- Faz 4'teki repository'nin bellek içi sahte karşılığıdır (K-14); yalnızca testler kullanır.
- Kimlik üretimi deterministiktir: defterdeki kayıt sayısına göre sıradaki `w<n>`. Defterde aynı kimlik varsa `KazaLedger`'ın `init`'i zaten reddeder.
- Her yazmadan sonra yeni bir `KazaLedger` kurar; böylece `init` değişmezleri **her adımda** denetlenir. Bir ara adımda bakiye negatife düşerse test bir `IllegalArgumentException` ile kırılır.

**Değişmez testi** (`LedgerInvariantTest`):

- `FR_KAZ_04_borcAslaNegatifOlmaz`: `@ParameterizedTest`. Her satır bir komut dizisidir. Küçük bir test sürücüsü her komutu sırayla karara çevirir ve yalnızca `Allowed` yazmalarını (onay gerektiren adımda belirtilen yanıta göre `writesIfConfirmed`'i ya da hiçbir şeyi) uygular; her adımdan sonra öğle bakiyesini kaydeder. Beklenen bakiye dizileri **aynen bunlar:**

  | `argumentSet` | Komutlar | Her adımdan sonra öğle bakiyesi |
  | --- | --- | --- |
  | `"FR-KAZ-04 sifirdan onayli kaza ve geri alma"` | kıl (onayla), kılınan kazayı geri al | 0, 1 |
  | `"FR-KAZ-04 vazgecilen soru"` | kıl (vazgeç), kıl (vazgeç) | 0, 0 |
  | `"FR-KAZ-04 borc biterken"` | borcu 2 yap, kıl, kıl, kıl (vazgeç) | 2, 1, 0, 0 |
  | `"FR-KAZ-04 negatife dusuren geri alma engellenir"` | borcu 1 yap, kıl, ilk kaydı geri al (engel) | 1, 0, 0 |
  | `"FR-KAZ-04 sifira indirme ve negatif hedef"` | borcu 5 yap, borcu 0 yap, borcu −1 yap (engel) | 5, 0, 0 |

  "Kılınan kazayı geri al" ve "ilk kaydı geri al" için sürücü, kimliği uygulanan yazmanın `LedgerFixture`'daki kimliğinden alır.

Commit: `test(domain): add balance invariant test (FR-KAZ-04, K-12)`

## Bölüm B — Kapılar

1. **`ktPending`:** Kök `build.gradle.kts`'te `// F2-03` grubunun üç satırını (`"KT-35"`, `"KT-36"`, `"KT-81"`) ve grup yorumunu sil. Başka hiçbir satıra dokunma.
2. `.\gradlew.bat checkKtTraceability` → `KT izlenebilirlik: 101 geçerli satır, 3 testli, 98 bekliyor, fark yok.`
3. `.\gradlew.bat :core:domain:jacocoTestCoverageVerification` yeşil; satır ve dal kapsamı rapora yazılır.

Commit: `build: remove F2-03 rows from pending KT list (K-16)`

## Bilinen tuzaklar

1. **Motor yazmaz.** `decidePerform`, `decideUndo` ve `decideSetDebt` defteri değiştirmez, yeni bir defter döndürmez; yalnızca `KazaWrite` önerileri döndürür. Defteri değiştiren tek şey testteki `LedgerFixture`'dır.
2. **Kayıt silme yok.** Geri alma bir `DUZELTME` kaydıdır; hiçbir fonksiyon `entries` ya da `performed` listesinden eleman çıkarmaz (M-31).
3. **`KazaPerformed` geri alınınca** borç +1 artar ve düzeltmenin kalemi kılınan kazanın kalemidir.
4. **Kimlikler iki tablo için ortaktır.** `KazaEntry` ve `KazaPerformed` aynı kimliği taşıyamaz; `corrects` her iki türe de işaret edebilir.
5. **`ELLE` ile `ONERI` farklıdır** (DATA-04). KT-35'teki "önce ekle" kaydı `ELLE`, KT-81'deki onaylanmış öneri `ONERI`dir. Türleri karıştırma; geçmiş ekranı bu türe göre "elle" ya da "öneri onaylandı" yazacak (KS-ONB-04, KS-KAZ-09).
6. **Birlikte geri alma yok.** KT-35'teki iki kayıt ya da FR-TRK-04'teki "kaza olarak kıldım" kayıtları için "birlikte geri alma" (işlem kimliği) F2-04'te eklenecek. Bu kartta her kayıt tek tek geri alınır.
7. **KT adı yalnızca üç testte.** `KT35_`, `KT36_`, `KT81_` dışında `KT` ile başlayan test adı ya da `argumentSet` yazma; KT kapısı "geçersiz" ya da "bekleyen listede" diye kırılır.
8. **Kerahat yok.** KT-35'in saati (14:00) kerahat dışındadır; kerahatte kaza engeli F2-05'te `decidePerform`'a eklenecek.
9. **`git add -A`, `git add .`, `stash`, `--amend`, `reset --hard` yasak.** Belge commit'lerine kod girmez (CLAUDE.md 1.8 Bölüm 10).

## Dur ve sor

- Çalışma ağacında ön koşullarda sayılanlar dışında bir değişiklik var ya da `main`, `origin/main`'in gerisinde.
- Kartın bir kuralı SRS DATA-04, FR-KAZ-01 – 05 ya da KT-35, 36, 81 ile çelişiyor gibi görünüyor.
- Bir karar için `Prayer`, `RuleSet`, `PrayerTimesDay` ya da `domain.times` tiplerini değiştirmek gerekiyor gibi görünüyor.
- KT-35, 36, 81 dışında bir KT satırının testini yazmak ya da `ktPending`'de başka bir satıra dokunmak gerekiyor gibi görünüyor.
- `CLAUDE.md` Bölüm 12'deki diğer koşullar.

## Kapsam dışı (bu kartta yapma)

- Kaçırıldı önerisi, otomatik ekleme, bekleyen öneriler, toplu onay, işaretleme ve "kaza olarak kıldım" (F2-04).
- Birlikte yazılan kayıtları birlikte geri alma ve işlem kimliği (F2-04).
- Kerahatte kaza engeli (F2-05), seferde kaçan namazın rekâtı (F2-05; `missedInSefer` alanı burada yalnızca taşınır).
- Tertip notu ve farz borcu toplamı (F2-06).
- Mazeret birleşmesinde onaylanmış borçların geri alınması (F2-09; KT-96).
- Borç hesaplayıcı (F2-13; `HESAPLAYICI` türü yalnızca tanımlıdır).
- Repository arayüzü, Room ve kalıcılık (Faz 4).
- `Prayer`, `RuleSet`, `domain.times`, `ci.yml`, lint kapısı, M-08 ve `ProjectConfig`.
- `docs/` altındaki belgeleri değiştirmek (kartın plan ve rapor dosyaları hariç).

## Kabul kriterleri

**Domain**

- [ ] `KazaRecordId`, `KazaEntryKind`, `KazaEntry`, `KazaPerformed`, `KazaLedger`, `KazaWrite`, `PerformDecision`, `UndoDecision`, `SetDebtDecision` ve yardımcı enum'lar karttaki imzalarla `domain.kaza` paketinde.
- [ ] `KazaEntry` ve `KazaLedger` değişmezleri A1 ve A2 tablolarıyla aynı; her biri en az bir testle sınanıyor.
- [ ] `decidePerform`, `decideUndo` ve `decideSetDebt` A3 ve A4 tablolarıyla birebir; hiçbiri defteri değiştirmiyor.
- [ ] KT-35, KT-36 ve KT-81'in beklenenleri karar tablosuyla aynı: borç asla −1 olmaz, onaysız hiçbir şey yazılmaz; vakit seçilmeden engel; geri alma kaydı silmez, düzeltme ekler, borç 0, geçmişte iki kayıt.
- [ ] `FR_KAZ_04_borcAslaNegatifOlmaz` beş dizinin bakiyelerini aynen veriyor.
- [ ] Her A adımında testin önce kırmızı ya da derlenmeyen olduğu raporda yazıyor.
- [ ] `:core:domain` ürün kodunda yasak çağrılar yok (Bölüm D'deki aramalar).

**Kapılar**

- [ ] `ktPending`'den yalnızca KT-35, KT-36, KT-81 ve `// F2-03` yorumu silinmiş.
- [ ] KT kapısı: `101 geçerli satır, 3 testli, 98 bekliyor, fark yok.`
- [ ] Kapsam kapısı yeşil; satır ve dal yüzdeleri raporda.

**Genel**

- [ ] `.\gradlew.bat ciCheck` yerelde yeşil; lint kapısı "bilinen 6 uyarı eşleşti, fark yok".
- [ ] `.\gradlew.bat help` hatasız (M-08).
- [ ] `.github/workflows/ci.yml` değişmemiş.
- [ ] Belge commit'lerinde (`docs:`, `docs(tasks):`) kod, test ya da derleme dosyası yok.
- [ ] Plan `docs/tasks/F2-03-plan.md`'de, rapor `docs/tasks/F2-03-rapor.md`'de; rapor `CLAUDE.md` Bölüm 11 biçiminde ve son satırı **PR başlığı**.
- [ ] Commit'ler `feat/f2-03-kaza-ledger` dalında, Conventional Commits biçiminde; `main`'e dokunulmamış; F1-05 dosyaları commit'lenmemiş.

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

Son komut, belge commit'lerinde yalnızca belge dosyalarının bulunduğunu göstermek içindir; çıktısını rapora özetle.

Yasak çağrılar için (Git Bash). Yorum satırları (`*` ya da `//` ile başlayanlar) aramanın dışında tutulur:

```
grep -rnE "android\.|Instant\.now|LocalDate\.now|LocalDateTime\.now|ZoneId\.systemDefault|System\.currentTimeMillis|!!|clock\.zone" core/domain/src/main | grep -vE "^[^:]+:[0-9]+:[[:space:]]*(\*|//)"
```

Beklenen: çıktı yok.

Beklenenler:

- `ciCheck` yeşil.
- `:core:domain` testleri: F2-02 sonrası 110 + bu kartın testleri; toplam dosya dosya raporda.
- Satır kapsamı %90 veya üstü.
- KT kapısı: 101 geçerli, 3 testli, 98 bekliyor.
- Lint kapısı: bilinen 6 uyarı eşleşti.

Commit sırası:

1. `docs: add F2-03 card and update planning docs`
2. `docs(tasks): add F2-03 plan`
3. `feat(domain): add kaza record types (DATA-04)`
4. `feat(domain): add kaza ledger and balance (DATA-04)`
5. `feat(domain): add perform kaza decision (FR-KAZ-02, KT-35, KT-36)`
6. `feat(domain): add undo and set debt decisions (FR-KAZ-01, FR-KAZ-05, KT-81)`
7. `test(domain): add balance invariant test (FR-KAZ-04, K-12)`
8. `build: remove F2-03 rows from pending KT list (K-16)`
9. `docs(tasks): add F2-03 report`

Commit mesajlarını tek satır ve düz ASCII tırnakla yaz; `git log --oneline` ile bozulmadığını kontrol et.

Raporu `docs/tasks/F2-03-rapor.md` dosyasına, `CLAUDE.md` Bölüm 11'e göre yaz ve son commit olarak ekle. Ek olarak:

- Test sayısı dosya dosya.
- Kapsam yüzdeleri.
- KT kapısının çıktısı.
- Son satır: `PR başlığı: feat(domain): add kaza ledger with perform, undo and set debt decisions (F2-03)`

**Push etme, PR açma.** Kullanıcı yapacak.

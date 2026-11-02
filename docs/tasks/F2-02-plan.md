# F2-02 Uygulama Planı — Vakit hesapları

Dal: `feat/f2-02-prayer-time-windows` · 2 Kasım 2026 · Onay: Adım 0 planı onaylandı; D1 – D2 düzeltmeleri işlendi.

Bu plan, `docs/tasks/F2-02-vakit-hesaplari.md` kartının Adım 0 çıktısıdır. Kartın sırası ve kapsamı esastır; planda karttan sapma yoktur. D1 – D2 yalnızca uygulama ayrıntısını netleştirir.

## 1. Git başlangıcı

- `main` = `origin/main` = `9631cc3ef73ff1ed1809a7178d7565e1a83eff41`; `HEAD...origin/main` → `0 0`.
- `9631cc3` `HEAD`'in atasıdır; `git fetch origin` yeni bir şey getirmedi; `git stash list` boş.
- Yerel dallar: `main`, `chore/f1-02-modules-hygiene`, `feat/f1-01-skeleton-domain`. `feat/f2-01-domain-foundation` yerelde yok; silme yapılmadı.
- Çalışma ağacı grupları:
  - Bu kartın dört dosyası: `CLAUDE.md`, `docs/mimari-ve-kodlama-ilkeleri.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-02-vakit-hesaplari.md`.
  - F1-05'in iki izlenmeyen dosyası: `docs/tasks/F1-05-plan.md`, `docs/tasks/F1-05-risk-prototipi.md` — dokunulmaz, commit edilmez.
  - Beklenmeyen değişiklik yok.

İlk commit yalnızca dört belgedir; dosyalar adlarıyla eklenir:

```
git add CLAUDE.md docs/mimari-ve-kodlama-ilkeleri.md docs/gelistirme-yol-haritasi.md docs/tasks/F2-02-vakit-hesaplari.md
git commit -m "docs: add F2-02 card and update planning docs"
```

İkinci commit bu plan dosyasıdır: `docs(tasks): add F2-02 plan`.

## 2. Dosya ağacı ve imzalar

Karttaki imzalardan **sapma yok**. Bütün yeni tipler `com.eceakin.namaztakibi.domain.times` paketinde. Dosya dağılımı:

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/times/TimeWindow.kt`
  - `data class TimeWindow(val start: Instant, val end: Instant)`
  - `init`: `require(start < end)`
  - `operator fun contains(instant: Instant): Boolean` → `start <= instant < end`
  - `val duration: Duration` → `Duration.between(start, end)`
- `.../times/PrayerDayWindows.kt`
  - `data class PrayerDayWindows(val day: PrayerTimesDay, val next: PrayerTimesDay)`
  - `init`: `require(next.district == day.district)`, `require(next.date == day.date.plusDays(1))`, `require(day.yatsi < next.imsak)`
  - `fun windowOf(prayer: Prayer): TimeWindow` — A2 tablosundaki altı eşleme aynen; `YATSI` ve `VITIR` → `[day.yatsi, next.imsak)`
  - `fun missedAt(prayer: Prayer): Instant` → `windowOf(prayer).end`
  - `fun currentVakit(clock: Clock): CurrentVakit`
  - `fun kerahatWindows(ruleSet: RuleSet): List<KerahatWindow>` — sıra `GUNES`, `ISTIVA`, `AKSAM`
  - `fun kerahatStatus(clock: Clock, ruleSet: RuleSet): KerahatStatus`
- `.../times/CurrentVakit.kt`
  - `sealed interface CurrentVakit`
  - `data class Within(val prayer: Prayer, val window: TimeWindow) : CurrentVakit`
  - `data class Outside(val nextPrayer: Prayer, val nextStart: Instant) : CurrentVakit`
- `.../times/KerahatWindow.kt`
  - `enum class KerahatKind { GUNES, ISTIVA, AKSAM }`
  - `data class KerahatWindow(val kind: KerahatKind, val window: TimeWindow)`
  - `sealed interface KerahatStatus`; `data object None`; `data class Inside(val kerahat: KerahatWindow)`
- `.../times/PrayerDate.kt`
  - Üst düzey `fun prayerDateOf(clock: Clock, zone: ZoneId, localDayTimes: PrayerTimesDay): LocalDate`

Test dosyaları: `TimeWindowTest.kt`, `PrayerDayWindowsTest.kt`, `CurrentVakitTest.kt`, `KerahatTest.kt`, `PrayerDateTest.kt`, `DaylightSavingTest.kt`. `ReferenceDay`'e ekleme yapılmayacak.

`currentVakit` ve `kerahatStatus` yalnızca `clock.instant()` kullanacak; `clock.zone` hiçbir hesapta geçmeyecek. `prayerDateOf` bölgeyi ayrı `ZoneId` parametresinden alacak. `CurrentVakit.Within` hiçbir zaman `VITIR` döndürmeyecek; `Outside` yalnızca güneş–öğle arasında oluşacak.

## 3. Test listesi

Hiçbir test adı ya da `argumentSet` adı `KT` ile başlamıyor. D1 sonrası toplam **64 yeni test çalıştırması**; F2-01'in 46'sıyla `:core:domain` **110 çalıştırma**.

> **Sayım notu:** Onay mesajındaki "KerahatTest 15" ifadesi ile iki `argumentSet`'in ayrı çalıştırma sayılması sonucu 16 arasında küçük bir sayım farkı var. 64 ve 110 toplamları, KerahatTest'in 16 çalıştırmasıyla tutarlıdır; plan bu toplamları esas alır.

### A1 — `TimeWindowTest` (3)

| Test | Sınadığı |
| --- | --- |
| `M24_baslangicBitistenOnceOlmali` | `start == end` ve `start > end` → `IllegalArgumentException` |
| `M23_aralikBaslangicDahilBitisHaric` | `start` içeride, `end` dışarıda, `end − 1 dk` içeride, `start − 1 dk` dışarıda |
| `M23_sureDogruHesaplanir` | `12:30–15:30` penceresinin süresi 3 saat |

### A2 — `PrayerDayWindowsTest` (16)

| Test | Sınadığı |
| --- | --- |
| `BR02_BR04_vakitAraliklari` | Altı kalemin penceresi A2 tablosuyla birebir |
| `BR04_kacirildiAnlari` | Altı kalemin `missedAt` değeri |
| `BR02_gunesIleOgleArasiHicbirKaleminPenceresiDegil` | 07:00, 09:00 ve 12:29 hiçbir pencerede değil; 12:30 öğlenin penceresinde |
| `M24_gecersizGunCiftiReddedilir` | Üç geçersiz gün çifti reddedilir |

`BR02_BR04_vakitAraliklari` argumentSet satırları:
- `"BR-02 SABAH 05:30-07:00"`
- `"BR-02 OGLE 12:30-15:30"`
- `"BR-02 IKINDI 15:30-18:00"`
- `"BR-02 AKSAM 18:00-19:30"`
- `"BR-02 YATSI 19:30-G2 05:30"`
- `"BR-03 VITIR 19:30-G2 05:30"`

`BR04_kacirildiAnlari` argumentSet satırları:
- `"BR-04 SABAH 07:00"`
- `"BR-04 OGLE 15:30"`
- `"BR-04 IKINDI 18:00"`
- `"BR-04 AKSAM 19:30"`
- `"BR-04 YATSI G2 05:30"`
- `"BR-04 VITIR G2 05:30"`

`M24_gecersizGunCiftiReddedilir` argumentSet satırları:
- `"M-24 farkli ilce"`
- `"M-24 ardisik olmayan tarih"`
- `"M-24 yatsi ertesi imsaktan sonra"`

Bozuk nesneler F2-01'deki gibi `Executable` içinde kurulacak.

### A3 — `CurrentVakitTest` (16)

| Test | Sınadığı |
| --- | --- |
| `BR02_BR04_suAnkiVakit` | Tablodaki 13 an; `Within` satırlarında pencere `windowOf` ile aynı |
| `BR04_saniyeliAnSinirinDogruTarafinda` | G1 15:29:59 → `Within(OGLE)`, G1 15:30:00 → `Within(IKINDI)` |
| `M23_namazGunuDisindakiAnReddedilir` | G1 05:29 ve G2 05:30 → `IllegalArgumentException` |

`BR02_BR04_suAnkiVakit` argumentSet satırları:
- `"BR-02 G1 05:30 SABAH"`
- `"BR-02 G1 06:59 SABAH"`
- `"BR-02 G1 07:00 OGLE disinda"`
- `"BR-02 G1 12:29 OGLE disinda"`
- `"BR-02 G1 12:30 OGLE"`
- `"BR-02 G1 15:29 OGLE"`
- `"BR-02 G1 15:30 IKINDI"`
- `"BR-02 G1 17:59 IKINDI"`
- `"BR-02 G1 18:00 AKSAM"`
- `"BR-02 G1 19:29 AKSAM"`
- `"BR-02 G1 19:30 YATSI"`
- `"BR-02 G2 00:30 YATSI"`
- `"BR-02 G2 05:29 YATSI"`

`M23_namazGunuDisindakiAnReddedilir` argumentSet satırları:
- `"M-23 G1 05:29"`
- `"M-23 G2 05:30"`

### A4 — `KerahatTest` (16)

| Test | Sınadığı |
| --- | --- |
| `BR07_kerahatPencereleri` | `[07:00, 07:45)`, `[11:45, 12:30)`, `[17:15, 18:00)` |
| `BR07_kerahatSinirlari` | 12 sınır anı |
| `BR07_pencereSuresiRuleSettenGelir` | `kerahatAfterGunes = 40 dk` ile `[07:00, 07:40)`; 07:40 → `None` |
| `M23_kerahatDurumuNamazGunuDisindaReddedilir` | `kerahatStatus` namaz günü dışındaki anı reddeder (D1) |

`BR07_kerahatSinirlari` argumentSet satırları:
- `"BR-07 G1 06:59 None"`
- `"BR-07 G1 07:00 GUNES"`
- `"BR-07 G1 07:44 GUNES"`
- `"BR-07 G1 07:45 None"`
- `"BR-07 G1 11:44 None"`
- `"BR-07 G1 11:45 ISTIVA"`
- `"BR-07 G1 12:29 ISTIVA"`
- `"BR-07 G1 12:30 None"`
- `"BR-07 G1 17:14 None"`
- `"BR-07 G1 17:15 AKSAM"`
- `"BR-07 G1 17:59 AKSAM"`
- `"BR-07 G1 18:00 None"`

`M23_kerahatDurumuNamazGunuDisindaReddedilir` argumentSet satırları:
- `"M-23 G1 05:29"`
- `"M-23 G2 05:30"`

Bu testte `ruleSet = RuleSet.HANEFI`; çağrı `Executable` içinde yapılır ve `IllegalArgumentException` beklenir. Test A4'ün TDD sırasına girer: önce kırmızı görülecek, sonra `kerahatStatus` yazılacak.

### A5 — `PrayerDateTest` (8)

| Test | Sınadığı |
| --- | --- |
| `KS_TRK_11_geceYarisindanImsakaOncekiNamazGunu` | Altı an için namaz günü |
| `M23_yanlisGununVakitleriReddedilir` | G2 00:30 için `times(1)` → `IllegalArgumentException` |
| `M23_saatinBolgesiKullanilmaz` | UTC bölgeli saat + İstanbul `zone` + `times(2)` → G1 |

`KS_TRK_11_geceYarisindanImsakaOncekiNamazGunu` argumentSet satırları:
- `"KS-TRK-11 G1 23:59 times(1) -> G1"`
- `"KS-TRK-11 G2 00:00 times(2) -> G1"`
- `"KS-TRK-11 G2 00:30 times(2) -> G1"`
- `"KS-TRK-11 G2 05:29 times(2) -> G1"`
- `"KS-TRK-11 G2 05:29:59 times(2) -> G1"`
- `"KS-TRK-11 G2 05:30 times(2) -> G2"`

### A6 — `DaylightSavingTest` (5)

| Test | Sınadığı |
| --- | --- |
| `NFR_CMP_02_ilkbaharGecisi_yatsiPenceresi8Saat` | `[2026-03-28T19:00Z, 2026-03-29T03:00Z)`, 8 saat |
| `NFR_CMP_02_sonbaharGecisi_yatsiPenceresi10Saat` | `[2026-10-24T18:00Z, 2026-10-25T04:00Z)`, 10 saat |
| `NFR_CMP_02_ilkbaharGecisi_namazGunu` | 00:59Z ve 01:30Z → 28 Mart; 03:00Z → 29 Mart |
| `NFR_CMP_02_sonbaharGecisi_ikiKezYasanan0230` | 00:30Z ve 01:30Z → 24 Ekim; 04:00Z → 25 Ekim |
| `NFR_CMP_02_gecisGunundeVakitlerUtcDogru` | 29 Mart öğle `[10:30Z, 13:30Z)`; 25 Ekim öğle `[11:30Z, 14:30Z)` |

A6 testleri kod yazıldıktan sonra eklenecek; kırmızı beklenmiyor. Bölge `Europe/Berlin`; fikstür yardımcısı test dosyasında olacak, `ReferenceDay`'e dokunulmayacak.

## 4. Java zaman API'si notları

- **Boşluk (spring forward):** Berlin'de 2026-03-29 02:30 yerel saati yoktur. `LocalDateTime.atZone(...)` boşluktaki saati ileri kaydırır: 02:30 → 03:30 CEST, yani `2026-03-29T01:30Z`.
- **Çakışma (fall back):** Berlin'de 2026-10-25 02:30 iki kez yaşanır. `LocalDateTime.atZone(...)` varsayılan olarak **önceki ofseti** (CEST, +02) seçer: 02:30 → `2026-10-25T00:30Z`. İkinci 02:30 (CET, +01) ancak açıkça `01:30Z` yazılarak temsil edilir.
- Bu yüzden A6'da beklenen anlar `Instant.parse("...Z")` ile UTC olarak yazılacak; yerel saatten türetilmeyecek. Fikstür yerel saatleri (05:00, 06:30, 12:30, 15:30, 18:30, 20:00) bilerek 02:00–03:00 dışında; yardımcı bunları `LocalDate.atTime(...).atZone(BERLIN)` ile kurabilir.
- `ZonedDateTime.toInstant()` ve `Duration.between(start, end)` `Instant` üzerinde çalıştığı için yaz saati geçişinde gerçek geçen süreyi verir; 8 ve 10 saatlik yatsı pencereleri bunu doğrular.
- A5'te `clock.zone` tuzağı ayrıca sınanacak: saat UTC bölgeli, `zone` parametresi İstanbul, `localDayTimes` G2. Kod yanlışlıkla `clock.zone` kullanırsa yerel gün G1 bulunur ve `require` kırılır.

## 5. Bölüm C — F2-01 temizlikleri

1. **`build.gradle.kts`, `checkKtTraceability` okuma hatası:** Tablo dosyası okunamazsa (yok ya da okuma hatası) ayrı mesaj verilecek: `KT kapısı: docs/karar-tablosu.md okunamadı: <hata mesajı>.` "KT satırı bulunamadı" mesajı yalnızca dosya okunduğu hâlde `validIds` boşsa kalacak.
2. **Ek uyumu:** `"$id'in testi var..."` yerine `"$id kimliğinin testi var ama bekleyen listede duruyor. Kimliği ktPending'den sil."` kullanılacak.
3. **`PrayerTimesDayTest`:** Instance `baseDay()` ve companion `baseDayForArguments()` aynı nesneyi kuruyor. Instance fonksiyon kaldırılacak; yalnızca companion'daki fonksiyon `baseDay()` adıyla kalacak ve testler ona bağlanacak. Test sayısı (10) ve adları değişmeyecek.

**Doğrulama (commit sonrası, commit edilmez):**

- `ktPending`'e geçici `KT-14` eklenip "geçersiz kimlik" mesajı görülecek.
- Geçici `@Test fun KT01_kapiDenemesi() {}` ile yeni `"KT-01 kimliğinin testi var..."` mesajı görülecek.
- Tablo yolu geçici olarak yanlış adla değiştirilip `"KT kapısı: docs/karar-tablosu.md okunamadı: ..."` mesajı görülecek.
- Her deneme yalnızca dokunulan dosyada `git restore <yol>` ile geri alınacak; sonrasında `git diff` boş olacak. `git restore .`, `git checkout -- .`, `git reset`, `git stash` kullanılmayacak.

## 6. Commit sırası

Dal: güncel `main`'den `feat/f2-02-prayer-time-windows`. İlk commit yalnızca kartın dört belgesi; `git add` dosya adlarıyla yapılacak, F1-05 dosyaları asla eklenmeyecek.

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

Her A adımında önce test yazılıp `:core:domain:test` ile kırmızı görülecek (A6 hariç), sonra kod yazılıp `:core:domain:test` + `ktlintCheck` yeşil olacak. Bölüm B'de `ciCheck`, kapsam kapısı ve KT kapısı doğrulanacak; `ktPending` değişmeyecek.

### Son doğrulama (Bölüm D)

Aşağıdaki komutlar **bu sırayla** çalıştırılacak ve sonuçları rapora özetlenecek:

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

İkisinin de çıktısı **boş** olmalı; komutlar ve sonuçları rapora yazılacak.

Beklenenler: `ciCheck` yeşil; `:core:domain` 110 test; satır kapsamı %90 veya üstü; KT kapısı `101 geçerli satır, 0 testli, 101 bekliyor, fark yok.`; lint kapısı bilinen 6 uyarı eşleşti.

Raporun son satırı tam olarak şu olacak (CLAUDE.md Bölüm 11):

```
PR başlığı: feat(domain): add prayer time windows and prayer date (F2-02)
```

## Onay notu

"Dur ve sor" koşullarından hiçbiri yok: `main` ile `origin/main` aynı ve `9631cc3`'ü içeriyor; çalışma ağacında beklenmeyen değişiklik yok; A2/A4 pencere tabloları SRS BR-02, BR-03, BR-04, BR-07 ve karar tablosunun "Fikstürden türeyen pencereler" tablosuyla uyumlu; yeni bir `RuleSet` alanı gerekmiyor; yüksek enlem durumu kapsam dışı; `ktPending` değişmiyor.

## Rapor: F2-02 — Vakit hesapları

Dal: `feat/f2-02-prayer-time-windows`

### Yapılanlar

- **A1 — `TimeWindow`:** `[start, end)` aralığı; `start < end` değişmezi, `contains` ve `duration`. Başlangıç dahil, bitiş hariç semantiği test edildi.
- **A2 — `PrayerDayWindows`:** Altı kalemin penceresi ve `missedAt` anları; `next` ile ilçe/tarih/yatsı-imsak değişmezleri. `VITIR` penceresi yatsıyla aynı; vitirin yatsıya bağlılığı bu kartta uygulanmadı.
- **A3 — `CurrentVakit`:** `Within` ve `Outside`; `currentVakit` namaz günü dışındaki anı reddediyor; `Within` hiçbir zaman `VITIR` dönmüyor; `Outside` yalnızca güneş–öğle arasında.
- **A4 — Kerahat:** `KerahatKind`, `KerahatWindow`, `KerahatStatus`; üç pencere `RuleSet`'ten okunuyor; sınırlar ve `RuleSet`'ten gelen süre test edildi. D1 gereği `M23_kerahatDurumuNamazGunuDisindaReddedilir` eklendi.
- **A5 — `prayerDateOf`:** Namaz günü imsakta başlıyor; bölge ayrı `ZoneId` parametresinden geliyor; `clock.zone` kullanılmıyor.
- **A6 — Yaz saati:** `Europe/Berlin` için ilkbahar ve sonbahar geçişleri; yatsı pencereleri 8 ve 10 saat, namaz günü ve geçiş günü vakitleri UTC olarak doğrulandı.
- **Bölüm C — F2-01 temizlikleri:** KT kapısının okuma hatası ayrı mesaj veriyor; ek uyumu düzeltildi; `PrayerTimesDayTest` tek `baseDay()` yardımcısına indirildi.

### Değişen dosyalar

- `core/domain/src/main/kotlin/com/eceakin/namaztakibi/domain/times/TimeWindow.kt` (yeni)
- `.../times/PrayerDayWindows.kt` (yeni)
- `.../times/CurrentVakit.kt` (yeni)
- `.../times/KerahatWindow.kt` (yeni)
- `.../times/PrayerDate.kt` (yeni)
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/times/TimeWindowTest.kt` (yeni)
- `.../times/PrayerDayWindowsTest.kt` (yeni)
- `.../times/CurrentVakitTest.kt` (yeni)
- `.../times/KerahatTest.kt` (yeni)
- `.../times/PrayerDateTest.kt` (yeni)
- `.../times/DaylightSavingTest.kt` (yeni)
- `build.gradle.kts`
- `core/domain/src/test/kotlin/com/eceakin/namaztakibi/domain/times/PrayerTimesDayTest.kt`
- `docs/tasks/F2-02-plan.md` (yeni), `docs/tasks/F2-02-rapor.md` (yeni)
- İlk commit'te: `CLAUDE.md`, `docs/mimari-ve-kodlama-ilkeleri.md`, `docs/gelistirme-yol-haritasi.md`, `docs/tasks/F2-02-vakit-hesaplari.md`

`.github/workflows/ci.yml` değişmedi. F1-05 dosyaları commit edilmedi.

### Testler

`.\gradlew.bat :core:domain:test` → **110 test, 0 hata**. Dosya dosya:

| Test dosyası | Test sayısı |
| --- | --- |
| `PrayerTest` (F2-01) | 2 |
| `RuleSetTest` (F2-01) | 22 |
| `ReferenceDayTest` (F2-01) | 12 |
| `PrayerTimesDayTest` (F2-01) | 10 |
| `TimeWindowTest` | 3 |
| `PrayerDayWindowsTest` | 16 |
| `CurrentVakitTest` | 16 |
| `KerahatTest` | 16 |
| `PrayerDateTest` | 8 |
| `DaylightSavingTest` | 5 |
| **Toplam** | **110** |

Her A adımında (A6 hariç) test önce yazıldı ve kırmızı/derlenmeyen görüldü:
- A1: `Unresolved reference 'TimeWindow'`.
- A2: `Unresolved reference 'PrayerDayWindows'`.
- A3: `Unresolved reference 'CurrentVakit'`.
- A4: `Unresolved reference 'KerahatKind'` (D1 testi dahil).
- A5: `Unresolved reference 'prayerDateOf'`.
- A6: Testler ilk çalıştırmada yeşildi (5/5). Yalnızca ktlint `function-signature` biçim uyarıları çıktı; iki yardımcı fonksiyonun ifade gövdesi düzenlendi. Test beklentileri ve üretim kodu değişmedi.

### Kapsam (JaCoCo)

| Sayaç | Kapsanan | Kaçan | Oran |
| --- | --- | --- | --- |
| Satır (LINE) | 183 | 0 | **%100** |
| Dal (BRANCH) | 109 | 0 | **%100** |
| Talimat (INSTRUCTION) | 1383 | 6 | %99,6 |
| Yöntem (METHOD) | 63 | 2 | %96,9 |
| Sınıf (CLASS) | 14 | 0 | %100 |

Kapsam kapısı yeşil; hiçbir sınıf/paket ölçümden hariç tutulmadı.

### Bölüm C negatif denemeleri

1. `ktPending`'e geçici `KT-14` eklendi:
   `KT kapısı: KT-14 karar tablosunda geçerli bir satır değil (kaldırılmış ya da yok). Planlamaya bildir.`
2. `RuleSetTest`'e geçici `@Test fun KT01_kapiDenemesi() {}` eklendi:
   `KT kapısı: KT-01 kimliğinin testi var ama bekleyen listede duruyor. Kimliği ktPending'den sil.`
3. Tablo yolu geçici olarak `docs/karar-tablosu-yok.md` yapıldı:
   `KT kapısı: docs/karar-tablosu.md okunamadı: C:\Users\eceak\AndroidStudioProjects\NamazTakibiApp\docs\karar-tablosu-yok.md (Sistem belirtilen dosyayı bulamıyor).`

Her denemeden sonra yalnızca değiştirilen dosya `git restore <dosya>` ile geri alındı; her geri almadan sonra `git diff` boştu. `git restore .`, `git checkout -- .`, `git reset` ve `git stash` kullanılmadı. Son `git status` yalnızca F1-05'in iki izlenmeyen dosyasını gösteriyor.

### Son doğrulama

- `.\gradlew.bat clean` → başarılı.
- `.\gradlew.bat ciCheck` → **başarılı (1 dk 35 sn)**.
- `.\gradlew.bat :core:domain:jacocoTestReport` → başarılı.
- `.\gradlew.bat checkKtTraceability` → `KT izlenebilirlik: 101 geçerli satır, 0 testli, 101 bekliyor, fark yok.`
- `.\gradlew.bat help` → başarılı (M-08 bozulmadı).
- Lint kapısı: `Lint kapısı: bilinen 6 uyarı eşleşti, fark yok.`
- `git status` → yalnızca `docs/tasks/F1-05-plan.md` ve `docs/tasks/F1-05-risk-prototipi.md` izlenmeyen.
- `grep -rnE "android\.|Instant\.now|LocalDate\.now|LocalDateTime\.now|ZoneId\.systemDefault|System\.currentTimeMillis|!!" core/domain/src/main` → **çıktı yok**.
- `grep -rn "\.zone\b" core/domain/src/main` → **çıktı yok**.

### Karşılanan kimlikler

BR-02, BR-03, BR-04, BR-07, FR-TRK-05, NFR-CMP-02, M-23, M-24, K-03, K-12, K-15, K-16, K-17, DATA-03, KS-TRK-11. Bu kartta hiçbir KT satırının testi yazılmadı; `ktPending` değişmedi (101 kimlik).

### Varsayımlar

- Dosya adları seçildi: `TimeWindow.kt`, `PrayerDayWindows.kt`, `CurrentVakit.kt`, `KerahatWindow.kt`, `PrayerDate.kt`. Karttaki imzalardan sapma yok.
- `M24_baslangicBitistenOnceOlmali` tek `@Test` içinde iki `assertThrows` ile yazıldı; kart bu test için parametreli biçim istemiyordu.
- `M23_namazGunuDisindakiAnReddedilir` ve D1 testi iki `argumentSet`'li parametreli test olarak yazıldı; bozuk çağrılar `Executable` içinde.
- D1'deki "KerahatTest 15" ifadesi ile iki `argumentSet`'in ayrı sayılmasıyla oluşan 16 arasında sayım farkı var; 64 yeni ve 110 toplam sayıları 16 ile tutarlı.
- A6 fikstür yardımcısı test dosyasında; `ReferenceDay` değişmedi. Beklenen anlar `Instant.parse("...Z")` ile UTC olarak yazıldı.
- D2'nin `.zone` aramasının boş çıkması için `PrayerDate.kt` KDoc'undaki `clock.zone` ifadesi "saat nesnesinin bölgesi" olarak yazıldı; tek satırlık yorum düzeltmesi rapor commit'ine dahil edildi (10 commit sırası korundu).

### Belge tutarsızlıkları

- D1 onay mesajındaki "KerahatTest 15" ifadesi, iki `argumentSet`'in ayrı çalıştırma sayılmasıyla 16 olur; 64 ve 110 toplamları 16 ile tutarlıdır. Plan ve rapor bu toplamları esas aldı.
- Kart, SRS ve karar tablosu arasında pencere değerlerinde çelişki bulunmadı.

### Açık sorular

Yok.

### Commit'ler

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

Push edilmedi, PR açılmadı.

PR başlığı: feat(domain): add prayer time windows and prayer date (F2-02)

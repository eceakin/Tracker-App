# Namaz Takibi

Reklamsız, hesapsız, çevrimdışı öncelikli, widget öncelikli bir namaz ve kaza takip uygulaması. v1.0 kapsamı yalnızca Android (minSdk 26), yalnızca Hanefî mezhebi, vakit kaynağı Diyanet ve Türkçe arayüzdür. Uygulamanın kalbi, saf Kotlin ile yazılan ve veriyi kullanıcı onayı olmadan değiştirmeyen fıkhi kural motorudur.

## Durum

Geliştirme aşamasındadır (Faz 1 — mimari ve proje iskeleti). Yayınlanmamıştır.

## Modüller

| Modül | İçerik | Bağımlı olabileceği modüller |
| --- | --- | --- |
| `:core:domain` | Varlıklar, kural motoru, arayüzler, use case'ler; saf Kotlin | Hiçbiri |
| `:core:data` | Room, DataStore, repository uygulamaları, yedekleme | `:core:domain` |
| `:core:prayertimes` | Vakit kaynağı istemcisi, önbellek, Adhan yedeği | `:core:domain` |
| `:feature:*` | Onboarding, Bugün, Takvim, Kaza, Mazeret, Ayarlar ekranları | `:core:domain` |
| `:widget` | Glance widget'ları | `:core:domain`, `:core:data` |
| `:app` | Hilt, navigasyon, bildirim ve alarm altyapısı | Hepsi |

Bu bağımlılık kuralları derleme sırasında denetlenir: kurala aykırı bir `project(...)` bağımlılığı kök `build.gradle.kts`'teki M-08 denetimi tarafından, tarafları ve izin verilenleri adlandıran bir mesajla durdurulur.

## Derleme

JDK 17 ya da üstü gerekir.

Windows:

```
.\gradlew.bat assembleDebug
.\gradlew.bat test
.\gradlew.bat ktlintCheck
.\gradlew.bat lintDebug
.\gradlew.bat ciCheck
```

macOS / Linux:

```
./gradlew assembleDebug
./gradlew test
./gradlew ktlintCheck
./gradlew lintDebug
./gradlew ciCheck
```

`ciCheck` tek komutta ktlint, birim testleri, Android Lint kapısı ve debug derlemesini çalıştırır. GitHub Actions CI'ı her Pull Request'te ve `main`'e yapılan her birleştirmede bu komutu çalıştırır; PR açmadan önce yerelde de aynı komut çalıştırılmalıdır.

## Belgeler

- `docs/gereksinim-analizi-srs.md` — gereksinimler (SRS), tek doğruluk kaynağı.
- `docs/karar-tablosu.md` — kural motorunun test sözleşmesi (KT-01 – KT-102).
- `docs/kabul-senaryolari.md` — kullanıcı gözünden kabul senaryoları.
- `docs/mimari-ve-kodlama-ilkeleri.md` — mimari ve kodlama kuralları (M/P/K).
- `docs/gelistirme-yol-haritasi.md` — fazlar ve "bitti tanımı".
- `docs/fikhi-gereksinimler.md`, `docs/hocaya-sorular.md` — fıkhi gerekçeler ve hoca görüşleri.
- `docs/ekran-taslaklari-tur1.md` – `tur5.md` — ekran taslakları ve mikro metinler.
- `docs/tasks/` — görev kartları ve görev sonu raporları.
- `CLAUDE.md` — kodlama ajanının bağlayıcı çalışma talimatı.

## Çalışma akışı

`main`'e doğrudan commit atılmaz. Her görev kısa ömürlü bir dalda (`feat/`, `fix/`, `test/`, `chore/`) yapılır, GitHub'da Pull Request ile ve "Squash and merge" yöntemiyle birleştirilir.

`main` dalı GitHub ruleset'iyle korunur: doğrudan push yapılamaz ve birleştirme yalnızca Pull Request ile, CI yeşilken (zorunlu `ciCheck` durum kontrolü) mümkündür.

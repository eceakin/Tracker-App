# 5te5 Namaz Takibi — Kullanıcı Yorumu Analizi (Ekim 2026)

Kaynak: Google Play yorumları — genel_yorumlar (780, "en alakalı" sıralama, yıldız bilgisi yok), 1 yıldız (106), 2 yıldız (31), 3 yıldız (65). Çakışmalar çıkarılınca 962 tekil yorum.

## Veri notları
- Genel yorumlar ağırlıkla 2026 tarihli ve çok olumlu; 1–3 yıldızlılar çoğunlukla 2019–2025. Eski şikayetlerin bir kısmı (ör. Instagram ile kayıt) yeni sürümde çözülmüş görünüyor.
- Sayılar anahtar kelime eşleştirmesiyle yaklaşık hesaplandı; kesin oran değil, öncelik sıralaması için kullanılmalı.

## Neden seviliyor
- Ürün bir "takip aracı" değil, 40 günlük alışkanlık programı. Hedef: namaza yeni başlayan/bırakıp başlayan, çok sayıda genç (11–15 yaş) ve aile/çocuk kullanımı.
- En çok övülen: günün hediyesi yazı+video (~185), namaza başlattı/düzen getirdi (~180), ağaç/bahçe oyunlaştırması (~80), reklamsız/ücretsiz (~38).
- Dağıtım avantajı: Sözler Köşkü YouTube/Instagram kitlesi.

## Şikayet ve istekler (öncelik sırasıyla)
1. **40 gün sınırı** (~66 yorum, en yüksek faydalı oy, 2026'da artıyor) — sınırsız takip, 80/120/160 gün serileri, eski ağaçların korunması isteniyor; kullanıcılar silip yeniden yüklüyor.
2. **Açılmama / gri-siyah ekran / çökme** (~80) ve **veri kaybı** (~20) — 2023–2025 yoğun; çözüm "verileri sil" olunca tüm kayıt gidiyor; yedekleme isteniyor.
3. **Kaza takibi** (~17 yorum, yüksek faydalı oy) — borç miktarı girip kıldıkça düşen sayaç, geriye dönük işaretleme; bazıları kaza için ikinci uygulama kullanıyor.
4. **Bildirimler** — ezan bildirimi yok/çalışmıyor; vakit çıkmadan "kıldın mı?" hatırlatması, kerahat uyarısı, vakit başına ayrı ayar isteniyor; aktif kullanıcıya "10 gündür yoksun" gibi hatalı bildirimler; ton olarak "korkutma, sevdir".
5. **Kadın/regl (mazeret) modu** (~21) — seri bozuluyor, motivasyon düşüyor; daha önce varmış, kaldırılmış.
6. **Yanlış işaret geri alınamıyor + vakti gelmemiş namaz işaretlenebiliyor** (~17) — ilk açılışta sabah namazını zorla işaretletiyor.
7. **Vakit/konum/kıble güvenilirliği** — şehir kaydedilmiyor, Ankara'ya dönüyor (bir kullanıcı bütün gün erken kılmış), yanlış sabah saati, yanlış kıble. Az ama en ağır sonuçlu.
8. **Tarih** — Ekim 2026 güncellemesinde gün tarihi kaldırılmış; başlangıç tarihi seçilemiyor.
9. **Widget / bildirim çubuğunda vakitler** (~12).
10. **Zorunlu hesap / Instagram kullanıcı adı / doğum tarihi** — 1 yıldızların en büyük grubu (~35) ama 2019–2022; hesap silinemiyordu.
11. Diğer: Türkçe meal/anlam (~30), çoklu profil (anne+çocuk), zikirmatik iyileştirmeleri, toplu işaretlemede atlanamayan animasyon, vakit bazlı analiz ("hangi vakti kaçırıyorum").

## Kendi uygulamamız için çıkarımlar
- Rakibin hendeği içerik + kitle; onunla yarışma. Boşluk: **40 gün sonrası kalıcı takip ve kaza**.
- Konumlandırma fikri: "40 günü bitirdin, şimdi ömür boyu devam" — 5te5'i bitirenler doğal hedef kitle.
- MVP önerisi: sınırsız tarihli takip + takvim; kaza borcu sayacı (manuel giriş/hesaplayıcı); kaçırılan vakti kazaya ekleme; geri al + gelecek vakti engelleme; mazeret modu; kaydedilen konum + doğru vakit kaynağı; işaretlenmemiş vakit için akıllı hatırlatma; ilk günden yedek/dışa aktarma; hesap zorunlu değil; reklamsız.
- Widget tek başına fark değil (rakip de ekleyebilir); fark widget'tan tek dokunuşla işaretleme + kaza sayacı olabilir.
- Offline-first: yerel veri tek kopyaysa rakibin veri kaybı sorunu tekrarlanır → yedekleme ve migration testleri kritik.

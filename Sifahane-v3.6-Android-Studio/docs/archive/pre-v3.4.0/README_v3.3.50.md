# Şifahane v3.3.50

## Yeni geliştirme: “Alındı” kaydını geri alma

- Bugün sayfasında **ALINDI** durumundaki ilaç kartına dokunulduğunda **ALINDI KAYDINI GERİ AL** seçeneği gösterilir.
- İşlem ikinci bir onay penceresi olmadan uygulanmaz.
- Geri alınan doz yeniden **Henüz Alınmadı** durumuna döner.
- “İlacı aldım” sırasında stoktan gerçekten bir adet düşülüp düşülmediği doz kaydında tutulur.
- Stok azaltılmışsa geri alma sırasında stok otomatik olarak bir artırılır; azaltılmamışsa stok değiştirilmez.
- Önceki sürümlerde oluşturulan ve stok hareketi kesin bilinmeyen kayıtlar için kullanıcıya “Stoka 1 adet geri ekle” seçimi sunulur.
- Alındı kaydı silinmez; **ALINDI GERİ ALINDI** düzeltme hareketi oluşturularak rapor geçmişi korunur.
- Doz kaydı ve stok düzeltmesi tek bir veritabanı işlemi içinde gerçekleştirilir.
- Yedek formatı v3'e yükseltilmiştir; eski v1/v2 Şifahane yedekleri içe aktarılmaya devam eder.
- Alarm zamanlama ve alarm yenileme altyapısında değişiklik yapılmamıştır.

## Teknik bilgiler

- Uygulama sürümü: **3.3.50**
- Sürüm kodu: **335000**
- Veritabanı: **9 → 10**, veri kayıpsız Room migration
- Temel alınan kararlı sürüm: **3.3.49.2.11**

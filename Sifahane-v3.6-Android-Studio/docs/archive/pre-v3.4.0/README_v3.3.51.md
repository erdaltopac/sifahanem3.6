# Şifahane v3.3.51

## Grup ilaç alarmı

- Aynı tarih, saat ve dakikadaki ilaçlar için tek Android alarmı, tek bildirim ve tek alarm sesi oluşturulur.
- Grup alarm kartı bütün ilaçları listeler.
- Farklı kullanıcılara ait ilaçlar aynı alarmda kullanıcı sekmeleriyle ayrılır.
- Her ilaç için ayrı ayrı **Aldım**, **5 dk ertele**, **10 dk ertele** ve **Almayacağım** işlemleri yapılabilir.
- Seçili kullanıcı sekmesinde aynı işlemler topluca uygulanabilir.
- Toplu alma işleminde stok ve doz geçmişi her ilaç için ayrı, tek Room transaction içinde güncellenir.
- Kısmen tamamlanan grupta yalnız işlem bekleyen ilaçlar ekranda kalır.
- Ertelenen ilaçlar yeni tetik dakikasında yeniden tek grup alarmına dönüştürülür.
- Normal, geçmiş ve ertelenmiş dozlar grup planlamasını kullanır.
- Telefon açılışı, uygulama güncellemesi, tarih/saat ve saat dilimi değişikliklerinde gruplar yeniden kurulur.

## Teknik bilgiler

- Sürüm: **3.3.51**
- Sürüm kodu: **335100**
- Veritabanı: **v10** (şema değişmedi, migration gerekmez)
- Temel alınan sürüm: **3.3.50**
- Minimum Android: **API 26**

## Doğrulama

- Kotlin debug derlemesi başarılı.
- 3 grup kimliği birim testi başarılı.
- Debug APK üretimi başarılı.
- Android Lint: 0 hata (mevcut uyarılar engelleyici değildir).

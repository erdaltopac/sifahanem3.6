# Şifahane v3.3.52

## Bu sürümde

- Alarm kartında Ayarlar'dan değiştirilebilen üç erteleme süresi bulunur (varsayılan 5/10/15 dk).
- Tekil ve toplu erteleme aynı üç seçenekli pencereyi kullanır.
- Hareketsizlik sonrası profil desen kilidi süresi Ayarlar'dan belirlenir.
- İlk kurulumda 12345 doğrulaması, yeni yönetici şifresi, kişi içe aktarma seçimi ve desen oluşturma akışı uygulanır.
- Her sürüm güncellemesinin ilk açılışında sabit 12345 doğrulanır ve Kişiler ekranı açılır.
- Tek kalan yönetici profilinin silinmesi engellenir; profil silme özel yönetici doğrulaması gerektirir.
- Birincil yedek Şifahane Yedek klasörüne yazılır ve ikinci kopya isteğe bağlı olarak sorulur.
- Bildirim ve alarm sağlık simgesi hilal olarak değiştirildi.
- Genel tema beyaz, düşük opaklıklı vantablack, logo yeşili ve Türk kırmızısına göre merkezileştirildi; tipografi ailesi birleştirildi.
- Grafik raporlarına tarih ve ölçüm değeri eksen etiketleri eklendi.
- Yardım kılavuzu genişletildi; Türkçe arama ve seçili konuyu Android paylaşım ekranıyla harici yapay zekâya gönderme eklendi.
- Sistem Geri tuşu uygulama içi geri davranışıyla eşleştirildi; alarm ekranı işlemsiz kapatılamaz.
- Kamera ve Android 26 uyumluluk Lint yapılandırmaları korunur.

## Teknik bilgiler

- Sürüm adı: **3.3.52**
- Sürüm kodu: **335200**
- Veritabanı: **v10** (şema değişikliği yok)
- Temel sürüm: **3.3.51**
- Minimum Android: **API 26**

## Depolama notu

Modern Android güvenliği nedeniyle ilk yedeklemede kullanıcıdan dahili depolama konumunu bir kez seçmesi istenir. Uygulama seçilen konumda `Şifahane Yedek` klasörünü oluşturur ve sonraki yedekleri otomatik kaydeder.

## Gizlilik

“Yapay zekâya sor” yalnız uygulama sürümünü ve seçilen yardım metnini paylaşır. Profil, ilaç, ölçüm veya diğer sağlık kayıtları eklenmez.

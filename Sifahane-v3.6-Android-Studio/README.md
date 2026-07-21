# Şifahane v3.4.0

Bu kaynak paketi, **Şifahane v3.3.64** Android Studio projesi üzerinde güvenlik, veri koruma, yedekleme, alarm kimliği ve kalite altyapısı odaklı ilk v3.4.0 geliştirme paketidir.

## Bu pakette tamamlanan ana değişiklikler

- Kod içine gömülü varsayılan yönetici PIN'i kaldırıldı. İlk açılışta kullanıcı kendi 4–12 haneli PIN'ini oluşturur.
- Yönetici PIN'i ve kullanıcı desenleri benzersiz salt içeren PBKDF2-HMAC-SHA256 ile saklanır. Eski özetler ilk başarılı doğrulamada yükseltilir.
- Hatalı PIN/desen denemelerine yeniden başlatmadan etkilenmeyen artan bekleme süresi eklendi.
- Room veritabanı SQLCipher ile şifrelenir; veritabanı anahtarı Android Keystore tarafından sarılarak korunur. Mevcut düz SQLite veritabanı için geri alınabilir tek seferlik geçiş kodu eklendi.
- Yeni yedekler parola türetilmiş AES-256-GCM ile `.sifbackup` biçiminde şifrelenir. Yanlış parola ve dosya değişikliği reddedilir; eski ZIP yedekleri yalnız içe aktarma uyumluluğu için açılır.
- ZIP giriş sayısı, boyut, yol, sıkıştırma oranı ve toplam açılmış veri sınırları eklendi.
- Uygulamanın Android otomatik yedeklemesi kapatıldı; hassas ekranlar `FLAG_SECURE` ile korundu.
- İlaç ve randevu alarmlarında çakışmaya dayanıklı, kalıcı PendingIntent kimliği kullanıldı; çift tam ekran başlatma yolu kaldırıldı.
- Sürüm katalogu, Gradle wrapper önyükleyicisi, `.gitignore`, GitHub Actions kalite kapısı ve yeni birim testleri eklendi.

## Doğrulama durumu

Bu çalışma ortamında Android SDK bulunmadığı için tam `assembleDebug`, `lint` ve cihaz/emülatör testleri çalıştırılamadı. Android'den bağımsız güvenlik ve yedekleme çekirdeği JVM üzerinde doğrulandı; ayrıntılar `DEGISIKLIK_VE_TEST_RAPORU_v3.4.0.md` içindedir.

## Açılış ve derleme

1. ZIP'i ayrı bir klasöre çıkarın.
2. Android Studio'da bu klasörü **Open** ile açın.
3. JDK 17 seçili olmalıdır.
4. Gradle Sync tamamlandıktan sonra önce `testDebugUnitTest`, sonra `lintDebug` ve `assembleDebug` çalıştırın.
5. Mevcut uygulamayı kaldırmadan APK'yı güncelleme olarak kurun. Uygulamayı kaldırmak yerel verileri silebilir.

## Sürüm bilgileri

- `versionName`: `3.4.0`
- `versionCode`: `340000`
- Room veritabanı: `10`
- Minimum Android: API 26
- Hedef Android: API 35

Eski sürüm notları `docs/archive/pre-v3.4.0/` altında arşivlenmiştir. Bundan sonraki değişikliklerin tek kaynağı `CHANGELOG.md` dosyasıdır.

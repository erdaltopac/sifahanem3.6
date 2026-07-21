# Değişiklik Günlüğü

Bu dosya v3.4.0 ve sonraki sürümlerin tek değişiklik günlüğüdür. Önceki dağınık sürüm belgeleri `docs/archive/pre-v3.4.0/` altında korunur.

## 3.4.0 — 2026-07-21

### Güvenlik
- Varsayılan `12345` yönetici PIN'i ve arka kapı davranışı kaldırıldı.
- PIN/desen saklama PBKDF2-HMAC-SHA256, benzersiz salt ve sabit zamanlı karşılaştırmaya geçirildi.
- Kalıcı artan deneme bekleme süresi eklendi.
- Room için SQLCipher, Android Keystore korumalı rastgele anahtar ve düz veritabanından şifreli veritabanına geçiş eklendi.
- Android otomatik yedekleme kapatıldı ve hassas ekranlarda ekran görüntüsü engellendi.

### Yedekleme
- AES-256-GCM korumalı `.sifbackup` kapsayıcısı eklendi.
- Yanlış parola, kimlik doğrulama etiketi bozulması, zip-slip, zip-bomb ve aşırı giriş sınırları eklendi.
- Eski açık ZIP yedekleri yalnız içe aktarma için desteklenmeye devam eder.

### Alarm
- İlaç ve randevu PendingIntent kimlikleri kalıcı, çakışmaya dayanıklı kayıt sistemine geçirildi.
- Eski istek kodlarının temizlenmesi korunarak yeni kimlikler eklendi.
- Kilit ekranı bildirimleri özel yapıldı ve çift tam ekran Activity başlatma yolu kaldırıldı.

### Yapı ve kalite
- Version catalog, Gradle wrapper önyükleyicisi, temiz `.gitignore` ve GitHub Actions kalite kapısı eklendi.
- Güvenlik, yedekleme sınırları, AES-GCM bütünlüğü ve alarm kimliği için birim testleri eklendi.
- Deterministik `BUILD_TIME_MILLIS` ve v3.4.0 sürüm numarası tanımlandı.

# Şifahane Güvenlik Mimarisi — v3.4.0

## Kimlik bilgileri
- Yönetici PIN'i ve profil desenleri PBKDF2-HMAC-SHA256 ile türetilir.
- Her kayıt için 128 bit rastgele salt ve 210.000 yineleme kullanılır.
- Eski SHA-256 kayıtları yalnız geçiş amacıyla doğrulanır ve ilk başarılı girişte yeni biçime yükseltilir.
- Beşinci hatadan sonra başlayan bekleme süreleri cihaz yeniden başlatılsa da korunur.

## Veritabanı
- Room, SQLCipher `SupportOpenHelperFactory` üzerinden açılır.
- 256 bit rastgele veritabanı parolası cihaz içinde oluşturulur.
- Bu parola Android Keystore AES-GCM anahtarıyla sarılır; açık parola tercihlerde tutulmaz.
- Düz SQLite dosyası algılanırsa geçici şifreli dosyaya `sqlcipher_export` yapılır; doğrulamadan sonra atomik ad değiştirme uygulanır.

## Yedek
- Yeni yedek biçimi: `SIFAHANE-AESGCM1` başlığı + PBKDF2 parametreleri + AES-GCM şifreli ZIP akışı.
- Parola: 8–128 karakter, 310.000 PBKDF2 yinelemesi, 128 bit salt, 96 bit IV, 128 bit GCM etiketi.
- Uygulama parolayı kalıcı olarak saklamaz.
- İçe aktarma; yol geçişi, mutlak yol, yinelenen giriş, aşırı sıkıştırma, aşırı dosya sayısı ve boyut sınırlarını denetler.

## Platform
- `allowBackup=false`; bulut/ADB veri çıkarımı kuralları tüm uygulama alanlarını hariç tutar.
- Ana uygulama ekranı `FLAG_SECURE` kullanır.
- Alarm bildirimlerinin kilit ekranındaki genel sürümü ilaç adı veya kişi bilgisi içermez.

## Kalan doğrulamalar
- SQLCipher düz-veri geçişi gerçek v3.3.64 veritabanı kopyasıyla cihazda test edilmelidir.
- Keystore anahtar kaybı, cihaz kilidi değişimi ve üreticiye özgü yedek davranışı test edilmelidir.
- Kaynak taraması, bağımlılık zafiyet taraması ve imzalı release derlemesi CI/Android Studio ortamında tamamlanmalıdır.

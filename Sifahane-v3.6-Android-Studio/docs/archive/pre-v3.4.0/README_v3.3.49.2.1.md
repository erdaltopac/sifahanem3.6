# Şifahane v3.3.49.2.1

## Derleme hata düzeltmesi

- `AppointmentScreen.kt` içindeki randevu kartı, `MainActivity.kt` dosyasında `private` kapsamda bulunan `LogoColor` değişkenine erişmeye çalışıyordu.
- Randevu ekranına dosya içi `AppointmentAccentColor` tanımı eklendi ve özel kapsam ihlali kaldırıldı.
- Android Studio ekranında görünen `Cannot access val LogoColor: Color: it is private in file` hatası giderildi.
- Sürüm kodu `334921`, sürüm adı `3.3.49.2.1` olarak güncellendi.
- Kaynak ayraçları, yeni randevu dosyalarındaki çapraz özel erişimler ve ZIP bütünlüğü statik olarak kontrol edildi.

> Bu ortamda Android SDK/Gradle derlemesi bulunmadığından proje Android Studio ile gerçek APK derlemesine tabi tutulamamıştır.

# Şifahane v3.3.49.1

## Derleme bakım düzeltmesi

Android Studio'da görülen hata zincirinin kaynağı giderildi:

- Projede kullanılabilir olmayan `rememberSaveable` çağrıları, mevcut Compose Runtime ile uyumlu `remember` çağrılarına dönüştürüldü.
- Buna bağlı oluşan `when expression must be exhaustive`, `Unresolved reference: it` ve `@Composable invocations can only happen...` zincirleme hataları giderildi.
- Ayarlar alt sayfaları, yardım araması, büyük metin ve yüksek kontrast işlevleri korunmuştur.
- Room veritabanı sürümü değiştirilmemiştir.

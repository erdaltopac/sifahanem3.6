# Şifahane v3.3.49.2.6

## RectangleShape paket düzeltmesi

Android Studio tarafından bildirilen iki hata:

- `Unresolved reference: RectangleShape` (import satırı)
- `Unresolved reference: RectangleShape` (gölge katmanı kullanımı)

giderildi.

Düzeltme:

Yanlış paket:
`androidx.compose.foundation.shape.RectangleShape`

Doğru paket:
`androidx.compose.ui.graphics.RectangleShape`

Alt menünün güvenli alanı, 10 dp ek boşluğu, 0.5 dp bordürü,
düşük opaklıklı siyah gölgesi, cam görünümü, seçili sekme yükseltisi,
spring animasyonu, kaydırma ve sürükle-bırak işlevleri korunmuştur.

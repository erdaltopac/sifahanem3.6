# v3.4.0 Teknik Release Kontrolü

- [ ] `./gradlew testDebugUnitTest`
- [ ] `./gradlew lintDebug`
- [ ] `./gradlew assembleDebug`
- [ ] `./gradlew assembleRelease` (üretim imzası olmadan doğrulama)
- [ ] Room şema JSON'ları `app/schemas/` altında oluştu ve incelendi
- [ ] Mevcut v3.3.64 veritabanıyla SQLCipher geçişi doğrulandı
- [ ] Yeni şifreli yedek round-trip testi cihazda geçti
- [ ] Eski v1/v2/v3 ZIP yedek örnekleri kontrollü içe aktarıldı
- [ ] Yanlış parola, tek bayt değişiklik, zip-slip ve zip-bomb örnekleri reddedildi
- [ ] Android 12–16 alarm test matrisi tamamlandı
- [ ] Büyük yazı, TalkBack, 48 dp hedef ve kontrast denetimi tamamlandı
- [ ] APK/AAB SHA-256 hesaplandı
- [ ] İmzalı değişiklik ve test raporu arşivlendi

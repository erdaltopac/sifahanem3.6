# Şifahane v3.4.0 — Değişiklik ve Test Raporu

**Tarih:** 21 Temmuz 2026  
**Kaynak tabanı:** Şifahane v3.3.64 Android Studio  
**Hedef:** Şifahane v3.4.0 güvenlik ve kalite temeli

## 1. Sonuç özeti

Bu teslim, 109 maddelik geliştirme listesinin tamamı değildir. İlk paket; kritik güvenlik, veritabanı/yedek koruması, alarm istek kimliği, yeniden üretilebilir yapı ve test altyapısına odaklanır. Büyük mimari ayrıştırma, tam UI/UX dönüşümü, erişilebilirlik otomasyonu ve fiziksel Android 12–16 cihaz kabulü sonraki paketlerde kalmıştır.

Dosya farkı: **64 yeni**, **19 değiştirilmiş**, **36 taşınmış/kaldırılmış** dosya.

## 2. Uygulanan maddeler

### Güvenlik ve gizlilik

| Madde | Durum | Uygulama |
|---|---|---|
| A01 | Kod tamamlandı, cihaz doğrulaması bekliyor | Room SQLCipher ile açılıyor; 256 bit rastgele veri anahtarı Android Keystore AES-GCM anahtarıyla sarılıyor; düz SQLite için geri alınabilir `sqlcipher_export` geçişi eklendi. |
| A02 | Kod ve çekirdek test tamamlandı, Android entegrasyonu bekliyor | Yeni `.sifbackup` kapsayıcısı PBKDF2 + AES-256-GCM kullanıyor; yanlış parola ve değiştirilmiş veri reddediliyor; eski ZIP yalnız içe aktarma için kabul ediliyor. |
| A03 | Tamamlandı | Varsayılan `12345` PIN ve arka kapı akışı kaldırıldı; ilk açılışta kullanıcı kendi PIN'ini oluşturuyor. |
| A04 | Tamamlandı | PIN/desen için benzersiz salt, PBKDF2-HMAC-SHA256 ve sabit zamanlı karşılaştırma; eski SHA-256 kayıtları ilk başarılı girişte yükseltiliyor. |
| A05 | Tamamlandı | Beşinci hatadan sonra 30 sn, 60 sn, 120 sn, 5 dk ve 15 dk artan, epoch tabanlı kalıcı kilit süresi. |
| A07 | Tamamlandı | `allowBackup=false`, `fullBackupContent=false` ve veri çıkarım kurallarında tüm alanların dışlanması. |
| A08 | Kod tamamlandı | Ana ekran ve alarm ekranı `FLAG_SECURE`; kilit ekranı bildirimi özel ve genel sürüm kişisel veri içermiyor. Fiziksel cihaz kullanılabilirlik testi bekliyor. |
| A09 | Kısmi | Şifreli yedek paylaşımı öncesi içerik/hedef/parola uyarısı var. Rapor ve diğer açık dışa aktarımların tamamı henüz ortak uyarı katmanına alınmadı. |
| A12 | Kod tamamlandı | CI'a gitleaks ve yüksek önem dereceli GitHub Dependency Review kapısı eklendi. İlk CI çalışması bekliyor. |

### Yapı, veri ve yedekleme

| Madde | Durum | Uygulama |
|---|---|---|
| B08 | Tamamlandı | Bağımlılık ve eklenti sürümleri `gradle/libs.versions.toml` dosyasına taşındı. |
| B09 | Kod tamamlandı | Gradle 8.9 dağıtım SHA-256 doğrulamalı wrapper önyükleyicisi ve platform betikleri eklendi. Temiz makine çalışması CI/Android Studio'da doğrulanmalı. |
| B10 | Kısmi | Temiz `.gitignore` ve CI eklendi; gerçek Git deposu/etiketi bu ZIP içinde oluşturulmadı. |
| C01 | Kısmi | `exportSchema=true` ve `room.schemaLocation` ayarlandı; şema JSON'ları Android derlemesiyle üretilmeli. |
| C05 | Kod tamamlandı | Giriş sayısı, tek giriş boyutu, toplam açılmış boyut, yol ve sıkıştırma oranı sınırları eklendi. |
| C06 | Kısmi | AES-GCM katmanı akış halinde çalışıyor; mevcut medya toplama kodu hâlâ bazı dosyaları belleğe alıyor. Tam akış/atomik import dönüşümü kalmıştır. |
| C07 | Kısmi | Geçerli yeni biçim, yanlış parola, değişiklik ve yol sınırı testleri eklendi. v1–v3 gerçek örnek fixture seti henüz eklenmedi. |

### Alarm güvenilirliği

| Madde | Durum | Uygulama |
|---|---|---|
| D01 | Kısmi | İlaç grupları ve randevular kalıcı çakışma araştırmalı PendingIntent kimliğine geçirildi; tüm alarm türlerinin tek üst yaşam döngüsünde birleştirilmesi tamamlanmadı. |
| D02 | Kısmi | Biten grup bildirimi ve PendingIntent'i iptal edilip kayıt serbest bırakılıyor; fiziksel erteleme zinciri matrisi bekliyor. |
| D10 | Kısmi | Receiver kanal sesi kapalı, ses/titreşim AlarmActivity tarafından tek oturumda yönetiliyor; servis tabanlı ortak bileşen dönüşümü kalmıştır. |
| D11 | Kod tamamlandı | API 27+ için `setShowWhenLocked`/`setTurnScreenOn`; eski pencere bayrakları yalnız eski API'de. Çift Activity başlatma yolu kaldırıldı. |

### Test, CI ve dokümantasyon

| Madde | Durum | Uygulama |
|---|---|---|
| I03 | Kısmi | Alarm kimliği ve çakışma araştırması birim testi eklendi; DST/izin/yeniden başlatma otomasyonu kalmıştır. |
| I04 | Kısmi | AES-GCM round-trip, yanlış parola, değişiklik ve arşiv sınırı testleri eklendi; tam Room yedek fixture seti kalmıştır. |
| I09 | Kısmi | GitHub Actions içinde birim test, lint, debug/release derleme, secret scan ve dependency review kapısı var; androidTest ve erişilebilirlik kapıları kalmıştır. |
| K02 | Kısmi | `versionCode=340000`, `versionName=3.4.0`, sabit `SOURCE_DATE_EPOCH` tabanlı build zamanı ve dağıtım checksum'u eklendi. APK/AAB yeniden üretilebilirliği henüz çalıştırılmadı. |
| K04 | Kısmi | PIN, şifreli yedek ve güvenlik yardım metinleri güncellendi; ekran görüntülü tam kılavuz kalmıştır. |
| K05 | Tamamlandı | Tek `CHANGELOG.md` oluşturuldu; eski raporlar `docs/archive/pre-v3.4.0/` altında arşivlendi. |

## 3. Özellikle tamamlanmayan büyük kapsam

- B01–B07: 8.000+ satırlık `MainActivity.kt` henüz özellik bazlı ekran/ViewModel/repository mimarisine ayrılmadı.
- C02–C04: Tüm Room migrasyon testleri, foreign key ve indeks dönüşümü yapılmadı.
- E01–E12, F01–F10, G01–G10: Kapsamlı navigasyon, tema ve erişilebilirlik dönüşümü bu pakete alınmadı.
- H01–H07: APK boyut bütçesi, POI alternatifi, profil ve başlangıç performansı ölçülmedi.
- I05–I12: DAO/transaction cihaz testleri, Compose UI, erişilebilirlik ve screenshot otomasyonu tamamlanmadı.
- X01–X06: Android SDK derleme, lint, cihaz matrisi ve APK/AAB olmadığı için hiçbir blokaj kapısı tam kabul edilmiş sayılmaz.

## 4. Yerel doğrulama

### Geçen kontroller

- `CORE_SELF_TEST_OK`: PBKDF2 salt/doğrulama, kilit süresi, AES-GCM round-trip, yanlış parola, tek bayt değişiklik, arşiv yolu ve alarm kimliği.
- 7 Android XML dosyası iyi biçimli olarak ayrıştırıldı.
- Kaynakta `12345`, `DEFAULT_PIN` veya varsayılan PIN doğrulama işareti bulunmadı.
- Gradle önyükleyici Java kaynağı `javac` ile derlendi ve JAR giriş noktası doğrulandı.
- Değiştirilen Kotlin dosyalarında bağımsız Kotlin parser taramasında sözdizimi tanısı bulunmadı. Android sınıfları mevcut olmadığı için bu taramalar doğal olarak çözümleme hatasıyla sonlandı.

### Çalıştırılamayan kontroller

Bu çalışma ortamında Android SDK/sdmanager/Gradle bağımlılık önbelleği bulunmadığından şunlar çalıştırılamadı:

- `testDebugUnitTest`
- `lintDebug`
- `assembleDebug` ve `assembleRelease`
- Room şema üretimi ve MigrationTestHelper
- Android 12–16 emülatör/cihaz alarm matrisi
- APK/AAB ve imza doğrulaması

## 5. Android Studio'da zorunlu sonraki doğrulama

1. JDK 17 ve Android SDK 35 ile Gradle Sync.
2. `gradlew.bat testDebugUnitTest lintDebug assembleDebug`.
3. v3.3.64 gerçek veritabanı kopyasıyla uygulama içi SQLCipher geçişi.
4. Yeni `.sifbackup` oluşturma, yanlış parola, değiştirilmiş dosya ve geri yükleme testi.
5. `docs/ALARM_TEST_MATRIX.md` içindeki Android 12–16 senaryoları.
6. Sonuç başarılıysa debug APK SHA-256'sının teslim kaydına eklenmesi.

## 6. Dosya özeti

### Yeni dosyalar

```text
.github/workflows/android-quality.yml
.gitignore
CHANGELOG.md
SECURITY.md
app/src/main/java/com/hazerfen/sifahane/alarm/AlarmIdentity.kt
app/src/main/java/com/hazerfen/sifahane/alarm/AlarmRequestCodeRegistry.kt
app/src/main/java/com/hazerfen/sifahane/backup/BackupArchivePolicy.kt
app/src/main/java/com/hazerfen/sifahane/backup/BackupCipherCore.kt
app/src/main/java/com/hazerfen/sifahane/backup/BackupCrypto.kt
app/src/main/java/com/hazerfen/sifahane/backup/BackupValidationException.kt
app/src/main/java/com/hazerfen/sifahane/data/DatabaseKeyManager.kt
app/src/main/java/com/hazerfen/sifahane/data/PlaintextDatabaseMigrator.kt
app/src/main/java/com/hazerfen/sifahane/security/SecureCredentialKdf.kt
app/src/main/res/xml/data_extraction_rules.xml
app/src/test/java/com/hazerfen/sifahane/alarm/AlarmIdentityTest.kt
app/src/test/java/com/hazerfen/sifahane/backup/BackupArchivePolicyTest.kt
app/src/test/java/com/hazerfen/sifahane/backup/BackupCipherCoreTest.kt
app/src/test/java/com/hazerfen/sifahane/security/SecureCredentialKdfTest.kt
docs/ALARM_TEST_MATRIX.md
docs/RELEASE_CHECKLIST.md
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.50.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.51.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.52.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.53.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.54.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.55.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.56.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.57.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.58.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.59.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.60.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.61.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.62.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.63.txt
docs/archive/pre-v3.4.0/DEGISIKLIK_VE_TEST_RAPORU_v3.3.64.txt
docs/archive/pre-v3.4.0/README_v3.3.47.1.md
docs/archive/pre-v3.4.0/README_v3.3.48.1.md
docs/archive/pre-v3.4.0/README_v3.3.48.md
docs/archive/pre-v3.4.0/README_v3.3.49.1.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.1.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.10.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.11.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.2.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.3.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.4.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.5.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.6.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.7.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.8.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.9.md
docs/archive/pre-v3.4.0/README_v3.3.49.2.md
docs/archive/pre-v3.4.0/README_v3.3.49.md
docs/archive/pre-v3.4.0/README_v3.3.50.md
docs/archive/pre-v3.4.0/README_v3.3.51.md
docs/archive/pre-v3.4.0/README_v3.3.52.md
docs/archive/pre-v3.4.0/README_v3.3.53.md
gradle/libs.versions.toml
gradle/wrapper/GradleWrapperMain.java
gradle/wrapper/gradle-wrapper.jar
gradlew
gradlew.bat
test-results/CORE_SELF_TEST.txt
test-results/LOCAL_VALIDATION.txt
```

### Değiştirilen dosyalar

```text
ANDROID_STUDIO_ACILIS_TALIMATI.txt
KURULUM_ADIMLARI.txt
README.md
app/build.gradle.kts
app/proguard-rules.pro
app/src/main/AndroidManifest.xml
app/src/main/java/com/hazerfen/sifahane/AlarmActivity.kt
app/src/main/java/com/hazerfen/sifahane/MainActivity.kt
app/src/main/java/com/hazerfen/sifahane/UserGuideDocument.kt
app/src/main/java/com/hazerfen/sifahane/alarm/AlarmReceiver.kt
app/src/main/java/com/hazerfen/sifahane/alarm/AlarmScheduler.kt
app/src/main/java/com/hazerfen/sifahane/alarm/AppointmentAlarmReceiver.kt
app/src/main/java/com/hazerfen/sifahane/alarm/AppointmentAlarmScheduler.kt
app/src/main/java/com/hazerfen/sifahane/backup/SifahaneBackupManager.kt
app/src/main/java/com/hazerfen/sifahane/data/AppDatabase.kt
app/src/main/java/com/hazerfen/sifahane/security/AccessControl.kt
app/src/main/java/com/hazerfen/sifahane/security/PatternLock.kt
build.gradle.kts
gradle/wrapper/gradle-wrapper.properties
```

### Taşınan/kaldırılan dosyalar

```text
DEGISIKLIK_VE_TEST_RAPORU_v3.3.50.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.51.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.52.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.53.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.54.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.55.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.56.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.57.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.58.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.59.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.60.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.61.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.62.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.63.txt
DEGISIKLIK_VE_TEST_RAPORU_v3.3.64.txt
README_v3.3.47.1.md
README_v3.3.48.1.md
README_v3.3.48.md
README_v3.3.49.1.md
README_v3.3.49.2.1.md
README_v3.3.49.2.10.md
README_v3.3.49.2.11.md
README_v3.3.49.2.2.md
README_v3.3.49.2.3.md
README_v3.3.49.2.4.md
README_v3.3.49.2.5.md
README_v3.3.49.2.6.md
README_v3.3.49.2.7.md
README_v3.3.49.2.8.md
README_v3.3.49.2.9.md
README_v3.3.49.2.md
README_v3.3.49.md
README_v3.3.50.md
README_v3.3.51.md
README_v3.3.52.md
README_v3.3.53.md
```

# Manuel Alarm Test Matrisi — v3.4.0

Her satır için cihaz modeli, Android sürümü, navigasyon modu, izinler, beklenen ve gerçek sonuç kaydedilmelidir.

| Senaryo | Ekran | Kilit | Uygulama | Alarm türü | Beklenen |
|---|---|---|---|---|---|
| 1 | Açık | Açık değil | Ön planda | Tekli ilaç | Tek ekran, tek ses, tek bildirim |
| 2 | Açık | Açık değil | Arka planda | Grup ilaç | Tek grup ekranı, tekrar yok |
| 3 | Kapalı | Kilitli | Arka planda | Tekli ilaç | Tam ekran veya belgeli yüksek öncelikli bildirim |
| 4 | Kapalı | Kilitli | Arka planda | Grup ilaç | Tek ses/titreşim oturumu |
| 5 | Açık | Açık değil | Arka planda | Erteleme | Eski ses/bildirim kapanır, yalnız yeni alarm kurulur |
| 6 | Kapalı | Kilitli | Arka planda | Deneme alarmı | Gerçek doz/stok değişmez, bir kez erteleme |
| 7 | Açık | Açık değil | Arka planda | Randevu | Doğru randevu, tek bildirim |
| 8 | Herhangi | Herhangi | Öldürülmüş | Yeniden başlatma | Geçerli alarmlar bir kez yeniden kurulur |
| 9 | Herhangi | Herhangi | Arka planda | Saat dilimi değişimi | Mükerrer alarm yok, yerel saat politikası tutarlı |
| 10 | Herhangi | Herhangi | Arka planda | Kesin alarm izni yok | Yanıltıcı başarı yok; fallback/ayar akışı görünür |

Minimum cihaz matrisi: Android 12, 13, 14, 15 ve erişilebiliyorsa 16; en az bir Samsung, bir Xiaomi/benzeri OEM ve düşük bellekli cihaz.

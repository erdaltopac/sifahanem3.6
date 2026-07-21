# Şifahane v3.3.48

- Yönetici ve Standart Kullanıcı rolleri eklendi.
- Birden fazla kullanıcı yönetici yapılabilir.
- Son yönetici kaldırılamaz.
- Her yönetici için ayrı 4–12 haneli PIN kaydedilebilir.
- PIN değerleri düz metin yerine SHA-256 özeti olarak saklanır.
- Genel yönetici şifresi uygulama güncellemelerinde artık sıfırlanmaz.
- Veri içeri aktarma ve güvenlik yönetimi yönetici yetkisine bağlandı.
- Room veritabanı 7'den 8'e yükseltildi.
- Mevcut verileri koruyan MIGRATION_7_8 eklendi.

# ŞİFAHANE – SONRAKİ SÜRÜM GELİŞTİRME VE İYİLEŞTİRME KONTROL LİSTESİ

Bu belge, Şifahane v3.3.55 sonrasında planlanan geliştirmelerin birleştirilmiş ve güncellenmiş kontrol listesidir.

## A. ALARM SİSTEMİ

### 1. Deneme alarmı kartı ve erteleme testi

- [ ] “Şifahane Deneme Alarmı” başlığı yatay eksende tam ortalansın.
- [ ] Merkezleme farklı ekran genişlikleri ve yazı ölçeklerinde doğrulansın.
- [ ] Gerçek alarm sistemindeki üç erteleme seçeneği deneme alarmında bulunsun.
- [ ] Süreler Ayarlar sayfasındaki erteleme tercihlerinden okunsun.
- [ ] Erteleme seçildiğinde mevcut ses ve bildirim kapatılsın.
- [ ] Deneme alarmı seçilen sürenin sonunda yalnızca bir kez yeniden tetiklensin.
- [ ] Deneme alarmı gerçek ilaç kayıtlarını, alım durumlarını ve raporları değiştirmesin.
- [ ] Ekran açık/kapalı ve uygulama ön planda/arka planda test edilsin.

### 2. Önceden kalmış alarm isteklerini temizleme

- [ ] Ayarlara “Önceden Kalmış Alarm İsteklerini Sil” seçeneği eklensin.
- [ ] Yalnız Şifahane’ye ait eski alarm, PendingIntent ve bildirimler tespit edilsin.
- [ ] Mümkünse silinecek ve yeniden kurulacak alarm sayıları önceden gösterilsin.
- [ ] İşlemden önce kullanıcıdan onay alınsın.
- [ ] Eski alarm istekleri, bildirimler ve açık kalmış eski alarm ekranları temizlensin.
- [ ] Temizlikten sonra “Alarmları Yenile” otomatik çalışsın.
- [ ] Yalnız güncel ilaç planlarının alarmları yeniden oluşturulsun.
- [ ] Sonuç kullanıcıya sayısal özetle bildirilsin.

### 3. Ekran açıkken alarmın doğrudan açılması

- [ ] Ekran açık ve uygulama arka plandayken tekli ve grup alarmının sessiz bildirimde kalması giderilsin.
- [ ] Sistem izin verdiğinde alarm kartı doğrudan tam ekran açılsın ve ses başlasın.
- [ ] Tam ekran alarm erişimi yoksa gerekli sistem ayarı kullanıcıya gösterilsin.
- [ ] Tekli ve grup alarmı ortak açılış davranışı kullansın.

### 4. Alarm ve erteleme bildirimi yaşam döngüsü

- [ ] Alarm ertelendiğinde önceki bildirim kesin olarak kapatılsın.
- [ ] Eski bildirime dokunmak bitmiş veya ertelenmiş alarmı yeniden açmasın.
- [ ] Her erteleme yalnızca bir yeni alarm oluştursun.
- [ ] Art arda ertelemeler paralel alarm zincirleri oluşturmasın.
- [ ] Alarm, bildirim ve PendingIntent kimlikleri çakışmasın.
- [ ] Tekli ve grup alarmı aynı yaşam döngüsü altyapısını kullansın.

### 5. Grup alarmında tek ses kaynağı

- [ ] Aynı saate ait grup alarmında yalnızca bir ses kaynağı çalışsın.
- [ ] İlaç sayısı alarm sesi sayısını etkilemesin.
- [ ] Ekranın yeniden oluşturulması veya bildirime dokunulması ikinci sesi başlatmasın.
- [ ] Alarm hizmeti tekil çalışma kilidiyle korunsun.
- [ ] Alarm kapanırken ses, titreşim ve hizmet birlikte sonlandırılsın.

### 6. Alarm tanılama bölümü

- [ ] Ayarlara sade bir alarm tanılama bölümü eklensin.
- [ ] Son alarm, son erteleme ve sonraki planlanan alarm zamanı gösterilsin.
- [ ] Alarm/bildirim kimliği ve aktif hizmet durumu gösterilsin.
- [ ] Kesin alarm, tam ekran bildirim, bildirim ve pil optimizasyonu durumları gösterilsin.
- [ ] Tanılama bilgileri kişisel sağlık verileri eklenmeden kopyalanabilsin veya paylaşılabilsin.

## B. UYGULAMA VE SÜRÜM BİLGİLERİ

### 7. Uygulama Hakkında bölümü

- [ ] Bölüm varsayılan temayla uyumlu biçimde yeniden tasarlansın.
- [ ] Version Name ve Version Code gösterilsin.
- [ ] Bilgiler Android derleme yapılandırmasından otomatik okunsun.
- [ ] Derleme türü ve derleme tarihi gösterilsin.
- [ ] Teknik bilgiler tek dokunuşla panoya kopyalanabilsin.

## C. KULLANICI VE GEZİNME AKIŞLARI

### 8. Yeni kişi ekleme akışı

- [ ] Güvenli şekilde vazgeçilebilen kartlara “İptal” düğmesi eklensin.
- [ ] İptal kullanıcıyı uygun önceki ekrana döndürsün.
- [ ] Tamamlanmamış profil, şifre veya desen bilgileri kalıcı kaydedilmesin.
- [ ] Veri girildikten sonra çıkılırsa veri kaybı uyarısı gösterilsin.
- [ ] Elle oluşturma ve içeri aktarma aynı güvenli iptal standardını kullansın.

### 9. Sistem geri tuşu ve gezinme geçmişi

- [ ] Android geri tuşu kullanıcıyı gerçek bir önceki ekrana götürsün.
- [ ] Yardım → Bugün geçişinden sonra geri tuşu Yardım sayfasını açsın.
- [ ] Sekme, ayrıntı ekranı ve iletişim kutularında tutarlı geri davranışı sağlansın.
- [ ] Sistem geri hareketi ve fiziksel geri tuşu aynı geçmişi kullansın.

## D. ANDROID İZİN YÖNETİMİ

### 10. Merkezi Android İzinleri bölümü

- [ ] Ayarlara “Android İzinleri” bölümü eklensin.
- [ ] Her iznin adı, amacı, açıklaması, türü ve gerçek sistem durumu gösterilsin.
- [ ] Bildirim, kesin alarm, tam ekran alarm, pil optimizasyonu, kamera ve dosya/medya erişimleri kapsansın.
- [ ] Uygulamanın kullanmadığı gereksiz izinler talep edilmesin.

### 11. İzinlerin tek tek yönetilmesi

- [ ] Uygun izinler tek tek yönetilebilsin.
- [ ] Doğrudan değiştirilemeyen erişimlerde ilgili sistem ekranı açılsın.
- [ ] Uygulamaya dönüldüğünde izin durumu yeniden okunsun.
- [ ] Verilmeyen izin açıkmış gibi gösterilmesin.

### 12. Toplu izin işlemleri

- [ ] “Tümünü Aç” gerekli izin akışlarını sırayla başlatsın.
- [ ] Sonuçta açık, kapalı ve kullanıcı müdahalesi gereken izinler ayrı gösterilsin.
- [ ] Reddedilmiş izinler için rahatsız edici tekrar istemleri oluşturulmasın.

### 13. “Tümünü Kapat” davranışı

- [ ] Android’in bütün izinleri uygulama içinden kapatmaya izin vermediği açıklansın.
- [ ] Düğmede “Sistem uygulama ayarlarına yönlendirir” açıklaması bulunsun.
- [ ] Kullanıcı doğru sistem ekranına yönlendirilsin ve dönüşte durumlar yenilensin.

### 14. Mevcut izin ayarlarının birleştirilmesi

- [ ] Dağınık izin seçenekleri merkezi bölüme taşınsın.
- [ ] Eski tekrar eden izin kartları kaldırılsın.
- [ ] İzin gerektiren ekranlar kullanıcıyı merkezdeki ilgili satıra yönlendirsin.

## E. SİMGE VE RENK STANDARDI

### 15. Logo yeşili simge çerçevesi

- [ ] Logo yeşili uygun simgelere çok ince, %50 opak vantablack dış sınır uygulansın.
- [ ] Kalınlık ve görünüm uygulama genelinde tutarlı olsun.
- [ ] Ortak OutlinedThemeIcon bileşeni oluşturulsun.
- [ ] Çerçeve simgeyi baskılamasın ve okunabilirliği azaltmasın.

### 16. Ekle düğmeleri

- [ ] İlaçlar, Randevular ve Ölçümler sayfalarındaki + düğmeleri aynı logo yeşilini kullansın.
- [ ] Ekle simgeleri ortak vantablack çerçeve standardını kullansın.
- [ ] Görünen simgeden bağımsız yeterli dokunma alanı korunsun.

### 17. Randevular sayfasındaki Ekle simgesi

- [ ] Yalnız Randevular sayfasındaki + simgesinin boyutu ve yatay hizası düzeltilsin.
- [ ] Hizalama diğer sekme simgelerine göre yapılsın; sekme yazıları hesaba katılmasın.
- [ ] İlaçlar ve Ölçümler sayfalarında bu madde kapsamında boyut değişikliği yapılmasın.

### 18. Üç ana kayıt sayfasının görsel standardı

- [ ] İlaçlar, Randevular ve Ölçümler sayfalarının başlık, sekme, simge, boşluk ve kart düzeni standartlaştırılsın.

## F. KART TASARIM SİSTEMİ

### 19. Randevu kartlarının yeniden tasarlanması

- [ ] Bilgi hiyerarşisi, tarih-saat, doktor, kurum ve açıklama alanları düzenlensin.
- [ ] Simge, işlem düğmesi, tipografi, renk, boşluk ve köşe geçişleri yenilensin.
- [ ] Modern, sade ve okunabilir ortak randevu kartı bileşeni hazırlansın.

### 20. Bugünkü Randevular kartı

- [ ] Bugün ve Randevular sayfaları aynı ortak randevu kartını kullansın.
- [ ] Renk, tipografi, simge ve bilgi hiyerarşisi aynı olsun.
- [ ] Yalnız kullanım bağlamından doğan içerik farklılıkları korunsun.

### 21. Türk mavisi kart iç gölgesi

- [ ] Uygun kartlara çok ince, %50 opak Türk mavisi iç vurgu uygulansın.
- [ ] Alarm ve kritik uyarı kartlarının anlam renkleri korunsun.
- [ ] Gerçek cihazda fazla güçlü görünürse opaklık tema ayarından azaltılabilsin.
- [ ] Kart gölge opaklığı genel tema opaklığından bağımsız olsun.

### 22. Ortak kart ve arayüz bileşenleri

- [ ] Çerçeveli simge, iç gölgeli kart, randevu kartı, Ekle düğmesi ve standart bilgi/işlem satırları merkezileştirilsin.

## G. KAYAR BANNER VE ALT MENÜ BÜTÜNLÜĞÜ

### 23. Kesin logo kaynağı ve ölçülendirme

- [ ] Yalnız son paylaşılan hazerfen.png kullanılsın.
- [ ] Eski banner logo kaynağı yeni dosyayla değiştirilsin.
- [ ] Logo banner yüksekliğine göre yeniden boyutlandırılsın.
- [ ] Özgün en-boy oranı korunsun; kırpma, sıkıştırma veya esnetme yapılmasın.
- [ ] Bütün kopyalar aynı kaynak, yükseklik ve ölçekleme yöntemini kullansın.
- [ ] Şirket alt açıklaması logoyla birlikte korunsun.

### 24. Bitişik ve ardışık logo dizilimi

- [ ] Aynı Hazerfen logosu kopyaları boşluksuz, bitişik ve ardışık yerleştirilsin.
- [ ] Kopyalar arasında margin, padding, Spacer veya saydam ayırma payı bulunmasın.
- [ ] Bütün kopyalar tam olarak aynı yükseklikte görünsün.
- [ ] Üçüncü veya sonraki logo küçülmesin.
- [ ] Birleşim noktalarında üst üste binme veya beyaz boşluk oluşmasın.

### 25. Kesintisiz gerçek sonsuz döngü

- [ ] Logo dizisi ekran genişliğini aşacak sayıda dinamik olarak çoğaltılsın.
- [ ] Aynı ölçüde iki eş logo grubu arka arkaya kullanılsın.
- [ ] Bir tarafta kaybolan logo bölümü eş zamanlı olarak karşı taraftan görüntüye girsin.
- [ ] Üç logo geçtikten sonra boş bekleme veya boş ekran oluşmasın.
- [ ] Döngünün başlangıç ve bitiş noktası fark edilmesin.
- [ ] Sıçrama, takılma, titreme, konum atlaması veya hız değişikliği olmasın.
- [ ] Animasyon mesafesi ölçülen gerçek grup genişliğinden hesaplansın.

### 26. Banner ile alt menü arasındaki beyaz hattın kaldırılması

- [ ] Banner ile alt menü arasında beyaz çizgi veya boşluk bulunmasın.
- [ ] Margin, padding, Spacer, ayırıcı, arka plan farkı ve inset etkileri temizlensin.
- [ ] Sistem gezinme boşluğu birleşik bileşenin dışına uygulansın.
- [ ] Hareketle ve üç düğmeli gezinmede beyaz hat yeniden oluşmasın.

### 27. Banner ve alt menünün ortak tasarımı

- [ ] Banner alt menünün doğrudan üst ve bütünleyici parçası gibi görünsün.
- [ ] İki bileşen aynı dış kapsayıcı, uyumlu yüzey, gölge ve köşe geçişlerini kullansın.
- [ ] Gölge aralarına değil birleşik yapının dış sınırına uygulansın.
- [ ] %10 opak vantablack cam katmanı hareket eden logoların üzerinde sabit kalsın.
- [ ] Banner yüksekliği yaklaşık 10 mm olacak şekilde ekran yoğunluğuna uyarlansın.
- [ ] Bannerın tamamına dokunulduğunda https://www.hazerfen.com.tr açılsın.

### 28. Banner hareketinin yaşam döngüsü ve erişilebilirlik

- [ ] Sekme değişiminde animasyon gereksiz yere yeniden başlamasın.
- [ ] Uygulama ön plana geldiğinde ve ekran yönü değiştiğinde akış boşluk oluşturmadan sürsün.
- [ ] Aynı anda yalnız bir banner animasyonu çalışsın.
- [ ] Android’in “animasyonları azalt” tercihine uyulsun.
- [ ] Hareket durdurulduğunda bağlantı çalışmaya devam etsin.

## H. SİSTEM ALANLARI VE ERİŞİLEBİLİRLİK

### 29. Alt içerik boşluğunun dinamik hesaplanması

- [ ] Alt menü, banner, içerik ve Android gezinme alanı arasındaki boşluk sabit olmasın.
- [ ] Window Insets ile gerçek sistem gezinme alanı ve güvenli sınırlar hesaplansın.
- [ ] Yalnız gerekli minimum güvenlik payı eklensin.
- [ ] İçerik alt menü altında kalmasın ve gereksiz geniş boşluk oluşmasın.
- [ ] Farklı ekranlar, yatay görünüm ve iki gezinme yöntemi test edilsin.

### 30. Genel erişilebilirlik ve okunabilirlik

- [ ] Beş yazı tipi ve büyük sistem yazı ölçekleri test edilsin.
- [ ] Türkçe karakterler, renk kontrastı ve uzun metin taşmaları doğrulansın.
- [ ] TalkBack açıklamaları ve erişilebilir dokunma alanları kontrol edilsin.
- [ ] Ekran büyütme ve animasyonları azaltma ayarları desteklensin.

## I. DOĞRULANMIŞ DAVRANIŞLAR

### 31. Ekran kapalıyken alarm çalışması

- [x] Tekli ilaç alarmının ekran kapalıyken çalıştığı doğrulandı.
- [x] Çoklu/grup ilaç alarmının ekran kapalıyken çalıştığı doğrulandı.
- [ ] Sonraki sürümde bu davranışların bozulmadığı regresyon testiyle doğrulansın.

## J. TEST VE SÜRÜM KABUL ÖLÇÜTLERİ

### 32. Alarm test matrisi

- [ ] Tekli/grup, normal/deneme ve ertelemeli/ertelemesiz alarmlar test edilsin.
- [ ] Ekran açık/kapalı, telefon kilitli/kilitsiz ve uygulama ön/arka planda/kapalı durumları test edilsin.
- [ ] Bildirim, tam ekran alarm, kesin alarm ve pil optimizasyonu durumları test edilsin.
- [ ] Android 12–16 ve hareketle/üç düğmeli gezinme mümkün olan ölçüde test edilsin.

### 33. Görsel ve banner test matrisi

- [ ] Alarm başlık merkezlemesi, Ekle simgeleri, simge çerçeveleri ve kart gölgeleri doğrulansın.
- [ ] Randevu kartları ve beş yazı tipi farklı ekranlarda kontrol edilsin.
- [ ] Bütün banner logolarının aynı yükseklikte olduğu doğrulansın.
- [ ] Logoların bitişik, boşluksuz ve sonsuz aktığı doğrulansın.
- [ ] Kaybolan logo bölümünün karşı taraftan eş zamanlı girdiği doğrulansın.
- [ ] Banner-alt menü beyaz hattının kaldırıldığı doğrulansın.
- [ ] Banner ve alt menünün tek bileşen gibi göründüğü doğrulansın.

### 34. Sürümün kabul koşulları

- [ ] Proje hatasız derlensin.
- [ ] Birim testleri ve Android lint denetimi geçsin.
- [ ] APK üretilsin ve temel gerçek cihaz testleri yapılsın.
- [ ] Grup alarmında yalnız bir ses kaynağı çalışsın.
- [ ] Ertelenen alarmın eski bildirimi kapansın ve yeniden açılamasın.
- [ ] Ekran kapalı alarm davranışında gerileme olmasın.
- [ ] Banner boşluksuz, kesintisiz ve sıçramasız çalışsın.
- [ ] İzin merkezi Android’in gerçek durumunu göstersin.
- [ ] Tema ve yazı tipi tercihleri yeniden açılışta korunsun.
- [ ] APK ve kaynak ZIP için SHA-256 özetleri oluşturulsun.
- [ ] Değişiklik ve test raporu kaynak paketine eklensin.


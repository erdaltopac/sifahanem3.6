package com.hazerfen.sifahane

import android.content.Context
import java.io.File

fun comprehensiveUserGuideSections(): List<Pair<String, String>> = listOf(
        "1. Başlangıç ve güvenlik" to "İlk kurulumda kullanıcı 4–12 haneli kendi yönetici şifresini iki kez girerek oluşturur. Şifahane’de varsayılan veya arka kapı yönetici şifresi yoktur. Kullanıcı desenleri en az dört noktadan oluşturulur; art arda hatalı PIN veya desen denemelerinde giderek artan güvenlik beklemesi uygulanır.",
        "2. Kişiler ve yetkiler" to "Kişiler bölümünden yönetici veya standart kullanıcı profilleri yönetilir. Tek kalan yönetici silinemez. Kullanıcı silme işlemleri yönetici şifresi gerektirir. Kişi eklerken elle giriş veya yedekten içe aktarma yolu seçilir.",
        "3. İlaç ekleme ve düzenleme" to "İlaçlar bölümünde ilaç adı, doz, kullanım amacı, başlangıç-bitiş tarihleri, saatler ve stok bilgileri kaydedilir. Aynı dakika için planlanan ilaçlar alarm sırasında tek grupta gösterilir.",
        "4. Bugün ekranı" to "Bugün ekranı seçili kullanıcının günlük ilaç planını gösterir. Kartlardan alınma durumu izlenir; geçmiş veya gelecek günlere gidilebilir.",
        "5. Alarm ve grup işlemleri" to "Aynı saatteki ilaçlar kullanıcı sekmeleriyle tek alarm kartında listelenir. Her ilaç için Aldım, Ertele ve Almayacağım işlemleri ayrı ayrı veya topluca uygulanabilir. Her grup için tek ses ve titreşim oturumu çalışır.",
        "6. Erteleme seçenekleri" to "Ayarlar > Alarm ve Bildirimler bölümünde üç erteleme süresi belirlenir. Alarm ekranında bireysel ve toplu erteleme için bu üç seçenek kullanılır. Yeni seçim aynı ilaç ve doz için önceki bekleyen ertelemeyi geçersiz kılar.",
        "7. Randevular" to "Randevular bölümünde doktor, kurum, tarih, saat ve not bilgileri eklenebilir; kayıtlar düzenlenebilir ve hatırlatıcı kurulabilir.",
        "8. Ölçümler" to "Tansiyon ve kan şekeri sekmelerinde yeni ölçüm eklenebilir, mevcut kayıtlar düzenlenebilir ve zaman sırasıyla incelenebilir.",
        "9. Raporlar" to "Tarih aralığı seçilerek kayıt ve grafik raporları görüntülenir. Grafiklerde eksen değerleri bulunur. Grafikler yüksek çözünürlüklü PNG olarak paylaşılabilir veya Şifahane Raporları albümüne kaydedilebilir.",
        "10. Yedekleme" to "Kişi verilerini dışa aktarma işlemi, kullanıcının belirlediği en az sekiz karakterli parola ile AES-GCM korumalı .sifbackup dosyası oluşturur. Parola uygulamada saklanmaz; kaybedilirse yedek açılamaz. İkinci kopya Android paylaşım ekranıyla gönderilebilir veya seçilen dosya konumuna kaydedilebilir.",
        "11. Yedekten içe aktarma" to "Şifreli yedek seçildiğinde oluşturma parolası istenir; yanlış parola veya değiştirilmiş dosya reddedilir. Eski Şifahane ZIP yedekleri yalnız kontrollü içe aktarma uyumluluğu için desteklenir. Önizleme ve kullanıcı rolü kontrolünden sonra içe aktarma başlatılır.",
        "12. Otomatik ekran kilidi" to "Ayarlar > Güvenlik ve Yetkilendirme bölümündeki açılır listeden 30 saniye, 1, 2, 5, 10 veya 30 dakika ya da Hiçbir zaman seçilir.",
        "13. Görünüm ve tema" to "Varsayılan tema beyaz, vantablack, Şifahane logo yeşili ve yalnızca uyarılarda Türk kırmızısı kullanır. Tema ayarlarından görünüm tercihleri değiştirilebilir ve varsayılana döndürülebilir.",
        "14. Geri gezinme" to "Telefonun sistem geri düğmesi veya geri hareketi önce açılır kartı ya da iletişim kutusunu kapatır; ardından kullanıcıyı gerçek gezinme geçmişindeki bir önceki ekrana döndürür.",
        "15. Yardım ve yapay zekâ" to "Yardım başlıkları uygulama içinde aranabilir. Yapay Zekâya Sor seçeneği yalnızca bu kapsamlı kılavuzu kişisel veri içermeyen Word belgesi olarak Android paylaşım ekranına gönderir.",
        "16. Android izinleri" to "Ayarlar > Android İzinleri bölümünde bildirim, kesin alarm, tam ekran alarm, pil optimizasyonu ve kamera durumları birlikte görülür. Android'in doğrudan değiştirmeye izin vermediği erişimlerde ilgili sistem ayarı açılır.",
        "17. Eski alarm isteklerini temizleme" to "Alarm ve Bildirimler bölümündeki Önceden Kalmış Alarm İsteklerini Sil işlemi Şifahane'nin kayıtlı eski isteklerini temizler ve yalnız güncel ilaç planlarının alarmlarını yeniden kurar.",
        "18. Kayar Hazerfen bannerı" to "Alt menünün üstündeki banner, üst yumuşak geçiş ve cam katmanıyla birlikte 7,5 mm yüksekliğindedir. Tam opak Hazerfen logoları aynı yükseklikte, bitişik ve boşluksuz olarak beş dakikada bir ekran genişliği hızla akar. Bannerın üst sınırı, alt menü altındaki vantablack yumuşak geçişin düşey aynasıdır. Bannera dokunulduğunda Hazerfen internet sitesi açılır.",
        "19. Gizlilik" to "Şifrelerinizi, deseninizi ve sağlık yedeklerinizi güvenmediğiniz uygulamalarla paylaşmayın. Kılavuz belgesinde kişi veya sağlık verisi bulunmaz. Acil sağlık kararları için uygulama yerine sağlık profesyoneline başvurun."
    )

/** Creates a selectable, searchable Word-compatible document without profile or health data. */
fun createComprehensiveUserGuide(context: Context): File {
    val sections = comprehensiveUserGuideSections()
    val html = buildString {
        append("<html><head><meta charset='utf-8'><title>Şifahane Kullanıcı Kılavuzu</title></head><body>")
        append("<h1>Şifahane Kapsamlı Kullanıcı Kılavuzu</h1>")
        append("<p><strong>Uygulama ve kılavuz sürümü: ${BuildConfig.VERSION_NAME}</strong></p>")
        append("<p>Bu belge aranabilir ve seçilebilir metin içerir. Kişisel sağlık veya profil verisi içermez.</p>")
        append("<h2>İçindekiler</h2><ol>")
        sections.forEach { append("<li>${it.first.substringAfter(". ")}</li>") }
        append("</ol>")
        sections.forEach { (title, body) -> append("<h2>$title</h2><p>$body</p>") }
        append("</body></html>")
    }
    val directory = File(context.cacheDir, "guides").apply { mkdirs() }
    return File(directory, "Sifahane_Kapsamli_Kullanici_Kilavuzu_v${BuildConfig.VERSION_NAME}.doc")
        .apply { writeText(html, Charsets.UTF_8) }
}

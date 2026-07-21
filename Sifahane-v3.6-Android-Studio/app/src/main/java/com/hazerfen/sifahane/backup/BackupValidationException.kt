package com.hazerfen.sifahane.backup

sealed class BackupValidationException(message: String) : Exception(message) {
    class NotSifahaneBackup : BackupValidationException("Yüklemeye çalıştığınız dosya Şifahane yedeği değildir.")
    class CorruptBackup : BackupValidationException("Şifahane yedek dosyası bozuk veya eksik olduğu için içe aktarılamadı.")
    class UnsupportedVersion : BackupValidationException("Bu yedek daha yeni bir Şifahane sürümüne aittir.")
    class ResourceLimit : BackupValidationException("Yedek dosyası güvenli boyut veya içerik sınırlarını aşıyor.")
    class PasswordRequired : BackupValidationException("Bu yedek şifrelidir; yedek parolasını girin.")
    class WrongPassword : BackupValidationException("Yedek parolası yanlış veya şifreli dosyanın bütünlüğü bozulmuş.")
}

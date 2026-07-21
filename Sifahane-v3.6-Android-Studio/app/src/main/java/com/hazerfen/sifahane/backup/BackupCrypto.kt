package com.hazerfen.sifahane.backup

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream

internal object BackupCrypto {
    fun isEncrypted(context: Context, uri: Uri): Boolean =
        context.contentResolver.openInputStream(uri)?.let(BackupCipherCore::isEncrypted) ?: false

    fun encryptFile(plainZip: File, encryptedTarget: File, password: CharArray) {
        plainZip.inputStream().use { input ->
            encryptedTarget.outputStream().use { output ->
                BackupCipherCore.encrypt(input, output, password)
            }
        }
    }

    fun openInput(context: Context, uri: Uri, password: CharArray?): InputStream {
        val source = context.contentResolver.openInputStream(uri)
            ?: throw BackupValidationException.NotSifahaneBackup()
        return BackupCipherCore.open(source, password)
    }
}

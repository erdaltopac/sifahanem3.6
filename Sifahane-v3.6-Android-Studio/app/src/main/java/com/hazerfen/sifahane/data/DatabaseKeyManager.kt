package com.hazerfen.sifahane.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.FileInputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Room/SQLCipher veri anahtarını Android Keystore içindeki AES anahtarıyla sarar.
 * Keystore anahtarı cihaz dışına çıkarılamaz; veri anahtarının açık biçimi tercihlere yazılmaz.
 */
internal object DatabaseKeyManager {
    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "sifahane.database.keywrap.v1"
    private const val PREFS = "sifahane_database_security"
    private const val WRAPPED_KEY = "wrapped_database_key_v1"
    private const val IV = "wrapped_database_key_iv_v1"
    private const val KEY_BYTES = 32

    fun getOrCreate(context: Context): ByteArray {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val wrapped = prefs.getString(WRAPPED_KEY, null)
        val iv = prefs.getString(IV, null)
        if (wrapped != null || iv != null) {
            check(wrapped != null && iv != null) { "Veritabanı anahtar kaydı eksik." }
            return decrypt(wrapped, iv)
        }

        val database = context.getDatabasePath("sifahane.db")
        check(!database.isFile || database.length() == 0L || isPlaintextSqlite(database)) {
            "Şifreli veritabanı var ancak Keystore anahtar kaydı bulunamadı; veri kaybını önlemek için yeni anahtar üretilmedi."
        }

        val plain = ByteArray(KEY_BYTES).also(SecureRandom()::nextBytes)
        val result = encrypt(plain)
        val committed = prefs.edit()
            .putString(WRAPPED_KEY, result.first)
            .putString(IV, result.second)
            .commit()
        check(committed) { "Veritabanı anahtarı güvenli depoya yazılamadı." }
        return plain
    }

    private fun isPlaintextSqlite(file: java.io.File): Boolean {
        val expected = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
        if (file.length() < expected.size) return false
        val actual = ByteArray(expected.size)
        FileInputStream(file).use { if (it.read(actual) != actual.size) return false }
        return actual.contentEquals(expected)
    }

    private fun encrypt(plain: ByteArray): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateWrappingKey())
        val encrypted = cipher.doFinal(plain)
        return Base64.getEncoder().encodeToString(encrypted) to
            Base64.getEncoder().encodeToString(cipher.iv)
    }

    private fun decrypt(wrapped: String, iv: String): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateWrappingKey(),
            GCMParameterSpec(128, Base64.getDecoder().decode(iv))
        )
        return cipher.doFinal(Base64.getDecoder().decode(wrapped)).also {
            check(it.size == KEY_BYTES) { "Veritabanı anahtar uzunluğu geçersiz." }
        }
    }

    private fun getOrCreateWrappingKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }
}

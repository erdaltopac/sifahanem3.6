package com.hazerfen.sifahane.backup

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.PushbackInputStream
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/** Android bağımsız, birim test edilebilir AES-GCM yedek kapsayıcısı. */
internal object BackupCipherCore {
    private val MAGIC = "SIFAHANE-AESGCM1".toByteArray(Charsets.US_ASCII)
    private const val SALT_BYTES = 16
    private const val IV_BYTES = 12
    private const val ITERATIONS = 310_000
    private const val KEY_BITS = 256
    private val random = SecureRandom()

    fun isEncrypted(input: InputStream): Boolean = input.use {
        val header = ByteArray(MAGIC.size)
        var offset = 0
        while (offset < header.size) {
            val count = it.read(header, offset, header.size - offset)
            if (count < 0) return@use false
            offset += count
        }
        header.contentEquals(MAGIC)
    }

    fun encrypt(input: InputStream, output: OutputStream, password: CharArray) {
        require(password.size in 8..128) { "Yedek parolası en az 8 karakter olmalıdır." }
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        val iv = ByteArray(IV_BYTES).also(random::nextBytes)
        val key = derive(password, salt, ITERATIONS)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, iv))
        key.fill(0)

        BufferedOutputStream(output).use { raw ->
            raw.write(MAGIC)
            raw.write(ByteBuffer.allocate(4).putInt(ITERATIONS).array())
            raw.write(salt)
            raw.write(iv)
            CipherOutputStream(raw, cipher).use { encrypted ->
                BufferedInputStream(input).use { it.copyTo(encrypted) }
            }
        }
    }

    fun open(input: InputStream, password: CharArray?): InputStream {
        val pushback = PushbackInputStream(BufferedInputStream(input), MAGIC.size)
        val header = ByteArray(MAGIC.size)
        val read = pushback.read(header)
        if (read != MAGIC.size || !header.contentEquals(MAGIC)) {
            if (read > 0) pushback.unread(header, 0, read)
            return pushback
        }

        val provided = password ?: run {
            pushback.close()
            throw BackupValidationException.PasswordRequired()
        }
        if (provided.size !in 8..128) {
            pushback.close()
            throw BackupValidationException.WrongPassword()
        }

        val iterations = ByteBuffer.wrap(pushback.readExactly(4)).int
        if (iterations !in 100_000..1_000_000) {
            pushback.close()
            throw BackupValidationException.CorruptBackup()
        }
        val salt = pushback.readExactly(SALT_BYTES)
        val iv = pushback.readExactly(IV_BYTES)
        val key = derive(provided, salt, iterations)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, iv))
        key.fill(0)
        return CipherInputStream(pushback, cipher)
    }

    private fun derive(password: CharArray, salt: ByteArray, iterations: Int): ByteArray {
        val copy = password.copyOf()
        return try {
            val spec = PBEKeySpec(copy, salt, iterations, KEY_BITS)
            try {
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .encoded
            } finally {
                spec.clearPassword()
            }
        } finally {
            copy.fill('\u0000')
        }
    }

    private fun InputStream.readExactly(length: Int): ByteArray {
        val result = ByteArray(length)
        var offset = 0
        while (offset < length) {
            val count = read(result, offset, length - offset)
            if (count < 0) throw BackupValidationException.CorruptBackup()
            offset += count
        }
        return result
    }
}

package com.hazerfen.sifahane.backup

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class BackupCipherCoreTest {
    private val payload = ByteArray(64 * 1024) { (it % 251).toByte() }

    @Test
    fun encryptedRoundTripRestoresBytes() {
        val encrypted = ByteArrayOutputStream()
        BackupCipherCore.encrypt(
            ByteArrayInputStream(payload),
            encrypted,
            "correct horse battery staple".toCharArray()
        )

        val restored = BackupCipherCore.open(
            ByteArrayInputStream(encrypted.toByteArray()),
            "correct horse battery staple".toCharArray()
        ).use { it.readBytes() }

        assertArrayEquals(payload, restored)
        assertFalse(encrypted.toByteArray().containsSubsequence(payload.copyOfRange(0, 32)))
    }

    @Test
    fun wrongPasswordIsRejectedByAuthenticationTag() {
        val encrypted = ByteArrayOutputStream()
        BackupCipherCore.encrypt(ByteArrayInputStream(payload), encrypted, "right-password".toCharArray())

        val failure = runCatching {
            BackupCipherCore.open(
                ByteArrayInputStream(encrypted.toByteArray()),
                "wrong-password".toCharArray()
            ).use { it.readBytes() }
        }.exceptionOrNull()

        assertNotNull(failure)
    }

    @Test
    fun oneByteTamperIsRejected() {
        val encrypted = ByteArrayOutputStream()
        BackupCipherCore.encrypt(ByteArrayInputStream(payload), encrypted, "right-password".toCharArray())
        val tampered = encrypted.toByteArray().also { it[it.lastIndex - 10] = (it[it.lastIndex - 10].toInt() xor 1).toByte() }

        val failure = runCatching {
            BackupCipherCore.open(ByteArrayInputStream(tampered), "right-password".toCharArray())
                .use { it.readBytes() }
        }.exceptionOrNull()

        assertNotNull(failure)
    }

    @Test
    fun legacyPlainInputPassesThrough() {
        val restored = BackupCipherCore.open(ByteArrayInputStream(payload), null).use { it.readBytes() }
        assertArrayEquals(payload, restored)
    }
}

private fun ByteArray.containsSubsequence(candidate: ByteArray): Boolean {
    if (candidate.isEmpty() || candidate.size > size) return false
    for (start in 0..size - candidate.size) {
        if (candidate.indices.all { this[start + it] == candidate[it] }) return true
    }
    return false
}

package com.hazerfen.sifahane.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Salt'lı ve yavaş parola türetme katmanı.
 *
 * Saklama biçimi: v2$pbkdf2-sha256$210000$<salt-b64>$<hash-b64>
 */
internal object SecureCredentialKdf {
    private const val VERSION = "v2"
    private const val ALGORITHM_LABEL = "pbkdf2-sha256"
    internal const val ITERATIONS = 210_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_BYTES = 16
    private val random = SecureRandom()

    fun hash(secret: String, context: String): String {
        require(secret.isNotEmpty())
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        val derived = derive(secret, context, salt, ITERATIONS)
        return listOf(
            VERSION,
            ALGORITHM_LABEL,
            ITERATIONS.toString(),
            Base64.getEncoder().withoutPadding().encodeToString(salt),
            Base64.getEncoder().withoutPadding().encodeToString(derived)
        ).joinToString("$")
    }

    fun verify(stored: String, secret: String, context: String): Boolean {
        val parts = stored.split('$')
        if (parts.size != 5 || parts[0] != VERSION || parts[1] != ALGORITHM_LABEL) return false
        val iterations = parts[2].toIntOrNull()?.takeIf { it in 100_000..1_000_000 } ?: return false
        return runCatching {
            val salt = Base64.getDecoder().decode(parts[3])
            val expected = Base64.getDecoder().decode(parts[4])
            val actual = derive(secret, context, salt, iterations)
            MessageDigest.isEqual(expected, actual)
        }.getOrDefault(false)
    }

    fun isModern(stored: String?): Boolean = stored?.startsWith("$VERSION$") == true

    private fun derive(secret: String, context: String, salt: ByteArray, iterations: Int): ByteArray {
        val chars = "$context\u0000$secret".toCharArray()
        return try {
            val spec = PBEKeySpec(chars, salt, iterations, KEY_LENGTH_BITS)
            try {
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .encoded
            } finally {
                spec.clearPassword()
            }
        } finally {
            chars.fill('\u0000')
        }
    }
}

internal object PersistentLockoutPolicy {
    const val FIRST_LOCKOUT_AFTER_FAILURES = 5

    fun delayMillis(failureCount: Int): Long = when {
        failureCount < FIRST_LOCKOUT_AFTER_FAILURES -> 0L
        failureCount == 5 -> 30_000L
        failureCount == 6 -> 60_000L
        failureCount == 7 -> 120_000L
        failureCount == 8 -> 300_000L
        else -> 900_000L
    }
}

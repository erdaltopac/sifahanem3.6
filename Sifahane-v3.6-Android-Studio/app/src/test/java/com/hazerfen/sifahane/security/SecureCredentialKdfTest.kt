package com.hazerfen.sifahane.security

import org.junit.Assert.*
import org.junit.Test

class SecureCredentialKdfTest {
    @Test
    fun sameSecretUsesUniqueSaltAndVerifies() {
        val first = SecureCredentialKdf.hash("482915", "GLOBAL_ADMIN_PIN")
        val second = SecureCredentialKdf.hash("482915", "GLOBAL_ADMIN_PIN")

        assertNotEquals(first, second)
        assertTrue(SecureCredentialKdf.verify(first, "482915", "GLOBAL_ADMIN_PIN"))
        assertTrue(SecureCredentialKdf.verify(second, "482915", "GLOBAL_ADMIN_PIN"))
    }

    @Test
    fun wrongSecretOrContextIsRejected() {
        val stored = SecureCredentialKdf.hash("482915", "GLOBAL_ADMIN_PIN")

        assertFalse(SecureCredentialKdf.verify(stored, "482916", "GLOBAL_ADMIN_PIN"))
        assertFalse(SecureCredentialKdf.verify(stored, "482915", "PROFILE_PATTERN:7"))
    }

    @Test
    fun malformedHashIsRejected() {
        assertFalse(SecureCredentialKdf.verify("v2\$broken", "1234", "GLOBAL_ADMIN_PIN"))
        assertFalse(SecureCredentialKdf.verify("", "1234", "GLOBAL_ADMIN_PIN"))
    }

    @Test
    fun lockoutDelayIncreasesAndIsBounded() {
        assertEquals(0L, PersistentLockoutPolicy.delayMillis(4))
        assertEquals(30_000L, PersistentLockoutPolicy.delayMillis(5))
        assertEquals(60_000L, PersistentLockoutPolicy.delayMillis(6))
        assertEquals(300_000L, PersistentLockoutPolicy.delayMillis(8))
        assertEquals(900_000L, PersistentLockoutPolicy.delayMillis(20))
    }
}

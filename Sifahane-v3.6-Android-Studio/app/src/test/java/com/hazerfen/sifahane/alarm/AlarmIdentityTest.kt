package com.hazerfen.sifahane.alarm

import org.junit.Assert.*
import org.junit.Test

class AlarmIdentityTest {
    @Test
    fun sameKeyIsStableAndPositive() {
        val first = AlarmIdentity.initialRequestCode("NORMAL:123456")
        val second = AlarmIdentity.initialRequestCode("NORMAL:123456")
        assertEquals(first, second)
        assertTrue(first > 0)
    }

    @Test
    fun allocationProbesPastCollision() {
        val key = "SNOOZE:999"
        val initial = AlarmIdentity.initialRequestCode(key)
        val allocated = AlarmIdentity.allocate(key, mapOf(initial to "OTHER"))
        assertEquals(if (initial == Int.MAX_VALUE) 1 else initial + 1, allocated)
    }

    @Test
    fun allocationReusesCodeOwnedBySameKey() {
        val key = "NORMAL:42"
        val initial = AlarmIdentity.initialRequestCode(key)
        assertEquals(initial, AlarmIdentity.allocate(key, mapOf(initial to key)))
    }
}

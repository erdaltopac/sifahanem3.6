package com.hazerfen.sifahane.alarm

import java.nio.ByteBuffer
import java.security.MessageDigest

/** Stable alarm identity with deterministic collision probing. */
internal object AlarmIdentity {
    fun initialRequestCode(groupKey: String): Int {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(groupKey.toByteArray(Charsets.UTF_8))
        return (ByteBuffer.wrap(digest, 0, 4).int and 0x7fffffff).coerceAtLeast(1)
    }

    fun allocate(groupKey: String, occupied: Map<Int, String>): Int {
        var candidate = initialRequestCode(groupKey)
        repeat(Int.MAX_VALUE - 1) {
            val owner = occupied[candidate]
            if (owner == null || owner == groupKey) return candidate
            candidate = if (candidate == Int.MAX_VALUE) 1 else candidate + 1
        }
        error("Alarm istek kodu alanı tükendi.")
    }
}

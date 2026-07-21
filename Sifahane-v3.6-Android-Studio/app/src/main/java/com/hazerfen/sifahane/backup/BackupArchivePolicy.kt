package com.hazerfen.sifahane.backup

internal object BackupArchivePolicy {
    const val MAX_ENTRIES = 512
    const val MAX_JSON_BYTES = 10L * 1024L * 1024L
    const val MAX_MEDIA_BYTES = 50L * 1024L * 1024L
    const val MAX_TOTAL_UNCOMPRESSED_BYTES = 250L * 1024L * 1024L
    const val MAX_COMPRESSION_RATIO = 100L

    fun normalizeEntryName(raw: String): String = raw.replace('\\', '/').trim()

    fun isUnsafeEntryName(name: String): Boolean {
        if (name.isBlank() || name.startsWith('/') || name.startsWith('\\')) return true
        if (name.length > 240 || name.contains('\u0000')) return true
        val segments = name.split('/')
        return segments.any { it.isBlank() || it == "." || it == ".." } ||
            Regex("^[A-Za-z]:").containsMatchIn(name)
    }

    fun entryLimit(name: String, backupJsonName: String): Long =
        if (name == backupJsonName) MAX_JSON_BYTES else MAX_MEDIA_BYTES

    fun hasSuspiciousCompression(uncompressed: Long, compressed: Long): Boolean =
        uncompressed > 0L && compressed > 0L &&
            uncompressed / compressed.coerceAtLeast(1L) > MAX_COMPRESSION_RATIO
}

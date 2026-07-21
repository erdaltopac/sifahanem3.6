package com.hazerfen.sifahane.backup

import org.junit.Assert.*
import org.junit.Test

class BackupArchivePolicyTest {
    @Test
    fun rejectsZipSlipAndAbsolutePaths() {
        assertTrue(BackupArchivePolicy.isUnsafeEntryName("../secret.txt"))
        assertTrue(BackupArchivePolicy.isUnsafeEntryName("photos/../../secret.txt"))
        assertTrue(BackupArchivePolicy.isUnsafeEntryName("/absolute/file"))
        assertTrue(BackupArchivePolicy.isUnsafeEntryName("C:/windows/file"))
        assertFalse(BackupArchivePolicy.isUnsafeEntryName("medicine_photos/medicine_1.jpg"))
    }

    @Test
    fun suspiciousCompressionRatioIsRejected() {
        assertTrue(BackupArchivePolicy.hasSuspiciousCompression(101_000L, 1_000L))
        assertFalse(BackupArchivePolicy.hasSuspiciousCompression(100_000L, 1_000L))
        assertFalse(BackupArchivePolicy.hasSuspiciousCompression(1_000L, -1L))
    }
}

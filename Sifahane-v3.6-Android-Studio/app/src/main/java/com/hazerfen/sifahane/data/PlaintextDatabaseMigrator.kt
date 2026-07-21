package com.hazerfen.sifahane.data

import android.content.Context
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.io.File
import java.io.FileInputStream

/** Tek seferlik düz SQLite -> SQLCipher dönüşümü. Orijinal dosya ancak doğrulama sonrası silinir. */
internal object PlaintextDatabaseMigrator {
    private val SQLITE_HEADER = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)

    @Synchronized
    fun migrateIfNeeded(context: Context, databaseName: String, passphrase: ByteArray) {
        val source = context.getDatabasePath(databaseName)
        if (!source.isFile || source.length() == 0L || !isPlaintextSqlite(source)) return

        source.parentFile?.mkdirs()
        val encrypted = File(source.parentFile, "$databaseName.encrypted-migration")
        val originalBackup = File(source.parentFile, "$databaseName.plaintext-rollback")
        encrypted.delete()
        originalBackup.delete()

        val sourceDb = SQLiteDatabase.openDatabase(
            source.absolutePath,
            ByteArray(0),
            null,
            SQLiteDatabase.OPEN_READWRITE,
            null
        )
        try {
            runCatching {
                sourceDb.rawQuery("PRAGMA wal_checkpoint(FULL)", null).use { it.moveToFirst() }
            }
            val escapedPath = encrypted.absolutePath.replace("'", "''")
            val hexKey = passphrase.joinToString("") { "%02x".format(it) }
            sourceDb.execSQL("ATTACH DATABASE '$escapedPath' AS encrypted KEY \"x'$hexKey'\"")
            try {
                sourceDb.rawQuery("SELECT sqlcipher_export('encrypted')", null).use { it.moveToFirst() }
                sourceDb.execSQL("PRAGMA encrypted.user_version = ${sourceDb.version}")
            } finally {
                sourceDb.execSQL("DETACH DATABASE encrypted")
            }
        } finally {
            sourceDb.close()
        }

        validateEncrypted(encrypted, passphrase)
        deleteSidecars(source)

        check(source.renameTo(originalBackup)) { "Düz veritabanı geçici olarak taşınamadı." }
        if (!encrypted.renameTo(source)) {
            originalBackup.renameTo(source)
            error("Şifreli veritabanı etkinleştirilemedi.")
        }

        try {
            validateEncrypted(source, passphrase)
            check(originalBackup.delete()) { "Düz veritabanı güvenli geçiş sonrası silinemedi." }
        } catch (failure: Throwable) {
            source.delete()
            originalBackup.renameTo(source)
            throw failure
        } finally {
            encrypted.delete()
            deleteSidecars(source)
        }
    }

    private fun validateEncrypted(file: File, passphrase: ByteArray) {
        val db = SQLiteDatabase.openDatabase(
            file.absolutePath,
            passphrase.copyOf(),
            null,
            SQLiteDatabase.OPEN_READONLY,
            null
        )
        try {
            db.rawQuery("SELECT count(*) FROM sqlite_master", null).use {
                check(it.moveToFirst()) { "Şifreli veritabanı doğrulanamadı." }
                it.getLong(0)
            }
        } finally {
            db.close()
        }
    }

    private fun isPlaintextSqlite(file: File): Boolean {
        if (file.length() < SQLITE_HEADER.size) return false
        val header = ByteArray(SQLITE_HEADER.size)
        FileInputStream(file).use { input ->
            if (input.read(header) != header.size) return false
        }
        return header.contentEquals(SQLITE_HEADER)
    }

    private fun deleteSidecars(database: File) {
        File(database.absolutePath + "-wal").delete()
        File(database.absolutePath + "-shm").delete()
        File(database.absolutePath + "-journal").delete()
    }
}

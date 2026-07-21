package com.hazerfen.sifahane.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        UserProfile::class,
        Medication::class,
        DoseLog::class,
        BloodPressure::class,
        BloodGlucose::class,
        ReportGroup::class,
        Appointment::class
    ],
    version = 10,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun medicationDao(): MedicationDao
    abstract fun doseLogDao(): DoseLogDao
    abstract fun vitalsDao(): VitalsDao
    abstract fun reportGroupDao(): ReportGroupDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE profiles ADD COLUMN surname TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE profiles ADD COLUMN photoUri TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE medications ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE medications ADD COLUMN barcode TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE medications ADD COLUMN prospectusUrl TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE medications ADD COLUMN doctorName TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE medications ADD COLUMN doctorBranch TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE medications ADD COLUMN doctorInstitution TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE medications ADD COLUMN doctorPhone TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE medications ADD COLUMN isReported INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE medications ADD COLUMN reportStartDate TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE medications ADD COLUMN reportEndDate TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE medications ADD COLUMN reportWarningDays INTEGER NOT NULL DEFAULT 7")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE profiles ADD COLUMN birthDate TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE profiles ADD COLUMN bloodGroup TEXT NOT NULL DEFAULT 'Bilinmiyor'")
                db.execSQL("ALTER TABLE profiles ADD COLUMN profileNote TEXT NOT NULL DEFAULT ''")
            }
        }

        

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE medications ADD COLUMN reportGroupId INTEGER DEFAULT NULL")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS report_groups (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                profileId INTEGER NOT NULL,
                name TEXT NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT NOT NULL,
                warningDays INTEGER NOT NULL
            )
        """.trimIndent())
    }
}

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE profiles ADD COLUMN role TEXT NOT NULL DEFAULT 'STANDARD'")
                db.execSQL("ALTER TABLE profiles ADD COLUMN adminPinHash TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE profiles ADD COLUMN permissionsCsv TEXT NOT NULL DEFAULT 'OWN_DATA,MEDICATIONS,MEASUREMENTS,REPORTS,EXPORT_BACKUP'")
                db.execSQL("ALTER TABLE profiles ADD COLUMN accountEnabled INTEGER NOT NULL DEFAULT 1")
                db.execSQL("UPDATE profiles SET role='ADMIN', permissionsCsv='ALL' WHERE id=(SELECT MIN(id) FROM profiles)")
            }
        }


        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS appointments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        profileId INTEGER NOT NULL,
                        doctorName TEXT NOT NULL,
                        branch TEXT NOT NULL,
                        institution TEXT NOT NULL,
                        clinic TEXT NOT NULL,
                        appointmentDateTime INTEGER NOT NULL,
                        phone TEXT NOT NULL,
                        address TEXT NOT NULL,
                        note TEXT NOT NULL,
                        status TEXT NOT NULL,
                        remindersCsv TEXT NOT NULL,
                        active INTEGER NOT NULL,
                        source TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Existing records remain NULL because older versions did not
                // record whether the stock decrement actually happened.
                db.execSQL("ALTER TABLE dose_logs ADD COLUMN stockDecreased INTEGER")
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val appContext = context.applicationContext
                    System.loadLibrary("sqlcipher")
                    val passphrase = DatabaseKeyManager.getOrCreate(appContext)
                    PlaintextDatabaseMigrator.migrateIfNeeded(
                        appContext,
                        "sifahane.db",
                        passphrase
                    )
                    val factory = SupportOpenHelperFactory(passphrase.copyOf())
                    passphrase.fill(0)
                    Room.databaseBuilder(
                        appContext,
                        AppDatabase::class.java,
                        "sifahane.db"
                    )
                        .openHelperFactory(factory)
                        .addMigrations(
                            MIGRATION_3_4,
                            MIGRATION_4_5,
                            MIGRATION_5_6,
                            MIGRATION_6_7,
                            MIGRATION_7_8,
                            MIGRATION_8_9,
                            MIGRATION_9_10
                        )
                        .build()
                        .also { INSTANCE = it }
                }
            }
    }
}

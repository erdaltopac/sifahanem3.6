package com.hazerfen.sifahane.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Transaction
    suspend fun deleteProfileCascade(profileId: Long) {
        deleteDoseLogsForProfile(profileId)
        deleteBpForProfile(profileId)
        deleteGlucoseForProfile(profileId)
        deleteMedicationsForProfile(profileId)
        deleteReportGroupsForProfile(profileId)
        deleteAppointmentsForProfile(profileId)
        deleteProfileById(profileId)
    }

    @Query("DELETE FROM dose_logs WHERE profileId = :profileId")
    suspend fun deleteDoseLogsForProfile(profileId: Long)

    @Query("DELETE FROM blood_pressure WHERE profileId = :profileId")
    suspend fun deleteBpForProfile(profileId: Long)

    @Query("DELETE FROM blood_glucose WHERE profileId = :profileId")
    suspend fun deleteGlucoseForProfile(profileId: Long)

    @Query("DELETE FROM medications WHERE profileId = :profileId")
    suspend fun deleteMedicationsForProfile(profileId: Long)

    @Query("DELETE FROM report_groups WHERE profileId = :profileId")
    suspend fun deleteReportGroupsForProfile(profileId: Long)

    @Query("DELETE FROM appointments WHERE profileId = :profileId")
    suspend fun deleteAppointmentsForProfile(profileId: Long)

    @Query("DELETE FROM profiles WHERE id = :profileId")
    suspend fun deleteProfileById(profileId: Long)

    @Query("SELECT * FROM profiles ORDER BY name, surname")
    fun observeAll(): Flow<List<UserProfile>>

    @Insert
    suspend fun insert(item: UserProfile): Long

    @Update
    suspend fun update(item: UserProfile)

    @Delete
    suspend fun delete(item: UserProfile)

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun count(): Int

    @Query("SELECT * FROM profiles WHERE id=:profileId LIMIT 1")
    suspend fun byId(profileId: Long): UserProfile?

    @Query("SELECT * FROM profiles ORDER BY id")
    suspend fun allProfiles(): List<UserProfile>

    @Query("SELECT COUNT(*) FROM profiles WHERE role='ADMIN' AND accountEnabled=1")
    suspend fun enabledAdminCount(): Int

    @Query("UPDATE profiles SET role=:role, permissionsCsv=:permissionsCsv WHERE id=:profileId")
    suspend fun updateRole(profileId: Long, role: String, permissionsCsv: String)

    @Query("UPDATE profiles SET adminPinHash=:pinHash WHERE id=:profileId")
    suspend fun updateAdminPinHash(profileId: Long, pinHash: String?)

    @Query("UPDATE profiles SET accountEnabled=:enabled WHERE id=:profileId")
    suspend fun updateAccountEnabled(profileId: Long, enabled: Boolean)
}

@Dao
interface MedicationDao {
    @Query(
        "UPDATE medications SET " +
            "reportStartDate = :startDate, " +
            "reportEndDate = :endDate, " +
            "reportWarningDays = :warningDays " +
            "WHERE reportGroupId = :groupId"
    )
    suspend fun updateReportGroupDates(
        groupId: Long,
        startDate: String,
        endDate: String,
        warningDays: Int
    )

    @Query("SELECT * FROM medications ORDER BY name")
    fun observeAllMedications(): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE profileId=:profileId AND archived=0 ORDER BY name")
    fun observeForProfile(profileId: Long): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE profileId=:profileId AND archived=1 ORDER BY startDate DESC")
    fun observeArchive(profileId: Long): Flow<List<Medication>>

    @Query("SELECT * FROM medications WHERE active=1 AND archived=0")
    suspend fun activeNow(): List<Medication>

    @Query("SELECT * FROM medications WHERE id=:id LIMIT 1")
    suspend fun byId(id: Long): Medication?

    @Query("SELECT * FROM medications WHERE profileId=:profileId ORDER BY name")
    suspend fun allForProfile(profileId: Long): List<Medication>

    @Insert
    suspend fun insert(item: Medication): Long

    @Update
    suspend fun update(item: Medication)

    @Delete
    suspend fun delete(item: Medication)

    @Query("UPDATE medications SET stock=stock-1 WHERE id=:id AND stock>0")
    suspend fun decreaseStock(id: Long): Int

    @Query("UPDATE medications SET stock=stock+1 WHERE id=:id")
    suspend fun increaseStock(id: Long): Int
}

@Dao
interface DoseLogDao {
    @Query("DELETE FROM dose_logs WHERE profileId = :profileId AND timestamp >= :from AND timestamp < :to")
    suspend fun deleteDoseLogRange(profileId: Long, from: Long, to: Long)

    @Query("DELETE FROM dose_logs WHERE profileId = :profileId AND timestamp >= :from AND timestamp < :to")
    suspend fun deleteRange(profileId: Long, from: Long, to: Long)

    @Query("DELETE FROM dose_logs WHERE profileId = :profileId")
    suspend fun deleteAllForProfile(profileId: Long)

    @Query("SELECT * FROM dose_logs WHERE profileId=:profileId ORDER BY timestamp DESC")
    fun observeForProfile(profileId: Long): Flow<List<DoseLog>>

    @Query("SELECT * FROM dose_logs WHERE profileId=:profileId ORDER BY timestamp DESC")
    suspend fun allForProfile(profileId: Long): List<DoseLog>

    @Query("SELECT * FROM dose_logs WHERE profileId=:profileId ORDER BY timestamp DESC")
    suspend fun allLogsForProfile(profileId: Long): List<DoseLog>

    @Query("SELECT * FROM dose_logs ORDER BY timestamp DESC")
    suspend fun allLogs(): List<DoseLog>

    @Query(
        """
        SELECT d.* FROM dose_logs d
        WHERE d.action LIKE '% DK ERTELENDİ'
          AND d.actualDateTime IS NOT NULL
          AND d.actualDateTime > :now
          AND NOT EXISTS (
              SELECT 1 FROM dose_logs newer
              WHERE newer.medicationId = d.medicationId
                AND newer.scheduledDateTime = d.scheduledDateTime
                AND newer.timestamp > d.timestamp
          )
        ORDER BY d.actualDateTime ASC
        """
    )
    suspend fun pendingSnoozes(now: Long): List<DoseLog>


    @Insert
    suspend fun insert(item: DoseLog)

    @Update
    suspend fun update(item: DoseLog)
}

@Dao
interface VitalsDao {
    @Query("SELECT * FROM blood_pressure WHERE profileId=:profileId ORDER BY measuredAt DESC")
    fun observeBp(profileId: Long): Flow<List<BloodPressure>>

    @Query("SELECT * FROM blood_glucose WHERE profileId=:profileId ORDER BY measuredAt DESC")
    fun observeGlucose(profileId: Long): Flow<List<BloodGlucose>>

    @Query("SELECT * FROM blood_pressure WHERE profileId=:profileId ORDER BY measuredAt DESC")
    suspend fun allBp(profileId: Long): List<BloodPressure>

    @Query("SELECT * FROM blood_pressure WHERE profileId=:profileId ORDER BY measuredAt ASC")
    suspend fun allBpForExport(profileId: Long): List<BloodPressure>

    @Query("SELECT * FROM blood_glucose WHERE profileId=:profileId ORDER BY measuredAt DESC")
    suspend fun allGlucose(profileId: Long): List<BloodGlucose>

    @Query("SELECT * FROM blood_glucose WHERE profileId=:profileId ORDER BY measuredAt ASC")
    suspend fun allGlucoseForExport(profileId: Long): List<BloodGlucose>

    @Insert
    suspend fun insertBp(item: BloodPressure)

    @Insert
    suspend fun insertGlucose(item: BloodGlucose)

    @Update
    suspend fun updateBp(item: BloodPressure)

    @Update
    suspend fun updateGlucose(item: BloodGlucose)

    @Delete
    suspend fun deleteBp(item: BloodPressure)

    @Delete
    suspend fun deleteGlucose(item: BloodGlucose)

    @Query("DELETE FROM blood_pressure WHERE profileId = :profileId AND measuredAt >= :from AND measuredAt < :to")
    suspend fun deleteBpRange(profileId: Long, from: Long, to: Long)

    @Query("DELETE FROM blood_glucose WHERE profileId = :profileId AND measuredAt >= :from AND measuredAt < :to")
    suspend fun deleteGlucoseRange(profileId: Long, from: Long, to: Long)
}

@Dao
interface ReportGroupDao {
    @Query("SELECT * FROM report_groups WHERE profileId = :profileId ORDER BY name")
    fun observeForProfile(profileId: Long): Flow<List<ReportGroup>>

    @Insert
    suspend fun insert(item: ReportGroup): Long

    @Update
    suspend fun update(item: ReportGroup)

    @Delete
    suspend fun delete(item: ReportGroup)

    @Query("SELECT COUNT(*) FROM medications WHERE reportGroupId = :groupId")
    suspend fun linkedMedicationCount(groupId: Long): Int

    @Query("UPDATE medications SET reportGroupId = NULL WHERE reportGroupId = :groupId")
    suspend fun unlinkMedications(groupId: Long)

    @Query("SELECT * FROM report_groups WHERE profileId=:profileId ORDER BY id")
    suspend fun allForProfile(profileId: Long): List<ReportGroup>
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments WHERE profileId=:profileId ORDER BY appointmentDateTime ASC")
    fun observeForProfile(profileId: Long): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE active=1 AND status='PLANNED' AND appointmentDateTime>:now ORDER BY appointmentDateTime ASC")
    suspend fun activeFuture(now: Long): List<Appointment>

    @Query("SELECT * FROM appointments WHERE id=:id LIMIT 1")
    suspend fun byId(id: Long): Appointment?

    @Insert
    suspend fun insert(item: Appointment): Long

    @Update
    suspend fun update(item: Appointment)

    @Delete
    suspend fun delete(item: Appointment)
}

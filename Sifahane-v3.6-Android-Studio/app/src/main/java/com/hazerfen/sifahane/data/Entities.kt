package com.hazerfen.sifahane.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relation: String = "",
    val surname: String = "",
    val photoUri: String? = null,
    val birthDate: String? = null,
    val bloodGroup: String = "Bilinmiyor",
    val profileNote: String = "",
    val role: String = "STANDARD",
    val adminPinHash: String? = null,
    val permissionsCsv: String = "OWN_DATA,MEDICATIONS,MEASUREMENTS,REPORTS,EXPORT_BACKUP",
    val accountEnabled: Boolean = true
)

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long = 1,
    val name: String,
    val purpose: String = "",
    val dose: String,
    val timesCsv: String,
    val stock: Int,
    val lowStockLimit: Int = 5,
    val photoUri: String? = null,
    val notes: String = "",
    val startDate: String,
    val endDate: String? = null,
    val continuous: Boolean = false,
    val active: Boolean = true,
    val archived: Boolean = false,
    val barcode: String? = null,
    val prospectusUrl: String? = null,
    val doctorName: String = "",
    val doctorBranch: String = "",
    val doctorInstitution: String = "",
    val doctorPhone: String = "",
    val isReported: Boolean = false,
    val reportStartDate: String? = null,
    val reportEndDate: String? = null,
    val reportWarningDays: Int = 7,
    val reportGroupId: Long? = null
)

@Entity(tableName = "dose_logs")
data class DoseLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val medicationId: Long,
    val medicationName: String,
    val scheduledDateTime: Long,
    val actualDateTime: Long? = null,
    val action: String,
    /**
     * true  = this ALINDI log decreased medication stock by one
     * false = stock was not decreased (for example stock was already zero)
     * null  = legacy log created before v3.3.50; stock effect is unknown
     */
    val stockDecreased: Boolean? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "blood_pressure")
data class BloodPressure(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val measuredAt: Long,
    val note: String = ""
)

@Entity(tableName = "blood_glucose")
data class BloodGlucose(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val valueMgDl: Int,
    val measurementType: String,
    val measuredAt: Long,
    val note: String = ""
)


@Entity(tableName = "report_groups")
data class ReportGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val name: String,
    val startDate: String,
    val endDate: String,
    val warningDays: Int = 7
)

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: Long,
    val doctorName: String = "",
    val branch: String = "",
    val institution: String = "",
    val clinic: String = "",
    val appointmentDateTime: Long,
    val phone: String = "",
    val address: String = "",
    val note: String = "",
    val status: String = "PLANNED",
    val remindersCsv: String = "10080,1440,180",
    val active: Boolean = true,
    val source: String = "MANUAL",
    val createdAt: Long = System.currentTimeMillis()
)


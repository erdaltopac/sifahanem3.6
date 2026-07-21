package com.hazerfen.sifahane.alarm

import android.content.Context
import com.hazerfen.sifahane.data.AppDatabase
import com.hazerfen.sifahane.data.DoseLog
import kotlin.math.abs

data class AlarmRefreshResult(
    val medicationCount: Int,
    val futureAlarmCount: Int,
    val catchUpAlarmCount: Int,
    val snoozeAlarmCount: Int
)

private data class PendingGroupDose(
    val dose: AlarmDose,
    val triggerAt: Long,
    val kind: MedicationAlarmKind
)

object AlarmRescheduler {
    suspend fun clearStaleAndRefresh(context: Context): Pair<Int, AlarmRefreshResult> {
        val db = AppDatabase.get(context)
        val removedGroups = AlarmScheduler.cancelAllRegisteredGroups(context)
        val medications = db.medicationDao().activeNow()
        medications.forEach { medication ->
            AlarmScheduler.cancelMedication(context, medication.id)
            AlarmScheduler.cancelSnooze(context, medication.id)
        }
        context.getSystemService(android.app.NotificationManager::class.java).cancelAll()
        context.getSharedPreferences("alarm_delivery_v3353", Context.MODE_PRIVATE).edit().clear().apply()
        return removedGroups to refreshAll(context)
    }

    suspend fun refreshAll(context: Context): AlarmRefreshResult {
        val db = AppDatabase.get(context)
        val medications = db.medicationDao().activeNow()
        val logs = db.doseLogDao().allLogs()
        val now = System.currentTimeMillis()
        val pending = mutableListOf<PendingGroupDose>()

        db.appointmentDao().activeFuture(now).forEach {
            AppointmentAlarmScheduler.schedule(context, it)
        }

        medications.forEach { medication ->
            AlarmScheduler.cancelMedication(context, medication.id)
            AlarmScheduler.medicationTimes(medication).forEach { scheduledTime ->
                val plannedToday = AlarmScheduler.plannedForToday(scheduledTime)
                if (AlarmScheduler.isMedicationValidOn(medication, plannedToday)) {
                    if (plannedToday > now) {
                        pending += PendingGroupDose(
                            AlarmDose(medication.id, medication.profileId, medication.name, scheduledTime, plannedToday),
                            plannedToday,
                            MedicationAlarmKind.NORMAL
                        )
                    } else {
                        val latest = logs.asSequence()
                            .filter { it.medicationId == medication.id && abs(it.scheduledDateTime - plannedToday) < 60_000L }
                            .maxByOrNull(DoseLog::timestamp)
                        val answered = latest?.action == "ALINDI" || latest?.action == "BUGÜN ALINMADI"
                        val snoozeAt = latest?.actualDateTime?.takeIf {
                            latest.action.endsWith("DK ERTELENDİ") && it > now
                        }
                        when {
                            snoozeAt != null -> pending += PendingGroupDose(
                                AlarmDose(medication.id, medication.profileId, medication.name, scheduledTime, plannedToday),
                                snoozeAt,
                                MedicationAlarmKind.SNOOZE
                            )
                            !answered -> pending += PendingGroupDose(
                                AlarmDose(medication.id, medication.profileId, medication.name, scheduledTime, plannedToday),
                                now + 2_000L,
                                MedicationAlarmKind.CATCH_UP
                            )
                        }
                        AlarmScheduler.nextValidOccurrence(medication, scheduledTime, plannedToday)?.let { next ->
                            pending += PendingGroupDose(
                                AlarmDose(medication.id, medication.profileId, medication.name, scheduledTime, next),
                                next,
                                MedicationAlarmKind.NORMAL
                            )
                        }
                    }
                } else {
                    AlarmScheduler.nextValidOccurrence(medication, scheduledTime, now)?.let { next ->
                        pending += PendingGroupDose(
                            AlarmDose(medication.id, medication.profileId, medication.name, scheduledTime, next),
                            next,
                            MedicationAlarmKind.NORMAL
                        )
                    }
                }
            }
        }

        val groups = pending.groupBy { Pair(it.kind, it.triggerAt / 60_000L) }
        groups.values.forEach { items ->
            AlarmScheduler.scheduleGroup(
                context,
                items.map(PendingGroupDose::dose),
                items.minOf(PendingGroupDose::triggerAt),
                items.first().kind
            )
        }

        return AlarmRefreshResult(
            medicationCount = medications.size,
            futureAlarmCount = groups.count { it.key.first == MedicationAlarmKind.NORMAL },
            catchUpAlarmCount = groups.count { it.key.first == MedicationAlarmKind.CATCH_UP },
            snoozeAlarmCount = groups.count { it.key.first == MedicationAlarmKind.SNOOZE }
        )
    }
}

package com.hazerfen.sifahane

import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.room.withTransaction
import com.hazerfen.sifahane.alarm.AlarmDose
import com.hazerfen.sifahane.alarm.AlarmScheduler
import com.hazerfen.sifahane.data.AppDatabase
import com.hazerfen.sifahane.data.DoseLog
import com.hazerfen.sifahane.data.Medication
import com.hazerfen.sifahane.ui.SifahaneTheme
import com.hazerfen.sifahane.ui.sifahaneSoftBoundary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class GroupDoseUi(
    val medication: Medication,
    val profileName: String,
    val scheduledTime: String,
    val plannedDateTime: Long
)

class AlarmActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var activeGroupKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_SECURE
        )
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            getSystemService(KeyguardManager::class.java).requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        showAlarm(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        showAlarm(intent)
    }

    private fun showAlarm(source: Intent) {
        val ids = source.getLongArrayExtra("medicationIds") ?: longArrayOf()
        val planned = source.getLongArrayExtra("plannedDateTimes") ?: longArrayOf()
        val times = source.getStringArrayListExtra("scheduledTimes").orEmpty()
        val groupKey = source.getStringExtra("groupKey") ?: "GROUP:${System.currentTimeMillis()}"
        if (getSharedPreferences("alarm_delivery_v3353", MODE_PRIVATE)
                .getBoolean("handled:$groupKey", false)
        ) {
            getSystemService(NotificationManager::class.java)
                .cancel(AlarmScheduler.notificationId(this, groupKey))
            finish()
            return
        }
        if (activeGroupKey != groupKey) {
            stopAlarmEffects()
            activeGroupKey = groupKey
            startAlarm()
        }
        val isTest = source.getBooleanExtra("isTest", false)
        setContent {
            SifahaneTheme {
                if (isTest) TestAlarm(groupKey) else GroupAlarmScreen(ids, planned, times, groupKey)
            }
        }
    }

    @Composable
    private fun TestAlarm(groupKey: String) {
        var showSnooze by remember { mutableStateOf(false) }
        LaunchedEffect(groupKey) {
            kotlinx.coroutines.delay(AppPreferences.alarmRingDurationMillis(this@AlarmActivity))
            AlarmScheduler.scheduleTest(this@AlarmActivity, System.currentTimeMillis() + 5 * 60_000L)
            finishAlarm(groupKey)
        }
        Surface(Modifier.fillMaxSize(), color = Color(0xFF101515)) {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.NightlightRound, null, tint = Color(0xFF72D4CD), modifier = Modifier.size(64.dp))
                Text(
                    "ŞİFAHANE DENEME ALARMI",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { showSnooze = true },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("ERTELE") }
                Button(onClick = { finishAlarm(groupKey) }, modifier = Modifier.fillMaxWidth()) { Text("TESTİ KAPAT") }
            }
        }
        if (showSnooze) {
            SnoozeDialog(
                dismiss = { showSnooze = false },
                select = { minutes ->
                    showSnooze = false
                    AlarmScheduler.scheduleTest(
                        this@AlarmActivity,
                        System.currentTimeMillis() + minutes * 60_000L
                    )
                    finishAlarm(groupKey)
                }
            )
        }
    }

    @Composable
    private fun GroupAlarmScreen(
        ids: LongArray,
        planned: LongArray,
        times: List<String>,
        groupKey: String
    ) {
        androidx.activity.compose.BackHandler { /* Alarm işlemsiz kapatılamaz. */ }
        val db = remember { AppDatabase.get(this) }
        val scope = rememberCoroutineScope()
        val doses = remember { mutableStateListOf<GroupDoseUi>() }
        var loading by remember { mutableStateOf(true) }
        var takeSelection by remember { mutableStateOf<List<GroupDoseUi>?>(null) }
        var snoozeSelection by remember { mutableStateOf<List<GroupDoseUi>?>(null) }
        var selectedProfileId by remember { mutableStateOf<Long?>(null) }

        LaunchedEffect(groupKey) {
            doses.clear()
            ids.indices.forEach { index ->
                db.medicationDao().byId(ids[index])?.takeIf { it.active && !it.archived }?.let { medication ->
                    val profile = db.profileDao().byId(medication.profileId)
                    doses += GroupDoseUi(
                        medication,
                        profile?.let { "${it.name} ${it.surname}".trim() } ?: "Kullanıcı",
                        times.getOrNull(index) ?: "",
                        planned.getOrNull(index) ?: System.currentTimeMillis()
                    )
                }
            }
            selectedProfileId = doses.firstOrNull()?.medication?.profileId
            loading = false
            if (doses.isEmpty()) finishAlarm(groupKey)
        }
        LaunchedEffect(groupKey, loading) {
            if (!loading) {
                kotlinx.coroutines.delay(AppPreferences.alarmRingDurationMillis(this@AlarmActivity))
                if (doses.isNotEmpty()) snooze(db, doses.toList(), 5, groupKey, doses)
            }
        }

        Surface(Modifier.fillMaxSize(), color = Color(0xFF101515)) {
            Card(
                Modifier.fillMaxSize().padding(16.dp).sifahaneSoftBoundary(2.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(
                    Color(0xFF050505).copy(alpha = 0.38f),
                    Color(0xFF72D4CD).copy(alpha = 0.72f),
                    Color(0xFF00AEEF).copy(alpha = 0.50f)
                ))),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FFFE))
            ) {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.NightlightRound, null, tint = Color(0xFF72D4CD), modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(6.dp))
                        Text("İLAÇ SAATİ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                        if (!loading) Text("Birlikte alınacak ${doses.size} ilaç")
                    }
                    Spacer(Modifier.height(12.dp))
                    if (loading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    } else {
                        val profiles = doses.distinctBy { it.medication.profileId }
                        val selectedDoses = doses.filter { it.medication.profileId == selectedProfileId }
                        LaunchedEffect(profiles.map { it.medication.profileId }) {
                            if (profiles.none { it.medication.profileId == selectedProfileId }) {
                                selectedProfileId = profiles.firstOrNull()?.medication?.profileId
                            }
                        }
                        if (profiles.size > 1) {
                            ScrollableTabRow(
                                selectedTabIndex = profiles.indexOfFirst {
                                    it.medication.profileId == selectedProfileId
                                }.coerceAtLeast(0),
                                edgePadding = 0.dp
                            ) {
                                profiles.forEach { profileDose ->
                                    Tab(
                                        selected = profileDose.medication.profileId == selectedProfileId,
                                        onClick = { selectedProfileId = profileDose.medication.profileId },
                                        text = { Text(profileDose.profileName, fontWeight = FontWeight.Bold) }
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(selectedDoses, key = { it.medication.id }) { dose ->
                                MedicationAlarmRow(
                                    dose = dose,
                                    onTaken = { takeSelection = listOf(dose) },
                                    onSnooze = { snoozeSelection = listOf(dose) },
                                    onSkip = { scope.launch { skip(db, listOf(dose), groupKey, doses) } }
                                )
                            }
                        }
                        if (selectedDoses.size > 1) {
                            HorizontalDivider(Modifier.padding(vertical = 8.dp))
                            Text("TOPLU İŞLEMLER", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Button(
                                onClick = { takeSelection = selectedDoses },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("TÜMÜNÜ ALDIM") }
                            OutlinedButton(
                                onClick = { snoozeSelection = selectedDoses },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("TÜMÜNÜ ERTELE") }
                            OutlinedButton(
                                onClick = { scope.launch { skip(db, selectedDoses, groupKey, doses) } },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("TÜMÜNÜ ALMAYACAĞIM") }
                        }
                    }
                }
            }
        }

        takeSelection?.let { selection ->
            TakenTimeDialog(
                scheduledTime = selection.firstOrNull()?.scheduledTime.orEmpty(),
                dismiss = { takeSelection = null },
                save = { actual ->
                    takeSelection = null
                    scope.launch { take(db, selection, actual, groupKey, doses) }
                }
            )
        }
        snoozeSelection?.let { selection ->
            SnoozeDialog(
                dismiss = { snoozeSelection = null },
                select = { minutes ->
                    snoozeSelection = null
                    scope.launch { snooze(db, selection, minutes, groupKey, doses) }
                }
            )
        }
    }

    @Composable
    private fun MedicationAlarmRow(
        dose: GroupDoseUi,
        onTaken: () -> Unit,
        onSnooze: () -> Unit,
        onSkip: () -> Unit
    ) {
        Card(
            modifier = Modifier.sifahaneSoftBoundary(2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(3.dp),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(
                Color(0xFF050505).copy(alpha = 0.30f),
                Color(0xFF72D4CD).copy(alpha = 0.60f),
                Color(0xFF00AEEF).copy(alpha = 0.50f)
            )))
        ) {
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text(dose.medication.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                if (dose.medication.dose.isNotBlank()) Text("Doz: ${dose.medication.dose}")
                if (dose.medication.purpose.isNotBlank()) Text(dose.medication.purpose)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(18.dp), tint = Color(0xFF72D4CD))
                    Spacer(Modifier.width(4.dp)); Text("Planlanan: ${dose.scheduledTime}")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onTaken, modifier = Modifier.fillMaxWidth()) { Text("ALDIM") }
                OutlinedButton(onClick = onSnooze, modifier = Modifier.fillMaxWidth()) { Text("ERTELE") }
                OutlinedButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) { Text("ALMAYACAĞIM") }
            }
        }
    }

    @Composable
    private fun SnoozeDialog(dismiss: () -> Unit, select: (Int) -> Unit) {
        val options = remember { AppPreferences.snoozeMinutes(this@AlarmActivity) }
        AlertDialog(
            onDismissRequest = dismiss,
            title = { Text("Erteleme süresini seçin") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { minutes ->
                        OutlinedButton(
                            onClick = { select(minutes) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("$minutes DAKİKA ERTELE") }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = dismiss) { Text("İPTAL") } }
        )
    }

    @Composable
    private fun TakenTimeDialog(scheduledTime: String, dismiss: () -> Unit, save: (Long) -> Unit) {
        var option by remember { mutableIntStateOf(1) }
        var custom by remember { mutableLongStateOf(System.currentTimeMillis()) }
        AlertDialog(
            onDismissRequest = dismiss,
            title = { Text("İlaç ne zaman alındı?") },
            text = {
                Column {
                    listOf("Planlanan saatte: $scheduledTime", "Şimdi", "Başka saat seç").forEachIndexed { index, label ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = option == index, onClick = {
                                option = index
                                if (index == 2) {
                                    val calendar = Calendar.getInstance()
                                    TimePickerDialog(this@AlarmActivity, { _, h, m ->
                                        calendar.set(Calendar.HOUR_OF_DAY, h); calendar.set(Calendar.MINUTE, m)
                                        custom = calendar.timeInMillis
                                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
                                }
                            })
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val planned = if (scheduledTime.matches(Regex("\\d{2}:\\d{2}"))) {
                        AlarmScheduler.plannedForToday(scheduledTime)
                    } else System.currentTimeMillis()
                    save(when (option) { 0 -> planned; 2 -> custom; else -> System.currentTimeMillis() })
                }) { Text("KAYDET") }
            },
            dismissButton = { TextButton(onClick = dismiss) { Text("İPTAL") } }
        )
    }

    private suspend fun take(
        db: AppDatabase,
        selection: List<GroupDoseUi>,
        actual: Long,
        groupKey: String,
        visible: MutableList<GroupDoseUi>
    ) {
        db.withTransaction {
            selection.forEach { dose ->
                val decreased = db.medicationDao().decreaseStock(dose.medication.id) > 0
                db.doseLogDao().insert(DoseLog(
                    profileId = dose.medication.profileId, medicationId = dose.medication.id,
                    medicationName = dose.medication.name, scheduledDateTime = dose.plannedDateTime,
                    actualDateTime = actual, action = "ALINDI", stockDecreased = decreased
                ))
            }
        }
        complete(selection, groupKey, visible)
    }

    private suspend fun snooze(
        db: AppDatabase,
        selection: List<GroupDoseUi>,
        minutes: Int,
        groupKey: String,
        visible: MutableList<GroupDoseUi>
    ) {
        val at = System.currentTimeMillis() + minutes * 60_000L
        getSharedPreferences("sifahane_alarm_diagnostics", MODE_PRIVATE).edit()
            .putLong("last_snoozed_at", System.currentTimeMillis())
            .putLong("last_snooze_trigger_at", at)
            .putInt("last_snooze_minutes", minutes)
            .apply()
        AlarmScheduler.snoozeGroup(this, selection.map {
            AlarmDose(it.medication.id, it.medication.profileId, it.medication.name, it.scheduledTime, it.plannedDateTime)
        }, at)
        db.withTransaction {
            selection.forEach { dose -> db.doseLogDao().insert(DoseLog(
                profileId = dose.medication.profileId, medicationId = dose.medication.id,
                medicationName = dose.medication.name, scheduledDateTime = dose.plannedDateTime,
                actualDateTime = at, action = "$minutes DK ERTELENDİ"
            )) }
        }
        complete(selection, groupKey, visible)
    }

    private suspend fun skip(
        db: AppDatabase,
        selection: List<GroupDoseUi>,
        groupKey: String,
        visible: MutableList<GroupDoseUi>
    ) {
        db.withTransaction {
            selection.forEach { dose -> db.doseLogDao().insert(DoseLog(
                profileId = dose.medication.profileId, medicationId = dose.medication.id,
                medicationName = dose.medication.name, scheduledDateTime = dose.plannedDateTime,
                action = "BUGÜN ALINMADI"
            )) }
        }
        complete(selection, groupKey, visible)
    }

    private fun complete(selection: List<GroupDoseUi>, groupKey: String, visible: MutableList<GroupDoseUi>) {
        val handled = selection.map { it.medication.id }.toSet()
        visible.removeAll { it.medication.id in handled }
        if (visible.isEmpty()) finishAlarm(groupKey)
    }

    private fun startAlarm() {
        if (mediaPlayer?.isPlaying == true) return
        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build())
                setDataSource(this@AlarmActivity, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                isLooping = true; prepare(); start()
            }
        }
        vibrator = if (Build.VERSION.SDK_INT >= 31) getSystemService(VibratorManager::class.java).defaultVibrator
        else @Suppress("DEPRECATION") (getSystemService(VIBRATOR_SERVICE) as Vibrator)
        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 700, 300, 700), 0))
    }

    private fun finishAlarm(groupKey: String) {
        stopAlarmEffects()
        activeGroupKey = null
        val notificationId = AlarmScheduler.notificationId(this, groupKey)
        getSystemService(NotificationManager::class.java).cancel(notificationId)
        AlarmScheduler.cancelGroup(this, groupKey)
        getSharedPreferences("alarm_delivery_v3353", MODE_PRIVATE).edit()
            .putBoolean("handled:$groupKey", true)
            .remove(groupKey)
            .apply()
        finish()
    }

    private fun stopAlarmEffects() {
        runCatching { mediaPlayer?.stop() }
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
    }

    override fun onDestroy() {
        stopAlarmEffects(); super.onDestroy()
    }
}

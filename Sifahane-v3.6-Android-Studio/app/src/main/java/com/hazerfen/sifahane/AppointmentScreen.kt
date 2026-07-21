package com.hazerfen.sifahane

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hazerfen.sifahane.alarm.AppointmentAlarmScheduler
import com.hazerfen.sifahane.alarm.AppointmentPreferences
import com.hazerfen.sifahane.data.AppDatabase
import com.hazerfen.sifahane.data.Appointment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.hazerfen.sifahane.ui.OutlinedLogoIcon
import com.hazerfen.sifahane.ui.SifahaneCard
import java.text.SimpleDateFormat
import java.util.*

private const val APPOINTMENT_PLANNED = "PLANNED"
private const val APPOINTMENT_ATTENDED = "ATTENDED"
private const val APPOINTMENT_CANCELLED = "CANCELLED"
private const val APPOINTMENT_POSTPONED = "POSTPONED"
private val AppointmentAccentColor = Color(0xFF72D4CD)
private val AppointmentLabelColor = Color(0xFF123A37)

@Composable
fun AppointmentsScreen(
    appointments: List<Appointment>,
    profileId: Long,
    db: AppDatabase,
    modifier: Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var showPast by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Appointment?>(null) }
    var creating by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Appointment?>(null) }
    val now = System.currentTimeMillis()

    val shown = appointments.filter {
        if (showPast) {
            it.appointmentDateTime < now || it.status != APPOINTMENT_PLANNED
        } else {
            it.appointmentDateTime >= now && it.status == APPOINTMENT_PLANNED
        }
    }.let { list ->
        if (showPast) list.sortedByDescending { it.appointmentDateTime }
        else list.sortedBy { it.appointmentDateTime }
    }

    Column(modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = if (showPast) 1 else 0,
                modifier = Modifier.weight(1f)
            ) {
                Tab(
                    selected = !showPast,
                    onClick = { showPast = false },
                ) { AppointmentTabContent(Icons.Default.EventAvailable, "Yaklaşan") }
                Tab(
                    selected = showPast,
                    onClick = { showPast = true },
                ) { AppointmentTabContent(Icons.Default.History, "Geçmiş") }
            }
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .height(72.dp)
                    .clickable { creating = true },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedLogoIcon(
                        Icons.Default.AddCircle,
                        contentDescription = "Yeni randevu ekle",
                        size = 30.dp
                    )
                }
                Text(
                    text = "Ekle",
                    color = AppointmentLabelColor,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (shown.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    if (showPast) "Geçmiş randevu kaydı bulunmuyor."
                    else "Yaklaşan randevu bulunmuyor. Sağ üstteki + simgesiyle randevu ekleyebilirsiniz.",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shown, key = { it.id }) { item ->
                    AppointmentCard(
                        item = item,
                        onEdit = { editing = item },
                        onStatus = { status ->
                            scope.launch {
                                val updated = item.copy(
                                    status = status,
                                    active = status == APPOINTMENT_PLANNED
                                )
                                withContext(Dispatchers.IO) {
                                    db.appointmentDao().update(updated)
                                    if (updated.active) AppointmentAlarmScheduler.schedule(context, updated)
                                    else AppointmentAlarmScheduler.cancel(context, updated.id)
                                }
                            }
                        },
                        onDelete = { deleteTarget = item }
                    )
                }
            }
        }
    }

    if (creating || editing != null) {
        AppointmentEditorDialog(
            initial = editing,
            defaultRemindersCsv = AppointmentPreferences.defaultRemindersCsv(context),
            onDismiss = {
                creating = false
                editing = null
            },
            onSave = { draft ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        if (editing == null) {
                            val id = db.appointmentDao().insert(draft.copy(profileId = profileId))
                            AppointmentAlarmScheduler.schedule(context, draft.copy(id = id, profileId = profileId))
                        } else {
                            db.appointmentDao().update(draft)
                            AppointmentAlarmScheduler.schedule(context, draft)
                        }
                    }
                    creating = false
                    editing = null
                }
            }
        )
    }

    deleteTarget?.let { item ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Randevuyu sil") },
            text = { Text("Bu randevu ve randevuya ait hatırlatmalar silinecek. Devam edilsin mi?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            AppointmentAlarmScheduler.cancel(context, item.id)
                            db.appointmentDao().delete(item)
                        }
                        deleteTarget = null
                    }
                }) { Text("Sil") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("İptal") }
            }
        )
    }
}

@Composable
private fun AppointmentTabContent(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(
        modifier = Modifier.fillMaxWidth().height(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(42.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedLogoIcon(icon, null, size = 30.dp)
        }
        Text(
            text = label,
            color = AppointmentLabelColor,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun AppointmentCard(
    item: Appointment,
    onEdit: () -> Unit,
    onStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    val dateText = remember(item.appointmentDateTime) {
        SimpleDateFormat("dd.MM.yyyy EEEE • HH:mm", Locale("tr", "TR"))
            .format(Date(item.appointmentDateTime))
    }
    SifahaneCard(modifier = Modifier.fillMaxWidth(), onClick = onEdit) {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                item.doctorName.ifBlank { "Doktor bilgisi girilmemiş" },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedLogoIcon(Icons.Default.Event, null, size = 20.dp)
                Spacer(Modifier.width(7.dp))
                Text(dateText, fontWeight = FontWeight.SemiBold)
            }
            listOf(item.branch, item.institution, item.clinic)
                .filter { it.isNotBlank() }
                .forEach { Text(it) }
            if (item.address.isNotBlank()) Text("Adres: ${item.address}")
            if (item.phone.isNotBlank()) Text("Telefon: ${item.phone}")
            if (item.note.isNotBlank()) Text("Not: ${item.note}")
            Text("Durum: ${appointmentStatusLabel(item.status)}")
            if (item.status == APPOINTMENT_PLANNED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { onStatus(APPOINTMENT_ATTENDED) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) { Text("Gidildi") }
                    OutlinedButton(
                        onClick = { onStatus(APPOINTMENT_CANCELLED) },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                    ) { Text("İptal") }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TextButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Düzenle")
                }
                TextButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Delete, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Sil")
                }
            }
        }
    }
}

@Composable
private fun AppointmentEditorDialog(
    initial: Appointment?,
    defaultRemindersCsv: String,
    onDismiss: () -> Unit,
    onSave: (Appointment) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.US) }
    val initialDate = initial?.appointmentDateTime ?: (System.currentTimeMillis() + 24 * 60 * 60_000L)

    var doctor by remember(initial) { mutableStateOf(initial?.doctorName.orEmpty()) }
    var branch by remember(initial) { mutableStateOf(initial?.branch.orEmpty()) }
    var institution by remember(initial) { mutableStateOf(initial?.institution.orEmpty()) }
    var clinic by remember(initial) { mutableStateOf(initial?.clinic.orEmpty()) }
    var date by remember(initial) { mutableStateOf(dateFormat.format(Date(initialDate))) }
    var time by remember(initial) { mutableStateOf(timeFormat.format(Date(initialDate))) }
    var phone by remember(initial) { mutableStateOf(initial?.phone.orEmpty()) }
    var address by remember(initial) { mutableStateOf(initial?.address.orEmpty()) }
    var note by remember(initial) { mutableStateOf(initial?.note.orEmpty()) }
    var selectedReminders by remember(initial, defaultRemindersCsv) {
        mutableStateOf(
            (initial?.remindersCsv ?: defaultRemindersCsv)
                .split(",").mapNotNull { it.toIntOrNull() }.toSet()
        )
    }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Yeni doktor randevusu" else "Randevuyu düzenle") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { AppointmentTextField(doctor, { doctor = it }, "Doktor adı") }
                item { AppointmentTextField(branch, { branch = it }, "Branş") }
                item { AppointmentTextField(institution, { institution = it }, "Hastane / sağlık tesisi") }
                item { AppointmentTextField(clinic, { clinic = it }, "Poliklinik / oda") }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("Tarih") },
                            supportingText = { Text("YYYY-AA-GG") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("Saat") },
                            supportingText = { Text("SS:DD") },
                            singleLine = true,
                            modifier = Modifier.weight(0.75f)
                        )
                    }
                }
                item { AppointmentTextField(phone, { phone = it }, "Telefon", KeyboardType.Phone) }
                item { AppointmentTextField(address, { address = it }, "Adres") }
                item {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Hazırlık ve randevu notu") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                item {
                    Text("Hatırlatmalar", fontWeight = FontWeight.Bold)
                    ReminderOptions(
                        selected = selectedReminders,
                        onToggle = { value ->
                            selectedReminders = if (value in selectedReminders)
                                selectedReminders - value else selectedReminders + value
                        }
                    )
                }
                error?.let { message ->
                    item { Text(message, color = MaterialTheme.colorScheme.error) }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val parsed = runCatching {
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
                        isLenient = false
                    }.parse("$date $time")?.time
                }.getOrNull()
                error = when {
                    parsed == null -> "Tarih veya saat geçerli değil."
                    parsed <= System.currentTimeMillis() && initial == null ->
                        "Yeni randevu gelecekte bir tarih ve saate ayarlanmalıdır."
                    selectedReminders.isEmpty() -> "En az bir hatırlatma seçin."
                    else -> null
                }
                if (error == null) {
                    onSave(
                        (initial ?: Appointment(profileId = 0, appointmentDateTime = parsed!!)).copy(
                            doctorName = doctor.trim(),
                            branch = branch.trim(),
                            institution = institution.trim(),
                            clinic = clinic.trim(),
                            appointmentDateTime = parsed!!,
                            phone = phone.trim(),
                            address = address.trim(),
                            note = note.trim(),
                            status = if (initial?.status == null) APPOINTMENT_PLANNED else initial.status,
                            remindersCsv = selectedReminders.sortedDescending().joinToString(","),
                            active = initial?.status?.let { it == APPOINTMENT_PLANNED } ?: true
                        )
                    )
                }
            }) { Text("Kaydet") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("İptal") } }
    )
}

@Composable
private fun AppointmentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun ReminderOptions(selected: Set<Int>, onToggle: (Int) -> Unit) {
    listOf(
        10080 to "1 hafta önce",
        4320 to "3 gün önce",
        1440 to "1 gün önce",
        180 to "3 saat önce",
        60 to "1 saat önce"
    ).forEach { (minutes, label) ->
        Row(
            modifier = Modifier.fillMaxWidth().clickable { onToggle(minutes) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = minutes in selected,
                onCheckedChange = { onToggle(minutes) }
            )
            Text(label)
        }
    }
}

fun appointmentStatusLabel(status: String): String = when (status) {
    APPOINTMENT_ATTENDED -> "Gidildi"
    APPOINTMENT_CANCELLED -> "İptal edildi"
    APPOINTMENT_POSTPONED -> "Ertelendi"
    else -> "Planlandı"
}

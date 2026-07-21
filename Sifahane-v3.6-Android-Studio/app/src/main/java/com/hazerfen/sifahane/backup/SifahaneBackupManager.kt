package com.hazerfen.sifahane.backup

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.room.withTransaction
import com.hazerfen.sifahane.BuildConfig
import com.hazerfen.sifahane.alarm.AlarmRescheduler
import com.hazerfen.sifahane.data.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

private const val FORMAT = "SifahaneBackup"
private const val FORMAT_VERSION = 3
private const val BACKUP_JSON = "backup.json"
private const val PREFS = "sifahane_backup_preferences"
private const val PREF_TREE_URI = "backup_tree_uri"

data class BackupDocument(val uri: Uri, val name: String, val size: Long, val modifiedAt: Long)
data class BackupPreview(
    val uri: Uri, val profileName: String, val birthDate: String?, val bloodGroup: String,
    val createdAt: String, val appVersion: String, val medicationCount: Int, val doseLogCount: Int,
    val bloodPressureCount: Int, val glucoseCount: Int, val reportGroupCount: Int, val photoCount: Int
)

object SifahaneBackupManager {
    fun savedTreeUri(context: Context): Uri? = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(PREF_TREE_URI, null)?.let(Uri::parse)

    fun saveTreeUri(context: Context, uri: Uri) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(PREF_TREE_URI, uri.toString()).apply()
    }

    suspend fun createBackup(context: Context, db: AppDatabase, profileId: Long, password: CharArray): File {
        val p = db.profileDao().byId(profileId) ?: error("Kişi bulunamadı.")
        val meds = db.medicationDao().allForProfile(profileId)
        val logs = db.doseLogDao().allForProfile(profileId)
        val bp = db.vitalsDao().allBpForExport(profileId)
        val gl = db.vitalsDao().allGlucoseForExport(profileId)
        val groups = db.reportGroupDao().allForProfile(profileId)
        val media = linkedMapOf<String, ByteArray>()

        fun addMedia(uriText: String?, entry: String): String? {
            if (uriText.isNullOrBlank()) return null
            return runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriText))?.use {
                    val bytes = it.readLimited(BackupArchivePolicy.MAX_MEDIA_BYTES); media[entry] = bytes; entry
                }
            }.getOrNull()
        }

        val profilePhoto = addMedia(p.photoUri, "profile_photos/profile_${p.id}.jpg")
        val medPhotos = mutableMapOf<Long, String>()
        meds.forEach { m -> addMedia(m.photoUri, "medicine_photos/medicine_${m.id}.jpg")?.let { medPhotos[m.id] = it } }

        val payload = JSONObject().apply {
            put("profile", JSONObject().apply {
                put("originalProfileId", p.id); put("name", p.name); put("surname", p.surname); put("relation", p.relation)
                putN("birthDate", p.birthDate); put("bloodGroup", p.bloodGroup); put("profileNote", p.profileNote); putN("photoEntry", profilePhoto)
            })
            put("medications", JSONArray().apply { meds.forEach { m -> put(JSONObject().apply {
                put("oldId", m.id); put("name", m.name); put("purpose", m.purpose); put("dose", m.dose); put("timesCsv", m.timesCsv)
                put("stock", m.stock); put("lowStockLimit", m.lowStockLimit); putN("photoEntry", medPhotos[m.id]); put("notes", m.notes)
                put("startDate", m.startDate); putN("endDate", m.endDate); put("continuous", m.continuous); put("active", m.active)
                put("archived", m.archived); putN("barcode", m.barcode); putN("prospectusUrl", m.prospectusUrl)
                put("doctorName", m.doctorName); put("doctorBranch", m.doctorBranch); put("doctorInstitution", m.doctorInstitution)
                put("doctorPhone", m.doctorPhone); put("isReported", m.isReported); putN("reportStartDate", m.reportStartDate)
                putN("reportEndDate", m.reportEndDate); put("reportWarningDays", m.reportWarningDays); putN("reportGroupId", m.reportGroupId)
            }) } })
            put("doseLogs", JSONArray().apply { logs.forEach { x -> put(JSONObject().apply {
                put("oldMedicationId", x.medicationId); put("medicationName", x.medicationName); put("scheduledDateTime", x.scheduledDateTime)
                putN("actualDateTime", x.actualDateTime); put("action", x.action); putN("stockDecreased", x.stockDecreased); put("timestamp", x.timestamp)
            }) } })
            put("bloodPressure", JSONArray().apply { bp.forEach { x -> put(JSONObject().apply {
                put("systolic", x.systolic); put("diastolic", x.diastolic); putN("pulse", x.pulse); put("measuredAt", x.measuredAt); put("note", x.note)
            }) } })
            put("bloodGlucose", JSONArray().apply { gl.forEach { x -> put(JSONObject().apply {
                put("valueMgDl", x.valueMgDl); put("measurementType", x.measurementType); put("measuredAt", x.measuredAt); put("note", x.note)
            }) } })
            put("reportGroups", JSONArray().apply { groups.forEach { x -> put(JSONObject().apply {
                put("oldId", x.id); put("name", x.name); put("startDate", x.startDate); put("endDate", x.endDate); put("warningDays", x.warningDays)
            }) } })
        }

        val identity = listOf(p.name.trim().lowercase(), p.surname.trim().lowercase(), p.birthDate.orEmpty()).joinToString("|")
        val root = JSONObject().apply {
            put("format", FORMAT); put("formatVersion", FORMAT_VERSION); put("appVersion", BuildConfig.VERSION_NAME)
            put("createdAt", isoNow()); put("backupId", UUID.randomUUID().toString())
            put("profileIdentity", JSONObject().apply {
                put("originalProfileId", p.id); put("name", p.name); put("surname", p.surname); putN("birthDate", p.birthDate)
                put("bloodGroup", p.bloodGroup); put("relation", p.relation); put("profileFingerprint", sha256(identity.toByteArray()))
            })
            put("recordSummary", JSONObject().apply {
                put("medicationCount", meds.size); put("doseLogCount", logs.size); put("bloodPressureCount", bp.size)
                put("glucoseCount", gl.size); put("reportGroupCount", groups.size); put("photoCount", media.size)
            })
            put("payloadSha256", sha256(payload.toString().toByteArray(Charsets.UTF_8)))
            put("mediaChecksums", JSONObject().apply { media.forEach { (n, b) -> put(n, sha256(b)) } })
            put("payload", payload)
        }

        val safe = (p.name + "_" + p.surname).trim('_').replace(Regex("[^A-Za-z0-9ÇĞİÖŞÜçğıöşü_-]"), "_")
        val stamp = SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US).format(Date())
        val cache = File(context.cacheDir, "sifahane_backups").apply { mkdirs() }
        val plain = File(cache, ".Sifahane_${safe}_${stamp}_${UUID.randomUUID()}.zip.tmp")
        val out = File(cache, "Sifahane_${safe}_${stamp}.sifbackup")
        try {
            ZipOutputStream(BufferedOutputStream(plain.outputStream())).use { zip ->
                zip.putNextEntry(ZipEntry(BACKUP_JSON)); zip.write(root.toString(2).toByteArray()); zip.closeEntry()
                media.forEach { (n, b) -> zip.putNextEntry(ZipEntry(n)); zip.write(b); zip.closeEntry() }
            }
            BackupCrypto.encryptFile(plain, out, password)
        } finally {
            plain.delete()
            password.fill('\u0000')
        }
        return out
    }

    fun copyBackupToTree(context: Context, treeUri: Uri, source: File): Uri {
        val selected = DocumentFile.fromTreeUri(context, treeUri) ?: error("Yedek klasörüne erişilemedi.")
        val folder = if (selected.name == "Şifahane Yedek") selected
        else selected.findFile("Şifahane Yedek") ?: selected.createDirectory("Şifahane Yedek")
        ?: error("Şifahane Yedek klasörü oluşturulamadı.")
        folder.findFile(source.name)?.delete()
        val target = folder.createFile("application/octet-stream", source.name) ?: error("Yedek oluşturulamadı.")
        context.contentResolver.openOutputStream(target.uri, "w")!!.use { out -> source.inputStream().use { it.copyTo(out) } }
        return target.uri
    }

    fun copyBackupToUri(context: Context, source: File, target: Uri) {
        context.contentResolver.openOutputStream(target, "w")?.use { out -> source.inputStream().use { it.copyTo(out) } }
            ?: error("Seçilen hedefe yazılamadı.")
    }

    fun listBackups(context: Context, treeUri: Uri): List<BackupDocument> {
        val selected = DocumentFile.fromTreeUri(context, treeUri) ?: return emptyList()
        val folder = if (selected.name == "Şifahane Yedek") selected else selected.findFile("Şifahane Yedek") ?: selected
        return folder.listFiles().filter { it.isFile && (it.name?.endsWith(".sifbackup", true) == true || it.name?.endsWith(".zip", true) == true) }
            .map { BackupDocument(it.uri, it.name ?: "Şifahane yedeği.zip", it.length(), it.lastModified()) }
            .sortedByDescending { it.modifiedAt }
    }

    fun isEncrypted(context: Context, uri: Uri): Boolean = BackupCrypto.isEncrypted(context, uri)

    fun preview(context: Context, uri: Uri, password: CharArray? = null): BackupPreview {
        val root = extract(context, uri, false, password).first
        val i = root.getJSONObject("profileIdentity"); val s = root.getJSONObject("recordSummary")
        return BackupPreview(uri, listOf(i.optString("name"), i.optString("surname")).filter(String::isNotBlank).joinToString(" "),
            i.optNS("birthDate"), i.optString("bloodGroup", "Bilinmiyor"), root.optString("createdAt"), root.optString("appVersion"),
            s.optInt("medicationCount"), s.optInt("doseLogCount"), s.optInt("bloodPressureCount"), s.optInt("glucoseCount"),
            s.optInt("reportGroupCount"), s.optInt("photoCount"))
    }

    suspend fun importBackup(context: Context, db: AppDatabase, uri: Uri, mergeIntoProfileId: Long?, password: CharArray? = null): Long {
        val (root, media) = extract(context, uri, true, password)
        val payload = root.getJSONObject("payload"); val p = payload.getJSONObject("profile")
        val mediaDir = File(context.filesDir, "imported_backup_media/${UUID.randomUUID()}").apply { mkdirs() }
        fun restore(entry: String?): String? {
            val b = entry?.let(media::get) ?: return null
            val f = File(mediaDir, entry.substringAfterLast('/')); f.writeBytes(b); return Uri.fromFile(f).toString()
        }
        var profileId = 0L
        db.withTransaction {
            profileId = mergeIntoProfileId ?: db.profileDao().insert(UserProfile(
                name = p.optString("name"), surname = p.optString("surname"), relation = p.optString("relation"),
                birthDate = p.optNS("birthDate"), bloodGroup = p.optString("bloodGroup", "Bilinmiyor"), profileNote = p.optString("profileNote"),
                photoUri = restore(p.optNS("photoEntry"))))
            if (mergeIntoProfileId != null) db.profileDao().byId(profileId)?.let { old ->
                if (old.photoUri.isNullOrBlank()) db.profileDao().update(old.copy(photoUri = restore(p.optNS("photoEntry"))))
            }
            val groupMap = mutableMapOf<Long, Long>()
            val groups = payload.optJSONArray("reportGroups") ?: JSONArray()
            for (n in 0 until groups.length()) groups.getJSONObject(n).let { g -> groupMap[g.optLong("oldId")] = db.reportGroupDao().insert(
                ReportGroup(profileId = profileId, name = g.optString("name"), startDate = g.optString("startDate"), endDate = g.optString("endDate"), warningDays = g.optInt("warningDays", 7))) }
            val medMap = mutableMapOf<Long, Long>(); val meds = payload.optJSONArray("medications") ?: JSONArray()
            for (n in 0 until meds.length()) meds.getJSONObject(n).let { m ->
                val oldGroup = m.optNL("reportGroupId")
                medMap[m.optLong("oldId")] = db.medicationDao().insert(Medication(
                    profileId = profileId, name = m.optString("name"), purpose = m.optString("purpose"), dose = m.optString("dose"),
                    timesCsv = m.optString("timesCsv"), stock = m.optInt("stock"), lowStockLimit = m.optInt("lowStockLimit", 5),
                    photoUri = restore(m.optNS("photoEntry")), notes = m.optString("notes"), startDate = m.optString("startDate"),
                    endDate = m.optNS("endDate"), continuous = m.optBoolean("continuous"), active = m.optBoolean("active", true),
                    archived = m.optBoolean("archived"), barcode = m.optNS("barcode"), prospectusUrl = m.optNS("prospectusUrl"),
                    doctorName = m.optString("doctorName"), doctorBranch = m.optString("doctorBranch"), doctorInstitution = m.optString("doctorInstitution"),
                    doctorPhone = m.optString("doctorPhone"), isReported = m.optBoolean("isReported"), reportStartDate = m.optNS("reportStartDate"),
                    reportEndDate = m.optNS("reportEndDate"), reportWarningDays = m.optInt("reportWarningDays", 7), reportGroupId = oldGroup?.let(groupMap::get)))
            }
            val logs = payload.optJSONArray("doseLogs") ?: JSONArray()
            for (n in 0 until logs.length()) logs.getJSONObject(n).let { x -> db.doseLogDao().insert(DoseLog(
                profileId = profileId, medicationId = medMap[x.optLong("oldMedicationId")] ?: 0L, medicationName = x.optString("medicationName"),
                scheduledDateTime = x.optLong("scheduledDateTime"), actualDateTime = x.optNL("actualDateTime"), action = x.optString("action"),
                stockDecreased = x.optNB("stockDecreased"), timestamp = x.optLong("timestamp"))) }
            val bp = payload.optJSONArray("bloodPressure") ?: JSONArray()
            for (n in 0 until bp.length()) bp.getJSONObject(n).let { x -> db.vitalsDao().insertBp(BloodPressure(
                profileId = profileId, systolic = x.optInt("systolic"), diastolic = x.optInt("diastolic"), pulse = x.optNI("pulse"),
                measuredAt = x.optLong("measuredAt"), note = x.optString("note"))) }
            val gl = payload.optJSONArray("bloodGlucose") ?: JSONArray()
            for (n in 0 until gl.length()) gl.getJSONObject(n).let { x -> db.vitalsDao().insertGlucose(BloodGlucose(
                profileId = profileId, valueMgDl = x.optInt("valueMgDl"), measurementType = x.optString("measurementType"),
                measuredAt = x.optLong("measuredAt"), note = x.optString("note"))) }
        }
        runCatching { AlarmRescheduler.refreshAll(context) }
        return profileId
    }

    private fun extract(context: Context, uri: Uri, mediaToo: Boolean, password: CharArray?): Pair<JSONObject, Map<String, ByteArray>> {
        val entries = linkedMapOf<String, ByteArray>()
        val seenNames = hashSetOf<String>()
        var entryCount = 0
        var totalUncompressed = 0L
        try {
            BackupCrypto.openInput(context, uri, password).use { input ->
                ZipInputStream(BufferedInputStream(input)).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        val name = BackupArchivePolicy.normalizeEntryName(entry.name)
                        if (BackupArchivePolicy.isUnsafeEntryName(name)) {
                            throw BackupValidationException.CorruptBackup()
                        }
                        if (!entry.isDirectory) {
                            entryCount++
                            if (entryCount > BackupArchivePolicy.MAX_ENTRIES || !seenNames.add(name)) {
                                throw BackupValidationException.ResourceLimit()
                            }
                            val limit = BackupArchivePolicy.entryLimit(name, BACKUP_JSON)
                            if (entry.size > limit) throw BackupValidationException.ResourceLimit()
                            val bytes = zip.readLimited(limit)
                            totalUncompressed += bytes.size
                            if (totalUncompressed > BackupArchivePolicy.MAX_TOTAL_UNCOMPRESSED_BYTES) {
                                throw BackupValidationException.ResourceLimit()
                            }
                            if (BackupArchivePolicy.hasSuspiciousCompression(bytes.size.toLong(), entry.compressedSize)) {
                                throw BackupValidationException.ResourceLimit()
                            }
                            if (name == BACKUP_JSON || mediaToo) entries[name] = bytes
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            }
        } catch (e: BackupValidationException) {
            throw e
        } catch (failure: Exception) {
            val names = generateSequence<Throwable>(failure) { it.cause }.map { it::class.java.name }.toList()
            if (names.any { it.contains("AEADBadTag") || it.contains("BadPadding") }) {
                throw BackupValidationException.WrongPassword()
            }
            throw BackupValidationException.NotSifahaneBackup()
        }
        val root = try {
            JSONObject(String(entries[BACKUP_JSON] ?: throw BackupValidationException.NotSifahaneBackup(), Charsets.UTF_8))
        } catch (e: BackupValidationException) {
            throw e
        } catch (_: Exception) {
            throw BackupValidationException.NotSifahaneBackup()
        }
        if (root.optString("format") != FORMAT) throw BackupValidationException.NotSifahaneBackup()
        val version = root.optInt("formatVersion", -1)
        if (version > FORMAT_VERSION) throw BackupValidationException.UnsupportedVersion()
        if (version <= 0) throw BackupValidationException.CorruptBackup()
        val payload = root.optJSONObject("payload") ?: throw BackupValidationException.CorruptBackup()
        if (sha256(payload.toString().toByteArray()) != root.optString("payloadSha256")) {
            throw BackupValidationException.CorruptBackup()
        }
        if (mediaToo) {
            val checksums = root.optJSONObject("mediaChecksums") ?: JSONObject()
            val keys = checksums.keys()
            while (keys.hasNext()) {
                val name = keys.next()
                val bytes = entries[name] ?: throw BackupValidationException.CorruptBackup()
                if (sha256(bytes) != checksums.optString(name)) {
                    throw BackupValidationException.CorruptBackup()
                }
            }
        }
        return root to (entries - BACKUP_JSON)
    }
}

private fun JSONObject.putN(k: String, v: Any?) { if (v == null) put(k, JSONObject.NULL) else put(k, v) }
private fun JSONObject.optNS(k: String): String? = if (!has(k) || isNull(k)) null else optString(k).takeIf { it.isNotBlank() }
private fun JSONObject.optNB(k: String): Boolean? = if (!has(k) || isNull(k)) null else optBoolean(k)
private fun JSONObject.optNL(k: String): Long? = if (!has(k) || isNull(k)) null else optLong(k)
private fun JSONObject.optNI(k: String): Int? = if (!has(k) || isNull(k)) null else optInt(k)
private fun sha256(b: ByteArray) = MessageDigest.getInstance("SHA-256").digest(b).joinToString("") { "%02x".format(it) }
private fun isoNow() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(Date())
private fun InputStream.readLimited(limit: Long): ByteArray { val out = ByteArrayOutputStream(); val buf = ByteArray(8192); var total = 0L; while (true) { val n = read(buf); if (n < 0) break; total += n; if (total > limit) throw IOException("Dosya çok büyük"); out.write(buf, 0, n) }; return out.toByteArray() }
